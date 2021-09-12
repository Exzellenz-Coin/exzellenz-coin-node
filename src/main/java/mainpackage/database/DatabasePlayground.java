package mainpackage.database;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.transaction.Transaction;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.LinkedHashMap;
import java.util.Optional;

public class DatabasePlayground {
    public static DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.DEV_CONFIG;

    public static void registerMappers(Handle handle) {
        handle.registerArgument(new PublicKeyArgumentFactory());
        handle.registerColumnMapper(new PublicKeyColumnMapper());
        handle.registerRowMapper(new BlockMapper());
        handle.registerRowMapper(new TransactionMapper());
    }

    public static void createTables(Handle handle) {
        handle.execute("""
                CREATE TABLE IF NOT EXISTS block (
                    hash char(64) not null,
                    prevHash char(64),
                    timeStamp int8,
                    validator bytea not null,
                    blockSignature bytea not null,
                    primary key (hash),
                    foreign key (prevHash) references block(hash)
                )
                """);

        handle.execute("""
                CREATE TABLE IF NOT EXISTS transaction (
                    sourceWalletId bytea,
                    targetWalletId bytea not null,
                    amount numeric,
                    tip numeric,
                    transactionSignature bytea not null,
                    blockHash char(64) not null,
                    foreign key (blockHash) references block(hash)
                )
                """);
    }

    public static void insertBlock(Block block, Handle handle) {
        handle.createUpdate("INSERT INTO block(hash, prevHash, timeStamp, validator, blockSignature) VALUES (:hash, :prevHash, :timeStamp, :validator, :signature)")
                .bindBean(block)
                .execute();
        for (Transaction transaction : block.getTransactions()) {
            insertTransaction(transaction, block.getHash(), handle);
        }
    }

    public static void insertTransaction(Transaction transaction, String blockHash, Handle handle) {
        handle.createUpdate("INSERT INTO transaction(sourceWalletId, targetWalletId, amount, tip, transactionSignature, blockHash) VALUES (:sourceWalletId, :targetWalletId, :amount, :transactionFee, :signature, :blockHash)")
                .bindBean(transaction)
                .bind("blockHash", blockHash)
                .execute();
    }

    public static Optional<Block> getBlock(String hash, Handle handle) {
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
                .values().stream().findFirst();
    }

    public static void main(String[] args) {
        Jdbi jdbi = databaseConfiguration.getJdbi();
        final Chain chain = new Chain();
        var block = jdbi.withHandle(handle -> {
            registerMappers(handle);
            createTables(handle);
            insertBlock(chain.getHead(), handle);

            var ret = getBlock(chain.getHead().getHash(), handle);
            handle.execute("DROP TABLE transaction");
            handle.execute("DROP TABLE block");
            return ret;
        });
        System.out.println(block);
    }
}
