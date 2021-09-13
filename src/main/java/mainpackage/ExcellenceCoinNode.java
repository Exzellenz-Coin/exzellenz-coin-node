package mainpackage;

import mainpackage.server.node.FullNode;
import mainpackage.server.node.INode;

public class ExcellenceCoinNode {
    /**
     * Executing this program requires the following JVM arguments:
     *
     * @param args TODO: currently unused
     */
    public static void main(String[] args) {
        // TODO: Set database configuration
        // TODO: Update database
        INode node = new FullNode();
        node.start();
		/*
		node.getServer().connectToPeer(null, 0);
		node.getServer().doInitialConnect();
		 */
    }
}
