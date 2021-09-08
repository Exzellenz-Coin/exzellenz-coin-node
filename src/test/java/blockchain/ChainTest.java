package blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

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
        Transaction transaction = new Transaction(wallet1.getId(), wallet2.getId(), BigDecimal.TEN, new byte[0]);
        Block block = new Block(chain.getHead().getHash(), transaction);
        chain.addBlock(block);
        assertEquals(block, chain.getHead(), "A wrong head was set for the blockchain!");
    }

    @Test
    @DisplayName("Illegal Block Addition Test")
    public void testIllegalBlockAddition() {
        Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, new byte[0]);
        Block illegalBlock = new Block("I am an illegal hash :)", transaction);
        assertThrows(IllegalArgumentException.class, () -> chain.addBlock(illegalBlock));
    }

    @Test
    @DisplayName("Amount Calculation Test")
    public void testAmountCalculation() {
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();
        chain.addBlock(
                chain.getHead().getHash(),
                Chain.ROOT_WALLET.getId(),
                wallet1.getId(),
                new BigDecimal("123.456789"),
                null
        );
        chain.addBlock(
                chain.getHead().getHash(),
                wallet1.getId(),
                wallet2.getId(),
                new BigDecimal("0.123456"),
                null
        );
        chain.addBlock(
                chain.getHead().getHash(),
                Chain.ROOT_WALLET.getId(),
                wallet2.getId(),
                new BigDecimal(1),
                null
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