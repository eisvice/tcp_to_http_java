package com.httpfromtcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;


public class Main {
    public static final int bufSize = 16;
    public static final byte[] br = new byte[]{'\n'};
    public static final String EOF = "___EOF___";

    public static void main(String[] args) {
        try (
            InputStream inputStream = new FileInputStream("messages.txt");
        ) {
            BlockingQueue<String> lines = getLinesChannel(inputStream);
            while (true) {
                String line = lines.take();
                if (line.equals(EOF)) break;
                System.out.println(line);
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