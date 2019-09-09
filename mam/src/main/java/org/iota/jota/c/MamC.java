package org.iota.jota.c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.iota.jota.Mam;
import org.iota.jota.MamChecksum;
import org.iota.jota.NarSystem;
import org.iota.jota.dto.MamAnnounceChannelResponse;
import org.iota.jota.dto.MamAnnounceEndpointResponse;
import org.iota.jota.dto.MamCreateChannelResponse;
import org.iota.jota.dto.MamCreateEndpointResponse;
import org.iota.jota.dto.MamReadBundleResponse;
import org.iota.jota.dto.MamRemainingChannelKeysResponse;
import org.iota.jota.dto.MamRemainingEndpointKeysResponse;
import org.iota.jota.dto.MamResponse;
import org.iota.jota.dto.MamReturnSerialised;
import org.iota.jota.dto.MamReturnSerialisedSize;
import org.iota.jota.dto.MamWriteHeaderOnChannelResponse;
import org.iota.jota.dto.MamWriteHeaderOnEndpointResponse;
import org.iota.jota.dto.MamWritePacketToBundleResponse;
import org.iota.jota.dto.MamWriteTagResponse;
import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trits;
import org.iota.jota.types.Trytes;

public class MamC implements Mam {
    
    static
    {
        NarSystem.loadLibrary();
    }

    private static native long mam_api_init(String seed);
    
    private static native long mam_api_destroy();
    
    private static native long mam_api_add_trusted_channel_pk(String pk);
    private static native long mam_api_add_trusted_endpoint_pk(String pk);

    private static native long mam_api_add_ntru_sk(mam_ntru_sk_t ntru_sk);
    private static native long mam_api_add_ntru_pk(mam_ntru_pk_t ntru_pk);
    private static native long mam_api_add_psk(mam_psk_t psk);
    
    private static native long mam_api_channel_create(MamCreateChannelResponse response, long height);
    private static native long mam_api_channel_remaining_sks(String channel_id);

    private static native long mam_api_endpoint_create(MamCreateEndpointResponse response, long height, String channel_id);
    private static native long mam_api_endpoint_remaining_sks(String channel_id, String endpoint_id);
  
    //TODO Byte buffer
    private static native long mam_api_write_tag(MamWriteTagResponse ret, int[] message_id, int order);
    
    private static native long mam_api_bundle_write_header_on_channel(MamWriteHeaderOnChannelResponse response, String ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);

    private static native long mam_api_bundle_write_header_on_endpoint(MamWriteHeaderOnEndpointResponse response, String ch_id, String ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);
    private static native long mam_api_bundle_announce_channel(MamAnnounceChannelResponse response, String ch_id, String new_ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);
    private static native long mam_api_bundle_announce_endpoint(MamAnnounceEndpointResponse response, String ch_id, String new_ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);
    
    //TODO Byte buffer
    private static native long mam_api_bundle_write_packet(MamWritePacketToBundleResponse response, char[] msg_id, String payload, long payload_size, int checksum, boolean is_last_packet, Bundle bundle);

    private static native long mam_api_bundle_read(MamReadBundleResponse response, Bundle bundle);
    

    private static native long mam_api_serialized_size(MamReturnSerialisedSize ret);
   
	private static native void mam_api_serialize(MamReturnSerialised response, String encr_key_trytes, long encr_key_trytes_size);
	
	// TODO: Byte buffer
	private static native long mam_api_deserialize(int[] buffer, long buffer_size, String decr_key_trytes, long decr_key_trytes_size);
	
	private static native long mam_api_save(String filename, String encr_key_trytes, long encr_key_trytes_size);
	
	private static native long mam_api_load(String filename, String decr_key_trytes, long decr_key_trytes_size);
	
