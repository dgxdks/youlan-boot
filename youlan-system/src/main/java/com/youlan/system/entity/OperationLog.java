package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.common.excel.anno.ExcelMappingProperty;
import com.youlan.common.excel.anno.MappingProperty;
import com.youlan.common.excel.converter.MappingConverter;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sys_operation_log")
@ExcelIgnoreUnannotated
public class OperationLog {

    @ExcelProperty(value = "操作序号")
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "操作模块")
    @Schema(title = "日志名称")
    private String logName;

    @ExcelProperty(value = "操作类型", converter = DictConverter.class)
    @Schema(title = "日志类型(字典类型[tools_generator_query_type])")
    private String logType;

    @Schema(title = "日志用户ID")
    private Long logUser;

    @ExcelProperty(value = "操作用户")
    @Schema(title = "日志用户")
    private String logBy;

    @ExcelProperty(value = "操作时间")
    @Schema(title = "日志时间")
    private Date logTime;

    @ExcelProperty(value = "操作状态", converter = MappingConverter.class)
    @ExcelMappingProperty(value = {@MappingProperty(value = "1", name = "正常"), @MappingProperty(value = "2", name = "异常")})
    @Schema(title = "日志状态(1-正常 2-异常)")
    private String logStatus;

    @ExcelProperty(value = "错误信息")
    @Schema(title = "错误信息")
    private String errorMsg;

    @Schema(title = "调用方法")
    private String method;

    @ExcelProperty(value = "操作IP")
    @Schema(title = "来源IP")
    private String sourceIp;

    @ExcelProperty(value = "操作地点")
    @Schema(title = "来源位置")
    private String sourceLocation;

    @ExcelProperty(value = "请求方式")
    @Schema(title = "HTTP请求方法")
    private String httpMethod;

    @ExcelProperty(value = "请求路径")
    @Schema(title = "HTTP请求路径")
    private String httpUrl;

    @ExcelProperty(value = "请求参数")
    @Schema(title = "HTTP请求参数")
    private String httpQuery;

    @ExcelProperty(value = "请求体")
    @Schema(title = "HTTP请求体")
    private String httpBody;

    @ExcelProperty(value = "响应体")
    @Schema(title = "HTTP响应体")
    private String httpResponse;

}
