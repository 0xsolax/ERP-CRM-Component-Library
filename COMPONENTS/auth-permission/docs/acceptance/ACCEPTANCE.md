# ACCEPTANCE｜auth-permission

## 快照验收

- [ ] `backend/project-scaffold/` 包含认证、JWT、Token、用户相关代码。
- [ ] `backend/zhongsheng-rbac/` 包含角色、菜单、权限常量和方法级鉴权参考。
- [ ] `frontend/qmy-admin/` 包含登录、auth API、路由守卫、权限 store、角色菜单页面。
- [ ] `db/` 包含认证表和 RBAC 表。
- [ ] 不包含 `.git/`、`.DS_Store`、`target/`、`node_modules/`。
- [ ] 不包含真实数据库密码、JWT secret、调试 token。

## 接入验收

- [ ] 密码登录成功返回 token。
- [ ] 扫码登录未配置平台时有明确错误提示。
- [ ] 无 token 访问受保护接口返回未授权。
- [ ] 退出登录后旧 token 失效。
- [ ] `/sysUser/info` 返回当前用户和 `permission.curPermissions`。
- [ ] 普通用户只能看到有权限的路由。
- [ ] `v-permission` 能隐藏无权限按钮。
- [ ] 后端接口缺权限时拒绝访问，不只依赖前端隐藏。
- [ ] 角色能关联菜单权限。
- [ ] 菜单权限标识不能重复。

## 安全验收

- [ ] JWT secret 由环境配置或密钥系统注入。
- [ ] 不存在硬编码超级 token。
- [ ] 初始化管理员密码不写死在源码中。
- [ ] Swagger 是否匿名开放已按环境确认。
- [ ] 第三方扫码 app secret 不进入代码仓库。

## 当前快照未验证

- 未对抽取快照做编译，因为它是多来源拼装证据包，不是单一工程。
- 后续在真实项目接入时，需要分别执行后端构建、前端构建和浏览器登录验收。
