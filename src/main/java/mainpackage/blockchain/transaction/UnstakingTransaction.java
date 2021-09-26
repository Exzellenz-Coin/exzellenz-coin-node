package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class UnstakingTransaction extends Transaction {
    public static String ID = "UNSTAKE";
    public UnstakingTransaction(PublicKey targetWalletID, BigDecimal amount, BigDecimal tip) {
        super(null, targetWalletID, amount, tip, ID);
    }
}
