package mainpackage.server.message.block;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.Block;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.chain.SendChainLengthMessage;

import java.io.IOException;

public class SendBlockMessage extends AbstractMessage {
    @JsonProperty
    private Block block;

    private SendBlockMessage() {
    }

    public SendBlockMessage(Block block) {
        this.block = block;
    }

    @Override
    public void handle(Peer sender) {
        //attempt add block
        if (sender.getNode().getBlockChain().tryAddBlockSync(this.block)) {
            try {
                sender.send(new SendChainLengthMessage(sender.getNode().getBlockChain().size())); //requests more blocks if needed
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
