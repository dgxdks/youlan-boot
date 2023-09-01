package com.youlan.system.constant;

public class SystemConstant {
    // ******************** 操作日志状态 ********************
    public static final String OPERATION_LOG_STATUS_OK = "1";
    public static final String OPERATION_LOG_STATUS_ERROR = "2";

    // ******************** 缓存前缀 ********************
    /**
     * 系统配置缓存前缀
     */
    public static final String REDIS_PREFIX_CONFIG = "system_config:";
    /**
     * 数据字典缓存前缀
     */
    public static final String REDIS_PREFIX_DICT = "system_dict:";
    /**
     * 系统用户登录重试缓存前缀
     */
    public static final String REDIS_PREFIX_LOGIN_RETRY = "system_login_retry:";
    /**
     * 存储配置缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_CONFIG = "system_storage_config:";
    /**
     * 存储记录objectId缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_OBJECT_ID = "system_storage_record:objectId:";
    /**
     * 存储记录fileName缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_FILE_NAME = "system_storage_record:fileName:";


    // ******************** 机构常量 ********************
    public static final String ORG_TABLE = "t_sys_org";
    public static final String ALIAS_ENTITY_TABLE = "yt";
    public static final String ALIAS_ORG_TABLE = "yo";
    public static final String COL_ORG_NAME = "org_name";
    public static final String COL_ORG_TYPE = "org_type";
    public static final String COL_ORG_LEVEL = "org_level";
    public static final String COL_ORG_ANCESTORS = "org_ancestors";
    public static final String COL_PARENT_ORG_ID = "parent_org_id";
    public static final String COL_ORG_SORT = "org_sort";
    public static final String COL_ORG_REMARK = "org_remark";
    public static final String COL_ORG_STATUS = "org_status";
    public static final String COL_ORG_ID = "org_id";
    public static final String COL_ORG_STS = "org_sts";

    // ******************** 机构类型常量 ********************
    public static final String ORG_TYPE_PLATFORM = "0";
    public static final String ORG_TYPE_DEPT = "1";

    // ******************** 机构类型常量 ********************
    /**
     * 全部数据权限
     */
    public static final String ROLE_SCOPE_ALL = "1";
    /**
     * 自定义数据权限
     */
    public static final String ROLE_SCOPE_CUSTOM = "2";
    /**
     * 本机构数据权限
     */
    public static final String ROLE_SCOPE_ORG = "3";
    /**
     * 本机构及以下数据权限
     */
    public static final String ROLE_SCOPE_ORG_CHILDREN = "4";
    /**
     * 仅本人数据权限
     */
    public static final String ROLE_SCOPE_USER = "5";

    // ******************** 机构类型常量 ********************
    /**
     * 内置参数
     */
    public static final String CONFIG_TYPE_INNER = "1";
    /**
     * 外置参数
     */
    public static final String CONFIG_TYPE_OUTER = "2";

    // ******************** 用户常量 ********************
    public static final Long SUPER_ADMIN_USER_ID = 100L;

    // ******************** 系统配置常量 ********************
    /**
     * 用户初始Miami
     */
    public static final String CONFIG_KEY_USER_INIT_PASSWORD = "sys.user.initPassword";
    /**
     * 是否开启验证码
     */
    public static final String CONFIG_KEY_CAPTCHA_IMAGE_ENABLED = "sys.captcha.image.enabled";
    /**
     * 用户登录最大重试次数
     */
    public static final String CONFIG_KEY_LOGIN_RETRY_TIMES = "sys.login.retry.times";
    /**
     * 用户登录重试策略
     */
    public static final String CONFIG_KEY_LOGIN_RETRY_STRATEGY = "sys.login.retry.strategy";
    /**
     * 用户登录重试策略(USERNAME-按用户名记录重试次数)
     */
    public static final String CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME = "USERNAME";
    /**
     * 用户登录重试策略(USERNAME_IP-按用户名和IP记录重试次数)
     */
    public static final String CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME_IP = "USERNAME_IP";
    /**
     * 用户登录锁定时间(秒)
     */
    public static final String CONFIG_KEY_LOGIN_LOCK_TIME = "sys.login.lock.time";

    // ******************** 来源类型常量 ********************
    /**
     * 后台端
     */
    public static final String SOURCE_TYPE_DESKTOP = "1";
    /**
     * 移动端
     */
    public static final String SOURCE_TYPE_MOBILE = "2";
    /**
     * 其它
     */
    public static final String SOURCE_TYPE_OTHER = "99";

    /**
     * 超级管理员用户ID
     */
    public static final Long ADMIN_USER_ID = 100L;

    /**
     * 超级管理员角色字符
     */
    public static final String ADMIN_ROLE_STR = "admin";

    /**
     * 超级管理员角色ID
     */
    public static final Long ADMIN_ROLE_ID = 100L;

    /**
     * 超级管理员权限字符
     */
    public static final String ADMIN_PERM_STR = "*";
}
