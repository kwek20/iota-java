package jota;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.config.IotaConfig;
import jota.config.IotaEnvConfig;
import jota.config.IotaFileConfig;

import jota.connection.Connection;
import jota.connection.ConnectionFactory;
import jota.connection.HttpConnector;
import jota.store.IotaFileStore;
import jota.store.IotaStore;

public class NewIOTApi {
    
    private static final String DEFAULT_PORT = "14265";
    private static final String DEFAULT_PROTOCOL = "http";
    private static final String DEFAULT_HOST = "localhost";
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    
    private List<Connection> nodes = new ArrayList<>();
    
    private IotaStore store;

    private IotaConfig config;
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * The default storage will be at {@value jota.config.IotaFileStore#DEFAULT_STORE}
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi() throws Exception {
        this.store = new IotaFileStore();
        load();
    }
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * @param store The method we use for storing key/value data
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi(IotaStore store) throws Exception {
        this.store = store;
        
        load();
    }
    
    /**
     * Constructs a IotaAPI with a config from String
     * @param store The method we use for storing key/value data
     * @param config The location of the config
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi(IotaStore store, String config) throws Exception {
        this(store, new IotaFileConfig(config));
    }

    /**
     * Constructs a IotaAPI with config
     * @param store The method we use for storing key/value data
     * @param iotaConfig The config we load nodes from
     * @throws Exception If the config did not load for whatever reason
     */
    public NewIOTApi(IotaStore store, IotaConfig iotaConfig) throws Exception {
        this.store = store;
        this.config = iotaConfig;
        
        load();
    }
    
    private void load() {
        if (config == null) {
            IotaEnvConfig env = new IotaEnvConfig();
            String configName = env.getConfigName();
            
            if (configName != null) {
                this.config = new IotaFileConfig(configName);
            } else {
                this.config = new IotaFileConfig();
            }
        }
            
        if (config.hasNodes()) {
            for (Connection c : config.getNodes()) {
                if (addNode(c)) {
                    
                }
            }
        } else {
            if (addNode(getDefaultNode())) {
                
            }
        }
    }

    public boolean addNode(Connection n) {
        try {
            n.start();
            nodes.add(n);
            return true;
        } catch (Exception e) {
            log.warn("Failed to add node connection to pool due to " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasNodes() {
        return nodes != null && nodes.size() > 0;
    }
    
    public Connection getRandomNode() {
        if (!hasNodes()) return null;
        return nodes.get(new Random().nextInt(nodes.size()));
    }
    
    private Connection getDefaultNode() {
        return new HttpConnector(DEFAULT_PROTOCOL, DEFAULT_HOST, DEFAULT_PORT);
    }
}
