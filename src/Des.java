
import java.util.ArrayList;
import java.util.Random;

public class Des {
    private Random r = new Random();
    static int taille_bloc = 64;
    static int taille_sous_bloc = 32;
    static int nb_ronde = 1;
    static int[] tab_decalage = new int[64];
    static int[] perm_initiale = {
        58,50,42,34,26,18,10,2,
        60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6,
        64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1,
        59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5,
        63,55,47,39,31,23,15,7
    };
    static int[] S = {
        16,7,20,21,29,12,28,17,
        1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };
    static int[] E = {
        32,1,2,3,4,5,
        4,5,6,7,8,9,
        8,9,10,11,12,13,
        12,13,14,15,16,17,
        16,17,18,19,20,21,
        20,21,22,23,24,25,
        24,25,26,27,28,29,
        28,29,30,31,32,1
    };
    int[] masterKey;

    public Des(){
        masterKey = new int[64];
        for (int i = 0; i < 64; i++) {
            masterKey[i] = r.nextInt(2);
        }
        
        ArrayList<int[]> tab_cles = new ArrayList<>();
    }

    public int[] crypte(String message_clair){

        // blocs de 64 bits, soit 8 caractères
        ArrayList<String> message_découpé = new ArrayList<>();
            // Todo : bourrer le message clair pour qu'il soit découpable en tronçon de 64 bits (mod64 = 0)
        for (int i = 0; i < message_clair.length(); i+=8) {        
            message_découpé.add(message_clair.substring(i, i+8));
        }
        System.out.println(message_découpé);

/* 
        byte[] bytes = message_clair.getBytes();
        System.out.println(Arrays.toString(bytes));
        for (byte b : bytes) {
            System.out.println('0'+Integer.toBinaryString(b));
        } 
*/



        int[] cryptage = new int[64];
        return cryptage; 
    }
    
    public static void main(String[] args) {
        Des des = new Des();
        des.crypte("coucou");
    
    }

}
