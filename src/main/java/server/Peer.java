package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.message.IMessage;

import java.io.*;
import java.net.Socket;

public class Peer extends Thread {
    private Socket socket;
    private Server server;
    private BufferedWriter writer;
    private BufferedReader reader;
    private ObjectMapper mapper = new ObjectMapper();

    public Peer(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(IMessage message) throws IOException {
        writer.write(mapper.writeValueAsString(message));
        writer.newLine();
        writer.flush();
    }

    @Override
    public void run() {
        try {
            reader.lines().forEach(line -> {
                System.out.println("Received: " + line);
                try {
                    IMessage message = mapper.readValue(line, IMessage.class);
                    message.handle();
                    if (server != null)
                        server.sendToAll(message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {

        }
        if (server != null)
            server.remove(this);
    }
}
