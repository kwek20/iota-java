package org.iota.jota.stream;

import org.iota.jota.store.IotaStore;

public class IotaStreamingApi implements StreamingApi {
    
    private IotaStore store;

    public IotaStreamingApi(IotaStore store) {
        this.store = store;
    }
}
