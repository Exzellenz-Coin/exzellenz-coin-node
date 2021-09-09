package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;

public class LeaveNetworkMessage extends AbstractMessage {
    @JsonProperty
    private NodeEntry nodeEntry;

    private LeaveNetworkMessage() {
    }

    public LeaveNetworkMessage(NodeEntry nodeEntry) {
        this.nodeEntry = nodeEntry;
    }

    @Override
    public void handle(Peer sender) {
        sender.getNode().removeNodeEntry(nodeEntry);
    }

    @Override
    public boolean shouldRelay() {
        return true;
    }
}
