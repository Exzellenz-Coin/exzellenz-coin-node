package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.Block;
import mainpackage.server.Peer;

import java.io.IOException;

public class CreatedBlockMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private Block block;

    private CreatedBlockMessage() {
    }

    public CreatedBlockMessage(Block block) {
        this.block = block;
    }

    @Override
    public void handle(Peer sender) {
        if (sender.getNode().getBlockChain().isValidBlock(this.block)) {
            sender.getNode().getBlockChain().addBlock(this.block);
            try {
                this.shouldRelay = true;
                sender.send(new CreatedBlockMessage(this.block)); //forward message if valid
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean shouldRelay() {
        return this.shouldRelay;
    }
}
