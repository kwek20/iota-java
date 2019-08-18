package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamCreateEndpointResponse extends MamResponse {

    private Trytes endpointId;
    private String endpoint_id;
    
    /**
     * @return the endpointId
     */
    public Trytes getEndpointId() {
        return endpointId;
    }
}
