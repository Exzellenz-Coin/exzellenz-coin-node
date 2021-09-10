package mainpackage.server.message.block;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.io.IOException;

public class RequestBlockMessage extends AbstractMessage {
    @JsonProperty
    private int blockNumber;

    private RequestBlockMessage() {
    }

    public RequestBlockMessage(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    @Override
    public void handle(Peer sender) {
        try {
            sender.send(new SendBlockMessage(sender.getNode().getBlockChain().get(blockNumber)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
