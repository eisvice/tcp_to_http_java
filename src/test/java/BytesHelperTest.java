import static com.httpfromtcp.helpers.BytesHelper.copy;
import static com.httpfromtcp.helpers.BytesHelper.indexOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class BytesHelperTest {

    @Test
    public void testIndexOf() {
        int testOne = indexOf(new byte[] {'s', 's', 'w', '\n', 'f', 'n'}, new byte[] {'\n'});
        assertEquals(testOne, 3);

        int testTwo = indexOf(new byte[] {'f', 'r', 'o', 'g', 'g'}, new byte[] {'r', 'o'});
        assertEquals(testTwo, 1);

        int testThree = indexOf(new byte[] {'f', 'r', 'o', 'g', 'g'}, new byte[] {'r', 'r'});
        assertEquals(testThree, -1);

        int testFour = indexOf(new byte[] {'f', 'r', 'o', 'g', 'g'}, new byte[] {'v'});
        assertEquals(testFour, -1);

        int testFive = indexOf(new byte[] {'f', 'r', 'f', 'r', 'f', 'g'}, new byte[] {'f', 'g'});
        assertEquals(testFive, 4);
    }

    @Test
    public void testCopy() {
        // destination length is more than the source length
        byte[] dst = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] result = copy(dst, new byte[] {24, 25, 26, 27});
        assertEquals(result.length, 8);
        assertEquals(result[0], 24);
        assertEquals(result[7], 8);
    }
}
