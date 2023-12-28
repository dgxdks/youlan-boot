package com.youlan.system.converter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.youlan.common.excel.converter.AbstractConverter;
import com.youlan.system.anno.ExcelDictProperty;
import com.youlan.system.service.biz.DictBizService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictConverter extends AbstractConverter {
    public static final ConcurrentHashMap<String, Map<String, String>> CONVERT_TO_JAVA_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Map<String, String>> CONVERT_TO_EXCEL_CACHE = new ConcurrentHashMap<>();


    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Object javaData = cellDataToJavaData(cellData);
        if (ObjectUtil.isNull(javaData)) {
            return null;
        }
        final ExcelDictProperty excelDictProperty = getFieldAnnotation(contentProperty, ExcelDictProperty.class);
        DictBizService dictBizService = SpringUtil.getBean(DictBizService.class);
        String typeKey = excelDictProperty.value();
        Map<String, String> dictDataNameMap = CONVERT_TO_JAVA_CACHE.computeIfAbsent(typeKey, key -> dictBizService.getDictDataNameMap(typeKey));
        if (CollectionUtil.isEmpty(dictDataNameMap)) {
            return null;
        }
        String convertDataStr = convertToMappingData(dictDataNameMap, javaData.toString(), excelDictProperty.separator());
        return Convert.convert(getField(contentProperty).getType(), convertDataStr);
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (ObjectUtil.isNull(value)) {
            return emptyWriteCellData();
        }
        final ExcelDictProperty excelDictProperty = getFieldAnnotation(contentProperty, ExcelDictProperty.class);
        DictBizService dictBizService = SpringUtil.getBean(DictBizService.class);
        String typeKey = excelDictProperty.value();
        Map<String, String> dictDataValueMap = CONVERT_TO_EXCEL_CACHE.computeIfAbsent(typeKey, dictBizService::getDictDataValueMap);
        if (CollectionUtil.isEmpty(dictDataValueMap)) {
            return emptyWriteCellData();
        }
        String convertDataStr = convertToMappingData(dictDataValueMap, value.toString(), excelDictProperty.separator());
        return new WriteCellData<>(concatPrefixSuffix(convertDataStr, excelDictProperty.prefixStr(), excelDictProperty.suffixStr()));
    }

}
