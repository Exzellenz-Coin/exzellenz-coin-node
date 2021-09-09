package mainpackage.server;

import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.ConnectMessage;
import mainpackage.server.message.JoinNetworkMessage;
import mainpackage.server.message.RequestNetworkMessage;
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

public class Server extends Thread {
    protected static final Logger logger = LogManager.getLogger(Server.class);
    protected static final int DEFAULT_PORT = 1337;
    protected static final int MAX_MSG_CACHE_SIZE = 100;
    protected final INode node;
    protected final int port;
    protected List<Peer> peers;
    protected MessageCache receivedMessages;
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

    public void connectToPeer(String hostName, int port) throws IOException {
        logger.info("Connecting to: %s:%s".formatted(hostName, port));
        var peer = createPeer(new Socket(hostName, port), new NodeEntry(hostName, port));
        peer.send(new ConnectMessage(node.getNodeEntry()));
    }

    public void doInitialConnect() throws IOException {
        if (peers.size() == 0) return;
        logger.debug("Executing initial connection setup");
        var peer = peers.get(0);
        peer.send(new JoinNetworkMessage(node.getNodeEntry()));
        peer.send(new RequestNetworkMessage());
    }

    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

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

    public void remove(Peer peer) {
        logger.debug("Closing Connection");
        peers.remove(peer);
    }

    public void cacheReceivedMessage(UUID id) {
        receivedMessages.add(id);
    }

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
