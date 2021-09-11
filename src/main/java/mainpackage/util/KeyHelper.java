package mainpackage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;

public class KeyHelper {
    private static final Logger logger = LogManager.getLogger(KeyHelper.class);
    private static KeyPairGenerator keyGen;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            logger.error("Unable to initialize KeyGenerator", e);
        }
    }

    public static KeyPair generateKeyPair() {
        logger.debug("Generating a new key pair");
        return keyGen.generateKeyPair();
    }

    public static boolean deletePrivateKey(ECPrivateKey privateKey) {
        logger.debug("Wiping a private key");
        try {
            final BigInteger bigInteger = privateKey.getS();
            final Field magField = BigInteger.class.getDeclaredField("mag");
            magField.setAccessible(true);
            final int[] mag = (int[]) magField.get(bigInteger);
            //noinspection ExplicitArrayFilling
            for (int i = 0; i < mag.length; i++) {
                mag[i] = 0;
            }
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.warn("Error while wiping private key", e);
            return false;
        }
    }
}
