package com.youlan.tools.entity.vo;

import com.youlan.tools.entity.GeneratorColumn;
import com.youlan.tools.entity.GeneratorTable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GeneratorVO {
    private GeneratorTable generatorTable;
    private List<GeneratorColumn> generatorColumnList;
}
