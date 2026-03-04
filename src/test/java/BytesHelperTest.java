import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static com.httpfromtcp.helpers.BytesHelper.indexOf;


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


}
