package com.httpfromtcp;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.httpfromtcp.helpers.BytesHelper;

public class Main {
    public static final int bufSize = 8;
    public static final byte[] br = new byte[]{'\n'};

    public static void main(String[] args) {
        try (
            InputStream inputStream = new FileInputStream("messages.txt");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bufSize);
        ) {
            byte[] buf = new byte[bufSize];
            int bytesRead, idx;

            while ((bytesRead = inputStream.read(buf)) != -1) {
                if ((idx = BytesHelper.indexOf(buf, br)) != -1) {
                    outputStream.write(buf, 0, idx);
                    System.out.printf("read: %s\n", outputStream.toString());
                    outputStream.reset();
                    outputStream.write(buf, idx+1, bytesRead - idx - 1);
                    continue;
                }
                outputStream.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}