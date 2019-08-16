package org.iota.jota.c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.iota.jota.Mam;
import org.iota.jota.MamChecksum;
import org.iota.jota.MamRead;
import org.iota.jota.NarSystem;
import org.iota.jota.c.dto.ReturnSerialsedSize;
import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trytes;

public class MamC implements Mam {
    
    static
    {
        NarSystem.loadLibrary();
    }
    
    // SECTION JNI WRAPPERS
    
    /*
    typedef struct mam_api_s {
      mam_prng_t prng;
      mam_ntru_sk_t_set_t ntru_sks;
      mam_ntru_pk_t_set_t ntru_pks;
      mam_psk_t_set_t psks;
      mam_channel_t_set_t channels;
      trint18_t channel_ord;
      trit_t_to_mam_msg_write_context_t_map_t write_ctxs;
      trit_t_to_mam_msg_read_context_t_map_t read_ctxs;
      mam_pk_t_set_t trusted_channel_pks;
      mam_pk_t_set_t trusted_endpoint_pks;
    } mam_api_t;
     */
    
    // Sponge fixed key size
    private static final int MAM_SPONGE_KEY_SIZE = 243;
    
    // Size of a Pre-Shared Key ID 
    private static final int MAM_PSK_ID_SIZE = 81;
    // Size of a Pre-Shared Key 
    private static final int MAM_PSK_KEY_SIZE = 243;
    
    // NTRU id size
    private static final int MAM_NTRU_ID_SIZE = 81;
    // NTRU public key size
    private static final int MAM_NTRU_PK_SIZE = 9216;
    // NTRU secret key size
    private static final int MAM_NTRU_SK_SIZE = 1024;
    // NTRU session symmetric key size
    private static final int MAM_NTRU_KEY_SIZE = MAM_SPONGE_KEY_SIZE;
    // NTRU encrypted key size
    private static final int MAM_NTRU_EKEY_SIZE = 9216;
    
    private class mam_psk_t {
        // trit_t id[MAM_PSK_ID_SIZE];
        byte[] id = new byte[MAM_PSK_ID_SIZE];
        
        // trit_t key[MAM_PSK_KEY_SIZE];
        byte[] key = new byte[MAM_PSK_KEY_SIZE];
      }
    
    private class mam_psk_t_set_entry_t {
        mam_psk_t value;
        
        // UT_hash_handle
        long hh;
    }
    
    private class mam_ntru_pk_t {
        // trit_t id[MAM_NTRU_ID_SIZE];
        byte[] id = new byte[MAM_NTRU_ID_SIZE];
        
        // trit_t key[MAM_NTRU_KEY_SIZE];
        byte[] key = new byte[MAM_NTRU_KEY_SIZE];
      }
    
    private class mam_ntru_pk_t_set_entry_t {
        mam_psk_t value;
        
        // UT_hash_handle
        long hh;
    }

	// Wrapper classes> trit_t
	
	private static native long mam_api_serialized_size(ReturnSerialsedSize ret);
	
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
	// mam_psk_t_set_t -> mam_psk_t_set_entry_t -> mam_psk_t value && UT_hash_handle hh -> 
	private static native int mam_api_bundle_write_header_on_channel(String ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);
	
	private static native int mam_api_bundle_write_header_on_endpoint(String ch_id, String ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);
	
	private static native int mam_api_bundle_read(Bundle bundle, String[] payload, long payload_size, boolean is_last_packet);
	
	private static native int mam_api_bundle_write_packet(byte[] msg_id, String payload, long payload_size, String checksum, boolean is_last_packet, Bundle bundle);
	
	// SECTION END
    
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
	public void initApi(String seed) {
	    mam_api_init(seed);
	}

	@Override
	public void destroyApi() {
		
	}

	@Override
	public void saveApi(String filename, Trytes encryptionKey, long keySize) {
	    mam_api_save(filename, encryptionKey.getTrytesString(), keySize);
	}

	@Override
	public void loadApi(String filename, Trytes decryptionKey, long keySize) {
		mam_api_load(filename, decryptionKey.getTrytesString(), keySize);
	}

	@Override
	public long serializedSize() {
	    ReturnSerialsedSize sizeRet = new ReturnSerialsedSize();
		return mam_api_serialized_size(sizeRet);
	}

	@Override
	public Trytes serialize(Trytes encryptionKey, long keySize) {
		byte[] trit_t = new byte[0];
		mam_api_serialize(trit_t, encryptionKey.getTrytesString(), keySize);
		
		return new Trytes(new String(trit_t));
	}

	@Override
	public void deserialize(Trytes encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize) {
		
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
