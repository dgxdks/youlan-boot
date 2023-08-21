package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.youlan.common.db.constant.DBConstant.DESC_ID;

@Data
@Accessors(chain = true)
@TableName("t_sys_storage_record")
public class StorageRecord {
    @Schema(title = DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "文件访问地址")
    private String url;

    @Schema(title = "文件大小")
    private Long size;

    @Schema(title = "文件名称")
    private String fileName;

    @Schema(title = "原始文件名称")
    private String originalFileName;

    @Schema(title = "基础存储路径")
    private String basePath;

    @Schema(title = "存储路径")
    private String path;

    @Schema(title = "文件扩展名")
    private String ext;

    @Schema(title = "MIME类型")
    private String contentType;

    @Schema(title = "存储平台名称")
    private String platform;

    @Schema(title = "缩略图访问路径")
    private String thUrl;

    @Schema(title = "缩略图文件名称")
    private String thFileName;

    @Schema(title = "缩略图大小")
    private Long thSize;

    @Schema(title = "缩略图MIME类型")
    private String thContentType;

    @Schema(title = "文件所属对象ID")
    private String objectId;

    @Schema(title = "文件所属对象类型")
    private String objectType;

    @Schema(title = "附加属性")
    private String attr;

    @Schema(title = "文件ACL")
    private String fileAcl;

    @Schema(title = "缩略图文件ACL")
    private String thFileAcl;

    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(title = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Hidden
    @TableField(exist = false)
    private byte[] data;
}
