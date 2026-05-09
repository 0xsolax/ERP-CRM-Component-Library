package com.qmy.zhongsheng.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 启动时幂等补齐供应商询价台账表，避免新环境只重启服务但未手工执行 SQL 时列表报错。
 *
 * @author AI Coding
 */
@Slf4j
@Component
@Order(45)
@RequiredArgsConstructor
public class SupplierInquirySchemaInitializer implements ApplicationRunner {

    private static final String SUPPLIER_INQUIRY_DDL = """
            CREATE TABLE IF NOT EXISTS `supplier_inquiry`
            (
              `id` BIGINT NOT NULL COMMENT '主键 ID',
              `supplier_id` BIGINT NOT NULL COMMENT '供应商 ID',
              `supplier_code` VARCHAR(64) DEFAULT NULL COMMENT '供应商编号快照',
              `supplier_name` VARCHAR(255) NOT NULL COMMENT '供应商名称快照',
              `target_type` VARCHAR(32) NOT NULL COMMENT '询价对象类型，系统预置或用户自定义',
              `target_id` BIGINT DEFAULT NULL COMMENT '询价对象 ID，手工对象为空',
              `target_code` VARCHAR(64) DEFAULT NULL COMMENT '询价对象编号快照',
              `target_name` VARCHAR(255) NOT NULL COMMENT '询价对象名称快照',
              `specification` VARCHAR(255) DEFAULT NULL COMMENT '规格',
              `unit` VARCHAR(64) DEFAULT NULL COMMENT '单位',
              `price` DECIMAL(18,4) NOT NULL COMMENT '报价单价',
              `currency` VARCHAR(16) NOT NULL DEFAULT 'RMB' COMMENT '币种',
              `tax_rate` DECIMAL(8,4) DEFAULT 0.0000 COMMENT '税率',
              `moq` DECIMAL(18,4) DEFAULT NULL COMMENT '起订量',
              `delivery_days` VARCHAR(100) DEFAULT NULL COMMENT '交期天数或交期说明',
              `quote_date` DATE NOT NULL COMMENT '报价日期',
              `valid_until` DATE DEFAULT NULL COMMENT '有效期',
              `contact_name` VARCHAR(100) DEFAULT NULL COMMENT '联系人',
              `contact_phone` VARCHAR(64) DEFAULT NULL COMMENT '联系方式',
              `owner_id` BIGINT DEFAULT NULL COMMENT '录入人 ID',
              `owner_name` VARCHAR(100) DEFAULT NULL COMMENT '录入人名称快照',
              `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
              `modification_log_json` LONGTEXT DEFAULT NULL COMMENT '编辑日志 JSON',
              `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
              `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
              `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
              PRIMARY KEY (`id`),
              KEY `idx_supplier_inquiry_supplier` (`supplier_id`, `is_deleted`),
              KEY `idx_supplier_inquiry_target` (`target_type`, `target_id`, `is_deleted`),
              KEY `idx_supplier_inquiry_quote_date` (`quote_date`, `is_deleted`),
              KEY `idx_supplier_inquiry_currency` (`currency`, `is_deleted`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商询价台账'
            """;

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(SUPPLIER_INQUIRY_DDL);
            ensureColumn(connection, statement, "supplier_inquiry", "modification_log_json",
                    "ALTER TABLE `supplier_inquiry` ADD COLUMN `modification_log_json` LONGTEXT DEFAULT NULL COMMENT '编辑日志 JSON' AFTER `remark`");
            log.info("supplier_inquiry 表结构检查完成");
        } catch (SQLException ex) {
            throw new IllegalStateException("supplier_inquiry 表结构初始化失败", ex);
        }
    }

    private void ensureColumn(Connection connection, Statement statement, String tableName, String columnName, String ddl)
            throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
            if (!columns.next()) {
                statement.execute(ddl);
            }
        }
    }
}
