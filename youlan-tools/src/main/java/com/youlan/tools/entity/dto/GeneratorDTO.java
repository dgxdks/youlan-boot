package com.youlan.tools.entity.dto;

import com.youlan.tools.entity.GeneratorColumn;
import com.youlan.tools.entity.GeneratorTable;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class GeneratorDTO {
    @NotNull(message = "表信息不能为空")
    private GeneratorTable generatorTable;

    @NotNull(message = "列信息不能为空")
    private List<GeneratorColumn> generatorColumnList;
}
