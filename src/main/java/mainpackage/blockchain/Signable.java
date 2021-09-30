package mainpackage.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface Signable {
    void sign(PrivateKey privateKey) throws Exception;

    boolean verifySignature(PublicKey publicKey) throws Exception;
}
