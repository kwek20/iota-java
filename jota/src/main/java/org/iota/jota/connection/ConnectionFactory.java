package org.iota.jota.connection;

import java.util.Map;

import org.iota.jota.connection.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFactory {
    //Critical
    private static final String KEY_TYPE = "type";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "host";
    
    //Others
    private static final String KEY_PORT = "port";
    private static final String KEY_PROTOCOL = "protocol";
    
    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    
    public static Connection createConnection(Map<String, String> configValues) {
        if (!preRequirements(configValues)) {
            log.error("Configuration of node missing critical sections. Required: " + 
                    KEY_TYPE + ", " + KEY_NAME + " and " + KEY_HOST);
            return null;
        }
        
        ConnectionType type = ConnectionType.valueOf(configValues.get(KEY_TYPE));
        if (type == null) return null;
        
        String host = configValues.get(KEY_HOST);
        
        try {
            switch (type) {
            case HTTP:
                int port = Integer.parseInt(KEY_PORT);
                return new HttpConnector(
                        configValues.get(KEY_PROTOCOL), 
                        host, port);
            }
        } catch (Exception e) {
            //Wrong parameters for a connection type
            log.error("Failed making a connection due to " + e.getMessage());
        }
        
        //We wont get here, any ConnectionType must have an entry in the switch
        log.error("Failed making a connection for node type " + type + " at location " + host);
        return null;
    }
    
    private static boolean preRequirements(Map<String, String> configValues) {
        return !configValues.isEmpty() 
                && configValues.containsKey(KEY_TYPE) 
                && configValues.containsKey(KEY_NAME)
                && configValues.containsKey(KEY_HOST);
    }
    
    private ConnectionFactory() {}
}
