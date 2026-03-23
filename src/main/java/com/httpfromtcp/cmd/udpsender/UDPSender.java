package com.httpfromtcp.cmd.udpsender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPSender {

    private final int port = 42069;
    private final DatagramSocket socket;
    private InetAddress address;

    public UDPSender() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in); this.socket) {
            byte[] buf;
            while(!socket.isClosed()) {
                System.out.print("> ");
                String userInput = scanner.nextLine() + "\n";
    
                buf = userInput.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                try {
                    socket.send(packet);
                } catch(IOException e) {
                    System.out.printf("error while receiving a packet %s", e.toString());
                }
            }
        }
    }
}
