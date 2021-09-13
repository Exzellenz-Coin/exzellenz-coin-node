package mainpackage.server.message;

import mainpackage.blockchain.Chain;
import mainpackage.server.Peer;
import mainpackage.server.message.block.RequestBlockMessage;
import mainpackage.server.message.chain.RequestChainLengthMessage;
import mainpackage.server.message.chain.SendChainLengthMessage;
import mainpackage.server.node.INode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChainMessageTest {
    @Mock
    private Peer mockedPeer;
    @Mock
    private INode mockedNode;

    @BeforeEach
    public void init() {
        when(mockedPeer.getNode()).thenReturn(mockedNode);
    }

    @Test
    @DisplayName("Request Length Test")
    public void testRequestLengthMessage() throws IOException {
        when(mockedNode.getBlockChain()).thenReturn(new Chain());
        new RequestChainLengthMessage().handle(mockedPeer);
        verify(mockedNode).getBlockChain();
        verify(mockedPeer).send(any(SendChainLengthMessage.class));
    }

    @Test
    @DisplayName("Send Length Test")
    public void testSendLengthMessage() throws IOException {
        when(mockedNode.getBlockChain()).thenReturn(new Chain());
        new SendChainLengthMessage(100).handle(mockedPeer);
        verify(mockedPeer, times(1)).send(any(RequestBlockMessage.class));
    }
}
