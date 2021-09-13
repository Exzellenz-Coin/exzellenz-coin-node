package mainpackage.database.mapper;

import mainpackage.util.KeyHelper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PublicKeyColumnMapper implements ColumnMapper<PublicKey> {
    public PublicKey map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        final byte[] bytes = r.getBytes(columnNumber);
        if (bytes == null) return null;
        try {
            return KeyHelper.keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (InvalidKeySpecException e) {
            throw new SQLException(e);
        }
    }
}