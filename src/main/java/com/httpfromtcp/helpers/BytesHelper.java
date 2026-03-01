package com.httpfromtcp.helpers;

import java.util.ArrayList;
import java.util.List;

public class BytesHelper {

    public static byte[] concatenateByteArrays(byte[][] args) {
        // System.out.printf("args length: %d\n", args.length);
        int arrLength = 0;
        for (int i = 0; i < args.length; i++) {
            // System.out.printf("arg[%d] length: %d\n", i, args[i].length);
            arrLength += args[i].length;
        }
        // System.out.printf("array length: %d\n", arrLength);
        byte[] resultByteArr = new byte[arrLength];


        int currentIndex = 0;
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args[i].length; j++) {
                resultByteArr[currentIndex] = args[i][j];
                currentIndex++;
            }
        }
        return resultByteArr;
    }

    public static byte[][] splitByteArray(byte[] arr) {
        return null;
    }

    public static List<Byte> toList(byte[] arr) {
        List<Byte> resultList = new ArrayList<>();
        for (byte c: arr) {
            resultList.add((Byte) c);
        }

        return resultList;
    }

    public static boolean contains(byte[] arr, byte[] sub) {
        if (sub.length == 0 || sub == null) return true;

        if (sub.length > arr.length) return false;

        boolean match = false;
        for (int i = 0; i < arr.length - sub.length; i++) {

            if (arr[i] == sub[0]) {
                for (int j = 0; j < sub.length; j++) {
                    if (arr[i+j] != sub[j]) {
                        match = false;
                        break;
                    }
                }
                match = true;
            }

            if (match) break;

        }

        return match;
    }
}
