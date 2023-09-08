package com.youlan.system.entity.dto;

import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogPageDTO extends PageDTO {

    @Query(type = QueryType.LIKE)
    @Schema(title = "日志名称")
    private String logName;

    @Query(type = QueryType.EQUAL)
    @Schema(title = "日志类型[sys_operation_log_type]")
    private String logType;

    @Query(type = QueryType.LIKE)
    @Schema(title = "日志用户")
    private String logBy;

    @Query(type = QueryType.LIKE)
    @Schema(title = "日志状态[sys_operation_log_status]")
    private String logStatus;

    @Query(column = "log_time", type = QueryType.BETWEEN)
    @Schema(title = "日志时间")
    private List<Date> logTimeRange;

}
