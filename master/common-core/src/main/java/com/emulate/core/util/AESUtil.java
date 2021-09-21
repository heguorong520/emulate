package com.emulate.core.util;

import com.emulate.core.excetion.CustomizeException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

/**
 * 密钥加签
 */
public class AESUtil {
    public static final String PASSWORD_KEY = "4Z9Bnjk9piw8hN0Y";
    public static final String SIGN_KEY = "hza9zarTV1dluCxN";
    private static final String SECRET = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * AES加密ECB模式PKCS7Padding填充方式
     * @param str 字符串
     * @param key 密钥
     * @return 加密字符串
     * @throws Exception 异常信息
     */
    public static String encrypt(String str, String key) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, SECRET));
            byte[] doFinal = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(doFinal));
        }catch (Exception e){
            throw new CustomizeException("加密失败");
        }
    }

    /**
     * AES解密ECB模式PKCS7Padding填充方式
     * @param str 字符串
     * @param key 密钥
     * @return 解密字符串
     * @throws Exception 异常信息
     */
    public static String decrypt(String str, String key) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, SECRET));
            byte[] doFinal = cipher.doFinal(Base64.getDecoder().decode(str));
            return new String(doFinal);
        }catch (Exception e){
            throw new CustomizeException("解密失败");
        }
    }
    public static void main(String[] args) throws Exception {
        String str = "a1234567";
        System.out.println("字符串：" + str);
        String encryptStr = AESUtil.encrypt(str, "AES454-HTJSQ9-IT");
        System.out.println("加密后字符串：" + encryptStr);
        String decryptStr = AESUtil.decrypt(encryptStr, "AES454-HTJSQ9-IT");
        System.out.println("解密后字符串：" + decryptStr);
    }

}
