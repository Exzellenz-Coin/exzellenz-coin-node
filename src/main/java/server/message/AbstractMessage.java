package server.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "hello-world", value = HelloWorldMessage.class)
})
public abstract class AbstractMessage {
    protected final UUID id;

    public AbstractMessage() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public abstract void handle();
}
