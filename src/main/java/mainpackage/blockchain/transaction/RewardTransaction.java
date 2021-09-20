package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class RewardTransaction extends Transaction {
    public RewardTransaction(PublicKey targetWalletID, BigDecimal amount, byte[] signature) {
        super(null, targetWalletID, amount, BigDecimal.ZERO, "REWARD", signature);
    }
}
