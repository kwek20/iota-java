package org.iota.jota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.iota.jota.c.MamC;
import org.iota.jota.dto.MamAnnounceChannelResponse;
import org.iota.jota.dto.MamCreateChannelResponse;
import org.iota.jota.dto.MamCreateEndpointResponse;
import org.iota.jota.dto.MamRemainingChannelKeysResponse;
import org.iota.jota.dto.MamRemainingEndpointKeysResponse;
import org.iota.jota.dto.MamResponse;
import org.iota.jota.dto.MamReturnSerialised;
import org.iota.jota.dto.MamWriteHeaderOnChannelResponse;
import org.iota.jota.dto.MamWriteHeaderOnEndpointResponse;
import org.iota.jota.dto.MamWriteTagResponse;
import org.iota.jota.model.Bundle;
import org.iota.jota.types.Trits;
import org.iota.jota.types.Trytes;
import org.iota.jota.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MamTest {
    
    private static final String SEED = "KHFHKHGLB9VWJSORQTTNGGIIHGPGKKJCUOXUFCCUEPDZSZIHYJCSELIOBJPVAE9QLODMGTYFNGUKZ9999";

    private MamC mam;
    
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
        assertEquals(ReturnCode.OK, ret.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, ret.getChannelId().getTrytesString().length());
        
        MamRemainingChannelKeysResponse response = mam.getRemainingChannelSecretKeys(ret.getChannelId());
        
        //TODO: mam c api doesnt have a retcode
        assertEquals(ReturnCode.MISSING, response.getReturnCode());
        assertEquals(32, response.getNumChannelKeys());
    }

    @Test
    void testCreateEndpoint() {
        MamCreateChannelResponse ret = mam.createChannel(5);
        assertEquals(ReturnCode.OK, ret.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, ret.getChannelId().getTrytesString().length());
        
        MamCreateEndpointResponse endpoint = mam.createEndpoint(5, ret.getChannelId());

        assertEquals(ReturnCode.OK, endpoint.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, endpoint.getEndpointId().getTrytesString().length());
        
        MamRemainingEndpointKeysResponse response = mam.getRemainingEndpointSecretKeys(ret.getChannelId(), endpoint.getEndpointId());

        //TODO: mam c api doesnt have a retcode
        assertEquals(ReturnCode.MISSING, response.getReturnCode());
        assertEquals(32, response.getNumEndpointKeys());
    }

    @Test
    void testWriteTag() {
        MamWriteTagResponse tagResponse = mam.writeTag(new Trits(new Trytes("TEST9TAG").toTrits()), 1);
        System.out.println(tagResponse);
    }

    @Test
    void testWriteHeaderOnChannel() {
        Bundle bundle = new Bundle();
        MamCreateChannelResponse ret = mam.createChannel(5);
        assertEquals(ReturnCode.OK, ret.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, ret.getChannelId().getTrytesString().length());
        
        MamWriteHeaderOnChannelResponse writeHeaderRet = mam.writeHeaderOnChannel(ret.getChannelId(), null, null, bundle);
        assertEquals(ReturnCode.OK, writeHeaderRet.getReturnCode());
        
        assertEquals(ret.getChannelId().getTrytesString(), bundle.getTransactions().get(0).getAddress());
    }

    @Test
    void testWriteHeaderOnEndpoint() {
        Bundle bundle = new Bundle();
        MamCreateChannelResponse ret = mam.createChannel(5);
        assertEquals(ReturnCode.OK, ret.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, ret.getChannelId().getTrytesString().length());
        
        MamCreateEndpointResponse endpointRet = mam.createEndpoint(5, ret.getChannelId());
        assertEquals(ReturnCode.OK, ret.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, ret.getChannelId().getTrytesString().length());
        
        MamWriteHeaderOnEndpointResponse writeHeaderRet = mam.writeHeaderOnEndpoint(ret.getChannelId(), endpointRet.getEndpointId(), null, null, bundle);
        assertEquals(ReturnCode.OK, writeHeaderRet.getReturnCode());
        
        assertEquals(ret.getChannelId().getTrytesString(), bundle.getTransactions().get(0).getAddress());
    }

    @Test
    void testAnnounceChannel() {
        Bundle bundle = new Bundle();
        
        MamCreateChannelResponse oldChannel = mam.createChannel(5);
        assertEquals(ReturnCode.OK, oldChannel.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, oldChannel.getChannelId().getTrytesString().length());
        
        MamCreateChannelResponse newChannel = mam.createChannel(5);
        assertEquals(ReturnCode.OK, newChannel.getReturnCode());
        assertEquals(Constants.ADDRESS_LENGTH_WITHOUT_CHECKSUM, newChannel.getChannelId().getTrytesString().length());
        
        MamAnnounceChannelResponse announceRet = mam.announceChannel(oldChannel.getChannelId(), newChannel.getChannelId(), null, null, bundle);
        assertEquals(ReturnCode.OK, announceRet.getReturnCode());
        
        System.out.println(announceRet);
        System.out.println(bundle);
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
        MamReturnSerialised serialised = mam.serialize(null, 0);
        System.out.println(serialised);
    }

    @Test
    void testDeserialize() {
        MamReturnSerialised serialised = mam.serialize(null, 0);
        int[] trits = serialised.getSerialisedState().toTrits();
        MamResponse deserialised = mam.deserialize(new Trits(trits), trits.length, null, 0);
        
        System.out.println(deserialised);
    }

    @Test
    void testSaveApi(@TempDir Path folder) throws IOException {
        File file = folder.resolve("storedapi.mam").toFile();
        
        mam.saveApi(file.getAbsolutePath(), null, 0);
        mam.destroyApi();
        mam.loadApi(file.getAbsolutePath(), null, 0);
    }
}
