package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.youlan.common.crypto.encrypt.Encryptor;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.EncryptEncode;
import lombok.Getter;

@Getter
public abstract class AbstractEncryptor implements Encryptor {
    private final EncryptorContext context;

    public AbstractEncryptor(EncryptorContext context) {
        this.context = context;
    }

    @Override
    public String encrypt(String plainText) {
        EncryptEncode encryptEncode = context.getEncryptEncode();
        switch (encryptEncode) {
            case HEX:
                return encryptToHex(plainText);
            case BASE64:
            default:
                return encryptToBase64(plainText);
        }
    }

    /**
     * 获取非对称加密公钥
     */
    public byte[] getPublicKeyEncode() {
        return toKeyEncode(context.getPublicKey());
    }

    /**
     * 获取非对称加密私钥
     */
    public byte[] getPrivateKeyEncode() {
        return toKeyEncode(context.getPrivateKey());
    }

    /**
     * 获取对称加密秘钥
     */
    public byte[] getKeyEncode() {
        return toKeyEncode(context.getKey());
    }

    /**
     * 获取秘钥字节编码
     */
    public byte[] toKeyEncode(String targetKey) {
        if (StrUtil.isBlank(targetKey)) {
            throw new IllegalArgumentException();
        }
        if (Base64.isBase64(targetKey)) {
            return Base64.decode(targetKey);
        } else if (HexUtil.isHexNumber(targetKey)) {
            return HexUtil.decodeHex(targetKey);
        } else {
            return Base64.decode(targetKey);
        }
    }

    /**
     * 获取加密使用的秘钥类型
     */
    public KeyType getEncryptKeyType() {
        return context.getEncryptKeyType();
    }

    /**
     * 获取解密使用的秘钥类型
     */
    public KeyType getDecryptKeyType() {
        return context.getDecryptKeyType();
    }
}
