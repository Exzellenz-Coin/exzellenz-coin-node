package mainpackage.server.message;

import mainpackage.server.Peer;
import mainpackage.server.node.NodeEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageTest {
    @Test
    @DisplayName("Connect Message Test")
    public void testConnectMessage() {
        Peer mockedPeer = mock(Peer.class);
        NodeEntry nodeEntry = new NodeEntry("", 0);
        new ConnectMessage(nodeEntry).handle(mockedPeer);
        verify(mockedPeer).setNodeEntry(nodeEntry);
    }
}
