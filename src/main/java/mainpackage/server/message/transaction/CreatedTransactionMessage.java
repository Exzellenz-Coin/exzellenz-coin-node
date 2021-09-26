package mainpackage.server.message.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

import java.security.PrivateKey;

public class CreatedTransactionMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private Transaction transaction;

    private CreatedTransactionMessage() {
    }

    public CreatedTransactionMessage(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void handle(Peer sender) {
        if (sender.getNode().addTransaction(transaction)) {
            this.shouldRelay = true;
        }
    }

    @Override
    public boolean shouldRelay() {
        return shouldRelay;
    }
}
