import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TripleDesTest {
    
    TripleDes triple_des;
    Des des;

    @Before
    public void setup(){
        triple_des = new TripleDes();
        des = new Des(true);
    }   


    @Test
    public void testCrypte() {
        String message_clair = "salut à tous";
        String message_crypté = triple_des.crypte(message_clair);
        assertNotEquals(message_clair, message_crypté);
        assertTrue(des.estBinaire(message_crypté));
        assertTrue(message_crypté.length() % 64 == 0);

        System.out.println(message_crypté.length());
    }

    @Test
    public void testDecrypte() {
        String message = "salut à tous @info, étonnant non ? 😀";
        assertEquals(message, triple_des.decrypte(triple_des.crypte(message)));
    }
}
