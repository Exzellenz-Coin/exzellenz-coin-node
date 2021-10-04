package mainpackage.server.message.chain;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.block.RequestBlockMessage;

import java.io.IOException;

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
        int curBlockChainSize = (int)sender.getNode().getBlockChain().size();
        if (this.chainLength > curBlockChainSize) {
            try {
                sender.send(new RequestBlockMessage(curBlockChainSize)); //request new block from the longer chain
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
