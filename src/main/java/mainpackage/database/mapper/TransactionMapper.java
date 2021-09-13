package mainpackage.database.mapper;

import mainpackage.blockchain.transaction.Transaction;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {

    public Transaction map(ResultSet rs, StatementContext ctx) throws SQLException {
        final PublicKeyColumnMapper keyMapper = new PublicKeyColumnMapper();
        return new Transaction(
                keyMapper.map(rs, "sourceWalletId", ctx),
                keyMapper.map(rs, "targetWalletId", ctx),
                rs.getBigDecimal("amount"),
                rs.getBigDecimal("tip"),
                rs.getBytes("transactionSignature")
        );
    }
}