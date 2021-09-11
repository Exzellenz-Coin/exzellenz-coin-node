package mainpackage.blockchain.transaction;

import mainpackage.util.KeyHelper;

import java.math.BigDecimal;
import java.security.PublicKey;

public class StakingTransaction extends Transaction {
    public static PublicKey STAKING_WALLET; //locked wallet
    static {
        try {
            STAKING_WALLET = KeyHelper.loadPublicKey("staking_wallet.der");
        } catch (Exception e) {
            e.printStackTrace();
            STAKING_WALLET = null;
        }
    }

    public StakingTransaction(PublicKey sourceWalletId, BigDecimal amount, BigDecimal tip, byte[] signature) {
        super(sourceWalletId, STAKING_WALLET, amount, tip, signature);
    }
}
