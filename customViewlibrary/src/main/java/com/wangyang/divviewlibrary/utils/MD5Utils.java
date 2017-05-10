package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * MD5工具
 */


import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    //对用户设置的密码进行MD5加密
    public static String encode(String password) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            //把密码转换成字节
            byte[] pass = instance.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : pass) {
                //与11111111进行加密，也就是十六进制中的ff，
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }
    //输入文件的位置
    public static String getFileMd5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            //数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");

            while ((len = fis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            byte[] digest = messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0").append(hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}