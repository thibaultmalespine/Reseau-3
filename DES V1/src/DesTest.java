import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
    public void testStringTobitString() {
        String message = "alkndezndklznedlz";
        String test1 = des.stringToBitString(message);
        assertEquals(test1.length(), message.length()*8);
        String test2 = des.stringToBitString("a");
        assertEquals(test2, "0"+Integer.toBinaryString('a'));

        message = "espace ici";
        String test3 = des.stringToBitString(message);
        assertEquals(test3.length(), message.length()*8);

    }

    @Test
    public void testBitStringToString() {
        assertEquals(des.bitStringToString(des.stringToBitString("a")), "a");
        assertEquals(des.bitStringToString(des.stringToBitString("salut")), "salut");
        assertEquals(des.bitStringToString(des.stringToBitString("espace dans la phrase")), "espace dans la phrase");
    }

    @Test
    public void testEstBinaire() {
        assertEquals(des.estBinaire("00001010"), true);
        assertEquals(des.estBinaire("101010121"), false);
    }

    @Test
    public void testBitsToStringBits() {
        int[] bloc = {0,1,0,1,1,1};
        String s = "010111";
        assertEquals(des.bitsToStringBits(bloc), s);
    }

    @Test
    public void testStringBitsToBits() {
        String s = "010111";
        int[] bloc = {0,1,0,1,1,1};
        assertEquals(Arrays.toString(des.stringBitsToBits(s)), Arrays.toString(bloc));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionStringBitsToBits() {
        String s = "mauvaise chaîne";
        des.stringBitsToBits(s);
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
    public void testInvPermutation() {
        // Test sur un cas simple
        String message = "0101";
        int[] tab_perm = new int[]{3,1,4,2};
        String message_permuté = des.permutation(tab_perm,message);
        assertEquals(message_permuté, "0011");
        assertEquals(des.invPermutation(tab_perm, message_permuté), message);

        // Test sur un bloc de taille 32 et E
        String message32bits = "01110000"
                        +"10001111"
                        +"11001011"
                        +"10011010";

        assertEquals(message32bits, des.invPermutation(Des.E, des.permutation(Des.E,message32bits)));
    }


    @Test
    public void testDecalleGauche() {
        int[] message = {1,0,0,1,1,0};
        
        assertEquals(Arrays.toString(des.decalleGauche(message, 0)), Arrays.toString(message));
        assertEquals(Arrays.toString(des.decalleGauche(message, 1)), Arrays.toString(new int[]{0,0,1,1,0,0}));
        assertEquals(Arrays.toString(des.decalleGauche(message, 2)), Arrays.toString(new int[]{0,1,1,0,0,0}));
    }

    @Test
    public void testXOR() {
        assertEquals(des.XOR("0101", "0011"), "0110");  
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionXOR() {
        des.XOR("00", "1111");
    }

    @Test
    public void testSubstitutionS() {
        assertEquals(des.substitutionS(Des.S, "000000"), "1110");
        assertEquals(des.substitutionS(Des.S, "000001"), "0000");
        assertEquals(des.substitutionS(Des.S, "011110"), "0111");
        assertEquals(des.substitutionS(Des.S, "111111"), "1101");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionTailleSubstitutionS() {
        des.substitutionS(Des.S, "00000");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionTypeSubstitutionS() {
        des.substitutionS(Des.S, "chaîne non binaire");
    }

    @Test
    public void testCrypte() {
        String message = "salut à tous";
        assertNotEquals(des.crypte(message), message);
        assertTrue(des.estBinaire(des.crypte(message)));    
    }

    @Test
    public void testDecrypte() {
        String message = "salut à tous @info, étonnant non ? 😀";
        assertEquals(message, des.decrypte(des.crypte(message)));
    }
   
}