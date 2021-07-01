package com.ut.user.utils;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMD5(String src) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5bytes = messageDigest.digest(src.getBytes());
        String encrypt = Hex.toHexString(md5bytes);
        return encrypt;
    }

}
