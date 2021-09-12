package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TransactionTest {
    @Test
    @DisplayName("Sign and verify Test")
    public void testSignVerify() throws Exception {
        PublicKey founderPublic = KeyHelper.loadPublicKey("founder_wallet.der");
        PrivateKey founderPrivate = KeyHelper.loadPrivateKey("founder_pk.der");
        Transaction t1 = new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.ONE, BigDecimal.valueOf(0.1));
        t1.sign(founderPrivate);
        Assertions.assertTrue(t1.verifySignature(founderPublic));
    }
}
