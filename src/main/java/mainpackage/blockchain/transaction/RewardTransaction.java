package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class RewardTransaction extends Transaction {
    public static String ID = "RESTAKE";
    public RewardTransaction(PublicKey targetWalletID, BigDecimal amount, byte[] signature) {
        super(null, targetWalletID, amount, BigDecimal.ZERO, ID, signature);
    }
}
