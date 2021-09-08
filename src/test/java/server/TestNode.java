package server;

import java.io.IOException;

import blockchain.Block;
import server.node.INode;

public class TestNode implements INode {
	private final TestServer server;

	public TestNode(int port) {
		this.server = new TestServer(port, this);
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
		//return;
	}

	public boolean shouldRelayMessages() {
		return true;
	}

	@Override
	public TestServer getServer() {
		return server;
	}
}
