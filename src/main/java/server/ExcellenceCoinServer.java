package server;

import static java.math.BigDecimal.ONE;

import java.math.BigDecimal;

import blockchain.Block;
import blockchain.Chain;
import blockchain.Transaction;
import blockchain.Wallet;

public class ExcellenceCoinServer {
	public static void main(String[] args) {
		Chain blockChain = new Chain();

		final Wallet walletOne = new Wallet();
		final Wallet walletTwo = new Wallet();

		blockChain.addBlock(new Block(
				blockChain.getHead().getHash(),
				new Transaction(
						Chain.ROOT_WALLET.getId(),
						walletOne.getId(),
						ONE, // STONKS!!!
						new byte[0]
				)
		));
		blockChain.addBlock(new Block(
				blockChain.getHead().getHash(),
				new Transaction(
						walletOne.getId(),
						walletTwo.getId(),
						new BigDecimal("0.25"), // NOT STONKS
						new byte[0]
				)
		));
		blockChain.addBlock(new Block(
				blockChain.getHead().getHash(),
				new Transaction(
						Chain.ROOT_WALLET.getId(),
						walletTwo.getId(),
						new BigDecimal("3.000000000000000000000000000000000000000000000000000000000000000000000001"), // ULTIMATE STONKS
						new byte[0]
				)
		));
		blockChain.printChain();
		System.out.println("Wallet 1: " + blockChain.getAmount(walletOne) + "€");
		System.out.println("Wallet 2: " + blockChain.getAmount(walletTwo) + "€");
		System.out.println("Total: " + blockChain.getAmount(Chain.ROOT_WALLET).abs() + "€");

	}
}
