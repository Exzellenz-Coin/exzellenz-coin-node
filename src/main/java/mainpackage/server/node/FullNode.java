package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.server.Server;

import java.util.ArrayList;
import java.util.List;

public class FullNode implements INode {
	protected Server server;
	protected final List<NodeEntry> network;
	protected final Chain blockChain;

	public FullNode() {
		this.server = new Server(this);
		this.network = new ArrayList<>();
		this.blockChain = null; //TODO: load blockchain from storage
		//TODO: request peer blockchain length, and update if current blockchain is smaller
	}

	@Override
	public void start() {
		server.start();
		network.add(getNodeEntry());
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

	public boolean shouldRelayMessages() {
		return true;
	}

	@Override
	public List<NodeEntry> getNetwork() {
		return network;
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
