package mainpackage.blockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

public class TransactionTest {
    @Test
    @DisplayName("Load Staking PK Test")
    public void testStakingWallet() {
        Transaction st = new StakingTransaction(null,null,null,null);
        Assertions.assertNotNull(StakingTransaction.STAKING_WALLET);
    }
}
