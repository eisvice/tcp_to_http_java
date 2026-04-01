package com.httpfromtcp.internal.request;

import java.io.IOException;

public class RequestLine {

    private String httpVersion;
    private String requestTarget;
    private String method;

    public RequestLine(String startLine) throws IOException {
        parseRequestLineString(startLine);
    }

    private void parseRequestLineString(String startLine) throws IOException {
        if (startLine.isEmpty()) {
            throw new IOException("empty request line");
        }

        String[] startLineParts = startLine.split(" ");
        if (startLineParts.length != 3) {
            throw new IOException(String.format("invalid request line: ", startLine));
        }

        setMethod(startLineParts[0]);
        setRequestTarget(startLineParts[1]);
        setHttpVersion(startLineParts[2]);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) throws IOException {
        if (!httpVersion.equals("HTTP/1.1")) {
            throw new IOException("unsupported HTTP version: " + httpVersion);
        }
        this.httpVersion = httpVersion;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public void setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) throws IOException {
        if (!method.equals(method.toUpperCase())) {
            throw new IOException("method is not capitalized: " + method);
        }
        this.method = method;
    }

    @Override
    public String toString() {
        return "RequestLine [httpVersion=" + httpVersion + ", requestTarget=" + requestTarget + ", method=" + method
                + "]";
    }
}
