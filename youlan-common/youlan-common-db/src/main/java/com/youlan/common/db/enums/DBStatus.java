package com.youlan.common.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.youlan.common.db.constant.DBConstant.*;


@Getter
@AllArgsConstructor
public enum DBStatus {
    /**
     * 正常
     */
    ENABLED(VAL_STATUS_ENABLED, DESC_STATUS_ENABLED),
    /**
     * 停用
     */
    DISABLED(VAL_STATUS_DISABLED, DESC_STATUS_DISABLED);

    private final String code;

    private final String text;
}
