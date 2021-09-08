package mainpackage.server;

import mainpackage.server.node.FullNode;
import mainpackage.server.node.NodeEntry;

public class TestNode extends FullNode {

    public TestNode(int port) {
        super();
        this.server = new TestServer(port, this);
    }

    @Override
    public NodeEntry getNodeEntry() {
        return new NodeEntry("localhost", getServer().port);
    }

}
