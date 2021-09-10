package mainpackage.blockchain;

import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Chain {
    public static final Wallet ROOT_WALLET = new Wallet(); // TODO: change to PublicKey and load from resource folder
    private static final Block START_BLOCK = Block.createGenesisBlock();
    private static final BigDecimal MIN_STAKE = BigDecimal.valueOf(69); //minimum stake to become a staker
    private static final BigDecimal PENALTY = BigDecimal.ONE; //penalty for stakers that do not valdiate a block
    private final ArrayList<Block> blockChain;
    private final ArrayList<Pair<PublicKey, BigDecimal>> validators; //public key and stake of VALID stakers
    private final ArrayList<Pair<PublicKey, BigDecimal>> wallets; //wallets that have received coins and their current balances

    public Chain() {
        blockChain = new ArrayList<>();
        validators = new ArrayList<>();
        wallets = new ArrayList<>();
        blockChain.add(START_BLOCK); //first entry in the mainpackage.blockchain
    }

    public void addBlock(final Block block) {
        if (!block.getPrevHash().equals(getHead().getHash()))
            throw new IllegalArgumentException("Block must have the current head as previous block!");
        blockChain.add(block);
    }

    public Block get(int index) { return blockChain.get(index); };

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

    public ArrayList<PublicKey> getLeaders(Block block, int depth) throws IndexOutOfBoundsException { // calculates the first "depth"-number of leaders
        if (depth >= 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        ArrayList<PublicKey> result = new ArrayList<>();
        long seed = new BigInteger(block.getHash().getBytes()).longValue();
        Random rdm = new Random(seed); //randomness based on the most recent accepted block hash
        BigDecimal sum = validators.stream().map(Pair::two).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        List<Pair<PublicKey, BigDecimal>> weights = validators.stream()
                .map(p -> new Pair<>(p.one(), p.two().divide(sum, RoundingMode.HALF_EVEN))).collect(Collectors.toList());
        BigDecimal selector = BigDecimal.valueOf(rdm.nextDouble());
        while (depth != 0) { //we add depth new PublicKeys
            for (Pair<PublicKey, BigDecimal> pair : weights) {
                if (pair.two().compareTo(selector) >= 0) {
                    result.add(pair.one());
                    depth--;
                    break;
                }
            }
        }
        return result;
    }

    public boolean isValidBlock(Block block) {
        Block lastValid = blockChain.get(blockChain.size() - 1);
        if (!lastValid.getHash().equals(block.getPrevHash()) //wrong last hash
                || !Hash.createHash(block).equals(block.getHash()) //wrong hash
                //currently only allows the first leader TODO: add acceptance for other leaders if the first leader missed their turn
                || !getLeaders(lastValid, 1).contains(block.getValidator())
                //TODO: check for valid transactions
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

    public boolean isValidChain(int beginBlock) throws IndexOutOfBoundsException {
        if (beginBlock >= 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        if (beginBlock > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        for (int i = beginBlock; i < this.blockChain.size(); i++) {
            Block prev = this.blockChain.get(i - 1);
            Block cur = this.blockChain.get(i);
            if (!prev.getHash().equals(cur.getHash()) || !cur.getHash().equals(Hash.createHash(cur))) {
                return false;
            }
        }
        return true;
    }

    public void updateValidators(int beginBlock) throws IndexOutOfBoundsException {
        if (beginBlock >= 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        if (beginBlock > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        for (int i = beginBlock; i < this.blockChain.size(); i++) {
            ArrayList<Transaction> cur = (ArrayList<Transaction>) this.blockChain.get(i).getTransactions();
            cur.stream()
                    .filter(e -> e.getTargetWalletId().equals(StakingTransaction.STAKING_WALLET)) //only staking transactions
                    .forEach(stakeTransaction -> {
                        if (this.validators.stream().filter(validator -> validator.one().equals(stakeTransaction.getSourceWalletId())).count() != 0) { //find staker in list if he exists
                            this.validators.stream().filter(validator -> validator.one().equals(stakeTransaction.getSourceWalletId()))
                                    .forEach(validator -> validator = new Pair<>(validator.one(), validator.two().add(stakeTransaction.getAmount())));
                        } else { //create new entry
                            this.validators.add(new Pair<>(stakeTransaction.getSourceWalletId(), stakeTransaction.getAmount()));
                        }
                    });
        }
    }

    public void updateWallets(int beginBlock) throws IndexOutOfBoundsException {
        if (beginBlock >= 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        if (beginBlock > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        for (int i = beginBlock; i < this.blockChain.size(); i++) {
            ArrayList<Transaction> cur = (ArrayList<Transaction>) this.blockChain.get(i).getTransactions();
            cur.stream()
                    .forEach(transaction -> {
                        if (this.wallets.stream().filter(sender -> sender.one().equals(transaction.getSourceWalletId())).count() != 0) { //find sender in list if he exists
                            this.wallets.stream().filter(sender -> sender.one().equals(transaction.getSourceWalletId()))
                                    .forEach(sender -> sender = new Pair<>(sender.one(), sender.two().add(transaction.getAmount())));
                        } else { //create new entry
                            this.wallets.add(new Pair<>(transaction.getSourceWalletId(), transaction.getAmount()));
                        }
                    });
        }
    }

    public int size() {
        return this.blockChain.size();
    }

    @Override
    public String toString() {
        return "Chain{" +
                "blockChain=" + blockChain +
                '}';
    }
}
