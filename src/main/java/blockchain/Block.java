package blockchain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Block {
	//private final static PublicKey FOUNDER_WALLET; //initially all coins here
	private final String prevHash; //hash of the previous block
	private final long timeStamp;
	private final List<Transaction> transactions;
	private PublicKey validator;
	private final Signature signature;
	private final String hash; //hash of this block

	public Block(String prevHash, List<Transaction> transactions, PublicKey validator, Signature signature) {
		this.prevHash = prevHash;
		this.transactions = transactions;
		this.validator = validator;
		this.signature = signature;
		this.timeStamp = System.currentTimeMillis();
		this.hash = Hash.createHash(this);
	}

	public String getPrevious() {
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

	public PublicKey getValidator() {
		return validator;
	}

	public Signature getSignature() {
		return signature;
	}

	@Override
	public int hashCode() {
		return Objects.hash(prevHash, timeStamp, transactions, hash, validator, signature);
	}

	public static Block createGenesisBlock() {
		return new Block("0",
				Collections.singletonList(new Transaction(null, null, null, null)),
				null, null);
	}
}
