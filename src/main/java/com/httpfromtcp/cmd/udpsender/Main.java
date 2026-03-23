package com.httpfromtcp.cmd.udpsender;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        try {
            new UDPSender().run();
        } catch (SocketException | UnknownHostException e) {
            System.out.println("error while sending UDP packet: " + e.toString());
        }

    }

}
