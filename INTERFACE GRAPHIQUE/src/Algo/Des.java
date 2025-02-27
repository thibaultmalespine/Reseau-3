package Algo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import Data.TablePermutation;
import Data.TableS;
import Utils.ChaineBinaire;

/**
 * Algorithme DES
 */
public class Des {
    static int taille_bloc = 64;
    static int taille_sous_bloc = 32;
    static int nb_ronde = 16;
    static int[] tab_decalage = new int[64];

    String masterKey;
    ArrayList<String> tab_cles;

    /**
     * Initialise la masterKey, puis créer et remplit tab_cles en générant les n clés
     * @param utf8 indique si l'encodage se fait en utf8 où non
     */
    public Des(String masterKey){
        this.masterKey = masterKey;

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
            String bout_de_message_permuté = permutation(TablePermutation.perm_initiale ,bout_de_message);
            
            // Etape 4 : découpage en deux bloc (haut/bas)
            String Gn = bout_de_message_permuté.substring(0,32);
            String Dn = bout_de_message_permuté.substring(32,64);

            for (int i = 0; i < nb_ronde; i++) {
                // Etape 5 : calcul de Dn+1 et Gn+1
                String Gn_plus_1 = Dn;
                String Dn_plus_1 = XOR(Gn, F(i, Dn));

                Gn = Gn_plus_1;
                Dn = Dn_plus_1;
            }

            // Etape 6 : on réunit G et D
            String bout_de_message_après_ronde = Gn + Dn;

            // Etape 7 : permutation inverse de perm_initiale
            String bout_de_message_crypté = permutation(TablePermutation.perm_inverse, bout_de_message_après_ronde);
       
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
            // b est un entier signé que l'on transforme en entier non signé avant de le transformer en binaire
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
    public String bitStringToString(String binary_string) {
 
        String message = "";
        int i = 0;
        
        while (i < binary_string.length()){
            if (binary_string.substring(i, i+3).equals("110") && i+16 < binary_string.length()) {
                message += décoderCharactère(binary_string.substring(i, i+16));
                i += 16;
            }
            else if (binary_string.substring(i, i+4).equals("1110") && i+24 < binary_string.length()){
                message += décoderCharactère(binary_string.substring(i, i+24));
                i += 24;            
            }
            else if(binary_string.substring(i, i+5).equals("11110") && i+32 < binary_string.length()){
                message += décoderCharactère(binary_string.substring(i, i+32));
                i += 32;
            }
            else{
                message += décoderCharactère(binary_string.substring(i, i+8));
                i += 8;
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
        String Kn = permutation(TablePermutation.PC1, masterKey);
        
        // Séparation de la clé en deux
        String Gauche = Kn.substring(0,28);
        String Droite = Kn.substring(28, 56);

        // On décalle vers la gauche  
        Gauche = decalleGauche(Gauche, décallage); 
        Droite = decalleGauche(Droite, décallage);  

        // On recolle les blocs
        Kn = Gauche + Droite;

        // Seconde permutation à l'aide de PC2
        Kn = permutation(TablePermutation.PC2, Kn);

        // Ajout de la nouvelle clé à tab_cles
        tab_cles.add(Kn);
    }
       
    /**
     * Effectue un décallage vers la gauche de l'ensemble des caractères de bloc,
     * les derniers indice sont remplacés par des 0
     * 
     * @param bloc chaîne de caractère où l'on souhaite effectuer un décallage 
     * @param nbCran nombre de cran dont on souhaite décaller l'index
     * @return Une nouvelle chaîne de charactère, décallé
     */
    public String decalleGauche(String bloc, int nbCran){
        String bloc_décallé = bloc.substring(nbCran, bloc.length());
        for (int i = 0; i < nbCran; i++) {
            bloc_décallé += "0";
        }
        return bloc_décallé;
    }

    /**
     * fonction F de l'algorithme DES
     * 
     * @param Kn clé numéro n
     * @param Dn bloc droit numéro n
     * @return
     */
    public String F(int numeroRonde, String Dn) {
        String Dn_prime = permutation(TablePermutation.E, Dn);
        
        String Dn_etoile = XOR(Dn_prime, tab_cles.get(numeroRonde));
        
        ArrayList<String> blocs = découpageEnSousBloc(Dn_etoile, 6);
        
        String resultat_substitution = "";
        for (String bloc : blocs) {
            resultat_substitution += substitutionS(TableS.getTables(numeroRonde%8), bloc);
        }   

        return permutation(TablePermutation.P, resultat_substitution);
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

        String resultat = "";
        for (int i = 0; i < chaîne_binaire_1.length(); i++) {
            int bit_chaine_binaire_1 = Character.getNumericValue(chaîne_binaire_1.charAt(i));
            int bit_chaine_binaire_2 = Character.getNumericValue(chaîne_binaire_2.charAt(i));

            resultat += String.valueOf(bit_chaine_binaire_1 ^ bit_chaine_binaire_2);
        }
        return resultat;
    }

    /**
     * Sous fonction de la fonction F
     * @param S fonction de subtitution S, type int[] 
     * @param bloc chaîne binaire à substituer, de taille 6
     * @return une nouvelle chaîne binaire de taille 4
     */
    public String substitutionS(int[] S, String bloc) throws IllegalArgumentException{

        if (bloc.length() != 6 || !ChaineBinaire.estBinaire(bloc)) {
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
            String bout_de_message_permuté = permutation(TablePermutation.perm_initiale, bout_de_message);
            
            // Etape 3 : faire les rondes dans le sens inverse
            String Gn = bout_de_message_permuté.substring(0,32);
            String Dn = bout_de_message_permuté.substring(32,64);
            
            for (int i = nb_ronde-1; i >= 0; i--) {
                String Dn_moins_1 = Gn;
                String Gn_moins_1 = XOR(Dn, F(i, Dn_moins_1));

                Gn = Gn_moins_1;
                Dn = Dn_moins_1;
            }
            
            // Etape 4 : on réunit G et D
            String bout_de_message_après_ronde = Gn + Dn;

            // Etape 5 : permutation inverse de perm_initiale
            String bout_de_message_décrypté = permutation(TablePermutation.perm_inverse, bout_de_message_après_ronde);
       
            // Etape 6 : assemblage des blocs décrypté
            message_décrypté += bout_de_message_décrypté; 
        }

        // Etape 7 : on passe le texte binaire en texte clair
        return bitStringToString(message_décrypté).trim();
    }
}