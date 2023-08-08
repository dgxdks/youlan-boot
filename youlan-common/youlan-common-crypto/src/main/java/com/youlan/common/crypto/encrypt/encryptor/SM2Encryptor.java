package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.crypto.asymmetric.SM2;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;

public class SM2Encryptor extends AbstractEncryptor {
    private final SM2 sm2;

    public SM2Encryptor(EncryptorContext context) {
        super(context);
        this.sm2 = new SM2(getPrivateKeyEncode(), getPublicKeyEncode());
    }

    @Override
    public AlgorithmType algorithmType() {
        return AlgorithmType.SM2;
    }

    @Override
    public String encryptToHex(String plainText) {
        return sm2.encryptHex(plainText, getEncryptKeyType());
    }

    @Override
    public String encryptToBase64(String plainText) {
        return sm2.encryptBase64(plainText, getEncryptKeyType());
    }

    @Override
    public String decrypt(String encryptText) {
        return sm2.decryptStr(encryptText, getDecryptKeyType());
    }
}
