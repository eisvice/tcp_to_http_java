import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.httpfromtcp.internal.request.Request;
import com.httpfromtcp.internal.request.RequestState;

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
                "GET / HTTP/1.1\r\nHost: localhost:42069\r\n" + 
                    "User-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n", 
                3
            )
        ));
        assertNotNull(r.getRequestHeaders());
        assertEquals("localhost:42069", r.getRequestHeaders().getHeader("host"));
        assertEquals("curl/7.81.0", r.getRequestHeaders().getHeader("user-agent"));
        assertEquals("*/*", r.getRequestHeaders().getHeader("accept"));
        assertEquals(r.getRequestState(), RequestState.DONE);
    }

    @Test
    public void TestHeaders_EmptyHeaders() {
        Request r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\n\r\n", 
                3
            )
        ));

        assertNotNull(r);
        assertTrue(r.getRequestHeaders().getHeaders().keySet().isEmpty());
    }

    @Test
    public void TestHeaders_MalformedHeader() {
        assertThrows(IOException.class, () -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHost localhost:42069       \r\n\r\n", 
                3
            )
        ));
    }

    @Test
    public void TestHeaders_DuplicateHeaders() {
        Request r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHost: localhost:42069\r\nHost: duplicate:8080\r\n\r\n", 
                3
            )
        ));
        assertNotNull(r.getRequestHeaders());
        assertEquals("localhost:42069, duplicate:8080", r.getRequestHeaders().getHeader("host"));
    }

    @Test
    public void TestHeaders_CaseInsensitiveHeaders() {
        Request r = assertDoesNotThrow(() -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHOST: localhost:42069\r\nUSER-AGENT: curl/7.81.0\r\n\r\n", 
                3
            )
        ));
        assertNotNull(r.getRequestHeaders());
        assertEquals("localhost:42069", r.getRequestHeaders().getHeader("host"));
        assertEquals("curl/7.81.0", r.getRequestHeaders().getHeader("user-agent"));
    }

    @Test
    public void TestHeaders_MissingEndOfHeaders() {
        assertThrows(IOException.class, () -> new Request(
            new ChunkReader(
                "GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0", 
                3
            )
        ));
    }
}
