package server;

import server.message.AbstractMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    protected static final int DEFAULT_PORT = 1337;
    protected static final int MAX_MSG_CACHE_SIZE = 100;
    protected List<Peer> peers;
    protected List<UUID> receivedMessages;
    protected final int port;
    protected boolean shutdown;
    protected boolean relayMessages;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        this.peers = new ArrayList<>();
        this.receivedMessages = new LinkedList<>();
        this.shutdown = false;
        this.relayMessages = true;
    }

    public void connectToPeer(String hostName, int port) throws IOException {
        System.out.println("Connection to: " + hostName + ":" + port);
        Socket socket = new Socket(hostName, port);
        Peer peer = new Peer(socket, this);
        peer.start();
        peers.add(peer);
    }

    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    public void sendToAll(AbstractMessage message) {
        cacheReceivedMessage(message.getId());
        System.out.println("Sending message to " + peers.size() + " peers");
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
        if (hasReceivedMessage(id))
            return;
        receivedMessages.add(id);
        if (receivedMessages.size() > MAX_MSG_CACHE_SIZE)
            receivedMessages.remove(0);
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
                    Socket newSocket = serverSocket.accept();
                    System.out.println("Connection from: " + newSocket.getInetAddress() + ":" + newSocket.getPort());
                    Peer peer = new Peer(newSocket, this);
                    peer.start();
                    peers.add(peer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            Thread thread = new Thread(server);
            thread.start();
            Thread.sleep(60 * 1000);
            server.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean shouldRelayMessages() {
        return relayMessages;
    }

    public void setRelayMessages(boolean relayMessages) {
        this.relayMessages = relayMessages;
    }
}
