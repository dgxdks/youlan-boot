package com.youlan.system.excel.converter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.youlan.common.excel.converter.AbstractConverter;
import com.youlan.system.entity.DictData;
import com.youlan.system.excel.anno.ExcelDictProperty;
import com.youlan.system.service.biz.DictBizService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictConverter extends AbstractConverter {
    public static final ConcurrentHashMap<String, Map<String, DictData>> CONVERT_TO_JAVA_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Map<String, DictData>> CONVERT_TO_EXCEL_CACHE = new ConcurrentHashMap<>();


    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Object javaData = cellDataToJavaData(cellData);
        if (ObjectUtil.isNull(javaData)) {
            return null;
        }
        final ExcelDictProperty excelDictProperty = getFieldAnnotation(contentProperty, ExcelDictProperty.class);
        DictBizService dictBizService = SpringUtil.getBean(DictBizService.class);
        String typeKey = excelDictProperty.value();
        Map<String, DictData> dictDataMap = CONVERT_TO_JAVA_CACHE.computeIfAbsent(typeKey, key -> dictBizService.getDictDataNameMap(typeKey));
        if (CollectionUtil.isEmpty(dictDataMap)) {
            return null;
        }
        //强制使用字符串进行匹配
        DictData dictData = dictDataMap.get(javaData.toString());
        if (ObjectUtil.isNull(dictData)) {
            return javaData;
        }
        return Convert.convert(getField(contentProperty).getType(), dictData.getDataValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (ObjectUtil.isNull(value)) {
            return emptyWriteCellData();
        }
        final ExcelDictProperty excelDictProperty = getFieldAnnotation(contentProperty, ExcelDictProperty.class);
        DictBizService dictBizService = SpringUtil.getBean(DictBizService.class);
        String typeKey = excelDictProperty.value();
        Map<String, DictData> dictDataMap = CONVERT_TO_EXCEL_CACHE.computeIfAbsent(typeKey, dictBizService::getDictDataValueMap);
        if (CollectionUtil.isEmpty(dictDataMap)) {
            return emptyWriteCellData();
        }
        DictData dictData = dictDataMap.get(value.toString());
        if (ObjectUtil.isNull(dictData)) {
            return emptyWriteCellData();
        }
        String finalData = StrUtil.concat(true, excelDictProperty.prefixStr(), dictData.getDataName(), excelDictProperty.suffixStr());
        return new WriteCellData<>(finalData);
    }
}
