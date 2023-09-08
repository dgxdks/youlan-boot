package com.youlan.common.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.youlan.common.db.constant.DBConstant.*;


@Getter
@AllArgsConstructor
public enum DBYesNo {
    /**
     * 是
     */
    YES(VAL_YES, DESC_YES),
    /**
     * 否
     */
    NO(VAL_NO, DESC_NO);

    private final String code;

    private final String text;
}
