package com.youlan.common.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;

import java.util.Map;

public abstract class AbstractExcelListener<T> implements ReadListener<T> {
    protected Map<Integer, String> headMap;

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap = ConverterUtils.convertToStringMap(headMap, context);
    }
}
