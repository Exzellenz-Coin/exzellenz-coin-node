package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionTest {
    @Test
    @DisplayName("Load Staking PK Test")
    public void testStakingWallet() {
        Transaction st = new StakingTransaction(null,null,null,null);
        Assertions.assertNotNull(StakingTransaction.STAKING_WALLET);
    }
}
