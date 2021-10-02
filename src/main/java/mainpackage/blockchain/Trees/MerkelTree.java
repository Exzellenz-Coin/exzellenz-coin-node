package mainpackage.blockchain.Trees;

import mainpackage.blockchain.Hash;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.Pair;

import java.util.*;

import static java.util.stream.Collectors.toCollection;

/**
 * A Merkle Tree implementation inspired from https://www.pranaybathini.com/merkle-tree
 */
public class MerkelTree {
    /**
     * Creates a tree from the given transactions.
     * This method will store the transactions in their associated leaves.
     *
     * @param transactions The transactions
     * @return The Merkel Tree
     */
    public static MerkelNode generateFullTree(List<Transaction> transactions) {
        List<MerkelNode> childNodes = new ArrayList<>();

        for (Transaction transaction : transactions) {
            childNodes.add(new MerkelNode(null, null, transaction)); //construct leaf nodes
        }

        //need power of 2 leaf nodes
        String lastHash = Hash.createHash(transactions.get(transactions.size() - 1));
        if (childNodes.size() % 2 != 0 && (childNodes.size() & childNodes.size() - 1) != 0) //make even
            childNodes.add(new MerkelNode(null, null, lastHash)); //duplicated last valid hash
        while ((childNodes.size() & childNodes.size() - 1) != 0)
            childNodes.add(null); //fill with dummy nodes

        return buildTree(childNodes);
    }

    /**
     * Creates a tree from the given transaction hashes.
     * This method will not store the transactions in their associated leaves.
     *
     * @param hashes The transaction hashes
     * @return The Merkel Tree
     */
    public static MerkelNode generateEmptyTree(List<String> hashes) { //builds empty tree, which can be filled
        List<MerkelNode> childNodes = new ArrayList<>();

        for (String hash : hashes) {
            childNodes.add(new MerkelNode(null, null, hash)); //construct leaf nodes
        }

        //need power of 2 leaf nodes
        String lastHash = hashes.get(hashes.size() - 1);
        if (childNodes.size() % 2 != 0 && (childNodes.size() & childNodes.size() - 1) != 0) //make even
            childNodes.add(new MerkelNode(null, null, lastHash)); //duplicated last valid hash
        while ((childNodes.size() & childNodes.size() - 1) != 0)
            childNodes.add(null); //fill with null for 2^n children

        return buildTree(childNodes);
    }

    private static MerkelNode buildTree(List<MerkelNode> children) {
        List<MerkelNode> parents = new ArrayList<>();
        while (children.size() != 1) {
            int index = 0, length = children.size();
            while (index < length) {
                MerkelNode leftChild = children.get(index++);
                MerkelNode rightChild = children.get(index++);

                if (leftChild == null) {
                    parents.add(null);
                } else if (rightChild == null) {
                    String parentHash = Hash.createHash(leftChild, leftChild);
                    parents.add(new MerkelNode(leftChild, leftChild, parentHash));
                } else {
                    String parentHash = Hash.createHash(leftChild, rightChild);
                    parents.add(new MerkelNode(leftChild, rightChild, parentHash));
                }
            }
            children = parents;
            parents = new ArrayList<>();
        }
        return children.get(0);
    }

    /**
     * Checks if a tree has the transactions stored in its leaves.
     * Or in other words it checks if it is a Full Tree
     *
     * @param root The Merkel Tree
     * @return true if root is a full tree
     */
    public static boolean isComplete(MerkelNode root) {
        if (root == null) {
            return false;
        }

        if (root.getLeft() != null && root.getRight() != null) {
            return root.getLeft().getHash().equals(root.getRight().getHash())
                    || isComplete(root.getLeft()) && isComplete(root.getRight()); //go further down
        } else if (root.getLeft() != null && root.getRight() == null) {
            return isComplete(root.getLeft());
        } else if (root.getLeft() == null && root.getRight() != null) {
            return isComplete(root.getRight());
        } else {
            return root.getHash() != null && root.getTransaction() != null;
        }
    }