	/**
	 * Loads an instance of Mam through the C api.
	 * 
	 * @param location The location we load the library from
	 * @throws IOException
	 */
    public MamC(String location) throws IOException {
        String[] libs = new String[] {
                location
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
    
    /**
     * Loads an instance of Mam through the C api.
     * Will attempt to look up the library called libmam.so
     * 
     * @throws IOException
     */
    public MamC() throws IOException {
        this("libmam.so");
    }

    @Override
    public MamResponse initApi(String seed) {
        MamResponse ret = new MamResponse();
        long code = mam_api_init(seed);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse destroyApi() {
        MamResponse ret = new MamResponse();
        long code = mam_api_destroy();
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse addTrustedChannelPrivateKey(String pk) {
        MamResponse ret = new MamResponse();
        long code = mam_api_add_trusted_channel_pk(pk);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse addTrustedEndpointPrivateKey(String pk) {
        MamResponse ret = new MamResponse();
        long code = mam_api_add_trusted_endpoint_pk(pk);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse addNTRUSecretKey(mam_ntru_sk_t ntru_sk) {
        MamResponse ret = new MamResponse();
        long code = mam_api_add_ntru_sk(ntru_sk);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse addNTRUPublicKey(mam_ntru_pk_t ntru_pk) {
        MamResponse ret = new MamResponse();
        long code = mam_api_add_ntru_pk(ntru_pk);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamResponse addPreSharedKey(mam_psk_t psk) {
        MamResponse ret = new MamResponse();
        long code = mam_api_add_psk(psk);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamCreateChannelResponse createChannel(int height) {
        MamCreateChannelResponse ret = new MamCreateChannelResponse();
        long code = mam_api_channel_create(ret, height);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamRemainingChannelKeysResponse getRemainingChannelSecretKeys(Trytes channelId) {
        MamRemainingChannelKeysResponse ret = new MamRemainingChannelKeysResponse();
        long numChannelKeys = mam_api_channel_remaining_sks(channelId.getTrytesString());
        ret.setNumChannelKeys(numChannelKeys);
        return ret;
    }

    @Override
    public MamCreateEndpointResponse createEndpoint(int height, Trytes channelId) {
        MamCreateEndpointResponse ret = new MamCreateEndpointResponse();
        long code = mam_api_endpoint_create(ret, height, channelId.getTrytesString());
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamRemainingEndpointKeysResponse getRemainingEndpointSecretKeys(Trytes channelId, Trytes endpointId) {
        MamRemainingEndpointKeysResponse ret = new MamRemainingEndpointKeysResponse();
        long numChannelKeys = mam_api_channel_remaining_sks(channelId.getTrytesString());
        ret.setNumEndpointKeys(numChannelKeys);
        return ret;
    }

    @Override
    public MamWriteTagResponse writeTag(Trits messageId, int order) {
        MamWriteTagResponse ret = new MamWriteTagResponse();
        long code = mam_api_write_tag(ret, tritsToInts(messageId), order);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamWriteHeaderOnChannelResponse writeHeaderOnChannel(Trytes ch_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle) {
        MamWriteHeaderOnChannelResponse ret = new MamWriteHeaderOnChannelResponse();
        long code = mam_api_bundle_write_header_on_channel(ret, ch_id.getTrytesString(), psks, ntru_pks, bundle);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamWriteHeaderOnEndpointResponse writeHeaderOnEndpoint(Trytes ch_id, Trytes ep_id,
            mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle) {
        MamWriteHeaderOnEndpointResponse ret = new MamWriteHeaderOnEndpointResponse();
        long code = mam_api_bundle_write_header_on_endpoint(ret, ch_id.getTrytesString(), ep_id.getTrytesString(), psks, ntru_pks, bundle);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamAnnounceChannelResponse announceChannel(Trytes ch_id, Trytes new_ch_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle) {
        MamAnnounceChannelResponse ret = new MamAnnounceChannelResponse();
        long code = mam_api_bundle_announce_channel(ret, ch_id.getTrytesString(), new_ch_id.getTrytesString(), psks, ntru_pks, bundle);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamAnnounceEndpointResponse announceEndpoint(Trytes ch_id, Trytes new_ep_id, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle) {
        MamAnnounceEndpointResponse ret = new MamAnnounceEndpointResponse();
        long code = mam_api_bundle_announce_endpoint(ret, ch_id.getTrytesString(), new_ep_id.getTrytesString(), psks, ntru_pks, bundle);
        ret.setReturnValue(code);
        return ret;
    }

    @Override
    public MamWritePacketToBundleResponse writePacketToBundle(Trytes messageId, Trytes payload, long payloadSize,
            MamChecksum checksum, boolean isLast, Bundle bundle) {
        MamWritePacketToBundleResponse response = new MamWritePacketToBundleResponse();
        long code = mam_api_bundle_write_packet(response, intToChar(messageId.toTrits()), payload.getTrytesString(), payloadSize, checksum.getNum(), isLast, bundle);
        response.setReturnValue(code);
        return response;
    }

    @Override
    public MamReadBundleResponse readBundle(Bundle bundle) {
        MamReadBundleResponse response = new MamReadBundleResponse();
        long code = mam_api_bundle_read(response, bundle);
        response.setReturnValue(code);
        return response;
    }

    @Override
    public MamReturnSerialisedSize serializedSize() {
        MamReturnSerialisedSize response = new MamReturnSerialisedSize();
        long code = mam_api_serialized_size(response);
        response.setReturnValue(code);
        return response;
    }

    @Override
    public MamReturnSerialised serialize(Trytes encryptionKey, long keySize) {
        MamReturnSerialised response = new MamReturnSerialised();
        mam_api_serialize(response, encryptionKey != null ? encryptionKey.getTrytesString() : null, keySize);
        return response;
    }

    @Override
    public MamResponse deserialize(Trits encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize) {
        MamResponse response = new MamResponse();
        long code = mam_api_deserialize(tritsToInts(encryptedApi), encryptedSize, decryptionKey != null ? decryptionKey.getTrytesString() : null, keySize);
        
        response.setReturnValue(code);
        return response;
    }

    @Override
    public MamResponse saveApi(String fileName, Trytes encryptionKey, long keySize) {
        MamResponse response = new MamResponse();
        long code = mam_api_save(fileName, encryptionKey != null ? encryptionKey.getTrytesString() : null, keySize);
        response.setReturnValue(code);
        return response;
    }

    @Override
    public MamResponse loadApi(String fileName, Trytes decryptionKey, long keySize) {
        MamResponse response = new MamResponse();
        long code = mam_api_load(fileName, decryptionKey != null ? decryptionKey.getTrytesString() : null, keySize);
        response.setReturnValue(code);
        return response;
    }
    
    private int[] tritsToInts(Trits t) {
        int[] trits = new int[t.getTrits().size()];
        List<Integer> tritList = t.getTrits();
        for (int i = 0; i < tritList.size(); i++) {
            trits[i] = tritList.get(i);
        }
        
        return trits;
    }
    
    private char[] intToChar(int[] trits) {
        char[] charTrits = new char[trits.length];
        for (int i = 0; i < trits.length; i++) {
            charTrits[i] = (char) trits[i];
        }
        
        return charTrits;
    }
}
