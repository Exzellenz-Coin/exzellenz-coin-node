package mainpackage.blockchain;

import mainpackage.blockchain.transaction.StakingTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.Pair;
import mainpackage.util.Trees.MerkelNode;
import mainpackage.util.Trees.MerkelTree;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Timeout(100)
public class MerkelTest {
    @Test
    @DisplayName("Common merkel tree operations Test")
    public void testMerkel() throws Exception {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 8; i++) //add 8 transactions
            transactions.add(new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.valueOf(1 + i), BigDecimal.valueOf(0.1 + i), ""));
        //create full tree
        MerkelNode root1 = MerkelTree.generateFullTree(transactions);
        Assertions.assertTrue(MerkelTree.isComplete(root1));
        //create hash only tree
        List<String> hashes = transactions.stream().map(Hash::createHash).toList();
        MerkelNode root2 = MerkelTree.generateEmptyTree(hashes);
        Assertions.assertFalse(MerkelTree.isComplete(root2));
        Assertions.assertEquals(MerkelTree.toString(root1), MerkelTree.toString(root2)); //same order
        //fill tree an empty tree
        transactions.stream().forEach(transaction -> Assertions.assertTrue(MerkelTree.load(root2, transaction)));
        Assertions.assertTrue(MerkelTree.isComplete(root2));
        //attempt to add a wrong element
        Transaction fakeTransaction = new Transaction(Chain.FOUNDER_WALLET, StakingTransaction.STAKING_WALLET, BigDecimal.ONE, BigDecimal.valueOf(32), "faker");
        Assertions.assertFalse(MerkelTree.load(root2, fakeTransaction));
        //get needed verification elements and attempt verify
        String hashToVerify = Hash.createHash(transactions.get(4)); //some middle transaction hash
        List<Pair<String, Boolean>> minHashes = MerkelTree.hashesNeededToVerifyTransaction(root2, hashToVerify);
        Assertions.assertNotNull(minHashes);
        Assertions.assertTrue(MerkelTree.verify(minHashes, root2.getHash(), hashToVerify));
    }
}
