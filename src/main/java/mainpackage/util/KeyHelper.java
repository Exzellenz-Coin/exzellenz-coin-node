package mainpackage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyHelper {
    private static final Logger logger = LogManager.getLogger(KeyHelper.class);
    private static KeyPairGenerator keyGenerator;
    public static KeyFactory keyFactory;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            keyGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGenerator.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            logger.error("Unable to initialize KeyGenerator", e);
        }
    }

    public static KeyPair generateKeyPair() {
        logger.debug("Generating a new key pair");
        return keyGenerator.generateKeyPair();
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

    //ref: https://stackoverflow.com/questions/11410770/load-rsa-public-key-from-file
    public static PrivateKey loadPrivateKey(String name) throws Exception {
        logger.info("Loading private key from file %s".formatted(name));
        byte[] keyBytes = KeyHelper.class.getClassLoader().getResourceAsStream(name).readAllBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String name) throws Exception {
        logger.info("Loading public key from file %s".formatted(name));
        byte[] keyBytes = KeyHelper.class.getClassLoader().getResourceAsStream(name).readAllBytes();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}