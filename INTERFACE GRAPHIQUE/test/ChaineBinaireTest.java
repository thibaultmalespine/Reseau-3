import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Controller.ChaineBinaire;

public class ChaineBinaireTest {
    @Test
    public void testEstBinaire() {
        assertEquals(ChaineBinaire.estBinaire("00001010"), true);
        assertEquals(ChaineBinaire.estBinaire("101010121"), false);
    }
}
