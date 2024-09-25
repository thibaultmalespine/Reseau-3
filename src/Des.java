
import java.util.ArrayList;
import java.util.Arrays;
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
    static int[] PC1 = {
        57,49,41,33,25,17,9,63,55,47,39,31,23,15,
        1,58,50,42,34,26,18,07,62,54,46,30,30,22,
        10,2,59,51,43,35,27,14,6,61,53,45,37,29,
        19,11,3,60,52,44,36,21,13,5,28,20,12,4
    };
    static int[] PC2 = {
        14,17,11,24,1,5,
        3,28,15,6,21,10,
        23,19,12,4,26,8,
        16,7,27,20,13,2,
        41,52,31,37,47,55,
        30,40,51,45,33,48,
        44,49,39,56,34,53,
        46,42,50,36,29,32
    };
    static int[] P = {
        16,7,20,21,29,12,28,17,
        1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };
    static int[] S = {
        14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,
        0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,
        4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,
        15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13
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
    ArrayList<int[]> tab_cles;
    /**
     * Initialise la masterKey et créé puis remplit tab_cles en générant les n clés
     */
    public Des(){
        // génère la master key
        masterKey = new int[64];
        for (int i = 0; i < 64; i++) {
            masterKey[i] = r.nextInt(2);
        }

        // génères les n clés
        tab_cles = new ArrayList<>();
        // Table de décallage en fonction du numéro de ronde 
        int[] table_décallage = {1,1,2,2,2,2,2,1,2,2,2,2,2,2,1};
        for (int i = 0; i < nb_ronde; i++) {
            génèreClé(table_décallage[i]);
        }
    }

    public void crypte(String message_clair){
        // Etape 1 : texte en clair
        message_clair.replaceAll(" ", "_");
        
       
        // Etape 2 : texte en binaire
        String message_en_binaire = stringToBitString(message_clair);
        
        // Etape 3 : découpage en blocs de 64 bits
        ArrayList<String> message_découpé = découpageEnSousBloc(message_en_binaire, 64);

        for (String bout_de_message : message_découpé) {
            // Etape 4 : permutation initiale
            String bout_de_message_permuté = permutation(perm_initiale ,bout_de_message);
            
            // Etape 5 : découpage en deux bloc (haut/bas)
            String Gn = bout_de_message_permuté.substring(0,32);
            String Dn = bout_de_message_permuté.substring(32,64);

            for (int i = 0; i < nb_ronde; i++) {
                // Etape 6 : calcul de Dn+1 et Gn+1
                String Gn_plus_1 = Dn;
                String Dn_plus_1 = XOR(Gn, F(tab_cles.get(i), Dn));

                Gn = Gn_plus_1;
                Dn = Dn_plus_1;
            }

        }


    }

    /**
     * Transforme une chaîne de caractère dans sa représentation binaire 
     * @param message Chaîne de caractère à transformer
     * @return Une nouvelle chaîne de caractère, contient la représentation binaire
     */
    public String stringToBitString(String message){
        message.replaceAll(" ", "_");

        String message_en_binaire = "";
        for (char c : message.toCharArray()) {
            message_en_binaire += "0"+Integer.toBinaryString(c);
        }

        return message_en_binaire;
        
    }

    /**
     * Transforme un tableau d'entier en sa représentation sous forme de chaîne binaire
     * @param bloc 
     * @return une chaîne binaire
     */
    public String bitsToStringBits(int[] bloc){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bloc.length; i++) {
            sb.append(bloc[i]);
        }
        
        return sb.toString();
    }

    /**
     * Transfomre une chaîne de caractère binaire en tableau d'entier 
     *
     * @param message Doit être une chaîne de caractère binaire
     * @return 
     */
    public  int[] stringBitsToBits(String message) throws IllegalArgumentException{
        for (char c : message.toCharArray()) {
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("La chaîne doit être binaire !");
            }
        }

        int[] bits = new int[message.length()];
        for (int i = 0; i < message.length(); i++) {
            bits[i] = Character.getNumericValue(message.charAt(i));
        }

        return bits;
    }

    /**
     * Prend une chaîne binaire est la découpe en sous chaîne binaire
     * @param message_en_binaire 
     * @param taille_bloc
     * @return une liste de chaîne binaire
     */
    public ArrayList<String> découpageEnSousBloc(String message_en_binaire, int taille_bloc){
        
        ArrayList<String> message_découpé = new ArrayList<>();
        
        while(message_en_binaire.length() % taille_bloc != 0){
            message_en_binaire += "0";
        }

        for (int i = 0; i < message_en_binaire.length(); i+=taille_bloc) {
            message_découpé.add(message_en_binaire.substring(i, i+taille_bloc));
        }

        return message_découpé; 
    }

    /**
     * Permute les bits d'une chaîne binaire à l'aide d'une table de permutation
     * @param table_de_permutation
     * @param message chaîne binaire
     * @return une nouvelle chaîne binaire obtenu après permutation
     */
    public String permutation(int[] table_de_permutation, String message){
        String message_permuté = "";

        for (int indice : table_de_permutation) {
            message_permuté += message.charAt(indice-1);
        }

        return message_permuté;
    }

    /**
     * Fonction qui génère une Kn clé, puis l'ajoute à tab_cles 
     * @param décallage nombre de bits décaller vers la gauche en fonction du numéro de ronde
     */
    private void génèreClé(int décallage){
        
        // Première permutation à l'aide de PC1 
        String Kn = permutation(PC1, bitsToStringBits(masterKey));
        

        // Séparation de la clé en deux
        String[] Kn_séparé = découpageGaucheDroite(Kn, 14);
        String Gauche = Kn_séparé[0];
        String Droite = Kn_séparé[1];


        // On décalle vers la gauche  
        Gauche = bitsToStringBits(decalleGauche(stringBitsToBits(Gauche), décallage)); 
        Droite = bitsToStringBits(decalleGauche(stringBitsToBits(Droite), décallage));  

        // On recolle les blocs
        Kn = recollageGaucheDroite(new String[]{Gauche,Droite}, 14);

        // Seconde permutation à l'aide de PC2
        Kn = permutation(PC2, Kn);

        // Ajout de la nouvelle clé à tab_cles
        tab_cles.add(stringBitsToBits(Kn));

    }
    
    /**
     * Scinde un tableau en deux : sa partie gauche et sa partie droite
     * @param tableau doit avoir une longueur qui est un chiffre pair
     * @param largeur_tableau largeur du tableau d'entrée
     * @return
     * @throws IllegalArgumentException
     */
    public String[] découpageGaucheDroite(String tableau, int largeur_tableau) throws IllegalArgumentException {
        if (largeur_tableau % 2 != 0) {
            throw new IllegalArgumentException("la largeur du tableau doit être un nombre pair !");
        }

        String Gauche = new String();
        String Droite  = new String();
        
        for (int i = 0; i < tableau.length(); i++) {
            if (i % largeur_tableau < largeur_tableau/2) {
                Gauche += tableau.charAt(i); 
               
            }
            else {
                Droite += tableau.charAt(i);
            }
        }

        return new String[]{Gauche,Droite};

    }
    
    /**
     * Effectue un décallage vers la gauche de l'ensemble des entiers contenu dans bloc,
     * les derniers indice sont remplacés par des 0
     * 
     * @param bloc tableau où l'on souhaite effectuer un décallage 
     * @param nbCran nombre de cran dont on souhaite décaller l'index
     * @return Un nouveau tableau décallé
     */
    public int[] decalleGauche(int[] bloc, int nbCran){
        int[] bloc_decalle = new int[bloc.length];

        for (int i = 0; i < bloc.length; i++) {
            if (i + nbCran < bloc.length){
                bloc_decalle[i] = bloc[i + nbCran];
            }
            else {
                bloc_decalle[i] = 0;
            }
        }

        return bloc_decalle;
    }

    /**
     * Colle deux tableaux entre eux
     * @param tableaux_à_coller sous forme de String[], doit avoir une taille de 2
     * @param largeur_tableau largeur du tableau de sortie
     * @return 
     * @throws IllegalArgumentException
     */
    public String recollageGaucheDroite(String[] tableaux_à_coller, int largeur_tableau)throws IllegalArgumentException{
        if (tableaux_à_coller.length != 2) {
            throw new IllegalArgumentException("le paramètre tableaux_à_coller doit être de taille 2 !");
        }
        
        int taille_total_textes = tableaux_à_coller[0].length() + tableaux_à_coller[1].length();
        
        StringBuilder sb = new StringBuilder(tableaux_à_coller[0]+tableaux_à_coller[1]);
        for (int i = 0; i < taille_total_textes; i++) {
            if (i % largeur_tableau < largeur_tableau/2) {
                sb.setCharAt(i, tableaux_à_coller[0].charAt((i/largeur_tableau)*(largeur_tableau/2) + i%largeur_tableau));
            }
            else {
                sb.setCharAt(i, tableaux_à_coller[1].charAt((i/largeur_tableau)*(largeur_tableau/2) + (i%largeur_tableau)-(largeur_tableau/2)));
            }
        }
        return sb.toString();
    }

    /**
     * fonction F de l'algorithme DES
     * 
     * @param Kn clé numéro n
     * @param Dn bloc droit numéro n
     * @return
     */
    public String F(int[] Kn, String Dn) {
        String Dn_prime = permutation(E, Dn);
        
        String Dn_etoile = XOR(Dn_prime, bitsToStringBits(Kn));
        
        ArrayList<String> blocs = découpageEnSousBloc(Dn_etoile, 6);
        
        String resultat_substitution = "";
        for (String bloc : blocs) {
        // faire une fonction substitutionS(S, bloc)

            char[] data_ligne = {bloc.charAt(0), bloc.charAt(5)};
            String binaire_ligne = new String(data_ligne);
            int ligne = Integer.parseInt(binaire_ligne, 2);
            
            char[] data_colonne = {bloc.charAt(1),bloc.charAt(2),bloc.charAt(3),bloc.charAt(4)};
            String binaire_colonne = new String(data_colonne);
            int colonne = Integer.parseInt(binaire_colonne, 2);
            
            
            bloc = Integer.toBinaryString(S[ligne * 16 + colonne]);
            while (bloc.length() < 4) {
                bloc = "0"+bloc;   
            }
            // fin substitutionS

            resultat_substitution += bloc;
        }   

        return permutation(P, resultat_substitution);
    }

    /**
     * Fait un Xor entre deux chaîne binaire de même longeur
     * @param chaîne_binaire_1
     * @param chaîne_binaire_2
     * @return une nouvelle chaîne binaire, résultat du Xor
     */
    public String XOR(String chaîne_binaire_1, String chaîne_binaire_2) throws IllegalArgumentException{
        if (chaîne_binaire_1.length() != chaîne_binaire_2.length()) {
            throw new IllegalArgumentException("les deux chaînes binaires doivent être de même longeur !");
        }

        int[] chaîne_binaire_1_to_bits = stringBitsToBits(chaîne_binaire_1);
        int[] chaîne_binaire_2_to_bits = stringBitsToBits(chaîne_binaire_2);
        int[] result = new int[chaîne_binaire_1.length()];
      
        for (int i = 0; i < chaîne_binaire_1_to_bits.length ;i++) {
            result[i] = chaîne_binaire_1_to_bits[i] ^ chaîne_binaire_2_to_bits[i]; 
        }
        return bitsToStringBits(result);
    }


    public static void main(String[] args) {
        Des des = new Des();
        //des.crypte("coucou ");
        for (int[] tab : des.tab_cles) {
            System.out.println(Arrays.toString(tab));
        }
    }
}