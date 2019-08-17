package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamCreateEndpointResponse extends MamResponse {

    private Trytes endpointId;

    /**
     * @return the endpointId
     */
    public Trytes getEndpointId() {
        return endpointId;
    }
}
