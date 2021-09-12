package mainpackage.blockchain;

import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;

import java.beans.ConstructorProperties;
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
    private List<Transaction> transactions;
    private long timeStamp;
    private PublicKey validator; //rewards sent here
    private byte[] signature; //staker signature
    private String hash; //hash of this block

    private Block() {
    }

    public Block(String prevHash, List<Transaction> transactions, PublicKey validator) {
        this.prevHash = prevHash;
        this.transactions = transactions;
        this.validator = validator;
        this.timeStamp = System.currentTimeMillis();
    }

    public Block(String prevHash, List<Transaction> transactions, long timeStamp, PublicKey validator, byte[] signature, String hash) {
        this.prevHash = prevHash;
        this.transactions = transactions;
        this.timeStamp = timeStamp;
        this.validator = validator;
        this.signature = signature;
        this.hash = hash;
    }

    public static Block createGenesisBlock() {
        try {
            var transaction = new Transaction(null, Chain.FOUNDER_WALLET, BigDecimal.valueOf(100), BigDecimal.ZERO);
            var block = new Block("0", Collections.singletonList(transaction), Chain.FOUNDER_WALLET);
            // TODO: Use actual private key
            var privateKey = KeyHelper.generateKeyPair().getPrivate();
            transaction.sign(privateKey);
            block.sign(privateKey);
            block.createHash();
            return block;
        } catch (SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

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
        byte[] transactionData = concatenate(transactions.stream().map(e -> e.toByteArray()).toArray(byte[][]::new));
        byte[] data = concatenate(prevHash.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(timeStamp).toByteArray(), transactionData);
        data = concatenate(data, validator.getEncoded());
        sign.update(data);
        this.signature = sign.sign();
    }

    @Override
    public boolean verifySignature(PublicKey publicKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initVerify(publicKey);
        byte[] transactionData = concatenate((byte[][]) transactions.stream().map(e -> e.toByteArray()).toArray());
        byte[] data = concatenate(prevHash.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(timeStamp).toByteArray(), transactionData);
        data = concatenate(data, validator.getEncoded());
        sign.update(data);
        return sign.verify(this.signature);
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHash='" + prevHash + '\'' +
                ", timeStamp=" + timeStamp +
                ", transactions=" + transactions +
                ", validator=" + validator +
                ", signature=" + Arrays.toString(signature) +
                ", hash='" + hash + '\'' +
                '}';
    }
}
