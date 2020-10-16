/*
 * Copyright (C) 2016 The CloudManage Project
 * All right reserved.
 * author: xunyicao
 */
package com.komlin.libcommon.util.encryp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lipeiyong
 */
public class AESUtils {
    private static final String algorithmStr = "AES/ECB/PKCS5Padding";

    private static final String AES = "AES";

    private static final String key = "B17F4E7F83C2061DE0740F09EE56447C";

    /**
     * 生成密钥
     *
     * @throws Exception
     */
    public static String getKey(int keyLength) throws Exception {
        // 实例化
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        // 设置密钥长度
        keyGen.init(128);
        // 生成一个实现指定转换的 Cipher 对象。
        Cipher cipher = Cipher.getInstance(algorithmStr);

        // 返回密钥的16进制编码
        return parseByte2HexStr(keyGen.generateKey().getEncoded());
    }

    /**
     * 加密 <br/>
     * 字符串-》utf-8-》加密后为byte[]-》16进制
     *
     * @param content 需要加密的内容
     * @return
     */
    public static String encrypt(String content) {
        try {
            return encrypt(content, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptToByte(String content) {
        byte[] keyStr = parseHexStr2Byte(key);
        SecretKeySpec key = new SecretKeySpec(keyStr, AES);

        try {
            byte[] byteContent = content.getBytes("utf-8");
            // algorithmStr
            Cipher cipher = Cipher.getInstance(algorithmStr);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(byteContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密 <br/>
     * 字符串-》utf-8-》加密后为byte[]-》16进制
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encrypt(String content, String password) throws Exception {
        byte[] keyStr = parseHexStr2Byte(password);
        SecretKeySpec key = new SecretKeySpec(keyStr, AES);

        byte[] byteContent = content.getBytes("utf-8");
        // algorithmStr
        Cipher cipher = Cipher.getInstance(algorithmStr);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(byteContent);

        return parseByte2HexStr(result);
    }

    /**
     * 解密 <br/>
     * 16进制-》解密后为byte[]-》utf-8-》字符串
     *
     * @param content 待解密内容
     * @return
     */
    public static String decrypt(String content) {
        try {
            return decrypt(content, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密 <br/>
     * 16进制-》解密后为byte[]-》utf-8-》字符串
     *
     * @param content 待解密内容
     * @return
     */
    public static String decrypt(byte[] content) {
        byte[] keyStr = parseHexStr2Byte(key);
        SecretKeySpec key = new SecretKeySpec(keyStr, AES);

        try {
            Cipher cipher = Cipher.getInstance(algorithmStr);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密 <br/>
     * 16进制-》解密后为byte[]-》utf-8-》字符串
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String content, String password) throws Exception {
        byte[] keyStr = parseHexStr2Byte(password);
        SecretKeySpec key = new SecretKeySpec(keyStr, AES);
        // algorithmStr
        Cipher cipher = Cipher.getInstance(algorithmStr);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(parseHexStr2Byte(content));
        return new String(result, "utf-8");
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
