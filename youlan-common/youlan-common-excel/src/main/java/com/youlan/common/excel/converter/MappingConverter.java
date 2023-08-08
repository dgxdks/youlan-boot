package com.youlan.common.excel.converter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.youlan.common.excel.anno.ExcelMappingProperty;
import com.youlan.common.excel.anno.MappingProperty;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MappingConverter extends AbstractConverter {
    public static final ConcurrentHashMap<ExcelMappingProperty, Map<String, String>> CONVERT_TO_JAVA_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ExcelMappingProperty, Map<String, String>> CONVERT_TO_EXCEL_CACHE = new ConcurrentHashMap<>();

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Object javaData = cellDataToJavaData(cellData);
        //未匹配到指定类型返回空
        if (ObjectUtil.isNull(javaData)) {
            return javaData;
        }
        final ExcelMappingProperty excelMappingProperty = getFieldAnnotation(contentProperty, ExcelMappingProperty.class);
        Map<String, String> mappingMap = CONVERT_TO_JAVA_CACHE.computeIfAbsent(excelMappingProperty, key -> {
            MappingProperty[] mappingProperties = excelMappingProperty.value();
            //导入应该是 名称->值
            return Arrays.stream(mappingProperties)
                    .collect(Collectors.toMap(MappingProperty::name, MappingProperty::value));
        });
        //强制使用字符进行匹配
        return Convert.convert(getField(contentProperty).getType(), mappingMap.get(javaData.toString()));
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(value)) {
            return emptyWriteCellData();
        }
        final ExcelMappingProperty excelMappingProperty = getFieldAnnotation(contentProperty, ExcelMappingProperty.class);
        Map<String, String> mappingMap = CONVERT_TO_EXCEL_CACHE.computeIfAbsent(excelMappingProperty, key -> {
            MappingProperty[] mappingProperties = excelMappingProperty.value();
            //导出应该是 值->名称
            return Arrays.stream(mappingProperties)
                    .collect(Collectors.toMap(MappingProperty::value, MappingProperty::name));
        });
        String mappingData = mappingMap.get(value.toString());
        if (StrUtil.isBlank(mappingData)) {
            return emptyWriteCellData();
        }
        String finalData = StrUtil.concat(true, excelMappingProperty.prefixStr(), mappingData, excelMappingProperty.suffixStr());
        return new WriteCellData<>(finalData);
    }
}
