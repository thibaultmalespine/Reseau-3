import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class MasterKeyTest {
    @Test
    public void testCreateMasterKey() {
        int[] master_key = MasterKey.createMasterKey(); 
        for (int bit : master_key) {
            assertEquals(bit == 0 || bit == 1, true);
        } 

        assertEquals(master_key.length, 64);

        int[] master_key_2 = MasterKey.createMasterKey();

        assertNotEquals(master_key, master_key_2);
    }
}
