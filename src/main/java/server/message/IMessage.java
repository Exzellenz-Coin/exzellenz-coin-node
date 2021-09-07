package server.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
                @JsonSubTypes.Type(name = "hello-world", value = HelloWorldMessage.class)
})
public interface IMessage {

    void handle();
}
