package blockchain;

import util.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {
	@Test
	@DisplayName("Serialization Test")
	public void testWalletSerialization() throws JsonProcessingException {
		var mapper = JsonMapper.mapper;
		var wallet1 = new Wallet();
		var json = mapper.writeValueAsString(wallet1);
		System.out.println(json);
		var wallet2 = mapper.readValue(json, Wallet.class);
		assertEquals(wallet1, wallet2);
	}
}
