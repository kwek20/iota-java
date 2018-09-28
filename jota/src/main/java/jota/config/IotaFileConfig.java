package jota.config;

import java.io.File;
import java.util.Optional;

import jota.store.FlatFileStore;

public class IotaFileConfig extends IotaClientConfig {

    private static final String DEFAULT_CONFIG_NAME = ".." + File.pathSeparator + "node_config.properties";
    
    public IotaFileConfig() {
        super(new FlatFileStore(DEFAULT_CONFIG_NAME));
    }

    public IotaFileConfig(String url) {
        super(new FlatFileStore(url));
    }
    
    public IotaFileConfig(Optional<String> url) {
        super(new FlatFileStore(url.isPresent() ? url.get() : DEFAULT_CONFIG_NAME));
    }
}
