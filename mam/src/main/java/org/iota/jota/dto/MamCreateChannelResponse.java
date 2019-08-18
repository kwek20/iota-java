package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamCreateChannelResponse extends MamResponse {
    
    private Trytes channelId;
    private String channel_id;

    public MamCreateChannelResponse() {
        
    }

    /**
     * @return the channelId
     */
    public Trytes getChannelId() {
        return channelId;
    }

}
