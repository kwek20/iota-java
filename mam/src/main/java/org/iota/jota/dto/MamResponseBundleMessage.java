package org.iota.jota.dto;

import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trytes;
import org.iota.jota.utils.Converter;

public abstract class MamResponseBundleMessage extends MamResponse {

    private Bundle bundle;
    
    private Trytes messageId;
    
    /**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * @return the messageId
     */
    public Trytes getMessageId() {
        return messageId;
    }
    
    public void setMessageId(byte[] messageId) {
        int[] intTrits = new int[messageId.length];
        for (int i = 0; i < messageId.length; i++) {
            intTrits[i] = messageId[i];
        }
        this.messageId = new Trytes(Converter.trytes(intTrits));
    }
}
