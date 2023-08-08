package com.youlan.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceType {
    PC("1", "PC端"),
    MOBILE("2", "移动端"),
    OTHER("99", "其它");


    private final String code;
    private final String text;
}
