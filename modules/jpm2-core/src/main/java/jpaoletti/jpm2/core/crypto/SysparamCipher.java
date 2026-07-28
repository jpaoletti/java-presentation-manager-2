package jpaoletti.jpm2.core.crypto;

import jpaoletti.jpm2.core.PMException;

/**
 * Cipher used to protect SECRET sysparam values at rest. Implementations must be
 * transparent for plaintext: {@link #decrypt(String)} returns non-encrypted input
 * unchanged, so encrypted and plain values can coexist during migration.
 *
 * <p>The default implementation ({@link AesSysparamCipher}) is self-contained (JDK
 * JCE only). Applications may plug their own (HSM/KMS) by declaring a different
 * {@code SysparamCipher} bean.
 *
 * @author jpaoletti
 */
public interface SysparamCipher {

    /**
     * @return true if a secret key is configured and encryption/decryption of new
     * SECRET values is possible.
     */
    boolean isEnabled();

    /**
     * Encrypts a plaintext value. Throws if the cipher is not enabled.
     */
    String encrypt(String plain) throws PMException;

    /**
     * Decrypts a value. If the value is not in this cipher's wire format it is
     * returned unchanged (plaintext passthrough).
     */
    String decrypt(String value) throws PMException;

    /**
     * @return true if the value is in this cipher's encrypted wire format.
     */
    boolean isEncrypted(String value);
}
