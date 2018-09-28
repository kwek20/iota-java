package jota.connection;

import jota.dto.request.IotaAttachToTangleRequest;
import jota.dto.request.IotaBroadcastTransactionRequest;
import jota.dto.request.IotaCheckConsistencyRequest;
import jota.dto.request.IotaCommandRequest;
import jota.dto.request.IotaFindTransactionsRequest;
import jota.dto.request.IotaGetBalancesRequest;
import jota.dto.request.IotaGetInclusionStateRequest;
import jota.dto.request.IotaGetTransactionsToApproveRequest;
import jota.dto.request.IotaGetTrytesRequest;
import jota.dto.request.IotaNeighborsRequest;
import jota.dto.request.IotaStoreTransactionsRequest;
import jota.dto.request.IotaWereAddressesSpentFromRequest;
import jota.dto.response.AddNeighborsResponse;
import jota.dto.response.BroadcastTransactionsResponse;
import jota.dto.response.CheckConsistencyResponse;
import jota.dto.response.FindTransactionResponse;
import jota.dto.response.GetAttachToTangleResponse;
import jota.dto.response.GetBalancesResponse;
import jota.dto.response.GetInclusionStateResponse;
import jota.dto.response.GetNeighborsResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.GetTipsResponse;
import jota.dto.response.GetTransactionsToApproveResponse;
import jota.dto.response.GetTrytesResponse;
import jota.dto.response.InterruptAttachingToTangleResponse;
import jota.dto.response.RemoveNeighborsResponse;
import jota.dto.response.StoreTransactionsResponse;
import jota.dto.response.WereAddressesSpentFromResponse;

public interface IotaApi {
    

    /**
     * Returns information about the node.
     * @throws Exception 
     */
    GetNodeInfoResponse getNodeInfo(IotaCommandRequest request) throws Exception;

    /**
     * Get the list of neighbors from the node.
     * @throws Exception 
     */
    GetNeighborsResponse getNeighbors(IotaCommandRequest request) throws Exception;

    /**
     * Add a list of neighbors to the node.
     * @throws Exception 
     */
    AddNeighborsResponse addNeighbors(IotaNeighborsRequest request) throws Exception;

    /**
     * Removes a list of neighbors from the node.
     * @throws Exception 
     */
    RemoveNeighborsResponse removeNeighbors(IotaNeighborsRequest request) throws Exception;

    /**
     * Get the list of latest tips (unconfirmed transactions).
     * @throws Exception 
     */
    GetTipsResponse getTips(IotaCommandRequest request) throws Exception;

    /**
     * Find the transactions which match the specified input and return.
     * @throws Exception 
     */
    FindTransactionResponse findTransactions(IotaFindTransactionsRequest request) throws Exception;


    /**
     * Get the inclusion states of a set of transactions. This is for determining if a transaction was accepted and confirmed by the network or not.
     * You can search for multiple tips (and thus, milestones) to get past inclusion states of transactions.
     * @throws Exception 
     */
    GetInclusionStateResponse getInclusionStates(IotaGetInclusionStateRequest request) throws Exception;

    /**
     * Returns the raw trytes data of a transaction.
     * @throws Exception 
     */
    GetTrytesResponse getTrytes(IotaGetTrytesRequest request) throws Exception;

    /**
     * Tip selection which returns trunkTransaction and branchTransaction.
     * The input value is the latest coordinator milestone, as provided through the getNodeInfo API call.
     * @throws Exception 
     */
    GetTransactionsToApproveResponse getTransactionsToApprove(IotaGetTransactionsToApproveRequest request) throws Exception;

    /**
     * It returns the confirmed balance which a list of addresses have at the latest confirmed milestone.
     * @throws Exception 
     */
    GetBalancesResponse getBalances(IotaGetBalancesRequest request) throws Exception;

    /**
     * Attaches the specified transactions (trytes) to the Tangle by doing Proof of Work.
     * @throws Exception 
     */
    GetAttachToTangleResponse attachToTangle(IotaAttachToTangleRequest request) throws Exception;

    /**
     * Interrupts and completely aborts the attachToTangle process.
     * @throws Exception 
     */
    InterruptAttachingToTangleResponse interruptAttachingToTangle(IotaCommandRequest request) throws Exception;

    /**
     * Broadcast a list of transactions to all neighbors. The input trytes for this call are provided by attachToTangle.
     * @throws Exception 
     */
    BroadcastTransactionsResponse broadcastTransactions(IotaBroadcastTransactionRequest request) throws Exception;

    /**
     * Store transactions into the local storage. The trytes to be used for this call are returned by attachToTangle.
     * @throws Exception 
     */
    StoreTransactionsResponse storeTransactions(IotaStoreTransactionsRequest request) throws Exception;

    /**
     * Checks the consistency of the subtangle descirbed by the provided tails.
     * @throws Exception 
     */
    CheckConsistencyResponse checkConsistency(IotaCheckConsistencyRequest request) throws Exception;
    
    /**
     * Check if a list of addresses was ever spent from, in the current epoch, or in previous epochs.
     * @throws Exception 
    */
    WereAddressesSpentFromResponse wereAddressesSpentFrom(IotaWereAddressesSpentFromRequest request) throws Exception;
}
