package com.youlan.common.core.jackson.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonHelper {
    public static final ObjectMapper objectMapper = getObjectMapper();

    /**
     * 对象转json字符串
     *
     * @param object 对象
     * @return json字符串
     */
    public static String toJsonStr(Object object) {
        if (ObjectUtil.isNull(object)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串转对象
     *
     * @param text json字符串
     * @param clz  对象类型
     * @param <T>  对象类型
     * @return 对象
     */
    public static <T> T toBean(String text, Class<T> clz) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, clz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串转对象
     *
     * @param text          json字符串
     * @param typeReference 类型描述
     * @param <T>           对象类型
     * @return 对象
     */
    public static <T> T toBean(String text, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取jackson对象映射器
     *
     * @return 对象映射器
     */
    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
        if (ObjectUtil.isNotNull(objectMapper)) {
            return objectMapper;
        }
        return new ObjectMapper();
    }

}
