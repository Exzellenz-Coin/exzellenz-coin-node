package mainpackage.blockchain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Chain {
    public static final Wallet ROOT_WALLET = new Wallet(); // MOVE TO SERVER LATER maybe
    private static final Block START_BLOCK = Block.createGenesisBlock();
    private final ArrayList<Block> blockChain;
    private final ArrayList<PublicKey> validators;

    public Chain() {
        blockChain = new ArrayList<>();
        blockChain.add(START_BLOCK); //first entry in the mainpackage.blockchain
        validators = null;
    }

    public void addBlock(final Block block) {
        if (!block.getPrevHash().equals(getHead().getHash()))
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

    public PublicKey getLeader() {
        long seed = new BigInteger(blockChain.get(blockChain.size() - 1).getHash().getBytes()).longValue();
        Random rdm = new Random(seed);
        return this.validators.get(rdm.nextInt(this.validators.size())); //TODO: select randomly based on the stake
    }

    public boolean isValidBlock(Block block) {
        Block lastValid = blockChain.get(blockChain.size() - 1);
        if (!lastValid.getHash().equals(block.getPrevHash()) //wrong last hash
                || !Hash.createHash(block).equals(block.getHash()) //wrong hash
                || validators.contains(block.getValidator()) //leader is invalid<<<<
        ) {
            return false;
        }
        return true;
    }

    public boolean isValidChain() {
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
}
