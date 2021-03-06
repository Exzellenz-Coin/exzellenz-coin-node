package mainpackage.blockchain.transaction;

import mainpackage.blockchain.staking.StakeKeys;
import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class StakingTransaction extends Transaction {
    public static String ID = "STAKE";
    public static PublicKey STAKING_WALLET; //locked wallet

    static {
        try {
            STAKING_WALLET = KeyHelper.loadPublicKey("staking_wallet.der");
        } catch (Exception e) {
            e.printStackTrace();
            STAKING_WALLET = null;
        }
    }

    public StakingTransaction(PublicKey sourceWalletId, BigDecimal amount, BigDecimal tip, StakeKeys keys) {
        super(sourceWalletId, STAKING_WALLET, amount, tip, ID + keys.toString());
    }

    public static List<Pair<PublicKey, byte[]>> parseDataToObject(String[] data) { //the data of a transaction minus the first parameter
        try {
            List<Pair<PublicKey, byte[]>> result = new ArrayList<>();
            for (int i = 1; i < data.length - 1; i += 2) {
                result.add(new Pair(KeyHelper.publicKeyFromString(data[i]), Base64.getDecoder().decode(data[i + 1])));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; //invalid
    }
}