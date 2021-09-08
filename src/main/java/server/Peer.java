package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.message.AbstractMessage;
import server.node.INode;
import util.JsonMapper;

import java.io.*;
import java.net.Socket;

public class Peer extends Thread {
    protected Socket socket;
	protected INode node;
    protected Server server;
    protected BufferedWriter writer;
    protected BufferedReader reader;

    public Peer(Socket socket, Server server) throws IOException {
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
                    System.out.printf("Received message of type \"%s\" with id \"%s\"%n",
							message.getClass().getSimpleName(), message.getId());
                    if (server.hasReceivedMessage(message.getId()))
                        return;
                    server.cacheReceivedMessage(message.getId());
                    message.handle(node);
                    if (node.shouldRelayMessages())
                        server.sendToAll(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ignored) {

        } finally {
            if (server != null)
                server.remove(this);
        }
    }
}
