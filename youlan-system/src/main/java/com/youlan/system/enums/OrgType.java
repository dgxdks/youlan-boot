package com.youlan.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrgType {

    PLATFORM("0", "平台"),
    DEPT("1", "部门");

    private final String code;
    private final String text;
}
