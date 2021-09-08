package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.server.Server;

import java.util.List;

public interface INode {
	void start();

	void stop();

	void mineBlock(Block block);

	Server getServer();

	boolean shouldRelayMessages();

	List<NodeEntry> getNetwork();

	NodeEntry getNodeEntry();
}
