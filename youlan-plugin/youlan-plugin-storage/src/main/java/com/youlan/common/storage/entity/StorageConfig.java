package com.youlan.common.storage.entity;

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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@TableName("t_sys_storage_config")
public class StorageConfig extends PageDTO {

    @ExcelProperty("配置编号")
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty("配置名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "存储配置名称不能为空")
    @Schema(description = "存储配置名称")
    private String name;

    @Query
    @ExcelProperty(value = "存储类型", converter = EnumConverter.class)
    @ExcelEnumProperty(StorageType.class)
    @NotNull(message = "存储类型不能为空")
    @Schema(description = "存储类型(字典类型[storage_type])")
    private StorageType type;

    @ExcelProperty(value = "平台名称")
    @Query(type = QueryType.LIKE)
    @Schema(description = "存储平台名称")
    private String platform;

    @ExcelProperty(value = "存储域名")
    @Schema(description = "存储域名")
    private String domain;

    @ExcelProperty(value = "基础路径")
    @Schema(description = "基础路径")
    private String basePath;

    @ExcelProperty(value = "访问秘钥")
    @Schema(description = "访问秘钥(accessKey)")
    private String accessKey;

    @ExcelProperty(value = "私密秘钥")
    @Schema(description = "私密秘钥(secretKey)")
    private String secretKey;

    @ExcelProperty(value = "桶名称")
    @Schema(description = "桶名称(bucket)")
    private String bucketName;

    @ExcelProperty(value = "端点")
    @Schema(description = "端点(endPoint)")
    private String endPoint;

    @ExcelProperty(value = "域名称")
    @Schema(description = "域名称(region)")
    private String region;

    @ExcelProperty(value = "是否默认", converter = EnumConverter.class)
    @ExcelEnumProperty(DBYesNo.class)
    @NotBlank(message = "是否默认不能为空")
    @Schema(description = "是否默认" + DBConstant.DESC_YES_NO)
    private String isDefault;

    @ExcelProperty(value = "访问控制")
    @Schema(description = "访问控制(数据字典[storage_acl_type])")
    private String fileAcl;

    @ExcelProperty(value = "是否HTTPS(1-是 2-否)")
    private String isHttps;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageConfig)) return false;
        if (!super.equals(o)) return false;
        StorageConfig that = (StorageConfig) o;
        return getType() == that.getType() && Objects.equals(getPlatform(), that.getPlatform()) && Objects.equals(getDomain(), that.getDomain()) && Objects.equals(getBasePath(), that.getBasePath()) && Objects.equals(getAccessKey(), that.getAccessKey()) && Objects.equals(getSecretKey(), that.getSecretKey()) && Objects.equals(getBucketName(), that.getBucketName()) && Objects.equals(getEndPoint(), that.getEndPoint()) && Objects.equals(getRegion(), that.getRegion()) && Objects.equals(getFileAcl(), that.getFileAcl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getPlatform(), getDomain(), getBasePath(), getAccessKey(), getSecretKey(), getBucketName(), getEndPoint(), getRegion(), getFileAcl());
    }
}
