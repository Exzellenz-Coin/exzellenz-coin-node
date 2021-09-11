package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class CoinbaseTransaction extends Transaction {
    public CoinbaseTransaction(PublicKey targetWalletID, BigDecimal amount, BigDecimal tip, byte[] signature) {
        super(null, targetWalletID, amount, tip, null);
    }
}
