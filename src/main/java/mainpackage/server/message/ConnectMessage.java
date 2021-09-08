package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;

public class ConnectMessage extends AbstractMessage {
    @JsonProperty
    private NodeEntry nodeEntry;

    private ConnectMessage() {

    }

    public ConnectMessage(NodeEntry nodeEntry) {
        this.nodeEntry = nodeEntry;
    }

    @Override
    public void handle(Peer sender) {
        sender.setNodeEntry(nodeEntry);
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
