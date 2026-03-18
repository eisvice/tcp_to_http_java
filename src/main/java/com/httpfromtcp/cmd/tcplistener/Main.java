package com.httpfromtcp.cmd.tcplistener;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;


public class Main {
    public static final int bufSize = 16;
    public static final byte[] br = new byte[]{'\n'};
    public static final String EOF = "___EOF___";
    public static final int port = 42069;

    public static void main(String[] args) {
        try (
            ServerSocket serverSocket = new ServerSocket(port);
        ) {
            System.out.println("+++++++++++++++++");
            System.out.printf("Start listening on port: %d\n", serverSocket.getLocalPort());
            System.out.println("+++++++++++++++++");
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("<- Connection has been accepted");
    
                    BlockingQueue<String> lines = getLinesChannel(socket.getInputStream());
                    while (true) {
                        String line = lines.take();
                        if (line.equals(EOF)) break;
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.printf(
                        "Error while accepting a connection: %s", 
                        e.toString()
                    );
                } finally {
                    System.out.println("Connection has been closed ->");
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static BlockingQueue<String> getLinesChannel(InputStream inputStream) {
        BlockingQueue<String> lines = new LinkedBlockingQueue<>();

        CompletableFuture.runAsync(() -> {
            try (inputStream) {
                StringBuilder currentLineContents = new StringBuilder();
                byte[] buf = new byte[bufSize];
                int bytesRead;

                while ((bytesRead = inputStream.read(buf)) != -1) {
                    String str = new String(buf, 0, bytesRead, StandardCharsets.UTF_8);
                    String[] parts = str.split("\n", -1);

                    for (int i = 0; i < parts.length - 1; i++) {
                        currentLineContents.append(parts[i]);
                        lines.put(currentLineContents.toString());
                        currentLineContents.setLength(0);
                    }
                    currentLineContents.append(parts[parts.length - 1]);
                }

                if (currentLineContents.length() > 0) {
                    lines.put(currentLineContents.toString());
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                lines.add(EOF);
            }
        });

        return lines;
    }
}