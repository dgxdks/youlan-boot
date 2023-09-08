package com.youlan.common.db.entity.dto;

import com.youlan.common.validator.anno.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(title = "排序参数")
public class PageSortDTO {

    @NotBlank(message = "排序列不能为空")
    @Xss(message = "不允许包含特殊字符")
    @Pattern(regexp = "^[a-zA-Z0-9,]+$", message = "排序列只能包含（小写字母，数字，逗号）")
    @Schema(title = "排序列")
    private String column;

    @Schema(title = "是否升序")
    private Boolean asc = true;
}
