package com.youlan.common.crypto.encrypt;

import com.youlan.common.crypto.enums.AlgorithmType;

public interface Encryptor {
    /**
     * 加密算法类型
     */
    AlgorithmType algorithmType();

    String encrypt(String plainText);

    /**
     * 加密至HEX格式
     */
    String encryptToHex(String plainText);

    /**
     * 加密至Base64格式
     */
    String encryptToBase64(String plainText);


    /**
     * 解密至明文
     */
    String decrypt(String encryptText);
}
