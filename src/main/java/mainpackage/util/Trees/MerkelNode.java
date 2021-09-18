package mainpackage.util.Trees;

import mainpackage.blockchain.Hash;
import mainpackage.blockchain.transaction.Transaction;

public class MerkelNode {
    private MerkelNode left;
    private MerkelNode right;
    private String hash;
    private Transaction transaction;

    public MerkelNode(MerkelNode left, MerkelNode right, String hash) {
        this.left = left;
        this.right = right;
        this.hash = hash;
    }

    public MerkelNode(MerkelNode left, MerkelNode right, Transaction transaction) {
        this.left = left;
        this.right = right;
        this.transaction = transaction;
        this.hash = Hash.createHash(transaction);
    }

    public MerkelNode getLeft() {
        return left;
    }

    public void setLeft(MerkelNode left) {
        this.left = left;
    }

    public MerkelNode getRight() {
        return right;
    }

    public void setRight(MerkelNode right) { this.right = right; }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
