package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class UnstakingTransaction extends Transaction {
    public UnstakingTransaction(PublicKey targetWalletID, BigDecimal amount, BigDecimal tip, byte[] signature) {
        super(null, targetWalletID, amount, tip, "UNSTAKE", signature);
    }
}
