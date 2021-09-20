package mainpackage.blockchain;

import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SignatureException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {
    @Test
    @DisplayName("Time Test")
    public void testTime() {
        KeyPair w1 = KeyHelper.generateKeyPair();
        KeyPair w2 = KeyHelper.generateKeyPair();
        Transaction transaction1 = new Transaction(w1.getPublic(), w2.getPublic(), BigDecimal.ONE, BigDecimal.ZERO, "");
        long timeBefore = System.currentTimeMillis();
        Block block = new Block( "", 0, Collections.singletonList(transaction1), null);
        long timeAfter = System.currentTimeMillis();
        Assertions.assertTrue(timeBefore <= block.getTimeStamp(),
                "Time in the past was set to the block!");
        Assertions.assertTrue(block.getTimeStamp() <= timeAfter,
                "Time in the future was set to the block!");
    }

    @Test
    @DisplayName("Signature Test")
    public void signatureTest() throws SignatureException, InvalidKeyException {
        var block = createBlock(new Chain().getHead().getHash(), 0, KeyHelper.generateKeyPair(), "1");
        assertNotNull(block.getSignature(), "Signature is null");
        assertNotEquals(0, block.getSignature().length, "Signature array is empty");
    }

    @Test
    @DisplayName("Hashing Test")
    public void hashTest() throws InvalidKeyException, SignatureException {
        var chain = new Chain();
        var keyPair = KeyHelper.generateKeyPair();
        var block1 = createBlock(chain.getHead().getHash(), 0, keyPair, "123.456789");
        var block3 = createBlock(chain.getHead().getHash(), 1, keyPair, "1");

        assertNotEquals(block1.getHash(), block3.getHash(), "Hash collision: hash function broken");
        assertEquals(64, block1.getHash().length(), "Hash has an incorrect length");
        assertEquals(64, block3.getHash().length(), "Hash has an incorrect length");
    }

    private Block createBlock(String headHash, long blockNumber, KeyPair keyPair, String amount) throws SignatureException, InvalidKeyException {
        var transaction = new Transaction(
                Chain.FOUNDER_WALLET,
                keyPair.getPublic(),
                new BigDecimal(amount),
                BigDecimal.ZERO,
                ""
        );
        var block = new Block(
                headHash,
                blockNumber,
                Collections.singletonList(transaction),
                keyPair.getPublic()
        );
        transaction.sign(keyPair.getPrivate());
        block.sign(keyPair.getPrivate());
        block.createHash();
        return block;
    }
}
