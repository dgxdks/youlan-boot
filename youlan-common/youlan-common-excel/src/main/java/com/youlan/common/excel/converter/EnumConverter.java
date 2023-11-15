package com.youlan.common.excel.converter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.youlan.common.excel.anno.ExcelEnumProperty;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnumConverter extends AbstractConverter {
    public static final ConcurrentHashMap<ExcelEnumProperty, Map<String, String>> CONVERT_TO_JAVA_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ExcelEnumProperty, Map<String, String>> CONVERT_TO_EXCEL_CACHE = new ConcurrentHashMap<>();


    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Object javaData = cellDataToJavaData(cellData);
        //未匹配到指定类型返回空
        if (ObjectUtil.isNull(javaData)) {
            return javaData;
        }
        final ExcelEnumProperty excelEnumProperty = getFieldAnnotation(contentProperty, ExcelEnumProperty.class);
        Map<String, String> mappingMap = CONVERT_TO_JAVA_CACHE.computeIfAbsent(excelEnumProperty, key -> {
            Class<? extends Enum<?>> enumClass = excelEnumProperty.value();
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            // excel转java映射关系为 text -> code
            return Arrays.stream(enumConstants)
                    .collect(Collectors.toMap(anEnum -> getEnumFieldValue(anEnum, key.textField()), anEnum -> getEnumFieldValue(anEnum, key.codeField())));

        });
        String convertDataStr = convertToMappingData(mappingMap, javaData.toString(), excelEnumProperty.separator());
        return Convert.convert(getField(contentProperty).getType(), convertDataStr);
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(value)) {
            return emptyWriteCellData();
        }
        final ExcelEnumProperty excelEnumProperty = getFieldAnnotation(contentProperty, ExcelEnumProperty.class);
        Map<String, String> mappingMap = CONVERT_TO_EXCEL_CACHE.computeIfAbsent(excelEnumProperty, key -> {
            Class<? extends Enum<?>> enumClass = excelEnumProperty.value();
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            // excel转java映射关系为 code -> text
            return Arrays.stream(enumConstants)
                    .collect(Collectors.toMap(anEnum -> getEnumFieldValue(anEnum, key.codeField()), anEnum -> getEnumFieldValue(anEnum, key.textField())));
        });
        String mappingData = mappingMap.get(value.toString());
        if (StrUtil.isBlank(mappingData)) {
            return emptyWriteCellData();
        }
        String convertDataStr = convertToMappingData(mappingMap, value.toString(), excelEnumProperty.separator());
        return new WriteCellData<>(concatPrefixSuffix(convertDataStr, excelEnumProperty.prefixStr(), excelEnumProperty.suffixStr()));
    }

    /**
     * 获取枚举类中指定字段的值
     */
    public String getEnumFieldValue(Enum<?> anEnum, String filedName) {
        Object textFieldValue = ReflectUtil.getFieldValue(anEnum, filedName);
        if (ObjectUtil.isNull(textFieldValue)) {
            throw new IllegalArgumentException(StrUtil.format("枚举类{}中不存在字段{}", anEnum.getClass(), filedName));
        }
        return textFieldValue.toString();
    }
}
