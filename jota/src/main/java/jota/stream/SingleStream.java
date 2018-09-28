package jota.stream;

import jota.connection.Connection;
import jota.connection.IotaApi;
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

public class SingleStream extends Stream implements IotaApi {
    
    private Connection connection;

    public SingleStream(Connection connection) {
        this.connection = connection;
    }

    @Override
    public GetNodeInfoResponse getNodeInfo(IotaCommandRequest request) throws Exception {
        return connection.getNodeInfo(request);
    }

    @Override
    public GetNeighborsResponse getNeighbors(IotaCommandRequest request) throws Exception {
        return connection.getNeighbors(request);
    }

    @Override
    public AddNeighborsResponse addNeighbors(IotaNeighborsRequest request) throws Exception {
        return connection.addNeighbors(request);
    }

    @Override
    public RemoveNeighborsResponse removeNeighbors(IotaNeighborsRequest request) throws Exception {
        return connection.removeNeighbors(request);
    }

    @Override
    public GetTipsResponse getTips(IotaCommandRequest request) throws Exception {
        return connection.getTips(request);
    }

    @Override
    public FindTransactionResponse findTransactions(IotaFindTransactionsRequest request) throws Exception {
        return connection.findTransactions(request);
    }

    @Override
    public GetInclusionStateResponse getInclusionStates(IotaGetInclusionStateRequest request) throws Exception {
        return connection.getInclusionStates(request);
    }

    @Override
    public GetTrytesResponse getTrytes(IotaGetTrytesRequest request) throws Exception {
        return connection.getTrytes(request);
    }

    @Override
    public GetTransactionsToApproveResponse getTransactionsToApprove(IotaGetTransactionsToApproveRequest request)
            throws Exception {
        return connection.getTransactionsToApprove(request);
    }

    @Override
    public GetBalancesResponse getBalances(IotaGetBalancesRequest request) throws Exception {
        return connection.getBalances(request);
    }

    @Override
    public GetAttachToTangleResponse attachToTangle(IotaAttachToTangleRequest request) throws Exception {
        return connection.attachToTangle(request);
    }

    @Override
    public InterruptAttachingToTangleResponse interruptAttachingToTangle(IotaCommandRequest request) throws Exception {
        return connection.interruptAttachingToTangle(request);
    }

    @Override
    public BroadcastTransactionsResponse broadcastTransactions(IotaBroadcastTransactionRequest request)
            throws Exception {
        return connection.broadcastTransactions(request);
    }

    @Override
    public StoreTransactionsResponse storeTransactions(IotaStoreTransactionsRequest request) throws Exception {
        return connection.storeTransactions(request);
    }

    @Override
    public CheckConsistencyResponse checkConsistency(IotaCheckConsistencyRequest request) throws Exception {
        return connection.checkConsistency(request);
    }

    @Override
    public WereAddressesSpentFromResponse wereAddressesSpentFrom(IotaWereAddressesSpentFromRequest request)
            throws Exception {
        return connection.wereAddressesSpentFrom(request);
    }

    
}
