package mainpackage.blockchain;

import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Chain {
    public static final Wallet ROOT_WALLET = new Wallet(); // TODO: change to PublicKey and load from resource folder
    private static final Block START_BLOCK = Block.createGenesisBlock();
    private static final BigDecimal MIN_STAKE = BigDecimal.valueOf(69); //minimum stake to become a staker
    private static final BigDecimal PENALTY = BigDecimal.ONE; //penalty for stakers that do not valdiate a block
    private final ArrayList<Block> blockChain;
    private final ArrayList<Pair<PublicKey, BigDecimal>> validators; //public key and stake of VALID stakers

    public Chain() {
        blockChain = new ArrayList<>();
        validators = new ArrayList<>();
        blockChain.add(START_BLOCK); //first entry in the mainpackage.blockchain
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

    public PublicKey getLeader(Block block) {
        long seed = new BigInteger(block.getHash().getBytes()).longValue();
        Random rdm = new Random(seed); //randomness based on the most recent accepted block hash
        BigDecimal sum = validators.stream().map(Pair::two).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        List<Pair<PublicKey, BigDecimal>> weights = validators.stream()
                .map(p -> new Pair<>(p.one(), p.two().divide(sum, RoundingMode.HALF_EVEN))).collect(Collectors.toList());
        BigDecimal selector = BigDecimal.valueOf(rdm.nextDouble());
        for (Pair<PublicKey, BigDecimal> pair : weights) {
            if (pair.two().compareTo(selector) >= 0) {
                return pair.one();
            }
        } // should never happen, but if some calculation errors occur etc
        return weights.get(weights.size() - 1).one();
    }

    public boolean isValidBlock(Block block) {
        Block lastValid = blockChain.get(blockChain.size() - 1);
        if (!lastValid.getHash().equals(block.getPrevHash()) //wrong last hash
                || !Hash.createHash(block).equals(block.getHash()) //wrong hash
                 //TODO: leader is invalid
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

    public void findValidators() {

    }

    @Override
    public String toString() {
        return "Chain{" +
                "blockChain=" + blockChain +
                '}';
    }
}
