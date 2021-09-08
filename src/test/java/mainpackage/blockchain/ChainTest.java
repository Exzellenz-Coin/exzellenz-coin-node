package mainpackage.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ChainTest {
	public Chain chain;

	@BeforeEach
	public void createChain() {
		chain = new Chain();
	}

	@Test
	@DisplayName("Header Creation Test")
	public void testHeaderCreation() {
		assertNotNull(chain.getHead());
	}

	@Test
	@DisplayName("Block Addition Test")
	public void testBlockAddition() {
		Wallet wallet1 = new Wallet();
		Wallet wallet2 = new Wallet();
		Transaction transaction = new Transaction(wallet1.getPublicKey(), wallet2.getPublicKey(), BigDecimal.TEN, new byte[0]);
		Block block = new Block(chain.getHead().getHash(), Collections.singletonList(transaction),
				null, null);
		chain.addBlock(block);
		assertEquals(block, chain.getHead(), "A wrong head was set for the mainpackage.blockchain!");
	}

	@Test
	@DisplayName("Illegal Block Addition Test")
	public void testIllegalBlockAddition() {
		Wallet wallet1 = new Wallet();
		Wallet wallet2 = new Wallet();
		Transaction transaction = new Transaction(wallet1.getPublicKey(), wallet2.getPublicKey(), BigDecimal.ONE, new byte[0]);
		Block illegalBlock = new Block("I am an illegal hash :)", Collections.singletonList(transaction),
				null, null);
		assertThrows(IllegalArgumentException.class, () -> chain.addBlock(illegalBlock));
	}

	@Test
	@DisplayName("Amount Calculation Test")
	public void testAmountCalculation() {
		Wallet wallet1 = new Wallet();
		Wallet wallet2 = new Wallet();
		chain.addBlock(new Block(
						chain.getHead().getHash(),
						Collections.singletonList(new Transaction(
								Chain.ROOT_WALLET.getPublicKey(),
								wallet1.getPublicKey(),
								new BigDecimal("123.456789"),
								null
						)),
						null,
						null
				)
		);
		chain.addBlock(new Block(
						chain.getHead().getHash(),
						Collections.singletonList(new Transaction(
								wallet1.getPublicKey(),
								wallet2.getPublicKey(),
								new BigDecimal("0.123456"),
								null
						)),
						null,
						null
				)
		);
		chain.addBlock(new Block(
						chain.getHead().getHash(),
						Collections.singletonList(new Transaction(
								Chain.ROOT_WALLET.getPublicKey(),
								wallet2.getPublicKey(),
								new BigDecimal(1),
								null
						)),
						null,
						null
				)
		);

		BigDecimal expected1 = new BigDecimal("123.333333");
		BigDecimal actual1 = chain.getAmount(wallet1);
		BigDecimal expected2 = new BigDecimal("1.123456");
		BigDecimal actual2 = chain.getAmount(wallet2);
		BigDecimal expected3 = new BigDecimal("-124.456789");
		BigDecimal actual3 = chain.getAmount(Chain.ROOT_WALLET);

		assertEquals(expected1, actual1, "Amount for wallet 1 was not correctly calculated");
		assertEquals(expected2, actual2, "Amount for wallet 2 was not correctly calculated");
		assertEquals(expected3, actual3, "Amount for root wallet was not correctly calculated");
	}
}
