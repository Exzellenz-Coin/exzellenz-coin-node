package server;

import server.node.FullNode;
import server.node.INode;

public class ExcellenceCoinServer {
	public static void main(String[] args) {
		INode node = new FullNode();
		node.start();
	}
}
