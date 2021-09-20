package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SignatureException;
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
    public void testBlockAddition() throws SignatureException, InvalidKeyException {
        KeyPair wallet1 = KeyHelper.generateKeyPair();
        KeyPair wallet2 = KeyHelper.generateKeyPair();
        Transaction transaction = new Transaction(wallet1.getPublic(), wallet2.getPublic(), BigDecimal.TEN, BigDecimal.ZERO, "");
        Block block = new Block(chain.getHead().getHash(), 0, Collections.singletonList(transaction), wallet1.getPublic());
        transaction.sign(wallet1.getPrivate());
        block.sign(wallet1.getPrivate());
        block.createHash();
        chain.tryAddBlockSync(block);
        assertEquals(block, chain.getHead(), "A wrong head was set for the mainpackage.blockchain!");
    }

    @Test
    @DisplayName("Illegal Block Addition Test")
    public void testIllegalBlockAddition() {
        KeyPair wallet1 = KeyHelper.generateKeyPair();
        KeyPair wallet2 = KeyHelper.generateKeyPair();
        Transaction transaction = new Transaction(wallet1.getPublic(), wallet2.getPublic(), BigDecimal.ONE, BigDecimal.ZERO, "");
        Block illegalBlock = new Block("I am an illegal hash :)", 0, Collections.singletonList(transaction), null);
        assertFalse(chain.tryAddBlockSync(illegalBlock));
    }

    @Test
    @DisplayName("Amount Calculation Test")
    public void testAmountCalculation() {
        KeyPair wallet1 = KeyHelper.generateKeyPair();
        KeyPair wallet2 = KeyHelper.generateKeyPair();
        chain.addBlock(new Block(
                        chain.getHead().getHash(),
                        0,
                        Collections.singletonList(new Transaction(
                                Chain.FOUNDER_WALLET,
                                wallet1.getPublic(),
                                new BigDecimal("123.456789"),
                                BigDecimal.ZERO,
                                ""
                        )),
                        null
                )
        );
        chain.addBlock(new Block(
                        chain.getHead().getHash(),
                         0,
                        Collections.singletonList(new Transaction(
                                wallet1.getPublic(),
                                wallet2.getPublic(),
                                new BigDecimal("0.123456"),
                                BigDecimal.ZERO,
                                ""
                        )),
                        null
                )
        );
        chain.addBlock(new Block(
                        chain.getHead().getHash(),
                         0,
                        Collections.singletonList(new Transaction(
                                Chain.FOUNDER_WALLET,
                                wallet2.getPublic(),
                                new BigDecimal(1),
                                BigDecimal.ZERO,
                                ""
                        )),
                        null
                )
        );

        BigDecimal expected1 = new BigDecimal("123.333333");
        BigDecimal actual1 = chain.getAmount(wallet1.getPublic());
        BigDecimal expected2 = new BigDecimal("1.123456");
        BigDecimal actual2 = chain.getAmount(wallet2.getPublic());
        BigDecimal expected3 = new BigDecimal("-124.456789");
        //BigDecimal actual3 = chain.getAmount(Chain.FOUNDER_WALLET);

		assertEquals(expected1, actual1, "Amount for wallet 1 was not correctly calculated");
		assertEquals(expected2, actual2, "Amount for wallet 2 was not correctly calculated");
		//assertEquals(expected3, actual3, "Amount for root wallet was not correctly calculated");
	}

	@Test
	@DisplayName("Update Wallet Test")
	public void testWalletCache() {
		assertEquals(BigDecimal.valueOf(100), chain.getCachedAmount(Chain.FOUNDER_WALLET)); //initial funds
		chain.addBlock(new Block(
						chain.getHead().getHash(),
                        0,
						Collections.singletonList(new Transaction(
								Chain.FOUNDER_WALLET,
								StakingTransaction.STAKING_WALLET,
								new BigDecimal(4),
								new BigDecimal(1),
                                ""
                        )),
						Chain.FOUNDER_WALLET
				)
		);
		assertEquals(BigDecimal.valueOf(196).setScale(Transaction.DOWN_ROUNDING_SCALE, RoundingMode.DOWN), chain.getCachedAmount(Chain.FOUNDER_WALLET)); //initial funds
		assertEquals(BigDecimal.valueOf(4), chain.getCachedAmount(StakingTransaction.STAKING_WALLET)); //initial funds
	}
}
