package com.youlan.common.crypto.jackson;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.helper.EncryptorHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DecryptFiledDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
    private DecryptField decryptField;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String encryptText = p.getValueAsString();
        if (StrUtil.isBlank(encryptText)) {
            return encryptText;
        }
        try {
            return EncryptorHelper.decrypt(encryptText, decryptField);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0003);
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (ObjectUtil.isNull(property)) {
            return ctxt.findContextualValueDeserializer(property.getType(), property);
        }
        DecryptField decryptField = property.getAnnotation(DecryptField.class);
        if (ObjectUtil.isNull(decryptField)) {
            decryptField = property.getContextAnnotation(DecryptField.class);
        }
        //不是String类型获取未找到注解不处理
        if (ObjectUtil.notEqual(property.getType().getRawClass(), String.class) || ObjectUtil.isNull(decryptField)) {
            return ctxt.findContextualValueDeserializer(property.getType(), property);
        }
        return new DecryptFiledDeserializer(decryptField);
    }
}
