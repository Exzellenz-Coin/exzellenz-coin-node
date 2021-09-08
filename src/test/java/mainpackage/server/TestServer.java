package mainpackage.server;

import java.util.List;

import mainpackage.server.node.INode;

public class TestServer extends Server {
    public TestServer(int port, INode node) {
        super(port, node);
    }

    public List<Peer> getPeers() {
        return peers;
    }
}
