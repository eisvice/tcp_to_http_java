package com.httpfromtcp.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.httpfromtcp.helpers.BytesHelper;

public class Request {
    public static final byte[] CRLF = {'\r', '\n'};
    public static final int bufferSize = 8;
    private RequestLine requestLine;
    private RequestState requestState;

    public Request (InputStream in) throws IOException {
        requestFromReader(in);
    }

    public void requestFromReader(InputStream in) throws IOException {
        byte[] buf = new byte[bufferSize];
        int readToIndex = 0;
        setRequestState(RequestState.INITIALIZED);
        try {
            while (getRequestState() != RequestState.DONE) {
                if (readToIndex >= buf.length) {
                    byte[] newBuf = Arrays.copyOf(buf, buf.length * 2);
                    buf = newBuf;
                }

                int bytesRead = in.read(buf, readToIndex, buf.length - readToIndex);
                if (bytesRead == -1) {
                    setRequestState(RequestState.DONE);
                    break;
                }

                readToIndex += bytesRead;
                int bytesParsed = parse(Arrays.copyOfRange(buf, 0, readToIndex));
                buf = Arrays.copyOfRange(buf, bytesParsed, buf.length);
                readToIndex -= bytesParsed;
            }
        } catch (IOException e) {
            throw new IOException("error while parsing a request: " + e.getMessage());
        } finally {
            if (in != null) in.close();
        }
    }

    public int parse(byte[] data) throws IOException {
        int bytesParsed = 0;
        switch (getRequestState()) {
            case INITIALIZED:
                bytesParsed = parseRequestLine(data);
                if (bytesParsed > 0) {
                    setRequestState(RequestState.DONE);
                }
                return bytesParsed;
            case DONE:
                throw new IOException("error: trying to read data in a done state");
            default:
                throw new IOException("error: unknown state");
        }
    }

    public int parseRequestLine(byte[] data) throws IOException {
        int endOfRequestLine = BytesHelper.indexOf(data, CRLF);
        if (endOfRequestLine == -1) {
            return 0;
        }
        String dataString = new String(data, 0, endOfRequestLine, StandardCharsets.UTF_8);
        setRequestLine(new RequestLine(dataString));
        return endOfRequestLine;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public RequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(RequestState requestState) {
        this.requestState = requestState;
    }
}
