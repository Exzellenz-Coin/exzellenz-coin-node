package mainpackage.blockchain.transaction;

import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

public class RestakingTransaction extends Transaction {
    public static String ID = "RESTAKE";
    public static PublicKey STAKING_WALLET; //locked wallet

    static {
        try {
            STAKING_WALLET = KeyHelper.loadPublicKey("staking_wallet.der");
        } catch (Exception e) {
            e.printStackTrace();
            STAKING_WALLET = null;
        }
    }

    //"activates" someone's stake for the next epoch. can also add more stake
    public RestakingTransaction(PublicKey sourceWalletId, BigDecimal amount, BigDecimal tip) {
        super(sourceWalletId, STAKING_WALLET, amount, tip, ID);
    }

    public static List<Pair<PublicKey, byte[]>> parseDataToObject(String[] data) { //the data of a transaction minus the first parameter
        return StakingTransaction.parseDataToObject(data);
    }
}
