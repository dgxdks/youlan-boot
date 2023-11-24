package com.youlan.system.service.api;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.api.DictApi;
import com.youlan.system.entity.DictData;
import com.youlan.system.service.biz.DictBizService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DictApiService implements DictApi {
    private final DictBizService dictBizService;

    @Override
    public Map<String, String> getDictDataValueMap(String typeKey) {
        List<DictData> dictDataList = dictBizService.getDict(typeKey);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return new HashMap<>();
        }
        return dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataValue, DictData::getDataName));
    }

    @Override
    public Map<String, String> getDictDataNameMap(String typeKey) {
        List<DictData> dictDataList = dictBizService.getDict(typeKey);
        if (CollectionUtil.isEmpty(dictDataList)) {
            return new HashMap<>();
        }
        return dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataName, DictData::getDataValue));
    }
}