import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.httpfromtcp.internal.headers.Header;

public class HeadersTest {

    @Test
    public void TestHeaders_ValidSingleHeader() {
        Header headers = new Header();
        byte[] data = "Host: localhost:42069\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("localhost:42069", headers.getHeader("Host"));
        assertEquals(23, n);
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_InvalidSpacingHeader() {
        Header headers = new Header();
        byte[] data = "       Host : localhost:42069       \r\n\r\n".getBytes();

        assertThrows(IOException.class, () -> headers.parse(data));
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidSingleHeader_WithExtraSpace() {
        Header headers = new Header();
        byte[] data = "Host:      localhost:42069\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("localhost:42069", headers.getHeader("Host"));
        assertEquals(28, n);
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidTwoHeaders_WithExistingHeader() {
        Header headers = new Header();
        headers.setHeader("host", "localhost:42069");
        byte[] data = "User-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("localhost:42069", headers.getHeader("host"));
        assertEquals("curl/7.81.0", headers.getHeader("User-Agent"));
        assertEquals(25, n);
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidDone() {
        Header headers = new Header();
        byte[] data = "\r\nSomething somethind not interesting".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals(2, n);
        assertTrue(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidSingleHeader_WithCapitalLetters() {
        Header headers = new Header();
        byte[] data = "Host: localhost:42069\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("localhost:42069", headers.getHeader("HoSt"));
        assertEquals(23, n);
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_InvalidSingleHeader_WithInvalidCharacters() {
        Header headers = new Header();
        byte[] data = "H©st: localhost:42069\r\n\r\n".getBytes();
        
        assertThrows(IOException.class, () -> headers.parse(data));
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidSingleHeader_WithValidCharacters() {
        Header headers = new Header();
        byte[] data = "Host_123_!: localhost:42069\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("localhost:42069", headers.getHeader("host_123_!"));
        assertEquals(29, n);
        assertFalse(headers.isDone());
    }

    @Test
    public void TestHeaders_ValidTwoHeaders_WithSameFieldName() {
        Header headers = new Header();
        headers.setHeader("Set-Person", "lane-loves-go");
        byte[] data = "Set-Person: prime-loves-zig\r\nAccept: */*\r\n\r\n".getBytes();
        
        int n = assertDoesNotThrow(() -> headers.parse(data));
        assertNotNull(headers);
        assertEquals("lane-loves-go, prime-loves-zig", headers.getHeader("Set-Person"));
        assertEquals(29, n);
        assertFalse(headers.isDone());
    }
}
