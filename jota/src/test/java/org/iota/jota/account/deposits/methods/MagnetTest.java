package org.iota.jota.account.deposits.methods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.deposits.DepositRequest;
import org.iota.jota.account.deposits.DepositTest;
import org.iota.jota.account.errors.MagnetError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MagnetTest extends DepositTest {

    private static final long TIME = 0;
    private static final boolean MULTI = false;
    private static final long AMOUNT = 5;
    private static final String MESSAGE = "Test message";
    
    private static final String MAGNET_CHECKSUM = "IGZOCSVP9";

    private static final String MAGNET = "iota://" + DepositTest.depositAddress.getHash() + MAGNET_CHECKSUM + "/?"
            + MagnetMethod.CONDITION_EXPIRES + "=" + TIME + "&"
            + MagnetMethod.CONDITION_MULTI_USE + "=" + MULTI + "&"
            + MagnetMethod.CONDITION_AMOUNT + "=" + AMOUNT + "&"
            + MagnetMethod.CONDITION_MESSAGE + "=" + encode(MESSAGE);
    
    private static String encode(String message) {
        try {
            return URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private MagnetMethod method;

    private ConditionalDepositAddress conditions;

    @BeforeEach
    public void setUp() throws Exception {
        method = new MagnetMethod();

        DepositRequest request = new DepositRequest(new Date(TIME), MULTI, AMOUNT, MESSAGE);
        conditions = new ConditionalDepositAddress(request, DepositTest.depositAddress);
    }

    

    @Test
    public void buildMagnet() {
        String magnet = method.build(conditions);
        System.out.println(magnet);
        System.out.println(MAGNET);
        assertEquals(MagnetTest.MAGNET, magnet);
    }

    @Test
    public void readMagnet() {
        ConditionalDepositAddress request = method.parse(MAGNET);

        assertEquals(conditions, request);
        assertEquals(conditions.getDepositAddress().getHashCheckSum(), DepositTest.depositAddress.getHashCheckSum());
    }

    @Test
    public void magnetChecksum() {
        String checksum = method.magnetChecksum(DepositTest.depositAddress.getHash(),
                TIME, MULTI, AMOUNT, encode(MESSAGE));

        assertEquals(MAGNET_CHECKSUM, checksum, "Checksum should be equal to the pre-generated one");
    }

    @Test
    public void removeAmountDefaultResultsInWrongChecksum() {
        assertThrows(MagnetError.class, () -> method.parse("iota://" + DepositTest.depositAddress.getHash() + MAGNET_CHECKSUM + "/?"
                + MagnetMethod.CONDITION_EXPIRES + "=" + TIME + "&"
                + MagnetMethod.CONDITION_MULTI_USE + "=" + MULTI + "&"
                + MagnetMethod.CONDITION_MESSAGE + "=" + encode(MESSAGE)),
                "Invalid magnet should throw exception");
    }
    
    @Test
    public void magnetWithoutMessage() {
        String magnet = "iota://" + DepositTest.depositAddress.getHash() + "WEXOXOZBZ" + "/?"
                + MagnetMethod.CONDITION_EXPIRES + "=" + TIME + "&"
                + MagnetMethod.CONDITION_MULTI_USE + "=" + MULTI + "&"
                + MagnetMethod.CONDITION_AMOUNT + "=" + AMOUNT;
        ConditionalDepositAddress request = method.parse(magnet);
        assertEquals("", request.getRequest().getMessage());
    }
}
