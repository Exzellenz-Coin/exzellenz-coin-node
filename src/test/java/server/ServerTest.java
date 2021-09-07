package server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import server.message.HelloWorldMessage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {
    @Test
    @DisplayName("Simple Test")
    public void simpleTest() throws IOException, InterruptedException {
        // Create and connect the servers
        TestServer server1 = new TestServer(10000);
        TestServer server2 = new TestServer(10001);
        TestServer server3 = new TestServer(10002);
        server1.start();
        server2.start();
        server3.start();
        server2.connectToPeer("localhost", 10000);
        server3.connectToPeer("localhost", 10001);
        Thread.sleep(100);

        // Test if all server are connected properly
        assertEquals(1, server1.getPeers().size(), "Server 1 has not the correct number of peers");
        assertEquals(2, server2.getPeers().size(), "Server 2 has not the correct number of peers");
        assertEquals(1, server3.getPeers().size(), "Server 3 has not the correct number of peers");

        // Test if messages are sent/relayed to all peers
        HelloWorldMessage message = new HelloWorldMessage();
        server1.sendToAll(message);
        Thread.sleep(100);
        assertTrue(server1.hasReceivedMessage(message.getId()), "Message was not received by server 1");
        assertTrue(server2.hasReceivedMessage(message.getId()), "Message was not received by server 2");
        assertTrue(server3.hasReceivedMessage(message.getId()), "Message was not received by server 3");
    }
}
