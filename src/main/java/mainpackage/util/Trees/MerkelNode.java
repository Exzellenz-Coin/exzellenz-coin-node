package mainpackage.util.Trees;

import mainpackage.blockchain.Hash;
import mainpackage.blockchain.transaction.Transaction;

import java.util.Objects;

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

    public void setRight(MerkelNode right) {
        this.right = right;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerkelNode that = (MerkelNode) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right) && Objects.equals(hash, that.hash) && Objects.equals(transaction, that.transaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, hash, transaction);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int count) {
        String spacer = "    ".repeat(Math.max(0, count));
        String line = spacer +
                "-" +
                hash +
                "\n";
        if (getLeft() == null && getRight() == null)
            return line;
        var left = getLeft() == null ? "    " + spacer + "-" + "Empty\n" : getLeft().toString(count + 1);
        var right = getRight() == null ? "    " + spacer + "-" + "Empty\n" : getRight().toString(count + 1);
        return line + left + right;
    }
}
