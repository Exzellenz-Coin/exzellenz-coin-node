package mainpackage.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import mainpackage.server.message.HelloWorldMessage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {
    @Test
    @DisplayName("Simple Test")
    public void simpleTest() throws IOException, InterruptedException {
        // Create and connect the servers
        TestNode node1 = new TestNode(10000);
        TestNode node2 = new TestNode(10001);
        TestNode node3 = new TestNode(10002);
        node1.start();
        node2.start();
        node3.start();
        node2.getServer().connectToPeer("localhost", 10000);
        node3.getServer().connectToPeer("localhost", 10001);
        Thread.sleep(100);

        // Test if all mainpackage.server are connected properly
        assertEquals(1, node1.getServer().getPeers().size(), "Server 1 has not the correct number of peers");
        assertEquals(2, node2.getServer().getPeers().size(), "Server 2 has not the correct number of peers");
        assertEquals(1, node3.getServer().getPeers().size(), "Server 3 has not the correct number of peers");

        // Test if messages are sent/relayed to all peers
        HelloWorldMessage message = new HelloWorldMessage();
        node1.getServer().sendToAll(message);
        Thread.sleep(100);
        assertTrue(node1.getServer().hasReceivedMessage(message.getId()), "Message was not received by mainpackage.server 1");
        assertTrue(node2.getServer().hasReceivedMessage(message.getId()), "Message was not received by mainpackage.server 2");
        assertTrue(node3.getServer().hasReceivedMessage(message.getId()), "Message was not received by mainpackage.server 3");
    }
}
