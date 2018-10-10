package org.iota.jota;

import static org.iota.jota.utils.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.iota.jota.config.IotaConfig;
import org.iota.jota.config.IotaDefaultConfig;
import org.iota.jota.config.IotaEnvConfig;
import org.iota.jota.config.IotaFileConfig;
import org.iota.jota.connection.Connection;
import org.iota.jota.dto.request.*;
import org.iota.jota.dto.response.*;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Transaction;
import org.iota.jota.pow.ICurl;
import org.iota.jota.pow.IotaLocalPoW;
import org.iota.jota.pow.SpongeFactory;
import org.iota.jota.utils.Checksum;
import org.iota.jota.utils.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Provides all node(IRI) core API calls, with some helpers for optional parameters.
 * Checks all parameters for correct values.
 */
public class IotaAPICore {
    
    private static final Logger log = LoggerFactory.getLogger(IotaAPICore.class);

    protected List<Connection> nodes = new ArrayList<>();
    
    protected ICurl customCurl;
    protected IotaLocalPoW localPoW;
    
    private Connection service = null;
    
    protected <T extends Builder<T, E>, E extends IotaAPICore> IotaAPICore(Builder<T, E> builder) {
        localPoW = builder.localPoW;
        customCurl = builder.customCurl;
    }
    
    public boolean hasNodes() {
        return nodes != null && nodes.size() > 0;
    }
    
    public Connection getRandomNode() {
        if (!hasNodes()) return null;
        return nodes.get(new Random().nextInt(nodes.size()));
    }
    
    public List<Connection> getNodes() {
        return nodes;
    }
    
    public boolean addNode(Connection n) {
        try {
            for (Connection c : nodes) {
                if (c.toString().equals(n.toString())) {
                    log.warn("Tried to add a node we allready have: " + n);
                    return true;
                }
            }
            
            n.start();
            
            //Huray! Lets add it
            nodes.add(n);
            log.debug("Added node: " + n.toString());
            //Legacy wants a node in service for getting ports etc
            if (null == service) service = n;
            
            return true;
        } catch (Exception e) {
            log.warn("Failed to add node connection to pool due to " + e.getMessage());
            return false;
        }
    }
    
    public ICurl getCurl() {
        return customCurl;
    }
    
    public void setCurl(ICurl localPoW) {
        this.customCurl = localPoW;
    }
    
    public IotaLocalPoW getLocalPoW() {
        return localPoW;
    }
    
    public void setLocalPoW(IotaLocalPoW localPoW) {
        this.localPoW = localPoW;
    }
    
    /**
     * Get the node information.
     *
     * @return The information about the node.
     * @throws ArgumentException 
     */
    public GetNodeInfoResponse getNodeInfo() throws ArgumentException {
        return service.getNodeInfo(IotaCommandRequest.createNodeInfoRequest());
    }

    /**
     * Get the list of neighbors from the node.
     *
     * @return The set of neighbors the node is connected with.
     * @throws ArgumentException 
     */
    public GetNeighborsResponse getNeighbors() throws ArgumentException {
        return service.getNeighbors(IotaCommandRequest.createGetNeighborsRequest());
    }

    /**
     * Add a list of neighbors to the node.
     *
     * @param uris The list of URI elements.
     * @throws ArgumentException 
     */
    public AddNeighborsResponse addNeighbors(String... uris) throws ArgumentException {
        return service.addNeighbors(IotaNeighborsRequest.createAddNeighborsRequest(uris));
    }

    /**
     * Removes a list of neighbors from the node.
     *
     * @param uris The list of URI elements.
     * @throws ArgumentException 
     */
    public RemoveNeighborsResponse removeNeighbors(String... uris) throws ArgumentException {
        return service.removeNeighbors(IotaNeighborsRequest.createRemoveNeighborsRequest(uris));
    }

    /**
     * Get the list of latest tips (unconfirmed transactions).
     *
     * @return The the list of tips.
     * @throws ArgumentException 
     */
    public GetTipsResponse getTips() throws ArgumentException {
        return service.getTips(IotaCommandRequest.createGetTipsRequest());
    }

