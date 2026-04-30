-- 基础树节点（字段管理种子、分类等共用；业务侧通过 node_id 关联 base_data）
-- auto-generated definition
create table base_tree_node
(
    id             bigint                             not null comment '主键 ID'
        primary key,
    biz_type       varchar(32)                        not null comment '业务类型（大写英文）：FIELD_MGMT 字段管理；CATEGORY 分类；FABRIC 面料；PACKAGING 包材；UMBRELLA_FRAME 伞架',
    parent_id      bigint   default 0                 not null comment '父节点 id，根节点为 0',
    name           varchar(255)                       not null comment '节点名称',
    level          int                                not null comment '层级（根为 1）',
    sort_num       int      default 0                 not null comment '同级排序',
    node_key       varchar(64)                        null comment '节点唯一标识，用于前后端交互（如：FABRIC_TYPE、UMBRELLA_FRAME_FUNCTION）',
    data_bind_flag tinyint  default 0                 not null comment '是否允许绑定 base_data：0 不允许，1 允许',
    create_user    bigint   default -1                not null comment '创建人 ID',
    update_user    bigint   default -1                not null comment '修改人 ID',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted     tinyint  default 0                 not null comment '逻辑删除标记',
    deleted_time   datetime                           null comment '删除时间',
    constraint uk_base_tree_node_biz_parent_name_is_deleted
        unique (biz_type, parent_id, name, is_deleted, deleted_time),
    constraint uk_base_tree_node_node_key
        unique (node_key)
)
    comment '基础树节点表';

create index idx_base_tree_node_biz_type
    on base_tree_node (biz_type);

create index idx_base_tree_node_parent_id
    on base_tree_node (parent_id);

-- auto-generated definition
create table base_data
(
    id           bigint                             not null comment '主键 ID'
        primary key,
    node_id      bigint                             not null comment '基础树节点 id，关联 base_tree_node.id',
    value1       varchar(512)                       null comment '值1',
    value2       varchar(512)                       null comment '值2',
    value3       varchar(512)                       null comment '值3',
    value4       varchar(512)                       null comment '值4',
    remark       varchar(1024)                      null comment '备注',
    ext_json     text                               null comment '扩展 JSON',
    create_user  bigint   default -1                not null comment '创建人 ID',
    update_user  bigint   default -1                not null comment '修改人 ID',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '逻辑删除标记',
    deleted_time datetime                           null comment '删除时间'
)
    comment '通用基础数据表';

create index idx_base_data_node_id
    on base_data (node_id);

-- 种子数据：由应用启动类 BaseTreeNodeDataInitializer 按 BaseTreeNodeSeedEnum 幂等插入，
-- 主键 id 由 MyBatis-Plus ASSIGN_ID（雪花）在插入时生成，勿在 SQL 中写死 id。
