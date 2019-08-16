package org.iota.jota;

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

public interface Mam {
    

    
    // SECTION JNI WRAPPERS
    
    // Sponge fixed key size
    static final int MAM_SPONGE_KEY_SIZE = 243;
    
    // Size of a Pre-Shared Key ID 
    static final int MAM_PSK_ID_SIZE = 81;
    // Size of a Pre-Shared Key 
    static final int MAM_PSK_KEY_SIZE = 243;
    
    // NTRU id size
    static final int MAM_NTRU_ID_SIZE = 81;
    // NTRU public key size
    static final int MAM_NTRU_PK_SIZE = 9216;
    // NTRU secret key size
    static final int MAM_NTRU_SK_SIZE = 1024;
    // NTRU session symmetric key size
    static final int MAM_NTRU_KEY_SIZE = MAM_SPONGE_KEY_SIZE;
    // NTRU encrypted key size
    static final int MAM_NTRU_EKEY_SIZE = 9216;
    
    class mam_psk_t {
        // trit_t id[MAM_PSK_ID_SIZE];
        byte[] id = new byte[MAM_PSK_ID_SIZE];
        
        // trit_t key[MAM_PSK_KEY_SIZE];
        byte[] key = new byte[MAM_PSK_KEY_SIZE];
      }
    
    class mam_psk_t_set_entry_t {
        mam_psk_t value;
        
        // UT_hash_handle
        long hh;
    }
    
    class mam_ntru_pk_t {
        // trit_t id[MAM_NTRU_ID_SIZE];
        byte[] id = new byte[MAM_NTRU_ID_SIZE];
        
        // trit_t key[MAM_NTRU_KEY_SIZE];
        byte[] key = new byte[MAM_NTRU_KEY_SIZE];
      }
    
    class mam_ntru_pk_t_set_entry_t {
        mam_psk_t value;
        
        // UT_hash_handle
        long hh;
    }
    
    // Associated public key
    class mam_ntru_sk_t {
        mam_ntru_pk_t public_key;
        
        // Secret key - small coefficients of polynomial f
        byte[] secret_key = new byte[MAM_NTRU_SK_SIZE];
        
        // Internal representation of a private key: NTT(1+3f) poly_t
        Object f;
    }
    
    /**
     * Initializes the API
     *
     * @param seed  The seed
     * @return a status code
     */
    MamResponse initApi(String seed);
    
    /**
     * Destroys the API
     *
     * @return a status code
     */
    MamResponse destroyApi();
	
    /**
     * Adds a trusted channel pk into the api's trusted channels set
     *
     * @param pk  A channel public key
     * @return a status code
     */
    MamResponse addTrustedChannelPrivateKey(String pk);
    
    /**
     * Adds a trusted endpoint pk into the api's trusted endpoints set
     *
     * @param pk  An endpoint public key
     * @return a status code
     */
    MamResponse addTrustedEndpointPrivateKey(String pk);

    /**
     * Adds a NTRU secret key to api's NTRU sks set
     *
     * @param ntru_sk A NTRU secret key
     * @return a status code
     */
    MamResponse addNTRUSecretKey(mam_ntru_sk_t ntru_sk);
    
    /**
     * Adds a NTRU public key to api's NTRU pks set
     *
     * @param ntru_pk A NTRU public key
     * @return a status code
     */
    MamResponse addNTRUPublicKey(mam_ntru_pk_t ntru_pk);
    
    /**
     * Adds a pre-shared key to api's psks set
     *
     * @param psk A PSK
     * @return a status code
     */
    MamResponse addPreSharedKey(mam_psk_t psk);
    
    /**
     * Creates and adds a channel to the API
     *
     * @param   height      The channel's MSS height
     * @return a status code
     */
	MamCreateChannelResponse createChannel(int height);
    
	/**
	 * Returns the number of remaining secret keys of a channel
	 *
	 * @param channel_id  The channel id
	 * @return the number of remaining secret keys of the channel
	 */
	MamRemainingChannelKeysResponse getRemainingChannelSecretKeys(String channel_id);
    
    /**
     * Creates and adds an endpoint to the API
     *
     * @param   height      The endpoint's MSS height
     * @param   channel_id  The parent channel id
     * @return a status code
     */
	MamCreateEndpointResponse createEndpoint(int height, Trytes channelId);
	
