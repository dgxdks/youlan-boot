package com.youlan.common.core.helper;


import cn.hutool.extra.spring.SpringUtil;
import io.github.linpeilie.Converter;

import java.util.List;
import java.util.Map;

public class MapStructHelper {
    public static final Converter CONVERT = SpringUtil.getBean(Converter.class);

    public static <S, T> T convert(S source, Class<T> targetType) {
        return CONVERT.convert(source, targetType);
    }

    public static <S, T> T convert(S source, T target) {
        return CONVERT.convert(source, target);
    }

    public static <S, T> List<T> convert(List<S> source, Class<T> targetType) {
        return CONVERT.convert(source, targetType);
    }

    public static <T> T convert(Map<String, Object> map, Class<T> target) {
        return CONVERT.convert(map, target);
    }

}
