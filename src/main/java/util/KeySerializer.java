package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class KeySerializer extends StdSerializer<Key> {
	public KeySerializer() {
		this(null);
	}

	public KeySerializer(Class<Key> t) {
		super(t);
	}

	@Override
	public void serialize(Key value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(value);
		oos.close();
		jgen.writeString(Base64.getEncoder().encodeToString(baos.toByteArray()));
	}
}
