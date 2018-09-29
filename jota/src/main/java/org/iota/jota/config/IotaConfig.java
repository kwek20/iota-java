package org.iota.jota.config;

import java.util.List;

import org.iota.jota.connection.Connection;

public interface IotaConfig {

    /**
     * 
     * @return
     */
    boolean canWrite();

    /**
     * 
     * @return
     */
    List<Connection> getNodes();

    boolean hasNodes();
    
    @Deprecated
    public int getLegacyPort();
    
    @Deprecated
    public String getLegacyProtocol();
    
    @Deprecated
    public String getLegacyHost();
}
