---
name: project-scaffold-coding
description: >-
  Spring Boot 多模块脚手架（com.qmy.project）的编码规范与架构指南。用于在此项目中编写新功能代码时，
  确保分层架构、命名风格、工具类使用、异常处理、数据库设计等与现有代码保持一致。
  当需要在 project-scaffold 中新增业务域、编写 Controller/Service/Manager/DAO、
  定义 DTO/VO/DO、创建错误码或枚举、处理中台对接等场景时触发此 skill。
---

# 编码规范

## 项目背景

基于 JDK 21 + Spring Boot **独立部署的租户系统**，`core.external` 包接受中台项目的管理与数据同步。

## 模块职责速查

| 模块 | 职责 | 放什么 |
|------|------|--------|
| `project-application` | 启动、配置绑定、拦截器、全局异常处理、切面 | `@Configuration`、`HandlerInterceptor`、`@RestControllerAdvice`、`@Aspect` |
| `project-common` | 跨模块共享 | 枚举、常量、工具类、`ThreadLocal` 上下文、异常模型、错误码 |
| `project-api` | 对外协议契约 | DTO、`ResultInfo`、`PageResponse`、`Request`；**不含 Controller** |
| `project-core` | 核心业务 | 按业务域聚合（auth / user / tenant / file 等），每个域内 `controller → service → manager → dao` |
| `project-infrastructure` | 基础设施 | MyBatis-Plus 配置、`BaseDO`、`MetaObjectHandler` |
| `project-test` | 测试 | 测试代码 |

## 分层约定

`controller → service → manager → dao`

- **controller**：HTTP 接口层，只做收参、出参、协议适配，返回 `ResultInfo<T>`
- **service**：用例编排层，组装多个 manager，负责事务边界（`@Transactional(rollbackFor = Exception.class)`）
- **manager**：领域能力层，封装单表/单聚合的业务规则与查询
- **dao**：数据访问层，继承 `BaseMapper<DO>`，**极薄**，不写自定义方法
- **external.controller / external.service**：对外部系统（中台）的入口，service 编排各域已有 manager

新增业务域时在 `project-core` 下创建对应包，按分层组织。详见 [references/architecture.md](references/architecture.md)。

## 编码风格（阿里规约基础）

### 命名

- 包名：全小写，`com.qmy.project.core.{业务域}.{分层}`
- 类名：`UpperCamelCase`
  - Controller：`XxxController`
  - Service 接口：`XxxService`，实现：`XxxServiceImpl`
  - Manager 接口：`XxxManager`，实现：`XxxManagerImpl`
  - DAO：`XxxDAO`（全大写）
  - DO：`XxxDO`（继承 `BaseDO`），VO：`XxxVO`，DTO：`XxxDTO`
  - 枚举：`XxxEnum`，常量：`XxxConstants`
  - 错误码：`XxxErrorCodeConstants`
- 方法/变量：`lowerCamelCase`
- 常量：`UPPER_SNAKE_CASE`
- 枚举常量：`UPPER_SNAKE_CASE`

### 注解组合

```java
// Controller
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/xxx")
@Tag(name = "中文模块名")

// Service 实现
@Service
@RequiredArgsConstructor

// Manager 实现（注意用 @Component 不用 @Service）
@Component
@RequiredArgsConstructor

// DAO
@Mapper

// DO
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("表名")

// DTO
@Data
@Schema(description = "中文描述")

// VO
@Data
// 按需加 @Schema
```

### 类头注释

```java
/**
 * @author 作者名
 * @description 简要描述（可选）
 * @date yyyy/MM/dd HH:mm
 */
```

### 接口与对外方法 JavaDoc

- **Service / Manager 等接口**：每个 public 方法写 JavaDoc，**必须包含** `@param`（每个参数一行）与 `@return`（有返回值时）。
- **Controller** 的 public 接口方法同样写完整 JavaDoc（可与 `@Operation` 并存；`@Operation` 面向 OpenAPI，JavaDoc 面向源码与生成文档）。
- **实现类**（`*ServiceImpl`、`*ManagerImpl`）：**不在** `@Override` 方法上重复写 JavaDoc；说明以接口为准，IDE/文档生成会从接口继承。若实现上有额外约定（如副作用、线程安全），再单独在实现类方法上补充。

### 依赖注入

统一使用 **`@RequiredArgsConstructor` + `private final`** 构造器注入，**不使用** `@Autowired`。

### 工具类使用 — 必须使用项目自有工具类

| 需求 | 使用 | 禁止使用 |
|------|------|----------|
| 对象/集合转换 | `com.qmy.project.common.utils.BeanUtils` | `org.springframework.beans.BeanUtils`、Hutool `BeanUtil` 直接调用 |
| 判空/字符串 | `com.qmy.project.common.utils.ValidityUtils` | `StringUtils`、`ObjectUtils` 直接调用 |
| 获取当前登录用户 | `LoginUserInfoContext.requireLoginUserInfo()` | 自行从 Header 解析 |
| 抛业务异常 | `ServiceExceptionUtil.exception(ErrorCode)` | `new ServiceException(...)` 直接构造 |

`BeanUtils` 主要方法：`toBean`（单对象/列表）、`copyProperties`、`groupToMap`、`toMap`、`toList`、`toSet`。
`ValidityUtils` 主要方法：`isBlank`/`isNotBlank`（额外将 `"null"` 视为空）、`isNull`/`nonNull`、`isEmpty`/`isNotEmpty`、`equals`、`contains`。

