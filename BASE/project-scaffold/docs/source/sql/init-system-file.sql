-- auto-generated definition
create table system_file
(
    id           bigint                             not null comment '主键 ID'
        primary key,
    main_type    varchar(32)                        null comment '文件主类型，对应 SystemFileMainTypeEnum',
    sub_type     varchar(64)                        null comment '文件次类型，须与 main_type 匹配，对应 SystemFileSubTypeEnum#code',
    master_id    bigint                             null comment '关联业务主对象ID（如材料ID）',
    name         varchar(512)                       null comment '文件名称',
    url          varchar(2048)                      null comment '图片/文件地址',
    file_key     varchar(1024)                      null comment '文件存储路径（Key），URL中域名后面的路径部分',
    endpoint     varchar(512)                       null comment 'OSS Endpoint（域名部分）',
    size         bigint                             null comment '文件大小（字节）',
    type         varchar(128)                       null comment '文件类型（如 MIME）',
    create_user  bigint   default -1                not null comment '创建人 ID',
    update_user  bigint   default -1                not null comment '修改人 ID',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '逻辑删除标记',
    deleted_time datetime                           null comment '删除时间'
)
    comment '系统文件表';

create index idx_system_file_main_sub_is_deleted
    on system_file (main_type, sub_type, is_deleted);

create index idx_system_file_master_id
    on system_file (master_id);

