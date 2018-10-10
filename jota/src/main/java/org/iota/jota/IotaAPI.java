package org.iota.jota;

import static org.iota.jota.utils.Constants.INVALID_INPUT_ERROR;
import static org.iota.jota.utils.Constants.INVALID_SECURITY_LEVEL_INPUT_ERROR;
import static org.iota.jota.utils.Constants.INVALID_SEED_INPUT_ERROR;
import static org.iota.jota.utils.Constants.INVALID_TRANSFERS_INPUT_ERROR;
import static org.iota.jota.utils.Constants.MESSAGE_LENGTH;
import static org.iota.jota.utils.Constants.NOT_ENOUGH_BALANCE_ERROR;
import static org.iota.jota.utils.Constants.PRIVATE_KEY_REUSE_ERROR;
import static org.iota.jota.utils.Constants.SENDING_TO_USED_ADDRESS_ERROR;
import static org.iota.jota.utils.Constants.SEND_TO_INPUTS_ERROR;
import static org.iota.jota.utils.Constants.TAG_LENGTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.iota.jota.config.IotaConfig;
import org.iota.jota.config.IotaDefaultConfig;
import org.iota.jota.config.IotaEnvConfig;
import org.iota.jota.config.IotaFileConfig;
import org.iota.jota.connection.Connection;
import org.iota.jota.connection.ConnectionFactory;
import org.iota.jota.connection.HttpConnector;
import org.iota.jota.dto.response.FindTransactionResponse;
import org.iota.jota.dto.response.GetAccountDataResponse;
import org.iota.jota.dto.response.GetBalancesAndFormatResponse;
import org.iota.jota.dto.response.GetBalancesResponse;
import org.iota.jota.dto.response.GetNewAddressResponse;
import org.iota.jota.dto.response.GetTransferResponse;
import org.iota.jota.dto.response.SendTransferResponse;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Bundle;
import org.iota.jota.model.Input;
import org.iota.jota.model.Transaction;
import org.iota.jota.model.Transfer;
import org.iota.jota.store.IotaFileStore;
import org.iota.jota.store.IotaStore;
import org.iota.jota.utils.Checksum;
import org.iota.jota.utils.InputValidator;
import org.iota.jota.utils.IotaAPIUtils;
import org.iota.jota.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IotaAPI extends IotaAPIExtended {
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    
    private IotaStore store;

    private IotaConfig config;
    
    /**
     * 
     * @throws Exception If the config did not load for whatever reason
     */
    protected IotaAPI(Builder builder) throws Exception {
        super(builder);
        
        this.store = builder.store;
        this.config = builder.config;
        
        Arrays.stream(builder.getConfigs()).forEachOrdered(config -> {
            if (config.hasNodes()) {
                for (Connection c : config.getNodes()) {
                   addNode(c);
                }
            }
        });
        
        if (null != builder.host && null != builder.protocol && 0 != builder.port) {
            addNode(new HttpConnector(builder.protocol, builder.host, builder.port));
        } else {
          //Fallback on legacy option from config
            for (IotaConfig config : builder.getConfigs()) {
                if (config.getLegacyHost() != null) {
                    addNode(new HttpConnector(
                            config.getLegacyProtocol(), 
                            config.getLegacyHost(), 
                            config.getLegacyPort()));
                    
                    break; //If we define one in config, dont check rest, its legacy after all.
                }
            }
        }
        
        log.info(this.toString());
    }
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value org.iota.jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * The default storage will be at {@value jota.config.IotaFileStore#DEFAULT_STORE}
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI() throws Exception {
        this(new Builder().generate());
    }
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value org.iota.jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * @param store The method we use for storing key/value data
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store) throws Exception {
        this(new Builder().store(store).generate());
    }
    
    /**
     * Constructs a IotaAPI with a config from String
     * @param store The method we use for storing key/value data
     * @param config The location of the config
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store, String config) throws Exception {
        this(new Builder().store(store).config(new IotaFileConfig(config)).generate());
    }

    /**
     * Constructs a IotaAPI with config
     * @param store The method we use for storing key/value data
     * @param iotaConfig The config we load nodes from
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store, IotaConfig iotaConfig) throws Exception {
        this(new Builder().store(store).config(iotaConfig).generate());
    }
    
    /**
     * Generates a new address from a seed and returns the remainderAddress.
     * This is either done deterministically, or by providing the index of the new remainderAddress.
     * <br/><br/>
     * Deprecated -> Use the new functions {@link #getNextAvailableAddress}, {@link #getAddressesUnchecked} and {@link #generateNewAddresses}
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param index     Key index to start search from. If the index is provided, the generation of the address is not deterministic.
     * @param checksum  Adds 9-tryte address checksum.
     * @param total     Total number of addresses to generate.
     * @param returnAll If <code>true</code>, it returns all addresses which were deterministically generated (until findTransactions returns null).
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    @Deprecated
    public GetNewAddressResponse getNewAddress(final String seed, int security, final int index, final boolean checksum, final int total, final boolean returnAll) throws ArgumentException {

        // If total number of addresses to generate is supplied, simply generate
        // and return the list of all addresses
        if (total != 0) {
            return getAddressesUnchecked(seed, security, checksum, index, total);
        }

        // If !returnAll return only the last address that was generated
        if (!returnAll) {
            return generateNewAddresses(seed, security, checksum, 0, 1, true);
        } else {
            return generateNewAddresses(seed, security, checksum, 0, 1, false);
        }
    }
    
    /**
     * Checks all addresses until the first unspent address is found. Starts at index 0.
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse getNextAvailableAddress(String seed, int security, boolean checksum) throws ArgumentException {
        return generateNewAddresses(seed, security, checksum, 0, 1, false);
    }
    
    /**
     * Checks all addresses until the first unspent address is found.
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @param index     Key index to start search from.
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse getNextAvailableAddress(String seed, int security, boolean checksum, int index) throws ArgumentException {
        return generateNewAddresses(seed, security, checksum, index, 1, false);
    }
    
    /**
     * Generates new addresses, meaning addresses which were not spend from, according to the connected node.
     * Starts at index 0, untill <code>amount</code> of unspent addresses are found.
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @param amount    Total number of addresses to generate.
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse generateNewAddresses(String seed, int security, boolean checksum, int amount) throws ArgumentException {
        return generateNewAddresses(seed, security, checksum, 0, amount, false);
    }
    
    /**
     * Generates new addresses, meaning addresses which were not spend from, according to the connected node.
     * Stops when <code>amount</code> of unspent addresses are found,starting from <code>index</code>
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @param index     Key index to start search from.
     * @param amount    Total number of addresses to generate.
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse generateNewAddresses(String seed, int security, boolean checksum, int index, int amount) throws ArgumentException {
        return generateNewAddresses(seed, security, checksum, 0, amount, false);
    }
    
    /**
     * Generates new addresses, meaning addresses which were not spend from, according to the connected node.
     * Stops when <code>amount</code> of unspent addresses are found,starting from <code>index</code>
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @param index     Key index to start search from.
     * @param amount    Total number of addresses to generate.
     * @param addSpendAddresses If <code>true</code>, it returns all addresses, even those who were determined to be spent from
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse generateNewAddresses(String seed, int security, boolean checksum, int index, int amount, boolean addSpendAddresses) throws ArgumentException {
        if ((!InputValidator.isValidSeed(seed))) {
            throw new IllegalStateException(INVALID_SEED_INPUT_ERROR);
        }
        
        StopWatch stopWatch = new StopWatch();
        List<String> allAddresses = new ArrayList<>();

        for (int i = index, numUnspentFound=0; numUnspentFound < amount; i++) {
            final String newAddress = IotaAPIUtils.newAddress(seed, security, i, checksum, customCurl.clone());
            final FindTransactionResponse response = findTransactionsByAddresses(newAddress);

            
            if (response.getHashes().length == 0) {
                //Unspent address
                allAddresses.add(newAddress);
                numUnspentFound++;
            } else if (addSpendAddresses) {
                //Spend address, were interested anyways
                allAddresses.add(newAddress);
            }
        }
        
        return GetNewAddressResponse.create(allAddresses, stopWatch.getElapsedTimeMili());
    }
    
    /**
     * Generates <code>amount</code> of addresses, starting from <code>index</code>
     * This does not mean that these addresses are safe to use (unspent)
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  Security level to be used for the private key / address. Can be 1, 2 or 3.
     * @param checksum  Adds 9-tryte address checksum.
     * @param index     Key index to start search from. The generation of the address is not deterministic.
     * @param amount    Total number of addresses to generate.
     * @return GetNewAddressResponse containing an array of strings with the specified number of addresses.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetNewAddressResponse getAddressesUnchecked(String seed, int security, boolean checksum, int index, int amount) throws ArgumentException {
        if ((!InputValidator.isValidSeed(seed))) {
            throw new IllegalStateException(INVALID_SEED_INPUT_ERROR);
        }
        
        StopWatch stopWatch = new StopWatch();

        List<String> allAddresses = new ArrayList<>();
        for (int i = index; i < index + amount; i++) {
            allAddresses.add(IotaAPIUtils.newAddress(seed, security, i, checksum, customCurl.clone()));
        }
        return GetNewAddressResponse.create(allAddresses, stopWatch.getElapsedTimeMili());
    }
    
    /**GetNewAddressResponse
     * @param seed            Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security        The security level of private key / seed.
     * @param start           Starting key index.
     * @param end             Ending key index.
     * @param inclusionStates If <code>true</code>, it gets the inclusion states of the transfers.
     * @return Bundle of transfers.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetTransferResponse getTransfers(String seed, int security, Integer start, Integer end, Boolean inclusionStates) throws ArgumentException {

        // validate seed
        if ((!InputValidator.isValidSeed(seed))) {
            throw new IllegalStateException(INVALID_SEED_INPUT_ERROR);
        }

        if (start > end || end > (start + 500)) {
            throw new ArgumentException(INVALID_INPUT_ERROR);
        }

        StopWatch stopWatch = new StopWatch();

        GetNewAddressResponse gnr = getAddressesUnchecked(seed, security, false, start, end-start);
        
        if (gnr != null && gnr.getAddresses() != null) {
            Bundle[] bundles = bundlesFromAddresses(gnr.getAddresses().toArray(new String[gnr.getAddresses().size()]), inclusionStates);
            return GetTransferResponse.create(bundles, stopWatch.getElapsedTimeMili());
        }
        return GetTransferResponse.create(new Bundle[]{}, stopWatch.getElapsedTimeMili());
    }
    
    /**
     * Prepares transfer by generating bundle, finding and signing inputs.
     *
     * @param seed           Tryte-encoded private key / seed.
     * @param security       The security level of private key / seed.
     * @param transfers      Array of transfer objects.
     * @param remainder      If defined, this address will be used for sending the remainder value (of the inputs) to.
     * @param inputs         The inputs.
     * @param tips           The starting points we walk back from to find the balance of the addresses
     * @param validateInputs whether or not to validate the balances of the provided inputs
     * @return Returns bundle trytes.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public List<String> prepareTransfers(String seed, int security, final List<Transfer> transfers, String remainder, List<Input> inputs, List<Transaction> tips, boolean validateInputs) throws ArgumentException {

        // validate seed
        if ((!InputValidator.isValidSeed(seed))) {
            throw new IllegalStateException(INVALID_SEED_INPUT_ERROR);
        }

        if (security < 1) {
            throw new ArgumentException(INVALID_SECURITY_LEVEL_INPUT_ERROR);
        }

        // Input validation of transfers object
        if (!InputValidator.isTransfersCollectionValid(transfers)) {
            throw new ArgumentException(INVALID_TRANSFERS_INPUT_ERROR);
        }

        // Create a new bundle
        final Bundle bundle = new Bundle();
        final List<String> signatureFragments = new ArrayList<>();

        long totalValue = 0;
        String tag = "";
        //  Iterate over all transfers, get totalValue
        //  and prepare the signatureFragments, message and tag
        for (final Transfer transfer : transfers) {

            // remove the checksum of the address if provided
            if (Checksum.isValidChecksum(transfer.getAddress())) {
                transfer.setAddress(Checksum.removeChecksum(transfer.getAddress()));
            }

            int signatureMessageLength = 1;

            // If message longer than 2187 trytes, increase signatureMessageLength (add 2nd transaction)
            if (transfer.getMessage().length() > MESSAGE_LENGTH) {

                // Get total length, message / maxLength (2187 trytes)
                signatureMessageLength += Math.floor(transfer.getMessage().length() / MESSAGE_LENGTH);

                String msgCopy = transfer.getMessage();

                // While there is still a message, copy it
                while (!msgCopy.isEmpty()) {

                    String fragment = StringUtils.substring(msgCopy, 0, MESSAGE_LENGTH);
                    msgCopy = StringUtils.substring(msgCopy, MESSAGE_LENGTH, msgCopy.length());

                    // Pad remainder of fragment

                    fragment = StringUtils.rightPad(fragment, MESSAGE_LENGTH, '9');

                    signatureFragments.add(fragment);
                }
            } else {
                // Else, get single fragment with 2187 of 9's trytes
                String fragment = transfer.getMessage();

                if (transfer.getMessage().length() < MESSAGE_LENGTH) {
                    fragment = StringUtils.rightPad(fragment, MESSAGE_LENGTH, '9');
                }
                signatureFragments.add(fragment);
            }

            tag = transfer.getTag();

            // pad for required 27 tryte length
            if (transfer.getTag().length() < TAG_LENGTH) {
                tag = StringUtils.rightPad(tag, TAG_LENGTH, '9');
            }


            // get current timestamp in seconds
            long timestamp = (long) Math.floor(Calendar.getInstance().getTimeInMillis() / 1000);

            // Add first entry to the bundle
            bundle.addEntry(signatureMessageLength, transfer.getAddress(), transfer.getValue(), tag, timestamp);
            // Sum up total value
            totalValue += transfer.getValue();
        }

        // Get inputs if we are sending tokens
        if (totalValue != 0) {

            //  Case 1: user provided inputs
            //  Validate the inputs by calling getBalances
            if (inputs != null && !inputs.isEmpty()) {

                if (!validateInputs) {
                    return addRemainder(seed, security, inputs, bundle, tag, totalValue, remainder, signatureFragments);
                }
                // Get list if addresses of the provided inputs
                List<String> inputsAddresses = new ArrayList<>();
                for (final Input i : inputs) {
                    inputsAddresses.add(i.getAddress());
                }

                List<String> tipHashes = null;
                if (tips != null) {
                    tipHashes = new ArrayList<>();
                
                    for (final Transaction tx: tips) {
                        tipHashes.add(tx.getHash());
                    }
                }

                GetBalancesResponse balancesResponse = getBalances(100, inputsAddresses, tipHashes);
                String[] balances = balancesResponse.getBalances();

                List<Input> confirmedInputs = new ArrayList<>();
                long totalBalance = 0;

                for (int i = 0; i < balances.length; i++) {
                    long thisBalance = Long.parseLong(balances[i]);

                    // If input has balance, add it to confirmedInputs
                    if (thisBalance > 0) {
                        totalBalance += thisBalance;
                        Input inputEl = inputs.get(i);
                        inputEl.setBalance(thisBalance);
                        confirmedInputs.add(inputEl);

                        // if we've already reached the intended input value, break out of loop
                        if (totalBalance >= totalValue) {
                            log.info("Total balance already reached ");
                            break;
                        }
                    }

                }

                // Return not enough balance error
                if (totalValue > totalBalance) {
                    throw new IllegalStateException(NOT_ENOUGH_BALANCE_ERROR);
                }

                return addRemainder(seed, security, confirmedInputs, bundle, tag, totalValue, remainder, signatureFragments);
            }

            //  Case 2: Get inputs deterministically
            //
            //  If no inputs provided, derive the addresses from the seed and
            //  confirm that the inputs exceed the threshold
            else {
                GetBalancesAndFormatResponse newinputs = getInputs(seed, security, 0, 0, totalValue);
                
                // If inputs with enough balance
                return addRemainder(seed, security, newinputs.getInputs(), bundle, tag, totalValue, remainder, signatureFragments);
            }
        } else {

            // If no input required, don't sign and simply finalize the bundle
            bundle.finalize(customCurl.clone());
            bundle.addTrytes(signatureFragments);

            List<Transaction> trxb = bundle.getTransactions();
            List<String> bundleTrytes = new ArrayList<>();

            for (Transaction trx : trxb) {
                bundleTrytes.add(trx.toTrytes());
            }
            Collections.reverse(bundleTrytes);
            return bundleTrytes;
        }
    }

    /**
     * Gets the inputs of a seed
     *
     * @param seed      Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security  The Security level of private key / seed.
     * @param start     Starting key index.
     * @param end       Ending key index.
     * @param threshold Min balance required.
     * @param tips      The starting points we walk back from to find the balance of the addresses
     * @throws ArgumentException is thrown when the specified input is not valid.
     **/
    public GetBalancesAndFormatResponse getInputs(String seed, int security, int start, int end, long threshold, final String... tips) throws ArgumentException {

        // validate the seed
        if ((!InputValidator.isValidSeed(seed))) {
            throw new IllegalStateException(INVALID_SEED_INPUT_ERROR);
        }

        if (security < 1) {
            throw new ArgumentException(INVALID_SECURITY_LEVEL_INPUT_ERROR);
        }

        // If start value bigger than end, return error
        // or if difference between end and start is bigger than 500 keys
        if (start > end || end > (start + 500)) {
            throw new IllegalStateException(INVALID_INPUT_ERROR);
        }

        StopWatch stopWatch = new StopWatch();

        //  Case 1: start and end
        //
        //  If start and end is defined by the user, simply iterate through the keys
        //  and call getBalances
        if (end != 0) {

            List<String> allAddresses = new ArrayList<>();

            for (int i = start; i < end; i++) {

                String address = IotaAPIUtils.newAddress(seed, security, i, false, customCurl.clone());
                allAddresses.add(address);
            }

            return getBalanceAndFormat(allAddresses, Arrays.asList(tips), threshold, start, stopWatch, security);
        }
        //  Case 2: iterate till threshold || end
        //
        //  Either start from index: 0 or start (if defined) until threshold is reached.
        //  Calls getNewAddress and deterministically generates and returns all addresses
        //  We then do getBalance, format the output and return it
        else {
            final GetNewAddressResponse res = getNewAddress(seed, security, start, false, 0, true);
            return getBalanceAndFormat(res.getAddresses(), Arrays.asList(tips), threshold, start, stopWatch, security);
        }
    }
    
    /**
     * Similar to getTransfers, just that it returns additional account data
     *
     * @param seed            Tryte-encoded seed. It should be noted that this seed is not transferred.
     * @param security        The Security level of private key / seed.
     * @param index           Key index to start search from. If the index is provided, the generation of the address is not deterministic.
     * @param checksum        Adds 9-tryte address checksum.
     * @param total           Total number of addresses to generate.
     * @param returnAll       If <code>true</code>, it returns all addresses which were deterministically generated (until findTransactions returns null).
     * @param start           Starting key index.
     * @param end             Ending key index.
     * @param inclusionStates If <code>true</code>, it gets the inclusion states of the transfers.
     * @param threshold       Min balance required.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public GetAccountDataResponse getAccountData(String seed, int security, int index, boolean checksum, int total, boolean returnAll, int start, int end, boolean inclusionStates, long threshold) throws ArgumentException {

        if (start > end || end > (start + 1000)) {
            throw new ArgumentException(INVALID_INPUT_ERROR);
        }

        StopWatch stopWatch = new StopWatch();

        GetNewAddressResponse gna = getNewAddress(seed, security, index, checksum, total, returnAll);
        GetTransferResponse gtr = getTransfers(seed, security, start, end, inclusionStates);
        GetBalancesAndFormatResponse gbr = getInputs(seed, security, start, end, threshold);

        return GetAccountDataResponse.create(gna.getAddresses(), gtr.getTransfers(), gbr.getInputs(), gbr.getTotalBalance(), stopWatch.getElapsedTimeMili());
    }
    

    /**
     * @param seed     Tryte-encoded seed
     * @param security The security level of private key / seed.
     * @param trytes   The trytes.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public void validateTransfersAddresses(String seed, int security, List<String> trytes) throws ArgumentException {

        HashSet<String> addresses = new HashSet<>();
        List<Transaction> inputTransactions = new ArrayList<>();
        List<String> inputAddresses = new ArrayList<>();

        for (String trx : trytes) {
            addresses.add(new Transaction(trx, customCurl.clone()).getAddress());
            inputTransactions.add(new Transaction(trx, customCurl.clone()));
        }

        String[] hashes = findTransactionsByAddresses(addresses.toArray(new String[addresses.size()])).getHashes();
        List<Transaction> transactions = findTransactionsObjectsByHashes(hashes);
        GetNewAddressResponse gna = getNewAddress(seed, security, 0, false, 0, true);
        GetBalancesAndFormatResponse gbr = getInputs(seed, security, 0, 0, 0);

        for (Input input : gbr.getInputs()) {
            inputAddresses.add(input.getAddress());
        }

        //check if send to input
        for (Transaction trx : inputTransactions) {
            if (trx.getValue() > 0 && inputAddresses.contains(trx.getAddress()))
                throw new ArgumentException(SEND_TO_INPUTS_ERROR);
        }

        for (Transaction trx : transactions) {

            //check if destination address is already in use
            if (trx.getValue() < 0 && !inputAddresses.contains(trx.getAddress())) {
                throw new ArgumentException(SENDING_TO_USED_ADDRESS_ERROR);
            }

            //check if key reuse
            if (trx.getValue() < 0 && gna.getAddresses().contains(trx.getAddress())) {
                throw new ArgumentException(PRIVATE_KEY_REUSE_ERROR);
            }

        }
    }
    
    /**
     * @param seed               Tryte-encoded seed.
     * @param security           The security level of private key / seed.
     * @param inputs             List of inputs used for funding the transfer.
     * @param bundle             To be populated.
     * @param tag                The tag.
     * @param totalValue         The total value.
     * @param remainderAddress   If defined, this address will be used for sending the remainder value (of the inputs) to.
     * @param signatureFragments The signature fragments.
     * @throws ArgumentException is thrown when an invalid argument is provided.
     * @throws IllegalStateException     is thrown when a transfer fails because their is not enough balance to perform the transfer.
     */
    public List<String> addRemainder(final String seed,
                                     final int security,
                                     final List<Input> inputs,
                                     final Bundle bundle,
                                     final String tag,
                                     final long totalValue,
                                     final String remainderAddress,
                                     final List<String> signatureFragments) throws ArgumentException {

        long totalTransferValue = totalValue;
        for (int i = 0; i < inputs.size(); i++) {
            long thisBalance = inputs.get(i).getBalance();
            long toSubtract = 0 - thisBalance;
            long timestamp = (long) Math.floor(Calendar.getInstance().getTimeInMillis() / 1000);

            // Add input as bundle entry
            bundle.addEntry(security, inputs.get(i).getAddress(), toSubtract, tag, timestamp);
            // If there is a remainder value
            // Add extra output to send remaining funds to

            if (thisBalance >= totalTransferValue) {
                long remainder = thisBalance - totalTransferValue;

                // If user has provided remainder address
                // Use it to send remaining funds to
                if (remainder > 0 && remainderAddress != null) {
                    // Remainder bundle entry
                    bundle.addEntry(1, remainderAddress, remainder, tag, timestamp);
                    // Final function for signing inputs
                    return IotaAPIUtils.signInputsAndReturn(seed, inputs, bundle, signatureFragments, customCurl.clone());
                } else if (remainder > 0) {
                    // Generate a new Address by calling getNewAddress

                    GetNewAddressResponse res = getNextAvailableAddress(seed, security, false, 0);
                    // Remainder bundle entry
                    bundle.addEntry(1, res.getAddresses().get(0), remainder, tag, timestamp);

                    // Final function for signing inputs
                    return IotaAPIUtils.signInputsAndReturn(seed, inputs, bundle, signatureFragments, customCurl.clone());
                } else {
                    // If there is no remainder, do not add transaction to bundle
                    // simply sign and return
                    return IotaAPIUtils.signInputsAndReturn(seed, inputs, bundle, signatureFragments, customCurl.clone());
                }

                // If multiple inputs provided, subtract the totalTransferValue by
                // the inputs balance
            } else {
                totalTransferValue -= thisBalance;
            }
        }
        throw new IllegalStateException(NOT_ENOUGH_BALANCE_ERROR);
    }

    /**
     * Wrapper function that basically does prepareTransfers, as well as attachToTangle and finally, it broadcasts and stores the transactions locally.
     *
     * @param seed               Tryte-encoded seed
     * @param security           The security level of private key / seed.
     * @param depth              The depth.
     * @param minWeightMagnitude The minimum weight magnitude.
     * @param transfers          Array of transfer objects.
     * @param inputs             List of inputs used for funding the transfer.
     * @param remainderAddress   If defined, this remainderAddress will be used for sending the remainder value (of the inputs) to.
     * @param validateInputs     Whether or not to validate the balances of the provided inputs.
     * @param validateInputAddresses  Whether or not to validate if the destination address is already used, if a key reuse is detect ot it's send to inputs.
     * @param tips               The starting points we walk back from to find the balance of the addresses
     * @return Array of valid Transaction objects.
     * @throws ArgumentException is thrown when the specified input is not valid.
     */
    public SendTransferResponse sendTransfer(String seed, int security, int depth, int minWeightMagnitude, final List<Transfer> transfers, List<Input> inputs, String remainderAddress, boolean validateInputs, boolean validateInputAddresses, final List<Transaction> tips) throws ArgumentException {

        StopWatch stopWatch = new StopWatch();

        List<String> trytes = prepareTransfers(seed, security, transfers, remainderAddress, inputs, tips, validateInputs);

        if (validateInputAddresses) {
            validateTransfersAddresses(seed, security, trytes);
        }

        String reference = tips != null && tips.size() > 0 ? tips.get(0).getHash(): null;

        List<Transaction> trxs = sendTrytes(trytes.toArray(new String[trytes.size()]), depth, minWeightMagnitude, reference);

        Boolean[] successful = new Boolean[trxs.size()];

        for (int i = 0; i < trxs.size(); i++) {
            final FindTransactionResponse response = findTransactionsByBundles(trxs.get(i).getBundle());
            successful[i] = response.getHashes().length != 0;
        }

        return SendTransferResponse.create(trxs, successful, stopWatch.getElapsedTimeMili());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("----------------------");
        builder.append(System.getProperty("line.separator"));
        builder.append("iota-java started, configured with the following: ");
        
        builder.append(System.getProperty("line.separator"));
        builder.append("Config file: " + config);
        
        builder.append(System.getProperty("line.separator"));
        builder.append("Storage file: " + store);
        
        builder.append(System.getProperty("line.separator"));
        builder.append("Registrered nodes: " + System.getProperty("line.separator"));
        for (Connection n : nodes) {
            builder.append(n.toString() + System.getProperty("line.separator"));
        }
        
        return builder.toString();
    }
    
    public static class Builder extends IotaAPICore.Builder<IotaAPI.Builder, IotaAPI> {
        
        private IotaStore store;
        
        public Builder store(IotaStore store) {
            this.store = store;
            return this;
        }

        @Override
        public Builder generate() throws Exception {
            //If a config is specified through ENV, that one will be in the stream, otherwise default config is used
            Arrays.stream(getConfigs()).forEachOrdered(config -> {
                if (config != null) {
                    //calculate IotaApi specific values
                    
                    if (null == store) {
                        store = config.getStore();
                    }
                }
            });
            
            return super.generate();
        }
        
        @Override
        protected IotaAPI compile() throws Exception {
            return new IotaAPI(this);
        }
    }
}
