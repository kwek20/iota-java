package jota.config;

import java.util.List;

import jota.connection.Connection;
import jota.store.Store;

public abstract class IotaClientConfig implements IotaConfig {
    
    protected Store store;

    public IotaClientConfig(Store store) {
        this.store = store;
    }

    @Override
    public boolean canWrite() {
        return store.canWrite();
    }

    @Override
    public List<Connection> getNodes() {
        // TODO Auto-generated method stub
        return null;
    }

}