    /**
     * Load a transaction into a empty node
     *
     * @param root        The Merkel Tree
     * @param transaction The transaction
     * @return true if the transaction was successfully added
     */
    public static boolean load(MerkelNode root, Transaction transaction) {
        if (root == null) {
            return false;
        }

        //find the right leaf
        String hash = Hash.createHash(transaction);
        if (root.getLeft() != null && root.getRight() != null) {
            return load(root.getLeft(), transaction) || load(root.getRight(), transaction);
        } else if (root.getLeft() != null && root.getRight() == null) {
            return load(root.getLeft(), transaction);
        } else if (root.getLeft() == null && root.getRight() != null) {
            return load(root.getRight(), transaction);
        } else { //leaf
            if (root.getHash().equals(hash)) { //found
                root.setTransaction(transaction);
                return true;
            }
            return false;
        }
    }

    /**
     * Find the path (list of nodes) to a hash
     *
     * @param root The Merkel Tree
     * @param hash The hash
     * @return The path of nodes to get to the hash
     */
    public static List<MerkelNode> path(MerkelNode root, String hash) {
        if (root == null) {
            return null;
        }

        if ((root.getLeft() == null && root.getRight() == null)) {
            return root.getHash().equals(hash) ? new ArrayList<>() : null;
        }
        Stack<MerkelNode> stack = new Stack<>();
        List<String> visited = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            MerkelNode node = stack.peek();
            if (node != null) {
                if (node.getLeft() == null && node.getRight() == null) { //leaf
                    if (node.getHash().equals(hash)) { //found node
                        return stack.stream().filter(Objects::nonNull).collect(toCollection(ArrayList::new));
                    }
                    visited.add(node.getHash());
                    stack.pop();
                } else if (node.getLeft() != null && !visited.contains(node.getLeft().getHash())) { //go deeper left
                    stack.push(node.getLeft());
                } else if (node.getRight() != null && !visited.contains(node.getRight().getHash())) { //go deeper right
                    stack.push(node.getRight());
                } else { // dead end. go up
                    visited.add(node.getHash());
                    stack.pop();
                }
            }
        }
        return null;
    }


    //This is what a fellow node needs to verify the transaction as valid. Just call MerkelTree.verify(param) with it.
    public static List<Pair<String, Boolean>> hashesNeededToVerifyTransaction(MerkelNode root, String transactionHash) {
        List<MerkelNode> path = MerkelTree.path(root, transactionHash); //the path is from lowest to root
        /*
            System.out.println(MerkelTree.toString(root));
            System.out.println("----------path-------------------");
            path.forEach(e -> {
                        System.out.println(e.getHash());
                        if (e.getLeft() != null)
                            System.out.println("left: " + e.getLeft().getHash());
                        if (e.getRight() != null)
                            System.out.println("right: " + e.getRight().getHash());
                    }
            );
            System.out.println("------------need to send-----------------");
        */
        List<Pair<String, Boolean>> hashes = new ArrayList<>(); //the if the boolean is true, the node was right
        String curHash = transactionHash;
        for (int i = path.size() - 2; i != -1; i--) { //reverse traverse and skip root
            MerkelNode node = path.get(i);
            if (node.getLeft().getHash().equals(curHash)) {
                hashes.add(new Pair<>(node.getRight().getHash(), true)); //isRight=true
            } else {
                hashes.add(new Pair<>(node.getLeft().getHash(), false)); //isRight=false
            }
            //System.out.println(hashes.get(hashes.size() - 1));
            curHash = node.getHash();
        }
        return hashes;
    }

    public static boolean verify(List<Pair<String, Boolean>> receivedHashes, String rootHash, String transactionHash) {
        //System.out.println("-------------verify combination hashes----------------");
        //System.out.println("transhash: " + transactionHash);
        String curHash = transactionHash;
        for (Pair<String, Boolean> hash : receivedHashes) {
            curHash = hash.two() ? Hash.applySha256(curHash + hash.one()) : Hash.applySha256(hash.one() + curHash);
            //System.out.println(curHash);
        }
        return rootHash.equals(curHash);
    }
}