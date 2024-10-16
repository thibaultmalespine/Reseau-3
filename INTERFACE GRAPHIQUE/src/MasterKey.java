import java.util.Random;

public class MasterKey {
    static private int[] masterKey;
    static private Random r;

    public static int[] createMasterKey() {
        r = new Random();
        masterKey = new int[64];
        for (int i = 0; i < 64; i++) {
            masterKey[i] = r.nextInt(2);
        }
        return masterKey;
    }
    
}
