package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.crypto.symmetric.SM4;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;

public class SM4Encryptor extends AbstractEncryptor {
    private final SM4 sm4;

    public SM4Encryptor(EncryptorContext context) {
        super(context);
        this.sm4 = new SM4(getKeyEncode());
    }

    @Override
    public AlgorithmType algorithmType() {
        return AlgorithmType.SM4;
    }

    @Override
    public String encryptToHex(String plainText) {
        return sm4.encryptHex(plainText);
    }

    @Override
    public String encryptToBase64(String plainText) {
        return sm4.encryptBase64(plainText);
    }

    @Override
    public String decrypt(String encryptText) {
        return sm4.decryptStr(encryptText);
    }
}
