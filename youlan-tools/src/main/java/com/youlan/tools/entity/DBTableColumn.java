package com.youlan.tools.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DBTableColumn {

    @Schema(title = "表名称")
    private String tableName;

    @Schema(title = "列名称")
    private String columnName;

    @Schema(title = "是否允许为空")
    private String isNullable;

    @Schema(title = "列是否主键标志")
    private String columnKey;

    @Schema(title = "列描述")
    private String columnComment;

    @Schema(title = "列数据类型")
    private String columnType;

    @Schema(title = "列排序")
    private Integer ordinalPosition;

    @Schema(title = "列额外信息")
    private String extra;
}
