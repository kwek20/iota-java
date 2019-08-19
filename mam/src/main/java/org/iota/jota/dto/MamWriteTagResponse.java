package org.iota.jota.dto;

import org.iota.jota.types.Trytes;
import org.iota.jota.utils.Converter;

public class MamWriteTagResponse extends MamResponse {
    
    private Trytes tag;

    /**
     * @return the tag
     */
    public Trytes getTag() {
        return tag;
    }

    /**
     * Called from JNI
     * 
     * @param tagTrits
     */
    public void setByteTag(byte[] tagTrits) {
        int[] intTrits = new int[tagTrits.length];
        for (int i = 0; i < intTrits.length; i++) {
            intTrits[i] = tagTrits[i];
        }
        tag = new Trytes(Converter.trytes(intTrits));
    }
}
