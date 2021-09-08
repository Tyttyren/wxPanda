package com.wman.utils;

import java.util.Base64;

public class Base64Util {
    //加密
    public static String enCoded(String str){
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
    //解密
    public static String deCoded(String enCoded){
        byte[] decoded = Base64.getDecoder().decode(enCoded);
        String decodeStr = new String(decoded);
        return decodeStr;
    }
}
