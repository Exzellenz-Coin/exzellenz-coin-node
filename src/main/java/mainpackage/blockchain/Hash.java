package mainpackage.blockchain;

import mainpackage.blockchain.Trees.MerkelNode;
import mainpackage.blockchain.transaction.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {
    private static final String DELIMITER = "---";

    public static String createHash(Block block) { //hash based on contents of a block
        final StringBuilder builder = new StringBuilder();
        builder.append(block.getPrevHash())
                .append(DELIMITER)
                .append(block.getTimeStamp())
                .append(DELIMITER)
                .append(block.getValidator())
                .append(DELIMITER)
                .append(new String(block.getSignature()))
                .append(DELIMITER)
                .append(block.getMerkelRoot());
        for (final Transaction transaction : block.getTransactions()) {
            builder.append(DELIMITER)
                    .append(transaction.getSourceWalletId())
                    .append(DELIMITER)
                    .append(transaction.getTargetWalletId())
                    .append(DELIMITER)
                    .append(transaction.getAmount())
                    .append(DELIMITER)
                    .append(transaction.getTip())
                    .append(DELIMITER)
                    .append(transaction.getData())
                    .append(DELIMITER)
                    .append(new String(transaction.getSignature()));
        }
        return applySha256(builder.toString());
    }

    public static String createHash(Transaction transaction) {
        //System.out.println("hash: " + applySha256(new String(transaction.toByteArray())));
        return applySha256(new String(transaction.toByteArray()));
    }

    public static String createHash(MerkelNode nodeLeft, MerkelNode nodeRight) {
        return applySha256(nodeLeft.getHash() + nodeRight.getHash());
    }

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (final byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
