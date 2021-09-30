package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import mainpackage.server.Peer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloWorldMessage extends AbstractMessage {
    protected static final Logger logger = LogManager.getLogger(HelloWorldMessage.class);
    @JsonProperty
    private final String name;

    public HelloWorldMessage() {
        this("World");
    }

    public HelloWorldMessage(String name) {
        this.name = name;
    }

    @Override
    public void handle(Peer sender) {
        logger.info("Hello " + name);
    }

    @Override
    public boolean shouldRelay() {
        return true;
    }
}
