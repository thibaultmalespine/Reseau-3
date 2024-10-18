public class TripleDes {
    private Des des1, des2, des3;

    public TripleDes(){
        des1 = new Des();
        des2 = new Des();
        des3 = new Des();
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