    /**
     * Find the transactions which match the specified input.
     * Removes checksum from addresses, if supplied.
     * This data is NOT checked for hash size/trytes. Use the individual functions or check before calling.
     * 
     * @param addresses Array of address hashes
     * @param tags Array of tags, should be 27 trytes
     * @param approvees Array of approve hashes
     * @param bundles Array of bundle hashes
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException
     */
    public FindTransactionResponse findTransactions(String[] addresses, String[] tags, String[] approvees, String[] bundles) throws ArgumentException {
        
        List<String> addressesWithoutChecksum = new ArrayList<>();
        if (null != addresses) {
            for (String address : addresses) {
                String addressO = Checksum.removeChecksum(address);
                addressesWithoutChecksum.add(addressO);
            }
        }
        
        final IotaFindTransactionsRequest findTransRequest = IotaFindTransactionsRequest
                .createFindTransactionRequest()
                .byAddresses(addressesWithoutChecksum.toArray(new String[addressesWithoutChecksum.size()]))
                .byTags(tags)
                .byApprovees(approvees)
                .byBundles(bundles);
        return service.findTransactions(findTransRequest);
    }

    /**
     * Find the transactions by addresses
     *
     * @param addresses A List of addresses. Checksums are removed
     * @return The transaction hashes which are returned depend on the input.
     */
    public FindTransactionResponse findTransactionsByAddresses(final String... addresses) throws ArgumentException {
        if (!InputValidator.isStringArrayValid(addresses)) {
            throw new ArgumentException(ARRAY_NULL_OR_EMPTY);
        }
        
        if (!InputValidator.isAddressesArrayValid(addresses)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }
        
        
        return findTransactions(addresses, null, null, null);
    }

    /**
     * Find the transactions by bundles
     *
     * @param bundles A List of bundles.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactionsByBundles(final String... bundles) throws ArgumentException {
        if (!InputValidator.isStringArrayValid(bundles)) {
            throw new ArgumentException(ARRAY_NULL_OR_EMPTY);
        }
        
        if (!InputValidator.isAddressesArrayValid(bundles)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }
        
        
        return findTransactions(null, null, null, bundles);
    }

    /**
     * Find the transactions by approvees
     *
     * @param approvees A List of approvess.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactionsByApprovees(final String... approvees) throws ArgumentException {
        if (!InputValidator.isStringArrayValid(approvees)) {
            throw new ArgumentException(ARRAY_NULL_OR_EMPTY);
        }
        
        if (!InputValidator.isAddressesArrayValid(approvees)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }
        
        return findTransactions(null, null, approvees, null);
    }


    /**
     * Find the transactions by digests
     * Deprecated - Use findTransactionsByTag
     * @param digests A List of digests. Each should be 27 trytes.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    @Deprecated
    public FindTransactionResponse findTransactionsByDigests(final String... digests) throws ArgumentException {
        return findTransactionsByTags(digests);
    }
    
    /**
     * Find the transactions by tags
     * @param tags A List of tags. Each should be 27 trytes.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactionsByTags(final String... tags) throws ArgumentException {
        if (!InputValidator.isStringArrayValid(tags)) {
            throw new ArgumentException(ARRAY_NULL_OR_EMPTY);
        }
        
        if (!InputValidator.isArrayOfTrytes(tags, TAG_LENGTH)) {
            throw new ArgumentException(INVALID_TRYTES_INPUT_ERROR);
        }
        
        return findTransactions(null, tags, null, null);
    }

    /**
     * Returns the raw trytes data of a transaction.
     *
     * @param hashes The of transaction hashes of which you want to get trytes from.
     * @return The the raw transaction data (trytes) of a specific transaction.
     */
    public GetTrytesResponse getTrytes(String... hashes) throws ArgumentException {
        if (!InputValidator.isArrayOfHashes(hashes)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }
        
        return service.getTrytes(IotaGetTrytesRequest.createGetTrytesRequest(hashes));
    }

