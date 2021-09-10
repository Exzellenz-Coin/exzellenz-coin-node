package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	public FullNode() {
		this.server = new Server(this);
		this.network = new HashSet<>();
		this.blockChain = null; //TODO: load blockchain from storage
		//TODO: request peer blockchain length, and update if current blockchain is smaller
	}

	@Override
	public void start() {
		server.start();
		resetNetwork();
	}

	@Override
	public void stop() {
		server.shutdown();
	}

	@Override
	public void mineBlock(Block block) {
		if (true) { //TODO: only the current agreed upon highest staker can call this
			blockChain.addBlock(block);
		}
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
}
