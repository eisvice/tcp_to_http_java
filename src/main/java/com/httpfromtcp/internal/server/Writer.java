package com.httpfromtcp.internal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
        if (!List.of(WriterState.WriteHeaders, WriterState.WriteBody).contains(this.state)){
            throw new IOException("Wrong order call! Expected status WriteHeaders or WriteBody. Got " + this.state);
        }
        this.state = WriterState.WriteBody;

        byte[] prefix = Integer.toHexString(p.length).toUpperCase().getBytes();
        byte[] bodyChunk = BytesHelper.concatenateByteArrays(new byte[][]{prefix, "\r\n".getBytes(), p, "\r\n".getBytes()});

        this.oStream.write(bodyChunk);
        return bodyChunk.length;
    }

    public int writeChunkedBodyDone() throws IOException {
        if (this.state != WriterState.WriteBody) {
            throw new IOException("Wrong order call! Expected status WriteBody. Got " + this.state);
        }
        this.state = WriterState.Done;

        byte[] endChunk = new String("0\r\n\r\n").getBytes();
        this.oStream.write(endChunk);
        return endChunk.length;
    }

    public void writeTrailers(Header trailers) throws IOException {
        if (this.state != WriterState.WriteBody) {
            throw new IOException("Wrong order call! Expected status WriteBody. Got " + this.state);
        }
        this.state = WriterState.Done;

        StringBuilder builder = new StringBuilder();
        trailers.getHeaders().keySet().forEach(
            (h) -> builder.append(String.format("%s: %s\r\n", h, trailers.getHeader(h)))
        );
        byte[] trailerPart = builder.toString().getBytes();
        
        byte[] trailerChunk = BytesHelper.concatenateByteArrays(new byte[][]{"0\r\n".getBytes(), trailerPart, "\r\n".getBytes()});
        this.oStream.write(trailerChunk);
    }
}
