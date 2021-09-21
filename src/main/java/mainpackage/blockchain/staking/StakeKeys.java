package mainpackage.blockchain.staking;

import mainpackage.util.KeyHelper;
import mainpackage.util.Pair;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class StakeKeys {
    private static byte[] SHARED_SIGNABLE = "SIGNME".getBytes(StandardCharsets.UTF_8); //every node signs the same data
    private int amount;
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
            publicPairs.add(new Pair(kp.getPublic(), StakeKeys.signData(kp.getPublic(), kp.getPrivate())));
        }
    }

    public void generateEmpty(List<Pair<PublicKey, byte[]>> publicPairs) throws SignatureException, InvalidKeyException { //initializes empty
        if (publicPairs != null && publicPairs.size() <= 0)
            throw new IllegalArgumentException("amount of keys is less than 1");
        this.amount = publicPairs.size();
        this.publicPairs = publicPairs;
        this.privateKeys.clear();
        this.publicPairs.forEach(k -> this.privateKeys.add(null)); //private key filled with this.amount of null objects
    }

    //check if the private key a node send was unused and valid; update the private keys used
    public boolean tryAcceptPrivateKey(PrivateKey privateKey, byte[] signature, int index) throws SignatureException, InvalidKeyException {
        if (index < 0 || index > publicPairs.size() - 1)
            throw new IllegalArgumentException("index for that key does not exist");
        if (privateKeys.contains(privateKey) || !StakeKeys.validatePrivate(publicPairs.get(index).two(), privateKey))
            return false;
        privateKeys.set(index, privateKey);
        return true;
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
        sign.update(SHARED_SIGNABLE);
        return sign.sign().equals(signature);
    }

    private static byte[] signData(PublicKey publicKey, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initSign(privateKey);
        sign.update(SHARED_SIGNABLE);
        return sign.sign();
    }
}
