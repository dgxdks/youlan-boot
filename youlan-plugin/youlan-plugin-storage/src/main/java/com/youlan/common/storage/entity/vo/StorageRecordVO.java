package com.youlan.common.storage.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StorageRecordVO {

    @Schema(description = "文件访问地址")
    private String url;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "原始文件名称")
    private String originalFileName;

    @Schema(description = "文件扩展名")
    private String ext;

    @Schema(description = "MIME类型")
    private String contentType;

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

    @Schema(description = "文件所属对象ID")
    private String objectId;

    @Schema(description = "文件所属对象类型")
    private String objectType;
}
