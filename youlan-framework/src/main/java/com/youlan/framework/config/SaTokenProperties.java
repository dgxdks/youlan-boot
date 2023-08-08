package com.youlan.framework.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class SaTokenProperties {
    /**
     * 要排除的路径
     */
    private List<String> excludePathPatterns = new ArrayList<>();
}
