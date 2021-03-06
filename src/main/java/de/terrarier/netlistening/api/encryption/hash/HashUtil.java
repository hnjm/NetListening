package de.terrarier.netlistening.api.encryption.hash;

import de.terrarier.netlistening.utils.ByteBufUtilExtension;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class HashUtil {

    private HashUtil() {}

    public static byte[] hash(@NotNull HashingAlgorithm hashingAlgorithm, byte[] data) throws NoSuchAlgorithmException {
        return hash(hashingAlgorithm, data, 0);
    }

    private static byte[] hash(@NotNull HashingAlgorithm hashingAlgorithm, byte[] data, int salt) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(hashingAlgorithm.getRawName());
        if(salt > 0) {
            final ByteBuf dataBuffer = Unpooled.buffer(data.length + salt);
            dataBuffer.writeBytes(data);
            dataBuffer.writeBytes(getSalt(salt));
            data = ByteBufUtilExtension.getBytes(dataBuffer);
            dataBuffer.release();
        }
        return digest.digest(data);
    }

    private static byte[] getSalt(int length) {
        final byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static boolean isHashMatching(byte[] hash, byte[] compared) {
        final int hashLength = hash.length;
        if(hashLength != compared.length) {
            return false;
        }
        for(int i = 0; i < hashLength; i++) {
            if(hash[i] != compared[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] calculateHMAC(byte[] data, byte[] key, @NotNull HashingAlgorithm algorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        final String macName = "Hmac" + algorithm.name().replaceFirst("_", "");
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, macName);
        final Mac mac = Mac.getInstance(macName);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

}
