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


    // ******************** 数据权限常量 ********************
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
    public static final String ROLE_SCOPE_CURRENT_ORG = "3";
    /**
     * 本机构及以下数据权限
     */
    public static final String ROLE_SCOPE_ORG_BELOW = "4";
    /**
     * 仅本人数据权限
     */
    public static final String ROLE_SCOPE_CURRENT_USER = "5";


    // ******************** 系统配置常量 ********************
    /**
     * 内置参数
     */
    public static final String CONFIG_TYPE_INNER = "1";
    /**
     * 外置参数
     */
    public static final String CONFIG_TYPE_OUTER = "2";

    /**
     * 用户初始密码
     */
    public static final String CONFIG_KEY_USER_INIT_PASSWORD = "sys.user.initPassword";
    /**
     * 是否开启验证码
     */
    public static final String CONFIG_KEY_CAPTCHA_IMAGE_ENABLED = "sys.captcha.image.enabled";
    public static final String CONFIG_KEY_LOGIN_ACCOUNT_REGISTRY_ENABLED = "sys.login.account.registry.enabled";
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
    public static final String CONFIG_KEY_LOGIN_LOG_ENABLED = "sys.login.log.enabled";

    // ******************** 超级管理员常量 ********************
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

    // ******************** 用户常量 ********************
    public static final Long SUPER_ADMIN_USER_ID = 100L;

    // ******************** 机构常量 ********************
    public static final Long TOP_ORG_ID = 100L;
}
