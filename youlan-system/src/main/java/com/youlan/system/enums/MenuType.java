package com.youlan.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuType {
    DIRECTORY("1", "目录"),
    MENU("2", "菜单"),
    BUTTON("3", "按钮");

    private final String code;
    private final String text;
}
