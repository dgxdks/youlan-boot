package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.core.codec.Base64;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;

public class Base64Encryptor extends AbstractEncryptor {

    public Base64Encryptor(EncryptorContext context) {
        super(context);
    }

    @Override
    public AlgorithmType algorithmType() {
        return AlgorithmType.BASE64;
    }

    @Override
    public String encryptToHex(String plainText) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encryptToBase64(String plainText) {
        return Base64.encode(plainText);
    }

    @Override
    public String decrypt(String encryptText) {
        return Base64.decodeStr(encryptText);
    }
}
