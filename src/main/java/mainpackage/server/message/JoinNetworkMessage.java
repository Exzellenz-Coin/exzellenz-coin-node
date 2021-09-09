package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;

public class JoinNetworkMessage extends AbstractMessage {
    @JsonProperty
    private NodeEntry nodeEntry;

    private JoinNetworkMessage() {
    }

    public JoinNetworkMessage(NodeEntry nodeEntry) {
        this.nodeEntry = nodeEntry;
    }

    @Override
    public void handle(Peer sender) {
        sender.getNode().addNodeEntry(nodeEntry);
    }

    @Override
    public boolean shouldRelay() {
        return true;
    }
}
