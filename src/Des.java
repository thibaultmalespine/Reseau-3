
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

    /**
     * initialise la masterKey et créé tab_cles
     */
    public Des(){
        masterKey = new int[64];
        for (int i = 0; i < 64; i++) {
            masterKey[i] = r.nextInt(2);
        }
        
        ArrayList<int[]> tab_cles = new ArrayList<>();
    }

    public ArrayList<Integer>  crypte(String message_clair){
        message_clair.replaceAll(" ", "_");
        
        ArrayList<Integer> message_en_binaire = stringToBits(message_clair);
        
        return message_en_binaire;
    
    }

    /**
     * Transforme une chaine de caractère en tableau d'entier (chaque entier correspond à un charactère codé sur 7 bits)
     * @param message
     * @return
     */
    private ArrayList<Integer> stringToBits(String message){
        ArrayList<Integer> bits = new ArrayList<>();
        for (char c : message.toCharArray()) {
            bits.add(Integer.valueOf((Integer.toBinaryString((int)c))));  

        }
        return bits;
    }



    public ArrayList<ArrayList<Integer>>  decoupage(ArrayList<Integer> bloc, int taille_bloc){
        ArrayList<ArrayList<Integer>> blocDecoupé = new ArrayList<>();

        while(bloc.size() % (taille_bloc/8) != 0) {
            bloc.add(00000000);
        }


        for (int i = 0; i < bloc.size(); i+=taille_bloc/8) {
            ArrayList<Integer> sousBloc = new ArrayList<>() ;
            for (int j = 0; j < taille_bloc/8; j++) {
                sousBloc.add(bloc.get(j));
            }
            blocDecoupé.add(sousBloc);
        }

        return blocDecoupé;
    }


    public ArrayList<Integer> permutation(int[] perm_initiale, ArrayList<Integer> bloc){
        ArrayList<Integer> tableau_permuté = new ArrayList<>();

        for (int indicePermutation : perm_initiale) {
            tableau_permuté.get(indicePermutation) = bloc.get(indicePermutation);
        }

        return tableau_permuté; 
    }


    public static void main(String[] args) {
        Des des = new Des();
        //System.out.println(des.crypte("coucou"));
        ArrayList<ArrayList<Integer>> decoupage = (des.decoupage(des.crypte("coucou"), 64));
        System.out.println(des.permutation(perm_initiale ,decoupage.get(0)));
    }

}
