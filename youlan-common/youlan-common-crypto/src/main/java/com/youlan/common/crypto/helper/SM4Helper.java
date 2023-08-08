package com.youlan.common.crypto.helper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.SM4;

public class SM4Helper {
    /**
     * 生成SM4秘钥
     */
    public static String generateBase64Key() {
        return Base64.encode(generateKey());
    }

    /**
     * 生成SM4秘钥
     */
    public static String generateHexKey() {
        return HexUtil.encodeHexStr(generateKey());
    }

    /**
     * 生成SM4秘钥
     */
    public static byte[] generateKey() {
        SM4 sm4 = new SM4();
        return sm4.getSecretKey().getEncoded();
    }

}
