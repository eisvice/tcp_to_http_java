package com.httpfromtcp.internal.server;

import java.io.IOException;
import java.io.OutputStream;

import com.httpfromtcp.internal.response.Response;
import com.httpfromtcp.internal.response.StatusCode;

public class HandlerError {
    private StatusCode statusCode;
    private String message;

    public HandlerError(StatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void write(OutputStream oStream) {
        try {
            Response response = new Response();
            response.writeStatusLine(statusCode, oStream);
            response.setDefaultHeaders(this.message.length());
            response.writeHeaders(oStream);
            oStream.write(message.getBytes());
        } catch(IOException exception) {
            System.err.println("Errors in writing a handle error: " + exception.getMessage());
        }
    }
}
