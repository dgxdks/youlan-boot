package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.enums.DBYesNo;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.system.excel.anno.ExcelDictProperty;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@TableName("t_sys_storage_config")
@EqualsAndHashCode(callSuper = true)
public class StorageConfig extends PageDTO {

    @ExcelProperty("配置编号")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty("配置名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "存储配置名称不能为空")
    @Schema(title = "存储配置名称")
    private String name;

    @Query
    @ExcelProperty(value = "存储类型", converter = DictConverter.class)
    @ExcelDictProperty("sys_storage_type")
    @NotNull(message = "存储类型不能为空")
    @Schema(title = "存储类型(字典类型[sys_storage_type])")
    @EnumValue
    private StorageType type;

    @ExcelProperty(value = "平台名称")
    @Query(type = QueryType.LIKE)
    @Schema(title = "存储平台名称")
    private String platform;

    @ExcelProperty(value = "存储域名")
    @Schema(title = "存储域名")
    private String domain;

    @ExcelProperty(value = "基础路径")
    @Schema(title = "基础路径")
    private String basePath;

    @ExcelProperty(value = "访问秘钥")
    @Schema(title = "访问秘钥(accessKey)")
    private String accessKey;

    @ExcelProperty(value = "私密秘钥")
    @Schema(title = "私密秘钥(secretKey)")
    private String secretKey;

    @ExcelProperty(value = "桶名称")
    @Schema(title = "桶名称(bucket)")
    private String bucketName;

    @ExcelProperty(value = "端点")
    @Schema(title = "端点(endPoint)")
    private String endPoint;

    @ExcelProperty(value = "域名称")
    @Schema(title = "域名称(region)")
    private String region;

    @ExcelProperty(value = "是否默认", converter = EnumConverter.class)
    @ExcelEnumProperty(DBYesNo.class)
    @NotBlank(message = "是否默认不能为空")
    @Schema(title = "是否默认" + DBConstant.DESC_YES_NO)
    private String isDefault;

    @ExcelProperty(value = "访问控制")
    @Schema(title = "访问控制(数据字典[sys_storage_acl_type])")
    private String fileAcl;

    @ExcelProperty(value = "是否HTTPS(1-是 2-否)")
    private String isHttps;

    @ExcelProperty(value = "配置状态", converter = EnumConverter.class)
    @ExcelEnumProperty(DBStatus.class)
    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelProperty(value = "备注")
    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(title = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(title = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(title = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
