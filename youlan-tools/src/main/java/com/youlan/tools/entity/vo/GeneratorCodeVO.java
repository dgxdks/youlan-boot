package com.youlan.tools.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GeneratorCodeVO {

    @Schema(title = "包路径")
    private String packageName;

    @Schema(title = "模块名称")
    private String moduleName;

    @Schema(title = "业务名称")
    private String bizName;

    @Schema(title = "controller代码内容")
    private String controller;

    @Schema(title = "service代码内容")
    private String service;

    @Schema(title = "mapper代码内容")
    private String mapper;

    @Schema(title = "mapper.xml代码内容")
    private String mapperXml;

    @Schema(title = "entity代码内容")
    private String entity;

    @Schema(title = "entityDto代码内容")
    private String entityDto;

    @Schema(title = "entityPageDto代码内容")
    private String entityPageDto;

    @Schema(title = "entityVo代码内容")
    private String entityVo;
}
