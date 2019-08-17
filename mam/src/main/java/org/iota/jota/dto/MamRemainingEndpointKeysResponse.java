package org.iota.jota.dto;

public class MamRemainingEndpointKeysResponse extends MamResponse {
    
    private long numEndpointKeys;

    /**
     * @return the numEndpointKeys
     */
    public long getNumEndpointKeys() {
        return numEndpointKeys;
    }
    
    public void setNumEndpointKeys(long numEndpointKeys) {
        this.numEndpointKeys = numEndpointKeys;
    }
}
