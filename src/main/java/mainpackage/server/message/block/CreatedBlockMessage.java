package mainpackage.server.message.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.Block;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;

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
        if (sender.getNode().getBlockChain().isValidBlock(this.block, true)) {
            sender.getNode().getBlockChain().addBlock(this.block);
            this.shouldRelay = true;
            //sender.send(new CreatedBlockMessage(this.block)); //forward message if valid
        }
    }

    @Override
    public boolean shouldRelay() {
        return this.shouldRelay;
    }
}
