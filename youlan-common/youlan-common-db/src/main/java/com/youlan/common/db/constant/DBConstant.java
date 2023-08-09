package com.youlan.common.db.constant;

/**
 * 数据库常量
 */
public class DBConstant {
    // ******************** 状态 ********************
    /**
     * 正常
     */
    public static final String VAL_STATUS_ENABLED = "1";
    /**
     * 正常描述
     */
    public static final String DESC_STATUS_ENABLED = "正常";
    /**
     * 停用
     */
    public static final String VAL_STATUS_DISABLED = "2";
    /**
     * 描述
     */
    public static final String DESC_STATUS_DISABLED = "停用";
    /**
     * 状态列名
     */
    public static final String COL_STATUS = "status";
    /**
     * 状态描述
     */
    public static final String DESC_STATUS = "状态(1-正常 2-停用)";
    /**
     * 状态非空描述
     */
    public static final String DESC_STATUS_REQUIRED = "状态参数不能为空";

    // ******************** 创建用户 ********************
    /**
     * 创建用户列名
     */
    public static final String COL_CREATE_BY = "create_by";
    /**
     * 创建用户列名驼峰
     */
    public static final String COL_CREATE_BY_CAMEL = "createBy";
    /**
     * 创建用户描述
     */
    public static final String DESC_CREATE_BY = "创建用户";

    // ******************** 创建用户ID ********************
    /**
     * 创建用户ID列名
     */
    public static final String COL_CREATE_ID = "create_id";
    /**
     * 创建用户ID列名驼峰
     */
    public static final String COL_CREATE_ID_CAMEL = "createId";
    /**
     * 创建用户ID描述
     */
    public static final String DESC_CREATE_ID = "创建用户ID";

    // ******************** 修改用户ID ********************
    /**
     * 修改用户ID列名
     */
    public static final String COL_UPDATE_ID = "update_id";
    /**
     * 修改用户ID列名驼峰
     */
    public static final String COL_UPDATE_ID_CAMEL = "updateId";
    /**
     * 修改用户ID描述
     */
    public static final String DESC_UPDATE_ID = "修改用户ID";

    // ******************** 修改用户 ********************
    /**
     * 修改用户列名
     */
    public static final String COL_UPDATE_BY = "update_by";
    /**
     * 修改用户列名驼峰
     */
    public static final String COL_UPDATE_BY_CAMEL = "updateBy";
    /**
     * 修改用户描述
     */
    public static final String DESC_UPDATE_BY = "修改用户";

    // ******************** 创建时间 ********************
    /**
     * 创建时间列名
     */
    public static final String COL_CREATE_TIME = "create_time";
    /**
     * 创建时间列名驼峰
     */
    public static final String COL_CREATE_TIME_CAMEL = "createTime";
    /**
     * 创建时间描述
     */
    public static final String DESC_CREATE_TIME = "创建时间";

    // ******************** 修改时间 ********************
    /**
     * 修改时间列名
     */
    public static final String COL_UPDATE_TIME = "update_time";
    /**
     * 修改时间列名驼峰
     */
    public static final String COL_UPDATE_TIME_CAMEL = "updateTime";
    /**
     * 修改时间描述
     */
    public static final String DESC_UPDATE_TIME = "修改时间";


    // ******************** 逻辑删除 ********************
    /**
     * 未删除
     */
    public static final String VAL_STS_NO = "1";
    /**
     * 已删除
     */
    public static final String VAL_STS_YES = "2";
    /**
     * 逻辑删除列名
     */
    public static final String COL_STS = "sts";
    /**
     * 逻辑删除描述
     */
    public static final String DESC_STS = "逻辑删除(1-未删除 2-已删除)";

    // ******************** 备注 ********************
    /**
     * 备注列名
     */
    public static final String COL_REMARK = "remark";
    /**
     * 备注描述
     */
    public static final String DESC_REMARK = "备注";

    // ******************** 主键 ********************
    /**
     * 主键列名
     */
    public static final String COL_ID = "id";
    /**
     * 主键ID描述
     */
    public static final String DESC_ID = "主键ID";
    /**
     * 主键ID非空描述
     */
    public static final String DESC_ID_REQUIRED = "ID参数不能为空";

    // ******************** 是否 ********************
    public static final String VAL_YES = "1";
    public static final String VAL_NO = "2";
    public static final String DESC_YES_NO = "(1-是 2-否)";

    // ******************** 排序 ********************
    /**
     * 排序列名
     */
    public static final String COL_SORT = "sort";
    /**
     * 排序描述
     */
    public static final String DESC_SORT = "排序";
    /**
     * 排序非空描述
     */
    public static final String DESC_SORT_REQUIRED = "排序参数不能为空";

    public static String boolean2YesNo(boolean value) {
        return value ? VAL_YES : VAL_NO;
    }

    public static boolean yesNo2Boolean(String yesNo) {
        return VAL_YES.equals(yesNo);
    }
}
