package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamWriteTagResponse extends MamResponse {

    byte[] tagTrits;
    
    private Trytes tag;
    
    public MamWriteTagResponse() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the tag
     */
    public Trytes getTag() {
        return tag;
    }

}
