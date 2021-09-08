package mainpackage.util;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonMapper {
	public static final ObjectMapper mapper = new ObjectMapper();
	static {
		SimpleModule module = new SimpleModule();
		module.addSerializer(PublicKey .class, new KeySerializer());
		module.addSerializer(PrivateKey .class, new KeySerializer());
		module.addDeserializer(PublicKey.class, new KeyDeserializer<>());
		module.addDeserializer(PrivateKey.class, new KeyDeserializer<>());
		mapper.registerModule(module);
	}
}
