package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.server.Server;

import java.util.Set;

/**
 * The base interface for all nodes.
 */
public interface INode {
	/**
	 * Startup method for a node.
	 * Any logic that should happen everytime a node starts should be placed here.
	 * This should include starting the server thread.
	 */
	void start();

	/**
	 * Shutdown method for a node.
	 * Any logic that should happen everytime a node stops should be placed here.
	 * This should include stopping the server thread.
	 */
	void stop();

	/**
	 * Defines the logic for mining a block.
	 * TODO: Is this needed?
	 *
	 * @param block The block to mine
	 */
	void mineBlock(Block block);

	/**
	 * Method to get the server that is responsible for connecting to the node network.
	 *
	 * @return The server instance of this node
	 */
	Server getServer();

	/**
	 * Whether this node should relay messages to other nodes.
	 *
	 * @return true if messages should be relayed
	 */
	boolean shouldRelayMessages();

	/**
	 * Gets the set of NodeEntries representing all nodes in the network.
	 *
	 * @return Set of NodeEntries
	 */
	Set<NodeEntry> getNetwork();

	Chain getBlockChain();

	/**
	 * Resets the set of NodeEntries to only include the NodeEntry of this node.
	 */
	void resetNetwork();

	/**
	 * Adds a NodeEntry to the network set.
	 * Will not do anything if the NodeEntry was already added.
	 *
	 * @param nodeEntry The NodeEntry to add
	 * @return Whether the addition was successful
	 */
	boolean addNodeEntry(NodeEntry nodeEntry);

	/**
	 * Removes a NodeEntry to the network set.
	 * Will not do anything if the NodeEntry is not present.
	 *
	 * @param nodeEntry The NodeEntry to remove
	 * @return Whether the removal was successful
	 */
	boolean removeNodeEntry(NodeEntry nodeEntry);

	/**
	 * Get the NodeEntry of this node.
	 * The NodeEntry of a node should only change to adapt to a changed IP address or something similar.
	 *
	 * @return The NodeEntry of this node
	 */
	NodeEntry getNodeEntry();
}
