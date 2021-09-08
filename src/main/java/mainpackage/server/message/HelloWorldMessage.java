package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import mainpackage.server.Peer;

public class HelloWorldMessage extends AbstractMessage {
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
        System.out.println("Hello " + name);
    }

    @Override
    public boolean shouldRelay() {
        return true;
    }
}
