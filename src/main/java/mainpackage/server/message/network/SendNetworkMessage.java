package mainpackage.server.message.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;
import mainpackage.server.node.NodeEntry;

import java.util.Set;

public class SendNetworkMessage extends AbstractMessage {
    @JsonProperty
    private Set<NodeEntry> network;

    private SendNetworkMessage() {
    }

    public SendNetworkMessage(Set<NodeEntry> network) {
        this.network = network;
    }

    @Override
    public void handle(Peer sender) {
        sender.getNode().resetNetwork();
        network.forEach(sender.getNode()::addNodeEntry);
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
