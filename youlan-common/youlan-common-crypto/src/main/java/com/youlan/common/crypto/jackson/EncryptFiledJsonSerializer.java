package com.youlan.common.crypto.jackson;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.crypto.anno.EncryptField;
import com.youlan.common.crypto.helper.EncryptorHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class EncryptFiledJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private EncryptField encryptField;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        try {
            String encrypt = EncryptorHelper.encrypt(value, encryptField);
            gen.writeString(encrypt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0018);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (ObjectUtil.isNull(property)) {
            return prov.findNullValueSerializer(null);
        }
        EncryptField encryptField = property.getAnnotation(EncryptField.class);
        if (ObjectUtil.isNull(encryptField)) {
            encryptField = property.getContextAnnotation(EncryptField.class);
        }
        //不是String类型获取未找到注解不处理
        if (ObjectUtil.notEqual(property.getType().getRawClass(), String.class) || ObjectUtil.isNull(encryptField)) {
            return prov.findValueSerializer(property.getType(), property);
        }
        return new EncryptFiledJsonSerializer(encryptField);
    }
}
