package com.youlan.common.crypto.encrypt.encryptor;

import cn.hutool.crypto.asymmetric.RSA;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;

public class RSAEncryptor extends AbstractEncryptor {
    private final RSA rsa;

    public RSAEncryptor(EncryptorContext context) {
        super(context);
        this.rsa = new RSA(getPrivateKeyEncode(), getPublicKeyEncode());
    }

    @Override
    public AlgorithmType algorithmType() {
        return AlgorithmType.RSA;
    }

    @Override
    public String encryptToHex(String plainText) {
        return rsa.encryptHex(plainText, getEncryptKeyType());
    }

    @Override
    public String encryptToBase64(String plainText) {
        return rsa.encryptHex(plainText, getEncryptKeyType());
    }

    @Override
    public String decrypt(String encryptText) {
        return rsa.decryptStr(encryptText, getDecryptKeyType());
    }
}
