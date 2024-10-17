import java.util.List;

public class TripleDes {
    private Des des1, des2, des3;
    String masterKey1, masterKey2, masterKey3;
 
    /**
     * Initialise les 3 des nécessaire au fonctionnement du triple des
     * @param masterKeys ensemble de 3 clés
     */
    public TripleDes(List<String> masterKeys) throws IllegalArgumentException{
        if (masterKeys.size() != 3) {
            throw new IllegalArgumentException("l'argument masterKeys doit contenir 3 master keys !");
        }

        masterKey1 = masterKeys.get(0);
        masterKey2 = masterKeys.get(1);
        masterKey3 = masterKeys.get(2);
        des1 = new Des(masterKey1);
        des2 = new Des(masterKey2);
        des3 = new Des(masterKey3);
    }

    /**
     * Crypte un message selon l'algorithme Triple DES
     * @param message_clair
     * @return     
     * */
    public String crypte(String message_clair) {
        String message = des1.crypte(message_clair);
        message = des2.crypte(message);

        return des3.crypte(message);
    }

    /**
     * Décrypte un message crypté avec l'algorithme Triple DES
     * @param message_crypté
     * @return
     */
    public String decrypte(String message_crypté){
        String message = des3.decrypte(message_crypté);
        message = des2.decrypte(message);

        return des1.decrypte(message);
    }
}