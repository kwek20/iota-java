package jota.config;

import jota.store.EnvironmentStore;

public class IotaEnvConfig extends IotaClientConfig {

    private static final String CONFIG_PARAM = "CONFIG";

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
}
