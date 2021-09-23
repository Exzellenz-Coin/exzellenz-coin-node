package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.staking.StakeKeys;
import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.server.Server;
import mainpackage.server.message.block.CreatedBlockMessage;
import mainpackage.server.message.chain.RequestChainLengthMessage;
import mainpackage.server.message.transaction.CreatedTransactionMessage;
import mainpackage.util.KeyHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.security.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of INode that can perform every possible action.
 */
public class FullNode implements INode {
	private static BigDecimal DEFAULT_TIP = BigDecimal.valueOf(0.000001);

	protected static final Logger logger = LogManager.getLogger(FullNode.class);
	protected Server server;
	protected final Set<NodeEntry> network;
	protected final Chain blockChain;
	protected Block newBlock;
	protected ArrayList<Transaction> unofficialTransactions;
	private StakeKeys stakeKeys;
	private PublicKey nodeWallet;
	private PrivateKey nodePrivateKey;

	public FullNode() {
		this.server = new Server(this);
		this.network = new HashSet<>();
		this.blockChain = new Chain();
		this.unofficialTransactions = new ArrayList<>();
		this.newBlock = null; //have to set this after you have the newest blockchain
		this.stakeKeys = new StakeKeys(); //TODO: generate keys upon staking!!!
		try {
			nodeWallet = KeyHelper.loadPublicKey("node_wallet.der");
			nodePrivateKey = KeyHelper.loadPrivateKey("node_pk.der");
		} catch (Exception e) {
			e.printStackTrace();
			nodeWallet = null;
			nodePrivateKey = null;
		}
	}

	@Override
	public void start() {
		server.start();
		resetNetwork();
	}

	@Override
	public void update() {
		server.sendToAll(new RequestChainLengthMessage()); //get most recent chain
	}

	@Override
	public void stop() {
		server.shutdown();
	}

	@Override
	public boolean validateBlock(boolean force) { //if validator is chosen by the system they can add a block and claim rewards
		try {
			finalizeBlock(); //creates and populates a valid newBlock
		} catch (Exception e) {
			return false;
		}
		if ((force || blockChain.permittedToValidateNewBlock(nodeWallet)) && blockChain.tryAddBlockSync(newBlock)) {
			server.sendToAll(new CreatedBlockMessage(newBlock, stakeKeys.popPrivateKey()));
			return true;
		}
		return false;
	}

	@Override
	public void finalizeBlock() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		//template block
		newBlock =  new Block(blockChain.getHead().getHash(), blockChain.getHead().getBlockNumber() + 1, new ArrayList<>(), nodeWallet);
		//sort transactions based on highest tips and add to the block
		unofficialTransactions.sort(Comparator.comparing(Transaction::getTip));
		while (unofficialTransactions.size() != 0 && newBlock.getTransactions().size() != Block.MAX_TRANSACTIONS) {
			Transaction cur = unofficialTransactions.get(0);
			if (blockChain.isValidTransaction(cur)) {
				newBlock.getTransactions().add(cur);
			}
			unofficialTransactions.remove(0);
		}
		//sign and hash
		newBlock.sign(nodePrivateKey);
		newBlock.createHash();
	}

	@Override
	public boolean shouldRelayMessages() {
		return true;
	}

	@Override
	public Set<NodeEntry> getNetwork() {
		return network;
	}

	@Override
	public Chain getBlockChain() {
		return this.blockChain;
	}

	@Override
	public void resetNetwork() {
		network.clear();
		network.add(getNodeEntry());
	}

	@Override
	public boolean addNodeEntry(NodeEntry nodeEntry) {
		boolean added = network.add(nodeEntry);
		if (added)
			logger.debug("Added node %s from network".formatted(nodeEntry));
		return added;
	}

	@Override
	public boolean removeNodeEntry(NodeEntry nodeEntry) {
		boolean removed = network.remove(nodeEntry);
		if (removed)
			logger.debug("Removed node %s from network".formatted(nodeEntry));
		return removed;
	}

	@Override
	public NodeEntry getNodeEntry() {
		return new NodeEntry("localhost", server.getPort()); // TODO: Replace localhost with public IP address
	}

	@Override
	public Server getServer() { return server; }

	@Override
	public Block getNewBlock() {
		return newBlock;
	}

	@Override
	public void setNewBlock(Block block) {
		this.newBlock = block;
	}

	@Override
	public boolean addTransaction(Transaction transaction) {
		if (Transaction.validValues(transaction) && !unofficialTransactions.contains(transaction)) {
			unofficialTransactions.add(transaction);
			return true;
		}
		return false;
	}

	public boolean stake(BigDecimal amount) throws SignatureException, InvalidKeyException {
		var transaction = new StakingTransaction(nodeWallet, amount, DEFAULT_TIP, stakeKeys);
		transaction.sign(nodePrivateKey);
		server.sendToAll(new CreatedTransactionMessage(transaction));
		return true;
	}
}
