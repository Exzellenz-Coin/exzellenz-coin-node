package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.server.Server;
import mainpackage.server.message.block.CreatedBlockMessage;
import mainpackage.server.message.block.SendBlockMessage;
import mainpackage.server.message.chain.RequestChainLengthMessage;
import mainpackage.util.KeyFileLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of INode that can perform every possible action.
 */
public class FullNode implements INode {
	protected static final Logger logger = LogManager.getLogger(FullNode.class);
	protected Server server;
	protected final Set<NodeEntry> network;
	protected final Chain blockChain;
	protected Block newBlock; //add this
	private static PublicKey NODE_WALLET;
	private static PrivateKey NODE_PK;
	static {
		try {
			NODE_WALLET = KeyFileLoader.getPublic("node_wallet.der");
			NODE_PK = KeyFileLoader.getPrivate("node_pk.der");
		} catch (Exception e) {
			e.printStackTrace();
			NODE_WALLET = null;
			NODE_PK = null;
		}
	}

	public FullNode() {
		this.server = new Server(this);
		this.network = new HashSet<>();
		this.blockChain = new Chain(); //TODO: load blockchain from storage
		this.newBlock = null; //only makes sense to set when blockChain is most recent
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
	public boolean validateBlock() { //if validator is chosen by the system they can add a block and claim rewards
		if (blockChain.permittedToValidateNewBlock(NODE_WALLET) && blockChain.tryAddBlockSync(newBlock)) {
			server.sendToAll(new CreatedBlockMessage(newBlock));
			return true;
		}
		return false;
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
	public Server getServer() {
		return server;
	}

	@Override
	public Block getNewBlock() {
		return newBlock;
	}

	@Override
	public void setNewBlock(Block block) {
		this.newBlock = block;
	}
}
