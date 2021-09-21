package mainpackage.util;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JsonMapper {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(getModule());
    }

    public static Module getModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(PublicKey.class, new KeySerializer());
        module.addSerializer(PrivateKey.class, new KeySerializer());
        module.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
        module.addDeserializer(PrivateKey.class, new PrivateKeyDeserializer());
        return module;
    }
}
