package blockchain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Chain {
	private static final Block START_BLOCK = new Block("0", new Transaction(null, null, null, null));
	public static final Wallet ROOT_WALLET = new Wallet(); // MOVE TO SERVER LATER maybe
	private final ArrayList<Block> blockChain;

	public Chain() {
		blockChain = new ArrayList<>();
		addBlock(START_BLOCK); //TODO: first entry in the blockchain
	}

	public void addBlock(final Block block) {
		blockChain.add(block);
	}

	public void addBlock(final String previous, final UUID sourceWalletID, final UUID targetWalletID, final BigDecimal amount, final byte[] signature) {
		blockChain.add(new Block(previous, new Transaction(sourceWalletID, targetWalletID, amount, signature)));
	}

	public Block getHead() {
		return blockChain.get(blockChain.size() - 1);
	}

	public BigDecimal getAmount(Wallet wallet) {
		var amount = new BigDecimal(0);
		for (final Block block : blockChain) {
			Transaction transaction = block.getTransaction();
			if (Objects.equals(transaction.getSourceWalletId(), transaction.getTargetWalletId())) {
				continue;
			}
			if (wallet.getId().equals(transaction.getSourceWalletId())) {
				amount = amount.subtract(transaction.getAmount());
			} else if (wallet.getId().equals(transaction.getTargetWalletId())) {
				amount = amount.add(transaction.getAmount());
			}
		}
		return amount;
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
