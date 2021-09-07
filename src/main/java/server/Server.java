package server;

import server.message.IMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private static final int PORT = 1337;
    private List<Peer> peers;
    private boolean shutdown;

    public Server() throws IOException {
        this.peers = new ArrayList<>();
    }

    public void connectToPeer(String hostName, int port) throws IOException {
        System.out.println("Connection to: " + hostName + ":" + port);
        Socket socket = new Socket(hostName, port);
        Peer peer = new Peer(socket, this);
        peers.add(peer);
    }

    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    public void sendToAll(IMessage message) {
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

    @Override
    public void run() {
        System.out.println("Starting node...");
        try (var serverSocket = new ServerSocket(PORT)) {
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
