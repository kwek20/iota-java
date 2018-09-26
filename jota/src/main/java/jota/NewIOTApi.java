package jota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.connection.Connection;
import jota.connection.ConnectionFactory;
import jota.store.FlatFileStore;
import jota.store.Store;

public class NewIOTApi {
    
    private static final String DEFAULT_CONFIG_NAME = "../node_config.properties";
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    
    private List<Connection> nodes;
    
    private boolean started = false;

    private Store store;
    
    /**
     * Constructs a IotaAPI with a config from {@value #DEFAULT_CONFIG_NAME}
     * @param store The method we use for storing key/value data
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi(Store store) throws Exception {
        this(store, DEFAULT_CONFIG_NAME);
    }
    
    /**
     * Constructs a IotaAPI with a config from String
     * @param store The method we use for storing key/value data
     * @param config The location of the config
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi(Store store, String config) throws Exception {
        this.store = store;
        Store configStore = new FlatFileStore(config);
        configStore.load();
        
        this.nodes = loadNodesFromConfig(configStore);
    }

    public NewIOTApi(Store store, Store config) {
        this(store, loadNodesFromConfig(config));
    }
    
    public NewIOTApi(Store store, Connection... nodes) {
        this(store, Arrays.asList(nodes));
    }
    
    public NewIOTApi(Store store, List<Connection> nodes) {
        this.nodes = nodes;
        this.store = store;
    }
    
    private static List<Connection> loadNodesFromConfig(Store config) {
        
        return null;
    }

    public boolean addNode(Connection n) {
        try {
            if (started) n.start();
            nodes.add(n);
            return true;
        } catch (Exception e) {
            log.warn("Failed to add node connection to pool due to " + e.getMessage());
            return false;
        }
    }
    
    private class Builder {
        public Builder(Store config) {
            
        }
    }
}
