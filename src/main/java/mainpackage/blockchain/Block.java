package mainpackage.blockchain;

import mainpackage.blockchain.transaction.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Block implements Signable {
	public final static int MAX_TRANSACTIONS = 10;
	private final String prevHash; //hash of the previous block
	private long timeStamp;
	private final List<Transaction> transactions;
	private PublicKey validator; //rewards sent here
	private byte[] signature; //staker signature
	private String hash; //hash of this block

	public Block(String prevHash, List<Transaction> transactions, PublicKey validator, byte[] signature) {
		this.prevHash = prevHash;
		this.transactions = transactions;
		this.validator = validator;
		this.signature = signature;
		this.timeStamp = System.currentTimeMillis();
		this.hash = Hash.createHash(this);
	}

	public String getPrevHash() {
		return prevHash;
	}

	public String getHash() {
		return hash;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public PublicKey getValidator() {
		return validator;
	}

	public byte[] getSignature() {
		return signature;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Block block = (Block) o;
		return timeStamp == block.timeStamp && prevHash.equals(block.prevHash)
				&& Objects.equals(hash, block.hash) && Objects.equals(transactions, block.transactions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(prevHash, timeStamp, transactions, hash, validator, signature);
	}

	public void updateHash() {
		this.hash = Hash.createHash(this);
	}

	public static Block createGenesisBlock() {
		Block block = new Block("0", Collections.singletonList(new Transaction(null, Chain.FOUNDER_WALLET, BigDecimal.valueOf(100), BigDecimal.ZERO, null)), Chain.FOUNDER_WALLET, null);
		block.updateHash();
		return block;
	}

	@Override
	public void sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		byte[] transactionData = org.bouncycastle.util.Arrays.concatenate(transactions.stream().map(Transaction::toByteArray).toArray(byte[][]::new));
		byte[] data = org.bouncycastle.util.Arrays.concatenate(prevHash.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(timeStamp).toByteArray(),transactionData);
		data = org.bouncycastle.util.Arrays.concatenate(data, validator.getEncoded());
		signature.update(data);
		this.signature = signature.sign();
	}

	@Override
	public boolean verifySignature(PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		byte[] transactionData = org.bouncycastle.util.Arrays.concatenate((byte[][]) transactions.stream().map(Transaction::toByteArray).toArray());
		byte[] data = org.bouncycastle.util.Arrays.concatenate(prevHash.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(timeStamp).toByteArray(),transactionData);
		data = org.bouncycastle.util.Arrays.concatenate(data, validator.getEncoded());
		signature.update(data);
		return signature.verify(this.signature);
	}
}
