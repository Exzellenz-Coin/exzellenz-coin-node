package server.node;

import server.Client;
import server.Server;

import java.io.IOException;

public class FullNode implements INode {
    private Client client;
    private Server server;

    public FullNode(String hostName, int port) throws IOException {
        this.client = new Client(hostName, port);
        this.server = new Server();
    }
}
