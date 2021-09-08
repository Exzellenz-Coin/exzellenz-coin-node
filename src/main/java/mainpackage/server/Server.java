package mainpackage.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.MessageCache;
import mainpackage.server.node.INode;

public class Server extends Thread {
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
    	this.node = node;
        this.port = port;
        this.peers = new ArrayList<>();
        this.receivedMessages = new MessageCache(MAX_MSG_CACHE_SIZE);
        this.shutdown = false;
    }

	public void connectToPeer(String hostName, int port) throws IOException {
        System.out.printf("Connecting to: %s:%s%n", hostName, port);
        createPeer(new Socket(hostName, port));
    }

    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    public void sendToAll(AbstractMessage message) {
        cacheReceivedMessage(message.getId());
        System.out.printf("Sending message to %d peers%n", peers.size());
        for (Peer peer : peers) {
            try {
                peer.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(Peer peer) {
        System.out.println("Closing Connection");
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
        System.out.println("Starting node...");
        try (var serverSocket = new ServerSocket(port)) {
            while (!shutdown) {
                try {
                    var socket = serverSocket.accept();
                    System.out.printf("Connection from: %s:%s%n", socket.getInetAddress(), socket.getPort());
                    createPeer(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createPeer(Socket socket) throws IOException {
        var peer = new Peer(socket, this);
        peer.start();
        peers.add(peer);
    }

    public INode getNode() {
    	return node;
	}

}
