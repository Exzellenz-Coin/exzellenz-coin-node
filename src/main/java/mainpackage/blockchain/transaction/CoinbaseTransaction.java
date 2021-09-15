package mainpackage.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

public class CoinbaseTransaction extends Transaction {
    public CoinbaseTransaction(PublicKey targetWalletID, BigDecimal amount, BigDecimal tip, String data) {
        super(null, targetWalletID, amount, tip, data);
    }
}
