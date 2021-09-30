package mainpackage.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class PrivateKeyDeserializer extends StdDeserializer<PrivateKey> {
    public PrivateKeyDeserializer() {
        this(null);
    }

    public PrivateKeyDeserializer(Class<Key> t) {
        super(t);
    }

    @Override
    public PrivateKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return KeyHelper.privateKeyFromString(p.getValueAsString());
        } catch (InvalidKeySpecException e) {
            throw new IOException(e);
        }
    }
}
