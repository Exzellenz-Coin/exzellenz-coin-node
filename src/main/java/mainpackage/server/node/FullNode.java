package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.server.Server;

public class FullNode implements INode {
	private final Server server;

	public FullNode() {
		this.server = new Server(this);
	}

	@Override
	public void start() {
		server.start();
	}

	@Override
	public void stop() {
		server.shutdown();
	}

	@Override
	public void mineBlock(Block block) {
		// TODO
	}

	public boolean shouldRelayMessages() {
		return true;
	}

	@Override
	public Server getServer() {
		return server;
	}
}
