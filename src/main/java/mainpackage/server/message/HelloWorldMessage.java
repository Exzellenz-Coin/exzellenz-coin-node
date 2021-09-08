package mainpackage.server.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import mainpackage.server.node.INode;

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
    public void handle(INode node) {
        System.out.println("Hello " + name);
    }
}
