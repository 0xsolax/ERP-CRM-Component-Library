# ERP+CRM 组件知识库

## 定位

本知识库用于把历史 ERP/CRM 项目中的业务能力整理成可追溯、可装配的全链路组件包。第一阶段以 Markdown 知识库为主，沉淀契约、模板、接入步骤和验收标准；不做自动代码生成器，不抽公共代码包。

## 入口

- [来源索引](SOURCE_INDEX.md)
- [维护日志](log.md)
- [业务组件卡模板](TEMPLATE_业务组件卡.md)
- [来源 manifest](source_manifest.tsv)

## 快照资产入口

- [BASE 基座快照层](../BASE/README.md)
- [BASE_project-scaffold 基座快照](../BASE/project-scaffold/README.md)
- [COMPONENTS 组件快照层](../COMPONENTS/README.md)
- [组件快照模板](../COMPONENTS/_TEMPLATE/README.md)
- [COMPONENT_auth-permission](../COMPONENTS/auth-permission/README.md)
- [COMPONENT_base-data](../COMPONENTS/base-data/README.md)
- [COMPONENT_file-oss](../COMPONENTS/file-oss/README.md)
- [COMPONENT_product-material](../COMPONENTS/product-material/README.md)
- [COMPONENT_customer-management](../COMPONENTS/customer-management/README.md)
- [COMPONENT_quote-management](../COMPONENTS/quote-management/README.md)

## 来源项目

- [PROJECT_qmy-admin](PROJECT_qmy-admin.md)
- [PROJECT_project-scaffold](PROJECT_project-scaffold.md)
- [PROJECT_zhongsheng-AI](PROJECT_zhongsheng-AI.md)
- [PROJECT_zhongsheng-backend](PROJECT_zhongsheng-backend.md)
- [PROJECT_zhongsheng调研资料](PROJECT_zhongsheng调研资料.md)

## 综合方向

- [SYNTH_工厂工具箱组件化方向](SYNTH_工厂工具箱组件化方向.md)

## 第一批组件卡

- [COMP_登录认证与权限](COMP_登录认证与权限.md)
- [COMP_基础数据](COMP_基础数据.md)
- [COMP_客户管理](COMP_客户管理.md)
- [COMP_报价管理](COMP_报价管理.md)
- [COMP_订单管理](COMP_订单管理.md)
- [COMP_产品物料基础数据](COMP_产品物料基础数据.md)
- [COMP_采购供应商](COMP_采购供应商.md)
- [COMP_仓储发货](COMP_仓储发货.md)
- [COMP_文件上传与OSS](COMP_文件上传与OSS.md)

## v1 装配方式

新项目先按组件卡人工装配：

1. 从客户需求映射到组件卡。
2. 对照组件卡确认前端、接口、数据表、权限、验收项。
3. 从来源项目定位可参考实现。
4. 按目标项目技术栈改造，而不是直接复制到生产。
