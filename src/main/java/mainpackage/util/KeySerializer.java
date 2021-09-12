package mainpackage.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;

public class KeySerializer extends StdSerializer<Key> {
    public KeySerializer() {
        this(null);
    }

    public KeySerializer(Class<Key> t) {
        super(t);
    }

    @Override
    public void serialize(Key value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(Base64.getEncoder().encodeToString(value.getEncoded()));
    }
}
