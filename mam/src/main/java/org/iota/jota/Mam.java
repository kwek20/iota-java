package org.iota.jota;

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

public interface Mam {
    
    /*
    mam_psk_t psk;
    mam_psk_gen(&psk, &prng, "B9IOSRYXSJPELPKGTG9PJDQC9YS", "PZQZ...AKKEF", 42);
    // Provides it to recipients
    mam_psk_destroy(&psk);
    The receiver receives a PSK from a sender and adds it to the API.

    mam_psk_t psk;
    // Receives it from sender
    mam_api_add_psk(&api, &psk);
    mam_psk_destroy(&psk);
*/
    
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
        
        // UT_hash_handle -> store key and use for lookup in C structs
        long hh;
    }
    
    class mam_ntru_pk_t_set_entry_t {
        mam_psk_t value;
        
        // UT_hash_handle -> store key and use for lookup in C structs
        long hh;
    }
    

    
    class mam_ntru_pk_t {
        // trit_t id[MAM_NTRU_ID_SIZE];
        byte[] id = new byte[MAM_NTRU_ID_SIZE];
        
        // trit_t key[MAM_NTRU_KEY_SIZE];
        byte[] key = new byte[MAM_NTRU_KEY_SIZE];
      }
    
    class mam_ntru_sk_t {

        // Associated public key
        mam_ntru_pk_t public_key;
        
        // trit_t -> Secret key - small coefficients of polynomial f
        byte[] secret_key = new byte[MAM_NTRU_SK_SIZE];
        
        // Internal representation of a private key: NTT(1+3f) poly_t
        // type poly_t Trint9 -> Trint9 int16 -> short
        short f;
    }
    
    /**
     * Initializes the API
     *
     * @param seed The seed
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
     * @param pk A channel public key
     * @return a status code
     */
    MamResponse addTrustedChannelPrivateKey(String pk);

    /**
     * Adds a trusted endpoint pk into the api's trusted endpoints set
     *
     * @param pk An endpoint public key
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
     * @param height The channel's MSS height
     * @return a status code and channel ID
     */
    MamCreateChannelResponse createChannel(int height);

    /**
     * Returns the number of remaining secret keys of a channel
     *
     * @param channelId The channel id
     * @return the number of remaining secret keys of the channel
     */
    MamRemainingChannelKeysResponse getRemainingChannelSecretKeys(String channelId);

    /**
     * Creates and adds an endpoint to the API
     *
     * @param height     The endpoint's MSS height
     * @param channelId  The parent channel id
     * @return a status code and endpoint ID
     */
    MamCreateEndpointResponse createEndpoint(int height, Trytes channelId);

    /**
     * Returns the number of remaining secret keys of an endpoint
     *
     * @param channelId  The parent channel id
     * @param endpointId The endpoint id
     * @return the number of remaining secret keys of the endpoint
     */
    MamRemainingEndpointKeysResponse getRemainingEndpointSecretKeys(String channelId, String endpointId);

    /**
     * Creates a MAM tag that can be used in IOTA transactions
     *
     * @param messageId The message ID
     * @param order     The packet order
     * @return the tag
     */
    MamWriteTagResponse writeTag(Trits messageId, int order);

    /**
     * Writes a MAM header through a channel into a bundle
     *
     * @param channelId The channel ID
     * @param psks      Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks  NTRU public keys used for encrypting the session key
     * @param bundle    The bundle we are writing this to
     * @return a status code, the bundle and message ID
     */
    MamWriteHeaderOnChannelResponse writeHeaderOnChannel(String channelId, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);

    /**
     * Writes a MAM header through an endpoint into a bundle
     *
     * @param channelId  The parent channel ID
     * @param endpointId The endpoint ID
     * @param psks       Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks   NTRU public keys used for encrypting the session key
     * @param bundle     The bundle we are writing this to
     * @return a status code, the bundle and message ID
     */
    MamWriteHeaderOnEndpointResponse writeHeaderOnEndpoint(String channelId, String endpointId,
            mam_psk_t_set_entry_t[] psks, mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);

    /**
     * Writes an announcement of a channel into a bundle
     *
     * @param channelId    The channel ID
     * @param newChannelId The new channel ID
     * @param psks         Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks     NTRU public keys used for encrypting the session key
     * @param bundle       The bundle we are writing this to
     * @return a status code, the bundle and message ID
     */
    MamAnnounceChannelResponse announceChannel(String channelId, String newChannelId, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);

    /**
     * Writes an announcement of a endpoint into a bundle
     *
     * @param channelId     The channel ID
     * @param newEndpointId The new endpoint ID
     * @param psks          Pre-Shared Keys used for encrypting the session key
     * @param ntru_pks      NTRU public keys used for encrypting the session key
     * @param bundle        The bundle we are writing this to
     * @return a status code, the bundle and message ID
     */
    MamAnnounceEndpointResponse announceEndpoint(String channelId, String newEndpointId, mam_psk_t_set_entry_t[] psks,
            mam_ntru_pk_t_set_entry_t[] ntru_pks, Bundle bundle);

    /**
     * Writes a MAM packet into a bundle
     *
     * @param messageId      The message ID
     * @param payload        Payload to write into the packet
     * @param payloadSize    The payload size
     * @param isLast         Indicates whether or not this is the last packet
     * @param bundle         The bundle we are writing this to
     * @return a status code and the bundle we wrote to
     */
    MamWritePacketToBundleResponse writePacketToBundle(Trytes messageId, Trytes payload, long payloadSize,
            MamChecksum checksum, boolean isLast, Bundle bundle);

    /**
     * Reads a MAM header and potentially a MAM packet from a bundle
     *
     * @param bundle The bundle containing the MAM message
     * @return a status code, payload, payload size and a boolean indicating last
     *         packet or not
     */
    MamReadBundleResponse readBundle(Bundle bundle);

    /**
     * Gets the number of trits needed to serialize an API
     *
     * @return return the size of the api after serializing
     */
    MamReturnSerialisedSize serializedSize();

    /**
     * Serializes the API into a buffer
     *
     * @param encryptionKey        The encryption key (optional - can be set to null)
     * @param keySize              The encryption key size
     * @return return The serialized api trits
     */
    MamReturnSerialised serialize(Trytes encryptionKey, long keySize);

    /**
     * Deserializes a buffer into an API, and loads this
     *
     * @param encryptedApi  The buffer from where to deserialize api trits from
     * @param encryptedSize The size of the buffer in trits
     * @param decryptionKey The decryption key (optional - can be set to null)
     * @param keySize       The decryption key size
     * @return a status code
     */
    MamResponse deserialize(Trits encryptedApi, long encryptedSize, Trytes decryptionKey, long keySize);

    /**
     * Saves the API into a file
     *
     * @param fileName             The file name where to serialize the API into
     * @param encryptionKey        The encryption key (optional - can be set to null)
     * @param keySize              The encryption key size
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
