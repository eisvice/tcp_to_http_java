package com.httpfromtcp.cmd.tcplistener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.httpfromtcp.internal.request.Request;


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
    
                    Request request = new Request(socket.getInputStream());
                    System.out.printf(
                        "Request line:\n" +
                            "- Method: %s\n" +
                            "- Target: %s\n" +
                            "- Version: %s\n",
                        request.getRequestLine().getMethod(),
                        request.getRequestLine().getRequestTarget(),
                        request.getRequestLine().getHttpVersion()
                    );
                    System.out.println("Headers:");
                    for (String key: request.getRequestHeaders().getHeaders().keySet()) {
                        System.out.printf("- %s: %s\n", key, request.getRequestHeaders().getHeader(key));
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
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

}