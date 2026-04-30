# 项目架构详解

## 目录

- [模块依赖关系](#模块依赖关系)
- [project-core 包结构](#project-core-包结构)
- [分层职责详解](#分层职责详解)
- [新增业务域步骤](#新增业务域步骤)
- [external 包规范](#external-包规范)
- [数据库设计规范](#数据库设计规范)
- [配置管理](#配置管理)

## 模块依赖关系

```
project-application（启动模块）
├── project-core（核心业务）
│   ├── project-common（共享基础）
│   ├── project-api（协议契约）
│   │   └── project-common
│   └── project-infrastructure（基础设施）
│       └── project-common
└── project-test（测试）
    └── 依赖所有模块
```

## project-core 包结构

以 `auth` 域为例，每个业务域的标准包结构：

```
com.qmy.project.core.auth/
├── controller/
│   └── UserAuthController.java      # HTTP 接口
├── service/
│   ├── AuthService.java              # Service 接口
│   ├── impl/
│   │   └── AuthServiceImpl.java      # Service 实现
│   └── strategy/                     # 可选：设计模式子包
│       ├── ScanLoginStrategy.java
│       ├── ScanLoginStrategyContext.java
│       └── impl/
│           ├── FeishuScanLoginStrategy.java
│           ├── DingtalkScanLoginStrategy.java
│           └── WecomScanLoginStrategy.java
├── manager/
│   ├── AuthManager.java              # Manager 接口
│   └── impl/
│       └── AuthManagerImpl.java      # Manager 实现
├── dao/
│   └── AuthTokenDAO.java            # DAO（仅继承 BaseMapper）
├── model/
│   ├── entity/
│   │   └── AuthTokenDO.java         # DO（继承 BaseDO）
│   └── vo/
│       └── UserLoginVO.java         # VO
└── support/                          # 可选：辅助类
    └── JwtTokenService.java
```

**注意**：DTO 放在 `project-api` 模块的 `com.qmy.project.api.dto` 下，**不在** core 里。

## 分层职责详解

### Controller

- 只做**收参、出参、协议适配**
- 不含业务逻辑，不做 try-catch
- 返回 `ResultInfo<T>` 或 `ResultInfo<PageResponse<T>>`
- POST 用 `@RequestBody` + DTO，GET 用 `@RequestParam`

### Service

- **用例编排**：组合多个 Manager 的调用完成业务流程
- 负责**事务边界**：写操作加 `@Transactional(rollbackFor = Exception.class)`
- 可以调用同模块或跨模块的 Manager
- 不直接操作 DAO

### Manager

- **领域能力**：封装单表/单聚合的读写与业务规则
- 直接使用 DAO + MyBatis-Plus 的 `Wrappers`（`lambdaQuery`/`lambdaUpdate`）
- Bean 注解用 `@Component`（不用 `@Service`）
- 通常**不加** `@Transactional`

### DAO

- 继承 `BaseMapper<XxxDO>` + `@Mapper`
- **不新增任何方法**，所有查询在 Manager 中用 `lambdaQuery` 完成
- 不写 XML SQL

### model 子包

- `model.entity`：DO 类，继承 `BaseDO`，对应数据库表
- `model.vo`：VO 类，面向前端的返回值对象
- **DTO 放 project-api**

### support 子包（可选）

- 工具型协作类（如 `JwtTokenService`、`RequestClientInfoSupport`）
- 不是 Manager 但提供特定技术能力

## 新增业务域步骤

以新增 `order` 域为例：

### 1. 定义错误码（project-common）

在 `com.qmy.project.common.error` 下新增：

```
project-common/src/main/java/com/qmy/project/common/error/OrderErrorCodeConstants.java
```

### 2. 定义枚举（project-common，如需要）

```
project-common/src/main/java/com/qmy/project/common/enums/OrderStatusEnum.java
```

### 3. 定义 DTO（project-api）

```
project-api/src/main/java/com/qmy/project/api/dto/OrderCreateDTO.java
project-api/src/main/java/com/qmy/project/api/dto/OrderQueryDTO.java
```

### 4. 创建业务域包结构（project-core）

```
project-core/src/main/java/com/qmy/project/core/order/
├── controller/OrderController.java
├── service/
│   ├── OrderService.java
│   └── impl/OrderServiceImpl.java
├── manager/
│   ├── OrderManager.java
│   └── impl/OrderManagerImpl.java
├── dao/OrderDAO.java
└── model/
    ├── entity/OrderDO.java
    └── vo/OrderVO.java
```

### 5. 数据库表

在 `docs/sql/` 下新增对应初始化 SQL，遵循数据库设计规范。

## external 包规范

面向中台/外部系统的接口统一在 `com.qmy.project.core.external` 下：

```
core/external/
├── controller/
│   └── TenantMidPlatformController.java    # /external/xxx 路径
└── service/
    ├── TenantMidPlatformSyncService.java
    └── impl/
        └── TenantMidPlatformSyncServiceImpl.java
```

关键要点：
- Controller 路径以 `/external/` 开头，已在拦截器中排除鉴权
- Service 内编排调用各域已有 Manager，**不新建**独立 DAO 或 Manager
- DTO 放 `project-api` 的 `dto.midplatform` 包
- 生产环境建议加固安全校验

## 数据库设计规范

### 通用字段（BaseDO 自动管理）

| 字段 | 类型 | 说明                      |
|------|------|-------------------------|
| `id` | `BIGINT` | 雪花算法，`IdType.ASSIGN_ID` |
| `create_user` | `BIGINT` | 创建人 ID（数据库默认-1L）        |
| `update_user` | `BIGINT` | 更新人 ID（数据库默认-1L）        |
| `create_time` | `DATETIME` | 创建时间（当前时间）              |
| `update_time` | `DATETIME` | 更新时间 （当行变化则更新）          |
| `is_deleted` | `TINYINT` | 逻辑删除标记，0 正常 / 1 删除      |
| `deleted_time` | `DATETIME` | 删除时间 （默认null,删除后则更新）    |

### 唯一键规范

统一按 **"业务字段 + is_deleted + deleted_time"** 设计，确保逻辑删除后可重新创建同业务值数据。

### ID 策略

- 系统生成的雪花算法 ID
- **不使用自增 ID**
- 若需初始化数据，用 Java 编写初始化代码（参考 `TenantConfigDataInitializer`）

### 字段命名

- 列名：`snake_case`
- Java 属性：`camelCase`
- 使用 `@TableField("列名")` 显式映射

## 配置管理

### application.yml

仅保留通用运行参数（端口、上下文路径、JWT 配置等）。

### tenant_config 表

业务相关的租户配置落库，key-value 设计：
- `config_code`：配置编码（如 `tenant.name`）
- `config_value`：配置值
- 一个数据库环境维护自己的一组租户配置
- 配置编码常量在 `TenantConfigCodeConstants`，枚举在 `TenantConfigCodeEnum`

### 新增租户配置项

1. 在 `TenantConfigCodeConstants` 增加常量
2. 在 `TenantConfigCodeEnum` 增加枚举值
3. 按需在 `TenantConfigDataInitializer` 中补充初始化逻辑
