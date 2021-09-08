package server.node;

import blockchain.Block;
import server.Server;

public interface INode {
	void start();

	void stop();

	void mineBlock(Block block);

	Server getServer();

	boolean shouldRelayMessages();
}
