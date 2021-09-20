package mainpackage.blockchain.staking;

import mainpackage.util.Pair;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.List;

public class StakerIdentity {
    private PublicKey publicKey;
    private StakeKeys stakeKeys; //keys of the current epoch
    private BigDecimal stake;

    public StakerIdentity(PublicKey publicKey, List<Pair<PublicKey, byte[]>> publicPairs, BigDecimal stake) throws SignatureException, InvalidKeyException {
        this.publicKey = publicKey;
        this.stakeKeys = new StakeKeys();
        this.stakeKeys.generateEmpty(publicPairs);
        this.stake = stake;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public StakeKeys getStakeKeys() {
        return stakeKeys;
    }

    public void setStakeKeys(StakeKeys stakeKeys) {
        this.stakeKeys = stakeKeys;
    }

    public BigDecimal getStake() {
        return stake;
    }

    public void setStake(BigDecimal stake) {
        this.stake = stake;
    }
}
