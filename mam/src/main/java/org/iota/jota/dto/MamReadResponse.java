package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamReadResponse extends MamResponse {

	private Trytes payload;
	
	private int payloadSize;
	
	private boolean isLast;

	public MamReadResponse(Trytes payload, int payloadSize, boolean isLast) {
		this.payload = payload;
		this.payloadSize = payloadSize;
		this.isLast = isLast;
	}

	public Trytes getPayload() {
		return payload;
	}

	public int getPayloadSize() {
		return payloadSize;
	}

	public boolean isLast() {
		return isLast;
	}
}
