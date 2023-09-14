package com.youlan.common.crypto.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "youlan.common.crypto")
public class CryptoProperties {
    /**
     * AES秘钥
     */
    private String aesKey;
    /**
     * RSA公钥
     */
    private String rsaPublicKey;
    /**
     * RSA私钥
     */
    private String rsaPrivateKey;
    /**
     * SM2公钥
     */
    private String sm2PublicKey;
    /**
     * SM2私钥
     */
    private String sm2PrivateKey;
    /**
     * SM4秘钥
     */
    private String sm4Key;
}
