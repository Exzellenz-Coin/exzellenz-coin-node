package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.server.Server;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
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
     * Attempts to validate the NewBlock
     *
     * @param force If despite not being the validator we still want to attempt andvalidate
     * @return If addition and adding was successful
     */
    boolean validateBlock(boolean force);

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

    /**
     * Get the Chain of this node.
     *
     * @return The Chain of this node
     */
    Chain getBlockChain();

    /**
     * Get the currently worked on Block of this node.
     *
     * @return The unofficial Block of this node
     */
    Block getNewBlock();

    /**
     * Set the currently worked on Block of this node.
     *
     * @param block To set
     */
    void setNewBlock(Block block);

    /**
     * Setup the node
     * Updates the node with most recent information from the network
     */
    void update();

    /**
     * Add a Transaction to list of unpublished transactions
     *
     * @param transaction The Transaction to add
     * @return If the transaction is formatted correctly and was added
     */
    boolean addTransaction(Transaction transaction);

    /**
     * Finalize the newBlock for publication
     */
    void finalizeBlock() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException;
}
