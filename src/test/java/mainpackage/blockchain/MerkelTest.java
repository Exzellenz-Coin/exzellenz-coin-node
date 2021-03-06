package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.Pair;
import mainpackage.blockchain.Trees.MerkelNode;
import mainpackage.blockchain.Trees.MerkelTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(100)
public class MerkelTest {
    @Test
    @DisplayName("Common merkel tree operations Test")
    public void testMerkelPower2() {
        runMerkelTests(2);
        runMerkelTests(4);
        runMerkelTests(8);
        runMerkelTests(16);
        runMerkelTests(32);
    }

    @Test
    @DisplayName("Common merkel tree operations non power of 2 transactions Test")
    public void testMerkelNonPower2() {
        runMerkelTests(1);
        runMerkelTests(3);
        runMerkelTests(7);
        runMerkelTests(9);
        runMerkelTests(17);
        runMerkelTests(31);
    }

    public void runMerkelTests(int numTransactions) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < numTransactions; i++)
            transactions.add(new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.valueOf(1 + i), BigDecimal.valueOf(0.1 + i), ""));

        // Create a full tree
        MerkelNode fullTree = MerkelTree.generateFullTree(transactions);
        assertTrue(MerkelTree.isComplete(fullTree), "Full tree was identified as hash-only tree");

        // Create a hash only tree
        List<String> hashes = transactions.stream().map(Hash::createHash).toList();
        MerkelNode hashOnlyTree = MerkelTree.generateEmptyTree(hashes);
        assertFalse(MerkelTree.isComplete(hashOnlyTree), "Hash-only tree was identified as full tree");

        // Check if both trees have the same structure and order
        assertEquals(fullTree.toString(), hashOnlyTree.toString());

        // Fill hash only tree with transactions aka. converting it to a full tree
        transactions.forEach(transaction -> assertTrue(MerkelTree.load(hashOnlyTree, transaction)));
        assertTrue(MerkelTree.isComplete(hashOnlyTree), "Full tree was identified as hash-only tree");
        assertEquals(fullTree, hashOnlyTree, "The transactions were loaded incorrectly");

        // Attempt to add a wrong element
        Transaction fakeTransaction = new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.ONE, BigDecimal.valueOf(32), "faker");
        assertFalse(MerkelTree.load(hashOnlyTree, fakeTransaction));

        // Get needed verification elements and attempt verify
        String hashToVerify = Hash.createHash(transactions.get(numTransactions/2)); //some middle transaction hash
        //System.out.println(hashOnlyTree.getLeft().getHash().equals(hashToVerify) && hashOnlyTree.getRight().getHash().equals(hashToVerify));
        List<Pair<String, Boolean>> minHashes = MerkelTree.hashesNeededToVerifyTransaction(hashOnlyTree, hashToVerify);
        assertNotNull(minHashes);
        assertTrue(MerkelTree.verify(minHashes, hashOnlyTree.getHash(), hashToVerify));
    }
}
