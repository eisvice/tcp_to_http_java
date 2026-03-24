package com.httpfromtcp.internal.request;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public class Request {
    private RequestLine requestLine;

    public Request (Reader reader) throws IOException {
        String CRLF = "\r\n";
        try (StringWriter writer = new StringWriter()) {
            reader.transferTo(writer);

            String httpMessage = writer.toString();
            String[] httpMessageParts = httpMessage.split(CRLF);
            if (httpMessageParts.length == 0) {
                throw new IOException("empty http message");
            }

            setRequestLine(new RequestLine(httpMessageParts[0]));
        }
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }
}
