# CONFIGURATION_SPEC｜project-scaffold

## 配置边界

快照只保留公共 `application.yml`，不保留源项目的 `application-local.yml`、`application-dev.yml`、`application-prod.yml`。

原因：

- 环境配置容易包含真实数据库、账号、密码、密钥、OSS 参数。
- 基座应提供配置结构，不应携带任何客户或测试环境连接信息。

## 必填配置

新项目落地时至少需要配置：

| 配置 | 说明 |
| :--- | :--- |
| `spring.datasource.url` | 目标环境数据库地址 |
| `spring.datasource.username` | 数据库用户名 |
| `spring.datasource.password` | 数据库密码 |
| `auth.jwt.secret` | JWT 签名密钥 |
| `auth.jwt.header-name` | 前后端约定的访问令牌请求头 |
| `auth.jwt.access-token-expire-seconds` | token 有效期 |

快照副本中的公共配置使用环境变量：

| 环境变量 | 绑定配置 |
| :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `spring.profiles.active` |
| `AUTH_JWT_SECRET` | `auth.jwt.secret` |
| `AUTH_JWT_ACCESS_TOKEN_EXPIRE_SECONDS` | `auth.jwt.access-token-expire-seconds` |
| `AUTH_JWT_HEADER_NAME` | `auth.jwt.header-name` |

## 租户配置

业务或环境强相关配置放入 `tenant_config`，包括：

- 租户名称、域名、前端路由、状态。
- 飞书告警 webhook。
- 飞书/钉钉/企业微信应用配置。
- OSS endpoint、region、bucket、STS role、policy。

## 环境策略

推荐每个环境维护自己的数据库与 `tenant_config`，减少 `dev/prod` 配置差异：

```text
代码快照
  + 目标环境 application-*.yml
  + 目标环境 tenant_config
```

## 禁止项

- 不把真实密码、access key、secret、token 写入组件库。
- 不把客户环境连接信息写入 `BASE/` 或 `COMPONENTS/`。
- 不在 README 中复制真实连接串。
- 不通过默认弱密钥启动生产环境。
