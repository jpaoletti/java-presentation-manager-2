package jpaoletti.jpm2.core.crypto;

import jpaoletti.jpm2.core.PMException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AesSysparamCipher}: round-trip, plaintext passthrough, disabled state
 * and wrong-key failure.
 *
 * @author jpaoletti
 */
public class AesSysparamCipherTest {

    private final AesSysparamCipher cipher = new AesSysparamCipher("a-strong-passphrase");

    @Test
    public void roundTripReturnsOriginal() throws Exception {
        final String plain = "s3cr3t-value-áéí-ñ";
        final String enc = cipher.encrypt(plain);
        assertNotEquals(plain, enc, "ciphertext must differ from plaintext");
        assertTrue(cipher.isEncrypted(enc), "ciphertext must carry the version prefix");
        assertEquals(plain, cipher.decrypt(enc), "decrypt must recover the original");
    }

    @Test
    public void everyEncryptionIsDistinctButDecryptsEqual() throws Exception {
        final String plain = "same-input";
        final String a = cipher.encrypt(plain);
        final String b = cipher.encrypt(plain);
        assertNotEquals(a, b, "random salt/iv must make each ciphertext unique");
        assertEquals(plain, cipher.decrypt(a));
        assertEquals(plain, cipher.decrypt(b));
    }

    @Test
    public void plaintextIsPassedThroughOnDecrypt() throws Exception {
        assertEquals("not-encrypted", cipher.decrypt("not-encrypted"));
        assertFalse(cipher.isEncrypted("not-encrypted"));
    }

    @Test
    public void nullsAreHandled() throws Exception {
        assertNull(cipher.encrypt(null));
        assertNull(cipher.decrypt(null));
    }

    @Test
    public void disabledCipherRejectsEncryptButPassesPlaintext() throws Exception {
        final AesSysparamCipher disabled = new AesSysparamCipher("  ");
        assertFalse(disabled.isEnabled());
        assertThrows(PMException.class, () -> disabled.encrypt("x"));
        assertEquals("plain", disabled.decrypt("plain"), "plaintext must still pass through when disabled");
    }

    @Test
    public void wrongKeyFailsToDecrypt() throws Exception {
        final String enc = cipher.encrypt("value");
        final AesSysparamCipher other = new AesSysparamCipher("a-different-passphrase");
        assertThrows(PMException.class, () -> other.decrypt(enc));
    }
}
