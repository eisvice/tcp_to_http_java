package com.httpfromtcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.httpfromtcp.helpers.BytesHelper;

public class Main {
    public static final int bufSize = 8;
    public static final byte[] br = new byte[]{'\n'};

    public static void main(String[] args) {
        // System.out.println("I hope I get the job!");
        try (InputStream inputStream = new FileInputStream("messages.txt")) {
            // ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8);
            byte[] buf = new byte[bufSize];

            byte[] line = new byte[] {};
            while (inputStream.read(buf) != -1) {
                int idx = BytesHelper.indexOf(buf, br);
                if (idx != -1) {
                    line = BytesHelper.concatenateByteArrays(
                        new byte[][]{"read: ".getBytes(), line, Arrays.copyOfRange(buf, 0, idx+1)}
                    );
                    System.out.write(line);
                    System.out.flush();

                    line = Arrays.copyOfRange(buf, idx+1, buf.length);
                } else {
                    line = BytesHelper.concatenateByteArrays(new byte[][]{line, buf});
                }
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}