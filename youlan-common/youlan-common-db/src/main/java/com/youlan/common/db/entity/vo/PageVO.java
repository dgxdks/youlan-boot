package com.youlan.common.db.entity.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageVO<T> {
    @Schema(title = "页码")
    private Long pageNum;

    @Schema(title = "每页条数")
    private Long pageSize;

    @Schema(title = "总数")
    private Long total;

    @Schema(title = "分页数据")
    private List<T> rows;

    public PageVO(IPage<T> page) {
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.total = page.getTotal();
        this.rows = page.getRecords();
    }
}
