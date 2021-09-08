package mainpackage.server.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import mainpackage.server.Peer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "hello-world", value = HelloWorldMessage.class),
        @JsonSubTypes.Type(name = "connect", value = ConnectMessage.class),
        @JsonSubTypes.Type(name = "join-network", value = JoinNetworkMessage.class),
        @JsonSubTypes.Type(name = "leave-network", value = LeaveNetworkMessage.class),
        @JsonSubTypes.Type(name = "request-network", value = RequestNetworkMessage.class),
        @JsonSubTypes.Type(name = "send-network", value = SendNetworkMessage.class)
})
public abstract class AbstractMessage {
    protected final UUID id;

    public AbstractMessage() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public abstract void handle(Peer sender);

    public abstract boolean shouldRelay();
}
