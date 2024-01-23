package com.youlan.common.crypto.helper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.core.helper.EnvironmentHelper;
import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.anno.DecryptRequest;
import com.youlan.common.crypto.anno.EncryptField;
import com.youlan.common.crypto.anno.EncryptResponse;
import com.youlan.common.crypto.config.CryptoProperties;
import com.youlan.common.crypto.encrypt.Encryptor;
import com.youlan.common.crypto.entity.EncryptorContext;
import com.youlan.common.crypto.enums.AlgorithmType;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class EncryptorHelper {
    public static final ConcurrentHashMap<EncryptorContext, Encryptor> ENCRYPTOR_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<EncryptResponse, EncryptorContext> ENCRYPT_RESPONSE_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<DecryptRequest, EncryptorContext> DECRYPT_RESPONSE_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<DecryptField, EncryptorContext> DECRYPT_FIELD_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<EncryptField, EncryptorContext> ENCRYPT_FIELD_CACHE = new ConcurrentHashMap<>();


    /**
     * 加密
     */
    public static String encrypt(String plainText, EncryptorContext context) {
        return createOrgGetEncryptor(context).encrypt(plainText);
    }

    /**
     * 加密
     */
    public static String encrypt(String plainText, EncryptResponse encryptResponse) {
        EncryptorContext encryptorContext = createOrGetEncryptorContext(encryptResponse);
        return encrypt(plainText, encryptorContext);
    }

    /**
     * 加密
     */
    public static String encrypt(String plainText, EncryptField encryptResponse) {
        EncryptorContext encryptorContext = createOrGetEncryptorContext(encryptResponse);
        return encrypt(plainText, encryptorContext);
    }


    /**
     * 解密
     */
    public static String decrypt(String encryptText, EncryptorContext context) {
        return createOrgGetEncryptor(context).decrypt(encryptText);
    }

    /**
     * 界面
     */
    public static String decrypt(String encryptText, DecryptRequest decryptRequest) {
        EncryptorContext encryptorContext = createOrGetEncryptorContext(decryptRequest);
        return decrypt(encryptText, encryptorContext);
    }

    public static String decrypt(String encryptText, DecryptField decryptField) {
        EncryptorContext encryptorContext = createOrGetEncryptorContext(decryptField);
        return decrypt(encryptText, encryptorContext);
    }

    /**
     * 创建或获取加密器
     */
    public static Encryptor createOrgGetEncryptor(EncryptorContext context) {
        return ENCRYPTOR_CACHE.computeIfAbsent(context, ctx -> ReflectUtil.newInstance(ctx.getAlgorithmType().getEncryptor(), ctx));
    }

    /**
     * 创建或获取加密配置上下文
     */
    public static EncryptorContext createOrGetEncryptorContext(EncryptResponse encryptResponse) {
        return ENCRYPT_RESPONSE_CACHE.computeIfAbsent(encryptResponse, anno -> new EncryptorContext()
                .setAlgorithmType(anno.algorithm())
                .setKey(resolveAnnoKey(anno.algorithm(), anno.key()))
                .setPublicKey(resolveAnnoPublicKey(anno.algorithm(), anno.publicKey()))
                .setPrivateKey(resolveAnnoPrivateKey(anno.algorithm(), anno.privateKey()))
                .setEncryptKeyType(anno.encryptKeyType()));
    }

    /**
     * 创建或获取加密配置上下文
     */
    public static EncryptorContext createOrGetEncryptorContext(EncryptField encryptField) {
        return ENCRYPT_FIELD_CACHE.computeIfAbsent(encryptField, anno -> new EncryptorContext()
                .setAlgorithmType(anno.algorithm())
                .setKey(resolveAnnoKey(anno.algorithm(), anno.key()))
                .setPublicKey(resolveAnnoPublicKey(anno.algorithm(), anno.publicKey()))
                .setPrivateKey(resolveAnnoPrivateKey(anno.algorithm(), anno.privateKey()))
                .setEncryptKeyType(anno.encryptKeyType()));
    }

    /**
     * 创建或获取加密配置上下文
     */
    public static EncryptorContext createOrGetEncryptorContext(DecryptRequest decryptRequest) {
        return DECRYPT_RESPONSE_CACHE.computeIfAbsent(decryptRequest, anno -> new EncryptorContext()
                .setAlgorithmType(anno.algorithm())
                .setKey(resolveAnnoKey(anno.algorithm(), anno.key()))
                .setPublicKey(resolveAnnoPublicKey(anno.algorithm(), anno.publicKey()))
                .setPrivateKey(resolveAnnoPrivateKey(anno.algorithm(), anno.privateKey()))
                .setDecryptKeyType(anno.decryptKeyType()));
    }

    /**
     * 创建或获取加密配置上下文
     */
    private static EncryptorContext createOrGetEncryptorContext(DecryptField decryptField) {
        return DECRYPT_FIELD_CACHE.computeIfAbsent(decryptField, anno -> new EncryptorContext()
                .setAlgorithmType(anno.algorithm())
                .setKey(resolveAnnoKey(anno.algorithm(), anno.key()))
                .setPublicKey(resolveAnnoPublicKey(anno.algorithm(), anno.publicKey()))
                .setPrivateKey(resolveAnnoPrivateKey(anno.algorithm(), anno.privateKey()))
                .setDecryptKeyType(anno.decryptKeyType()));
    }


    /**
     * 解析秘钥内容(明文秘钥/绝对路径文件/类路径文件/EL表达式)
     */
    private static String resolveKey(String key) {
        Assert.notBlank(key);
        //如果是明文直接返回
        if (HexUtil.isHexNumber(key) || Base64.isBase64(key)) {
            return key;
        }
        //判断是否是文件
        try {
            URL keyUrl = ResourceUtils.getURL(key);
            if (ObjectUtil.isNotNull(keyUrl)) {
                return FileUtil.readString(keyUrl, StandardCharsets.UTF_8);
            }
        } catch (FileNotFoundException ignore) {
        }
        //最后从环境变量里面尝试获取
        return EnvironmentHelper.resolvePlaceHolders(key);
    }

    /**
     * 解析注解秘钥配置
     */
    private static String resolveAnnoKey(AlgorithmType algorithmType, String annoKey) {
        if (StrUtil.isNotBlank(annoKey)) {
            return resolveKey(EnvironmentHelper.resolvePlaceHolders(annoKey));
        }
        //默认使用系统AES秘钥
        switch (algorithmType) {
            case SM4:
                return resolveKey(getCryptProperties().getSm4Key());
            case AES:
            default:
                return resolveKey(getCryptProperties().getAesKey());
        }
    }

    /**
     * 解析注解公钥配置
     */
    private static String resolveAnnoPublicKey(AlgorithmType algorithmType, String annoPublicKey) {
        if (StrUtil.isNotBlank(annoPublicKey)) {
            return resolveKey(annoPublicKey);
        }
        switch (algorithmType) {
            case SM2:
                return resolveKey(getCryptProperties().getSm2PublicKey());
            case RSA:
                return resolveKey(getCryptProperties().getRsaPublicKey());
            default:
                return annoPublicKey;
        }
    }

    /**
     * 解析注解私钥配置
     */
    private static String resolveAnnoPrivateKey(AlgorithmType algorithmType, String annoPrivateKey) {
        if (StrUtil.isNotBlank(annoPrivateKey)) {
            return resolveKey(annoPrivateKey);
        }
        //默认使用系统RSA私钥
        switch (algorithmType) {
            case SM2:
                return resolveKey(getCryptProperties().getSm2PrivateKey());
            case RSA:
                return resolveKey(getCryptProperties().getRsaPrivateKey());
            default:
                return annoPrivateKey;
        }
    }

    /**
     * 获取加密配置
     */
    private static CryptoProperties getCryptProperties() {
        return SpringUtil.getBean(CryptoProperties.class);
    }
}
