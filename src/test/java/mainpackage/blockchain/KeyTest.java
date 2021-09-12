package mainpackage.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mainpackage.util.JsonMapper;
import mainpackage.util.KeyHelper;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class KeyTest {
    // This is here to init the KeyHelper class.
    // This results in more accurate timing for the test cases
    static {
        final KeyFactory keyFactory = KeyHelper.keyFactory;
        final ObjectMapper mapper = JsonMapper.mapper;
    }

    @Test
    @DisplayName("Generating Test")
    public void generateTest() {
        var keyPair = KeyHelper.generateKeyPair();
        assertNotNull(keyPair, "KeyPair is null");
        assertNotNull(keyPair.getPublic(), "PublicKey is null");
        assertNotNull(keyPair.getPrivate(), "PrivateKey is null");
    }

    @Test
    @DisplayName("Serialization Test")
    public void testKeySerialization() throws JsonProcessingException {
        var mapper = JsonMapper.mapper;
        var wallet = KeyHelper.generateKeyPair();
        var publicKeyJson = mapper.writeValueAsString(wallet.getPublic());
        var privateKeyJson = mapper.writeValueAsString(wallet.getPrivate());
        assertNotNull(publicKeyJson);
        assertNotNull(privateKeyJson);
        assertNotEquals("", publicKeyJson);
        assertNotEquals("", privateKeyJson);
        var publicKey = mapper.readValue(publicKeyJson, PublicKey.class);
        var privateKey = mapper.readValue(privateKeyJson, PrivateKey.class);
        assertEquals(wallet.getPublic(), publicKey);
        assertEquals(wallet.getPrivate(), privateKey);
    }

    @Test
    @DisplayName("Private Key Deletion Test")
    public void testPrivateKeyDeletion() throws InvalidKeyException, SignatureException {
        var privateKey = KeyHelper.generateKeyPair().getPrivate();
        final byte[] sign1 = sign(privateKey);
        final byte[] sign2 = sign(privateKey);
        assertThat(sign2).isEqualTo(sign2);
        assertTrue(KeyHelper.deletePrivateKey((BCEdDSAPrivateKey) privateKey), "PrivateKey deletion failed");
        final byte[] sign3 = sign(privateKey);
        assertThat(sign1).isNotEqualTo(sign3);
    }

    private byte[] sign(PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var signature = KeyHelper.createSignature();
        signature.initSign(privateKey);
        signature.update((byte) 2);
        return signature.sign();
    }
}
