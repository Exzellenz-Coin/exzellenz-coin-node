package mainpackage.server.message.network;

import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.io.IOException;

public class RequestNetworkMessage extends AbstractMessage {
    @Override
    public void handle(Peer sender) {
        try {
            sender.send(new SendNetworkMessage(sender.getNode().getNetwork()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldRelay() {
        return false;
    }
}
