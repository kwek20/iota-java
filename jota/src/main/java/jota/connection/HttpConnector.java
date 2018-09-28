package jota.connection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.IotaAPICore;
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
import jota.error.ArgumentException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpConnector implements Connection {
    private String protocol;
    private String host;
    private int port;
    
    private IotaAPIHTTPService service;
    
    private static final Logger log = LoggerFactory.getLogger(IotaAPICore.class);

    public HttpConnector(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public int port() {
        return port;
    }
    
    @Override
    public String url() {
        return host;
    }
    
    public void start() {
        final String nodeUrl = protocol + "://" + host + ":" + port;

        // Create OkHttpBuilder
        final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request newRequest;

                        newRequest = request.newBuilder()
                                .addHeader(X_IOTA_API_VERSION_HEADER_NAME, X_IOTA_API_VERSION_HEADER_VALUE)
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .connectTimeout(5000, TimeUnit.SECONDS)
                .build();

        // use client to create Retrofit service
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(nodeUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(IotaAPIHTTPService.class);
    }
    
    public void stop() {
        //does nothing
    }
    
    protected static <T> Response<T> wrapCheckedException(final Call<T> call) throws ArgumentException, IllegalStateException, IllegalAccessError {
        try {
            final Response<T> res = call.execute();

            String error = null;

            if (res.errorBody() != null) {
                error = res.errorBody().string();
            }

            if (res.code() == 400) {
                throw new ArgumentException(error);

            } else if (res.code() == 401) {
                throw new IllegalAccessError("401 " + error);
            } else if (res.code() == 500) {
                throw new IllegalAccessError("500 " + error);
            } else if (error != null) {
                //Unknown error
            }
            
            return res;
        } catch (IOException e) {
            log.error("Execution of the API call raised exception. IOTA Node not reachable?", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public GetNodeInfoResponse getNodeInfo(IotaCommandRequest request) throws ArgumentException {
        final Call<GetNodeInfoResponse> res = service.getNodeInfo(IotaCommandRequest.createNodeInfoRequest());
        return wrapCheckedException(res).body();
    }

    @Override
    public GetNeighborsResponse getNeighbors(IotaCommandRequest request) throws ArgumentException {
        final Call<GetNeighborsResponse> res = service.getNeighbors(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public AddNeighborsResponse addNeighbors(IotaNeighborsRequest request) throws ArgumentException {
        final Call<AddNeighborsResponse> res = service.addNeighbors(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public RemoveNeighborsResponse removeNeighbors(IotaNeighborsRequest request) throws ArgumentException {
        final Call<RemoveNeighborsResponse> res = service.removeNeighbors(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetTipsResponse getTips(IotaCommandRequest request) throws ArgumentException {
        final Call<GetTipsResponse> res = service.getTips(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public FindTransactionResponse findTransactions(IotaFindTransactionsRequest request) throws ArgumentException {
        final Call<FindTransactionResponse> res = service.findTransactions(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetInclusionStateResponse getInclusionStates(IotaGetInclusionStateRequest request) throws ArgumentException {
        final Call<GetInclusionStateResponse> res = service.getInclusionStates(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetTrytesResponse getTrytes(IotaGetTrytesRequest request) throws ArgumentException {
        final Call<GetTrytesResponse> res = service.getTrytes(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetTransactionsToApproveResponse getTransactionsToApprove(IotaGetTransactionsToApproveRequest request) throws ArgumentException {
        final Call<GetTransactionsToApproveResponse> res = service.getTransactionsToApprove(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetBalancesResponse getBalances(IotaGetBalancesRequest request) throws ArgumentException {
        final Call<GetBalancesResponse> res = service.getBalances(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public GetAttachToTangleResponse attachToTangle(IotaAttachToTangleRequest request) throws ArgumentException {
        final Call<GetAttachToTangleResponse> res = service.attachToTangle(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public InterruptAttachingToTangleResponse interruptAttachingToTangle(IotaCommandRequest request) throws ArgumentException {
        final Call<InterruptAttachingToTangleResponse> res = service.interruptAttachingToTangle(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public BroadcastTransactionsResponse broadcastTransactions(IotaBroadcastTransactionRequest request) throws ArgumentException {
        final Call<BroadcastTransactionsResponse> res = service.broadcastTransactions(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public StoreTransactionsResponse storeTransactions(IotaStoreTransactionsRequest request) throws ArgumentException {
        final Call<StoreTransactionsResponse> res = service.storeTransactions(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public CheckConsistencyResponse checkConsistency(IotaCheckConsistencyRequest request) throws ArgumentException {
        final Call<CheckConsistencyResponse> res = service.checkConsistency(request);
        return wrapCheckedException(res).body();
    }

    @Override
    public WereAddressesSpentFromResponse wereAddressesSpentFrom(IotaWereAddressesSpentFromRequest request) throws ArgumentException {
        final Call<WereAddressesSpentFromResponse> res = service.wereAddressesSpentFrom(request);
        return wrapCheckedException(res).body();
    }

}
