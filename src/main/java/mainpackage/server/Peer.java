package mainpackage.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import mainpackage.server.message.AbstractMessage;
import mainpackage.server.message.LeaveNetworkMessage;
import mainpackage.server.node.INode;
import mainpackage.server.node.NodeEntry;
import mainpackage.util.JsonMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Peer extends Thread {
    protected static final Logger logger = LogManager.getLogger(Peer.class);
    protected Socket socket;
    protected INode node;
    protected Server server;
    protected NodeEntry nodeEntry;
    protected BufferedWriter writer;
    protected BufferedReader reader;

    public Peer(Socket socket, Server server) throws IOException {
        super("Peer-N/D");
        this.socket = socket;
        this.server = server;
        this.node = server.getNode();
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(AbstractMessage message) throws IOException {
        writer.write(JsonMapper.mapper.writeValueAsString(message));
        writer.newLine();
        writer.flush();
    }

    @Override
    public void run() {
        try {
            reader.lines().forEach(line -> {
                try {
                    var message = JsonMapper.mapper.readValue(line, AbstractMessage.class);
                    logger.info("Received message of type \"%s\" with id \"%s\""
                            .formatted(
                                    message.getClass().getSimpleName(),
                                    message.getId()
                            ));
                    if (server.hasReceivedMessage(message.getId()))
                        return;
                    server.cacheReceivedMessage(message.getId());
                    message.handle(this);
                    if (message.shouldRelay() && node.shouldRelayMessages())
                        server.sendToAll(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ignored) {

        } finally {
            if (node.getNetwork().contains(nodeEntry))
                server.sendToAll(new LeaveNetworkMessage(nodeEntry));
            if (server != null)
                server.remove(this);
        }
    }

    public Server getServer() {
        return server;
    }

    public INode getNode() {
        return node;
    }

    public NodeEntry getNodeEntry() {
        return nodeEntry;
    }

    public void setNodeEntry(NodeEntry nodeEntry) {
        this.nodeEntry = nodeEntry;
        if (nodeEntry == null)
            this.setName("Peer-N/D");
        else
            this.setName("Peer-%s:%d".formatted(nodeEntry.getHostName(), nodeEntry.getPort())); // TODO: Replace with a better peer identifier
    }
}
