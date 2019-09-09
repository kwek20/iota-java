package org.iota.jota.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.iota.jota.ReturnCode;

public class MamResponse {

    long returnValue = -1;
    
    public long getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(long returnValue) {
        this.returnValue = returnValue;
    }
    
    public ReturnCode getReturnCode(){
        if (returnValue > Integer.MAX_VALUE) {
            return null;
        } else if (returnValue == -1) {
            //we didnt get a return code
            return ReturnCode.MISSING;
        }
        
        return ReturnCode.values()[(int) (returnValue % ReturnCode.values().length)];
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
