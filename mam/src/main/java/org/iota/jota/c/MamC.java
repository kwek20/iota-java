package org.iota.jota.c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.iota.jota.Mam;
import org.iota.jota.MamChecksum;
import org.iota.jota.NarSystem;
import org.iota.jota.dto.MamAnnounceChannelResponse;
import org.iota.jota.dto.MamAnnoundeEndpointResponse;
import org.iota.jota.dto.MamCreateChannelResponse;
import org.iota.jota.dto.MamCreateEndpointResponse;
import org.iota.jota.dto.MamReadResponse;
import org.iota.jota.dto.MamRemainingChannelKeysResponse;
import org.iota.jota.dto.MamRemainingEndpointKeysResponse;
import org.iota.jota.dto.MamResponse;
import org.iota.jota.dto.MamReturnSerialised;
import org.iota.jota.dto.MamReturnSerialisedSize;
import org.iota.jota.dto.MamWriteHeaderOnEndpointResponse;
import org.iota.jota.dto.MamWritePacketToBundleResponse;
import org.iota.jota.dto.MamWriteTagResponse;
import org.iota.jota.dto.WriteHeaderOnChannelResponse;
import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trytes;

public class MamC implements Mam {
    
    static
    {
        NarSystem.loadLibrary();
    }

    // seed=tryte_t
    private static native int mam_api_init(String seed);
    
    private static native int mam_api_destroy();
    
    private static native int mam_api_add_trusted_channel_pk(String pk);
    private static native int mam_api_add_trusted_endpoint_pk(String pk);

    private static native int mam_api_add_ntru_sk(mam_ntru_sk_t ntru_sk);
    private static native int mam_api_add_ntru_pk(mam_ntru_pk_t ntru_pk);
    private static native int mam_api_add_psk(mam_psk_t psk);
    

    // channel_id=tryte_t *const
    private static native int mam_api_channel_create(long height, String channel_id);
    private static native int mam_api_channel_remaining_sks(String channel_id);

    
    // channel_id=tryte_t *const
    private static native int mam_api_endpoint_create(long height, String channel_id, String endpoint_id);
    private static native int mam_api_endpoint_remaining_sks(String channel_id, String endpoint_id);
    
    private static native int mam_api_write_tag(MamWriteTagResponse ret, String message_id, int order);
    
    // ch_id=tryte_t const *const, bundle=bundle_transactions_t *const, msg_id=trit_t *const
    // mam_psk_t_set_t -> mam_psk_t_set_entry_t -> mam_psk_t value && UT_hash_handle hh -> 
    private static native int mam_api_bundle_write_header_on_channel(String ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);

    private static native int mam_api_bundle_write_header_on_endpoint(String ch_id, String ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);
    private static native int mam_api_bundle_announce_channel(String ch_id, String new_ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks);
    private static native int mam_api_bundle_announce_endpoint(String ch_id, String new_ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks);
    
    private static native int mam_api_bundle_write_packet(byte[] msg_id, String payload, long payload_size, String checksum, boolean is_last_packet, Bundle bundle);

    private static native int mam_api_bundle_read(Bundle bundle, String[] payload, long payload_size, boolean is_last_packet);
    

    private static native long mam_api_serialized_size(MamReturnSerialisedSize ret);
    
	// buffer=trit_t, encr_key_trytes=tryte_t
	private static native void mam_api_serialize(byte[] buffer, String encr_key_trytes, long encr_key_trytes_size);
	
	// buffer=trit_t, decr_key_trytes=tryte_tn
	private static native int mam_api_deserialize(byte[] buffer, long buffer_size, String decr_key_trytes, long decr_key_trytes_size);
	
	// encr_key_trytes=tryte_t
	private static native int mam_api_save(String filename, String encr_key_trytes, long encr_key_trytes_size);
	
	// decr_key_trytes=tryte_t
	private static native int mam_api_load(String filename, String decr_key_trytes, long decr_key_trytes_size);
	
    public MamC(String location) {
        
    }
    
    public MamC() throws IOException {
        String[] libs = new String[] {
                "libmam.so"
        };
        for (String libName : libs) {
            URL url = this.getClass().getResource("/" + libName);
            File tmpDir = Files.createTempDirectory("mam-native-lib").toFile();
            tmpDir.deleteOnExit();
            File nativeLibTmpFile = new File(tmpDir, libName);
            nativeLibTmpFile.deleteOnExit();
            try (InputStream in = url.openStream()) {
                Files.copy(in, nativeLibTmpFile.toPath());
            }
            System.load(nativeLibTmpFile.getAbsolutePath());
        }
    }

    @Override
    public MamResponse initApi(String seed) {
        mam_api_init(seed);
        return null;
    }

    @Override
    public MamResponse destroyApi() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse addTrustedChannelPrivateKey(String pk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse addTrustedEndpointPrivateKey(String pk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse addNTRUSecretKey(mam_ntru_sk_t ntru_sk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse addNTRUPublicKey(mam_ntru_pk_t ntru_pk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse addPreSharedKey(mam_psk_t psk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamCreateChannelResponse createChannel(int height) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamRemainingChannelKeysResponse getRemainingChannelSecretKeys(String channel_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamCreateEndpointResponse createEndpoint(int height, Trytes channelId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamRemainingEndpointKeysResponse getRemainingEndpointSecretKeys(String channel_id, String endpoint_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamWriteTagResponse writeTag(String message_id, int order) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WriteHeaderOnChannelResponse writeHeaderOnChannel(String ch_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamWriteHeaderOnEndpointResponse writeHeaderOnEndpoint(String ch_id, String ep_id,
            mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamAnnounceChannelResponse announceChannel(String ch_id, String new_ch_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamAnnoundeEndpointResponse announceEndpoint(String ch_id, String new_ep_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamWritePacketToBundleResponse writePacketToBundle(Trytes messageId, Trytes payload, int payloadSize,
            MamChecksum checksum, boolean isLast, Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamReadResponse readBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamReturnSerialisedSize serializedSize() {
        MamReturnSerialisedSize sizeRet = new MamReturnSerialisedSize();
        mam_api_serialized_size(sizeRet);
        //return sizeRet.getSize();
        return null;
    }

    @Override
    public MamReturnSerialised serialize(Trytes encryptionKey, long keySize) {
        byte[] trit_t = new byte[0];
        mam_api_serialize(trit_t, encryptionKey.getTrytesString(), keySize);
        
        //return new Trytes(new String(trit_t));
        return null;
    }

    @Override
    public MamResponse deserialize(Trytes encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MamResponse saveApi(String fileName, Trytes encryptionKey, long keySize) {
        mam_api_save(fileName, encryptionKey.getTrytesString(), keySize);
        return null;
    }

    @Override
    public MamResponse loadApi(String fileName, Trytes decryptionKey, long keySize) {
        mam_api_load(fileName, decryptionKey.getTrytesString(), keySize);
        return null;
    }
}
