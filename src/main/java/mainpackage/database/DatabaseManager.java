package mainpackage.database;

import mainpackage.database.dao.BlockDao;
import mainpackage.database.mapper.BlockMapper;
import mainpackage.database.mapper.PublicKeyArgumentFactory;
import mainpackage.database.mapper.PublicKeyColumnMapper;
import mainpackage.database.mapper.TransactionMapper;
import mainpackage.database.update.DatabaseUpdater;
import org.jdbi.v3.core.Handle;

@SuppressWarnings("SqlResolve")
public class DatabaseManager {
    public static DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.DEV_CONFIG;
    public static DatabaseUpdater databaseUpdater = new DatabaseUpdater();
    public static BlockDao blockDao = new BlockDao();

    public static void registerMappers(Handle handle) {
        handle.registerArgument(new PublicKeyArgumentFactory());
        handle.registerColumnMapper(new PublicKeyColumnMapper());
        handle.registerRowMapper(new BlockMapper());
        handle.registerRowMapper(new TransactionMapper());
    }
}
