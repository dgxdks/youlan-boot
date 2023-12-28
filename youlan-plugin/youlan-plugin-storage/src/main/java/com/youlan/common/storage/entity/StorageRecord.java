package com.youlan.common.storage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.x.file.storage.core.Downloader;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("t_sys_storage_record")
@EqualsAndHashCode(callSuper = true)
public class StorageRecord extends PageDTO {
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "文件访问地址")
    private String url;

    @Schema(description = "文件大小")
    private Long size;

    @Query(type = QueryType.LIKE_RIGHT)
    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "原始文件名称")
    private String originalFileName;

    @Schema(description = "基础存储路径")
    private String basePath;

    @Schema(description = "存储路径")
    private String path;

    @Query
    @Schema(description = "文件扩展名")
    private String ext;

    @Schema(description = "MIME类型")
    private String contentType;

    @Query(type = QueryType.LIKE_RIGHT)
    @Schema(description = "存储平台名称")
    private String platform;

    @Schema(description = "缩略图访问路径")
    private String thUrl;

    @Schema(description = "缩略图文件名称")
    private String thFileName;

    @Schema(description = "缩略图大小")
    private Long thSize;

    @Schema(description = "缩略图MIME类型")
    private String thContentType;

    @Query(type = QueryType.LIKE_RIGHT)
    @Schema(description = "文件所属对象ID")
    private String objectId;

    @Schema(description = "文件所属对象类型")
    private String objectType;

    @Schema(description = "附加属性")
    private String attr;

    @Schema(description = "文件ACL")
    private String fileAcl;

    @Schema(description = "缩略图文件ACL")
    private String thFileAcl;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Query(column = "create_time", type = QueryType.BETWEEN)
    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

    @Hidden
    @JsonIgnore
    @TableField(exist = false)
    private Downloader downloader;

    @Hidden
    @JsonIgnore
    @TableField(exist = false)
    private Downloader thDownloader;
}
