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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Transaction transaction1 = new Transaction(id1, id2, BigDecimal.ONE, new byte[0]);
        long timeBefore = System.currentTimeMillis();
        Block block = new Block("", transaction1);
        long timeAfter = System.currentTimeMillis();
        Assertions.assertTrue(timeBefore <= block.getTimeStamp(),
                "Time in the past was set to the block!");
        Assertions.assertTrue(block.getTimeStamp() <= timeAfter,
                "Time in the future was set to the block!");
    }
}
