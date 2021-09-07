package server;

import java.util.List;

public class TestServer extends Server {
    public TestServer(int port) {
        super(port);
    }

    public List<Peer> getPeers() {
        return peers;
    }
}
