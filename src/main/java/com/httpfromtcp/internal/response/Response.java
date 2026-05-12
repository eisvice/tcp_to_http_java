package com.httpfromtcp.internal.response;

import java.io.IOException;
import java.io.OutputStream;

import com.httpfromtcp.internal.headers.Header;

public class Response {
    private String statusLine;
    private Header headers;
    private final OutputStream stream;

    public Response(OutputStream stream) {
        this.headers = new Header();
        this.stream = stream;
    }

    public void writeStatusLine(StatusCode code) throws IOException {
        this.statusLine = String.format(
            "HTTP/1.1 %d %s\r\n", 
            code.getCode(), 
            code.getReasonPhrase()
        );
        stream.write(this.statusLine.getBytes());
    }

    public void setDefaultHeaders(int contentLen) {
        headers.setHeader("Content-Length", String.valueOf(contentLen));
        headers.setHeader("Connection", "close");
        headers.setHeader("Content-Type", "text/plain");
    }

    public void writeHeaders() throws IOException {
        stream.write(headers.toString().getBytes());
    }
}
