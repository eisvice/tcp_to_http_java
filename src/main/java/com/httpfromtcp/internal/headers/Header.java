package com.httpfromtcp.internal.headers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.httpfromtcp.helpers.BytesHelper;

public class Header {
    private Map<String, String> headers;
    private boolean done = false;
    public static final byte[] CRLF = {'\r', '\n'};

    public Header() {
        this.headers = new HashMap<>();
    }

    public int parse(byte[] data) throws IOException {
        int idx = BytesHelper.indexOf(data, CRLF);
        if (idx == -1) {
            setDone(false);
            return 0;
        }

        if (idx == 0) {
            setDone(true);
            return 2;
        }

        String headerLine = new String(data, 0, idx, StandardCharsets.UTF_8);
        headerLine = headerLine.strip();
        int delimeterIdx = headerLine.indexOf(":");
        String fieldName = headerLine.substring(0, delimeterIdx);
        String fieldValue = headerLine.substring(delimeterIdx + 1).strip();

        if (!fieldName.equals(fieldName.strip())) {
            throw new IOException("invalid header format. spaces after field's name: " + headerLine);
        }

        this.headers.put(fieldName, fieldValue);
        return idx + 2;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String fieldName, String fieldValue) {
        this.headers.put(fieldName, fieldValue);
    }

    public String getHeader(String fieldName) {
        return this.headers.get(fieldName);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
