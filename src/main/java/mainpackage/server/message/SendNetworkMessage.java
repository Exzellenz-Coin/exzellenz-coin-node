package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;

import java.util.List;

public class SendNetworkMessage extends AbstractMessage {
    @JsonProperty
    private List<NodeEntry> network;

    private SendNetworkMessage() {
    }

    public SendNetworkMessage(List<NodeEntry> network) {
        this.network = network;
    }

    @Override
    public void handle(Peer sender) {
        sender.getNode().getNetwork().clear();
        sender.getNode().getNetwork().addAll(network);
        System.out.println("Added " + network);
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
