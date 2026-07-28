package jpaoletti.jpm2.core.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Self-contained {@link SysparamCipher} using only the JDK JCE (no external deps):
 * AES/GCM/NoPadding (128-bit tag), a random 12-byte IV and a random 16-byte salt per
 * encryption, key derived with PBKDF2WithHmacSHA1 (65536 iterations, 128-bit key) from
 * an injected passphrase. Wire format is {@code "V1:" + Base64(salt || iv || ciphertext)}.
 * A value without the {@code V1:} prefix is treated as plaintext and returned unchanged
 * on decrypt, so plain and encrypted values coexist during migration.
 *
 * <p>The passphrase is injected (e.g. from {@code extra.properties}
 * {@code sysparam.secret.key}). When blank the cipher is disabled: encrypting a SECRET
 * fails loudly instead of storing plaintext.
 *
 * @author jpaoletti
 */
public class AesSysparamCipher implements SysparamCipher {

    private static final String VERSION_PREFIX = "V1:";
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;
    private static final int ITERATIONS = 65536;
    private static final int KEY_BITS = 128;
    private static final int GCM_TAG_BITS = 128;

    private final char[] passphrase;
    private final SecureRandom random = new SecureRandom();

    public AesSysparamCipher(String secretKey) {
        this.passphrase = StringUtils.isBlank(secretKey) ? null : secretKey.toCharArray();
    }

    @Override
    public boolean isEnabled() {
        return passphrase != null;
    }

    @Override
    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(VERSION_PREFIX);
    }

    @Override
    public String encrypt(String plain) throws PMException {
        if (plain == null) {
            return null;
        }
        if (!isEnabled()) {
            throw new PMException(MessageFactory.error("jpm.sysparam.cipher.disabled"));
        }
        try {
            final byte[] salt = new byte[SALT_LEN];
            random.nextBytes(salt);
            final byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, deriveKey(salt), new GCMParameterSpec(GCM_TAG_BITS, iv));
            final byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            final ByteBuffer bb = ByteBuffer.allocate(salt.length + iv.length + ct.length);
            bb.put(salt).put(iv).put(ct);
            return VERSION_PREFIX + Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new PMException(MessageFactory.error("jpm.sysparam.cipher.encryptError"), e);
        }
    }

    @Override
    public String decrypt(String value) throws PMException {
        if (value == null) {
            return null;
        }
        if (!isEncrypted(value)) {
            return value; // plaintext passthrough
        }
        if (!isEnabled()) {
            throw new PMException(MessageFactory.error("jpm.sysparam.cipher.disabled"));
        }
        try {
            final byte[] data = Base64.getDecoder().decode(value.substring(VERSION_PREFIX.length()));
            final ByteBuffer bb = ByteBuffer.wrap(data);
            final byte[] salt = new byte[SALT_LEN];
            bb.get(salt);
            final byte[] iv = new byte[IV_LEN];
            bb.get(iv);
            final byte[] ct = new byte[bb.remaining()];
            bb.get(ct);
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, deriveKey(salt), new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new PMException(MessageFactory.error("jpm.sysparam.cipher.decryptError"), e);
        }
    }

    private SecretKey deriveKey(byte[] salt) throws Exception {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final KeySpec spec = new PBEKeySpec(passphrase, salt, ITERATIONS, KEY_BITS);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
