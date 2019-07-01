package org.iota.jota;

public enum MamChecksum {
	
	/**
	 * Well.. none!
	 */
	NONE, 
	
	/**
	 * With MAC checksum you add integrity to your packet. 
	 * Due to the underlying sponge construction of a MAM message, a MAC checksum actually also adds integrity to all previous packets of the message. 
	 * Adding a MAC checksum to a packet is a really cheap operation so it is recommended in most cases.
	 */
	MAC, 
	
	/**
	 * With signature (chosen method in the example) you add integrity and authenticity to your packet. 
	 * The integrity part is due to the fact that the signed hash is actually the MAC checksum. 
	 * For the same reasons, by signing a packet you add authenticity to all previous packets of the message. 
	 * Signing a packet is an expensive operation, depending on your use case you could for example sign one packet out of ten.
	 */
	SIG
}
