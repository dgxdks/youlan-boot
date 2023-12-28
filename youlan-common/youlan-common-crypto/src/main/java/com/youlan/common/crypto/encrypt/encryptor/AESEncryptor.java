package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;

public class AESEncryptor extends AbstractEncryptor {
    private final AES aes;

    public AESEncryptor(EncryptorContext context) {
        super(context);
        aes = new AES(getKeyEncode());
    }

    @Override
    public AlgorithmType algorithmType() {
        return AlgorithmType.AES;
    }

    @Override
    public String encryptToHex(String plainText) {
        return aes.encryptHex(plainText);
    }

    @Override
    public String encryptToBase64(String plainText) {
        return aes.encryptBase64(plainText);
    }

    @Override
    public String decrypt(String encryptText) {
        return aes.decryptStr(encryptText);
    }
}
