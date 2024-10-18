package Data;
import java.util.Random;

/**
 * Classe pour générer les masterkeys 
 */
public class MasterKey {
    static private String masterKey;
    static private Random r;

    /**
     * Créer et retourne une nouvelle masterkey
     * @return
     */
    public static String createMasterKey() {
        r = new Random();
        masterKey = "";
        for (int i = 0; i < 64; i++) {
            masterKey += String.valueOf(r.nextInt(2));
        }
        return masterKey;
    }
    
}
