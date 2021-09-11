package mainpackage.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class KeyFileLoader {
    //ref: https://stackoverflow.com/questions/11410770/load-rsa-public-key-from-file
    public static PrivateKey getPrivate(String name)
            throws Exception {
        byte[] keyBytes = KeyFileLoader.class.getClassLoader().getResourceAsStream(name).readAllBytes();
        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublic(String name)
            throws Exception {
        byte[] keyBytes = KeyFileLoader.class.getClassLoader().getResourceAsStream(name).readAllBytes();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey result = kf.generatePublic(spec);
        System.out.println("Successfully loaded public key " + new String(Base64.getEncoder().encode(result.getEncoded())));
        return result;
    }
}
