package com.httpfromtcp.cmd.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import com.httpfromtcp.internal.headers.Header;
import com.httpfromtcp.internal.response.StatusCode;
import com.httpfromtcp.internal.server.Server;

public class Main {
    public static final int port = 42069;

    public static void main(String[] args) {
        CountDownLatch shutDownLatch = new CountDownLatch(1);

        try(Server server = new Server(port)) {
            System.out.println("Server started on port: " + port);
            server.serve(
                (writer, request) -> {
                    Header headers = new Header();
                    headers.setHeader("Connection", "close");
                    headers.setHeader("Content-Type", "text/html");
                    try {
                        if (request.getRequestLine().getRequestTarget().equals("/yourproblem")) {
                            byte[] responseBody = """
<html>
  <head>
    <title>400 Bad Request</title>
  </head>
  <body>
    <h1>Bad Request</h1>
    <p>Your request honestly kinda sucked.</p>
  </body>
</html>""".getBytes();
                            
                            headers.setHeader("Content-Length", String.valueOf(responseBody.length));
                            writer.writeStatusLine(StatusCode.StatusBadRequest);
                            writer.writeHeaders(headers);
                            writer.writeBody(responseBody);
                        }
                        else if (request.getRequestLine().getRequestTarget().equals("/myproblem")) {
                            byte[] responseBody = """
<html>
  <head>
    <title>500 Internal Server Error</title>
  </head>
  <body>
    <h1>Internal Server Error</h1>
    <p>Okay, you know what? This one is on me.</p>
  </body>
</html>""".getBytes();
                            headers.setHeader("Content-Length", String.valueOf(responseBody.length));
                            writer.writeStatusLine(StatusCode.StatusInternalError);
                            writer.writeHeaders(headers);
                            writer.writeBody(responseBody);
                        }

                        else if (request.getRequestLine().getRequestTarget().contains("/httpbin/")) {
                          Header headersChunk = new Header();
                          headersChunk.setHeader("Content-Type", "text/plain");
                          headersChunk.setHeader("Transfer-Encoding", "chunked");
                          writer.writeStatusLine(StatusCode.StatusOk);
                          writer.writeHeaders(headersChunk);

                          String httpbinPath = request.getRequestLine().getRequestTarget().substring("/httpbin/".length());

                          HttpClient client = HttpClient.newHttpClient();
                          HttpRequest proxyRequest = HttpRequest.newBuilder()
                                                  .uri(URI.create("https://httpbin.org/" + httpbinPath))
                                                  .GET()
                                                  .build();
                          try {
                            HttpResponse<InputStream> response = client.send(proxyRequest, HttpResponse.BodyHandlers.ofInputStream());
                            try (InputStream is = response.body()) {
                                byte[] buf = new byte[256];
                                int n;
                                
                                while ((n = is.read(buf)) != -1) {
                                  writer.writeChunkedBody(Arrays.copyOfRange(buf, 0, n));
                                }
                            }
                          } catch (IOException | InterruptedException e) {
                            System.out.println("error in reading httpbin response: " + e.getMessage());
                          } finally {
                            writer.writeChunkedBodyDone();
                          }

                        } 
                        
                        else {
                        byte[] responseBody = """
<html>
  <head>
    <title>200 OK</title>
  </head>
  <body>
    <h1>Success!</h1>
    <p>Your request was an absolute banger.</p>
  </body>
</html>""".getBytes();
                        headers.setHeader("Content-Length", String.valueOf(responseBody.length));
                        writer.writeStatusLine(StatusCode.StatusOk);
                        writer.writeHeaders(headers);
                        writer.writeBody(responseBody);
                      }
                    } catch (IOException e) {
                        System.out.println("Error while writing a response: " + e.getMessage());
                    }
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
