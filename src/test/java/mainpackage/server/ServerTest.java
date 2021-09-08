package mainpackage.server;

import mainpackage.server.message.HelloWorldMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {
    private static final int BASE_PORT = 13000;

    @Test
    @DisplayName("Message Send/Relay Test")
    public void sendRelayTest() throws IOException, InterruptedException {
        // Create and connect the servers
        TestNode node1 = new TestNode(BASE_PORT);
        TestNode node2 = new TestNode(BASE_PORT + 1);
        TestNode node3 = new TestNode(BASE_PORT + 2);
        node1.start();
        node2.start();
        node3.start();
        node2.getServer().connectToPeer("localhost", BASE_PORT);
        node3.getServer().connectToPeer("localhost", BASE_PORT + 1);
        Thread.sleep(100);

        // Test if all servers are connected properly (1<->2<->3)
        assertEquals(1, ((TestServer) node1.getServer()).getPeers().size(), "Server 1 has not the correct number of peers");
        assertEquals(2, ((TestServer) node2.getServer()).getPeers().size(), "Server 2 has not the correct number of peers");
        assertEquals(1, ((TestServer) node3.getServer()).getPeers().size(), "Server 3 has not the correct number of peers");

        // Test if messages are sent/relayed to all peers
        HelloWorldMessage message = new HelloWorldMessage();
        node1.getServer().sendToAll(message);
        Thread.sleep(100);
        assertTrue(node1.getServer().hasReceivedMessage(message.getId()), "Message was not received by server 1");
        assertTrue(node2.getServer().hasReceivedMessage(message.getId()), "Message was not received by server 2");
        assertTrue(node3.getServer().hasReceivedMessage(message.getId()), "Message was not received by server 3");
    }

    @Test
    @DisplayName("Network Synchronisation Test")
    public void networkSyncTest() throws IOException, InterruptedException {
        // Create and connect the servers
        TestNode node1 = new TestNode(BASE_PORT + 3);
        node1.start();
        assertEquals(1, node1.getNetwork().size(), "Node 1 does not know itself");

        TestNode node2 = new TestNode(BASE_PORT + 4);
        node2.start();
        node2.getServer().connectToPeer("localhost", BASE_PORT + 3);
        node2.getServer().doInitialConnect();
        Thread.sleep(100);
        assertEquals(2, node1.getNetwork().size(), "Node 1 does not know all network members");
        assertEquals(2, node2.getNetwork().size(), "Node 2 does not know all network members");

        TestNode node3 = new TestNode(BASE_PORT + 5);
        node3.start();
        node3.getServer().connectToPeer("localhost", BASE_PORT + 4);
        node3.getServer().doInitialConnect();
        Thread.sleep(100);
        assertEquals(3, node1.getNetwork().size(), "Node 1 does not know all network members");
        assertEquals(3, node2.getNetwork().size(), "Node 2 does not know all network members");
        assertEquals(3, node3.getNetwork().size(), "Node 3 does not know all network members");
    }
}
