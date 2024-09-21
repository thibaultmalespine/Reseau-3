import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class DesTest {
    Des des;

    @Before
    public void setup(){
        des = new Des();
    }

    @Test
    public void testTextTobitString() {
        String message = "alkndezndklznedlz";
        String test1 = des.textTobitString(message);
        assertEquals(test1.length(), message.length()*8);
        String test2 = des.textTobitString("a");
        assertEquals(test2, "0"+Integer.toBinaryString('a'));
    }

    @Test
    public void testBitsToString() {
        int[] bloc = {0,1,0,1,1,1};
        String s = "010111";
        assertEquals(des.bitsToString(bloc), s);
    }

    @Test
    public void testStringToBits() {
        String s = "010111";
        int[] bloc = {0,1,0,1,1,1};
        assertEquals(Arrays.toString(des.stringToBits(s)), Arrays.toString(bloc));
    }

    @Test
    public void testDécoupageEnSousBloc() {
        ArrayList<String> test1 = des.découpageEnSousBloc("01011010", 2);
        assertEquals(test1.size(), 4);
        assertEquals(test1.get(0), "01");
        assertEquals(test1.get(2), "10");
    }

    @Test
    public void testPermutation() {
        // construction du message de test
        String message = "1111";
        for (int i = 0; i < 60; i++) {
            message += "0";
        }
        String message_permuté = "";
        // construction de la permutation avec perm_initiale du message
        for (int i = 0; i<64; i++){
            if (i == 7 || i == 15 || i == 39 || i == 47) {
                message_permuté += "1";
            }
            else { message_permuté += "0";}
        }
        String test1 = des.permutation(Des.perm_initiale, message);
        assertEquals(test1, message_permuté);

        // autre test
        message = "1010";
        message_permuté = "0101";
        int[] table_de_permutation = {2,1,4,3};
        String test2 = des.permutation(table_de_permutation, message); 
        assertEquals(test2, message_permuté);

    }

    @Test
    public void testDecalleGauche() {
        int[] message = {1,0,0,1,1,0};
        
        assertEquals(Arrays.toString(des.decalleGauche(message, 0)), Arrays.toString(message));
        assertEquals(Arrays.toString(des.decalleGauche(message, 1)), Arrays.toString(new int[]{0,0,1,1,0,0}));
        assertEquals(Arrays.toString(des.decalleGauche(message, 2)), Arrays.toString(new int[]{0,1,1,0,0,0}));
    }

    @Test
    public void testDécoupageGaucheDroite() {
        String texte_à_decouper = "00001111"
                                 +"00001111";

        // test de la partie gauche
        assertEquals(des.découpageGaucheDroite(texte_à_decouper, 8)[0], "0000"
                                                                                      +"0000");
        // test de la partie droite
        assertEquals(des.découpageGaucheDroite(texte_à_decouper, 8)[1], "1111"
                                                                                      +"1111");
    }

    @Test
    public void testRecollageGaucheDroite() {
        assertEquals(des.recollageGaucheDroite(new String[]{"00000000","11111111"}, 8), "0000111100001111");
    }

   
}
