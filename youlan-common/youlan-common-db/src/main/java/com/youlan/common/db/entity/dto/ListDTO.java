package com.youlan.common.db.entity.dto;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListDTO<T> {
    @Schema(description = "数据列表")
    private List<T> list = new ArrayList<>();

    @Schema(hidden = true)
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(this.getList());
    }

    @Schema(hidden = true)
    public boolean isOne() {
        return !isEmpty() && list.size() == 1;
    }

    @Schema(hidden = true)
    public T getOne() {
        return CollectionUtil.getFirst(list);
    }
}
