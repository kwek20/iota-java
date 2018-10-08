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
