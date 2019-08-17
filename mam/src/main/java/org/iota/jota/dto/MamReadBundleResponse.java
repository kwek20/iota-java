package org.iota.jota.dto;

import org.iota.jota.types.Trytes;

public class MamReadBundleResponse extends MamResponse {

	private Trytes payload;
	
	private int payloadSize;
	
	private boolean isLast;

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
