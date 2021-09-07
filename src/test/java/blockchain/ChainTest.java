package blockchain;

import java.math.BigDecimal;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChainTest {

	public Chain chain;

	@BeforeEach
	public void createChain() {
		chain = new Chain();
	}

	@Test
	public void testHeaderCreation() {
		assertNotNull(chain.getHead());
	}

	@Test
	public void testBlockAddition() {
		Wallet wallet1 = new Wallet();
		Wallet wallet2 = new Wallet();
		Transaction transaction = new Transaction(wallet1.getId(), wallet2.getId(), BigDecimal.TEN, new byte[0]);
		Block block = new Block(chain.getHead().getHash(), transaction);
		chain.addBlock(block);
		assertEquals(block, chain.getHead(), "A wrong head was set for the blockchain!");
		assertEquals(BigDecimal.TEN.negate(), chain.getAmount(wallet1));
		assertEquals(BigDecimal.TEN, chain.getAmount(wallet2));
	}

	@Test
	public void testIllegalBLockAddition() {
		Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, new byte[0]);
		Block illegalBlock = new Block("I am an illegal hash :)", transaction);
		assertThrows(IllegalArgumentException.class, () -> chain.addBlock(illegalBlock));
	}

}
