package org.iota.jota.c.dto;

public abstract class ReturnObject {

    long returnValue = -1;
    
    public long getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(long returnValue) {
        this.returnValue = returnValue;
    }
}
