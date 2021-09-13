package mainpackage.server.message;

import mainpackage.server.Peer;
import mainpackage.server.message.network.JoinNetworkMessage;
import mainpackage.server.message.network.LeaveNetworkMessage;
import mainpackage.server.message.network.RequestNetworkMessage;
import mainpackage.server.message.network.SendNetworkMessage;
import mainpackage.server.node.INode;
import mainpackage.server.node.NodeEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NetworkMessageTest {
    private final NodeEntry nodeEntry = new NodeEntry("", 0);
    @Mock
    private Peer mockedPeer;
    @Mock
    private INode mockedNode;

    @BeforeEach
    public void init() {
        when(mockedPeer.getNode()).thenReturn(mockedNode);
    }

    @Test
    @DisplayName("Join Network Test")
    public void testJoinMessage() {
        when(mockedNode.addNodeEntry(nodeEntry)).thenReturn(true);
        new JoinNetworkMessage(nodeEntry).handle(mockedPeer);
        verify(mockedNode).addNodeEntry(nodeEntry);
    }

    @Test
    @DisplayName("Leave Network Test")
    public void testLeaveMessage() {
        when(mockedNode.removeNodeEntry(nodeEntry)).thenReturn(true);
        new LeaveNetworkMessage(nodeEntry).handle(mockedPeer);
        verify(mockedNode).removeNodeEntry(nodeEntry);
    }

    @Test
    @DisplayName("Request Network Test")
    public void testRequestMessage() throws IOException {
        new RequestNetworkMessage().handle(mockedPeer);
        verify(mockedNode).getNetwork();
        verify(mockedPeer).send(any(SendNetworkMessage.class));
    }

    @Test
    @DisplayName("Send Network Test")
    public void testSendMessage() {
        NodeEntry nodeEntry2 = new NodeEntry("", 1);
        HashSet<NodeEntry> set = new HashSet<>();
        set.add(nodeEntry);
        set.add(nodeEntry2);
        new SendNetworkMessage(set).handle(mockedPeer);
        verify(mockedNode).resetNetwork();
        verify(mockedNode).addNodeEntry(nodeEntry);
        verify(mockedNode).addNodeEntry(nodeEntry2);
    }
}
