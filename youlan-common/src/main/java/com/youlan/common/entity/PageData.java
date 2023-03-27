package com.youlan.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageData<T> {
    private List<T> rows;

    private Long page;

    private Long pages;

    private Long size;

    private Long total;

    public PageData(IPage<T> iPage) {
        this.rows = iPage.getRecords();
        this.page = iPage.getCurrent();
        this.size = iPage.getSize();
        this.total = iPage.getTotal();
        this.pages = iPage.getPages();
    }
}
