package com.httpfromtcp.internal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.httpfromtcp.internal.response.Response;
import com.httpfromtcp.internal.response.StatusCode;

public class Server implements AutoCloseable {
    private final ServerSocket socket;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private AtomicBoolean closed = new AtomicBoolean(false);

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    public void serve() {
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
        try (clientSocket; OutputStream oStream = clientSocket.getOutputStream()) {
            Response resp = new Response(oStream);
            resp.writeStatusLine(StatusCode.StatusOk);
            resp.setDefaultHeaders(0);
            resp.writeHeaders();
        } catch (IOException e) {
            System.err.println("Handle error: " + e.getMessage());
        }
    }
}
