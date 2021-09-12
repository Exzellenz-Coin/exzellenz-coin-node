package mainpackage;

import mainpackage.server.node.FullNode;
import mainpackage.server.node.INode;
import mainpackage.util.KeyHelper;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;

public class ExcellenceCoinNode {
	/**
	 * Executing this program requires the following JVM arguments:
	 *
	 * @param args TODO: currently unused
	 */
	public static void main(String[] args) {
		INode node = new FullNode();
		node.start();
		/*
		node.getServer().connectToPeer(null, 0);
		node.getServer().doInitialConnect();
		 */
	}
}
