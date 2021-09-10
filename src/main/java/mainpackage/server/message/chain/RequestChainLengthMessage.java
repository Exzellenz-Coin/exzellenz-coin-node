package mainpackage.server.message.chain;

import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.io.IOException;

public class RequestChainLengthMessage extends AbstractMessage {
    @Override
    public void handle(Peer sender) {
        try {
            sender.send(new SendChainLengthMessage(sender.getNode().getBlockChain().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
