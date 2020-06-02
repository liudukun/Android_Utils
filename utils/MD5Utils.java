package com.liudukun.dkchat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5Utils {



    /**
     * * MD5加密以byte数组表示的字符串
     *
     * @param input 目标字符串
     * @return MD5加密后的字符串
     */

    public static String getMD5String(String input)  {
        DKLog.d("md5",input);
        //创建MD5加密对象
        String result = input;
        if (input != null) {
            MessageDigest md = null; //or "SHA-1"
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while (result.length() < 32) { //31位string
                result = "0" + result;
            }
        }
        return result;
    }

}
