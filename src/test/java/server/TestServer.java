package server;

import java.util.List;

import server.node.INode;

public class TestServer extends Server {
    public TestServer(int port, INode node) {
        super(port, node);
    }

    public List<Peer> getPeers() {
        return peers;
    }
}
