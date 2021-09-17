package mainpackage.database.update;

import mainpackage.database.DatabaseManager;
import org.jdbi.v3.core.Handle;

@SuppressWarnings({"SqlResolve", "SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DatabaseUpdater {
    public static final int VERSION = 1;

    public int update(int currentVersion) {
        DatabaseManager.databaseConfiguration.getJdbi().withHandle(handle -> {
            if (currentVersion < 1)
                versionOneUpdate(handle);
            return null;
        });
        return VERSION;
    }

    private void versionOneUpdate(Handle handle) {
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
                    data text,
                    transactionSignature bytea not null,
                    blockHash char(64) not null,
                    foreign key (blockHash) references block(hash)
                )
                """);
    }
}
