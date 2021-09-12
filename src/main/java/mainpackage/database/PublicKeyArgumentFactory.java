package mainpackage.database;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.security.PublicKey;
import java.sql.Types;

class PublicKeyArgumentFactory extends AbstractArgumentFactory<PublicKey> {
    PublicKeyArgumentFactory() {
        super(Types.BINARY);
    }

    @Override
    protected Argument build(PublicKey value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setBytes(position, value.getEncoded());
    }
}