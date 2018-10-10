package org.iota.jota.stream;

import org.iota.jota.store.IotaStore;

public class StreamingApi {

    private IotaStore store;

    public StreamingApi(IotaStore store) {
        this.store = store;
    }
}
