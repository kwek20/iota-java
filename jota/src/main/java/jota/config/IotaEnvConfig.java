package jota.config;

import jota.store.EnvironmentStore;

public class IotaEnvConfig extends IotaClientConfig {

    private static final String CONFIG_PARAM = "CONFIG";
    private static final String ENV_PROT = "IOTA_NODE_PROTOCOL";
    private static final String ENV_HOST = "IOTA_NODE_HOST";
    private static final String ENV_PORT = "IOTA_NODE_PORT";
    
    public IotaEnvConfig() {
        super(new EnvironmentStore());
    }

    @Override
    public boolean canWrite() {
        return false;
    }
    
    public String getConfigName() {
        return stringOrNull(CONFIG_PARAM);
    }
    
    @Deprecated
    public int getLegacyPort() {
        return intOrNull(ENV_PORT);
    }
    
    @Deprecated
    public String getLegacyProtocol() {
        return stringOrNull(ENV_PROT);
    }
    
    @Deprecated
    public String getLegacyHost() {
        return stringOrNull(ENV_HOST);
    }
}
