package mainpackage.database.dao;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.transaction.Transaction;
import org.jdbi.v3.core.Handle;

import java.util.LinkedHashMap;
import java.util.Optional;

@SuppressWarnings("SqlResolve")
public class BlockDao {
    public void insertBlock(Block block, Handle handle) {
        handle.createUpdate("INSERT INTO block(hash, prevHash, blockNumber, timeStamp, validator, blockSignature) VALUES (:hash, :prevHash, :blockNumber, :timeStamp, :validator, :signature)")
                .bindBean(block)
                .execute();
        for (Transaction transaction : block.getTransactions()) {
            insertTransaction(transaction, block.getHash(), handle);
        }
    }

    private void insertTransaction(Transaction transaction, String blockHash, Handle handle) {
        handle.createUpdate("INSERT INTO transaction(sourceWalletId, targetWalletId, amount, tip, data, transactionSignature, blockHash) VALUES (:sourceWalletId, :targetWalletId, :amount, :tip, :data, :signature, :blockHash)")
                .bindBean(transaction)
                .bind("blockHash", blockHash)
                .execute();
    }

    public Optional<Block> getBlock(String hash, Handle handle) {
        return handle.createQuery("SELECT * FROM block JOIN transaction ON block.hash = transaction.blockHash WHERE block.hash = :hash")
                .bind("hash", hash)
                .reduceRows(
                        new LinkedHashMap<String, Block>(),
                        (map, rowView) -> {
                            final Block block = map.computeIfAbsent(
                                    rowView.getColumn("hash", String.class),
                                    id -> rowView.getRow(Block.class));
                            if (rowView.getColumn("blockHash", String.class) != null) {
                                block.getTransactions().add(rowView.getRow(Transaction.class));
                            }
                            return map;
                        })
                .values()
                .stream()
                .findFirst();
    }
}
