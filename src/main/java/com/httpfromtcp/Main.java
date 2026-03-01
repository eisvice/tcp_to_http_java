package com.httpfromtcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

import com.httpfromtcp.helpers.BytesHelper;

public class Main {
    public static final int bufSize = 8;
    public static final byte br = '\n';

    public static void main(String[] args) {
        // System.out.println("I hope I get the job!");
        try (InputStream inputStream = new FileInputStream("messages.txt")) {
            // ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8);
            byte[] buf = new byte[bufSize];

            // List<Byte> line = new ArrayList<>();

            while (inputStream.read(buf) != -1) {
                
                byte[] b = BytesHelper.concatenateByteArrays(new byte[][]{"read: ".getBytes(), buf, "\n".getBytes()});
                System.out.write(b);
                System.out.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}