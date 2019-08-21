package org.iota.jota.dto;

import org.iota.jota.types.Trytes;
import org.iota.jota.utils.Converter;

public class MamReturnSerialised extends MamResponse {

    private Trytes serialisedState;

    /**
     * Called from JNI
     * 
     * @param serialisedTrits
     */
    public void setSerialisedTrits(byte[] serialisedTrits) {
        int[] intTrits = new int[serialisedTrits.length];
        for (int i = 0; i < intTrits.length; i++) {
            intTrits[i] = serialisedTrits[i];
        }
        serialisedState = new Trytes(Converter.trytes(intTrits));
    }
    
    public Trytes getSerialisedState() {
        return serialisedState;
    }
}
