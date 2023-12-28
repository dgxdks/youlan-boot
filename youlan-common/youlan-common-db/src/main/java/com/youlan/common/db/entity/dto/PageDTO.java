package com.youlan.common.db.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDTO {

    @Schema(description = "页码")
    @TableField(exist = false)
    private Long pageNum;

    @Schema(description = "每页条数")
    @TableField(exist = false)
    private Long pageSize;

    @Schema(description = "分页排序字段列表")
    @TableField(exist = false)
    private List<PageSortDTO> sortList;

    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "是否降序", defaultValue = "false")
    @TableField(exist = false)
    private Boolean isDesc = false;

    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "是否统计总数", defaultValue = "true")
    @TableField(exist = false)
    private Boolean isNeedTotal = true;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "扩展参数", type = "object")
    @TableField(exist = false)
    private Map<String, String> extParams;
}
