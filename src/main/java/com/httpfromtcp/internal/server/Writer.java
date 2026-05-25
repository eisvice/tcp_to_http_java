package com.httpfromtcp.internal.server;

import java.io.IOException;
import java.io.OutputStream;

import com.httpfromtcp.helpers.BytesHelper;
import com.httpfromtcp.internal.headers.Header;
import com.httpfromtcp.internal.response.Response;
import com.httpfromtcp.internal.response.StatusCode;

public class Writer {
    private final OutputStream oStream;
    private WriterState state;
    private final Response response;

    public Writer(OutputStream oStream) {
        this.oStream = oStream;
        this.state = WriterState.Initialized;
        this.response = new Response();
    }

    public void writeStatusLine(StatusCode status) throws IOException {
        if (this.state != WriterState.Initialized) {
            throw new IOException("Something went wrong and the Writer instance did not initialized");
        }
        this.state = WriterState.WriteStatusLine;
        this.response.writeStatusLine(status, oStream);;
    }

    public void writeHeaders(Header headers) throws IOException {
        if (this.state != WriterState.WriteStatusLine) {
            throw new IOException("Wrong order call! Expected status WriteStatusLine. Got " + this.state);
        }
        this.state = WriterState.WriteHeaders;
        this.response.setHeaders(headers);
        this.response.writeHeaders(oStream);
    }

    public int writeBody(byte[] p) throws IOException{
        if (this.state != WriterState.WriteHeaders) {
            throw new IOException("Wrong order call! Expected status WriteHeaders. Got " + this.state);
        }
        this.state = WriterState.WriteBody;
        this.response.writeBody(new String(p), oStream);
        return p.length;
    }

    public int writeChunkedBody(byte[] p) throws IOException {
        if (p.length == 0) {
            return writeChunkedBodyDone();
        }

        byte[] suffix = new String("\r\n" + Integer.toHexString(p.length).toUpperCase() + "\r\n").getBytes();
        byte[] bodyChunk = BytesHelper.concatenateByteArrays(new byte[][]{p, suffix});

        this.oStream.write(bodyChunk);
        return bodyChunk.length;
    }

    public int writeChunkedBodyDone() throws IOException {
        byte[] endChunk = new String("0\r\n\r\n").getBytes();
        this.oStream.write(endChunk);
        return endChunk.length;
    }
}
