package com.youlan.common.excel.converter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractConverter implements Converter<Object> {

    /**
     * 单元格数据转Java数据(默认实现只匹配下面返回的数据)
     */
    public Object cellDataToJavaData(CellData<?> cellData) {
        CellDataTypeEnum cellDataType = cellData.getType();
        switch (cellDataType) {
            case STRING:
            case DIRECT_STRING:
            case RICH_TEXT_STRING:
                return cellData.getStringValue();
            case NUMBER:
                return cellData.getNumberValue();
            case BOOLEAN:
                return cellData.getBooleanValue();
            case DATE:
            case ERROR:
            case EMPTY:
            default:
                return null;
        }
    }

    /**
     * 空excel数据
     */
    public WriteCellData<Object> emptyWriteCellData() {
        return new WriteCellData<>(StrUtil.EMPTY);
    }

    /**
     * 获取字段上的注解
     */
    public <A extends Annotation> A getFieldAnnotation(ExcelContentProperty contentProperty, Class<A> annoClz) {
        Field field = getField(contentProperty);
        A fieldAnno = field.getAnnotation(annoClz);
        if (ObjectUtil.isNull(fieldAnno)) {
            throw new ExcelAnalysisException(StrUtil.format("字段{}未找到对应{}注解", field.getName(),
                    annoClz.getName()));
        }
        return fieldAnno;
    }

    /**
     * 获取字段
     */
    public Field getField(ExcelContentProperty contentProperty) {
        return contentProperty.getField();
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public abstract Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception;

    @Override
    public Object convertToJavaData(ReadConverterContext<?> context) throws Exception {
        return convertToJavaData(context.getReadCellData(), context.getContentProperty(),
                context.getAnalysisContext().currentReadHolder().globalConfiguration());
    }

    @Override
    public abstract WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception;

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Object> context) throws Exception {
        return convertToExcelData(context.getValue(), context.getContentProperty(),
                context.getWriteContext().currentWriteHolder().globalConfiguration());
    }

    /**
     * 将值转为映射值
     */
    public String convertToMappingData(Map<String, String> mapping, String value, String separator) {
        if (StrUtil.contains(value, separator)) {
            return Arrays.stream(value.split(separator))
                    .map(item -> mapping.getOrDefault(item, StrUtil.EMPTY))
                    .collect(Collectors.joining(separator));
        }
        return mapping.getOrDefault(value, StrUtil.EMPTY);
    }

    /**
     * 删除值中包含的前缀和后缀
     */
    public String removePrefixSuffix(String value, String prefix, String suffix) {
        if (StrUtil.isNotBlank(prefix)) {
            value = StrUtil.removePrefix(value, prefix);
        }
        if (StrUtil.isNotBlank(suffix)) {
            value = StrUtil.removeSuffix(value, suffix);
        }
        return value;
    }

    /**
     * 拼接值的前缀和后缀
     */
    public String concatPrefixSuffix(String value, String prefix, String suffix) {
        return StrUtil.concat(true, prefix, value, suffix);
    }
}
