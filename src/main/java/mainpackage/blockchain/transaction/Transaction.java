package mainpackage.blockchain.transaction;

import mainpackage.blockchain.Signable;
import mainpackage.util.KeyHelper;

import java.math.BigDecimal;
import java.security.*;
import java.util.Arrays;
import java.util.Objects;

import static org.bouncycastle.util.Arrays.concatenate;

public class Transaction implements Signable {
    private final PublicKey sourceWalletId;
    private final PublicKey targetWalletId;
    private final BigDecimal amount;
    private final BigDecimal tip; //validiators prioritize a higher tip
    private byte[] signature; //transaction author signature

    public Transaction(PublicKey sourceWalletId, PublicKey targetWalletId, BigDecimal amount, BigDecimal tip) {
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amount = amount;
        this.tip = tip;
    }

    public static boolean validValues(Transaction transaction) { //looks if this transaction makes sense outside the context of a blockchain
        try {
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) != 1 //amount is not greater than 0
                    || transaction.getTransactionFee().compareTo(BigDecimal.ZERO) == -1 //tip is less than 0
                    || !transaction.verifySignature(transaction.sourceWalletId) //could not verify signature
            ) {
                return false;
            }
        } catch (Exception e) {
            return false;
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

    public BigDecimal getTransactionFee() {
        return tip;
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Transaction that = (Transaction) o;
        return sourceWalletId.equals(that.sourceWalletId) && targetWalletId.equals(that.targetWalletId)
                && amount.equals(that.amount) && tip.equals(that.tip) && Arrays.equals(signature, that.signature);
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
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