### 异常与错误码

- 错误码定义在 `project-common` 的 `error` 包，按业务域拆分为 `*ErrorCodeConstants`
- 每个错误码为 `public static final ErrorCode`
- 抛异常：`throw ServiceExceptionUtil.exception(XxxErrorCodeConstants.CODE_NAME);`
- 全局异常处理器在 `project-application` 的 `GlobalExceptionHandler` 中统一捕获
- Controller **不**做 try-catch，异常交给全局处理

### 返回值

- 统一 `ResultInfo<T>` 包装：`ResultInfo.success(data)` / `ResultInfo.error(...)`
- 分页用 `PageResponse<T>`
- 查询、详情、列表等读接口：`ResultInfo<XxxVO>` 或 `ResultInfo<PageResponse<XxxVO>>`
- **保存与更新**类写接口的约定见下节「保存与更新（saveOrUpdate / batchSaveOrUpdate）」，**不要**默认返回完整 `VO`（除非业务明确要求）。

### 保存与更新（saveOrUpdate / batchSaveOrUpdate）

适用于「单表单条 upsert」与「同 DTO 结构批量 upsert」场景（如 `BaseDataController`）。

| 能力 | HTTP | `ResultInfo` 泛型 | 说明 |
|------|------|------------------|------|
| 单条保存或更新 | `POST /{资源}/saveOrUpdate` | `Long` | 返回记录主键：无 id 为新增（雪花 id），有 id 为更新后该 id。 |
| 批量保存或更新 | `POST /{资源}/batchSaveOrUpdate` | `Boolean` | 全部成功时 `data` 为 `true`；任一条校验或持久化失败抛业务异常，**Service 层事务回滚**。 |

约定：

- **路径与 Service 方法名**统一为 camelCase：`saveOrUpdate`、`batchSaveOrUpdate`，与 `@PostMapping` 一致。
- **单条**请求体仍用 `XxxSaveDTO`（含可选 `id`）；**批量**用 `XxxBatchSaveDTO` 包装多行，行结构与单条一致时用 `XxxBatchItemDTO` 或复用 `XxxSaveDTO` 列表字段。
- **Manager** 提供 `Long saveOrUpdate(XxxDO row)`：`id == null` 时 `insert`（回填主键），否则 `updateById`；**不要在 Service** 里手写「无 id 则 insert、有 id 则 update」的分支，统一委托给 Manager。Service 负责 DTO→DO、业务校验（如枚举、存在性）、事务边界；Manager 负责单表持久化的 upsert 分支。
- 需要完整字段或列表展示时，客户端再调 `list` / `detail`；单条写接口不返回 `XxxVO`，减轻响应体积并避免与「仅确认写成功」语义混淆。

### 事务

- 事务注解放在 **Service** 层的写方法上
- 格式：`@Transactional(rollbackFor = Exception.class)`
- Manager **不加** `@Transactional`（除个别含多步写操作的 Manager）

### MyBatis-Plus

- DO 继承 `BaseDO`，自动填充 `id` / `createUser` / `updateUser` / `createTime` / `updateTime` / `isDeleted`
- `id` 使用 `IdType.ASSIGN_ID`（雪花算法），**不使用自增**
- 数据库列名 `snake_case`，Java 字段 `camelCase`
- 查询用 `lambdaQuery` / `lambdaUpdate`，**尽量不写 XML SQL**
- DAO 只继承 `BaseMapper<DO>` + `@Mapper`，**不新增方法**
- 逻辑删除：`isDeleted` + `deletedTime` 联动，唯一键包含 `is_deleted + deleted_time`

### OpenAPI

- Controller 类：`@Tag(name = "模块中文名")`
- 方法：`@Operation(summary = "接口中文说明")`
- DTO 字段：`@Schema(description = "字段说明", requiredMode = Schema.RequiredMode.REQUIRED)`
- 必填字段同时加 `jakarta.validation`（`@NotBlank` / `@NotNull` / `@Valid`）

### 日志

- 使用 Lombok `@Slf4j`
- 只在需要时加（如外部调用、异常捕获），多数 Manager/Service 无 Logger
- 格式：`log.info/error("描述：{}", 参数)`

### 枚举

```java
@Getter
@RequiredArgsConstructor
public enum XxxEnum {
    VALUE_A(0),
    VALUE_B(1);

    private final Integer code;
}
```

需要根据 code 反查时添加 `fromCode` 静态方法。

### 常量类

```java
public final class XxxConstants {
    private XxxConstants() {}
    public static final String KEY = "value";
}
```

## 新增业务域清单

新增一个业务域（如 `order`）时需要创建的文件，详见 [references/architecture.md](references/architecture.md)。

## 中台对接（external 包）

面向中台系统的接口统一放在 `com.qmy.project.core.external`：

- Controller：`/external/xxx` 路径，已排除鉴权
- Service：编排各域 manager，**不**新建独立 DAO 或 Manager
- DTO：放在 `project-api` 的 `dto.midplatform` 下

## references 文件索引

- [references/architecture.md](references/architecture.md)：详细的模块分层、包结构、新增业务域步骤
- [references/coding-conventions.md](references/coding-conventions.md)：完整编码示例（Controller/Service/Manager/DO/DTO/VO/枚举/错误码全套代码模板）；**保存与更新**约定另见其中 [保存与更新（saveOrUpdate / batchSaveOrUpdate）](references/coding-conventions.md#保存与更新saveorupdate--batchsaveorupdate) 小节
