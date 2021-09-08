package blockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class BlockTest {
	@Test
	@DisplayName("Time Test")
	public void testTime() {
		Wallet w1 = new Wallet();
		Wallet w2 = new Wallet();
		Transaction transaction1 = new Transaction(w1.getPublicKey(), w2.getPublicKey(), BigDecimal.ONE, new byte[0]);
		long timeBefore = System.currentTimeMillis();
		Block block = new Block("", Collections.singletonList(transaction1), null, null);
		long timeAfter = System.currentTimeMillis();
		Assertions.assertTrue(timeBefore <= block.getTimeStamp(),
				"Time in the past was set to the block!");
		Assertions.assertTrue(block.getTimeStamp() <= timeAfter,
				"Time in the future was set to the block!");
	}
}
