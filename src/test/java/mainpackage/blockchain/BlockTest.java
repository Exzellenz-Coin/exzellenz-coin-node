package mainpackage.blockchain;

import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.Collections;

public class BlockTest {
	@Test
	@DisplayName("Time Test")
	public void testTime() {
		KeyPair w1 = KeyHelper.generateKeyPair();
		KeyPair w2 = KeyHelper.generateKeyPair();
		Transaction transaction1 = new Transaction(w1.getPublic(), w2.getPublic(), BigDecimal.ONE, BigDecimal.ZERO, new byte[0]);
		long timeBefore = System.currentTimeMillis();
		Block block = new Block("", Collections.singletonList(transaction1), null, null);
		long timeAfter = System.currentTimeMillis();
		Assertions.assertTrue(timeBefore <= block.getTimeStamp(),
				"Time in the past was set to the block!");
		Assertions.assertTrue(block.getTimeStamp() <= timeAfter,
				"Time in the future was set to the block!");
	}
}
