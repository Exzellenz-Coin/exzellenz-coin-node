package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.server.Server;

public interface INode {
	void start();

	void stop();

	void mineBlock(Block block);

	Server getServer();

	boolean shouldRelayMessages();
}
