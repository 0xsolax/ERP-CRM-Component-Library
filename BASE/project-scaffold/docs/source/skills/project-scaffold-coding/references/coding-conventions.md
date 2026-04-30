# 编码示例模板

## 目录

- [保存与更新（saveOrUpdate / batchSaveOrUpdate）](#保存与更新saveorupdate--batchsaveorupdate)
- [Controller 模板](#controller-模板)
- [Service 接口模板](#service-接口模板)
- [Service 实现模板](#service-实现模板)
- [Manager 接口模板](#manager-接口模板)
- [Manager 实现模板](#manager-实现模板)
- [DAO 模板](#dao-模板)
- [DO 模板](#do-模板)
- [DTO 模板](#dto-模板)
- [VO 模板](#vo-模板)
- [枚举模板](#枚举模板)
- [错误码模板](#错误码模板)
- [常量类模板](#常量类模板)
- [工具类使用示例](#工具类使用示例)
- [禁止用法清单](#禁止用法清单)

## 保存与更新（saveOrUpdate / batchSaveOrUpdate）

与 [SKILL.md](../SKILL.md) 中「保存与更新（saveOrUpdate / batchSaveOrUpdate）」一节一致，写接口默认：

| 能力 | HTTP | `ResultInfo` 泛型 | Service 方法 |
|------|------|------------------|----------------|
| 单条 upsert | `POST /{资源}/saveOrUpdate` | `Long` | `Long saveOrUpdate(XxxSaveDTO dto)` |
| 批量 upsert | `POST /{资源}/batchSaveOrUpdate` | `Boolean` | `Boolean batchSaveOrUpdate(XxxBatchSaveDTO dto)` |

- 单条返回**主键 id**（新增为雪花 id，更新为原 id）；需要完整字段时客户端再调 `list` / `detail`。
- 批量在**全部成功**时返回 `true`；任一条失败抛业务异常，`batchSaveOrUpdate` 上 **`@Transactional(rollbackFor = Exception.class)`** 整批回滚。
- **Manager** 实现 `Long saveOrUpdate(XxxDO row)` 承载 insert/update 分支；Service 的 `saveOrUpdate(XxxSaveDTO)` 在校验与装配 DO 后调用 `xxxManager.saveOrUpdate(row)`，避免在 Service 重复写 `if (id == null) insert else update`。

参考实现：`com.qmy.project.core.base.controller.BaseDataController`、`BaseDataService` 与 `BaseDataManager`。

## Controller 模板

```java
package com.qmy.project.core.order.controller;

import com.qmy.project.api.dto.OrderCreateDTO;
import com.qmy.project.api.reponse.PageResponse;
import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.core.order.model.vo.OrderVO;
import com.qmy.project.core.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 你的名字
 * @description 订单管理
 * @date 2026/03/31 10:00
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/order")
@Tag(name = "订单管理")
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单。
     *
     * @param dto 创建请求体
     * @return 统一响应，data 为新建订单信息
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public ResultInfo<OrderVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        return ResultInfo.success(orderService.create(dto));
    }

    /**
     * 查询订单详情。
     *
     * @param id 订单主键 id
     * @return 统一响应，data 为订单信息
     */
    @GetMapping("/detail")
    @Operation(summary = "订单详情")
    public ResultInfo<OrderVO> detail(
            @RequestParam @Parameter(description = "订单ID") Long id) {
        return ResultInfo.success(orderService.detail(id));
    }

    /**
     * 分页查询订单列表。
     *
     * @param current 当前页，从 1 开始
     * @param size    每页条数
     * @return 统一响应，data 为分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "订单分页列表")
    public ResultInfo<PageResponse<OrderVO>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        return ResultInfo.success(orderService.page(current, size));
    }
}
```

## Service 接口模板

```java
package com.qmy.project.core.order.service;

import com.qmy.project.api.dto.OrderCreateDTO;
import com.qmy.project.api.reponse.PageResponse;
import com.qmy.project.core.order.model.vo.OrderVO;

/**
 * @author 你的名字
 * @description 订单用例服务
 * @date 2026/03/31 10:00
 */
public interface OrderService {

    /**
     * 创建订单。
     *
     * @param dto 创建参数
     * @return 订单视图
     */
    OrderVO create(OrderCreateDTO dto);

    /**
     * 按主键查询订单详情。
     *
     * @param id 订单主键 id
     * @return 订单视图
     */
    OrderVO detail(Long id);

    /**
     * 分页查询订单。
     *
     * @param current 当前页，从 1 开始
     * @param size    每页条数
     * @return 分页数据
     */
    PageResponse<OrderVO> page(Long current, Long size);
}
```

## Service 实现模板

```java
package com.qmy.project.core.order.service.impl;

import com.qmy.project.api.dto.OrderCreateDTO;
import com.qmy.project.api.reponse.PageResponse;
import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.order.manager.OrderManager;
import com.qmy.project.core.order.model.entity.OrderDO;
import com.qmy.project.core.order.model.vo.OrderVO;
import com.qmy.project.core.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 你的名字
 * @description
 * @date 2026/03/31 10:00
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderManager orderManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(OrderCreateDTO dto) {
        LoginUserInfo loginUser = LoginUserInfoContext.requireLoginUserInfo();
        OrderDO orderDO = BeanUtils.toBean(dto, OrderDO.class);
        Long orderId = orderManager.save(orderDO);
        return BeanUtils.toBean(orderDO, OrderVO.class, v -> v.setId(orderId));
    }

    @Override
    public OrderVO detail(Long id) {
        OrderDO orderDO = orderManager.getById(id);
        return BeanUtils.toBean(orderDO, OrderVO.class);
    }

    @Override
    public PageResponse<OrderVO> page(Long current, Long size) {
        return orderManager.page(current, size);
    }
}
```

## Manager 接口模板

```java
package com.qmy.project.core.order.manager;

import com.qmy.project.api.reponse.PageResponse;
import com.qmy.project.core.order.model.entity.OrderDO;
import com.qmy.project.core.order.model.vo.OrderVO;

/**
 * @author 你的名字
 * @description 订单领域能力
 * @date 2026/03/31 10:00
 */
public interface OrderManager {

    /**
     * 插入订单并返回主键 id（insert 后实体已回填 id）。
     *
     * @param orderDO 待持久化的订单实体
     * @return 新建订单主键 id
     */
    Long save(OrderDO orderDO);

    /**
     * 按主键查询订单；不存在时抛出业务异常。
     *
     * @param id 订单主键 id
     * @return 订单实体
     */
    OrderDO getById(Long id);

    /**
     * 分页查询订单，按创建时间倒序。
     *
     * @param current 当前页，从 1 开始
     * @param size    每页条数
     * @return 分页结果（含列表与总数）
     */
    PageResponse<OrderVO> page(Long current, Long size);
}
```

## Manager 实现模板

```java
package com.qmy.project.core.order.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.project.api.reponse.PageResponse;
import com.qmy.project.common.error.GlobalErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.order.dao.OrderDAO;
import com.qmy.project.core.order.manager.OrderManager;
import com.qmy.project.core.order.model.entity.OrderDO;
import com.qmy.project.core.order.model.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author 你的名字
 * @description
 * @date 2026/03/31 10:00
 */
@Component
@RequiredArgsConstructor
public class OrderManagerImpl implements OrderManager {

    private final OrderDAO orderDAO;

    @Override
    public Long save(OrderDO orderDO) {
        orderDAO.insert(orderDO);
        return orderDO.getId();
    }

    @Override
    public OrderDO getById(Long id) {
        OrderDO orderDO = orderDAO.selectById(id);
        if (orderDO == null) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.DATA_NOT_FOUND);
        }
        return orderDO;
    }

    @Override
    public PageResponse<OrderVO> page(Long current, Long size) {
        Page<OrderDO> page = orderDAO.selectPage(
                new Page<>(current, size),
                Wrappers.<OrderDO>lambdaQuery().orderByDesc(OrderDO::getCreateTime));
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(),
                BeanUtils.toBean(page.getRecords(), OrderVO.class));
    }
}
```

## DAO 模板

```java
package com.qmy.project.core.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.project.core.order.model.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 你的名字
 * @description
 * @date 2026/03/31 10:00
 */
@Mapper
public interface OrderDAO extends BaseMapper<OrderDO> {
}
```

## DO 模板

```java
package com.qmy.project.core.order.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 你的名字
 * @description 订单表
 * @date 2026/03/31 10:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order")
public class OrderDO extends BaseDO {

    @TableField("order_no")
    private String orderNo;

    @TableField("amount")
    private Long amount;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;
}
```

## DTO 模板

DTO 放在 `project-api` 模块。

```java
package com.qmy.project.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author 你的名字
 * @description 创建订单参数
 * @date 2026/03/31 10:00
 */
@Data
@Schema(description = "创建订单参数")
public class OrderCreateDTO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @Schema(description = "金额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    private Long amount;

    @Schema(description = "备注")
    private String remark;
}
```

## VO 模板

VO 放在 `project-core` 对应域的 `model.vo` 下。

```java
package com.qmy.project.core.order.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 你的名字
 * @description 订单返回
 * @date 2026/03/31 10:00
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "金额（分）")
    private Long amount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
```

## 枚举模板

```java
package com.qmy.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 你的名字
 * @description 订单状态
 * @date 2026/03/31 10:00
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {

    PENDING(0),
    PAID(1),
    CANCELLED(2);

    private final Integer code;

    private static final Map<Integer, OrderStatusEnum> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(OrderStatusEnum::getCode, Function.identity()));

    public static OrderStatusEnum fromCode(Integer code) {
        return CODE_MAP.get(code);
    }
}
```

## 错误码模板

```java
package com.qmy.project.common.error;

/**
 * @author 你的名字
 * @description 订单相关错误码
 * @date 2026/03/31 10:00
 */
public final class OrderErrorCodeConstants {

    /** 订单不存在 */
    public static final ErrorCode ORDER_NOT_FOUND = new ErrorCode(40430, "订单不存在");
    /** 订单状态不允许此操作 */
    public static final ErrorCode ORDER_STATUS_INVALID = new ErrorCode(40031, "订单状态不允许此操作");
    /** 订单编号重复 */
    public static final ErrorCode ORDER_NO_DUPLICATE = new ErrorCode(40932, "订单编号重复");

    private OrderErrorCodeConstants() {
    }
}
```

错误码编码约定（参考现有）：
- `400xx`：参数/业务校验错误
- `401xx`：认证相关
- `403xx`：权限相关
- `404xx`：资源不存在
- `409xx`：冲突（唯一键等）
- `500xx`：系统/外部调用错误

新增域建议使用新的十位段避免冲突。

## 常量类模板

```java
package com.qmy.project.common.constants;

/**
 * @author 你的名字
 * @description
 * @date 2026/03/31 10:00
 */
public final class OrderConstants {

    public static final String ORDER_PREFIX = "ORD";
    public static final int MAX_REMARK_LENGTH = 500;

    private OrderConstants() {
    }
}
```

## 工具类使用示例

### BeanUtils — 对象转换

```java
// 单对象转换
OrderVO vo = BeanUtils.toBean(orderDO, OrderVO.class);

// 带后处理的转换
OrderVO vo = BeanUtils.toBean(orderDO, OrderVO.class, v -> {
    v.setStatusName(OrderStatusEnum.fromCode(orderDO.getStatus()).name());
});

// 列表转换
List<OrderVO> voList = BeanUtils.toBean(orderDOList, OrderVO.class);

// 属性复制
BeanUtils.copyProperties(dto, existingDO);

// 分组
Map<Integer, List<OrderDO>> grouped = BeanUtils.groupToMap(orders, OrderDO::getStatus);

// 转 Map
Map<Long, OrderDO> map = BeanUtils.toMap(orders, OrderDO::getId);

// 提取字段列表
List<Long> ids = BeanUtils.toList(orders, OrderDO::getId);
Set<Long> idSet = BeanUtils.toSet(orders, OrderDO::getId);
```

### ValidityUtils — 判空校验

```java
// 字符串判空（null、空串、全空白、"null" 均返回 true）
if (ValidityUtils.isBlank(str)) { ... }
if (ValidityUtils.isNotBlank(str)) { ... }

// 对象判空
if (ValidityUtils.isNull(obj)) { ... }
if (ValidityUtils.nonNull(obj)) { ... }

// 集合判空
if (ValidityUtils.isEmpty(list)) { ... }
if (ValidityUtils.isNotEmpty(list)) { ... }

// 相等比较
if (ValidityUtils.equals(a, b)) { ... }

// 集合包含
if (ValidityUtils.contains(collection, target)) { ... }

// 布尔判断
if (ValidityUtils.isTrue(flag)) { ... }
```

### LoginUserInfoContext — 获取当前登录用户

```java
// 强制要求登录（未登录抛 UNAUTHORIZED）
LoginUserInfo loginUser = LoginUserInfoContext.requireLoginUserInfo();
Long userId = loginUser.getUserId();

// 可选获取
Optional<Long> userIdOpt = LoginUserInfoContext.currentUserIdOptional();

// 带默认值（用于无登录态场景如定时任务；与 MybatisMetaObjectHandler 审计字段默认一致）
Long userId = LoginUserInfoContext.currentUserIdOrDefault(-1L);
```

### ServiceExceptionUtil — 抛业务异常

```java
// 使用预定义错误码
throw ServiceExceptionUtil.exception(OrderErrorCodeConstants.ORDER_NOT_FOUND);

// 自定义消息（不推荐，尽量用预定义错误码）
throw ServiceExceptionUtil.exception(400, "自定义错误消息");
```

### MyBatis-Plus 查询

```java
// lambdaQuery 条件查询
OrderDO order = orderDAO.selectOne(
        Wrappers.<OrderDO>lambdaQuery()
                .eq(OrderDO::getOrderNo, orderNo)
                .last("limit 1"));

// lambdaUpdate 条件更新
orderDAO.update(null,
        Wrappers.<OrderDO>lambdaUpdate()
                .eq(OrderDO::getId, id)
                .set(OrderDO::getStatus, OrderStatusEnum.CANCELLED.getCode()));

// 分页查询
Page<OrderDO> page = orderDAO.selectPage(
        new Page<>(current, size),
        Wrappers.<OrderDO>lambdaQuery()
                .eq(ValidityUtils.nonNull(status), OrderDO::getStatus, status)
                .orderByDesc(OrderDO::getCreateTime));
```

## 禁止用法清单

| 禁止 | 替代 |
|------|------|
| `org.springframework.beans.BeanUtils.copyProperties(...)` | `com.qmy.project.common.utils.BeanUtils.copyProperties(...)` |
| `cn.hutool.core.bean.BeanUtil.toBean(...)` | `com.qmy.project.common.utils.BeanUtils.toBean(...)` |
| `org.apache.commons.lang3.StringUtils.isBlank(...)` | `com.qmy.project.common.utils.ValidityUtils.isBlank(...)` |
| `cn.hutool.core.util.StrUtil.isBlank(...)` | `com.qmy.project.common.utils.ValidityUtils.isBlank(...)` |
| `cn.hutool.core.util.ObjectUtil.isNull(...)` | `com.qmy.project.common.utils.ValidityUtils.isNull(...)` |
| `org.springframework.util.CollectionUtils.isEmpty(...)` | `com.qmy.project.common.utils.ValidityUtils.isEmpty(...)` |
| `new ServiceException(code, msg)` | `ServiceExceptionUtil.exception(ErrorCode)` |
| `@Autowired` 字段注入 | `@RequiredArgsConstructor` + `private final` |
| `@Service` 在 Manager 实现上 | `@Component` |
| DAO 写自定义方法 | Manager 中用 `lambdaQuery` |
| XML SQL | `lambdaQuery` / `lambdaUpdate` |
| 自增 ID | `IdType.ASSIGN_ID`（雪花算法） |
| 在 Controller 中 try-catch | 交给 `GlobalExceptionHandler` |
| 直接从 Header 解析用户 | `LoginUserInfoContext.requireLoginUserInfo()` |
