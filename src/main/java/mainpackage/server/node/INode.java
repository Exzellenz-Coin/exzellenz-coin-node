package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.server.Server;

import java.util.Set;

public interface INode {
	void start();

	void stop();

	void mineBlock(Block block);

	Server getServer();

	boolean shouldRelayMessages();

	Set<NodeEntry> getNetwork();

	void resetNetwork();

	boolean addNodeEntry(NodeEntry nodeEntry);

	boolean removeNodeEntry(NodeEntry nodeEntry);

	NodeEntry getNodeEntry();
}