	/**
	 * Returns the number of remaining secret keys of an endpoint
	 *
	 * @param channel_id  The parent channel id
	 * @param endpoint_id The endpoint id
	 * @return the number of remaining secret keys of the endpoint
	 */
	MamRemainingEndpointKeysResponse getRemainingEndpointSecretKeys(String channel_id, String endpoint_id);

	/**
	 * Creates a MAM tag that can be used in IOTA transactions
	 *
	 * @param msg_id  The message ID
	 * @param ord     The packet ord
	 * @return
	 */
	MamWriteTagResponse writeTag(String message_id, int order);
    
	/**
	 * Writes a MAM header through a channel into a bundle
	 *
	 * @param ch_id     The channel ID
	 * @param psks      Pre-Shared Keys used for encrypting the session key
	 * @param ntru_pks  NTRU public keys used for encrypting the session key
	 * @return a status code
	 */
	WriteHeaderOnChannelResponse writeHeaderOnChannel(String ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);

	/**
	 * Writes a MAM header through an endpoint into a bundle
	 *
	 * @param ch_id     The parent channel ID
	 * @param ep_id     The endpoint ID
	 * @param psks      Pre-Shared Keys used for encrypting the session key
	 * @param ntru_pks  NTRU public keys used for encrypting the session key
	 * @return a status code
	 */
    MamWriteHeaderOnEndpointResponse writeHeaderOnEndpoint(String ch_id, String ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle, byte[] msg_id);
    
    /**
     * Writes an announcement of a channel into a bundle
     *
     * @param ch_id     The channel ID
     * @param ch1_id    The new channel ID
     * @param psks      Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks  NTRU public keys used for encrypting the session key
     * @return a status code
     */
    MamAnnounceChannelResponse announceChannel(String ch_id, String new_ch_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks);

    /**
     * Writes an announcement of a endpoint into a bundle
     *
     * @param ch_id     The channel ID
     * @param ep1_id    The new endpoint ID
     * @param psks      Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks  NTRU public keys used for encrypting the session key
     * @return a status code
     */
    MamAnnoundeEndpointResponse announceEndpoint(String ch_id, String new_ep_id, mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks);
    
    /**
     * Writes a MAM packet into a bundle
     *
     * @param msg_id          The message ID
     * @param payload         Payload to write into the packet
     * @param payload size    The payload size
     * @param is_last_packet  Indicates whether or not this is the last packet
     * @return a status code
     */
	MamWritePacketToBundleResponse writePacketToBundle(Trytes messageId, Trytes payload, int payloadSize, MamChecksum checksum, boolean isLast, Bundle bundle);
	
	/**
	 * Reads a MAM header and potentially a MAM packet from a bundle
	 *
	 * @param bundle          The bundle containing the MAM message
	 * @return a status code
	 */
	MamReadResponse readBundle(Bundle bundle);
	
	/**
	 * Gets the number of trits needed to serialize an API
	 *
	 * @return return the size
	 */
    MamReturnSerialisedSize serializedSize();
    
    /**
     * Serializes the API into a buffer
     *
     * @param api                   The API
     * @param encr_key_trytes       The encryption key (optional - can be set to null)
     * @param encr_key_trytes_size  The encryption key size
     * @return return void
     */
    MamReturnSerialised serialize(Trytes encryptionKey, long keySize);
    
    /**
     * Deserializes a buffer into an API
     *
     * @param buffer                The buffer from where to deserialize
     * @param buffer_size           The size of the buffer
     * @param decr_key_trytes       The decryption key (optional - can be set to null)
     * @param decr_key_trytes_size  The decryption key size
     * @return a status code
     */
    MamResponse deserialize(Trytes encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize);
    
    /**
     * Saves the API into a file
     *
     * @param fileName              The file name where to serialize the API into
     * @param encr_key_trytes        The encryption key (optional - can be set to null)
     * @param encr_key_trytes_size  The encryption key size
     * @return a status code
     */
    MamResponse saveApi(String fileName, Trytes encryptionKey, long keySize);
    
    /**
     * Loads an API from a file
     *
     * @param fileName      The file name where the API is serialized
     * @param decryptionKey The decryption key (optional - can be set to null)
     * @param keySize       The decryption key size
     * @return a status code
     */
    MamResponse loadApi(String fileName, Trytes decryptionKey, long keySize);
}
