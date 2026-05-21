package com.httpfromtcp.internal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import com.httpfromtcp.internal.request.Request;
import com.httpfromtcp.internal.response.Response;
import com.httpfromtcp.internal.response.StatusCode;

public class Server implements AutoCloseable {
    private final ServerSocket socket;
    private BiFunction<OutputStream, Request, HandlerError> handler;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private AtomicBoolean closed = new AtomicBoolean(false);

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    public void serve(BiFunction<OutputStream, Request, HandlerError> handler) {
        this.handler = handler;
        Thread thread = new Thread(this::listen);
        thread.start();
    }

    @Override
    public void close() throws IOException {
        closed.set(true);
        socket.close();
        executor.shutdown();
    }

    private void listen() {
        while (!closed.get()) {
            try {
                Socket clientSocket = socket.accept();
                executor.submit(() -> handle(clientSocket));
            } catch(IOException e) {
                if (closed.get()) break;
                System.err.println("Error excepting connection: " + e.getMessage());
                continue;
            }
        }
    }

    private void handle(Socket clientSocket) {
        try (
            OutputStream oStream = clientSocket.getOutputStream(); 
            InputStream iStream = clientSocket.getInputStream();
        ) {
            Request request = new Request(iStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            HandlerError hError = this.handler.apply(buf, request);
            if (hError != null) {
                hError.write(oStream);
                oStream.flush();
                return;
            }
            Response resp = new Response();
            resp.writeStatusLine(StatusCode.StatusOk, oStream);
            resp.setDefaultHeaders(buf.toByteArray().length);
            resp.writeHeaders(oStream);
            oStream.write(buf.toByteArray());
            oStream.flush();
        } catch (IOException e) {
            System.out.println("Handle func exception: " + e.getMessage());
            try (OutputStream oStream = clientSocket.getOutputStream()) {
                HandlerError hError = new HandlerError(StatusCode.StatusBadRequest, e.getMessage());
                hError.write(clientSocket.getOutputStream());
            } catch(IOException exception) {}
        } finally {
            try {
                clientSocket.close();
            } catch (Exception exception) {}
        }
    }
}
