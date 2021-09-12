package mainpackage.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JsonMapper {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        /*
        module.addSerializer(PublicKey.class, new KeySerializer2());
        module.addSerializer(PrivateKey.class, new KeySerializer2());
        module.addDeserializer(PublicKey.class, new KeyDeserializer2<>());
        module.addDeserializer(PrivateKey.class, new KeyDeserializer2<>());
         */
        module.addSerializer(PublicKey.class, new KeySerializer());
        module.addSerializer(PrivateKey.class, new KeySerializer());
        module.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
        module.addDeserializer(PrivateKey.class, new PrivateKeyDeserializer());
        mapper.registerModule(module);
    }
}
