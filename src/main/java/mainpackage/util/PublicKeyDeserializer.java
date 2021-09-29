package mainpackage.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyDeserializer extends StdDeserializer<PublicKey> {
    public PublicKeyDeserializer() {
        this(null);
    }

    public PublicKeyDeserializer(Class<Key> t) {
        super(t);
    }

    @Override
    public PublicKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return KeyHelper.publicKeyFromString(p.getValueAsString());
        } catch (InvalidKeySpecException e) {
            throw new IOException(e);
        }
    }
}
