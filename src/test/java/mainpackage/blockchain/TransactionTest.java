package mainpackage.blockchain;

import mainpackage.blockchain.staking.StakeKeys;
import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class TransactionTest {
    @Test
    @DisplayName("Sign and verify Test")
    public void testSignVerify() throws Exception {
        //System.out.println(Hash.applySha256("e5eee98f6861721f1bcf97b99651752faf3d01c5106b47ffab5cb4dcd7908dbb" + "cad681f2208842c6c62765d8def6643e959d4f245205e8c9ac16b9858529f91c"));
        //System.out.println("0473ab7d2c54225f70bc193320cfc27b9e0208c33382319eb893f052a7457be9");
        PublicKey founderPublic = KeyHelper.loadPublicKey("founder_wallet.der");
        PrivateKey founderPrivate = KeyHelper.loadPrivateKey("founder_pk.der");
        Transaction t1 = new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.ONE, BigDecimal.valueOf(0.1), "");
        t1.sign(founderPrivate);
        Assertions.assertTrue(t1.verifySignature(founderPublic));
    }

    @Test
    @DisplayName("Staking transaction parsing test")
    public void testStakingParse() throws Exception {
        StakeKeys keys = new StakeKeys();
        keys.generateFull(50);
        System.out.println(Base64.getEncoder().encodeToString(keys.getPublicPairs().get(0).one().getEncoded()));
        StakingTransaction transaction = new StakingTransaction(null, BigDecimal.TEN, BigDecimal.ONE, keys);
        var parsedKeys = StakingTransaction.parseDataToObject(transaction.getData().split(Transaction.DATA_SPLIT_REGEX));
        /*
        System.out.println(keys.getPublicPairs().get(0).one());
        System.out.println(parsedKeys.get(0).one());
        System.out.println(Arrays.toString(keys.getPublicPairs().get(0).two()));
        System.out.println(Arrays.toString(parsedKeys.get(0).two()));
        */
        Assertions.assertEquals(parsedKeys, keys.getPublicPairs());
    }

}
