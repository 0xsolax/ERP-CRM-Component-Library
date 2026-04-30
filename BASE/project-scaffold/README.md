# project-scaffold 基座快照

## 定位

`project-scaffold` 是 ERP/CRM 后端项目的 Spring Boot 多模块基座快照。它用于新项目初始化时提供统一的后端骨架、认证登录、租户配置、基础数据、文件 OSS、异常处理、接口返回、审计字段和数据库初始化样例。

本目录是可复用快照，不是 `RAW/PROJECTs/project-scaffold` 的 Git 子仓库。

## 快照结构

```text
BASE/project-scaffold/
  README.md
  base.yaml
  SOURCE_MAP.md
  code/
  docs/
    source/
    spec/
    contracts/
    acceptance/
```

## 代码范围

`code/` 保留源项目的核心结构：

- `project-api`：DTO、请求对象、统一响应、分页响应。
- `project-application`：启动类、配置、拦截器、全局异常、OpenAPI、日志、飞书告警。
- `project-common`：错误码、枚举、上下文、异常模型、工具类。
- `project-core`：`auth`、`user`、`tenant`、`base`、`file`、`external` 业务域。
- `project-infrastructure`：MyBatis-Plus 基础设施、`BaseDO`、自动填充。
- `project-test`：测试模块占位。
- `docs/sql`：认证、租户、基础数据、系统文件初始化 SQL。
- `docs/sh`、`docs/skills`：项目改名脚本与原编码规范。

## 已清理内容

快照已排除：

- 嵌套 `.git/`
- `.DS_Store`
- `target/`、`node_modules/`、`build/`、`dist/`
- IDE 本地配置
- `application-local.yml`、`application-dev.yml`、`application-prod.yml`

快照副本还移除了 `AuthTokenInterceptor` 中的硬编码调试超管 token 分支。源项目仍保留该逻辑，详见 `SOURCE_MAP.md` 的安全清理记录。

公共 `application.yml` 已改为通过环境变量读取 `SPRING_PROFILES_ACTIVE`、`AUTH_JWT_SECRET`、`AUTH_JWT_ACCESS_TOKEN_EXPIRE_SECONDS` 与 `AUTH_JWT_HEADER_NAME`，不再默认激活被排除的本地环境配置。

## 使用顺序

1. 阅读 [基座规范](docs/spec/BASE_SPEC.md)。
2. 对照 [来源映射](SOURCE_MAP.md) 确认本快照来自哪些源文件。
3. 按目标项目命名修改 Maven `artifactId`、模块前缀、Java 包名与启动类。
4. 按 [配置规范](docs/spec/CONFIGURATION_SPEC.md) 创建目标环境配置。
5. 执行 `docs/source/sql/` 中的初始化 SQL，或按目标项目迁移系统重写为 Flyway/Liquibase。
6. 按 [验收清单](docs/acceptance/ACCEPTANCE.md) 完成启动、登录、鉴权、基础数据、OSS 和外部同步验证。

## 不直接复用的内容

- 不直接复用任何源项目真实环境配置。
- 不直接复用客户数据库连接、账号、密码、OSS 密钥、飞书机器人地址。
- 不把 `/external/**` 匿名同步接口直接暴露到公网；生产环境必须增加签名、白名单或网关鉴权。
- 不把 `qiaomoyun-token` 作为所有新项目固定请求头；新项目需按前后端接口规范统一确认。
