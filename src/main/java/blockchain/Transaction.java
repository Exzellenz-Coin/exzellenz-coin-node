package blockchain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
	private final UUID sourceWalletId;
	private final UUID targetWalletId;
	private final BigDecimal amount;
	private byte[] signature;

	public Transaction(UUID sourceWalletId, UUID targetWalletId, BigDecimal amount, byte[] signature) {
		this.sourceWalletId = sourceWalletId;
		this.targetWalletId = targetWalletId;
		this.amount = amount;
		this.signature = signature;
	}

	public UUID getSourceWalletId() {
		return sourceWalletId;
	}

	public UUID getTargetWalletId() {
		return targetWalletId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(final byte[] signature) {
		this.signature = signature;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Transaction that = (Transaction) o;
		return sourceWalletId.equals(that.sourceWalletId) && targetWalletId.equals(that.targetWalletId)
			   && amount.equals(that.amount) && Arrays.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceWalletId, targetWalletId, amount);
	}

	@Override
	public String toString() {
		return "Transaction{" +
			   "sourceWalletId=" + sourceWalletId +
			   ", targetWalletId=" + targetWalletId +
			   ", amount=" + amount +
			   ", signature=" + Arrays.toString(signature) +
			   '}';
	}
}
