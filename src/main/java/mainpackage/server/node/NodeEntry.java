package mainpackage.server.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeEntry {
    @JsonProperty
    private String hostName;
    @JsonProperty
    private int port;

    private NodeEntry() {
    }

    public NodeEntry(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public String toString() {
        return "NodeEntry{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port +
                '}';
    }
}
