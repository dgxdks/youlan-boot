package com.youlan.common.crypto.helper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.youlan.common.crypto.entity.CryptoKeyPair;

public class RSAHelper {
    /**
     * 生成RSA秘钥对
     */
    public static CryptoKeyPair generateKeyPair() {
        RSA rsa = new RSA();
        return new CryptoKeyPair()
                .setPrivateKey(rsa.getPrivateKey())
                .setPublicKey(rsa.getPublicKey())
                .setPublicKeyBase64(rsa.getPublicKeyBase64())
                .setPrivateKeyBase64(rsa.getPrivateKeyBase64());
    }

    public static byte[] encrypt(String plainText, String key, KeyType keyType) {
        Assert.notEquals(KeyType.SecretKey, key);
        RSA rsa;
        if (keyType == KeyType.PrivateKey) {
            rsa = new RSA(key, null);
        } else {
            rsa = new RSA(null, key);
        }
        return rsa.encrypt(plainText, keyType);
    }

    public static String encryptToBase64(String plainText, String key, KeyType keyType) {
        return Base64.encode(encrypt(plainText, key, keyType));
    }

    public static void main(String[] args) {
        String aa = "{\n" +
                "    \"mobile\": \"18392879698\",\n" +
                "    \"timestamp\": 123123123123\n" +
                "}";
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSSMuUuRP5iY6TkansOjP2sdhgeF7DWjydlhq0uQxbwFrel/HqL3Y8xlKj6/T75WibklM6HiFr094Os+wdudi3GWV/dNgskBPONXiVEOOJWjgPSndtw7UBhmzNI+rUG9dJ18RT18OSl7AuoXXXKpzOL4cQ389Yf0DDpfKGNE0t7wIDAQAB";
        String s = encryptToBase64(aa, key, KeyType.PublicKey);
        System.out.println(s);
    }
}
