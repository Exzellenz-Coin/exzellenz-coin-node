package mainpackage.blockchain;

import mainpackage.util.JsonMapper;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;

import static org.junit.jupiter.api.Assertions.*;

public class KeyTest {
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
