# ACCEPTANCE｜project-scaffold

## 结构验收

- [ ] `BASE/project-scaffold/code/` 存在 Spring Boot 多模块源码。
- [ ] `docs/source/README.source.md` 已保存原 README。
- [ ] `docs/source/sql/` 已保存认证、租户、基础数据、系统文件 SQL。
- [ ] `docs/source/skills/project-scaffold-coding/` 已保存原编码规范。
- [ ] `docs/spec/` 已包含基座规范和配置规范。
- [ ] `docs/contracts/` 已包含 API、数据、权限契约。

## 污染文件验收

- [ ] 不包含嵌套 `.git/`。
- [ ] 不包含 `.DS_Store`。
- [ ] 不包含 `target/`、`node_modules/`、`build/`、`dist/`。
- [ ] 不包含 `application-local.yml`、`application-dev.yml`、`application-prod.yml`。
- [ ] 不包含真实数据库密码、OSS access key、token、飞书 webhook。

## 运行验收

在目标项目实际复制后使用 JDK 21 执行：

```bash
mvn -q -DskipTests compile
mvn clean test
mvn -pl project-application spring-boot:run
```

说明：若使用 JDK 25 直接编译，当前 Lombok 版本可能触发 Javac 兼容性错误；基座以 JDK 21 为验收环境。

## 功能验收

- [ ] 执行初始化 SQL 后应用可启动。
- [ ] `POST /api/sysUser/loginByPassword` 可完成账号密码登录。
- [ ] 登录成功后返回 `UserLoginVO.token`。
- [ ] 受保护接口缺 token 返回未授权错误。
- [ ] `GET /api/sysUser/info` 可返回当前用户信息。
- [ ] `POST /api/baseData/list` 可读取基础数据。
- [ ] `POST /api/baseData/saveOrUpdate` 可保存基础数据。
- [ ] `GET /api/oss/getOssToken` 在租户 OSS 配置完整时可返回 STS 凭证。
- [ ] `/external/tenant/sync` 在生产前已加固，不裸露公网匿名访问。

## 安全验收

- [ ] 已替换 JWT secret。
- [ ] 已确认 token 请求头名称。
- [ ] 已创建目标环境自己的 datasource 配置。
- [ ] 已确认 `tenant_config` 不含空的必需配置。
- [ ] 已确认快照副本无硬编码调试超管 token。

## 交付结论

若以上结构验收和污染文件验收通过，本快照可作为后续业务组件抽取的基座；运行验收和功能验收在新项目落地时执行。
