package com.youlan.common.crypto.entity;

import cn.hutool.crypto.asymmetric.KeyType;
import com.youlan.common.crypto.enums.AlgorithmType;
import com.youlan.common.crypto.enums.EncryptEncode;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EncryptorContext {
    /**
     * 算法类型
     */
    private AlgorithmType algorithmType;

    /**
     * 对称加密秘钥
     */
    private String key;

    /**
     * 非对称加密公钥
     */
    private String publicKey;

    /**
     * 非对称加密秘钥
     */
    private String privateKey;

    /**
     * 加密格式
     */
    private EncryptEncode encryptEncode = EncryptEncode.BASE64;

    /**
     * 加密使用的秘钥类型
     */
    private KeyType encryptKeyType;

    /**
     * 解密使用的秘钥类型
     */
    private KeyType decryptKeyType;
}
