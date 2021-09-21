package mainpackage.blockchain.transaction;

import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

public class UnstakingTransaction extends Transaction {
    public static String ID = "UNSTAKE";
    public UnstakingTransaction(PublicKey targetWalletID, BigDecimal amount, BigDecimal tip, byte[] signature) {
        super(null, targetWalletID, amount, tip, ID, signature);
    }

    public static List<Pair<PublicKey, byte[]>> parseDataToObject(String[] data) { //the data of a transaction minus the first parameter
        return null;
    }
}
