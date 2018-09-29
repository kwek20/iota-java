package jota;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.iota.jota.utils.Constants;
import org.iota.jota.utils.InputValidator;
import org.iota.jota.utils.SeedRandomGenerator;

/**
 * @author pinpong
 */
public class SeedRandomGeneratorTest {

    @Test
    public void shouldGenerateNewSeed() {
        String generatedSeed = SeedRandomGenerator.generateNewSeed();
        assertEquals(InputValidator.isAddress(generatedSeed), true);
        assertEquals(generatedSeed.length(), Constants.SEED_LENGTH_MAX);
    }
}
