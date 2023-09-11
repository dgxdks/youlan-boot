package com.youlan.common.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageAclType {
    DEFAULT("default", "默认"),

    PRIVATE("private", "私有"),

    PUBLIC_READ("public-read", "公共读"),

    PUBLIC_READ_WRITE("public-read-write", "公共读写");

    private final String code;
    private final String text;

    public static boolean isDefault(String code) {
        return DEFAULT.code.equals(code);
    }

    public static boolean isNotDefault(String code) {
        return !isDefault(code);
    }
}
