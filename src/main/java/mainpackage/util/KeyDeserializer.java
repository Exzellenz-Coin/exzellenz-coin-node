package mainpackage.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class KeyDeserializer<T extends Key> extends StdDeserializer<T> {
	public KeyDeserializer() {
		this(null);
	}

	public KeyDeserializer(Class<Key> t) {
		super(t);
	}

	@SuppressWarnings("unchecked")
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		final String json = p.getValueAsString();
		byte[] data = Base64.getDecoder().decode(json);
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(data));
		try {
			Object o = ois.readObject();
			ois.close();
			return (T) o;
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
}
