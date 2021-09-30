package mainpackage.server.message.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.server.Peer;
import mainpackage.server.message.AbstractMessage;

public class CreatedStakingTransactionMessage extends AbstractMessage {
    @JsonIgnore
    private boolean shouldRelay = false;
    @JsonProperty
    private StakingTransaction transaction;

    private CreatedStakingTransactionMessage() {
    }

    public CreatedStakingTransactionMessage(StakingTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void handle(Peer sender) {
        try {
            String[] rawData = transaction.getData().split(Transaction.DATA_SPLIT_REGEX);
            if (!rawData[0].equals(StakingTransaction.ID)
                    || sender.getNode().getBlockChain().calculateNumberStakeKeys(transaction.getAmount()) != StakingTransaction.parseDataToObject(rawData).size()) //wrong number of tickets
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
