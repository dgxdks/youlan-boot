package com.youlan.common.crypto.enums;

import com.youlan.common.crypto.encrypt.encryptor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmType {
    /**
     * Base64加密
     */
    BASE64(Base64Encryptor.class),
    /**
     * AES对称加密
     */
    AES(AESEncryptor.class),
    /**
     * RSA非对称加密
     */
    RSA(RSAEncryptor.class),
    /**
     * 国密非对称加密
     */
    SM2(SM2Encryptor.class),
    /**
     * 国密对称加密
     */
    SM4(SM4Encryptor.class);

    private final Class<? extends AbstractEncryptor> encryptor;
}
