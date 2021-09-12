package mainpackage.database;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.transaction.Transaction;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SqlResolve")
public class MapperTest {
    private static final Jdbi jdbi = DatabasePlayground.databaseConfiguration.getJdbi();

    @BeforeEach
    public void initDatabase() {
        jdbi.withHandle(handle -> {
            DatabasePlayground.createTables(handle);
            return null;
        });
    }

    @AfterEach
    public void resetDatabase() {
        jdbi.withHandle(handle -> {
            handle.execute("DROP TABLE transaction");
            handle.execute("DROP TABLE block");
            return null;
        });
    }

    @Test
    @DisplayName("Block Mapper Test")
    public void blockMapperTest() {
        Block block = Block.createGenesisBlock();
        assertThat(block).isNotNull();
        Optional<Block> readBlock = jdbi.withHandle(handle -> {
            DatabasePlayground.registerMappers(handle);
            DatabasePlayground.insertBlock(block, handle);
            return DatabasePlayground.getBlock(block.getHash(), handle);
        });
        assertThat(readBlock).get().isEqualTo(block);
    }
}
