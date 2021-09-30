package mainpackage.server;

import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.ConnectMessage;
import mainpackage.server.message.network.JoinNetworkMessage;
import mainpackage.server.message.network.RequestNetworkMessage;
import mainpackage.server.node.INode;
import mainpackage.server.node.NodeEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The server class represents the network connection of a node.
 * It handles all new incoming connections from other nodes.
 * It also subsequently handles all Peers that are created from these connections.
 */
public class Server extends Thread {
    protected static final Logger logger = LogManager.getLogger(Server.class);
    // The default port of the ServerSocket
    protected static final int DEFAULT_PORT = 1337;
    // The maximum size of the message cache
    protected static final int MAX_MSG_CACHE_SIZE = 100;
    // A reference to the node that this Server belongs to
    protected final INode node;
    // The actual port of the ServerSocket
    protected final int port;
    // The list of active peers
    protected List<Peer> peers;
    // The cache of already received messages
    protected MessageCache receivedMessages;
    // Whether this Server is or should shut down
    protected boolean shutdown;

    public Server(INode node) {
        this(DEFAULT_PORT, node);
    }

    public Server(int port, INode node) {
        super("Server");
        this.node = node;
        this.port = port;
        this.peers = new ArrayList<>();
        this.receivedMessages = new MessageCache(MAX_MSG_CACHE_SIZE);
        this.shutdown = false;
    }

    /**
     * Creates an outgoing connection to another node.
     * After establishing a connection the remote node a {@link ConnectMessage} is sent.
     * This is necessary so that the remote node knows the NodeEntry of the local node.
     *
     * @param hostName The hostName of the remote node
     * @param port     The port of the remote node
     * @throws IOException If there was a network error
     */
    public void connectToPeer(String hostName, int port) throws IOException {
        logger.info("Connecting to: %s:%s".formatted(hostName, port));
        var peer = createPeer(new Socket(hostName, port), new NodeEntry(hostName, port));
        peer.send(new ConnectMessage(node.getNodeEntry()));
    }

    /**
     * Handles the initial connection to the node network after the local node started operating.
     * This will send a {@link JoinNetworkMessage} and {@link RequestNetworkMessage}
     *
     * @throws IOException If there was a network error
     */
    public void doInitialConnect() throws IOException {
        if (peers.size() == 0) return;
        logger.debug("Executing initial connection setup");
        var peer = peers.get(0);
        peer.send(new JoinNetworkMessage(node.getNodeEntry()));
        peer.send(new RequestNetworkMessage());
    }

    /**
     * Shuts this server down
     */
    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    /**
     * Sends a message to all connected nodes.
     *
     * @param message The message to send
     */
    public void sendToAll(AbstractMessage message) {
        cacheReceivedMessage(message.getId());
        logger.debug("Sending message to %d peers".formatted(peers.size()));
        for (Peer peer : peers) {
            try {
                peer.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes a peer.
     * Should be called when a peer is closed.
     *
     * @param peer The peer to remove
     */
    public void remove(Peer peer) {
        logger.debug("Closing Connection");
        peers.remove(peer);
    }

    /**
     * Cache the UUID of a message that was received by this node.
     *
     * @param id the UUID of the message
     */
    public void cacheReceivedMessage(UUID id) {
        receivedMessages.add(id);
    }

    /**
     * Checks if a message with the given UUID was already received by this node.
     *
     * @param id The UUID of the message
     * @return true if the message was already received
     */
    public boolean hasReceivedMessage(UUID id) {
        return receivedMessages.contains(id);
    }

    @Override
    public void run() {
        logger.info("Starting node...");
        try (var serverSocket = new ServerSocket(port)) {
            while (!shutdown) {
                try {
                    var socket = serverSocket.accept();
                    logger.info("Connection from: %s:%s".formatted(socket.getInetAddress(), socket.getPort()));
                    createPeer(socket, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Creates a Peer instance for a connection.
     *
     * @param socket    The socket
     * @param nodeEntry The NodeEntry of the remote node
     * @return The created Peer instance
     * @throws IOException If there was a network error
     */
    protected Peer createPeer(Socket socket, NodeEntry nodeEntry) throws IOException {
        var peer = new Peer(socket, this);
        peer.setNodeEntry(nodeEntry);
        peer.start();
        peers.add(peer);
        return peer;
    }

    public INode getNode() {
        return node;
    }

    public int getPort() {
        return port;
    }
}
