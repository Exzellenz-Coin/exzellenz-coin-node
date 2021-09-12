package mainpackage.database;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.transaction.Transaction;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SignatureException;
import java.util.Collections;
import java.util.LinkedHashMap;

public class DatabasePlayground {
    public static void main(String[] args) {
        Jdbi jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/excellence-coin", "excellence-coin", "test");
        final Chain chain = new Chain();
        var blocks = jdbi.withHandle(handle -> {
            handle.registerArgument(new PublicKeyArgumentFactory());
            handle.registerColumnMapper(new PublicKeyColumnMapper());
            handle.registerRowMapper(new BlockMapper());
            handle.registerRowMapper(new TransactionMapper());
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

            handle.createUpdate("INSERT INTO block(hash, prevHash, timeStamp, validator, blockSignature) VALUES (:hash, :prevHash, :timeStamp, :validator, :signature)")
                    .bindBean(chain.getHead())
                    .execute();

            for (Transaction transaction : chain.getHead().getTransactions()) {
                handle.createUpdate("INSERT INTO transaction(sourceWalletId, targetWalletId, amount, tip, transactionSignature, blockHash) VALUES (:sourceWalletId, :targetWalletId, :amount, :transactionFee, :signature, :blockHash)")
                        .bindBean(transaction)
                        .bind("blockHash", chain.getHead().getHash())
                        .execute();
            }

            var ret =  handle.createQuery("SELECT * FROM block JOIN transaction ON block.hash = transaction.blockHash")
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
                    .values().stream().findFirst().get();
            handle.execute("DROP TABLE transaction");
            handle.execute("DROP TABLE block");
            return ret;
        });
        System.out.println(blocks);
        System.out.println(chain.getHead());
        System.out.println(blocks.getTransactions().equals(chain.getHead().getTransactions()));
        System.out.println(blocks.equals(chain.getHead()));
    }

    private static Block createBlock(String headHash, KeyPair keyPair, String amount) throws SignatureException, InvalidKeyException {
        var transaction = new Transaction(
                Chain.FOUNDER_WALLET,
                keyPair.getPublic(),
                new BigDecimal(amount),
                BigDecimal.ZERO
        );
        var block = new Block(
                headHash,
                Collections.singletonList(transaction),
                keyPair.getPublic()
        );
        transaction.sign(keyPair.getPrivate());
        block.sign(keyPair.getPrivate());
        block.createHash();
        return block;
    }
}
