package com.httpfromtcp.internal.response;

import java.io.IOException;
import java.io.OutputStream;

import com.httpfromtcp.internal.headers.Header;

public class Response {
    private String statusLine;
    private Header headers = new Header();

    public void writeStatusLine(StatusCode code, OutputStream oStream) throws IOException {
        this.statusLine = String.format(
            "HTTP/1.1 %d %s\r\n", 
            code.getCode(), 
            code.getReasonPhrase()
        );
        oStream.write(this.statusLine.getBytes());
    }

    public void setDefaultHeaders(int contentLen) {
        headers.setHeader("Content-Length", String.valueOf(contentLen));
        headers.setHeader("Connection", "close");
        headers.setHeader("Content-Type", "text/plain");
    }

    public Header getHeaders() {
        return this.headers;
    }

    public void setHeaders(Header headers) {
        this.headers = headers;
    }

    public void writeHeaders(OutputStream oStream) throws IOException {
        oStream.write(headers.toString().getBytes());
    }

    public void writeBody(String body, OutputStream oStream) throws IOException {
        // body = body + "\r\n";
        oStream.write(body.getBytes());
    }
}
