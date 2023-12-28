package com.youlan.common.api;

import java.util.Map;

public interface DictApi {
    /**
     * 根据字典类型获取数据字典值键值和名称的映射
     *
     * @param typeKey 数据字典键值
     * @return 数据字典值键值和名称的映射
     */
    Map<String, String> getDictDataValueMap(String typeKey);

    /**
     * 根据字典类型获取数据字典值名称和键值的映射
     */
    Map<String, String> getDictDataNameMap(String typeKey);
}
