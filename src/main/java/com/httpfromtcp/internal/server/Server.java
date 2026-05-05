package com.httpfromtcp.internal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
            String response = "HTTP/1.1 200 OK\r\n" + // Status line
                "Content-Type: text/plain\r\n" + // Example header
                "Content-Length: 13\r\n" + // Content length header
                "\r\n" + // Blank line to separate headers from the body
                "Hello World!\n"; // Body
            oStream.write(response.getBytes());
        } catch (IOException e) {
            System.err.println("Handle error: " + e.getMessage());
        }
    }
}
