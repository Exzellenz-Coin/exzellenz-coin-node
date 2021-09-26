package mainpackage.server.message.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SignatureException;

public class CreatedBlockMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private Block block;
    @JsonProperty
    private PrivateKey privateKey;

    private CreatedBlockMessage() {
    }

    public CreatedBlockMessage(Block block, PrivateKey privateKey) {
        this.block = block;
        this.privateKey = privateKey;
    }

    @Override
    public void handle(Peer sender) {
        try {
            //check if the private key "ticket" is not invalid
            if (this.block.getBlockNumber() >= Chain.EPOCH //no checks for the first epoch
                    && !sender.getNode().getBlockChain().tryAddPrivateValidatorKeySync(block.getValidator(), privateKey)) //remember private key
                return;
            //if it is, attempt add
            if (sender.getNode().getBlockChain().isValidNewBlock(this.block)) {
                sender.getNode().getBlockChain().addBlock(this.block);
                this.shouldRelay = true;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean shouldRelay() {
        return this.shouldRelay;
    }
}
