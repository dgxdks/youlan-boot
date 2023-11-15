package com.youlan.plugin.sms.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.plugin.sms.enums.SmsSupplierEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@TableName("t_sms_supplier")
@EqualsAndHashCode(callSuper = true)
public class SmsSupplier extends PageDTO {

    @ExcelProperty("配置编号")
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置标识不能为空")
    @ExcelProperty("配置标识")
    @Schema(description = "配置标识")
    private String configId;

    @Query
    @NotBlank(message = "短信厂商不能为空")
    @ExcelProperty(value = "短信厂商", converter = EnumConverter.class)
    @ExcelEnumProperty(SmsSupplierEnum.class)
    @Schema(description = "短信厂商(字典类型[sms_supplier])")
    private String supplier;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "访问秘钥不能为空")
    @ExcelProperty("访问秘钥")
    @Schema(description = "访问秘钥")
    private String accessKeyId;

    @NotBlank(message = "私密秘钥不能为空")
    @ExcelProperty("私密秘钥")
    @Schema(description = "私密秘钥")
    private String accessKeySecret;

    @Query(type = QueryType.LIKE)
    @ExcelProperty("应用ID")
    @Schema(description = "应用ID")
    private String sdkAppId;

    @NotBlank(message = "短信签名不能为空")
    @Query(type = QueryType.LIKE)
    @ExcelProperty("短信签名")
    @Schema(description = "短信签名")
    private String signature;

    @NotBlank(message = "模版ID不能为空")
    @Query(type = QueryType.LIKE)
    @ExcelProperty("模版ID")
    @Schema(description = "模版ID")
    private String templateId;

    @ExcelProperty("随机权重")
    @Schema(description = "随机权重")
    private Integer weight;

    @ExcelProperty("重试间隔")
    @Schema(description = "重试间隔")
    private Integer retryInterval;

    @ExcelProperty("重试次数")
    @Schema(description = "重试次数")
    private Integer maxRetries;

    @ExcelProperty(value = "额外参数")
    @Schema(description = "额外参数")
    private String extraParams;

    @ExcelProperty(value = "配置状态", converter = EnumConverter.class)
    @ExcelEnumProperty(DBStatus.class)
    @Schema(description = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelProperty(value = "备注")
    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
