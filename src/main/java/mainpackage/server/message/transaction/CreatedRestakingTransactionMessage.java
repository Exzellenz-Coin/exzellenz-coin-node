package mainpackage.server.message.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.transaction.RestakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

public class CreatedRestakingTransactionMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private RestakingTransaction transaction;

    private CreatedRestakingTransactionMessage() {
    }

    public CreatedRestakingTransactionMessage(RestakingTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void handle(Peer sender) {
        //tickets correct
        try {
            String[] rawData = transaction.getData().split(Transaction.DATA_SPLIT_REGEX);
            //TODO: check to see if the original staking transaction existed, is invalid and has not been unstaked
            //currently accepts ANY restaking attempt
            if (!rawData[0].equals(RestakingTransaction.ID))
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
