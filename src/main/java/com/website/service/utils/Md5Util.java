package com.website.service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

    private static final String keyNo1 = "some random value JKLMNOP_!#";

    private static final char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static final String digest(String s) {
        String encKey = keyNo1;
        String returnString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes("UTF-8"));
            md.update(encKey.getBytes("UTF-8"));
            md.update("Copyright Client Server International Inc.".getBytes("UTF-8"));
            byte[] digest = md.digest();
            returnString = bytes2HexStr(digest);
        } catch (NoSuchAlgorithmException var5) {
            returnString = null;
        } catch (Exception var6) {
            returnString = null;
        }
        return returnString;
    }

    public static final String bytes2HexStr(byte[] bcd) {
        StringBuilder s = new StringBuilder(bcd.length * 2);

        for(int i = 0; i < bcd.length; ++i) {
            s.append(hexChar[bcd[i] >>> 4 & 15]);
            s.append(hexChar[bcd[i] & 15]);
        }
        return s.toString();
    }
}
