import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import Controller.ChaineBinaire;
import Controller.TripleDes;
import Model.MasterKey;

public class TripleDesTest {
    
    TripleDes triple_des;

    @Before
    public void setup(){
        triple_des = new TripleDes(Arrays.asList(MasterKey.createMasterKey(), MasterKey.createMasterKey(), MasterKey.createMasterKey()));
    }   


    @Test
    public void testCrypte() {
        String message_clair = "salut à tous";
        String message_crypté = triple_des.crypte(message_clair);
        assertNotEquals(message_clair, message_crypté);
        assertTrue(ChaineBinaire.estBinaire(message_crypté));
        assertTrue(message_crypté.length() % 64 == 0);
    }

    @Test
    public void testDecrypte() {
        String message = "salut à tous @info, étonnant non ? 😀";
        assertEquals(message, triple_des.decrypte(triple_des.crypte(message)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testTripleDesIllegalArgument(){
        new TripleDes(Arrays.asList(MasterKey.createMasterKey(), MasterKey.createMasterKey()));
    }
}
