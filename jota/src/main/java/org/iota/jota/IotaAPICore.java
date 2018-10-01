package org.iota.jota;

import static org.iota.jota.utils.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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

public class IotaAPICore {
    
    private static final Logger log = LoggerFactory.getLogger(IotaAPICore.class);

    protected List<Connection> nodes = new ArrayList<>();
    
    protected ICurl customCurl;
    protected IotaLocalPoW localPoW;
    
    private Connection service = null;
    
    protected IotaAPICore() {
        
    }
    
    protected IotaAPICore(Builder builder) {
        localPoW = builder.localPoW;
        customCurl = builder.customCurl;
    }
    
    protected void load() throws Exception {
        service = getRandomNode();
        setCurl( SpongeFactory.create(SpongeFactory.Mode.KERL));
    }
    
    public boolean hasNodes() {
        return nodes != null && nodes.size() > 0;
    }
    
    public Connection getRandomNode() {
        if (!hasNodes()) return null;
        return nodes.get(new Random().nextInt(nodes.size()));
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
     * Find the transactions which match the specified input
     *
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactions(String[] addresses, String[] tags, String[] approvees, String[] bundles) throws ArgumentException {

        final IotaFindTransactionsRequest findTransRequest = IotaFindTransactionsRequest
                .createFindTransactionRequest()
                .byAddresses(addresses)
                .byTags(tags)
                .byApprovees(approvees)
                .byBundles(bundles);

        return service.findTransactions(findTransRequest);
    }

    /**
     * Find the transactions by addresses
     *
     * @param addresses A List of addresses.
     * @return The transaction hashes which are returned depend on the input.
     */
    public FindTransactionResponse findTransactionsByAddresses(final String... addresses) throws ArgumentException {
        List<String> addressesWithoutChecksum = new ArrayList<>();

        for (String address : addresses) {
            String addressO = Checksum.removeChecksum(address);
            addressesWithoutChecksum.add(addressO);
        }

        return findTransactions(addressesWithoutChecksum.toArray(new String[addressesWithoutChecksum.size()]), null, null, null);
    }

    /**
     * Find the transactions by bundles
     *
     * @param bundles A List of bundles.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactionsByBundles(final String... bundles) throws ArgumentException {
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
        return findTransactions(null, null, approvees, null);
    }


    /**
     * Find the transactions by digests
     *
     * @param digests A List of digests.
     * @return The transaction hashes which are returned depend on the input.
     * @throws ArgumentException 
     */
    public FindTransactionResponse findTransactionsByDigests(final String... digests) throws ArgumentException {
        return findTransactions(null, digests, null, null);
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
     * Tip selection which returns trunkTransaction and branchTransaction.
     *
     * @param depth The number of bundles to go back to determine the transactions for approval.
     * @param reference Hash of transaction to start random-walk from, used to make sure the tips returned reference a given transaction in their past.
     * @return The Tip selection which returns trunkTransaction and branchTransaction
     * @throws ArgumentException 
     */
    public GetTransactionsToApproveResponse getTransactionsToApprove(Integer depth, String reference) throws ArgumentException {

        return service.getTransactionsToApprove(IotaGetTransactionsToApproveRequest.createIotaGetTransactionsToApproveRequest(depth, reference));
    }

    /**
     * {@link #getTransactionsToApprove(Integer, String)}
     * @throws ArgumentException 
     */
    public GetTransactionsToApproveResponse getTransactionsToApprove(Integer depth) throws ArgumentException {
        return getTransactionsToApprove(depth, null);
    }

    /**
     * Similar to getInclusionStates.
     *
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The array list of addresses you want to get the confirmed balance from.
     * @param tips The starting points we walk back from to find the balance of the addresses
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     * @throws ArgumentException 
     */
    private GetBalancesResponse getBalances(Integer threshold, String[] addresses, String[] tips) throws ArgumentException {
        return service.getBalances(IotaGetBalancesRequest.createIotaGetBalancesRequest(threshold, addresses, tips));
    }

    /**
     * Similar to getInclusionStates.
     *
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The list of addresses you want to get the confirmed balance from.
     * @param tips The starting points we walk back from to find the balance of the addresses
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     */
    public GetBalancesResponse getBalances(Integer threshold, List<String> addresses, List<String> tips) throws ArgumentException {

        List<String> addressesWithoutChecksum = new ArrayList<>();

        for (String address : addresses) {
            String addressO = Checksum.removeChecksum(address);
            addressesWithoutChecksum.add(addressO);
        }
        String[] tipsArray = tips != null ? tips.toArray(new String[]{}) : null;
        return getBalances(threshold, addressesWithoutChecksum.toArray(new String[]{}), tipsArray);
    }
    
    /**
     * Similar to getInclusionStates.
     *
     * @param threshold The confirmation threshold, should be set to 100.
     * @param addresses The list of addresses you want to get the confirmed balance from.
     * @return The confirmed balance which a list of addresses have at the latest confirmed milestone.
     */
    public GetBalancesResponse getBalances(Integer threshold, List<String> addresses) throws ArgumentException {
        return getBalances(threshold, addresses, null);
    }

    /**
     * Check if a list of addresses was ever spent from, in the current epoch, or in previous epochs.
     *
     * @param addresses List of addresses to check if they were ever spent from.
     * @return The state of each address (true/false)
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
     */
    public CheckConsistencyResponse checkConsistency(String... tails) throws ArgumentException {
        if (!InputValidator.isArrayOfHashes(tails)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        return service.checkConsistency(IotaCheckConsistencyRequest.create(tails));
    }


    /**
     * Attaches the specified transactions (trytes) to the Tangle by doing Proof of Work.
     *
     * @param trunkTransaction The trunk transaction to approve.
     * @param branchTransaction The branch transaction to approve.
     * @param minWeightMagnitude The Proof of Work intensity.
     * @param trytes A List of trytes (raw transaction data) to attach to the tangle.
     */
    public GetAttachToTangleResponse attachToTangle(String trunkTransaction, String branchTransaction, Integer minWeightMagnitude, String... trytes) throws ArgumentException {

        if (!InputValidator.isHash(trunkTransaction)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        if (!InputValidator.isHash(branchTransaction)) {
            throw new ArgumentException(INVALID_HASHES_INPUT_ERROR);
        }

        if (!InputValidator.isArrayOfTrytes(trytes)) {
            throw new ArgumentException(INVALID_TRYTES_INPUT_ERROR);
        }

        if (customCurl != null) {
            System.out.println("Doing local PoW!");
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
     * @throws ArgumentException 
     */
    public InterruptAttachingToTangleResponse interruptAttachingToTangle() throws ArgumentException {
        return service.interruptAttachingToTangle(IotaCommandRequest.createInterruptAttachToTangleRequest());
    }

    /**
     * Broadcast a list of transactions to all neighbors. The input trytes for this call are provided by attachToTangle.
     *
     * @param trytes The list of raw data of transactions to be rebroadcast.
     */
    public BroadcastTransactionsResponse broadcastTransactions(String... trytes) throws ArgumentException {

        if (!InputValidator.isArrayOfAttachedTrytes(trytes)) {
            throw new ArgumentException(INVALID_ATTACHED_TRYTES_INPUT_ERROR);
        }

        return service.broadcastTransactions(IotaBroadcastTransactionRequest.createBroadcastTransactionsRequest(trytes));
        
    }

    /**
     * Store transactions into the local storage. The trytes to be used for this call are returned by attachToTangle.
     *
     * @param trytes The list of raw data of transactions to be rebroadcast.
     * @throws ArgumentException 
     */
    public StoreTransactionsResponse storeTransactions(String... trytes) throws ArgumentException {
        return service.storeTransactions(IotaStoreTransactionsRequest.createStoreTransactionsRequest(trytes));
    }

    /**
     * Gets the protocol.
     *
     * @return The protocol to use when connecting to the remote node.
     */
    public String getProtocol() {
        //Should be carefull, its still possible to not display the protocol if url doesn't contain :
        //Will never break because a split on not found character returns the entire string in [0]
        return service.url().split(":")[0];
    }

    /**
     * Gets the host.
     *
     * @return The host you want to connect to.
     */
    public String getHost() {
        return service.url().split("://")[1];
    }

    /**
     * Gets the port.
     *
     * @return The port of the host you want to connect to.
     */
    public String getPort() {
        return service.port() + "";
    }
   
    public static class Builder<T extends Builder<T>> {
        String protocol, host;
        int port;
        IotaLocalPoW localPoW;
        
        IotaConfig config;
        
        private ICurl customCurl = SpongeFactory.create(SpongeFactory.Mode.KERL);
        
        public IotaAPICore build() throws Exception {
            if (config == null){
                config = new IotaFileConfig();
            }
            
            // resolution order: builder value, configuration file, env, default value
            Stream<IotaConfig> stream = Arrays.stream(new IotaConfig[] {
                    config,
                    new IotaEnvConfig(),
                    new IotaDefaultConfig(),
            });
            
            //TODO set options for all settings
            stream.forEachOrdered(config -> {
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
            

            return new IotaAPICore(this);
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
