package com.topaiebiz.giftcard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @description: 对称加解密
 * @author: Jeff Chen
 * @date: created in 下午2:10 2018/2/7
 */
public class AESUtil {

    private static Logger logger = LoggerFactory.getLogger(AESUtil.class);

    /**
     * 卡密加密key
     */
    public static final String CARD_ENCRYPT_KEY = "27tjuxrMk1ueWnwKMHWX1A==";

    /**
     * 生产key
     * @throws NoSuchAlgorithmException
     */
    public static void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key key = keyGenerator.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    /**
     * 加密
     * @param plaintext
     * @param encryKey
     * @return
     */
    public static String encrypt(String plaintext,String encryKey) {
        // 根据密钥的Base64表示生产密钥的字节数组
        byte[] keyBytes = Base64.getDecoder().decode(encryKey);
        Key key = new SecretKeySpec(keyBytes, "AES");
        // AES对称加密算法
        // 加密模式
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 解密获得明文的字节数组
            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes());
            // 生成密文的Base64表示
            String ciphertext = Base64.getEncoder().encodeToString(cipherBytes);
            return ciphertext;
        } catch (Exception e) {
            logger.error("加密错误：{}",plaintext,e);
        }
        return plaintext;
    }

    /**
     * 解密
     * @param ciphertext
     * @param encryKey
     * @return
     */
    public static String decrypt(String ciphertext,String encryKey) {
        byte[] keyBytes = Base64.getDecoder().decode(encryKey);
        Key key = new SecretKeySpec(keyBytes, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            // 解密模式
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 明文的字节数组
            byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext.getBytes()));
            // 解码为字符串表示
            String plaintext = new String(plainBytes);
            return plaintext;
        } catch (Exception e) {
            logger.error("解密错误：{}",ciphertext,e);
        }
        return ciphertext;
    }
}
