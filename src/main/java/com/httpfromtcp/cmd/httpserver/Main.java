package com.httpfromtcp.cmd.httpserver;

import java.util.concurrent.CountDownLatch;

import com.httpfromtcp.internal.server.Server;

public class Main {
    public static final int port = 42069;

    public static void main(String[] args) {
        CountDownLatch shutDownLatch = new CountDownLatch(1);

        try(Server server = new Server(port)) {
            System.out.println("Server started on port: " + port);
            server.serve();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown signal received!");
                shutDownLatch.countDown();
            }));

            shutDownLatch.await();
        } catch(Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            System.out.println("server gracefully stopped");
        }
    }
}
