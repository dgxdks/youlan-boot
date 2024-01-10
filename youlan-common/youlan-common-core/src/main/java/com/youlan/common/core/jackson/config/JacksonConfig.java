package com.youlan.common.core.jackson.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.youlan.common.core.jackson.serializer.LongSerializer;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // 自定义Long类型序列化
            jacksonObjectMapperBuilder.serializerByType(Long.class, new LongSerializer(Number.class));
            jacksonObjectMapperBuilder.postConfigurer(objectMapper -> {
                // 未知枚举类型当做null处理
                objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
                // 空字符串当做null处理
                objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            });
        };
    }

}
