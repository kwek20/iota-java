package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamReadBundleResponse extends MamResponse {

	private Trytes payload;
	
	private long payloadSize;
	
	private boolean isLast;

	public Trytes getPayload() {
		return payload;
	}

	public long getPayloadSize() {
		return payloadSize;
	}

	public boolean isLast() {
		return isLast;
	}
	
	public void setPayload(String payload) {
        this.payload = new Trytes(payload);
    }
}
