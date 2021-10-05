package mainpackage.blockchain;

import mainpackage.blockchain.staking.StakerIdentity;
import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;
import java.util.stream.Collectors;

public class Chain {
    private static final long MIN_BLOCK_DELAY = 10000; //TODO: set to something reasonable
    private static final BigDecimal INITIAL_REWARD = BigDecimal.valueOf(100);
    private static final BigDecimal MIN_STAKE = BigDecimal.valueOf(69); //minimum stake to become a staker
    private static final BigDecimal PENALTY = BigDecimal.ONE; //penalty for stakers that do not valdiate a block
    public static long EPOCH = 100;
    public static PublicKey FOUNDER_WALLET;

    static {
        try {
            FOUNDER_WALLET = KeyHelper.loadPublicKey("founder_wallet.der");
        } catch (Exception e) {
            e.printStackTrace();
            FOUNDER_WALLET = null;
        }
    }

    private final List<Block> blockChain;
    private final List<StakerIdentity> validators;
    private final Map<PublicKey, BigDecimal> wallets; //wallets that have received coins and their current balances

    public Chain() {
        blockChain = new ArrayList<>();
        validators = new ArrayList<>();
        wallets = new HashMap<>();
        addBlock(Block.createGenesisBlock()); //first entry in the mainpackage.blockchain
    }

    /**
     * Unconditionally adds a block to the blockchain. Updates wallets and validators.
     *
     * @param block the block to add
     */
    public void addBlock(final Block block) {
        blockChain.add(block);
        updateWallets(blockChain.size() - 1);
        if (block.getBlockNumber() % EPOCH == 0 && block.getBlockNumber() != 0) { //new epoch started, so get new stakers
            extractValidators(block.getBlockNumber());
        }
        this.tryAddBlockSync(block);
    }

