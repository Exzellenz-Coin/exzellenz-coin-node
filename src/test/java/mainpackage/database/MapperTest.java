package mainpackage.database;

import mainpackage.blockchain.Block;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static mainpackage.database.DatabaseManager.*;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SqlResolve")
public class MapperTest {
    private static final Jdbi jdbi = DatabaseManager.databaseConfiguration.getJdbi();

    @BeforeEach
    public void initDatabase() {
        jdbi.withHandle(handle -> {
            databaseUpdater.update(0);
            return null;
        });
    }

    @AfterEach
    public void resetDatabase() {
        jdbi.withHandle(handle -> {
            handle.execute("DROP ALL OBJECTS");
            return null;
        });
    }

    @Test
    @DisplayName("Block Mapper Test")
    public void blockMapperTest() {
        Block block = Block.createGenesisBlock();
        assertThat(block).isNotNull();
        Optional<Block> readBlock = jdbi.withHandle(handle -> {
            registerMappers(handle);
            blockDao.insertBlock(block, handle);
            return blockDao.getBlock(block.getHash(), handle);
        });
        assertThat(readBlock).get().isEqualTo(block);
    }
}
