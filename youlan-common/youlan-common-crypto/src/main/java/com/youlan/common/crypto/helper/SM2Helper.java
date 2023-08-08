package com.youlan.common.crypto.helper;

import cn.hutool.crypto.asymmetric.SM2;
import com.youlan.common.crypto.entity.CryptoKeyPair;

public class SM2Helper {
    /**
     * 生成SM2秘钥对
     */
    public static CryptoKeyPair generateKeyPair() {
        SM2 sm2 = new SM2();
        return new CryptoKeyPair()
                .setPrivateKey(sm2.getPrivateKey())
                .setPublicKey(sm2.getPublicKey())
                .setPublicKeyBase64(sm2.getPublicKeyBase64())
                .setPrivateKeyBase64(sm2.getPrivateKeyBase64());
    }
}
