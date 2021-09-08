package blockchain;

import java.util.Objects;

public class Block {
    private final String previous;
    private final long timeStamp;
    private final Transaction transaction;
    private final String hash;

	public Block(String prevHash, List<Transaction> transactions, PublicKey validator, Signature signature) {
		this.prevHash = prevHash;
		this.transactions = transactions;
		this.validator = validator;
		this.signature = signature;
		this.timeStamp = System.currentTimeMillis();
		this.hash = Hash.createHash(this);
	}

    public String getPrevious() {
        return previous;
    }

	public String getHash() {
		return hash;
	}

    public Transaction getTransaction() {
        return transaction;
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
        return timeStamp == block.timeStamp && previous.equals(block.previous)
                && Objects.equals(hash, block.hash) && Objects.equals(transaction, block.transaction);
    }

    @Override
    public int hashCode() { // remove hash when hash on server side will be set by miner accordance
        return Objects.hash(previous, transaction, timeStamp, hash);
    }

    @Override
    public String toString() {
        return "Block{" +
                "previous='" + previous + '\'' +
                ", timeStamp=" + timeStamp +
                ", transaction=" + transaction +
                ", hash='" + hash + '\'' +
                '}';
    }
}
