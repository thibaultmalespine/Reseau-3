import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Algo.Des;
import Data.MasterKey;
import Data.TablePermutation;
import Data.TableS;
import Utils.ChaineBinaire;

public class DesTest {
    Des des;

    @Before
    public void setup(){
        des = new Des(MasterKey.createMasterKey());
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
    public void testDécoderCharactère() {
        /**
         * Code UTF8 :
         * charactère | code hexa | code binaire
         *      A  	  |    41     |   01000001
         *      Ç  	  |   c3 87   |   1100001110000111
         *      Ⓘ    |  E2 92 BE  |  111000101001001010111110
         *      𘘐    | F0 98 98 90 | 11110000100110001001100010010000
         */

        assertEquals(des.décoderCharactère("01000001"), "A");
        assertEquals(des.décoderCharactère("1100001110000111"), "Ç");
        assertEquals(des.décoderCharactère("111000101001001010111110"), "Ⓘ");
        assertEquals(des.décoderCharactère("11110000100110001001100010010000"), "𘘐");
        assertNotEquals(des.décoderCharactère("11110000100110001001100010010001"), "𘘐");
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
        String test1 = des.permutation(TablePermutation.perm_initiale, message);
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

        assertEquals(message32bits, des.invPermutation(TablePermutation.E, des.permutation(TablePermutation.E,message32bits)));
    }


    @Test
    public void testDecalleGauche() {
        String message = "100110";
        
        assertEquals(des.decalleGauche(message, 0), message);
        assertEquals(des.decalleGauche(message, 1), "001100");
        assertEquals(des.decalleGauche(message, 2), "011000");
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
        assertEquals(des.substitutionS(TableS.getTables(0), "000000"), "1110");
        assertEquals(des.substitutionS(TableS.getTables(0), "000001"), "0000");
        assertEquals(des.substitutionS(TableS.getTables(0), "011110"), "0111");
        assertEquals(des.substitutionS(TableS.getTables(0), "111111"), "1101");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionTailleSubstitutionS() {
        des.substitutionS(TableS.getTables(0), "00000");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionTypeSubstitutionS() {
        des.substitutionS(TableS.getTables(0), "chaîne non binaire");
    }

    @Test
    public void testCrypte() {
        String message = "salut à tous";
        String message_crypté = des.crypte(message);

        assertNotEquals(message_crypté, message);
        assertTrue(ChaineBinaire.estBinaire(message_crypté)); 
        
        assertEquals(message, des.decrypte(message_crypté));

    }

    @Test
    public void testDecrypte() {
        String message = "salut à tous @info, étonnant non ? 😀";
        assertEquals(message, des.decrypte(des.crypte(message)));
    }
   
}