package com.youlan.tools.entity.dto;

import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GeneratorPageDTO extends PageDTO {

    @Query(type = QueryType.LIKE)
    @Schema(description = "表名称")
    private String tableName;

    @Query(type = QueryType.LIKE)
    @Schema(description = "表描述")
    private String tableComment;

    @Query(column = "create_time", type = QueryType.BETWEEN)
    @Schema(description = "创建时间")
    private List<Date> createTimeRange;

}
