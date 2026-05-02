# 雨伞制造企业 ERP 系统 - 详细产品需求文档（按钮级）

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v3.0 |
| 编写日期 | 2026-03-23 |
| 项目名称 | 雨伞制造企业 ERP 系统 |
| 文档类型 | 详细功能需求文档（按钮级） |

---

## 目录

1. [基础信息管理](#1-基础信息管理)
   - [1.1 产品列表](#11-产品列表)
   - [1.2 产品表单（新增/编辑）](#12-产品表单新增编辑)
   - [1.3 工价管理](#13-工价管理)
2. [材料管理](#2-材料管理)
   - [2.1 伞架列表](#21-伞架列表)
   - [2.2 面料列表](#22-面料列表)
   - [2.3 其他材料](#23-其他材料)
   - [2.4 包材列表](#24-包材列表)
3. [销售管理](#3-销售管理)
   - [3.1 客户管理](#31-客户管理)
   - [3.2 报价管理](#32-报价管理)
   - [3.3 订单管理](#33-订单管理)
4. [采购管理](#4-采购管理)
   - [4.1 供应商管理](#41-供应商管理)
5. [生产管理](#5-生产管理)
   - [5.1 生产单列表](#51-生产单列表)
6. [财务管理](#6-财务管理)
   - [6.1 应收款管理](#61-应收款管理)
   - [6.2 应付款管理](#62-应付款管理)
7. [系统管理](#7-系统管理)
   - [7.1 数据字典](#71-数据字典)
8. [后端API接口规范](#8-后端api接口规范)

---

# 1. 基础信息管理

## 1.1 产品列表

**路由**: `/product`
**组件**: `ProductList.vue`

### 页面初始化

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 产品列表数据 | `productApi.page({current:1, size:10})` | 表格显示 |
| 产品分类选项 | `productCategoryApi.list()` | 产品类型下拉 |
| 伞架类型字典 | `dictApi.list('umbrella_type')` | 伞架类型下拉 |
| 伞架尺寸字典 | `dictApi.list('umbrella_size_cm')` | 伞架尺寸下拉 |
| 伞架功能字典 | `dictApi.list('umbrella_function')` | 伞架功能下拉 |
| 伞架材料字典 | `dictApi.list('umbrella_material')` | 伞架材料下拉 |
| 面料种类字典 | `dictApi.list('fabric_category')` | 面料种类下拉 |
| 印刷方式字典 | `dictApi.list('print_type')` | 印刷方式下拉 |
| 对齐方式字典 | `dictApi.list('print_alignment')` | 对齐方式下拉 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索产品编号、名称，回车触发查询 |
| 产品类型下拉 | `filters.category` | `productCategoryApi.list()` | 清除时触发查询 |
| 伞架类型下拉 | `filters.frameType` | `dictApi.list('umbrella_type')` | 清除时触发查询 |
| 伞架尺寸下拉 | `filters.frameSizeCm` | `dictApi.list('umbrella_size_cm')` | 清除时触发查询 |
| 伞架功能下拉 | `filters.frameFunction` | `dictApi.list('umbrella_function')` | 清除时触发查询 |
| 伞架材料下拉 | `filters.frameMaterial` | `dictApi.list('umbrella_material')` | 清除时触发查询 |
| 面料种类下拉 | `filters.fabricKind` | `dictApi.list('fabric_category')` | 清除时触发查询 |
| 印刷方式下拉 | `filters.printType` | `dictApi.list('print_type')` | 清除时触发查询 |
| 对齐方式下拉 | `filters.alignType` | `dictApi.list('print_alignment')` | 清除时触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，调用 `loadData()` |
| **重置按钮** | - | - | 清空所有筛选条件，重置页码为1，调用 `loadData()` |

### 产品列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 产品编号 | `row.code` | 后端 `product.code` | 原值显示 | - |
| 产品类型 | `row.category` | 后端 `product.category` | JSON数组转 `/` 分隔 | - |
| 图片 | `row.images` | 后端 `product.images` | 缩略图+预览 | 点击查看大图 |
| 货品描述 | `row.description` | 后端 `product.description` | 多行文本 | - |
| 毛重 | `row.grossWeight` | 后端 `product.grossWeight` | `值 + 'g'` 或 `-` | - |
| 净重 | `row.netWeight` | 后端 `product.netWeight` | `值 + 'g'` 或 `-` | - |
| 体积 | `row.volume` | 后端 `product.volume` | `值` 或 `-` m³ | 根据包材计算 |
| 装柜数 | `row.cabinetSmall` | 后端 `product.cabinetSmall` | `值` 或 `-` | 根据包材计算 |
| 高柜数 | `row.cabinetLarge` | 后端 `product.cabinetLarge` | `值` 或 `-` | 根据包材计算 |
| 箱规 | `row.packagingData` | 后端 `product.packagingData` | 解析纸箱长×宽×高 | - |
| 装箱数 | `row.pack` | 后端 `product.packagingData` | 纸箱装箱数 | - |
| 成本 | `row.totalCost` | 后端 `product.totalCost` | `¥值` 或 `-` | - |
| 售价 | `row.unitPrice` | 后端 `product.unitPrice` | `¥值` 或 `-` | - |
| **编辑按钮** | - | - | - | 跳转到 `/product/edit?id=row.id` |
| **删除按钮** | - | - | - | 弹窗确认后调用 `productApi.delete(id)` |

### 分页控件

| 组件 | 数据来源 | 操作说明 |
|------|----------|----------|
| 分页条 | 页码、每页条数、总数 | 切换页码或每页条数时调用 `loadData()` |

### 新增产品按钮

**点击效果**: 调用 `goToAddProduct()`

**逻辑流程**:
1. 检查 `/product/add` 标签是否已打开
2. 未打开：调用 `markForReset('/product/add')` 标记重置
3. 已打开：直接跳转，`keep-alive` 保留数据
4. 跳转到 `/product/add`

### 删除操作

**API调用**: `productApi.delete(id)`

**成功**: `ElMessage.success('删除成功')` + 刷新列表

**失败**: `ElMessage.error('删除失败：' + message)`

---

## 1.2 产品表单（新增/编辑）

**路由**: `/product/add` 或 `/product/edit`
**组件**: `ProductForm.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 材料分类选项 | `materialCategoryApi.list()` | 材料分类下拉 |
| 伞架数据（全部） | `umbrellaFrameApi.page({current:1, size:1000})` | 伞架级联选择 |
| 材料数据（全部） | `materialApi.page({current:1, size:1000})` | 材料名称下拉 |
| 面料数据（全部） | `fabricApi.page({current:1, size:1000})` | 面料种类、型号下拉 |
| 包材数据（全部） | `packagingApi.page({current:1, size:1000})` | 包材类型、名称下拉 |
| 面料用量数据 | `fabricUsageApi.list()` | 查询面料用量 |
| 纸箱单价数据 | `boxPriceApi.list()` | 纸箱单价查询 |
| 工价工序数据 | `processApi.page({current:1, size:999})` | 工价表格工序名称 |
| 印刷方式字典 | `dictApi.list('print_type')` | 印刷方式下拉 |
| 对齐方式字典 | `dictApi.list('print_alignment')` | 对齐方式下拉 |
| 产品详情数据 | `productApi.getById(id)` | 编辑模式时回显所有数据 |

### 表单字段说明

#### 基本信息区域

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 数据来源 | 数据用途 |
|--------|------|----------|----------|----------|----------|----------|
| 产品编号 | `el-input` | `formData.code` | String | 必填 | 用户输入 | 唯一标识 |
| 产品类型 | `DictSelect` | `formData.category` | Array | 必填、多选 | `productCategoryApi.list()` | 产品分类标识 |
| 毛重 | `el-input` | `formData.grossWeight` | BigDecimal | - | 用户输入 | 重量信息 |
| 净重 | `el-input` | `formData.netWeight` | BigDecimal | - | 用户输入 | 重量信息 |
| 损耗/杂费 | `el-input` | `formData.salePrice` | BigDecimal | - | 用户输入 | 成本计算附加项 |
|**总成本** | 只读显示 | `totalCostComputed` | BigDecimal | - | 自动计算 | `伞架成本 + 面料总计 + 其他材料总计 + 包材总计 + 工价总计 + 印刷方式总计 + 损耗/杂费` |
| 售价 | `el-input` | `formData.unitPrice` | BigDecimal | - | 用户输入 | 产品销售价格 |

**自动计算字段**（保存时同步计算）:
- **体积(m³)**: 纸箱体积 = 包材表格中最大体积纸箱的`长×宽×高 / 1000000`
- **小柜装柜数**: `floor(28m³ / 体积(m³) × 装箱数)`
- **高柜装柜数**: `floor(68m³ / 体积(m³) × 装箱数)`

#### 产品图片上传区域

| 字段名 | 组件 | 数据字段 | 数据类型 | 说明 |
|--------|------|----------|----------|----------|
| 图片列表 | `imageList` | - | Array<String> | 存储已上传的URL数组 |
| 上传按钮 | `el-upload` | - | - | 选择文件后调用上传接口 |

**上传接口**: `POST /api/upload/image`

**请求**: `FormData { file: MultipartFile }`

**响应**: `{ code: 200, data: "https://oss-cn-hangzhou.aliyuncs.com/..." }`

**上传中**: 显示加载图标

**成功**: 添加到 `imageList`

**失败**: `ElMessage.error('图片上传失败')`

**保存时序列化**: `formData.images = JSON.stringify(imageList)`

#### 货品描述区域

| 字段名 | 组件 | 数据字段 | 数据类型 | 说明 |
|--------|------|----------|----------|----------|
| 刷新按钮 | - | - | - | 点击后重新生成描述并翻译 |
| 语言切换 | `el-switch` | `descLang` | String | 切换中/英文描述 |
| 中文描述 | `el-textarea` | `formData.description` | String | 产品中文描述 |
| 英文描述 | `el-textarea` | `formData.descriptionEn` | String | 产品英文描述 |

**刷新按钮逻辑**:
1. 组装中文描述：
   ```
   品名：功能+类型
   尺寸：尺寸cm/mm/k
   伞架：材料
   面料：种类-型号
   材料：分类-名称
   ```
2. 调用 `translateApi.translate(formData.description)`
3. 保存翻译结果到 `formData.descriptionEn`

#### 伞架选择区域

| 字段名 | 组件 | 数据字段 | 数据类型 | 数据来源 | 说明 |
|--------|------|----------|----------|----------|----------|
| 级联选择器 | `el-cascader` | `umbrellaCascascaderValue` | Array | `umbrellaAllRecords` | 5级级联选择 |
| 刷新按钮 | - | - | - | 刷新伞架数据 |
| 成本标签 | - | - | String | 当前选中伞架的成本 |

**伞架级联结构**: 5级
1. 类型 (`type`): 折叠/直柄/自动等
2. 尺寸 (`sizeCm/sizeMm/sizeK`): 如 "55cm × 8mm × 8k"
3. 功能 (`function`): 如 "三折"
4. 材料 (`material`): 如 "纤维"
5. 特定属性 (`attribute`): 如 "-"

**级联变化事件**:
1. 移除之前伞架的绑定材料行（保留用户添加的）
2. 查找匹配的伞架记录
3. 设置 `selectedUmbrellaFrame` 和 `formData.umbrellaFrameId`
4. 更新所有面料行的用量数据
5. 如果伞架有绑定材料，注入到材料列表顶部（只读行）

**刷新按钮**: 调用 `umbrellaFrameApi.page({current:1, size:1000})`

#### 其他材料表格

| 列名 | 数据字段 | 数据类型 | 数据来源 | 计算方式 |
|------|----------|----------|----------|----------|
| 材料分类 | `row.materialType` | String | `materialTypeOptions` | 下拉选择 |
| 材料名称 | `row.materialName` | String | `materialAllRecords` | 根据分类过滤 |
| 尺寸 | `row.materialSize` | String | 选择名称时自动填充 | - |
| 数量 | `row.materialQty` | String | 用户输入，默认"1" | - |
| **价格(元)** | - | BigDecimal | **单价 × 数量** | - |

**材料名称选项**: `materialAllRecords.filter(r => r.category === row.materialType)`

**选择材料名称时自动填充**:
- `row.materialSize` = `record.size`
- `row._materialUnitCost` = `record.cost`

**添加材料按钮**: 添加新行 `{ materialType: '', materialName: '', materialSize: '', materialQty: '1', _materialUnitCost: '' }`

#### 面料选择区域

| 列名 | 数据字段 | 数据类型 | 数据来源 | 计算方式 |
|------|----------|----------|----------|----------|
| 种类 | `row.fabricName` | String | `fabricNameOptions` (去重) | 下拉选择 |
| 型号 | `row.fabricCode` | String | `fabricAllRecords` | 根据种类过滤 | 下拉选择 |
| 门幅(cm) | `row.fabricWidth` | BigDecimal | 选择型号时自动填充 | - |
| 用量(米) | `row.params` | String | 查询 `fabricUsage` 表 | 自动填充 |
| **价格(元)** | - | BigDecimal | **用量 × 单价（码单位×0.9144）** | - |

**面料用量查询**: `fabricUsageAllRecords.find(u => u.sizeCm === 伞架尺寸 && u.sizeK === 伞架K)`

**选择型号时自动填充**:
- `row.fabricWidth` = `record.width`
- `row.fabricCost` = `record.cost`
- `row.fabricUnit` = `record.unit`
- `row.params` = 查询面料用量

**添加面料按钮**: 添加新行，自动填充用量

#### 默认包材区域

| 列名 | 数据字段 | 数据类型 | 数据来源 | 计算方式 |
|------|----------|----------|----------|----------|
| 包材类型 | `row.packagingType` | String | `packagingTypeOptions` | 下拉选择 |
| 包材名称 | `row.packagingName` | String | `packagingOptions` | 根据类型过滤 | 下拉选择 |
| 尺寸(cm) | - | - | 根据类型不同 | 纸箱：3个输入框(长/宽/高)；其他：下拉选择尺寸规格 |
| 装箱数 | `row.packagingQty` | String | 用户输入，默认"1" | - |
| **单价(元)** | - | BigDecimal | 纸箱：`(长+宽+8)×(宽+高+4)×unitCost/10000/装箱数`；其他：`基础单价/装箱数` | - |

**纸箱尺寸输入** (`packagingType === '纸箱'`):
- 三个输入框：长、宽、高
- 数据绑定：`row.boxLength`, `row.boxWidth`, `row.boxHeight`
- 单价计算公式：`(长 + 宽 + 8) × (宽 + 高 + 4) × unitCost / 10000 / 装箱数`

**其他包材尺寸选择** (`packagingType !== '纸箱'`):
- 数据绑定：`row.packagingSpec`
- 单价计算公式：`基础单价 / 装箱数`

**纸箱单价来源**: `boxPriceAllRecords` (从 `/api/box-price/list`)

#### 印刷方式表格

| 列名 | 数据字段 | 数据类型 | 数据来源 |
|------|----------|----------|----------|
| 面料类型 | `row.fabricLabel` | String | 从面料表格同步 | 只读显示 |
| 印刷方式 | `row.printType` | String | `printTypeOptions` | 下拉选择 |
| 对齐方式 | `row.alignment` | String | `printAlignmentOptions` | 下拉选择 |
| 价格(元) | `row.price` | BigDecimal | 用户输入 | - |
| 版费(元) | `row.plateFee` | BigDecimal | 用户输入 | - |

#### 工价表格

| 列名 | 数据字段 | 数据类型 | 数据来源 |
|------|----------|----------|----------|
| 工序名称 | 固定值 | String | `process` 表 | 只读显示 |
| 金额(元) | `wageAmounts[processId]` | BigDecimal | 用户输入 | - |

### 保存按钮

**验证规则**:
- `code`: 产品编号（必填）
- `category`: 产品类型（必填）

**序列化准备**:
| 字段 | 序列化方式 | 说明 |
|------|------------|------|
| `images` | `JSON.stringify(imageList)` | 图片URL数组 |
| `category` | `JSON.stringify(formData.category)` | 产品分类数组 |
| `wageData` | `JSON.stringify(wageAmounts)` | 工价映射表 |
| `totalCost` | `parseFloat(totalCostComputed)` | 自动计算的总成本 |
| `unitPrice` | `parseFloat() or null` | 空字符串转null |
| `grossWeight` | `parseFloat() or null` | 空字符串转null |
| `netWeight` | `parseFloat() or null` | 空字符串转null |
| `materialData` | `JSON.stringify(过滤绑定材料)` | 其他材料数据 |
| `fabricData` | `JSON.stringify(所有面料行)` | 面料数据 |
| `printData` | `JSON.stringify(所有印刷行)` | 印刷数据 |
| `packagingData` | `JSON.stringify(所有包材行)` | 包材数据 |

**API调用**:
- 新增：`productApi.save(formData)`
- 编辑：`productApi.update(formData)`

**成功**: 关闭标签 + 刷新产品列表 + 跳转到 `/product`

---

## 1.3 工价管理

**路由**: `/process`
**组件**: `ProcessList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 工价列表数据 | `processApi.page({current:1, size:10})` | 表格显示 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索工艺名称/编号，回车触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，调用 `loadData()` |

### 工价列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 工序名称 | `row.name` | 后端 `process.name` | 原值显示 | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | - | 弹窗确认后调用 `processApi.delete(id)` |

### 工价表单（新增/编辑）

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|:----------|
| 工序编号 | `el-input` | `formData.code` | String | 必填 | - |
| 工序名称 | `el-input` | `formData.name` | String | 必填 | - |

**API调用**:
- 新增：`processApi.save(formData)`
- 编辑：`processApi.update(formData)`

### 印刷管理按钮

**点击效果**: 调用 `openPrintDialog()`

**弹窗内容**:
- 左侧：印刷方式表格
- 右侧：对齐方式表格

**印刷方式表格**:
| 列名 | 数据字段 | 数据类型 | 说明 |
|------|----------|----------|----------|
| 印刷方式 | `row.label` | String | 行内编辑 |
| **删除按钮** | - | - | 删除行 |

**对齐方式表格**:
| 列名 | 数据字段 | 数据类型 | 说明 |
|------|----------|----------|----------|
| 对齐方式 | `row.label` | String | 行内编辑 |
| **删除按钮** | - | - | 删除行 |

**保存逻辑**:
1. 验证所有行名称不为空
2. 删除旧的印刷方式字典项（`dictApi.list('print_type')`）
3. 删除旧的对齐方式字典项（`dictApi.list('print_alignment')`）
4. 批量保存新的印刷方式项（`dictApi.save()` 批量）
5. 批量保存新的对齐方式项（`dictApi.save()` 批量）

---

# 2. 材料管理

## 2.1 伞架列表

**路由**: `/umbrella-frame`
**组件**: `UmbrellaFrameList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 伞架列表数据 | `umbrellaFrameApi.page({current:1, size:10})` | 表格显示 |
| 字典数据 | `dictApi.list('umbrella_function')` 等 | 下拉选项 |
| 材料分类选项 | `materialCategoryApi.list()` | 材料分类下拉 |
| 材料数据 | `materialApi.page({current:1, size:1000})` | 材料名称下拉 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索所有字段，回车触发查询 |
| 功能下拉 | `filters.function` | `dictApi.list('umbrella_function')` | 精确匹配 |
| 类型下拉 | `filters.type` | `dictApi.list('umbrella_type')` | 精确匹配 |
| 尺寸下拉 | `filters.size` | 组合选项 | 解析为 `sizeCm/sizeMm/sizeK` |
| 材料下拉 | `filters.material` | `dictApi.list('umbrella_material')` | 精确匹配 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |
| **重置按钮** | - | - | 清空所有筛选，`loadData()` |

### 伞架列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 功能 | - | 后端 `row.function` | 原值显示 | - |
| 类型 | - | 后端 `row.type` | 原值显示 | - |
| 尺寸 | - | 组合显示 | `sizeCm + sizeMm + sizeK` | - |
| 材料 | - | 后端 `row.material` | 原值显示 | - |
| 特定属性 | - | 后端 `row.attribute` | 原值显示 | - |
| 绑定材料 | - | 后端 `row.boundMaterials` | 材材料名称列表 | JSON解析显示 |
| 单价 | - | 后端 `row.cost` | `¥值` | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | - | 弹窗确认后调用 `umbrellaFrameApi.delete(id)` |

### 新增/编辑弹窗

#### 基本信息表单

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 功能 | `DictSelect` | `formData.function` | String | 必填 | - |
| 类型 | `DictSelect` | `formData.type` | String | 必填 | - |
| 特定属性 | `el-input` | `formData.attribute` | String | - | - |
| 尺寸 | 3个DictSelect | `sizeCm/sizeMm/sizeK` | String | 必填 | cm/mm/k |
| 材料 | `DictSelect` | `formData.material` | String | 必填 | - |
| 单价 | `el-input-number` | `formData.cost` | BigDecimal | 必填 | - |

#### 绑定材料表格

| 列名 | 数据字段 | 数据类型 | 数据来源 | 计算方式 |
|------|----------|----------|----------|----------|
| 材料分类 | `row.materialType` | String | `materialTypeOptions` | 下拉选择 |
| 材料名称 | `row.materialName` | String | `materialAllRecords` | 根据分类过滤 |
| 尺寸 | `row.materialSize` | String | 选择名称时自动填充 | - |
| 数量 | `row.qty` | String | 用户输入 | - |
| **价格(元)** | - | BigDecimal | **单价 × 数量** | - |

**保存逻辑**:
- 序列化绑定材料：`boundMaterials: JSON.stringify([{ materialType, materialName, materialSize, qty, _unitCost }])`
- API：`umbrellaFrameApi.save()` / `umbrellaFrameApi.update()`
- 成功：关闭弹窗 + 刷新列表

**关闭弹窗确认**:
- 检测 `formDirty` 是否有未保存修改
- 提示："有未保存的内容，确定要关闭吗？"

---

## 2.2 面料列表

**路由**: `/fabric`
**组件**: `FabricList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 面料列表数据 | `fabricApi.page({current:1, size:10})` | 表格显示 |
| 字典数据 | `dictApi.list('fabric_category')` 等 | 下拉选项 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索所有字段，回车触发查询 |
| 种类下拉 | `filters.name` | `dictApi.list('fabric_category')` | 精确匹配 |
| 型号下拉 | `filters.code` | `dictApi.list('fabric_model')` | 精确匹配 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |
| **重置按钮** | - | - | 清空所有筛选，`loadData()` |

### 面料列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 种类 | - | 后端 `row.name` | 原值显示 | - |
| 型号 | - | 后端 `row.code` | 原值显示 | - |
| 门幅 | - | 后端 `row.width` | 原值显示 | - |
| 单价 | - | 后端 `row.cost` | `¥值` | - |
| 单位 | - | 后端 `row.unit` | 米/码 | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | - | 弹窗确认后调用 `fabricApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 种类 | ``DictSelect` | `formData.name` | String | 必填 | - |
| 型号 | `DictSelect` | `formData.code` | String | 必填 | - |
| 门幅 | `DictSelect` | `formData.width` | BigDecimal | - | - |
| 单价 | `el-input` | `formData.cost` | BigDecimal | - | - |
| 单位 | `el-radio` | `formData.unit` | String | 米/码 | - |

### 用量管理按钮

**点击效果**: 调用 `openUsageDialog()`

**API**: `fabricUsageApi.list()`

**用量配置表格**:
| 列名 | 数据字段 | 数据类型 | 说明 |
|------|----------|----------|----------|
| 尺寸 | `sizeCm/sizeK` | String | 下拉选择 | cm + k |
| 用量(米) | `row.usage` | String | 手动输入 | - |
| 收缩系数 | `row.shrink` | String | 手动输入 | - |
| 编辑按钮 | - | - | 行内编辑模式 |
| 删除按钮 | - | - | 删除行 |

**保存逻辑**:
1. 验证：尺寸、用量、收缩系数必填
2. 验证：无重复尺寸组合
3. API：`fabricUsageApi.saveAll(rows)`
4. 成功：关闭弹窗

---

## 2.3 其他材料

**路由**: `/material`
**组件**: `MaterialList.vue`

### 页面布局
- **左侧**: 分类侧边栏
- **右侧**: 材料列表

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 材料分类选项 | `materialCategoryApi.list()` | 左侧侧边栏 |
| 材料列表数据 | `materialApi.page({current:1, size:10})` | 右侧表格 |

### 分类侧边栏

| 组件 | 操作说明 |
|------|----------|
| 全部材料菜单 | 清空分类筛选，显示所有材料 |
| 分类菜单项 | 按分类筛选材料 |
| 新增分类按钮 | 打开分类表单弹窗 |
| 编辑分类图标 | 编辑分类 |
| 删除分类图标 | 删除分类（带验证）|

**删除分类验证逻辑**:
1. 查询该分类下是否还有材料：`materialApi.page({ category: cat.name, size:1 })`
2. 如果有材料 → 提示先删除材料
3. 如果无材料 → 删除分类

### 材料列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 所属分类 | - | 后端 `row.category` | 原值显示 | - |
| 材料名称 | - | 后端 `row.name` | 原值显示 | - |
| 尺寸 | - | 后端 `row.size` | 原值显示 | - |
| 价格 | - | 后端 `row.cost` | `¥值` | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | - | 弹窗确认后调用 `materialApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 材料名称 | `el-input` | `formData.name` | String | 必填 | - |
| 所属分类 | `el-select` | `formData.category` | String | - | 下拉选择 |
| 尺寸 | `el-input` | `formData.size` | String | - | - |
| 价格 | `el-input-number` | `formData.cost` | BigDecimal | - | - |

### 分类表单弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 分类名称 | `el-input` | `catFormData.name` | String | 必填 | - |
| 排序 | `el-input-number` | `catFormData.sort` | Integer | - | - |

**API**: `materialCategoryApi.save()` / `materialCategoryApi.update()` / `materialCategoryApi.delete(id)`

---

## 2.4 包材列表

**路由**: `/packaging`
**组件**: `PackagingList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 包材列表数据 | `packagingApi.page({current:1, size:10})` | 表格显示 |
| 包材类型字典 | `dictApi.list('packaging_type')` | 下拉选项 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索所有字段，回车触发查询 |
| 包材类型下拉 | `filters.type` | `dictApi.list('packaging_type')` | 精确匹配 |
| 尺寸输入框 | `filters.spec` | 用户输入 | 精确匹配 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |
| **重置按钮** | - | - | 清空所有筛选，`loadData()` |

### 包材列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 包材类型 | - | 后端 `row.type` | 原值显示 | - |
| 包材名称 | - | 后端 `row.name` | 原值显示 | - |
| 尺寸 | - | 后端 `row.spec` | 原值显示 | - |
| 单价 | - | 后端 `row.cost` | `¥值` | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | - | 弹窗确认后调用 `packagingApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 包材类型 | `DictSelect` | `formData.type` | String | - | - |
| 包材名称 | `el-input` | `formData.name` | String | 必填 | - |
| 尺寸 | `el-input` | `formData.spec` | String | - | - |
| 单价 | `el-input` | `formData.cost` | BigDecimal | - | - - |

### 纸箱单价按钮

**点击效果**: 调用 `openBoxPriceDialog()`

**API**: `boxPriceApi.list()`

**纸箱单价表格**:
| 列名 | 数据字段 | 数据类型 | 说明 |
|------|----------|----------|----------|
| 名称 | `row.name` | String | 行内编辑 | - |
| 单价(元) | `row.cost` | BigDecimal | 行内编辑 | - |
| 编辑按钮 | - | - | 切换编辑模式 | - |
| 删除按钮 | - | - | 删除行 |

**保存逻辑**: `boxPriceApi.saveAll(rows)`

---

# 3. 销售管理

## 3.1 客户管理

**路由**: `/customer`
**组件**: `CustomerList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 客户列表数据 | `customerApi.page({current:1, size:10})` | 表格显示 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索客户名称/编号/联系人，回车触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |

### 客户列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 客户编号 | - | 后端 `row.code` | 原值显示 | - |
| 客户名称 | - | 后端 `row.name` | 原值显示 | - |
| 归属 | - | 后端 `row.owner` | 原值显示 | - |
| 跟进人 | - | 后端 `row.follower` | 原值显示 | - |
| 客户类型 | - | 后端 `row.type` | 原值显示 | - |
| 客户层级 | - | 后端 `row.level` | 原值显示 | - |
| 近一年累计业绩 | - | 后端 `row.yearlyPerformance` | 原值显示 | - |
| 标签 | - | 后端 `row.tags` | 原值显示 | - |
| 国家地区 | - | 后端 `row.country` | 原值显示 | - |
| 创建时间 | - | 后端 `row.createTime` | 原值显示 | - |
| 最近跟进时间 | - | 后端 `row.lastFollowTime` | 原值显示 | - |
| 最近下单时间 | - | 后端 `row.lastOrderTime` | 原值显示 | - |
| **详情按钮** | - | - | - | 跳转到 `/customer/edit?id=row.id` |
| **删除按钮** | - | - | - | 弹窗确认后调用 `customerApi.delete(id)` |

### 新增客户按钮

**点击效果**: 跳转到 `/customer/add`

---

## 3.2 报价管理

**路由**: `/quote`
**组件**: `QuoteList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 报价列表数据 | `quoteApi.page({current:1, size:10})` | 表格显示 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索报价单号，回车触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |

### 报价列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 报价单号 | - | 后端 `row.code` | 原值显示 | - |
| 报价日期 | - | 后端 `row.quoteDate` | 原值显示 | - |
| 有效期 | - | 后端 `row.validDate` | 原值显示 | - |
| 报价金额 | - | 后端 `row.totalAmount` | `¥值` | - |
| 状态 | - | 后端 `row.status` | 标签显示 | draft→草稿(info)、confirmed→已确认(success)、expired→已过期(danger)、pending→待处理(warning)、processing→生产中(primary)、completed→已完成(success)、cancelled→已取消(danger) |
| 备注 | - | 后端 `row.remark` | 原值显示 | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - - | 弹窗确认后调用 `quoteApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 报价单号 | `el-input` | `formData.code` | String | 必填 | - |
| 报价日期 | `el-date-picker` | `formData.quoteDate` | Date | - | - |
| 有效期 | `el-date-picker` | `formData.validDate` | Date | - | - |
| 报价金额 | `el-input-number` | `formData.totalAmount` | BigDecimal | - | - |
| 状态 | `el-select` | `formData.status` | String | - | draft/confirmed/expired |
| 备注 | `el-textarea` | `formData.remark` | String | - | - |

**保存逻辑**:
- API：`'quoteApi.save()` / `quoteApi.update()`
- 成功：关闭弹窗 + 刷新列表

---

## 3.3 订单管理

**路由**: `/order`
**组件**: `OrderList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 订单列表数据 | `ordersApi.page({current:1, size:10})` | 表格显示 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索订单编号，回车触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |

### 订单列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 订单编号 | - | 后端 `row.code` | 原值显示 | - |
| 下单日期 | - | 后端 `row.orderDate` | 原值显示 | - |
| 要求交期 | - | 后端 `row.deliveryDate` | 原值显示 | - |
| 订单金额 | - | 后端 `row.totalAmount` | `¥值` | - |
| 状态 | - | 后端 `row.status` | 标签显示 | draft→草稿(info)、confirmed→已确认(success)、expired→已过期(danger)、pending→待处理(warning)、processing→生产中(primary)、completed→已完成(success)、cancelled→已取消(danger) |
| 备注 | - | 后端 `row.remark` | 原值显示 | - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | 弹窗确认后调用 `ordersApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 订单编号 | `el-input` | `formData.code` | String | 必填 | - |
| 下单日期 | `el-date-picker` | `formData.orderDate` | Date | - | - |
| 要求交期 | `el-date-picker` | `formData.deliveryDate` | Date | - | - |
| 订单金额 | `el-input-number` | `formData.totalAmount` | BigDecimal | - | - |
| 状态 | `el-select` | `formData.status` | String | - | pending/processing/completed/cancelled |
| 备注 | `el-textarea` | `formData.remark` | String | - | - |

**保存逻辑**:
- API：`ordersApi.save()` / `ordersApi.update()`
- 成功：关闭弹窗 + 刷新列表

---

# 4. 采购管理

## 4.1 供应商管理

**路由**: `/supplier`
**组件**: `SupplierList.vue`

### 页面初始化数据加载

| 数据项 | API接口 | 用途 |
|--------|----------|------|
| 供应商列表数据 | `supplierApi.page({current:1, size:10})` | 表格显示 |

### 搜索区域

| 搜索项 | 数据字段 | 数据来源 | 操作说明 |
|--------|----------|----------|----------|
| 关键字输入框 | `keyword` | 用户输入 | 搜索供应商名称/编号，回车触发查询 |
| **查询按钮** | - | - | 点击后重置页码为1，`loadData()` |

### 供应商列表表格

| 列名 | 显示字段 | 数据来源 | 显示格式 | 操作说明 |
|------|----------|----------|----------|----------|
| 供应商编号 | - | 后端 `row.code` | 原值显示 | - |
| 供应商名称 | - | 后端 `row.name` | 原值显示 | - |
| 联系人 | - | 后端 `row.contact` | 原值显示 | - |
| 电话 | - | 后端 `row.phone` | 原值显示 | - |
| 邮箱 | - | 后端 `row.email` | 原值显示 | - |
| 地址 | - | 后端 `row.address` | 原值显示 | - |
| 状态 | - | 后端 `row.status` | 标签显示（启用/停用）| - |
| **编辑按钮** | - | - | - | 打开编辑弹窗 |
| **删除按钮** | - | - | 弹窗确认后调用 `supplierApi.delete(id)` |

### 新增/编辑弹窗

| 字段名 | 组件 | 数据字段 | 数据类型 | 验证规则 | 说明 |
|--------|------|----------|----------|----------|----------|
| 供应商编号 | `el-input` | `formData.code` | String | 必填 | - |
| 供应商名称 | `el-input` | `formData.name` | String | 必填 | - |
| 联系人 | `el-input` | `formData.contact` | String | - | - |
| 电话 | `el-input` | `formData.phone` | String | - | - |
| 邮箱 | `el-input` | `formData.email` | String | - | - |
| 状态 | `el-select` | `formData.status` | Integer | - | 启用(1) / 停用(0) |
| 地址 | `el-input` | `formData.address` | String | - | - |
| 备注 | `el-textarea` | `formData.remark` | String | - | - |

**保存逻辑**:
- API：`supplierApi.save()` / `supplierApi.update()`
- 成功：关闭弹窗 + 刷新列表

---

# 5. 生产管理

## 5.1 生产单列表

**路由**: `/production`
**组件**: `ProductionList.vue`

### 当前状态

**显示**: `el-empty description="页面开发中，逻辑待完善"`

**功能**: 占位符，实际逻辑待实现

---

# 6. 财务管理

## 6.1 应收款管理

**路由**: `/finance/receivable`
**组件**: `Receivable.vue`

### 当前状态

**显示**: `el-empty description="页面开发中，逻辑待完善"`

**功能**: 占位符，实际逻辑待实现

---

## 6.2 应付款管理

**路由**: `/finance/payable`
**组件**: `Payable.vue`

### 当前状态

**显示**: `el-empty description="页面开发中，逻辑待完善"`

**功能**: 占位符，实际逻辑待实现

---

# 7. 系统管理

## 7.1 数据字典

**路由**: `/dict`

### 字典类型列表

| 字典类型 | 说明 | 字典项来源 |
|----------|------|----------|
| `product_category` | 产品分类 | `dictApi.list('product_category')` |
| `umbrella_type` | 伞架类型 | `dictApi.list('umbrella_type')` |
| `umbrella_size_cm` | 伞架尺寸(cm) | `dictApi.list('umbrella_size_cm')` |
| `umbrella_size_mm` | 伞架尺寸(mm) | `dictApi.list('umbrella_size_mm')` |
| `umbrella_size_k` | 伞架尺寸(k) | `dictApi.list('umbrella_size_k')` |
| `umbrella_function` | 伞架功能 | `dictApi.list('umbrella_function')` |
| `umbrella_material` | 伞架材料 | `dictApi.list('umbrella_material')` |
| `fabric_category` | 面料种类 | `dictApi.list('fabric_category')` |
| `fabric_model` | 面料型号 | `dictApi.list('fabric_model')` |
| `fabric_width` | 面料门幅 | `dictApi.list('fabric_width')` |
| `packaging_type` | 包材类型 | `dictApi.list('packaging_type')` |
| `print_type` | 印刷方式 | `dictApi.list('print_type')` |
| `print_alignment` | 对齐方式 | `dictApi.list('print_alignment')` |

### 字典项数据结构

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键 |
| dictType | String | 字典类型 |
| label | String | 显示标签 |
| value | String | 字典值 |
| sort | Integer | 排序 |
| status | Integer | 状态 |
| remark | String | 备注 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

# 8. 后端API接口规范

## 8.1 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 具体数据
  }
}
```

## 8.2 分页请求参数

```javascript
{
  current: 1,        // 当前页码
  size: 10,           // 每页条数
  keyword: "",         // 关键字搜索（可选）
  // 其他筛选条件...
}
```

## 8.3 分页响应格式

```json
{
  "code": 200,
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

## 8.4 API接口列表

### 字典接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dict/types` | 获取所有字典类型 |
| GET | `/api/dict/list?dictType=xxx` | 获取字典项列表 |
| POST | `/api/dict` | 新增字典项 |
| PUT | `/api/dict` | 更新字典项 |
| DELETE | `/api/dict/{id}` | 删除字典项 |

### 产品接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/product/page` | 分页查询（支持多条件筛选） |
| GET | `/api/product/{id}` | 获取产品详情 |
| POST | `/api/product` | 新增产品 |
| PUT | `/api/product` | 更新产品 |
| DELETE | `/api/product/{id}` | 删除产品 |

**产品分页查询参数**:
- `keyword`: 产品编号/名称模糊查询
- `category`: 产品类别
- `frameType`: 伞架类型
- `frameSizeCm`: 伞架尺寸cm
- `frameFunction`: 伞架功能
- `frameMaterial`: 伞架材料
- `fabricKind`: 面料种类
- `printType`: 印刷方式
- `alignType`: 对齐方式

**产品数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "category": String,           // JSON数组
  "unit": String,
  "price": BigDecimal,
  "status": Integer,
  "remark": String,
  "images": String,              // JSON数组URL
  "grossWeight": BigDecimal,
  "netWeight": BigDecimal,
  "volume": BigDecimal,
  "cabinetSmall": String,
  "cabinetLarge": String,
  "description": String,
  "descriptionEn": String,
  "totalCost": BigDecimal,
  "unitPrice": BigDecimal,
  "salePrice": BigDecimal,
  "printMethod": String,
  "wageData": String,           // JSON对象
  "umbrellaFrameId": Long,
  "materialData": String,       // JSON数组
  "fabricData": String,          // JSON数组
  "printData": String,           // JSON数组
  "packagingData": String,       // JSON数组
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 伞架接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/umbrella-frame/page` | 分页查询 |
| GET | `/api/umbrella-frame/{id}` | 获取详情 |
| POST | `/api/umbrella-frame` | 新增伞架 |
| PUT | `/api/umbrella-frame` | 更新伞架 |
| DELETE | `/api/umbrella-frame/{id}` | 删除伞架 |

**伞架数据字段**:
```java
{
  "id": Long,
  "function": String,           // 功能
  "sizeCm": String,             // 尺寸cm
  "sizeMm": String,             // 尺寸mm
  "sizeK": String,              // 尺寸k
  "type": String,               // 类型
  "material": String,           // 材料
  "cost": BigDecimal,           // 成本
  "attribute": String,          // 特定属性
  "boundMaterials": String,      // 绑定材料(JSON)
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 面料接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/fabric/page` | 分页查询 |
| GET | `/api/fabric/{id}` | 获取详情 |
| POST | `/api/fabric` | 新增面料 |
| PUT | `/api/fabric` | 更新面料 |
| DELETE | `/api/fabric/{id}` | 删除面料 |

**面料数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "color": String,
  "width": BigDecimal,           // 门幅
  "cost": BigDecimal,
  "stock": BigDecimal,
  "unit": String,                // 单位（米/码）
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 面料用量接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/fabric-usage/list` | 获取所有用量配置 |
| POST | `/api/fabric-usage/save-all` | 批量保存用量 |

**面料用量数据字段**:
```java
{
  "id": Long,
  "sizeCm": String,       // 尺寸cm
  "sizeK": String,         // 尺寸k
  "usage": String,         // 用量(米)
  "shrink": String,        // 收缩系数
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 纸箱单价接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/box-price/list` | 获取所有纸箱单价 |
| POST | `/api/box-price/save-all` | 批量保存纸箱单价 |

**纸箱单价数据字段**:
```java
{
  "id": Long,
  "name": String,         // 名称
  "cost": BigDecimal,      // 单价
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 材料接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/material/page` | 分页查询 |
| GET | `/api/material/{id}` | 获取详情 |
| POST | `/api/material` | 新增材料 |
| PUT | `/api/material` | 更新材料 |
| DELETE | `/api/material/{id}` | 删除材料 |

**材料数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "category": String,
  "size": String,
  "cost": BigDecimal,
  "params": String,
  "supplierId": Long,
  "stock": BigDecimal,
  "status": Integer,
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 材料分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/material-category/list` | 获取所有分类 |
| POST | `/api/material-category` | 新增分类 |
| PUT | `/api/material-category` | 更新分类 |
| DELETE | `/api/material-category/{`}` | 删除分类 |

### 包材接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/packaging/page` | 分页查询 |
| GET | `/api/packaging/{id}` | 获取详情 |
| POST | `/api/packaging` | 新增包材 |
| PUT | `/api/packaging` | 更新包材 |
| DELETE | `/api/packaging/{id}` | 删除包材

**包材数据字段**:
```java
{
  "id": Long,
  "type": String,
  "code": String,
  "name": String,
  "spec": String,
  "unit": String,
  "cost": BigDecimal,
  "stock": BigDecimal,
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 工价接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/process/page` | 分页查询 |
| GET | `/api/process/{id}` | 获取详情 |
| POST | `/api/process` | 新增工价 |
| PUT | `/api/process` | 更新工价 |
| DELETE | `/api/process/{id}` | 删除工价 |

**工价数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "productId": Long,
  "description": String,
  "status": Integer,
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 产品分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/product-category/list` | 获取所有分类 |
| POST | `/api/product-category` | 新增分类 |
| PUT | `/api/product-category` | 更新分类 |
| DELETE | `/api/product-category/{id}` | 删除分类 |

### 客户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customer/page` | 分页查询 |
| GET | `/api/customer/{id}` | 获取详情 |
| POST | `/api/customer` | 新增客户 |
| PUT | `/api/customer` | 更新客户 |
| DELETE | `/api/customer/{id}` | 删除客户 |

**客户数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "contact": String,
  "phone": String,
  "email": String,
  "address": String,
  "status": Integer,
  "remark": String,
  "owner": String,               // 归属
  "follower": String,            // 跟进人
  "type": String,                // 客户类型
  "level": String,                // 客户层级
  "yearlyPerformance": String,     // 近一年累计业绩
  "tags": String,                // 标签
  "country": String,              // 国家地区
  "createTime": LocalDateTime,     // 创建时间
  "lastFollowTime": LocalDateTime,  // 最近跟进时间
  "lastOrderTime": LocalDateTime,   // 最近下单时间
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 供应商接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/supplier/page` | 分页查询 |
| GET | `/api/supplier/{id}` | 获取详情 |
| POST | `/api/supplier` | 新增供应商 |
| PUT | `/api/supplier` | 更新供应商 |
| DELETE | `/api/supplier/{id}` | 删除供应商 |

**供应商数据字段**:
```java
{
  "id": Long,
  "code": String,
  "name": String,
  "contact": String,
  "phone": String,
  "email": String,
  "address": String,
  "status": Integer,
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 报价接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/quote/page` | 分页查询 |
| GET | `/api/quote/{id}` | 获取详情 |
| POST | `/api/quote` | 新增报价 |
| PUT | `/api/quote` | 更新报价 |
| DELETE | `/api/quote/{id}` | 删除报价 |

**报价数据字段**:
```java
{
  "id": Long,
  "code": String,
  "customerId": Long,
  "quoteDate": Date,
  "validDate": Date,
  "totalAmount": BigDecimal,
  "status": String,             // draft/confirmed/expired
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 订单接口

| 方法 | 路径 | 说明 |
|------|------|{------|
| GET | `/api/orders/page` | 分页查询 |
| GET | `/api/orders/{id}` | 获取详情 |
| POST | `/api/orders` | 新增订单 |
| PUT | `/api/orders` | 更新订单 |
| DELETE | `/api/orders/{id}` | 删除订单 |

**订单数据字段**:
```java
{
  "id": Long,
  "code": String,
  "customerId": Long,
  "quoteId": Long,
  "orderDate": Date,
  "deliveryDate": Date,
  "totalAmount": BigDecimal,
  "status": String,             // pending/processing/completed/cancelled
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 财务接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/finance/page` | 分页查询 |
| GET | `/api/finance/{id}` | 获取详情 |
| POST | `/api/finance` | 新增财务记录 |
| PUT | `/api/finance` | 更新财务记录 |
| DELETE | `/api/finance/{id}` | 删除财务记录 |

**财务数据字段**:
```java
{
  "id": Long,
  "code": String,
  "type": String,               // income/expense
  "category": String,
  "amount": BigDecimal,
  "orderId": Long,
  "tradeDate": Date,
  "description": String,
  "status": String,
  "remark": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### 文件上传接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/upload/image` | 上传图片到OSS |

**上传请求**:
- `Content-Type`: `multipart/form-data`
- 参数: `file: MultipartFile`

**上传响应**:
```json
{
  "code": 200,
  "data": "https://oss-cn-hangzhou.aliyuncs.com/bucket/path/filename.ext"
}
```

**上传逻辑**:
1. 生成唯一文件名: `UUID + 原扩展名`
2. 生成路径: `product/yyyy/MM/dd/文件名`
3. 上传到阿里云OSS
4. 返回完整URL

### 翻译接口（AI）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/translate` | 中文→英文翻译 |

**翻译请求**:
```json
{
  "text": "品名：折叠自动伞\n尺寸：55cm\n..."
}
```

**翻译响应**:
```json
{
  "code": 200,
  "data": "Product Name: Folding Auto Umbrella\nSize: 55cm\n..."
}
```

**翻译逻辑**:
1. 使用字节跳动豆包AI模型
2. 系统提示词：伞伞行业专业翻译
3. 返回翻译结果

---

## 附录

### 前端计算公式汇总

| 计算项 | 公式 | 说明 |
|--------|------|------|
| 产品总成本 | 伞架成本 + 面料总计 + 其他材料总计 + 包材总计 + 工价总计 + 印刷方式总计 + 损耗/杂费 | - |
| 面料价格 | 单价 × 数量 | - |
| 面料价格 | 用量 × 单价（码单位×0.9144） | 单位是码时需要转换 |
| 纸箱单价 | (长 + 宽 + 8) × (宽 + 高 + 4) × unitCost / 10000 / 装箱数 | - |
| 其他包材单价 | 基础单价 / 装箱数 | - |
| 箱体积 | 长×宽×高 (cm³) | - |
| 体积(m³) | 箱体积 / 1000000 | - |
| 小柜装柜数 | floor(28m³ / 体积(m³) × 装箱数) | - |
| 高柜装柜数 | floor(68m³ / 体积(m³) × 装箱数) | - |

### 全局数据字典类型列表

| 字典类型 | 说明 |
|----------|------|
| `product_category` | 产品分类 |
| `umbrella_type` | 伞架类型 |
| `umbrella_size_cm` | 伞架尺寸(cm) |
| `umbrella_size_mm` | 伞架尺寸(mm) |
| `umbrella_size_k` | 伞架尺寸(k) |
| `umbrella_function` | 伞架功能 |
| `umbrella_material` | 伞架材料 |
| `fabric_category` | 面料种类 |
| `fabric_model` | 面料型号 |
| `fabric_width` | 面料门幅 |
| `packaging_type` | 包材类型 |
| `print_type` | 印刷方式 |
| `print_alignment` | 对齐方式 |

---

*文档结束*
