-- ----------------------------
-- 机构表
-- ----------------------------
drop table if exists t_sys_org;
create table t_sys_org
(
    org_id        bigint      not null auto_increment comment '主键ID',
    org_name      varchar(64) not null comment '机构名称',
    org_type      varchar(4)  not null comment '机构类型(字典类型[sys_org_type])',
    org_level     int(4)      not null comment '机构层级',
    org_ancestors varchar(64) not null comment '机构祖级',
    parent_org_id longtext    not null comment '父级机构ID',
    org_sort      int(4)       default 0 comment '机构排序',
    org_remark    varchar(128) default '' comment '机构备注',
    org_status    varchar(4)   default '1' comment '机构状态(1-正常 2-停用)',
    org_sts       varchar(4)   default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (org_id)
) auto_increment = 100 comment '机构表';
insert into t_sys_org(org_id, org_name, org_type, org_level, org_ancestors, parent_org_id, org_sort)
values (100, '幽兰科技', '0', 0, '0', 0, 0),
       (101, '杭州总公司', '1', 1, '0,100', 100, 0),
       (102, '研发部门', '1', 2, '0,100,101', 101, 0),
       (103, '市场部门', '1', 2, '0,100,101', 101, 1),
       (104, '测试部门', '1', 2, '0,100,101', 101, 2),
       (105, '财务部门', '1', 2, '0,100,101', 101, 3),
       (106, '运维部门', '1', 2, '0,100,101', 101, 4),
       (107, '西安分公司', '1', 2, '0,100', 100, 1),
       (108, '市场部', '1', 2, '0,100,107', 107, 0),
       (109, '财务部', '1', 2, '0,100,107', 107, 1);

