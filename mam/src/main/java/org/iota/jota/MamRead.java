package org.iota.jota;

import org.iota.jota.types.Trytes;

public class MamRead {

	private Trytes payload;
	
	private int payloadSize;
	
	private boolean isLast;

	public MamRead(Trytes payload, int payloadSize, boolean isLast) {
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
