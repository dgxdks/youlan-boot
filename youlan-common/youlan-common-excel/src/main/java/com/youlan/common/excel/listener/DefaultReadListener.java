package com.youlan.common.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DefaultReadListener<T> implements ReadListener<T> {
    private final List<T> cacheList = new ArrayList<>(1024);

    @Override
    public void invoke(T data, AnalysisContext context) {
        cacheList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
