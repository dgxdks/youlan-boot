package com.youlan.common.db.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDTO {

    @Schema(title = "页码")
    @TableField(exist = false)
    private Long pageNum;

    @Schema(title = "每页条数")
    @TableField(exist = false)
    private Long pageSize;

    @Schema(title = "排序字段集合")
    @TableField(exist = false)
    private List<String> sortList;

    @Schema(title = "是否降序", defaultValue = "false")
    @TableField(exist = false)
    private Boolean isAsc;

    @Schema(title = "是否统计总数", defaultValue = "true")
    @TableField(exist = false)
    private Boolean needTotal;

    @Schema(title = "扩展参数", type = "object")
    @TableField(exist = false)
    private Map<String, String> extParams;

    public Boolean getIsAsc() {
        return isAsc;
    }

    public PageDTO setIsAsc(Boolean asc) {
        isAsc = asc;
        return this;
    }

    public Boolean getNeedTotal() {
        return needTotal;
    }

    public PageDTO setNeedTotal(Boolean needTotal) {
        this.needTotal = needTotal;
        return this;
    }
}
