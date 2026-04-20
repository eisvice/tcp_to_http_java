import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.httpfromtcp.internal.request.Request;

public class RequestTest {

    @Test
    public void TestRequestLineParse() {
        // Test: Good GET Request line
        Request r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n", 
                50
            )
        ));
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "1.1");
        assertEquals(r.getRequestLine().getMethod(), "GET");
        assertEquals(r.getRequestLine().getRequestTarget(), "/");

        // Test: Good GET Request line with path
        r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET /coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n", 
                3
            )
        ));
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "1.1");
        assertEquals(r.getRequestLine().getMethod(), "GET");
        assertEquals(r.getRequestLine().getRequestTarget(), "/coffee");

        // Test: Good POST Request with path
        String line = "POST /coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n";
        r = assertDoesNotThrow(() -> new Request(new ChunkReader(line, line.length())));
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "1.1");
        assertEquals(r.getRequestLine().getMethod(), "POST");
        assertEquals(r.getRequestLine().getRequestTarget(), "/coffee");

        // Test: Invalid number of parts in request line
        IOException exception = assertThrows(
            IOException.class,
            () -> new Request(
                new ChunkReader(
                    "/coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n", 
                    10
                )
            )
        );
        assertTrue(exception.getMessage().contains("invalid request line: "));

        // Test: Invalid method (out of order) Request line
        assertThrows(
            IOException.class,
            () -> new Request(
                new ChunkReader(
                    "/coffee GET HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n", 
                    20
                )
            )
        );

        // Test: Invalid version in Request line
        assertThrows( 
            IOException.class,
            () -> new Request(
                new ChunkReader(
                    "GET /coffee TCP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n",
                    20
                )
            )
        );
    }

    @Test
    public void TestHeaders_ValidSingleHeader() {
        Request r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHost: localhost:42069\r\n\r\n", 
                50
            )
        ));
        assertNotNull(r.getRequestHeaders());
        assertEquals("localhost:42069", r.getRequestHeaders().getHeader("Host"));
        assertFalse(r.getRequestHeaders().isDone());
    }

    @Test
    public void TestHeaders_InvalidSpacingHeader() {
        assertThrows(IOException.class, () -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\n      Host : localhost:42069       \r\n\r\n", 
                3
            )
        ));
    }

    // @Test
    // public void TestHeaders_ValidSingleHeader_WithExtraSpace() {
    //     Header headers = new Header();
    //     byte[] data = "Host:      localhost:42069\r\n\r\n".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals("localhost:42069", headers.getHeader("Host"));
    //     assertEquals(28, n);
    //     assertFalse(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_ValidTwoHeaders_WithExistingHeader() {
    //     Header headers = new Header();
    //     headers.setHeader("host", "localhost:42069");
    //     byte[] data = "User-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals("localhost:42069", headers.getHeader("host"));
    //     assertEquals("curl/7.81.0", headers.getHeader("User-Agent"));
    //     assertEquals(25, n);
    //     assertFalse(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_ValidDone() {
    //     Header headers = new Header();
    //     byte[] data = "\r\nSomething somethind not interesting".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals(2, n);
    //     assertTrue(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_ValidSingleHeader_WithCapitalLetters() {
    //     Header headers = new Header();
    //     byte[] data = "Host: localhost:42069\r\n\r\n".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals("localhost:42069", headers.getHeader("HoSt"));
    //     assertEquals(23, n);
    //     assertFalse(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_InvalidSingleHeader_WithInvalidCharacters() {
    //     Header headers = new Header();
    //     byte[] data = "H©st: localhost:42069\r\n\r\n".getBytes();
        
    //     assertThrows(IOException.class, () -> headers.parse(data));
    //     assertFalse(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_ValidSingleHeader_WithValidCharacters() {
    //     Header headers = new Header();
    //     byte[] data = "Host_123_!: localhost:42069\r\n\r\n".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals("localhost:42069", headers.getHeader("host_123_!"));
    //     assertEquals(29, n);
    //     assertFalse(headers.isDone());
    // }

    // @Test
    // public void TestHeaders_ValidTwoHeaders_WithSameFieldName() {
    //     Header headers = new Header();
    //     headers.setHeader("Set-Person", "lane-loves-go");
    //     byte[] data = "Set-Person: prime-loves-zig\r\nAccept: */*\r\n\r\n".getBytes();
        
    //     int n = assertDoesNotThrow(() -> headers.parse(data));
    //     assertNotNull(headers);
    //     assertEquals("lane-loves-go, prime-loves-zig", headers.getHeader("Set-Person"));
    //     assertEquals(29, n);
    //     assertFalse(headers.isDone());
    // }
}
