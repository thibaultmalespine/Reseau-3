import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import Data.MasterKey;

public class MasterKeyTest {
    @Test
    public void testCreateMasterKey() {
        String master_key = MasterKey.createMasterKey(); 
        for (int i = 0; i < master_key.length(); i++) {
            char bit = master_key.charAt(i);
            assertEquals(bit == '0' || bit == '1', true);
        }

        assertEquals(master_key.length(), 64);

        String master_key_2 = MasterKey.createMasterKey();

        assertNotEquals(master_key, master_key_2);
    }
}
