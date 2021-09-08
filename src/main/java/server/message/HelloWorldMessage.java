package server.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloWorldMessage extends AbstractMessage {
    @JsonProperty
    private final String name;

    public HelloWorldMessage() {
        this("World");
    }

    public HelloWorldMessage(String name) {
        this.name = name;
    }

    public void handle() {
        System.out.println("Hello " + name);
    }
}