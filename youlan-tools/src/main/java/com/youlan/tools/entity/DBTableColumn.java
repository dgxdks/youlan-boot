package com.youlan.tools.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DBTableColumn {

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "列名称")
    private String columnName;

    @Schema(description = "是否允许为空")
    private String isNullable;

    @Schema(description = "列是否主键标志")
    private String columnKey;

    @Schema(description = "列描述")
    private String columnComment;

    @Schema(description = "列数据类型")
    private String columnType;

    @Schema(description = "列排序")
    private Integer ordinalPosition;

    @Schema(description = "列额外信息")
    private String extra;
}
