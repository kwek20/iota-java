package org.iota.jota;

import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trytes;

public interface Mam {

	void initApi(String seed);
	
	void destroyApi();

	void saveApi(String filename, Trytes encryptionKey, long keySize);
	
	void loadApi(String filename, Trytes decryptionKey, long keySize);
	
	long serializedSize();
	
	Trytes serialize(Trytes encryptionKey, long keySize);
	
	void deserialize(Trytes encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize);
	
	Trytes createChannel(int height);
	
	Trytes createEndpoint(int height, Trytes channelId);
	
	Trytes writeHeaderOnChannel(Trytes channelId, Bundle bundle);
	
	Trytes writeHeaderOnEndpoint(Trytes channelId, Trytes endpointId, Bundle bundle);
	
	void writePacketToBundle(Trytes messageId, Trytes payload, int payloadSize, MamChecksum checksum, boolean isLast, Bundle bundle);
	
	/**
	 * Make sure to free the mamread payload afterwards!
	 * 
	 * @param bundle
	 * @return
	 */
	MamRead readBundle(Bundle bundle);
}
