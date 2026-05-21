package com.httpfromtcp.cmd.httpserver;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.httpfromtcp.internal.response.StatusCode;
import com.httpfromtcp.internal.server.HandlerError;
import com.httpfromtcp.internal.server.Server;

public class Main {
    public static final int port = 42069;

    public static void main(String[] args) {
        CountDownLatch shutDownLatch = new CountDownLatch(1);

        try(Server server = new Server(port)) {
            System.out.println("Server started on port: " + port);
            server.serve(
                (oStream, request) -> {
                    System.out.println(request.getRequestLine().getRequestTarget());
                    if (request.getRequestLine().getRequestTarget().equals("/yourproblem")) {
                        return new HandlerError(StatusCode.StatusBadRequest, "Your problem is not my problem\n");
                    }
                    if (request.getRequestLine().getRequestTarget().equals("/myproblem")) {
                        return new HandlerError(StatusCode.StatusInternalError, "Woopsie, my bad\n");
                    }
                    try {
                        oStream.write("All good, frfr\n".getBytes());
                    } catch (IOException exception) {
                        System.err.println(exception.getMessage());
                    }
                    return null;
                }
            );

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
