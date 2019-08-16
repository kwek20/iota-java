package org.iota.jota.dto;

public abstract class MamResponse {

    long returnValue = -1;
    
    public long getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(long returnValue) {
        this.returnValue = returnValue;
    }
}