-- ----------------------------
-- 部门表
-- ----------------------------
drop table if exists t_sys_dept;
create table t_sys_dept
(
    id          bigint not null auto_increment comment '主键ID',
    org_id      bigint not null comment '机构ID',
    leader      varchar(32) default '' comment '负责人',
    phone       varchar(32) default '' comment '联系电话',
    email       varchar(64) default '' comment '邮箱',
    status      varchar(4)  default '1' comment '状态(1-正常 2-停用)',
    create_id   bigint      default 0 comment '创建用户ID',
    create_by   varchar(64) default '' comment '创建用户',
    update_id   bigint      default 0 comment '修改用户ID',
    update_by   varchar(64) default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    sts         varchar(4)  default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (id)
) auto_increment = 100 comment '部门表';
insert into t_sys_dept(org_id, leader, phone, email, create_id, create_by, create_time)
values (100, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (101, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (102, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (103, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (104, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (105, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (106, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (107, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (108, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate()),
       (109, '幽兰', '1888888888', 'yl@youlan.com', 100, 'admin', sysdate());

-- ----------------------------
-- 用户表
-- ----------------------------
drop table if exists t_sys_user;
create table t_sys_user
(
    id            bigint       not null auto_increment comment '主键ID',
    org_id        bigint       not null comment '机构ID',
    user_name     varchar(64)  not null comment '用户账号',
    user_password varchar(128) not null comment '用户密码',
    user_mobile   varchar(32) default '' comment '用户手机',
    nick_name     varchar(64)  not null comment '用户昵称',
    email         varchar(64) comment '用户邮箱',
    avatar        varchar(128) comment '用户头像',
    sex           char(1)     default '3' comment '用户性别(1-男 2-女 3-未知)',
    status        varchar(4)  default '1' comment '状态(1-正常 2-停用)',
    login_ip      varchar(32) comment '最后登录IP',
    login_time    varchar(32) comment '最后登录时间',
    remark        varchar(128) comment '备注',
    create_id     bigint      default 0 comment '创建用户ID',
    create_by     varchar(64) default '' comment '创建用户',
    update_id     bigint      default 0 comment '修改用户ID',
    update_by     varchar(64) default '' comment '修改用户',
    create_time   datetime comment '创建时间',
    update_time   datetime comment '修改时间',
    sts           varchar(4)  default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (id)
) auto_increment = 100 comment '用户表';
insert into t_sys_user(id, org_id, user_name, user_password, user_mobile, nick_name, email, sex, create_id,
                       create_by, create_time)
values (100, 100, 'admin', '$2a$10$a.PsU/F7gk0OMVj8NJsg3.aPBFrwtxc25T9t9lgMMnuRg00ev7nGK', '1888888888', '超级管理员',
        'yl@youlan.com', '1', 100, 'admin', sysdate());

-- ----------------------------
-- 岗位表
-- ----------------------------
drop table if exists t_sys_post;
create table t_sys_post
(
    id          bigint      not null auto_increment comment '主键ID',
    post_name   varchar(32) not null comment '岗位名称',
    post_code   varchar(32) not null comment '岗位编码',
    sort        int(4)       default 0 comment '排序',
    status      varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark      varchar(128) default '' comment '备注',
    create_id   bigint       default 0 comment '创建用户ID',
    create_by   varchar(64)  default '' comment '创建用户',
    update_id   bigint       default 0 comment '修改用户ID',
    update_by   varchar(64)  default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    sts         varchar(4)   default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (id)
) auto_increment = 100 comment '岗位表';
insert into t_sys_post(id, post_name, post_code, create_id, create_by, create_time)
values (100, '董事长', 'ceo', 100, 'admin', sysdate()),
       (101, '项目经理', 'se', 100, 'admin', sysdate()),
       (102, '人力资源', 'hr', 100, 'admin', sysdate()),
       (103, '普通员工', 'user', 100, 'admin', sysdate());

-- ----------------------------
-- 用户关联岗位表(一对多)
-- ----------------------------
drop table if exists t_sys_user_post;
create table t_sys_user_post
(
    id      bigint not null auto_increment comment '主键ID',
    user_id bigint not null comment '用户ID',
    post_id bigint not null comment '岗位ID',
    primary key (id)
) auto_increment = 100 comment '用户关联岗位表';

-- ----------------------------
-- 角色表
-- ----------------------------
drop table if exists t_sys_role;
create table t_sys_role
(
    id          bigint      not null auto_increment comment '主键ID',
    role_name   varchar(32) not null comment '角色名称',
    role_str    varchar(64) not null comment '角色字符',
    role_scope  char(1)      default '4' comment '角色数据权限范围(1-全部数据权限 2-自定义数据权限 3-本机构数据权限 4-本机构及以下数据权限 5-仅本人数据权限)',
    sort        int(4)       default 0 comment '排序',
    status      varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark      varchar(128) default '' comment '备注',
    create_id   bigint       default 0 comment '创建用户ID',
    create_by   varchar(64)  default '' comment '创建用户',
    update_id   bigint       default 0 comment '修改用户ID',
    update_by   varchar(64)  default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    sts         varchar(4)   default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (id)
) auto_increment = 100 comment '角色表';
insert into t_sys_role(id, role_name, role_str, role_scope, remark, create_id, create_by, create_time)
values (100, '超级管理员', 'admin', '1', '超级管理员', 100, 'admin', sysdate());

-- ----------------------------
-- 用户关联角色表(一对多)
-- ----------------------------
drop table if exists t_sys_user_role;
create table t_sys_user_role
(
    id      bigint not null auto_increment comment '主键ID',
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (id)
) auto_increment = 100 comment '用户关联角色表';
insert into t_sys_user_role (user_id, role_id)
values (100, 100);

-- ----------------------------
-- 角色关联机构表(一对多)
-- ----------------------------
drop table if exists t_sys_role_org;
create table t_sys_role_org
(
    id      bigint not null auto_increment comment '主键ID',
    role_id bigint not null comment '角色ID',
    org_id  bigint not null comment '机构ID',
    primary key (id)
) auto_increment = 100 comment '角色关联机构表';

-- ----------------------------
-- 菜单表
-- ----------------------------
drop table if exists t_sys_menu;
create table t_sys_menu
(
    id             bigint      not null auto_increment comment '主键ID',
    menu_name      varchar(32) not null comment '菜单名称',
    menu_type      char(1)      default '1' comment '菜单类型(1-目录 2-菜单 3-按钮)',
    menu_perms     varchar(64)  default '' comment '菜单权限字符',
    parent_id      bigint       default 0 comment '父级菜单ID',
    menu_icon      varchar(64)  default '' comment '菜单图标',
    route_path     varchar(256) default '' comment '路由路径',
    route_query    varchar(256) default '' comment '路由参数',
    route_cache    char(1)      default '1' comment '路由缓存(1-是 2-否)',
    component_path varchar(256) default '' comment '组件路径',
    is_frame       char(1)      default '2' comment '是否外链(1-是 2-否)',
    sort           int(4)       default 0 comment '排序',
    visible        char(1)      default '1' comment '显示状态(1-显示 2-不显示)',
    status         varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark         varchar(128) default '' comment '备注',
    create_id      bigint       default 0 comment '创建用户ID',
    create_by      varchar(64)  default '' comment '创建用户',
    update_id      bigint       default 0 comment '修改用户ID',
    update_by      varchar(64)  default '' comment '修改用户',
    create_time    datetime comment '创建时间',
    update_time    datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '菜单表';
-- 一级菜单
insert into t_sys_menu
values (100, '系统管理', '1', 'system', 0, 'system', '', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (200, '系统监控', '1', 'monitor', 0, 'monitor', '', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (300, '系统工具', '1', 'tools', 0, 'tool', '', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(),
        null);
-- 二级菜单
insert into t_sys_menu
values (101, '用户管理', '2', 'user', 100, 'user', 'system/user' 'system/user/index', '', '1', '', '2', 0, '1', '1', '',
        100, 'admin', 0, '', sysdate(), null),
       (102, '角色管理', '2', 'role', 100, 'peoples', 'system/role' 'system/role/index', '', '1', '', '2', 0, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (103, '菜单管理', '2', 'menu', 100, 'tree-table', 'system/menu' 'system/menu/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (104, '部门管理', '2', 'dept', 100, 'tree', 'system/dept' 'system/dept/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (105, '岗位管理', '2', 'post', 100, 'post', 'system/post' 'system/post/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (106, '字典管理', '2', 'dict', 100, 'dict', 'system/dict' 'system/dict/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (107, '系统参数', '2', 'config', 100, 'edit', 'system/config' 'system/config/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (108, '通知公告', '2', 'notice', 100, 'message', 'system/notice' 'system/notice/index', '', '1', '', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (109, '日志管理', '1', 'log', 100, 'log', '' '', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (201, '在线用户', '2', 'onlineUser', 100, 'online', 'monitor/onlineUser' 'monitor/onlineUser/index', '', '1', '',
        '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (202, '缓存监控', '2', 'cacheMonitor', 100, 'redis', 'monitor/cacheMonitor' 'monitor/cacheMonitor/index', '',
        '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null);

-- 三级菜单
insert
into t_sys_menu
values (110, '操作日志', '2', 'operationLog', 100, 'log', 'system/operationLog' 'system/operationLog/index', '', '1',
        '', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (111, '登录日志', '2', 'loginLog', 100, 'log', 'system/loginLog' 'system/loginLog/index', '', '1', '', '2', 0,
        '1', '1', '', 100, 'admin', 0, '', sysdate(), null);

-- ----------------------------
-- 角色关联菜单表(一对多)
-- ----------------------------
drop table if exists t_sys_role_menu;
create table t_sys_role_menu
(
    id      bigint not null auto_increment comment '主键ID',
    role_id bigint not null comment '角色ID',
    menu_id bigint not null comment '菜单ID',
    primary key (id)
) auto_increment = 100 comment '角色关联菜单表';

-- ----------------------------
-- 系统操作日志表
-- ----------------------------
drop table if exists t_sys_operation_log;
create table t_sys_operation_log
(
    id              bigint not null auto_increment comment '主键ID',
    log_name        varchar(64)  default '' comment '日志名称',
    log_type        varchar(4)   default '99' comment '日志类型(字典类型[tools_generator_query_type])',
    log_user        bigint       default 100 comment '日志用户ID',
    log_by          varchar(64)  default '' comment '日志用户',
    log_time        datetime comment '日志时间',
    log_status      char(1)      default '1' comment '日志状态(1-正常 2-异常)',
    error_msg       varchar(256) default '' comment '错误信息',
    method          varchar(64)  default '' comment '调用方法',
    source_ip       varchar(32)  default '' comment '来源IP',
    source_location varchar(32)  default '' comment '来源位置',
    http_method     varchar(16)  default '' comment 'HTTP请求方法',
    http_url        varchar(256) default '' comment 'HTTP请求路径',
    http_query      longtext comment 'HTTP请求参数',
    http_body       longtext comment 'HTTP请求体',
    http_response   longtext comment 'HTTP响应体',
    primary key (id)
) auto_increment = 100 comment '系统操作日志表';

-- ----------------------------
-- 登录日志表
-- ----------------------------
drop table if exists t_sys_login_log;
create table t_sys_login_log
(
    id             bigint not null auto_increment comment '主键ID',
    user_name      varchar(64)  default '' comment '用户名',
    source_type    varchar(4)   default '99' comment '来源类型(1-后台端 2-移动端 99-其它)',
    login_ip       varchar(32)  default '' comment '登录IP',
    login_location varchar(32)  default '' comment '登录位置',
    login_status   char(1)      default '1' comment '登录状态(1-成功 2-失败)',
    login_time     datetime comment '登录时间',
    login_msg      varchar(256) default '' comment '登录消息',
    user_agent     varchar(128) default '' comment '用户浏览器代理',
    primary key (id)
) auto_increment = 100 comment '登录日志表';


-- ----------------------------
-- 系统配置表
-- ----------------------------
drop table if exists t_sys_config;
create table t_sys_config
(
    id           bigint not null auto_increment comment '主键ID',
    config_name  varchar(64)  default '' comment '配置名称',
    config_key   varchar(128) default '' comment '配置键名',
    config_value varchar(256) default '' comment '配置键值',
    config_type  varchar(4)   default '1' comment '配置类型(1-内置参数 2-外置参数)',
    remark       varchar(128) default '' comment '备注',
    create_id    bigint       default 0 comment '创建用户ID',
    create_by    varchar(64)  default '' comment '创建用户',
    update_id    bigint       default 0 comment '修改用户ID',
    update_by    varchar(64)  default '' comment '修改用户',
    create_time  datetime comment '创建时间',
    update_time  datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '系统配置表';
insert into t_sys_config(config_name, config_key, config_value, config_type, remark, create_id, create_by,
                         create_time)
values ('用户初始密码', 'sys.user.initPassword', '123456', '1', '用户初始密码', 100, 'admin', sysdate()),
       ('是否开启图片验证码', 'sys.captcha.image.enabled', 'true', '1', 'ture-开启 false-关闭', 100, 'admin',
        sysdate()),
       ('用户登录最大重试次数', 'sys.login.retry.times', '-1', '1', '小于等于0则不做限制', 100, 'admin',
        sysdate()),
       ('用户登录重试策略', 'sys.login.retry.strategy', 'USERNAME', '1',
        'USERNAME-按用户名记录重试次数 USERNAME_IP-按用户名和IP记录重试次数', 100, 'admin', sysdate()),
       ('用户登录锁定时间', 'sys.login.lock.time', '60', '1', '用户登录失败超过重试次数后锁定时间(秒)', 100,
        'admin', sysdate());


-- ----------------------------
-- 字典类型表
-- ----------------------------
drop table if exists t_sys_dict_type;
create table t_sys_dict_type
(
    id          bigint      not null auto_increment comment '主键ID',
    type_name   varchar(64) not null comment '字典类型名称',
    type_key    varchar(64) not null comment '字典类型键名',
    status      varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark      varchar(128) default '' comment '备注',
    create_id   bigint       default 0 comment '创建用户ID',
    create_by   varchar(64)  default '' comment '创建用户',
    update_id   bigint       default 0 comment '修改用户ID',
    update_by   varchar(64)  default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    sts         varchar(4)   default '1' comment '逻辑删除(1-未删除 2-已删除)',
    primary key (id)
) auto_increment = 100 comment '字典类型表';
insert into t_sys_dict_type(id, type_name, type_key, remark, create_id, create_by, create_time)
values (100, '机构类型', 'sys_org_type', '机构类型列表', 100, 'admin', sysdate()),
       (101, '查询类型', 'tools_generator_query_type', '查询类型列表', 100, 'admin', sysdate()),
       (102, '组件类型', 'tools_generator_component_type', '组件类型列表', 100, 'admin', sysdate()),
       (103, 'Java类型', 'tools_generator_java_type', 'Java类型列表', 100, 'admin', sysdate()),
       (104, '操作日志类型', 'sys_operation_log_type', '操作日志类型列表', 100, 'admin', sysdate()),
       (105, '数据权限范围', 'sys_data_scope', '数据权限范围列表', 100, 'admin', sysdate()),
       (106, '用户性别', 'sys_user_sex', '用户性别列表', 100, 'admin', sysdate());
-- ----------------------------
-- 字典值表
-- ----------------------------
drop table if exists t_sys_dict_data;
create table t_sys_dict_data
(
    id          bigint       not null auto_increment comment '主键ID',
    type_key    varchar(64)  not null comment '字典类型键名',
    data_name   varchar(64)  not null comment '字典值名称',
    data_value  varchar(128) not null comment '字典值键值',
    ui_class    varchar(64)  default '' comment 'UI样式',
    css_class   varchar(64)  default '' comment 'CSS样式',
    is_default  varchar(4)   default '2' comment '是否默认(1-是 2-否)',
    sort        int(4)       default 0 comment '排序',
    status      varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark      varchar(128) default '' comment '备注',
    create_id   bigint       default 0 comment '创建用户ID',
    create_by   varchar(64)  default '' comment '创建用户',
    update_id   bigint       default 0 comment '修改用户ID',
    update_by   varchar(64)  default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '字典值表';
insert into t_sys_dict_data(type_key, data_name, data_value, ui_class, css_class, is_default, create_id, create_by,
                            create_time)
values ('sys_org_type', '平台', '0', '', '', '2', 100, 'admin', sysdate()),
       ('sys_org_type', '部门', '1', '', '', '1', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '等于', 'EQUAL', '', '', '1', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '不等于', 'NOT_EQUAL', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '大于', 'GREATER_THAN', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '大于等于', 'GREATER_EQUAL', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '小于', 'LESS_THAN', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '小于等于', 'LESS_EQUAL', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '中间模糊', 'LIKE', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '左边模糊', 'LIKE_RIGHT', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '右边模糊', 'LIKE_LEFT', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '包含', 'IN', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '不包含', 'NOT_IN', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '不为空', 'NOT_NULL', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_query_type', '之间', 'BETWEEN', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '文本框', 'INPUT', '', '', '1', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '文本域', 'TEXTAREA', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '下拉框', 'SELECT', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '复选框', 'CHECKBOX', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '单选框', 'RADIO', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '日期选择', 'DATE', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '时间选择', 'DATETIME', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '日期范围选择', 'DATE_RANGE', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '时间范围选择', 'DATETIME_RANGE', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '图片上传', 'IMAGE_UPLOAD', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '图片显示', 'IMAGE_SHOW', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '文件上传', 'FILE_UPLOAD', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_component_type', '富文本编辑器', 'RICH_TEXT', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'String', 'String', '', '', '1', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'Long', 'Long', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'Integer', 'Integer', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'Double', 'Double', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'BigDecimal', 'BigDecimal', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'Date', 'Date', '', '', '2', 100, 'admin', sysdate()),
       ('tools_generator_java_type', 'Boolean', 'Boolean', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '新增', '1', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '修改', '2', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '删除', '3', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '分页查询', '4', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '列表查询', '5', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '导入', '6', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '导出', '7', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '其它', '99', '', '', '1', 100, 'admin', sysdate()),
       ('sys_data_scope', '全部数据权限', '1', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '自定义数据权限', '2', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '本机构数据权限', '3', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '本机构及以下数据权限', '4', '', '', '1', 100, 'admin', sysdate()),
       ('sys_data_scope', '仅本人数据权限', '5', '', '', '2', 100, 'admin', sysdate()),
       ('sys_user_sex', '男', '1', '', '', '1', 100, 'admin', sysdate()),
       ('sys_user_sex', '女', '2', '', '', '2', 100, 'admin', sysdate()),
       ('sys_user_sex', '未知', '3', '', '', '2', 100, 'admin', sysdate());