    /**
     * Checks for invalid values and ensures the validator is correct, then calls addBlock.
     *
     * @param block the block to add
     * @return true if the block was added
     */
    public synchronized boolean tryAddBlockSync(final Block block) {
        try {
            if (block.getValidator().equals(Chain.getLeader(this.getHead(), this.validators, block.getTimeStamp())) //correct validator for the given timestamp
                    && isValidBlock(block)) { //correct values
                addBlock(block);
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * Checks if a private key is unseen for the given validator, then attempts to add it to the list of seen keys.
     *
     * @param publicKey the validator wallet the key belongs to
     * @param privateKey the private key to use as a ticket
     * @return true if the ticket was successfully used and is now seen
     */
    public synchronized boolean tryAddPrivateValidatorKeySync(PublicKey publicKey, PrivateKey privateKey) {
        try {
            return this.validators.stream().filter(e -> e.getPublicKey().equals(publicKey)).findAny().get().getStakeKeys().tryAcceptPrivateKey(privateKey);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the given validator is permitted to validate the next block
     *
     * @param validator validator wallet
     * @return true if they are permitted
     */
    public boolean permittedToValidateNewBlock(PublicKey validator) {
        return validator.equals(getLeader(this.getHead(), validators));
    }

    /**
     * @return the block at that given index
     */
    public Block get(int index) {
        return blockChain.get(index);
    }

    /**
     * @return the most recent block
     */
    public Block getHead() {
        return get(blockChain.size() - 1);
    } //most recent valid block

    /**
     * Gets the amount of coins in a wallet
     *
     * @param wallet wallet
     * @return amount of coins
     */
    public BigDecimal getAmount(PublicKey wallet) {
        if (wallets.containsKey(wallet))
            return wallets.get(wallet);
        //not cached, check manually
        var amount = new BigDecimal(0);
        for (final Block block : blockChain) {
            for (final Transaction transaction : block.getTransactions()) {
                if (Objects.equals(transaction.getSourceWalletId(), transaction.getTargetWalletId())) {
                    continue;
                }
                if (wallet.equals(transaction.getSourceWalletId())) {
                    amount = amount.subtract(transaction.getAmount());
                } else if (wallet.equals(transaction.getTargetWalletId())) {
                    amount = amount.add(transaction.getAmount());
                }
            }
        }
        return amount;
    }

    /**
     * Gets leader based on the current time
     *
     * @param block most recent block
     * @param validators validators for the next block
     * @return the validator for the next block
     */
    private static PublicKey getLeader(Block block, List<StakerIdentity> validators) {
        if (System.currentTimeMillis() - block.getTimeStamp() < MIN_BLOCK_DELAY) { //initial block creation delay
            return null;
        }
        if (validators.size() == 0) //if there are no stakers, the founder validates
            return Chain.FOUNDER_WALLET;
        long seed = new BigInteger(block.getHash().getBytes()).longValue(); //previous block used as an agreed upon "seed"
        Random rdm = new Random(seed); //randomness based on the most recent accepted block hash
        BigDecimal sum = validators.stream().map(StakerIdentity::getStake).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        List<Pair<PublicKey, BigDecimal>> weights = validators.stream()
                .map(p -> new Pair<>(p.getPublicKey(), p.getStake().divide(sum, RoundingMode.HALF_EVEN))).collect(Collectors.toList());
        BigDecimal selector = BigDecimal.valueOf(rdm.nextDouble() + Math.round(block.getTimeStamp() / MIN_BLOCK_DELAY));
        for (Pair<PublicKey, BigDecimal> pair : weights) {
            if (pair.two().compareTo(selector) >= 0) {
                return pair.one();
            }
        } // should never happen, but if some calculation errors occur etc
        return weights.get(weights.size() - 1).one();
    }

    /**
     * Gets leader based on the timestamp
     *
     * @param block most recent block
     * @param validators validators for the next block
     * @param timeStamp timestamp used to determine leader
     * @return the validator for the next block
     */
    private static PublicKey getLeader(Block block, List<StakerIdentity> validators, long timeStamp) { //gets leader based on the timestamp
        if (timeStamp - block.getTimeStamp() < MIN_BLOCK_DELAY) { //initial block creation delay
            return null;
        }
        if (validators.size() == 0) //if there are no stakers, the founder validates
            return Chain.FOUNDER_WALLET;
        long seed = new BigInteger(block.getHash().getBytes()).longValue(); //previous block used as an agreed upon "seed"
        Random rdm = new Random(seed); //randomness based on the most recent accepted block hash
        BigDecimal sum = validators.stream().map(StakerIdentity::getStake).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        List<Pair<PublicKey, BigDecimal>> weights = validators.stream()
                .map(p -> new Pair<>(p.getPublicKey(), p.getStake().divide(sum, RoundingMode.HALF_EVEN))).collect(Collectors.toList());
        BigDecimal selector = BigDecimal.valueOf(rdm.nextDouble() + Math.round(block.getTimeStamp() / MIN_BLOCK_DELAY));
        for (Pair<PublicKey, BigDecimal> pair : weights) {
            if (pair.two().compareTo(selector) >= 0) {
                return pair.one();
            }
        } // should never happen, but if some calculation errors occur etc
        return weights.get(weights.size() - 1).one();
    }

    /**
     * Checks if the hashes for the most recent block is valid TODO: rework this to work with any block?
     *
     * @param block block to validate
     * @return if the block is valid
     */
    public boolean isValidBlock(Block block) {
        try {
            Block lastValid = blockChain.get(blockChain.size() - 1);
            return lastValid.getHash().equals(block.getPrevHash()) //wrong last hash
                    && Hash.createHash(block).equals(block.getHash()); //wrong hash
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * Calls isValidBlock and verifies the block signature TODO: maybe redundant?
     *
     * @param block block to validate
     * @return if the block is valid
     */
    public boolean isValidNewBlock(Block block) {
        return isValidBlock(block) && !block.verifySignature(block.getValidator());
    }

    /**
     * Checks if the chain is valid TODO: maybe redundant?
     *
     * @param beginBlockIndex index of the first block
     * @return if the chain is valid
     */
    public boolean isValidChain(int beginBlockIndex) throws IndexOutOfBoundsException {
        if (beginBlockIndex <= 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        if (beginBlockIndex > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        for (int i = beginBlockIndex; i < this.blockChain.size(); i++) {
            Block prev = this.blockChain.get(i - 1);
            Block cur = this.blockChain.get(i);
            if (!prev.getHash().equals(cur.getHash()) || !cur.getHash().equals(Hash.createHash(cur))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the validators from transactions during the previous epoch. Must be called on the first block of a new epoch.
     *
     * @param beginBlockIndex index of the first block of an epoch
     */
    public void extractValidators(long beginBlockIndex) throws IndexOutOfBoundsException {
        if (beginBlockIndex - EPOCH < 0)
            throw new IndexOutOfBoundsException("Last epoch must exist");
        if (beginBlockIndex > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        this.validators.clear();
        for (long i = beginBlockIndex - EPOCH; i < beginBlockIndex; i++) {
            List<Transaction> cur = this.blockChain.get((int) i).getTransactions();
            cur.stream()
                    .filter(e -> e.getTargetWalletId().equals(StakingTransaction.STAKING_WALLET)) //only staking transactions
                    .forEach(stakeTransaction -> {
                        if (this.validators.stream().filter(validator -> validator.getPublicKey().equals(stakeTransaction.getSourceWalletId())).count() != 0) { //find staker in list if he exists
                            this.validators.stream().filter(validator -> validator.getPublicKey().equals(stakeTransaction.getSourceWalletId()))
                                    .forEach(validator -> validator.setStake(validator.getStake().add(stakeTransaction.getAmount())));
                        } else { //create new entry
                            try {
                                this.validators.add(new StakerIdentity(stakeTransaction.getSourceWalletId(), StakingTransaction.parseDataToObject(stakeTransaction.getData().split("@")), stakeTransaction.getAmount()));
                            } catch (SignatureException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    /**
     * Updates the wallets with transactions from beginBlock onwards
     *
     * @param beginBlockIndex update wallets using block at this index and all further indices
     */
    public void updateWallets(int beginBlockIndex) throws IndexOutOfBoundsException {
        if (beginBlockIndex < 0)
            throw new IndexOutOfBoundsException("Index must be greater than 0");
        if (beginBlockIndex > this.blockChain.size())
            throw new IndexOutOfBoundsException("Index exceeds length of chain");
        for (int i = beginBlockIndex; i < this.blockChain.size(); i++) {
            Block curBlock = this.blockChain.get(i);
            List<Transaction> curTransactions = curBlock.getTransactions();
            //normal transfers
            curTransactions
                    .forEach(transaction -> {
                        if (transaction.getSourceWalletId() != null) { //accounts for coinbase transactions
                            //update senders
                            if (this.wallets.containsKey(transaction.getSourceWalletId())) { //find sender if he exists
                                this.wallets.replace(transaction.getSourceWalletId(), this.wallets.get(transaction.getSourceWalletId()).subtract(transaction.getAmount().add(transaction.getTip())));
                            }
                        }
                        //update receivers
                        if (this.wallets.containsKey(transaction.getTargetWalletId())) { //find receiver if he exists
                            this.wallets.replace(transaction.getTargetWalletId(), this.wallets.get(transaction.getTargetWalletId()).add(transaction.getAmount()));
                        } else { //create new entry
                            this.wallets.put(transaction.getTargetWalletId(), transaction.getAmount());
                        }
                    });
            //validator reward
            PublicKey validatorWallet = curBlock.getValidator();
            //block reward
            BigDecimal tips = curTransactions.stream().map(Transaction::getTip).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (this.wallets.containsKey(validatorWallet)) {
                this.wallets.replace(validatorWallet, this.wallets.get(validatorWallet).add(tips.add(calculateReward(curBlock.getBlockNumber()))));
            } else {
                this.wallets.put(validatorWallet, tips);
            }
        }
    }

    /**
     * Calculates the reward for validation a block
     *
     * @param blockIndex index of the block
     * @return the reward
     */
    public BigDecimal calculateReward(long blockIndex) {
        if (blockIndex == -1)
            throw new NoSuchElementException("Could not find block in blockchain");
        if (blockIndex == 0)
            return BigDecimal.ZERO;
        return INITIAL_REWARD.divide(BigDecimal.valueOf(blockIndex), Transaction.DOWN_ROUNDING_SCALE, RoundingMode.DOWN); //TODO: find more advanced equation
    }

    /**
     * Checks if a wallet has enough funds for a transaction
     *
     * @param transaction transaction to check
     * @return true if the transaction is valid
     */
    public boolean isValidTransaction(Transaction transaction) {
        if (!wallets.containsKey(transaction.getSourceWalletId()))
            return false;
        return transaction.getAmount().add(transaction.getTip()).compareTo(wallets.get(transaction.getSourceWalletId())) != 1;
    }

    /**
     * Calculates the amount of tickets a user has to send for a given stake
     *
     * @param stakeAmount amount staked
     * @return number of tickets to include in staking transaction
     */
    public int calculateNumberStakeKeys(BigDecimal stakeAmount) {
        return 100; //TODO: need some algorithm for this
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
