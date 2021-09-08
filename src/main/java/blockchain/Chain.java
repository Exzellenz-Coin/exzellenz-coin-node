package blockchain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class Chain {
	public static final Wallet ROOT_WALLET = new Wallet(); // MOVE TO SERVER LATER maybe
	private static final Block START_BLOCK = Block.createGenesisBlock();
	private final ArrayList<Block> blockChain;

	public Chain() {
		blockChain = new ArrayList<>();
		blockChain.add(START_BLOCK); //first entry in the blockchain
	}

	public void addBlock(final Block block) {
		if (!block.getPrevious().equals(getHead().getHash()))
			throw new IllegalArgumentException("Block must have the current head as previous block!");
		blockChain.add(block);
	}

	public Block getHead() {
		return blockChain.get(blockChain.size() - 1);
	} //most recent valid block

	public BigDecimal getAmount(Wallet wallet) {
		var amount = new BigDecimal(0);
		for (final Block block : blockChain) {
			for (final Transaction transaction : block.getTransactions()) {
				if (Objects.equals(transaction.getSourceWalletId(), transaction.getTargetWalletId())) {
					continue;
				}
				if (wallet.getPublicKey().equals(transaction.getSourceWalletId())) {
					amount = amount.subtract(transaction.getAmount());
				} else if (wallet.getPublicKey().equals(transaction.getTargetWalletId())) {
					amount = amount.add(transaction.getAmount());
				}
			}
		}
		return amount;
	}

	public boolean isValid() {
		for (int i = 1; i < this.blockChain.size(); i++) {
			Block prev = this.blockChain.get(i - 1);
			Block cur = this.blockChain.get(i);
			if (!prev.getHash().equals(cur.getHash()) || !cur.getHash().equals(Hash.createHash(cur))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Chain{" +
			   "blockChain=" + blockChain +
			   '}';
	}

	public void printChain() {
		System.out.println("Blockchain: ");
		blockChain.forEach(System.out::println);
	}
}
