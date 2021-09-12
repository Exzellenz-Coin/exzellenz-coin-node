package mainpackage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyHelper {
    private static final Logger logger = LogManager.getLogger(KeyHelper.class);
    public static KeyFactory keyFactory;
    private static KeyPairGenerator keyGenerator;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            keyFactory = KeyFactory.getInstance("Ed25519", "BC");
            keyGenerator = KeyPairGenerator.getInstance("Ed25519", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Initialize the key generator and generate a KeyPair
            // 256 bytes provides an acceptable security level
            keyGenerator.initialize(256, random);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            logger.error("Unable to initialize KeyGenerator", e);
        }
    }

    public static Signature createSignature() {
        try {
            return Signature.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            // This should never happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generateKeyPair() {
        logger.debug("Generating a new key pair");
        return keyGenerator.generateKeyPair();
    }

    public static boolean deletePrivateKey(BCEdDSAPrivateKey privateKey) {
        logger.debug("Wiping a private key");
        try {
            final var keyParamsField = BCEdDSAPrivateKey.class.getDeclaredField("eddsaPrivateKey");
            keyParamsField.setAccessible(true);
            final var keyParams = (Ed25519PrivateKeyParameters) keyParamsField.get(privateKey);
            final var dataField = Ed25519PrivateKeyParameters.class.getDeclaredField("data");
            dataField.setAccessible(true);
            final var data = (byte[]) dataField.get(keyParams);
            //noinspection ExplicitArrayFilling
            for (int i = 0; i < data.length; i++) {
                data[i] = 0;
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
        return keyFactory.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String name) throws Exception {
        logger.info("Loading public key from file %s".formatted(name));
        byte[] keyBytes = KeyHelper.class.getClassLoader().getResourceAsStream(name).readAllBytes();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(spec);
    }

    public static void storeKey(String name, byte[] data) throws Exception {
        final URL resource = KeyHelper.class.getClassLoader().getResource(name);
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(resource.toURI()));
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
