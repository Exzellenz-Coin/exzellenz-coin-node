package mainpackage.database;

import mainpackage.blockchain.Block;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlockMapper implements RowMapper<Block> {

    public Block map(ResultSet rs, StatementContext ctx) throws SQLException {
        final PublicKeyColumnMapper keyMapper = new PublicKeyColumnMapper();
        return new Block(
                rs.getString("prevHash"),
                new ArrayList<>(),
                rs.getLong("timeStamp"),
                keyMapper.map(rs, "validator", ctx),
                rs.getBytes("blockSignature"),
                rs.getString("hash")
        );
    }
}