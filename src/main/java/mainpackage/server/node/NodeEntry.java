package mainpackage.server.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeEntry nodeEntry = (NodeEntry) o;
        return port == nodeEntry.port && hostName.equals(nodeEntry.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName, port);
    }

    @Override
    public String toString() {
        return "NodeEntry{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port +
                '}';
    }
}
