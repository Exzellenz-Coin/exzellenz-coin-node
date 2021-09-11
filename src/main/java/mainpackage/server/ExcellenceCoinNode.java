package mainpackage.server;

import mainpackage.server.node.FullNode;
import mainpackage.server.node.INode;

public class ExcellenceCoinNode {
	/**
	 * Executing this program requires the following JVM arguments:
	 * --add-opens java.base/java.math=ALL-UNNAMED
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