    /**
     * Get the inclusion states of a set of transactions. This is for determining if a transaction was accepted and confirmed by the network or not.
     * Search for multiple tips (and thus, milestones) to get past inclusion states of transactions.
     *
     * @param transactions The list of transactions you want to get the inclusion state for.
     * @param tips         List of tips (including milestones) you want to search for the inclusion state.
     * @return The inclusion states of a set of transactions.
     */
    public GetInclusionStateResponse getInclusionStates(String[] transactions, String[] tips) throws ArgumentException {

        if (!InputValidator.isArrayOfHashes(transactions)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        if (!InputValidator.isArrayOfHashes(tips)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }


        return service.getInclusionStates(IotaGetInclusionStateRequest
                .createGetInclusionStateRequest(transactions, tips));
    }

    /**
     * Tip selection which returns trunkTransaction and branchTransaction.
     *
     * @param depth The number of milestones to go back to determine the transactions for approval.
     * @param reference Hash of transaction to start random-walk from, used to make sure the tips returned reference a given transaction in their past.
     * @return The Tip selection which returns trunkTransaction and branchTransaction
     * @throws ArgumentException 
     */
    public GetTransactionsToApproveResponse getTransactionsToApprove(Integer depth, String reference) throws ArgumentException {
        if (depth < 0) {
            throw new ArgumentException(INVALID_APPROVE_DEPTH_ERROR);
        }
        
        return service.getTransactionsToApprove(IotaGetTransactionsToApproveRequest.createIotaGetTransactionsToApproveRequest(depth, reference));
    }

    /**
     * Tip selection which returns trunkTransaction and branchTransaction.
     * No reference transaction hash will be used
     *
     * @param depth The number of bundles to go back to determine the transactions for approval.
     * @return The Tip selection which returns trunkTransaction and branchTransaction
     * @throws ArgumentException 
     */
    public GetTransactionsToApproveResponse getTransactionsToApprove(Integer depth) throws ArgumentException {
        return getTransactionsToApprove(depth, null);
    }

    /**
     * Returns the confirmed balance, as viewed by tips, in case tips is not supplied, the balance is based on the latest confirmed milestone.
     * In addition to the balances, it also returns the referencing tips (or milestone), as well as the index with which the confirmed balance was determined. 
     * The balances is returned as a list in the same order as the addresses were provided as input.
     *
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The array list of addresses you want to get the confirmed balance from.
     * @param tips The starting points we walk back from to find the balance of the addresses
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     * @throws ArgumentException 
     */
    private GetBalancesResponse getBalances(Integer threshold, String[] addresses, String[] tips) throws ArgumentException {
        if (threshold < 0 || threshold > 100) {
            throw new ArgumentException(INVALID_THRESHOLD_ERROR);
        }
        
        if (!InputValidator.isArrayOfHashes(addresses)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }
        
        String[] addressesWithoutChecksum = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            addressesWithoutChecksum[i] = Checksum.removeChecksum(addresses[0]);
        }
        
        return service.getBalances(IotaGetBalancesRequest.createIotaGetBalancesRequest(threshold, addressesWithoutChecksum, tips));
    }

    /**
     * Returns the confirmed balance, as viewed by tips, in case tips is not supplied, the balance is based on the latest confirmed milestone.
     * In addition to the balances, it also returns the referencing tips (or milestone), as well as the index with which the confirmed balance was determined. 
     * The balances is returned as a list in the same order as the addresses were provided as input.
     *
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The list of addresses you want to get the confirmed balance from.
     * @param tips The starting points we walk back from to find the balance of the addresses, <b>optional<b>
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     * @throws ArgumentException 
     */
    public GetBalancesResponse getBalances(Integer threshold, List<String> addresses, List<String> tips) throws ArgumentException {
        String[] tipsArray = tips != null ? tips.toArray(new String[]{}) : null;
        return getBalances(threshold, addresses.toArray(new String[]{}), tipsArray);
    }
    
    /**
     * Returns the confirmed balance, the balance is based on the latest confirmed milestone.
     * In addition to the balances, it also returns the referencing tips (or milestone), as well as the index with which the confirmed balance was determined. 
     * The balances is returned as a list in the same order as the addresses were provided as input.
     * 
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The list of addresses you want to get the confirmed balance from.
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     * @throws ArgumentException 
     */
    public GetBalancesResponse getBalances(Integer threshold, List<String> addresses) throws ArgumentException {
        return getBalances(threshold, addresses, null);
    }

