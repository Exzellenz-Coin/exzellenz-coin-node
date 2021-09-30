package mainpackage.server.message.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.blockchain.transaction.UnstakingTransaction;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

public class CreatedUnstakingTransactionMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private UnstakingTransaction transaction;

    private CreatedUnstakingTransactionMessage() {
    }

    public CreatedUnstakingTransactionMessage(UnstakingTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void handle(Peer sender) {
        //tickets correct
        try {
            String[] rawData = transaction.getData().split(Transaction.DATA_SPLIT_REGEX);
            //TODO: check to see if the original staking transaction existed and has not been unstaked before
            //currently accepts ANY unstaking attempt
            if (!rawData[0].equals(UnstakingTransaction.ID))
                return;
        } catch (Exception e) {
            return;
        }

        if (sender.getNode().addTransaction(transaction)) {
            this.shouldRelay = true;
        }
    }

    @Override
    public boolean shouldRelay() {
        return shouldRelay;
    }
}
