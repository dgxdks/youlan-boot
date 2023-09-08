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
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "日志名称")
    @Schema(title = "日志名称")
    private String logName;

    @ExcelProperty(value = "日志类型", converter = DictConverter.class)
    @ExcelDictProperty("sys_operation_log_type")
    @Schema(title = "日志类型[sys_operation_log_type]")
    private String logType;

    @Schema(title = "日志用户ID")
    private Long logUser;

    @ExcelProperty(value = "操作人员")
    @Schema(title = "日志用户")
    private String logBy;

    @ExcelProperty(value = "来源IP")
    @Schema(title = "来源IP")
    private String sourceIp;

    @ExcelProperty(value = "来源位置")
    @Schema(title = "来源位置")
    private String sourceLocation;

    @ExcelProperty(value = "日志时间")
    @Schema(title = "日志时间")
    private Date logTime;

    @ExcelProperty(value = "操作方法")
    @Schema(title = "调用方法")
    private String method;

    @ExcelProperty(value = "消耗时间(ms)")
    @Schema(title = "消耗时间(ms)")
    private Long costTime;

    @ExcelProperty(value = "日志状态", converter = DictConverter.class)
    @ExcelDictProperty("sys_operation_log_status")
    @Schema(title = "日志状态[sys_operation_log_status]")
    private String logStatus;

    @ExcelProperty(value = "请求地址")
    @Schema(title = "HTTP请求路径")
    private String httpUrl;

    @ExcelProperty(value = "请求方式")
    @Schema(title = "HTTP请求方法")
    private String httpMethod;

    @ExcelProperty(value = "请求参数")
    @Schema(title = "HTTP请求参数")
    private String httpQuery;

    @ExcelProperty(value = "请求体")
    @Schema(title = "HTTP请求体")
    private String httpBody;

    @ExcelProperty(value = "响应体")
    @Schema(title = "HTTP响应体")
    private String httpResponse;

    @ExcelProperty(value = "错误信息")
    @Schema(title = "错误信息")
    private String errorMsg;

}
