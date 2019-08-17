package org.iota.jota.dto;

import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trits;

public abstract class MamResponseBundleMessage extends MamResponse {

    private Bundle bundle;
    
    // TODO make bytes
    private int[] tritsId;
    private Trits messageId;
    
    /**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * @return the messageId
     */
    public Trits getMessageId() {
        if (null != messageId) {
            return messageId;
        }
        
        return messageId = new Trits(tritsId);
    }
}