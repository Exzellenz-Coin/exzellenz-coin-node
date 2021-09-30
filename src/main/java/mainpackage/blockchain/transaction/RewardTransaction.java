package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class RewardTransaction extends Transaction {
    public static String ID = "REWARD"; //block reward

    public RewardTransaction(PublicKey targetWalletID, BigDecimal amount, byte[] signature) {
        super(null, targetWalletID, amount, BigDecimal.ZERO, ID, signature);
    }
}
