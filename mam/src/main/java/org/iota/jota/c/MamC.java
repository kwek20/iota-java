package org.iota.jota.c;

import org.iota.jota.Mam;
import org.iota.jota.MamChecksum;
import org.iota.jota.MamRead;
import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trytes;

public class MamC implements Mam {
	
	/* Original methods, we have a wrapper that tracks our API
	private static native long mam_api_serialized_size(mam_api_t const *const api);
	private static native void mam_api_serialize(mam_api_t const *const api, trit_t *const buffer, tryte_t const *const encr_key_trytes, size_t encr_key_trytes_size);
	private static native int mam_api_deserialize(trit_t const *const buffer, size_t const buffer_size, mam_api_t *const api, tryte_t const *const decr_key_trytes, size_t decr_key_trytes_size);
	private static native int mam_api_save(mam_api_t const *const api, char const *const filename, tryte_t const *const encr_key_trytes, size_t encr_key_trytes_size);
	private static native int mam_api_load(char const *const filename, mam_api_t *const api, tryte_t const *const decr_key_trytes, size_t decr_key_trytes_size);
	
	private static native int mam_api_init(mam_api_t *const api, tryte_t const *const seed);
	
	private static native int mam_api_channel_create(mam_api_t *const api, size_t const height, tryte_t *const channel_id);
	private static native int mam_api_endpoint_create(mam_api_t *const api, size_t const height, tryte_t const *const channel_id, tryte_t *const endpoint_id);
	
	private static native int mam_api_bundle_write_header_on_channel(mam_api_t *const api, tryte_t const *const ch_id, mam_psk_t_set_t psks, mam_ntru_pk_t_set_t ntru_pks, bundle_transactions_t *const bundle, trit_t *const msg_id);
	private static native int mam_api_bundle_write_header_on_endpoint(mam_api_t *const api, tryte_t const *const ch_id, tryte_t const *const ep_id, mam_psk_t_set_t psks, mam_ntru_pk_t_set_t ntru_pks, bundle_transactions_t *const bundle, trit_t *const msg_id);
	
	private static native int mam_api_bundle_read(mam_api_t *const api, bundle_transactions_t const *const bundle, tryte_t **const payload, size_t *const payload_size, bool *const is_last_packet);
	private static native int mam_api_bundle_write_packet(mam_api_t *const api, trit_t const *const msg_id, tryte_t const *const payload, size_t const payload_size, mam_msg_checksum_t checksum, bool is_last_packet, bundle_transactions_t *const bundle);
	*/
	
	// Wrapper classes> trit_t
	
	private static native long mam_api_serialized_size();
	
	// buffer=trit_t, encr_key_trytes=tryte_t
	private static native void mam_api_serialize(byte[] buffer, String encr_key_trytes, long encr_key_trytes_size);
	// buffer=trit_t, decr_key_trytes=tryte_t
	private static native int mam_api_deserialize(byte[] buffer, long buffer_size, String decr_key_trytes, long decr_key_trytes_size);
	// encr_key_trytes=tryte_t
	private static native int mam_api_save(String filename, String encr_key_trytes, long encr_key_trytes_size);
	// decr_key_trytes=tryte_t
	private static native int mam_api_load(String filename, String decr_key_trytes, long decr_key_trytes_size);
	
	// seed=tryte_t
	private static native int mam_api_init(String seed);
	
	// channel_id=tryte_t *const
	private static native int mam_api_channel_create(long height, String channel_id);
	// channel_id=tryte_t *const
	private static native int mam_api_endpoint_create(long height, String channel_id, String endpoint_id);
	
	// ch_id=tryte_t const *const, bundle=bundle_transactions_t *const, msg_id=trit_t *const
	private static native int mam_api_bundle_write_header_on_channel(String ch_id, mam_psk_t_set_t psks, mam_ntru_pk_t_set_t ntru_pks, Bundle bundle, byte[] msg_id);
	private static native int mam_api_bundle_write_header_on_endpoint(tryte_t const *const ch_id, tryte_t const *const ep_id, mam_psk_t_set_t psks, mam_ntru_pk_t_set_t ntru_pks, bundle_transactions_t *const bundle, trit_t *const msg_id);
	
	private static native int mam_api_bundle_read(bundle_transactions_t const *const bundle, tryte_t **const payload, size_t *const payload_size, bool *const is_last_packet);
	private static native int mam_api_bundle_write_packet(trit_t const *const msg_id, tryte_t const *const payload, long payload_size, mam_msg_checksum_t checksum, bool is_last_packet, bundle_transactions_t *const bundle);
	
	@Override
	public void initApi(String seed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyApi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveApi(String filename, Trytes encryptionKey, long keySize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadApi(String filename, Trytes decryptionKey, long keySize) {
		// TODO Auto-generated method stub

	}

	@Override
	public long serializedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Trytes serialize(Trytes encryptionKey, long keySize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(Trytes encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize) {
		// TODO Auto-generated method stub

	}

	@Override
	public Trytes createChannel(int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trytes createEndpoint(int height, Trytes channelId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trytes writeHeaderOnChannel(Trytes channelId, Bundle bundle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trytes writeHeaderOnEndpoint(Trytes channelId, Trytes endpointId, Bundle bundle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writePacketToBundle(Trytes messageId, Trytes payload, int payloadSize, MamChecksum checksum,
			boolean isLast, Bundle bundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public MamRead readBundle(Bundle bundle) {
		// TODO Auto-generated method stub
		return null;
	}

}
