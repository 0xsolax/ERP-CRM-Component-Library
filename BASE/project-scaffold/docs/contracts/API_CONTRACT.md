# API_CONTRACT｜project-scaffold

## 全局约定

- 全局路径前缀由 `server.servlet.context-path` 控制，源项目 README 以 `/api` 为默认前缀说明。
- 统一返回 `ResultInfo<T>`。
- 分页返回 `PageResponse<T>`。
- 写接口按源规范优先返回主键或布尔值，不默认返回完整 VO。

## 认证接口

| 能力 | 方法 | 路径 | 鉴权 |
| :--- | :--- | :--- | :--- |
| 账号密码登录 | POST | `/sysUser/loginByPassword` | 否 |
| 扫码登录 | POST | `/sysUser/loginByScan` | 否 |
| 退出登录 | POST | `/sysUser/logout` | 是 |
| 当前用户信息 | GET | `/sysUser/info` | 是 |

登录成功返回 `UserLoginVO.token`。受保护接口通过 `auth.jwt.header-name` 指定的请求头传入完整 JWT 字符串，不使用 `Bearer ` 前缀。

## 租户接口

| 能力 | 方法 | 路径 | 鉴权 |
| :--- | :--- | :--- | :--- |
| 按域名查询租户展示信息 | GET | `/qiaoMoYun/tenant/getTenantId` | 否 |
| 中台同步租户配置与文件 | POST | `/external/tenant/sync` | 源项目匿名，生产需加固 |

## 基础数据接口

| 能力 | 方法 | 路径 | 鉴权 |
| :--- | :--- | :--- | :--- |
| 单条新增或更新 | POST | `/baseData/saveOrUpdate` | 是 |
| 删除 | POST | `/baseData/delete` | 是 |
| 列表 | POST | `/baseData/list` | 是 |
| 按节点 key 查询 | POST | `/baseData/listByNodeKey` | 是 |
| 树节点列表 | POST | `/baseData/treeNodeList` | 是 |

## 文件接口

| 能力 | 方法 | 路径 | 鉴权 |
| :--- | :--- | :--- | :--- |
| OSS STS 临时凭证 | GET | `/oss/getOssToken` | 是 |

## OpenAPI

Swagger/OpenAPI 相关路径在源项目中匿名放行：

- `/v3/api-docs/**`
- `/swagger-ui/**`
- `/swagger-ui.html`

生产环境是否公开这些路径由目标项目安全策略决定。
