
import java.nio.charset.StandardCharsets;
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
    static int[] perm_inverse = {
        40,8,48,16,56,24,64,32,
        39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,
        37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,
        35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,
        33,1,41,9,49,17,57,25
    };
    static int[] PC1 = {
        57,49,41,33,25,17,9,
        1,58,50,42,34,26,18,
        10,2,59,51,43,35,27,
        19,11,3,60,52,44,36,
        63,55,47,39,31,23,15,
        7,62,54,46,38,30,22,
        14,6,61,53,45,37,29,
        21,13,5,28,20,12,4
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
     * Initialise la masterKey, puis créer et remplit tab_cles en générant les n clés
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
        int[] table_décallage = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
        for (int i = 0; i < nb_ronde; i++) {
            génèreClé(table_décallage[i]);
        }
    }

    /**
     * Crypte un message via l'algorithme DES
     * @param message_clair 
     * @return le message crypté sous forme de chaîne binaire
     */
    public String crypte(String message_clair){
        String message_crypté = "";
        
        // Etape 1 : texte en binaire
        String message_en_binaire = stringToBitString(message_clair);
        
        // Etape 2 : découpage en blocs de 64 bits
        ArrayList<String> message_découpé = découpageEnSousBloc(message_en_binaire, 64);

        for (String bout_de_message : message_découpé) {
            // Etape 3 : permutation initiale
            String bout_de_message_permuté = permutation(perm_initiale ,bout_de_message);
            
            // Etape 4 : découpage en deux bloc (haut/bas)
            String Gn = bout_de_message_permuté.substring(0,32);
            String Dn = bout_de_message_permuté.substring(32,64);

            for (int i = 0; i < nb_ronde; i++) {
                // Etape 5 : calcul de Dn+1 et Gn+1
                String Gn_plus_1 = Dn;
                String Dn_plus_1 = XOR(Gn, F(tab_cles.get(i), Dn));

                Gn = Gn_plus_1;
                Dn = Dn_plus_1;
            }

            // Etape 6 : on réunit G et D
            String bout_de_message_après_ronde = Gn + Dn;

            // Etape 7 : permutation inverse de perm_initiale
            String bout_de_message_crypté = permutation(perm_inverse, bout_de_message_après_ronde);
       
            // Etape 8 : assemblage des blocs crypté
            message_crypté += bout_de_message_crypté; 
        }

        return message_crypté;
    }

    /**
     * Transforme une chaîne de caractère dans sa représentation binaire 
     * @param message Chaîne de caractère à transformer
     * @return Une nouvelle chaîne de caractère, contient la représentation binaire
     */
    public String stringToBitString(String message){
        message = message.replaceAll(" ", "_");

        String message_en_binaire = "";
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            message_en_binaire += binary;
        }

        return message_en_binaire;   
    }

    /**
 * Transforme une chaîne binaire dans sa représentation en chaîne de caractères
 * @param bitString Chaîne binaire à transformer
 * @return Une nouvelle chaîne de caractère
 */
    public String bitStringToString(String bitString) {
        String message = "";
        int i = 0;
        
        while (i < bitString.length()){
          
            if (bitString.charAt(i) == '0') {
                message += décoderCharactère(bitString.substring(i, i+8));
                i += 8;
            }
            else if (bitString.substring(i, i+3).equals("110")) {
                message += décoderCharactère(bitString.substring(i, i+16));
                i += 16;
            }
            else if (bitString.substring(i, i+4).equals("1110")){
                message += décoderCharactère(bitString.substring(i, i+24));
                i += 24;            
            }
            else {
                message += décoderCharactère(bitString.substring(i, i+32));
                i += 32;
            }
        }
         
        message = message.replaceAll("_", " ");
        
        return message;
    }

    /**
     * Décode un charactère encoder en utf-8
     * @param chaîne_binaire 
     * @return
     */
    public String décoderCharactère(String chaîne_binaire){
        byte[] octets = new byte[chaîne_binaire.length() / 8];
        int i = 0;
        for(String octet : découpageEnSousBloc(chaîne_binaire, 8)){
            octets[i] = (byte) Integer.parseInt(octet, 2);
            i++;
        }
        return new String(octets, StandardCharsets.UTF_8);
    }

    /**
     * petite fonction pour tester si une chaîne de caractère est une chaîne binaire 
     * @param chaine
     * @return
     */
    public boolean estBinaire(String chaine) {
        for (char c : chaine.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
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
     * Transforme une chaîne de caractère binaire en tableau d'entier 
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
     * Permet d'inverser la permutation effectué sur message_inversé
     * @param table_de_permutation
     * @param message_inversé
     * @return le message binaire avant permutation
     */
    public String invPermutation(int[] table_de_permutation, String message_permuté){
        String message = "";
        int taille_message = Arrays.stream(table_de_permutation).max().getAsInt();

        for (int i = 1; i < taille_message+1; i++) {
            int y = 0;

            while(table_de_permutation[y] != i  ){
                y++;
            }
            message += message_permuté.charAt(y);
        }

        return message;
    }

    /**
     * Fonction qui génère une Kn clé, puis l'ajoute à tab_cles 
     * @param décallage nombre de bits décaller vers la gauche en fonction du numéro de ronde
     */
    private void génèreClé(int décallage){
        
        // Première permutation à l'aide de PC1 
        String Kn = permutation(PC1, bitsToStringBits(masterKey));
        
        // Séparation de la clé en deux
        String Gauche = Kn.substring(0,28);
        String Droite = Kn.substring(28, 56);

        // On décalle vers la gauche  
        Gauche = bitsToStringBits(decalleGauche(stringBitsToBits(Gauche), décallage)); 
        Droite = bitsToStringBits(decalleGauche(stringBitsToBits(Droite), décallage));  

        // On recolle les blocs
        Kn = Gauche + Droite;

        // Seconde permutation à l'aide de PC2
        Kn = permutation(PC2, Kn);

        // Ajout de la nouvelle clé à tab_cles
        tab_cles.add(stringBitsToBits(Kn));
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
            resultat_substitution += substitutionS(S, bloc);
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

    /**
     * Sous fonction de la fonction F
     * @param S fonction de subtitution S, type int[] 
     * @param bloc chaîne binaire à substituer, de taille 6
     * @return une nouvelle chaîne binaire de taille 4
     */
    public String substitutionS(int[] S, String bloc) throws IllegalArgumentException{

        if (bloc.length() != 6 || !estBinaire(bloc)) {
            throw new IllegalArgumentException("le paramètre bloc doit être une chaîne binaire de taille 6");
        }

        int ligne = Integer.parseInt(bloc.substring(0, 1)+bloc.substring(5,6) , 2);
        int colonne = Integer.parseInt(bloc.substring(1,5), 2);
        
        bloc = Integer.toBinaryString(S[ligne * 16 + colonne]);
        while (bloc.length() < 4) {
            bloc = "0"+bloc;   
        }
        return bloc;
    }

    /**
     * décrypte un message crypté à l'aide de l'algorithme DES
     * @param message_crypté
     * @return le message décrypté
     */
    public String decrypte(String message_crypté){

        String message_décrypté = "";
        
        // Etape 1 : découpage en bloc de 64 bits
        ArrayList<String> message_découpé = découpageEnSousBloc(message_crypté, 64);  
        
        for (String bout_de_message : message_découpé) {
            // Etape 2 : faire une permutation à l'aide de perm_init sur chaque sous-bloc
            String bout_de_message_permuté = permutation(perm_initiale, bout_de_message);
            
            // Etape 3 : faire les rondes dans le sens inverse
            String Gn = bout_de_message_permuté.substring(0,32);
            String Dn = bout_de_message_permuté.substring(32,64);
            
            for (int i = nb_ronde-1; i >= 0; i--) {
                String Dn_moins_1 = Gn;
                String Gn_moins_1 = XOR(Dn, F(tab_cles.get(i), Dn_moins_1));

                Gn = Gn_moins_1;
                Dn = Dn_moins_1;
            }
            
            // Etape 4 : on réunit G et D
            String bout_de_message_après_ronde = Gn + Dn;

            // Etape 5 : permutation inverse de perm_initiale
            String bout_de_message_décrypté = permutation(perm_inverse, bout_de_message_après_ronde);
       
            // Etape 6 : assemblage des blocs décrypté
            message_décrypté += bout_de_message_décrypté; 
        }

        // Etape 7 : on passe le texte binaire en texte clair
        return bitStringToString(message_décrypté).trim();
    }

    public static void main(String[] args) {
        Des des = new Des();
        System.out.println(des.decrypte(des.crypte("salut à tous @info, étonnant non ? 😀")));
    }
}