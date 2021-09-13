package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class Chain {
	public static PublicKey FOUNDER_WALLET;

	static {
		try {
			FOUNDER_WALLET = KeyHelper.loadPublicKey("founder_wallet.der");
		} catch (Exception e) {
			e.printStackTrace();
			FOUNDER_WALLET = null;
		}
	}

	private static final BigDecimal INITIAL_REWARD = BigDecimal.valueOf(100);
	private static final BigDecimal MIN_STAKE = BigDecimal.valueOf(69); //minimum stake to become a staker
	private static final BigDecimal PENALTY = BigDecimal.ONE; //penalty for stakers that do not valdiate a block
	private final List<Block> blockChain;
	private final List<Pair<PublicKey, BigDecimal>> validators;  //public key and stake of VALID stakers
	private final Map<PublicKey, BigDecimal> wallets; //wallets that have received coins and their current balances

	public Chain() {
		blockChain = new ArrayList<>();
		validators = new ArrayList<>();
		wallets = new HashMap<>();
		addBlock(Block.createGenesisBlock()); //first entry in the mainpackage.blockchain
	}

	public void addBlock(final Block block) {
		// if (!block.getPrevHash().equals(getHead().getHash())) throw new IllegalArgumentException("Block must have the current head as previous block!");
		blockChain.add(block);
	}

	public synchronized boolean tryAddBlockSync(final Block block) {
		if (isValidBlock(block)) {
			addBlock(block);
			return true;
		}
		return false;
	}

	public boolean permittedToValidateNewBlock(PublicKey validator) {
		return true; //TODO: overhaul validator selection
	}

	public Block get(int index) {
		return blockChain.get(index);
	}

	public Block getHead() {
		return get(blockChain.size() - 1);
	} //most recent valid block

	public BigDecimal getCachedAmount(PublicKey wallet) {
		return wallets.get(wallet);
	}

	public BigDecimal getAmount(PublicKey wallet) { //TODO: make this use the cached values
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

	private PublicKey getLeader(Block block, List<Pair<PublicKey, BigDecimal>> validators) {
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

	public ArrayList<PublicKey> getLeaders(Block block, int depth) {
		if (depth <= 0)
			throw new IndexOutOfBoundsException("Index must be greater than 0");
		Map<PublicKey, BigDecimal> mapping = validators.stream().collect(Collectors.toMap(Pair::one, Pair::two));
		ArrayList<PublicKey> result = new ArrayList<>();
		for (int i = 0; i < depth; i++) {
			List<Pair<PublicKey, BigDecimal>> temp = mapping.entrySet().stream()
					.map(e -> new Pair<>(e.getKey(), e.getValue())).collect(Collectors.toList());
			PublicKey leader = getLeader(block, temp);
			result.add(leader);
			mapping.remove(leader);
		}
		return result;
	}

	public boolean isValidBlock(Block block) {
		Block lastValid = blockChain.get(blockChain.size() - 1);
		if (!lastValid.getHash().equals(block.getPrevHash()) //wrong last hash
			|| !Hash.createHash(block).equals(block.getHash()) //wrong hash
			//|| !getLeaders(lastValid, 1).contains(block.getValidator())
			//TODO: currently ignores who wrote the block for testing purposes
		) {
			//System.out.println(Hash.createHash(block) + " - " + block.getHash() + " - " + block.getHash());
			return false;
		}
		return true;
	}

	public boolean isValidChain() {
		return isValidChain(1);
	}

	public boolean isValidChain(int beginBlock) throws IndexOutOfBoundsException {
		if (beginBlock <= 0)
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

	public void updateValidators() {
		updateValidators(1);
	}

	public void updateValidators(int beginBlock) throws IndexOutOfBoundsException { //TODO: create tests
		if (beginBlock <= 0)
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

	public void updateWallets() {
		updateWallets(0);
	}

	public void updateWallets(int beginBlock) throws IndexOutOfBoundsException { //TODO: create tests
		if (beginBlock < 0)
			throw new IndexOutOfBoundsException("Index must be greater than 0");
		if (beginBlock > this.blockChain.size())
			throw new IndexOutOfBoundsException("Index exceeds length of chain");
		for (int i = beginBlock; i < this.blockChain.size(); i++) {
			Block curBlock = this.blockChain.get(i);
			List<Transaction> curTransactions = curBlock.getTransactions();
			//normal transfers
			curTransactions
					.forEach(transaction -> {
						if (transaction.getSourceWalletId() != null) { //accounts for coinbase transactions
							//update senders
							if (this.wallets.containsKey(transaction.getSourceWalletId())) { //find sender if he exists
								this.wallets.replace(transaction.getSourceWalletId(), this.wallets.get(transaction.getSourceWalletId()).subtract(transaction.getAmount().add(transaction.getTransactionFee())));
							}
						}
						//update receivers
						if (this.wallets.containsKey(transaction.getTargetWalletId())) { //find receiver if he exists
							this.wallets.replace(transaction.getSourceWalletId(), this.wallets.get(transaction.getSourceWalletId()).add(transaction.getAmount()));
						} else { //create new entry
							wallets.put(transaction.getTargetWalletId(), transaction.getAmount());
						}
					});
			//validator reward
			PublicKey validatorWallet = curBlock.getValidator();
			//block reward
			BigDecimal tips = curTransactions.stream().map(Transaction::getTransactionFee).reduce(BigDecimal.ZERO, BigDecimal::add);
			this.wallets.replace(validatorWallet, this.wallets.get(validatorWallet).add(tips.add(calculateReward(curBlock))));
		}
	}

	public BigDecimal calculateReward(Block block) { //this is the staker reward for validating a block
		int index = blockChain.indexOf(block);
		if (index == -1)
			throw new NoSuchElementException("Could not find block in blockchain");
		if (index == 0)
			return BigDecimal.ZERO;
		return INITIAL_REWARD.divide(BigDecimal.valueOf(index));
	}

	public boolean isValidTransaction(Transaction transaction) {
		if (!wallets.containsKey(transaction.getSourceWalletId()))
			return false;
		return transaction.getAmount().add(transaction.getTransactionFee()).compareTo(wallets.get(transaction.getSourceWalletId())) != 1;
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
