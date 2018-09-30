package org.iota.jota;

import java.util.Arrays;

import org.iota.jota.config.IotaConfig;
import org.iota.jota.config.IotaDefaultConfig;
import org.iota.jota.config.IotaEnvConfig;
import org.iota.jota.config.IotaFileConfig;
import org.iota.jota.connection.Connection;
import org.iota.jota.connection.ConnectionFactory;
import org.iota.jota.connection.HttpConnector;
import org.iota.jota.store.IotaFileStore;
import org.iota.jota.store.IotaStore;
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
        addNode(new HttpConnector(builder.protocol, builder.host, builder.port));
        load();
    }
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value org.iota.jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * The default storage will be at {@value jota.config.IotaFileStore#DEFAULT_STORE}
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI() throws Exception {
        this.store = new IotaFileStore();
        load();
    }
    
    /**
     * Constructs a IotaAPI with a config based on environment variables or default values.
     * If no environment variable is defined, will use {@value org.iota.jota.config.IotaFileConfig#DEFAULT_CONFIG_NAME}
     * @param store The method we use for storing key/value data
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store) throws Exception {
        this.store = store;
        
        load();
    }
    
    /**
     * Constructs a IotaAPI with a config from String
     * @param store The method we use for storing key/value data
     * @param config The location of the config
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store, String config) throws Exception {
        this(store, new IotaFileConfig(config));
    }

    /**
     * Constructs a IotaAPI with config
     * @param store The method we use for storing key/value data
     * @param iotaConfig The config we load nodes from
     * @throws Exception If the config did not load for whatever reason
     */
    public IotaAPI(IotaStore store, IotaConfig iotaConfig) throws Exception {
        this.store = store;
        this.config = iotaConfig;
        
        load();
    }
    
    @Override
    protected void load() throws Exception {
        IotaEnvConfig env = new IotaEnvConfig();
        IotaDefaultConfig defaultConf = new IotaDefaultConfig();
        
        if (config == null) {
            String configName = env.getConfigName();
            
            if (configName != null) {
                config = new IotaFileConfig(configName);
            } else {
                config = new IotaFileConfig();
            }
        }
        
        IotaConfig[] array = new IotaConfig[] {
                config,
                env,
                defaultConf,
        };
        
        Arrays.stream(array).forEachOrdered(config -> {
            if (config.hasNodes()) {
                for (Connection c : config.getNodes()) {
                   addNode(c);
                }
            }
        });
            
        //Fallback on legacy option
        if (!hasNodes()) {
            Arrays.stream(array).forEachOrdered(config -> {
                if (!hasNodes()) {
                    if (config.getLegacyHost() != null) {
                        addNode(new HttpConnector(
                                config.getLegacyProtocol(), 
                                config.getLegacyHost(), 
                                config.getLegacyPort()));
                    }
                }
            });
        }   
        
        //sets a single node to service, backwards compatibility
        super.load();
    }

    public boolean addNode(Connection n) {
        try {
            n.start();
            nodes.add(n);
            log.debug("Added node: " + n.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Failed to add node connection to pool due to " + e.getMessage());
            return false;
        }
    }
    
    public static class Builder extends IotaAPICore.Builder<Builder> {
        
        private IotaStore store = new IotaFileStore();
        
        public Builder withCustomStore(IotaStore store) {
            this.store = store;
            return this;
        }

        public IotaAPI build() throws Exception {
            super.build();
            return new IotaAPI(this);
        }
    }
}
