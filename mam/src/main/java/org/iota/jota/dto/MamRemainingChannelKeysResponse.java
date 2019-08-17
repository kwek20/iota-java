package org.iota.jota.dto;

public class MamRemainingChannelKeysResponse extends MamResponse {

    private long numChannelKeys;
    
    public MamRemainingChannelKeysResponse() {
        
    }
    
    public long getNumChannelKeys() {
        return numChannelKeys;
    }

    public void setNumChannelKeys(long numChannelKeys) {
        this.numChannelKeys = numChannelKeys;
    }
}
