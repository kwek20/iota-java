package org.iota.jota.dto;

import org.iota.jota.types.Trits;

public class MamReturnSerialised extends MamResponse {
    
    //TODO: Change to bytes
    private int[] serialisedBytes;

    private Trits trits;

    public Trits getSerialisedTrits() {
        if (null != trits) {
            return trits;
        }
        
        return trits = new Trits(serialisedBytes);
    }
}
