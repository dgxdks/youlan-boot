package com.youlan.common.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DefaultExcelListener<T> extends AbstractExcelListener<T> {
    private final List<T> dataList = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
