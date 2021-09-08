package mainpackage.server;

import mainpackage.server.node.FullNode;
import mainpackage.server.node.INode;

public class ExcellenceCoinServer {
	public static void main(String[] args) {
		INode node = new FullNode();
		node.start();
	}
}
