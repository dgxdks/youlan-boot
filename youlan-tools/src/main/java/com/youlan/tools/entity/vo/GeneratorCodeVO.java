package com.youlan.tools.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorCodeVO {

    @Schema(title = "代码内容")
    private String codeContent;

    @Schema(title = "包名称")
    private String packageName;

    @Schema(title = "模版名称")
    private String vmName;

    @Schema(title = "代码名称")
    private String codeName;
}
