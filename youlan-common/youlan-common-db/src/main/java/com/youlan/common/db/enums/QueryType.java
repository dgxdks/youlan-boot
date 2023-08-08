package com.youlan.common.db.enums;

public enum QueryType {
    /**
     * 等于
     */
    EQUAL,
    /**
     * 不等于
     */
    NOT_EQUAL,
    /**
     * 大于
     */
    GREATER_THAN,
    /**
     * 大于等于
     */
    GREATER_EQUAL,
    /**
     * 小于
     */
    LESS_THAN,
    /**
     * 小于等于
     */
    LESS_EQUAL,
    /**
     * 中间模糊
     */
    LIKE,
    /**
     * 左边模糊
     */
    LIKE_LEFT,
    /**
     * 右边模糊
     */
    LIKE_RIGHT,
    /**
     * 包含
     */
    IN,
    /**
     * 不包含
     */
    NOT_IN,
    /**
     * 不为空
     */
    NOT_NULL,
    /**
     * 之间
     */
    BETWEEN
}
