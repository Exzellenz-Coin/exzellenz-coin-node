package mainpackage.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import mainpackage.util.JsonMapper;
import mainpackage.util.KeyHelper;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.bouncycastle.jce.spec.ECKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
	public void testPrivateKeyDeletion() {
		final PrivateKey privateKey = KeyHelper.generateKeyPair().getPrivate();
		assertTrue(KeyHelper.deletePrivateKey((ECPrivateKey) privateKey), "PrivateKey deletion failed");
		//noinspection ResultOfMethodCallIgnored
		assertThrows(Exception.class, privateKey::toString);
	}
}
