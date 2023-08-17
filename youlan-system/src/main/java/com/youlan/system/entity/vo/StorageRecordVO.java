package com.youlan.system.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StorageRecordVO {

    @Schema(title = "文件访问地址")
    private String url;

    @Schema(title = "文件大小")
    private Long size;

    @Schema(title = "文件名称")
    private String fileName;

    @Schema(title = "原始文件名称")
    private String originalFileName;

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
}
