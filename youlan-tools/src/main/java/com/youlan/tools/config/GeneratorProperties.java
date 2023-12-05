package com.youlan.tools.config;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class GeneratorProperties {

    @Schema(description = "包路径")
    private String packageName;

    @Schema(description = "是否去除表前缀")
    private Boolean tableRemovePrefix;

    @Schema(description = "可匹配的表前缀列表")
    private List<String> tableMatchPrefix;

    @Schema(description = "编辑时要排除的列字段")
    private Set<String> editColumnExclude;

    @Schema(description = "查询时要排除的列字段")
    private Set<String> queryColumnExclude;

    @Schema(description = "显示时要排除的列字段")
    private Set<String> viewColumnExclude;

    @Schema(description = "默认是否生成DTO")
    private boolean needEntityDto = true;

    @Schema(description = "默认是否生成PageDTO")
    private boolean needEntityPageDto = true;

    @Schema(description = "默认是否生成VO")
    private boolean needEntityVo = true;

    @Schema(description = "表描述转功能名称替换正则")
    private String tableFeatureRegex;

    public boolean inQueryColumnExclude(String columnName) {
        return CollectionUtil.isNotEmpty(queryColumnExclude) && queryColumnExclude.contains(columnName);
    }

    public boolean inViewColumnExclude(String columnName) {
        return CollectionUtil.isNotEmpty(viewColumnExclude) && viewColumnExclude.contains(columnName);
    }

    public boolean inEditColumnExclude(String columnName) {
        return CollectionUtil.isNotEmpty(editColumnExclude) && editColumnExclude.contains(columnName);
    }
}
