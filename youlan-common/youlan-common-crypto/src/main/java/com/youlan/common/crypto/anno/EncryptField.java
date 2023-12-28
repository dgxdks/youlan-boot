package com.youlan.common.crypto.anno;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.youlan.common.crypto.enums.AlgorithmType;
import com.youlan.common.crypto.enums.EncryptEncode;
import com.youlan.common.crypto.jackson.EncryptFiledJsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = EncryptFiledJsonSerializer.class)
public @interface EncryptField {
    /**
     * 加密算法类型
     */
    AlgorithmType algorithm() default AlgorithmType.AES;

    /**
     * 对称加密秘钥(支持EL表达式从yml中获取,如果是绝对路径或者类路径格式则会加载对应文件内容)
     */
    String key() default StrUtil.EMPTY;

    /**
     * 非对称加密公钥(支持EL表达式从yml中获取,如果是绝对路径或者类路径格式则会加载对应文件内容)
     */
    String publicKey() default StrUtil.EMPTY;

    /**
     * 非对称加密私钥(支持EL表达式从yml中获取,如果是绝对路径或者类路径格式则会加载对应文件内容)
     */
    String privateKey() default StrUtil.EMPTY;

    /**
     * 加密结果格式
     */
    EncryptEncode encryptEncode() default EncryptEncode.BASE64;

    /**
     * 加密使用的秘钥类型
     */
    KeyType encryptKeyType() default KeyType.PrivateKey;
}
