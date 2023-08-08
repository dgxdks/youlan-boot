package com.youlan.common.crypto.helper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.nio.charset.StandardCharsets;

public class AESHelper {

    /**
     * 生成AES秘钥
     */
    public static String generateBase64Key() {
        return Base64.encode(generateKey());
    }

    /**
     * 生成AES秘钥
     */
    public static String generateHexKey() {
        return HexUtil.encodeHexStr(generateKey());
    }

    /**
     * 生成AES秘钥
     */
    public static byte[] generateKey() {
        return SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
    }

    /**
     * AES加密
     *
     * @param plainText 加密内容
     * @param encodeKey AES秘钥
     * @return 加密结果
     */
    public static byte[] encrypt(String plainText, byte[] encodeKey) {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, encodeKey);
        return aes.encrypt(plainText);
    }

    /**
     * AES加密
     *
     * @param plainText 加密内容
     * @param base64Key AES秘钥
     * @return 加密结果
     */
    public static String encryptHex(String plainText, String base64Key) {
        byte[] encrypt = encrypt(plainText, Base64.decode(base64Key));
        return HexUtil.encodeHexStr(encrypt);
    }

    /**
     * AES加密
     *
     * @param plainText 加密内容
     * @param encodeKey AES秘钥
     * @return 加密结果
     */
    public static String encryptHex(String plainText, byte[] encodeKey) {
        byte[] encrypt = encrypt(plainText, encodeKey);
        return HexUtil.encodeHexStr(encrypt);
    }

    /**
     * AES加密
     *
     * @param plainText 加密内容
     * @param base64Key AES秘钥
     * @return 加密结果
     */
    public static String encryptBase64(String plainText, String base64Key) {
        byte[] encrypt = encrypt(plainText, Base64.decode(base64Key));
        return Base64.encode(encrypt);
    }

    /**
     * AES解密
     *
     * @param encryptText 解密内容
     * @param encodeKey   AES秘钥
     * @return 解密结果
     */
    public static byte[] decrypt(String encryptText, byte[] encodeKey) {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, encodeKey);
        return aes.decrypt(encodeKey);
    }

    /**
     * AES解密
     *
     * @param encryptText 解密内容
     * @param base64Key   AES秘钥
     * @return 解密结果
     */
    public static byte[] decryptByBase64Key(String encryptText, String base64Key) {
        return decrypt(encryptText, Base64.decode(base64Key));
    }

    /**
     * AES解密
     *
     * @param encryptText 解密内容
     * @param base64Key   AES秘钥
     * @return 解密结果
     */
    public static String decryptStrByBase64Key(String encryptText, String base64Key) {
        byte[] decrypt = decrypt(encryptText, Base64.decode(base64Key));
        return new String(decrypt, StandardCharsets.UTF_8);
    }
}
