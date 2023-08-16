package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@TableName("t_sys_storage_config")
@EqualsAndHashCode(callSuper = true)
public class StorageConfig extends PageDTO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "存储类型不能为空")
    @Schema(title = "存储类型")
    private String type;

    @Schema(title = "存储平台名称")
    private String platform;

    @Schema(title = "存储域名")
    private String domain;

    @Schema(title = "基础路径")
    private String basePath;

    @Schema(title = "存储路径")
    private String storagePath;

    @Schema(title = "访问秘钥(accessKey)")
    private String accessKey;

    @Schema(title = "私密秘钥(secretKey)")
    private String secretKey;

    @Schema(title = "桶名称(bucket)")
    private String bucketName;

    @Schema(title = "端点(endPoint)")
    private String endPoint;

    @Schema(title = "域名称(region)")
    private String region;

    @NotBlank(message = "是否默认不能为空")
    @Schema(title = "是否默认" + DBConstant.DESC_YES_NO)
    private String isDefault;

    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

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
