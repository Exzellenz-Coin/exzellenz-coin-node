package mainpackage.server.node;

import mainpackage.blockchain.Block;
import mainpackage.blockchain.Chain;
import mainpackage.blockchain.staking.StakeKeys;
import mainpackage.blockchain.transaction.*;
import mainpackage.server.Server;
import mainpackage.server.message.block.CreatedBlockMessage;
import mainpackage.server.message.chain.RequestChainLengthMessage;
import mainpackage.server.message.transaction.CreatedRestakingTransactionMessage;
import mainpackage.server.message.transaction.CreatedStakingTransactionMessage;
import mainpackage.server.message.transaction.CreatedTransactionMessage;
import mainpackage.server.message.transaction.CreatedUnstakingTransactionMessage;
import mainpackage.util.KeyHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;

/**
 * An implementation of INode that can perform every possible action.
 */
public class FullNode implements INode {
    protected static final Logger logger = LogManager.getLogger(FullNode.class);
    private static final BigDecimal DEFAULT_TIP = BigDecimal.valueOf(0.000001); //give this normally
    private static final BigDecimal DEFAULT_ACCEPT_TIP = BigDecimal.valueOf(0); //accept everything >=
    protected final Set<NodeEntry> network;
    protected final Chain blockChain;
    protected Server server;
    protected Block newBlock;
    protected ArrayList<Transaction> unofficialTransactions;
    private final StakeKeys stakeKeys;
    private PublicKey nodeWallet;
    private PrivateKey nodePrivateKey;

    public FullNode() {
        this.server = new Server(this);
        this.network = new HashSet<>();
        this.blockChain = new Chain();
        this.unofficialTransactions = new ArrayList<>();
        this.newBlock = null; //have to set this after you have the newest blockchain
        this.stakeKeys = new StakeKeys(); //these need to be set based on the epoch
        try {
            //nodeWallet = KeyHelper.loadPublicKey("node_wallet.der");
            //nodePrivateKey = KeyHelper.loadPrivateKey("node_pk.der"); //TODO: change this back on release
            nodeWallet = KeyHelper.loadPublicKey("founder_wallet.der");
            nodePrivateKey = KeyHelper.loadPrivateKey("founder_pk.der");
        } catch (Exception e) {
            e.printStackTrace();
            nodeWallet = null;
            nodePrivateKey = null;
        }
    }

    @Override
    public void start() {
        server.start();
        resetNetwork();
    }

    @Override
    public void update() {
        server.sendToAll(new RequestChainLengthMessage()); //get most recent chain
        if (blockChain.permittedToValidateNewBlock(nodeWallet)) {
            logger.debug("Attempting to create block number %d".formatted(blockChain.size()));
            validateBlock();
        }
    }

    @Override
    public void stop() {
        server.shutdown();
    }

    @Override
    public void validateBlock() {
        if (finalizeBlock()) { //creates and populates a valid newBlock
            if (blockChain.tryAddBlockSync(newBlock)) {
                logger.debug("Block number %d with %d transactions successfully created".formatted(newBlock.getBlockNumber(), newBlock.getTransactions().size()));
                server.sendToAll(new CreatedBlockMessage(newBlock, stakeKeys.popPrivateKey()));
            }
        }
    }

    @Override
    public boolean finalizeBlock() {
        try {
            //copy of our transactions so we can revert in case of an error
            ArrayList<Transaction> unofficialTransactionsCopy = new ArrayList<>(unofficialTransactions);
            ArrayList<Transaction> blockTransactions = new ArrayList<>();
            //sort transactions based on highest tips and add to the block
            unofficialTransactionsCopy.sort(Comparator.comparing(Transaction::getTip));
            while (unofficialTransactionsCopy.size() != 0 && blockTransactions.size() != Block.MAX_TRANSACTIONS) {
                Transaction cur = unofficialTransactionsCopy.get(0);
                if (blockChain.isValidTransaction(cur)) {
                    blockTransactions.add(cur);
                }
                unofficialTransactionsCopy.remove(0); //removes invalid and used transactions
            }
            //add reward transaction
            Transaction rewardTransaction = new RewardTransaction(nodeWallet, blockChain.calculateReward(blockChain.getHead().getBlockNumber() + 1));
            rewardTransaction.sign(nodePrivateKey);
            blockTransactions.add(rewardTransaction);
            unofficialTransactions = unofficialTransactionsCopy; //finalize transactions that where used
            //populate newBlock
            newBlock = new Block(blockChain.getHead().getHash(), blockChain.getHead().getBlockNumber() + 1, blockTransactions, nodeWallet);
            //sign and hash
            newBlock.sign(nodePrivateKey);
            newBlock.createHash();
            return true;
        } catch (Exception ignored) {
            logger.debug("Failed creating a valid block. Aborting send");
            //ignored.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean shouldRelayMessages() {
        return true;
    }

    @Override
    public Set<NodeEntry> getNetwork() {
        return network;
    }

    @Override
    public Chain getBlockChain() {
        return blockChain;
    }

    @Override
    public void resetNetwork() {
        network.clear();
        network.add(getNodeEntry());
    }

    @Override
    public boolean addNodeEntry(NodeEntry nodeEntry) {
        boolean added = network.add(nodeEntry);
        if (added)
            logger.debug("Added node %s from network".formatted(nodeEntry));
        return added;
    }

    @Override
    public boolean removeNodeEntry(NodeEntry nodeEntry) {
        boolean removed = network.remove(nodeEntry);
        if (removed)
            logger.debug("Removed node %s from network".formatted(nodeEntry));
        return removed;
    }

    @Override
    public NodeEntry getNodeEntry() {
        return new NodeEntry("localhost", server.getPort()); // TODO: Replace localhost with public IP address
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public Block getNewBlock() {
        return newBlock;
    }

    @Override
    public void setNewBlock(Block block) {
        this.newBlock = block;
    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        if (Transaction.validValues(transaction)
                && !unofficialTransactions.contains(transaction)
                && blockChain.isValidTransaction(transaction)) {
            unofficialTransactions.add(transaction);
            server.sendToAll(new CreatedTransactionMessage(transaction));
            return true;
        }
        return false;
    }

    //node staking transaction creation
    public boolean stake(BigDecimal amount) throws SignatureException, InvalidKeyException {
        stakeKeys.generateFull(blockChain.calculateNumberStakeKeys(amount)); //generate keys
        StakingTransaction transaction = new StakingTransaction(nodeWallet, amount, DEFAULT_TIP, stakeKeys);
        transaction.sign(nodePrivateKey);
        if (addTransaction(transaction)) {
            server.sendToAll(new CreatedStakingTransactionMessage(transaction));
            return true;
        }
        return false;
    }

    public boolean unstake(BigDecimal amount) throws SignatureException, InvalidKeyException {
        UnstakingTransaction transaction = new UnstakingTransaction(nodeWallet, amount, DEFAULT_TIP);
        transaction.sign(nodePrivateKey);
        if (addTransaction(transaction)) {
            server.sendToAll(new CreatedUnstakingTransactionMessage(transaction));
            return true;
        }
        return false;
    }

    public boolean restake(BigDecimal amount) throws SignatureException, InvalidKeyException {
        RestakingTransaction transaction = new RestakingTransaction(nodeWallet, amount, DEFAULT_TIP);
        transaction.sign(nodePrivateKey);
        if (addTransaction(transaction)) {
            server.sendToAll(new CreatedRestakingTransactionMessage(transaction));
            return true;
        }
        return false;
    }
}
