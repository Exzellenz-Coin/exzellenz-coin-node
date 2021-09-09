package mainpackage.server;

import mainpackage.server.message.HelloWorldMessage;
import mainpackage.server.node.NodeEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

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
        HashSet<NodeEntry> expectedSet = new HashSet<>();
        // Create and connect the servers
        TestNode node1 = new TestNode(BASE_PORT + 3);
        node1.start();
        expectedSet.add(node1.getNodeEntry());
        assertEquals(expectedSet, node1.getNetwork(), "Node 1 does not know the correct network members");

        TestNode node2 = new TestNode(BASE_PORT + 4);
        node2.start();
        expectedSet.add(node2.getNodeEntry());
        node2.getServer().connectToPeer("localhost", BASE_PORT + 3);
        node2.getServer().doInitialConnect();
        Thread.sleep(100);
        assertEquals(expectedSet, node1.getNetwork(), "Node 1 does not know the correct network members");
        assertEquals(expectedSet, node2.getNetwork(), "Node 2 does not know the correct network members");

        TestNode node3 = new TestNode(BASE_PORT + 5);
        node3.start();
        expectedSet.add(node3.getNodeEntry());
        node3.getServer().connectToPeer("localhost", BASE_PORT + 4);
        node3.getServer().doInitialConnect();
        Thread.sleep(100);
        assertEquals(expectedSet, node1.getNetwork(), "Node 1 does not know the correct network members");
        assertEquals(expectedSet, node2.getNetwork(), "Node 2 does not know the correct network members");
        assertEquals(expectedSet, node3.getNetwork(), "Node 3 does not know the correct network members");
    }
}
