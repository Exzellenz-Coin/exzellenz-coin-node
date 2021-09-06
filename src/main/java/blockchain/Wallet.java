package blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.UUID;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Wallet {
	private UUID id = UUID.randomUUID();
	private PrivateKey privateKey;
	private PublicKey publicKey;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public Wallet() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	public UUID getId() {
		return id;
	}
}
