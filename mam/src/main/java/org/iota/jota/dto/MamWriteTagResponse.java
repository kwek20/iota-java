package org.iota.jota.dto;

import java.util.Arrays;

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
        int[] trits = Converter.bytesToTrits(tagTrits);
        System.out.println(Arrays.toString(trits));
        tag = new Trytes(Converter.trytes(trits));
    }
}
