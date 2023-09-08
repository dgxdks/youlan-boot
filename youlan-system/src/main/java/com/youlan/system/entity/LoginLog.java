package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@TableName("t_sys_login_log")
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class LoginLog extends PageDTO {

    @ExcelProperty(value = "日志ID")
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "用户名")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "用户名")
    private String userName;

    @ExcelProperty(value = "来源类型(1-后台端 2-移动端 99-其它)")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "来源类型(1-后台端 2-移动端 99-其它)")
    private String sourceType;

    @ExcelProperty(value = "登录IP")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "登录IP")
    private String loginIp;

    @ExcelProperty(value = "登录位置")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "登录位置")
    private String loginLocation;

    @ExcelProperty(value = "登录状态")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "登录状态[sys_login_log_status]")
    private String loginStatus;

    @ExcelProperty(value = "登录时间")
    @Schema(title = "登录时间")
    private Date loginTime;

    @Query(column = "login_time", type = QueryType.BETWEEN)
    @Schema(title = "登录时间")
    @TableField(exist = false)
    private List<Date> loginTimeRange;

    @ExcelProperty(value = "登录消息")
    @Schema(title = "登录消息")
    private String loginMsg;

    @ExcelProperty(value = "用户浏览器代理")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "用户浏览器代理")
    private String userAgent;
}
