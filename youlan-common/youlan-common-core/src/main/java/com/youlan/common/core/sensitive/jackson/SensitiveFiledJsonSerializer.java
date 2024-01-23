package com.youlan.common.core.sensitive.jackson;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.sensitive.anno.SensitiveField;
import com.youlan.common.core.sensitive.enums.SensitiveType;
import com.youlan.common.core.sensitive.helper.SensitizeHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class SensitiveFiledJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveField sensitiveField;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        SensitiveType sensitiveType = sensitiveField.type();
        switch (sensitiveType) {
            case USER_ID:
                gen.writeString(String.valueOf(SensitizeHelper.userId()));
            case CHINESE_NAME:
                gen.writeString(SensitizeHelper.chineseName(value));
                break;
            case ID_CARD:
                gen.writeString(SensitizeHelper.idCardNum(value, 1, 2));
                break;
            case FIXED_PHONE:
                gen.writeString(SensitizeHelper.fixedPhone(value));
                break;
            case MOBILE_PHONE:
                gen.writeString(SensitizeHelper.mobilePhone(value));
                break;
            case ADDRESS:
                gen.writeString(SensitizeHelper.address(value, 8));
                break;
            case EMAIL:
                gen.writeString(SensitizeHelper.email(value));
                break;
            case PASSWORD:
                gen.writeString(SensitizeHelper.password(value));
                break;
            case CAR_LICENSE:
                gen.writeString(SensitizeHelper.carLicense(value));
                break;
            case BANK_CARD:
                gen.writeString(SensitizeHelper.bankCard(value));
                break;
            case IPV4:
                gen.writeString(SensitizeHelper.ipv4(value));
                break;
            case IPV6:
                gen.writeString(SensitizeHelper.ipv6(value));
                break;
            case FIRST_ONLY:
                gen.writeString(SensitizeHelper.firstOnly(value));
                break;
            case CUSTOM_MASK:
                gen.writeString(SensitizeHelper.customMask(value, sensitiveField.prefixNoMaskLen(), sensitiveField.suffixNoMaskLen(), sensitiveField.maskStr()));
                break;
            default:
                throw new BizRuntimeException("不支持的脱敏类型");
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (ObjectUtil.isNull(property)) {
            return prov.findNullValueSerializer(null);
        }
        SensitiveField sensitiveField = property.getAnnotation(SensitiveField.class);
        if (ObjectUtil.isNull(sensitiveField)) {
            sensitiveField = property.getContextAnnotation(SensitiveField.class);
        }
        //不是String类型获取未找到注解不处理
        if (ObjectUtil.notEqual(property.getType().getRawClass(), String.class) || ObjectUtil.isNull(sensitiveField)) {
            return prov.findValueSerializer(property.getType(), property);
        }
        return new SensitiveFiledJsonSerializer(sensitiveField);
    }
}