    /**
     * Check if a list of addresses was ever spent from, in the current epoch, or in previous epochs.
     *
     * @param addresses List of addresses to check if they were ever spent from.
     * @return The state of each address (true/false)
     * @throws ArgumentException 
     */
    public WereAddressesSpentFromResponse wereAddressesSpentFrom(String... addresses) throws ArgumentException {
        if (!InputValidator.isAddressesArrayValid(addresses)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        return service.wereAddressesSpentFrom(IotaWereAddressesSpentFromRequest.create(addresses));
    }
    
    /**
     * Checks the consistency of the subtangle formed by the provided tails.
     *
     * @param tails The tails describing the subtangle.
     * @return The The the raw transaction data (trytes) of a specific transaction.
     * @throws ArgumentException 
     */
    public CheckConsistencyResponse checkConsistency(String... tails) throws ArgumentException {
        if (!InputValidator.isArrayOfHashes(tails)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        return service.checkConsistency(IotaCheckConsistencyRequest.create(tails));
    }
    
    /**
     * Attaches the specified transactions (trytes) to the Tangle by doing Proof of Work.
     * Uses the max number represented by 27 trits as the upper bound of attachment timestamp
     * 
     * @param trunkTransaction The trunk transaction to approve.
     * @param branchTransaction The branch transaction to approve.
     * @param minWeightMagnitude The Proof of Work intensity.
     * @param trytes A List of trytes (raw transaction data) to attach to the tangle.
     * @return
     * @throws ArgumentException 
     */
    public GetAttachToTangleResponse attachToTangle(String trunkTransaction, String branchTransaction, Integer minWeightMagnitude, String... trytes) throws ArgumentException {
        return attachToTangle(trunkTransaction, branchTransaction, minWeightMagnitude, TRANSACTION_UPPER_BOUND_MAX, trytes);
    }

    /**
     * Attaches the specified transactions (trytes) to the Tangle by doing Proof of Work.
     *
     * @param trunkTransaction The trunk transaction to approve.
     * @param branchTransaction The branch transaction to approve.
     * @param minWeightMagnitude The Proof of Work intensity.
     * @param timestampUpperBound Max timestamp used for attaching this transaction
     * @param trytes A List of trytes (raw transaction data) to attach to the tangle.
     * @return
     * @throws ArgumentException 
     */
    public GetAttachToTangleResponse attachToTangle(String trunkTransaction, String branchTransaction, Integer minWeightMagnitude, long timestampUpperBound, String... trytes) throws ArgumentException {

        if (!InputValidator.isHash(trunkTransaction)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        if (!InputValidator.isHash(branchTransaction)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        if (!InputValidator.isArrayOfRawTransactionTrytes(trytes)) {
            throw new ArgumentException(INVALID_TRYTES_INPUT_ERROR);
        }

        if (customCurl != null) {
            final String[] resultTrytes = new String[trytes.length];
            String previousTransaction = null;
            for (int i = 0; i < trytes.length; i++) {
                Transaction txn = new Transaction(trytes[i]);
                txn.setTrunkTransaction(previousTransaction == null ? trunkTransaction : previousTransaction);
                txn.setBranchTransaction(previousTransaction == null ? branchTransaction : trunkTransaction);
                if (txn.getTag().isEmpty() || txn.getTag().matches("9*"))
                    txn.setTag(txn.getObsoleteTag());
                txn.setAttachmentTimestamp(System.currentTimeMillis());
                txn.setAttachmentTimestampLowerBound(0);
                txn.setAttachmentTimestampUpperBound(3_812_798_742_493L);
                resultTrytes[i] = localPoW.performPoW(txn.toTrytes(), minWeightMagnitude);
                previousTransaction = new Transaction(resultTrytes[i]).getHash();
            }
            return new GetAttachToTangleResponse(resultTrytes);
        }

        return service.attachToTangle(IotaAttachToTangleRequest.createAttachToTangleRequest(trunkTransaction, branchTransaction, minWeightMagnitude, trytes));
    }

    /**
     * Interrupts and completely aborts the attachToTangle process.
     * @return
     * @throws ArgumentException 
     */
    public InterruptAttachingToTangleResponse interruptAttachingToTangle() throws ArgumentException {
        return service.interruptAttachingToTangle(IotaCommandRequest.createInterruptAttachToTangleRequest());
    }

    /**
     * Broadcast a list of transactions to all neighbors. The input trytes for this call are provided by attachToTangle.
     * @param trytes trytes The list of raw data of transactions to be rebroadcast.
     * @return
     * @throws ArgumentException
     */
    public BroadcastTransactionsResponse broadcastTransactions(String... trytes) throws ArgumentException {

        if (!InputValidator.isArrayOfRawTransactionTrytes(trytes)) {
            throw new ArgumentException(INVALID_ATTACHED_TRYTES_INPUT_ERROR);
        }

        return service.broadcastTransactions(IotaBroadcastTransactionRequest.createBroadcastTransactionsRequest(trytes));
        
    }

    /**
     * Store transactions into the nodes local storage. The trytes to be used for this call are returned by attachToTangle.
     *
     * @param trytes The list of raw data of transactions to be stored.
     * @throws ArgumentException 
     */
    public StoreTransactionsResponse storeTransactions(String... trytes) throws ArgumentException {
        if (!InputValidator.isArrayOfRawTransactionTrytes(trytes)) {
            throw new ArgumentException(INVALID_ATTACHED_TRYTES_INPUT_ERROR);
        }
        
        return service.storeTransactions(IotaStoreTransactionsRequest.createStoreTransactionsRequest(trytes));
    }

    /**
     * Gets the protocol.
     * Deprecated - Nodes could not have a protocol. Get specific connection and check url
     * @return The protocol to use when connecting to the remote node.
     */
    @Deprecated
    public String getProtocol() {
        //Should be carefull, its still possible to not display the protocol if url doesn't contain :
        //Will never break because a split on not found character returns the entire string in [0]
        return service.url().split(":")[0];
    }

    /**
     * Gets the host.
     * Deprecated - Nodes could not have a host. Get specific connection and check url
     * @return The host you want to connect to.
     */
    @Deprecated
    public String getHost() {
        return service.url().split("://")[1];
    }

    /**
     * Gets the port.
     * Deprecated - Get specific connection and check port
     * @return The port of the host you want to connect to.
     */
    @Deprecated
    public String getPort() {
        return service.port() + "";
    }
   
    //All casts are to T, and are okay unless you do really weird things.
    //Warnings are annoying
    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T, E>, E extends IotaAPICore> {
        String protocol, host;
        int port;
        IotaLocalPoW localPoW;
        
        IotaConfig config;
        
        private ICurl customCurl = SpongeFactory.create(SpongeFactory.Mode.KERL);
        
        public E build() throws Exception {
            generate();
            return compile();
        }
        
        protected T generate() throws Exception {
            Arrays.stream(getConfigs()).forEachOrdered(config -> {
                if (config != null) {
                    if (null == protocol) {
                        protocol = config.getLegacyProtocol();
                    }
    
                    if (null == host) {
                        host = config.getLegacyHost();
                    }
    
                    if (0 == port) {
                        port = config.getLegacyPort();
                    }
                }
            });
            return (T) this;
        }
        
        protected IotaConfig[] getConfigs() throws Exception {
            IotaEnvConfig env = new IotaEnvConfig();
            
            if (config == null) {
                String configName = env.getConfigName();
                
                if (configName != null) {
                    config = new IotaFileConfig(configName);
                } else {
                    config = new IotaFileConfig();
                }
            }
            
            return new IotaConfig[] {
                    config,
                    env,
                    new IotaDefaultConfig(),
            };
        }

        /**
         * Separated function so we don't generate 2 object instances (IotaAPICore and IotaApi)
         * @return a filled IotaAPICore
         */
        protected E compile() throws Exception {
            return (E) new IotaAPICore(this);
        }
        
        public T withCustomCurl(ICurl curl) {
            customCurl = curl;
            return (T) this;
        }

        public T config(IotaConfig properties) {
            config = properties;
            return (T) this;
        }

        public T host(String host) {
            this.host = host;
            return (T) this;
        }

        public T port(int port) {
            this.port = port;
            return (T) this;
        }

        public T protocol(String protocol) {
            this.protocol = protocol;
            return (T) this;
        }

        public T localPoW(IotaLocalPoW localPoW) {
            this.localPoW = localPoW;
            return (T) this;
        }
    }
}
