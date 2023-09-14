package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.system.excel.anno.ExcelDictProperty;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sys_operation_log")
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
public class OperationLog {

    @ExcelProperty(value = "日志编号")
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "日志名称")
    @Schema(description = "日志名称")
    private String logName;

    @ExcelProperty(value = "日志类型", converter = DictConverter.class)
    @ExcelDictProperty("sys_operation_log_type")
    @Schema(description = "日志类型[sys_operation_log_type]")
    private String logType;

    @Schema(description = "日志用户ID")
    private Long logUser;

    @ExcelProperty(value = "操作人员")
    @Schema(description = "日志用户")
    private String logBy;

    @ExcelProperty(value = "来源IP")
    @Schema(description = "来源IP")
    private String sourceIp;

    @ExcelProperty(value = "来源位置")
    @Schema(description = "来源位置")
    private String sourceLocation;

    @ExcelProperty(value = "日志时间")
    @Schema(description = "日志时间")
    private Date logTime;

    @ExcelProperty(value = "操作方法")
    @Schema(description = "调用方法")
    private String method;

    @ExcelProperty(value = "消耗时间(ms)")
    @Schema(description = "消耗时间(ms)")
    private Long costTime;

    @ExcelProperty(value = "日志状态", converter = DictConverter.class)
    @ExcelDictProperty("sys_operation_log_status")
    @Schema(description = "日志状态[sys_operation_log_status]")
    private String logStatus;

    @ExcelProperty(value = "请求地址")
    @Schema(description = "HTTP请求路径")
    private String httpUrl;

    @ExcelProperty(value = "请求方式")
    @Schema(description = "HTTP请求方法")
    private String httpMethod;

    @ExcelProperty(value = "请求参数")
    @Schema(description = "HTTP请求参数")
    private String httpQuery;

    @ExcelProperty(value = "请求体")
    @Schema(description = "HTTP请求体")
    private String httpBody;

    @ExcelProperty(value = "响应体")
    @Schema(description = "HTTP响应体")
    private String httpResponse;

    @ExcelProperty(value = "错误信息")
    @Schema(description = "错误信息")
    private String errorMsg;

}
