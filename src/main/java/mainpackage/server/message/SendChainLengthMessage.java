package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;

import java.io.IOException;
import java.util.Set;

public class SendChainLengthMessage extends AbstractMessage {
    @JsonProperty
    private int chainLength;

    private SendChainLengthMessage() {
    }

    public SendChainLengthMessage(int chainLength) {
        this.chainLength = chainLength;
    }

    @Override
    public void handle(Peer sender) {
        int curBlockChainSize = sender.getNode().getBlockChain().size();
        if (this.chainLength > curBlockChainSize) {
            try {
                sender.send(new RequestBlockMessage(curBlockChainSize + 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
