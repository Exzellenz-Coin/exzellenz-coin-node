package mainpackage.blockchain.staking;

import mainpackage.blockchain.transaction.Transaction;
import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class StakeKeys {
    private final static String SHARED_DATA = "SIGNME";
    private final static byte[] SHARED_SINGABLE = SHARED_DATA.getBytes(StandardCharsets.UTF_8); //every node signs the same data
    private int amount;
    private int headIndex;
    private List<Pair<PublicKey, byte[]>> publicPairs;
    private List<PrivateKey> privateKeys;

    public StakeKeys() {
        publicPairs = new ArrayList<>();
        privateKeys = new ArrayList<>();
    }

    public void generateFull(int amount) throws SignatureException, InvalidKeyException { //this creates keys, which is only for our node
        if (amount <= 0)
            throw new IllegalArgumentException("amount of keys is less than 1");
        this.amount = amount;
        publicPairs.clear();
        privateKeys.clear();
        for (int i = 0; i < amount; i++) {
            var kp = KeyHelper.generateKeyPair();
            privateKeys.add(kp.getPrivate());
            publicPairs.add(new Pair<>(kp.getPublic(), StakeKeys.signData(kp.getPublic(), kp.getPrivate())));
        }
        headIndex = amount;
    }

    public void generateEmpty(List<Pair<PublicKey, byte[]>> publicPairs) { //initializes empty
        if (publicPairs == null || publicPairs.size() <= 0)
            throw new IllegalArgumentException("amount of keys is less than 1");
        this.amount = publicPairs.size();
        this.publicPairs = publicPairs;
        this.privateKeys.clear();
        this.publicPairs.forEach(k -> this.privateKeys.add(null)); //private key filled with this.amount of null objects
        this.headIndex = 0;
    }

    //check if the private key a node send was unused and valid; update the private keys used
    public boolean tryAcceptPrivateKey(PrivateKey privateKey) throws SignatureException, InvalidKeyException {
        if (headIndex != amount)
            throw new IllegalArgumentException("index for that key does not exist");
        if (privateKeys.contains(privateKey) || !StakeKeys.validatePrivate(publicPairs.get(headIndex).two(), privateKey))
            return false;
        privateKeys.set(headIndex, privateKey);
        headIndex++;
        return true;
    }

    //returns the next unused private key
    public PrivateKey popPrivateKey() {
        if (headIndex == amount)
            return null;
        return privateKeys.get(headIndex++);
    }

    public List<Pair<PublicKey, byte[]>> getPublicPairs() {
        return publicPairs;
    }

    public void setPublicPairs(List<Pair<PublicKey, byte[]>> publicPairs) {
        this.publicPairs = publicPairs;
    }

    public List<PrivateKey> getPrivateKeys() {
        return privateKeys;
    }

    public void setPrivateKeys(List<PrivateKey> privateKeys) {
        this.privateKeys = privateKeys;
    }

    public static boolean validatePublic(byte[] signature, PublicKey publicKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initVerify(publicKey);
        return sign.verify(signature);
    }

    public static boolean validatePrivate(byte[] signature, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initSign(privateKey);
        sign.update(SHARED_SINGABLE);
        return Arrays.equals(sign.sign(), signature);
    }

    private static byte[] signData(PublicKey publicKey, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initSign(privateKey);
        sign.update(SHARED_SINGABLE);
        return sign.sign();
    }

    public String toString() {
        var strBuilder = new StringBuilder();
        publicPairs.forEach(e -> strBuilder
                .append(Transaction.DATA_SPLIT_REGEX)
                .append(Base64.getEncoder().encodeToString(e.one().getEncoded()))
                .append(Transaction.DATA_SPLIT_REGEX)
                .append(Base64.getEncoder().encodeToString(e.two())));
        return strBuilder.toString();
    }
}
