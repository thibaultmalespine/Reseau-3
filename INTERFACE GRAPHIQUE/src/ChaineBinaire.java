public class ChaineBinaire {
    /**
     * petite fonction pour tester si une chaîne de caractère est une chaîne binaire 
     * @param chaine
     * @return
     */
    static public boolean estBinaire(String chaine) {
        for (char c : chaine.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}
