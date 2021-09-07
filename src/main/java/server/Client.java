package server;

import server.message.HelloWorldMessage;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Peer peer;

    public Client(String hostName, int port) throws IOException {
        Socket socket = new Socket(hostName, port);
        this.peer = new Peer(socket, null);
        peer.send(new HelloWorldMessage("Client"));
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 1337);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
