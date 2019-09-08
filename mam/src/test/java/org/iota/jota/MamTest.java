package org.iota.jota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.iota.jota.c.MamC;
import org.iota.jota.dto.MamCreateChannelResponse;
import org.iota.jota.dto.MamCreateEndpointResponse;
import org.iota.jota.dto.MamRemainingChannelKeysResponse;
import org.iota.jota.dto.MamRemainingEndpointKeysResponse;
import org.iota.jota.dto.MamResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MamTest {
    
    private static final String SEED = "KHFHKHGLB9VWJSORQTTNGGIIHGPGKKJCUOXUFCCUEPDZSZIHYJCSELIOBJPVAE9QLODMGTYFNGUKZ9999";

    private Mam mam;
    
    @BeforeEach
    void setUp() throws Exception {
        mam = new MamC();

        MamResponse response = mam.initApi(SEED);
        assertEquals(0, response.getReturnValue());
        assertEquals(ReturnCode.OK, response.getReturnCode());
    }

    @AfterEach
    void tearDown() throws Exception {
        mam.initApi(SEED);
        MamResponse response = mam.destroyApi();

        assertEquals(0, response.getReturnValue());
        assertEquals(ReturnCode.OK, response.getReturnCode());
    }

    @Test
    void testAddTrustedChannelPrivateKey() {
        fail("Not yet implemented");
    }

    @Test
    void testAddTrustedEndpointPrivateKey() {
        fail("Not yet implemented");
    }

    @Test
    void testAddNTRUSecretKey() {
        fail("Not yet implemented");
    }

    @Test
    void testAddNTRUPublicKey() {
        fail("Not yet implemented");
    }

    @Test
    void testAddPreSharedKey() {
        fail("Not yet implemented");
    }

    @Test
    void testCreateChannel() {
        MamCreateChannelResponse ret = mam.createChannel(5);
        
        MamRemainingChannelKeysResponse response = mam.getRemainingChannelSecretKeys(ret.getChannelId());
        assertEquals(32, response.getNumChannelKeys());
    }

    @Test
    void testCreateEndpoint() {
        MamCreateChannelResponse ret = mam.createChannel(5);
        MamCreateEndpointResponse endpoint = mam.createEndpoint(5, ret.getChannelId());
        
        MamRemainingEndpointKeysResponse response = mam.getRemainingEndpointSecretKeys(ret.getChannelId(), endpoint.getEndpointId());
        assertEquals(32, response.getNumEndpointKeys());
    }

    @Test
    void testWriteTag() {
        fail("Not yet implemented");
    }

    @Test
    void testWriteHeaderOnChannel() {
        fail("Not yet implemented");
    }

    @Test
    void testWriteHeaderOnEndpoint() {
        fail("Not yet implemented");
    }

    @Test
    void testAnnounceChannel() {
        fail("Not yet implemented");
    }

    @Test
    void testAnnounceEndpoint() {
        fail("Not yet implemented");
    }

    @Test
    void testWritePacketToBundle() {
        fail("Not yet implemented");
    }

    @Test
    void testReadBundle() {
        fail("Not yet implemented");
    }

    @Test
    void testSerializedSize() {
        fail("Not yet implemented");
    }

    @Test
    void testSerialize() {
        fail("Not yet implemented");
    }

    @Test
    void testDeserialize() {
        fail("Not yet implemented");
    }

    @Test
    void testSaveApi() {
        fail("Not yet implemented");
    }

    @Test
    void testLoadApi() {
        fail("Not yet implemented");
    }

}
