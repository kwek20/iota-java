package jota.config;

import java.util.List;

import jota.connection.Connection;

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
}
