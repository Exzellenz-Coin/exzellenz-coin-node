package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import mainpackage.server.Peer;
import mainpackage.server.message.block.CreatedBlockMessage;
import mainpackage.server.message.block.RequestBlockMessage;
import mainpackage.server.message.block.SendBlockMessage;
import mainpackage.server.message.chain.RequestChainLengthMessage;
import mainpackage.server.message.chain.SendChainLengthMessage;
import mainpackage.server.message.network.JoinNetworkMessage;
import mainpackage.server.message.network.LeaveNetworkMessage;
import mainpackage.server.message.network.RequestNetworkMessage;
import mainpackage.server.message.network.SendNetworkMessage;

import java.util.UUID;

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
        @JsonSubTypes.Type(name = "send-network", value = SendNetworkMessage.class),
        @JsonSubTypes.Type(name = "request-block", value = RequestBlockMessage.class),
        @JsonSubTypes.Type(name = "send-block", value = SendBlockMessage.class),
        @JsonSubTypes.Type(name = "request-chain-length", value = RequestChainLengthMessage.class),
        @JsonSubTypes.Type(name = "send-chain-length", value = SendChainLengthMessage.class),
        @JsonSubTypes.Type(name = "created-block", value = CreatedBlockMessage.class),
})
public abstract class AbstractMessage {
    protected final UUID id;

    public AbstractMessage() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    /**
     * This is executed by receiver after the message was received.
     * Any action that this message should perform should be triggered here.
     *
     * @param sender The peer that sent this message
     */
    public abstract void handle(Peer sender);

    /**
     * Whether the message should be relayed to all connected nodes.
     * This method is called after {@link AbstractMessage#handle(Peer)}.
     *
     * @return true if the message should be relayed
     */
    public abstract boolean shouldRelay();
}
