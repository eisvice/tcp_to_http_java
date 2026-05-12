package com.httpfromtcp.internal.response;

public enum StatusCode {
    StatusOk(200, "OK"), 
    StatusBadRequest(400, "Bad Request"), 
    StatusInternalError(500, "Internal Server Error");

    private final int code;
    private final String reasonPhrase;

    StatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}


