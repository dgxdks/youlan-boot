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
    is_insert      char(1)     not null comment '插入时字段(1-是 2-否)',
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