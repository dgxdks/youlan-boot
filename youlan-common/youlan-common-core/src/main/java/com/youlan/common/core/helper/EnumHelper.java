package com.youlan.common.core.helper;

import cn.hutool.core.collection.CollUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EnumHelper {

    /**
     * 获取指定枚举类的枚举映射
     *
     * @param enumClz 枚举类
     * @param mapFuc  映射函数
     * @param <K>     映射键名
     * @param <T>     映射枚举
     * @return 枚举映射
     */
    public static <K, T extends Enum<T>> Map<K, T> getEnumMap(Class<T> enumClz, Function<T, K> mapFuc) {
        T[] enumArray = getEnumArray(enumClz);
        HashMap<K, T> enumMap = new HashMap<>();
        for (T t : enumArray) {
            enumMap.put(mapFuc.apply(t), t);
        }
        return enumMap;
    }

    /**
     * 获取指定枚举类的枚举数组
     *
     * @param enumClz 枚举类
     * @param <T>     枚举类型
     * @return 枚举数组
     */
    public static <T extends Enum<T>> T[] getEnumArray(Class<T> enumClz) {
        return enumClz.getEnumConstants();
    }

    /**
     * 获取指定枚举类的枚举集合
     *
     * @param enumClz 枚举类
     * @param <T>     枚举类型
     * @return 枚举集合
     */
    public static <T extends Enum<T>> List<T> getEnumList(Class<T> enumClz) {
        return CollUtil.toList(enumClz.getEnumConstants());
    }

}
