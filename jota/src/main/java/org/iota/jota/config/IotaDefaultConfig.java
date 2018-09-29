package org.iota.jota.config;

public class IotaDefaultConfig extends IotaClientConfig {

    public IotaDefaultConfig() {
        super(null);
    }

    @Override
    public int getLegacyPort() {
        return 14265;
    }

    @Override
    public String getLegacyProtocol() {
        return "http";
    }

    @Override
    public String getLegacyHost() {
        return "localhost";
    }
    
    @Override
    public boolean canWrite() {
        return false;
    }

}
