import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.httpfromtcp.internal.request.Request;

public class RequestTest {

    @Test
    public void TestRequestLineParse() {
        // Test: Good GET Request line
        Request r = assertDoesNotThrow(
            () -> new Request(
                new StringReader("GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            )
        );
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "HTTP/1.1");
        assertEquals(r.getRequestLine().getMethod(), "GET");
        assertEquals(r.getRequestLine().getRequestTarget(), "/");

        // Test: Good GET Request line with path
        r = assertDoesNotThrow(
            () -> new Request(
                new StringReader("GET /coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            )
        );
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "HTTP/1.1");
        assertEquals(r.getRequestLine().getMethod(), "GET");
        assertEquals(r.getRequestLine().getRequestTarget(), "/coffee");

        // Test: Good POST Request with path
        r = assertDoesNotThrow(
            () -> new Request(
                new StringReader("POST /coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            )
        );
        assertNotNull(r);
        assertEquals(r.getRequestLine().getHttpVersion(), "HTTP/1.1");
        assertEquals(r.getRequestLine().getMethod(), "POST");
        assertEquals(r.getRequestLine().getRequestTarget(), "/coffee");

        // Test: Invalid number of parts in request line
        assertThrows(
            IOException.class, 
            () -> new Request(new StringReader("/coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n"))
        );

        // Test: Invalid method (out of order) Request line
        assertThrows(
            IOException.class, 
            () -> new Request(new StringReader("/coffee GET HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n"))
        );

        // Test: Invalid version in Request line
        assertThrows(
            IOException.class, 
            () -> new Request(new StringReader("GET /coffee TCP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n"))
        );
    }
}
