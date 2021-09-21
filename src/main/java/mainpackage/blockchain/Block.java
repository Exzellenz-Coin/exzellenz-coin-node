package mainpackage.blockchain;

import mainpackage.blockchain.transaction.RewardTransaction;
import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.bouncycastle.util.Arrays.concatenate;

public class Block implements Signable {
    public final static int MAX_TRANSACTIONS = 10;
    private String prevHash; //hash of the previous block
    private long blockNumber;
    private List<Transaction> transactions;
    private long timeStamp;
    private String merkelRoot; //TODO: change list of transactions to merkel tree
    private PublicKey validator; //rewards sent here
    private byte[] signature; //staker signature
    private String hash; //hash of this block

    private Block() {
    }

    public Block(String prevHash, long blockNumber, List<Transaction> transactions, PublicKey validator) {
        this.prevHash = prevHash;
        this.blockNumber = blockNumber;
        this.transactions = transactions;
        this.validator = validator;
        this.timeStamp = System.currentTimeMillis();
    }

    public Block(String prevHash, long blockNumber, List<Transaction> transactions, long timeStamp, PublicKey validator, byte[] signature, String hash) {
        this.prevHash = prevHash;
        this.blockNumber = blockNumber;
        this.transactions = transactions;
        this.timeStamp = timeStamp;
        this.validator = validator;
        this.signature = signature;
        this.hash = hash;
    }

    public Block(String prevHash, long blockNumber, String merkelRoot, PublicKey validator) {
        this.prevHash = prevHash;
        this.blockNumber = blockNumber;
        this.merkelRoot = merkelRoot;
        this.validator = validator;
        this.timeStamp = System.currentTimeMillis();
    }

    public Block(String prevHash, long blockNumber, String merkelRoot, long timeStamp, PublicKey validator, byte[] signature, String hash) {
        this.prevHash = prevHash;
        this.blockNumber = blockNumber;
        this.merkelRoot = merkelRoot;
        this.timeStamp = timeStamp;
        this.validator = validator;
        this.signature = signature;
        this.hash = hash;
    }

    public static Block createGenesisBlock() {
        var transaction = new RewardTransaction(
                Chain.FOUNDER_WALLET,
                BigDecimal.valueOf(100),
                new byte[] {74, -24, -41, 21, -46, 60, -79, -87, 12, 69, 120, -27, 19, 54, -39, -128, -24, -81, -126, -46, -18, -44, -69, -33, -62, -83, -104, -90, -33, -6, 92, -63, -117, 76, -18, 76, -36, -109, -76, -91, -15, -70, -118, 22, -112, -53, 100, -2, -126, 53, -9, -84, -23, -77, 1, -91, 110, 39, -36, 41, 115, -89, -28, 4});
        var block = new Block(
                null,
                0,
                Collections.singletonList(transaction),
                0,
                Chain.FOUNDER_WALLET,
                new byte[] {24, 43, -43, 65, 89, 32, 113, 53, -44, 87, 12, 101, 82, 49, 31, -119, -76, 86, -117, 52, 77, 6, -80, 59, 33, 57, -2, -119, -14, -51, 127, 44, 16, 62, 48, -17, -73, -28, 3, -124, 22, -127, 4, 54, 54, -71, 56, -21, -117, 26, 27, -66, -19, 15, 13, -110, 2, 107, 107, 112, 109, 30, -90, 3},
                ""
        );
        block.createHash();
        return block;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public long getBlockNumber() { return blockNumber; }

    public String getHash() {
        return hash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getMerkelRoot() { return merkelRoot; }

    public PublicKey getValidator() {
        return validator;
    }


    public byte[] getSignature() {
        return signature;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return timeStamp == block.timeStamp && Objects.equals(prevHash, block.prevHash) && Objects.equals(transactions, block.transactions) && Objects.equals(validator, block.validator) && Arrays.equals(signature, block.signature) && Objects.equals(hash, block.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prevHash, timeStamp, transactions, hash, validator, signature);
    }

    public void createHash() {
        this.hash = Hash.createHash(this);
    }

    @Override
    public void sign(PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initSign(privateKey);
        byte[] transactionData = concatenate(transactions.stream().map(Transaction::toByteArray).toArray(byte[][]::new));
        byte[] data = concatenate(validator.getEncoded(), BigInteger.valueOf(timeStamp).toByteArray(), transactionData);
        if (prevHash != null)
            data = concatenate(data, prevHash.getBytes(StandardCharsets.UTF_8));
        sign.update(data);
        this.signature = sign.sign();
    }

    @Override
    public boolean verifySignature(PublicKey publicKey) {
        try {
            var sign = KeyHelper.createSignature();
            sign.initVerify(publicKey);
            byte[] transactionData = concatenate((byte[][]) transactions.stream().map(Transaction::toByteArray).toArray());
            byte[] data = concatenate(prevHash.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(timeStamp).toByteArray(), transactionData);
            data = concatenate(data, validator.getEncoded());
            sign.update(data);
            return sign.verify(this.signature);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHash='" + prevHash + '\'' +
                ", blockNumber=" + blockNumber +
                ", transactions=" + transactions +
                ", timeStamp=" + timeStamp +
                ", merkelRoot='" + merkelRoot + '\'' +
                ", validator=" + validator +
                ", signature=" + Arrays.toString(signature) +
                ", hash='" + hash + '\'' +
                '}';
    }
}
