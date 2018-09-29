package org.iota.jota.connection;

public interface Connection extends IotaApi {
    
    // version header
    String X_IOTA_API_VERSION_HEADER_NAME = "X-IOTA-API-Version";
    String X_IOTA_API_VERSION_HEADER_VALUE = "1";
    
    void start();
    
    void stop();
    
    int port();
    String url();
}
