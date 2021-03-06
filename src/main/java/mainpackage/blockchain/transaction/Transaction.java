package mainpackage.blockchain.transaction;

import mainpackage.blockchain.Signable;
import mainpackage.util.KeyHelper;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Objects;

import static org.bouncycastle.util.Arrays.concatenate;

public class Transaction implements Signable {
    public static int DOWN_ROUNDING_SCALE = 10;
    public static String DATA_SPLIT_REGEX = "@";
    private PublicKey sourceWalletId;
    private PublicKey targetWalletId;
    private BigDecimal amount;
    private BigDecimal tip; //validiators prioritize a higher tip
    private String data;
    private byte[] signature; //transaction author signature

    private Transaction() {
    }

    public Transaction(PublicKey sourceWalletId, PublicKey targetWalletId, BigDecimal amount, BigDecimal tip, String data) {
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amount = amount;
        this.tip = tip;
        this.data = data;
    }

    public Transaction(PublicKey sourceWalletId, PublicKey targetWalletId, BigDecimal amount, BigDecimal tip, String data, byte[] signature) {
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amount = amount;
        this.tip = tip;
        this.data = data;
        this.signature = signature;
    }

    public static boolean validValues(Transaction transaction) { //looks if this transaction makes sense outside the context of a blockchain
        try {
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) == -1 //amount is less than 0
                    || transaction.getTip().compareTo(BigDecimal.ZERO) == -1 //tip is less than 0
                    || !transaction.verifySignature(transaction.sourceWalletId) //could not verify signature
            ) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        //special requirements for staking transactions
        if (transaction.getTargetWalletId().equals(StakingTransaction.STAKING_WALLET)) {
            if (transaction.getData() == null)
                return false;
            String[] dataSplit = transaction.getData().split(Transaction.DATA_SPLIT_REGEX);
            if (dataSplit[0].equals(StakingTransaction.ID)) { //need valid keys
                return StakingTransaction.parseDataToObject(dataSplit) != null;
            } else //nothing needed
                if (dataSplit[0].equals(RestakingTransaction.ID)) { //need valid keys
                return RestakingTransaction.parseDataToObject(dataSplit) != null;
            } else return dataSplit[0].equals(UnstakingTransaction.ID); //no further data needed
//not recognized
        }
        return true;
    }

    public PublicKey getSourceWalletId() {
        return sourceWalletId;
    }

    public PublicKey getTargetWalletId() {
        return targetWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTip() {
        return tip;
    }

    public String getData() {
        return data;
    }

    @Override
    public void sign(PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initSign(privateKey);
        byte[] data = this.toByteArray();
        sign.update(data);
        this.signature = sign.sign();
    }

    @Override
    public boolean verifySignature(PublicKey publicKey) throws InvalidKeyException, SignatureException {
        var sign = KeyHelper.createSignature();
        sign.initVerify(publicKey);
        byte[] data = this.toByteArray();
        sign.update(data);
        return sign.verify(this.signature);
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(final byte[] signature) {
        this.signature = signature;
    }

    public byte[] toByteArray() {
        return concatenate(sourceWalletId == null ? new byte[0] : sourceWalletId.getEncoded(), targetWalletId.getEncoded(), amount.unscaledValue().toByteArray(), tip.unscaledValue().toByteArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return Objects.equals(sourceWalletId, that.sourceWalletId) && Objects.equals(targetWalletId, that.targetWalletId) && Objects.equals(amount, that.amount) && Objects.equals(tip, that.tip) && Objects.equals(data, that.data) && Arrays.equals(signature, that.signature) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceWalletId, targetWalletId, amount, tip);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "sourceWalletId=" + sourceWalletId +
                ", targetWalletId=" + targetWalletId +
                ", amount=" + amount +
                ", tip=" + tip +
                ", data='" + data + '\'' +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
