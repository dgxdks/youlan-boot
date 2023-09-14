package com.youlan.framework.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaTokenProperties {
    /**
     * 要排除的路径
     */
    private List<String> excludePathPatterns = new ArrayList<>();
}
