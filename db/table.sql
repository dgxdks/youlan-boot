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
        'yl@youlan.com', '1', 100, 'admin', sysdate()),
       (101, 100, 'youlan', '$2a$10$a.PsU/F7gk0OMVj8NJsg3.aPBFrwtxc25T9t9lgMMnuRg00ev7nGK', '1888888888', '后台用户',
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
-- 一级菜单(相邻菜单之间间隔100个ID预留给二级菜单)
insert into t_sys_menu
values (100, '系统管理', '1', 'system', 0, 'system', 'system', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (200, '系统监控', '1', 'monitor', 0, 'monitor', 'monitor', '', '1', '', '2', 0, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (300, '系统工具', '1', 'tools', 0, 'tool', 'tools', '', '1', '', '1', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null);
-- 二级菜单(二级菜单ID从父级菜单ID开始递增)
insert into t_sys_menu
values (101, '用户管理', '2', 'system:user', 100, 'user', 'user', '', '1', 'system/user/index', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (102, '角色管理', '2', 'system:role', 100, 'peoples', 'role', '', '1', 'system/role/index', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (103, '菜单管理', '2', 'system:menu', 100, 'tree-table', 'menu', '', '1', 'system/menu/index', '2', 0,
        '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (104, '部门管理', '2', 'system:dept', 100, 'tree', 'dept', '', '1', 'system/dept/index', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (105, '岗位管理', '2', 'system:post', 100, 'post', 'post', '', '1', 'system/post/index', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (106, '字典管理', '2', 'system:dict', 100, 'dict', 'dict', '', '1', 'system/dict/index', '2', 0, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (107, '系统参数', '2', 'system:config', 100, 'edit', 'config', '', '1', 'system/config/index', '2', 0,
        '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (108, '通知公告', '2', 'system:notice', 100, 'message', 'notice', '', '1', 'system/notice/index', '2',
        0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (109, '日志管理', '1', 'system:log', 100, 'log', 'log', '', '2', '', '2', 0, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (110, '存储管理', '1', 'system:storage', 100, 'storage', 'storage', '', '2', '', '2', 0, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (201, '在线用户', '2', 'monitor:onlineUser', 200, 'online', 'onlineUser' '', '', '1',
        'monitor/onlineUser/index', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (202, '缓存监控', '2', 'monitor:cacheMonitor', 200, 'redis', 'cacheMonitor' '', '', '1',
        'monitor/cacheMonitor/index', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null);

-- 三级菜单(三级菜单ID从10000开始)
insert into t_sys_menu
values (10000, '操作日志', '2', 'monitor:log:operationLog', 109, 'log', 'operationLog', '', '',
        'system/operationLog/index', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (10001, '登录日志', '2', 'monitor:log:loginLog', 109, 'log', 'loginLog', '', '', 'system/loginLog/index', '2', 0,
        '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (10002, '存储配置', '2', 'system:storage:storageConfig', 110, 'config', 'storageConfig', '', '',
        'system/storageConfig/index', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null),
       (10003, '存储记录', '2', 'system:storage:storageRecord', 110, 'upload', 'storageRecord', '', '',
        'system/storageRecord/index', '2', 0, '1', '1', '', 100, 'admin', 0, '', sysdate(), null);
-- 按钮菜单(按钮菜单ID从20000开始)
-- 用户管理按钮
insert
into t_sys_menu
values (20000, '用户新增', '3', 'system:user:add', 101, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0, '',
        sysdate(), null),
       (20001, '用户修改', '3', 'system:user:update', 101, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20002, '用户删除', '3', 'system:user:remove', 101, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20003, '用户详情', '3', 'system:user:load', 101, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20004, '用户分页', '3', 'system:user:list', 101, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20005, '用户导出', '3', 'system:user:export', 101, '', '', '', '1', '', '2', 6, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20006, '用户导入', '3', 'system:user:import', 101, '', '', '', '1', '', '2', 7, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20007, '密码修改', '3', 'system:user:updatePasswd', 101, '', '', '', '1', '', '2', 8, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 角色管理按钮
insert into t_sys_menu
values (20050, '角色新增', '3', 'system:role:add', 102, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (20051, '角色修改', '3', 'system:role:update', 102, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20052, '角色删除', '3', 'system:role:remove', 102, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20053, '角色详情', '3', 'system:role:load', 102, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20054, '角色分页', '3', 'system:role:list', 102, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20055, '角色导出', '3', 'system:role:export', 102, '', '', '', '1', '', '2', 6, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 菜单管理按钮
insert into t_sys_menu
values (20100, '菜单新增', '3', 'system:menu:add', 103, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (200101, '菜单修改', '3', 'system:menu:update', 103, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (200102, '菜单删除', '3', 'system:menu:remove', 103, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20103, '菜单详情', '3', 'system:menu:load', 103, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20104, '菜单分页', '3', 'system:menu:list', 103, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 部门管理按钮
insert into t_sys_menu
values (20150, '部门新增', '3', 'system:dept:add', 104, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (20151, '部门修改', '3', 'system:dept:update', 104, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20152, '部门删除', '3', 'system:dept:remove', 104, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20153, '部门详情', '3', 'system:dept:load', 104, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20154, '部门分页', '3', 'system:dept:list', 104, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 岗位管理按钮
insert into t_sys_menu
values (20200, '岗位新增', '3', 'system:post:add', 105, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (20201, '岗位修改', '3', 'system:post:update', 105, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20202, '岗位删除', '3', 'system:post:remove', 105, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20203, '岗位详情', '3', 'system:post:load', 105, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20204, '岗位分页', '3', 'system:post:list', 105, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20205, '岗位导出', '3', 'system:post:export', 105, '', '', '', '1', '', '2', 6, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 字典管理按钮
insert into t_sys_menu
values (20250, '字典新增', '3', 'system:dict:add', 106, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin', 0,
        '', sysdate(), null),
       (20251, '字典修改', '3', 'system:dict:update', 106, '', '', '', '1', '', '2', 2, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20252, '字典删除', '3', 'system:dict:remove', 106, '', '', '', '1', '', '2', 3, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20253, '字典详情', '3', 'system:dict:load', 106, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20254, '字典分页', '3', 'system:dict:list', 106, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20255, '字典导出', '3', 'system:dict:export', 106, '', '', '', '1', '', '2', 6, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 系统参数按钮
insert into t_sys_menu
values (20300, '参数新增', '3', 'system:config:add', 107, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20301, '参数修改', '3', 'system:config:update', 107, '', '', '', '1', '', '2', 2, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20302, '参数删除', '3', 'system:config:remove', 107, '', '', '', '1', '', '2', 3, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20303, '参数详情', '3', 'system:config:load', 107, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20304, '参数分页', '3', 'system:config:list', 107, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20305, '参数导出', '3', 'system:config:export', 107, '', '', '', '1', '', '2', 6, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 通知公告按钮
insert into t_sys_menu
values (20350, '通知新增', '3', 'system:notice:add', 108, '', '', '', '1', '', '2', 1, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20351, '通知修改', '3', 'system:notice:update', 108, '', '', '', '1', '', '2', 2, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20352, '通知删除', '3', 'system:notice:remove', 108, '', '', '', '1', '', '2', 3, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null),
       (20353, '通知详情', '3', 'system:notice:load', 108, '', '', '', '1', '', '2', 4, '1', '1', '', 100, 'admin',
        0, '', sysdate(), null),
       (20354, '通知分页', '3', 'system:notice:list', 108, '', '', '', '1', '', '2', 5, '1', '1', '', 100,
        'admin', 0, '', sysdate(), null);
-- 操作日志按钮
insert into t_sys_menu
values (20400, '日志删除', '3', 'system:operationLog:remove', 10000, '', '', '', '1', '', '2', 1, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20401, '日志详情', '3', 'system:operationLog:load', 10000, '', '', '', '1', '', '2', 2, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20402, '日志分页', '3', 'system:operationLog:list', 10000, '', '', '', '1', '', '2', 3, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20403, '日志导出', '3', 'system:operationLog:export', 10000, '', '', '', '1', '', '2', 4, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null);
-- 登录日志按钮
insert into t_sys_menu
values (20450, '日志删除', '3', 'system:loginLog:remove', 10001, '', '', '', '1', '', '2', 1, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20451, '日志详情', '3', 'system:loginLog:load', 10001, '', '', '', '1', '', '2', 2, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20452, '日志分页', '3', 'system:loginLog:list', 10001, '', '', '', '1', '', '2', 3, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20453, '日志导出', '3', 'system:loginLog:export', 10001, '', '', '', '1', '', '2', 4, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null);
-- 在线用户按钮
insert into t_sys_menu
values (20500, '在线分页', '3', 'system:onlineUser:list', 201, '', '', '', '1', '', '2', 1, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20501, '批量强退', '3', 'system:onlineUser:logoutBatch', 201, '', '', '', '1', '', '2', 2, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20502, '单条强退', '3', 'system:onlineUser:logout', 201, '', '', '', '1', '', '2', 3, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null);
-- 存储配置按钮
insert into t_sys_menu
values (20550, '存储新增', '3', 'system:storageConfig:add', 10002, '', '', '', '1', '', '2', 1, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20551, '存储修改', '3', 'system:storageConfig:update', 10002, '', '', '', '1', '', '2', 2, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20552, '存储删除', '3', 'system:storageConfig:remove', 10002, '', '', '', '1', '', '2', 3, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20553, '存储详情', '3', 'system:storageConfig:load', 10002, '', '', '', '1', '', '2', 4, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20554, '存储分页', '3', 'system:storageConfig:list', 10002, '', '', '', '1', '', '2', 5, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null);
-- 存储记录按钮
insert into t_sys_menu
values (20600, '存储新增', '3', 'system:storageRecord:add', 10003, '', '', '', '1', '', '2', 1, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20601, '存储修改', '3', 'system:storageRecord:update', 10003, '', '', '', '1', '', '2', 2, '1', '1',
        '', 100, 'admin', 0, '', sysdate(), null),
       (20602, '存储删除', '3', 'system:storageRecord:remove', 10003, '', '', '', '1', '', '2', 3, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20603, '存储详情', '3', 'system:storageRecord:load', 10003, '', '', '', '1', '', '2', 4, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null),
       (20604, '存储分页', '3', 'system:storageRecord:list', 10003, '', '', '', '1', '', '2', 5, '1',
        '1', '', 100, 'admin', 0, '', sysdate(), null);


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
-- 代码生成数据库表信息
-- ----------------------------
drop table if exists t_tools_generator_table;
create table t_tools_generator_table
(
    id              bigint      not null auto_increment comment '主键ID',
    table_name      varchar(64) not null comment '表名称',
    table_comment   varchar(128) default '' comment '表描述',
    feature_name    varchar(128) default '' comment '功能名称',
    module_name     varchar(32) not null comment '模块名称',
    biz_name        varchar(32) not null comment '业务名称',
    entity_name     varchar(32)  default '' comment '实体类名称',
    entity_dto      char(1)      default '1' comment '是否需要实体类DTO(1-是 2-否)',
    entity_page_dto char(1)      default '1' comment '是否需要实体类分页DTO(1-是 2-否)',
    entity_vo       char(1)      default '1' comment '是否需要实体类VO(1-是 2-否)',
    package_name    varchar(128) comment '包路径',
    generator_type  char(1)      default '1' comment '生成类型(1-zip包 2-指定路径)',
    generator_path  varchar(128) default '' comment '生成路径(不填默认为项目路径)',
    create_id       bigint       default 0 comment '创建用户ID',
    create_by       varchar(64)  default '' comment '创建用户',
    update_id       bigint       default 0 comment '修改用户ID',
    update_by       varchar(64)  default '' comment '修改用户',
    create_time     datetime comment '创建时间',
    update_time     datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '代码生成数据库表信息';

-- ----------------------------
-- 代码生成数据库列信息
-- ----------------------------
drop table if exists t_tools_generator_column;
create table t_tools_generator_column
(
    id             bigint      not null auto_increment comment '主键ID',
    table_id       bigint      not null comment '生成表ID',
    column_name    varchar(64) not null comment '生成表列名称',
    column_comment varchar(128) default '' comment '生成表列描述',
    column_type    varchar(64) not null comment '生成表列类型',
    java_type      varchar(64) not null comment '生成表列Java类型',
    java_field     varchar(64) not null comment '生成表列Java字段名',
    is_pk          char(1)     not null comment '是否主键(1-是 2-否)',
    is_increment   char(1)     not null comment '是否自增(1-是 2-否)',
    is_required    char(1)     not null comment '是否必填(1-是 2-否)',
    is_edit        char(1)     not null comment '编辑时字段(1-是 2-否)',
    is_table       char(1)     not null comment '表格时字段(1-是 2-否)',
    is_query       char(1)     not null comment '查询时字段(1-是 2-否)',
    query_type     varchar(64)  default 'EQUAL' comment '查询类型(字典类型[tools_generator_query_type])',
    component_type varchar(64)  default 'INPUT' comment '组件类型(字典类型[tools_generator_component_type])',
    type_key       varchar(64)  default '' comment '字典类型键名',
    create_id      bigint       default 0 comment '创建用户ID',
    create_by      varchar(64)  default '' comment '创建用户',
    update_id      bigint       default 0 comment '修改用户ID',
    update_by      varchar(64)  default '' comment '修改用户',
    create_time    datetime comment '创建时间',
    update_time    datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '代码生成数据库列信息';

-- ----------------------------
-- 存储记录表
-- ----------------------------
drop table if exists t_sys_storage_record;
create table t_sys_storage_record
(
    id                 bigint       not null auto_increment comment '主键ID',
    url                varchar(512) not null comment '文件访问地址',
    size               bigint       default 0 comment '文件大小',
    file_name          varchar(256) default '' comment '文件名称',
    original_file_name varchar(256) default '' comment '原始文件名称',
    base_path          varchar(256) default '' comment '基础存储路径',
    path               varchar(256) default '' comment '存储路径',
    ext                varchar(32)  default '' comment '文件扩展名',
    content_type       varchar(128) default '' comment 'MIME类型',
    platform           varchar(32)  default '' comment '存储平台名称',
    th_ur              varchar(512) default '' comment '缩略图访问路径',
    th_file_name       varchar(256) default '' comment '缩略图文件名称',
    th_size            bigint       default 0 comment '缩略图大小',
    th_content_type    varchar(128) default '' comment '缩略图MIME类型',
    object_id          varchar(32)  default '' comment '文件所属对象ID',
    object_type        varchar(32)  default '' comment '文件所属对象类型',
    attr               varchar(512) default '' comment '附加属性',
    file_acl           varchar(32)  default '' comment 'fileAcl',
    th_file_acl        varchar(32)  default '' comment 'thFileAcl',
    create_id          bigint       default 0 comment '创建用户ID',
    create_by          varchar(64)  default '' comment '创建用户',
    create_time        datetime comment '创建时间',
    primary key (id)
) auto_increment = 100 comment '存储记录表';

-- ----------------------------
-- 存储配置表
-- ----------------------------
drop table if exists t_sys_storage_config;
create table t_sys_storage_config
(
    id          bigint      not null auto_increment comment '主键ID',
    type        varchar(32) not null comment '存储类型',
    platform    varchar(64) not null comment '存储平台名称',
    domain      varchar(256) default '' comment '存储域名',
    base_path   varchar(128) default '' comment '基础路径',
    access_key  varchar(128) default '' comment '访问秘钥(accessKey)',
    secret_key  varchar(128) default '' comment '私密秘钥(secretKey)',
    bucket_name varchar(128) default '' comment '桶名称(bucket)',
    end_point   varchar(128) default '' comment '端点(endPoint)',
    region      varchar(128) default '' comment '域名称(region)',
    is_default  varchar(4)   default '2' comment '是否默认(1-是 2-否)',
    status      varchar(4)   default '1' comment '状态(1-正常 2-停用)',
    remark      varchar(128) default '' comment '备注',
    create_id   bigint       default 0 comment '创建用户ID',
    create_by   varchar(64)  default '' comment '创建用户',
    update_id   bigint       default 0 comment '修改用户ID',
    update_by   varchar(64)  default '' comment '修改用户',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    primary key (id)
) auto_increment = 100 comment '存储配置表';
insert into t_sys_storage_config(id, type, platform, domain, base_path, access_key, secret_key,
                                 bucket_name,
                                 end_point, region, is_default, status, remark, create_id, create_by, create_time)
values (100, 'LOCAL', 'LOCAL', '', 'youlan/upload/', '', '', '', '', '', '1', '1', '本地默认存储', 100, 'admin',
        sysdate());

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
insert into t_sys_dict_type(type_name, type_key, remark, create_id, create_by, create_time)
values ('机构类型', 'sys_org_type', '机构类型列表', 100, 'admin', sysdate()),
       ('状态', 'db_status', '状态列表', 100, 'admin', sysdate()),
       ('查询类型', 'tools_generator_query_type', '查询类型列表', 100, 'admin', sysdate()),
       ('组件类型', 'tools_generator_component_type', '组件类型列表', 100, 'admin', sysdate()),
       ('Java类型', 'tools_generator_java_type', 'Java类型列表', 100, 'admin', sysdate()),
       ('操作日志类型', 'sys_operation_log_type', '操作日志类型列表', 100, 'admin', sysdate()),
       ('数据权限范围', 'sys_data_scope', '数据权限范围列表', 100, 'admin', sysdate()),
       ('用户性别', 'sys_user_sex', '用户性别列表', 100, 'admin', sysdate()),
       ('存储类型', 'sys_storage_type', '存储类型列表', 100, 'admin', sysdate());

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
       ('db_status', '正常', '1', '', '', '1', 100, 'admin', sysdate()),
       ('db_status', '停用', '2', '', '', '2', 100, 'admin', sysdate()),
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
       ('sys_operation_log_type', '文件上传', '8', '', '', '2', 100, 'admin', sysdate()),
       ('sys_operation_log_type', '其它', '99', '', '', '1', 100, 'admin', sysdate()),
       ('sys_data_scope', '全部数据权限', '1', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '自定义数据权限', '2', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '本机构数据权限', '3', '', '', '2', 100, 'admin', sysdate()),
       ('sys_data_scope', '本机构及以下数据权限', '4', '', '', '1', 100, 'admin', sysdate()),
       ('sys_data_scope', '仅本人数据权限', '5', '', '', '2', 100, 'admin', sysdate()),
       ('sys_user_sex', '男', '1', '', '', '1', 100, 'admin', sysdate()),
       ('sys_user_sex', '女', '2', '', '', '2', 100, 'admin', sysdate()),
       ('sys_user_sex', '未知', '3', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', '本地存储', 'LOCAL', '', '', '1', 100, 'admin', sysdate()),
       ('sys_storage_type', '华为OBS', 'HUAWEI_OBS', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', '阿里云OSS', 'ALIYUN_OSS', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', '七牛KODO', 'QINIU_KODO', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', '腾讯COS', 'TENCENT_COS', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', '百度BOS', 'BAIDU_BOS', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', 'MINIO', 'MINIO', '', '', '2', 100, 'admin', sysdate()),
       ('sys_storage_type', 'AmazonS3', 'AMAZON_S3', '', '', '2', 100, 'admin', sysdate());
