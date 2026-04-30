# API_CONTRACT｜auth-permission

## 认证接口

| 能力 | 方法 | 路径 | 入参 | 返回 |
| :--- | :--- | :--- | :--- | :--- |
| 账号密码登录 | POST | `/sysUser/loginByPassword` | `PasswordLoginDTO` | `UserLoginVO` |
| 扫码登录 | POST | `/sysUser/loginByScan` | `ScanLoginDTO` | `UserLoginVO` |
| 退出登录 | POST | `/sysUser/logout` | 无 | `Void` |
| 当前用户 | GET | `/sysUser/info` | 无 | `SysUserInfoVO` |

约定：

- token header 默认参考 `qiaomoyun-token`，新项目可重命名，但前后端必须统一。
- token 值为完整 JWT 字符串，不带 `Bearer ` 前缀。
- 当前用户返回必须包含用户基础信息和权限集合。
- JWT 签名密钥必须来自 `auth.jwt.secret` 配置，不允许代码默认值。

## 扫码登录支持状态

| `ScanLoginDTO.type` | 状态 | 失败语义 |
| :--- | :--- | :--- |
| `feishu` | 可选通道，需平台配置 | 缺配置或换票失败时返回 `SCAN_LOGIN_NOT_READY` 或平台错误 |
| `dingtalk` | 可选通道，需平台配置 | 缺配置或换票失败时返回 `SCAN_LOGIN_NOT_READY` 或平台错误 |
| `wecom` | 待接入占位 | 当前固定返回 `SCAN_LOGIN_NOT_READY` |

生产接入前必须明确启用的扫码通道；未启用通道的前端入口应隐藏。

## 角色接口

| 能力 | 方法 | 路径 | 权限 |
| :--- | :--- | :--- | :--- |
| 角色分页 | POST | `/role/page` | `ROLE_PAGE` |

返回 `RolePageVO`，包含角色基础字段和该角色拥有的权限标识集合。

## 菜单接口

| 能力 | 方法 | 路径 | 权限 |
| :--- | :--- | :--- | :--- |
| 保存或更新菜单 | POST | `/menu/saveOrUpdate` | `MENU_SAVE_OR_UPDATE` |
| 菜单列表 | POST | `/menu/list` | `MENU_LIST` |
| 删除菜单 | POST | `/menu/delete` | `MENU_DELETE` |

## 前端 API 快照

| 来源 | 说明 |
| :--- | :--- |
| `frontend/qmy-admin/src/api/*/auth/user.ts` | 登录、扫码登录、退出、当前用户 |
| `frontend/qmy-admin/src/api/*/auth/role.ts` | 角色和权限相关 API |
| `frontend/qmy-admin/src/api/*/auth/menu.ts` | 菜单相关 API |
| `frontend/qmy-admin/src/api/*/auth/org.ts` | 组织/权限树参考 |

## 错误码

| 类型 | 说明 |
| :--- | :--- |
| `UNAUTHORIZED` | 缺 token 或未登录 |
| `TOKEN_INVALID` | JWT 或会话状态无效 |
| `TOKEN_EXPIRED` | token 已过期 |
| `ACCOUNT_NOT_EXISTS` | 账号不存在或第三方身份未绑定 |
| `SCAN_LOGIN_NOT_READY` | 扫码平台未配置或通道尚未接入 |
| `MENU_NOT_FOUND` | 菜单不存在 |
| `MENU_PERMISSION_DUPLICATE` | 权限标识重复 |
