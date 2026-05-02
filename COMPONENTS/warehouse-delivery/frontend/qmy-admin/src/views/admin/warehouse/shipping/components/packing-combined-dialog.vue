<template>
  <el-dialog
    v-model="dialogVisible"
    title="开始打包"
    width="97%"
    top="3vh"
    class="packing-combined-dialog"
    @close="onDestroy"
  >
    <div v-loading="pageLoading" class="combined-content">
      <!-- 左侧：打包需求 -->
      <div class="left-panel-wrapper">
        <div class="left-panel">
          <div class="panel-header">
            <div class="panel-title">打包需求</div>
          </div>
          <div class="detail-header">
            <span>客户名称：{{ customerName }}</span>
            <span style="margin-left: 40px">{{ status == 0 ? '最新更新时间' : '打包完成时间' }}：{{ updateTime }}</span>
          </div>
          <div class="tabs-wrapper">
            <div class="tabs-left">
              <el-radio-group v-model="viewType">
                <el-radio-button value="product">按产品分类</el-radio-button>
                <el-radio-button value="order">按订单分类</el-radio-button>
              </el-radio-group>
            </div>
            <div class="tabs-right">
              <el-input v-model="searchProductCode" placeholder="产品ID搜索" clearable style="width: 140px" />
              <el-input
                v-model="searchSpecName"
                placeholder="规格搜索"
                clearable
                style="width: 140px; margin-left: 8px"
              />
            </div>
          </div>
          <!-- 按产品分类 -->
          <el-table
            v-if="viewType === 'product'"
            ref="productTableRef"
            :data="filteredProductList"
            :span-method="productSpanMethod"
            max-height="500"
            border
            @selection-change="handleProductSelectionChange"
          >
            <el-table-column label="" width="50" align="center">
              <template #header>
                <el-checkbox
                  :model-value="areAllProductsSelected"
                  :disabled="isAllProductsDisabled"
                  @change="handleSelectAllProducts"
                />
              </template>
              <template #default="{ row, $index }">
                <el-checkbox
                  v-if="isProductFirstRow($index)"
                  :model-value="isProductSelected(row.productId)"
                  :disabled="row.packageNumber >= row.enterNumber"
                  @change="handleProductCheckboxChange(row.productId, $event)"
                />
              </template>
            </el-table-column>
            <el-table-column label="产品ID" prop="productCode" width="100" align="center">
              <template #default="{ row }">
                {{ row.productCode }}
              </template>
            </el-table-column>
            <el-table-column label="选择" width="80" align="center">
              <template #header>
                <el-checkbox
                  :model-value="isAllSpecsSelected"
                  :disabled="isAllSpecsDisabled"
                  @change="handleSelectAllSpecs"
                />
              </template>
              <template #default="{ row }">
                <el-checkbox
                  :model-value="row.selected"
                  :disabled="row.packageNumber >= row.enterNumber"
                  @change="handleSpecCheckboxChange(row, $event)"
                />
              </template>
            </el-table-column>
            <el-table-column label="本次发货总数" prop="number" align="center" />
            <el-table-column label="规格名称" align="center" min-width="110">
              <template #default="{ row }">
                <div class="spec-name-cell">
                  <el-tooltip v-if="row.description" :content="row.description" placement="top">
                    <div class="spec-name-content" style="cursor: pointer">
                      <template v-for="(segment, index) in row.specSegments" :key="index">
                        <span
                          class="spec-name-segment"
                          :class="{ 'spec-name-segment--colored': !!segment.color }"
                          :style="{
                            backgroundColor: segment.color || undefined,
                            color: segment.color ? getContrastTextColor(segment.color) : undefined
                          }"
                        >
                          {{ segment.text }}
                        </span>
                        <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                      </template>
                    </div>
                  </el-tooltip>
                  <div v-else class="spec-name-content">
                    <template v-for="(segment, index) in row.specSegments" :key="index">
                      <span
                        class="spec-name-segment"
                        :class="{ 'spec-name-segment--colored': !!segment.color }"
                        :style="{
                          backgroundColor: segment.color || undefined,
                          color: segment.color ? getContrastTextColor(segment.color) : undefined
                        }"
                      >
                        {{ segment.text }}
                      </span>
                      <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                    </template>
                  </div>
                  <el-tag v-if="row.isCustomerStore" size="small" type="primary" style="margin-left: 4px">独</el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="规格图片" align="center">
              <template #default="{ row }">
                <el-image
                  v-if="row.specImage"
                  :src="row.specImage"
                  v-image-preview="row.specImage"
                  fit="cover"
                  style="width: 40px; height: 40px"
                />
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="库位" prop="location" align="center" />
            <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
              <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
            </el-table-column>
            <el-table-column label="本次发货数量" align="center" width="120">
              <template #default="{ row }">
                <div :style="{ color: row.enterNumber < row.number ? '#F56C6C' : '#67C23A' }">
                  <div>要求：{{ row.number }}</div>
                  <div>仓库：{{ row.enterNumber }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="实际打包数量" align="center" width="120">
              <template #default="{ row }">
                <div :style="{ color: row.packageNumber < row.enterNumber ? '#F56C6C' : '#67C23A' }">
                  {{ row.packageNumber }}
                </div>
              </template>
            </el-table-column>
          </el-table>
          <!-- 按订单分类 -->
          <div v-if="viewType === 'order'" class="order-view">
            <div class="order-list">
              <div
                v-for="(order, index) in displayOrderList"
                :key="order.id"
                :class="['order-item', { active: currentOrderId === order.id }]"
                @click="handleOrderChange(order.id)"
              >
                <div class="order-label">
                  <span :class="['status-dot', getOrderStatus(order.id)]" />
                  <span>订单{{ index + 1 }}</span>
                </div>
                <div class="order-code">{{ order.orderCode }}</div>
              </div>
            </div>
            <div class="order-detail-wrapper">
              <div class="order-info">
                <div class="order-info-left">
                  <div class="order-info-base">
                    <span>订单号：{{ currentOrder.orderCode }}</span>
                    <span>发货形式：{{ currentOrder.shippingMethodText }}</span>
                  </div>
                  <div class="order-info-remark">
                    <span class="remark-label">订单备注：</span>
                    <el-tooltip :content="currentOrder.orderRemark || '-'" placement="top">
                      <span class="remark-text">{{ currentOrder.orderRemark || '-' }}</span>
                    </el-tooltip>
                  </div>
                </div>
                <div class="order-info-right">
                  <div class="status-item" v-if="currentOrder.productQuantityStatusText">
                    <el-icon
                      :size="16"
                      :color="currentOrder.productQuantityStatusIcon === 'success' ? '#67C23A' : '#F56C6C'"
                    >
                      <CircleCheck v-if="currentOrder.productQuantityStatusIcon === 'success'" />
                      <CircleClose v-else />
                    </el-icon>
                    <span>{{ currentOrder.productQuantityStatusText }}</span>
                  </div>
                  <div class="status-item" v-if="currentOrder.packingStatusText">
                    <el-icon :size="16" :color="currentOrder.packingStatusIcon === 'success' ? '#67C23A' : '#F56C6C'">
                      <CircleCheck v-if="currentOrder.packingStatusIcon === 'success'" />
                      <CircleClose v-else />
                    </el-icon>
                    <span>{{ currentOrder.packingStatusText }}</span>
                  </div>
                </div>
              </div>
              <el-table
                ref="orderProductTableRef"
                :data="currentOrder.products"
                :span-method="orderSpanMethod"
                max-height="400"
                border
                @selection-change="handleOrderProductSelectionChange"
              >
                <el-table-column label="" width="50" align="center">
                  <template #header>
                    <el-checkbox
                      :model-value="areAllOrderProductsSelected"
                      :disabled="isAllOrderProductsDisabled"
                      @change="handleSelectAllOrderProducts"
                    />
                  </template>
                  <template #default="{ row, $index }">
                    <el-checkbox
                      v-if="isOrderProductFirstRow($index)"
                      :model-value="isOrderProductSelected(row.productId)"
                      :disabled="row.packageNumber >= row.enterNumber"
                      @change="handleOrderProductCheckboxChange(row.productId, $event)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="产品ID" prop="productCode" align="center">
                  <template #default="{ row }">
                    {{ row.productCode }}
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="80" align="center">
                  <template #header>
                    <el-checkbox
                      :model-value="isAllOrderSpecsSelected"
                      :disabled="isAllOrderSpecsDisabled"
                      @change="handleSelectAllOrderSpecs"
                    />
                  </template>
                  <template #default="{ row }">
                    <el-checkbox
                      :model-value="row.selected"
                      :disabled="row.packageNumber >= row.enterNumber"
                      @change="handleSpecCheckboxChange(row, $event)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="本次发货总数" prop="number" align="center" />
                <el-table-column label="规格名称" align="center" min-width="110">
                  <template #default="{ row }">
                    <div class="spec-name-cell">
                      <el-tooltip v-if="row.description" :content="row.description" placement="top">
                        <div class="spec-name-content" style="cursor: pointer">
                          <template v-for="(segment, index) in row.specSegments" :key="index">
                            <span
                              class="spec-name-segment"
                              :class="{ 'spec-name-segment--colored': !!segment.color }"
                              :style="{
                                backgroundColor: segment.color || undefined,
                                color: segment.color ? getContrastTextColor(segment.color) : undefined
                              }"
                            >
                              {{ segment.text }}
                            </span>
                            <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                          </template>
                        </div>
                      </el-tooltip>
                      <div v-else class="spec-name-content">
                        <template v-for="(segment, index) in row.specSegments" :key="index">
                          <span
                            class="spec-name-segment"
                            :class="{ 'spec-name-segment--colored': !!segment.color }"
                            :style="{
                              backgroundColor: segment.color || undefined,
                              color: segment.color ? getContrastTextColor(segment.color) : undefined
                            }"
                          >
                            {{ segment.text }}
                          </span>
                          <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                        </template>
                      </div>
                      <el-tag v-if="row.isCustomerStore" size="small" type="primary" style="margin-left: 4px">
                        独
                      </el-tag>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="规格图片" align="center">
                  <template #default="{ row }">
                    <el-image
                      v-if="row.specImage"
                      :src="row.specImage"
                      v-image-preview="row.specImage"
                      fit="cover"
                      style="width: 40px; height: 40px"
                    />
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="库位" prop="location" align="center" />
                <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
                  <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
                </el-table-column>
                <el-table-column label="本次发货数量" align="center" width="120">
                  <template #default="{ row }">
                    <div :style="{ color: row.enterNumber < row.number ? '#F56C6C' : '#67C23A' }">
                      <div>要求：{{ row.number }}</div>
                      <div>仓库：{{ row.enterNumber }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="实际打包数量" align="center" width="120">
                  <template #default="{ row }">
                    <div :style="{ color: row.packageNumber < row.enterNumber ? '#F56C6C' : '#67C23A' }">
                      {{ row.packageNumber }}
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="left-panel-footer">
          <el-button type="primary" @click="handlePrint">打印</el-button>
        </div>
      </div>
      <!-- 中间：穿梭按钮 -->
      <div class="transfer-buttons">
        <el-button class="transfer-button" type="primary" :icon="ArrowRight" @click="transferToPackage" />
      </div>
      <!-- 右侧：包裹详情 -->
      <div class="right-panel-wrapper">
        <div class="right-panel">
          <div class="panel-title">包裹详情</div>
          <div class="detail-header">
            <span>客户名称：{{ customerName }}</span>
          </div>
          <div class="packing-content">
            <div ref="packageSidebarRef" class="package-sidebar">
              <div
                v-for="(pkg, index) in visiblePackageList"
                :key="pkg.id"
                :class="['package-card', { active: currentPackageIndex === index }]"
                @click="currentPackageIndex = index"
              >
                <el-icon class="close-icon" @click.stop="removePackage(index)"><Close /></el-icon>
                <div class="package-label">包裹{{ index + 1 }}</div>
                <el-input
                  v-model="pkg.boxCode"
                  placeholder="请输入箱号"
                  size="small"
                  style="margin-top: 3px"
                  clearable
                  @click.stop
                />
              </div>
              <el-button type="primary" link @click="addNewPackage">新增包裹</el-button>
            </div>
            <div class="package-main">
              <div class="scan-area">
                <div class="scan-input">
                  <el-input v-model="scanCode" placeholder="请扫码" @keyup.enter="handleScan">
                    <template #suffix>
                      <el-icon :size="24" style="cursor: pointer" @click="handleScan"><View /></el-icon>
                    </template>
                  </el-input>
                </div>
                <div class="weight-input">
                  <el-input
                    v-model="currentPackage.boxWeight"
                    placeholder="预估10kg"
                    style="width: 170px"
                    @input="handleWeightInput"
                  >
                    <template #append>kg</template>
                  </el-input>
                </div>
                <div class="dimension-input">
                  <el-input
                    v-model="currentPackage.boxSize"
                    placeholder="请输入尺寸"
                    style="width: 170px"
                    @input="handleDimensionInput"
                  />
                </div>
                <el-button type="danger" @click="clearAllProducts">一键全清</el-button>
              </div>
              <el-table
                :data="currentPackage.products"
                border
                style="width: 100%"
                :span-method="packageProductSpanMethod"
              >
                <el-table-column label="产品ID" prop="productCode" width="100" align="center" />
                <el-table-column label="规格名称" align="center">
                  <template #default="{ row }">
                    <el-tooltip v-if="row.description" :content="row.description" placement="top">
                      <div class="spec-name-content" style="cursor: pointer">
                        <template v-for="(segment, index) in row.specSegments" :key="index">
                          <span
                            class="spec-name-segment"
                            :class="{ 'spec-name-segment--colored': !!segment.color }"
                            :style="{
                              backgroundColor: segment.color || undefined,
                              color: segment.color ? getContrastTextColor(segment.color) : undefined
                            }"
                          >
                            {{ segment.text }}
                          </span>
                          <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                        </template>
                      </div>
                    </el-tooltip>
                    <div v-else class="spec-name-content">
                      <template v-for="(segment, index) in row.specSegments" :key="index">
                        <span
                          class="spec-name-segment"
                          :class="{ 'spec-name-segment--colored': !!segment.color }"
                          :style="{
                            backgroundColor: segment.color || undefined,
                            color: segment.color ? getContrastTextColor(segment.color) : undefined
                          }"
                        >
                          {{ segment.text }}
                        </span>
                        <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                      </template>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="图片" align="center">
                  <template #default="{ row }">
                    <el-image v-if="row.image" :src="row.image" fit="cover" style="width: 40px; height: 40px" />
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="所属订单" prop="orderNo" align="center" />
                <el-table-column label="实际打包数量" width="90" align="center">
                  <template #default="{ row }">
                    <el-input
                      v-model="row.actualCount"
                      placeholder="请输入"
                      style="width: 60px"
                      @input="(val: string) => { row.actualCount = validateInteger(val); validateActualCount(row) }"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
                  <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
                </el-table-column>
                <el-table-column label="状态" width="70" align="center">
                  <template #default="{ row }">
                    <el-icon
                      v-if="row.orderNo"
                      :size="18"
                      :color="getOrderShippingConditionMet(row) ? '#67C23A' : '#F56C6C'"
                    >
                      <CircleCheck v-if="getOrderShippingConditionMet(row)" />
                      <CircleClose v-else />
                    </el-icon>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ $index }">
                    <el-button
                      type="danger"
                      link
                      size="small"
                      @click="removeProductWithSync(currentPackageIndex, $index)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="right-panel-footer">
          <el-button :disabled="allPackageGreen" @click="handleNotifyBusiness">通知业务</el-button>
          <el-button @click="handleSave">暂存</el-button>
          <el-button
            type="primary"
            :loading="completeSubmitting"
            :disabled="hasRedPackageStatus || completeSubmitting"
            v-permission="'sto:yt:delivery:completePackage'"
            @click="handleComplete"
          >
            打包完成
          </el-button>
        </div>
      </div>
    </div>
    <template #footer />
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted, nextTick, watch } from 'vue'
import { Close, View, ArrowRight, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import {
  getPackageList,
  getPackageItemList,
  deliveryScan,
  getDeliveryDetail,
  getDeliveryDetailOrder,
  savePackage,
  completePackage,
  sendPackageMessage
} from '@/api/admin/warehouse'
import { getDeliveryTypeLabel } from '@/constant/yitang/sales'
import { validateInteger, validateDecimal } from '@/utils/validate'
import PrintPreviewDialog from './print-preview-dialog.vue'

const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const status = ref(rowData?.status ?? 0)

const viewType = ref('product')
const productTableRef = ref()
const orderProductTableRef = ref()
const currentOrderId = ref<string | number | null>(null)
const productList = ref<any[]>([])
const orderList = ref<any[]>([])
const displayOrderList = ref<any[]>([])
const selectedProducts = ref<any[]>([])
const completeSubmitting = ref(false)
const pageLoading = ref(false)

const customerName = ref(rowData?.customerName)
const updateTime = ref(rowData?.originalData?.updateTime || '')
const searchProductCode = ref('')
const searchSpecName = ref('')

const filteredProductList = computed(() => {
  return productList.value.filter((item: any) => {
    const matchCode =
      !searchProductCode.value || (item.productCode || '').toLowerCase().includes(searchProductCode.value.toLowerCase())
    const matchSpec =
      !searchSpecName.value || (item.specName || '').toLowerCase().includes(searchSpecName.value.toLowerCase())
    return matchCode && matchSpec
  })
})

const buildSortedOrderList = (list: any[] = []) => {
  return list
    .map(order => {
      const isProductComplete = order.productQuantityStatusIcon === 'success'
      const isPackingComplete = order.packingStatusIcon === 'success'
      const isComplete = isProductComplete && isPackingComplete

      return {
        ...order,
        status: isComplete ? 'complete' : 'incomplete'
      }
    })
    .sort((a, b) => {
      if (a.status === 'incomplete' && b.status === 'complete') return -1
      if (a.status === 'complete' && b.status === 'incomplete') return 1
      return 0
    })
}

const getContrastTextColor = (backgroundColor?: string) => {
  if (!backgroundColor) return '#303133'
  const normalized = backgroundColor.trim().replace('#', '')
  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) return '#303133'

  const red = parseInt(normalized.slice(0, 2), 16)
  const green = parseInt(normalized.slice(2, 4), 16)
  const blue = parseInt(normalized.slice(4, 6), 16)
  const brightness = red * 0.299 + green * 0.587 + blue * 0.114
  return brightness > 186 ? '#303133' : '#ffffff'
}

const buildSpecSegments = (specItems: any[] = [], fallbackName = '') => {
  if (!Array.isArray(specItems) || specItems.length === 0) {
    return [{ text: fallbackName || '-', color: '' }]
  }

  return specItems.map((item: any) => ({
    text: item.categorySpecificationItemValue || item.value || '',
    color: item.color || ''
  }))
}

const getOrderStatus = (orderId: string | number) => {
  const order = orderList.value.find(item => item.id === orderId)
  if (!order) return 'incomplete'
  return order.productQuantityStatusIcon === 'success' && order.packingStatusIcon === 'success'
    ? 'complete'
    : 'incomplete'
}

/**
 * 计算某 orderNo + 规格 在所有包裹中已打包的总数
 */
const getOrderSpecPackedTotal = (orderNo: string, specificationId: any, categorySpecificationItemId: any): number => {
  const normalizedCatId = normalizeCategoryId(categorySpecificationItemId)
  return packageList.value
    .filter((pkg: any) => !pkg.isDeleted)
    .reduce((total: number, pkg: any) => {
      return (
        total +
        (pkg.products || [])
          .filter(
            (p: any) =>
              p.orderNo === orderNo &&
              p.specificationId === specificationId &&
              normalizeCategoryId(p.categorySpecificationItemId) === normalizedCatId
          )
          .reduce((sum: number, p: any) => sum + Number(p.actualCount || 0), 0)
      )
    }, 0)
}

/**
 * 根据发货形式判断右侧包裹行对应订单是否满足发货条件
 * 0 整单齐发：订单所有规格都在包裹中
 * 1 单款齐发：该行 productCode 的所有规格都在包裹中
 * 2 单规格齐发：订单任意一个规格在包裹中即可
 * 3 有货就发：直接绿色
 */
const getOrderShippingConditionMet = (row: any): boolean => {
  const orderNo = row.orderNo ?? ''
  if (!orderNo) return false
  const order = orderList.value.find((o: any) => o.orderCode === orderNo)
  if (!order) return false

  const method = order.shippingMethod ?? '3'
  if (method === '3') return true

  const orderProducts: any[] = (order.products || []).filter((item: any) => item.specificationId)
  if (orderProducts.length === 0) return false

  const isPacked = (p: any) => {
    const required = Number(p.number || p.enterNumber || 0)
    const packed = getOrderSpecPackedTotal(orderNo, p.specificationId, p.categorySpecificationItemId)
    return required > 0 && packed >= required
  }

  if (method === '2') {
    // 单规格齐发：该行对应规格的打包数量 >= 需求量即可
    const normalizedCatId = normalizeCategoryId(row.categorySpecificationItemId)
    const matchingProduct = orderProducts.find(
      (p: any) =>
        p.specificationId === row.specificationId &&
        normalizeCategoryId(p.categorySpecificationItemId) === normalizedCatId
    )
    return matchingProduct ? isPacked(matchingProduct) : false
  }

  if (method === '1') {
    const sameProductSpecs = orderProducts.filter((p: any) => p.productCode === row.productCode)
    return sameProductSpecs.length > 0 && sameProductSpecs.every(isPacked)
  }

  // method === '0'
  return orderProducts.every(isPacked)
}

const currentOrder = computed(() => {
  if (orderList.value.length > 0 && currentOrderId.value !== null) {
    return (
      orderList.value.find(order => order.id === currentOrderId.value) || {
        products: [],
        orderCode: '',
        shippingMethodText: '',
        orderRemark: ''
      }
    )
  }
  return { products: [], orderCode: '', shippingMethodText: '', orderRemark: '' }
})

const productSpanMethod = ({ row, columnIndex, rowIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 1) {
    const list = filteredProductList.value
    const isFirst = rowIndex === 0 || list[rowIndex - 1]?.productId !== row.productId
    if (isFirst) {
      const span = list.filter((r: any) => r.productId === row.productId).length
      return { rowspan: span, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
  return { rowspan: 1, colspan: 1 }
}

const orderSpanMethod = ({ row, columnIndex, rowIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 1) {
    const list = currentOrder.value.products || []
    const isFirst = rowIndex === 0 || list[rowIndex - 1]?.productId !== row.productId
    if (isFirst) {
      const span = list.filter((r: any) => r.productId === row.productId).length
      return { rowspan: span, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
  return { rowspan: 1, colspan: 1 }
}

const getSelectableRows = (rows: any[] = []) => rows.filter((row: any) => row.packageNumber < row.enterNumber)

const isGroupFirstRow = (rows: any[] = [], index: number): boolean => {
  if (index === 0) return true
  const prevRow = rows[index - 1]
  const currRow = rows[index]
  return prevRow.productId !== currRow.productId
}

const isGroupSelected = (rows: any[] = [], productId: string | number): boolean => {
  const productRows = rows.filter((row: any) => row.productId === productId)
  if (productRows.length === 0) return false
  return productRows.every((row: any) => row.selected)
}

const isAllSpecsSelectedByRows = (rows: any[] = []): boolean => {
  const availableRows = getSelectableRows(rows)
  return availableRows.length > 0 && availableRows.every((row: any) => row.selected)
}

const isAllSpecsDisabledByRows = (rows: any[] = []): boolean => {
  return getSelectableRows(rows).length === 0
}

const areAllProductsSelectedByRows = (rows: any[] = []): boolean => {
  if (rows.length === 0) return false

  const productIds = [...new Set(rows.map((row: any) => row.productId))]
  const availableProductIds = productIds.filter((pid: any) => {
    const productRows = rows.filter((row: any) => row.productId === pid)
    return getSelectableRows(productRows).length > 0
  })

  if (availableProductIds.length === 0) return false

  return availableProductIds.every((pid: any) => {
    const productRows = rows.filter((row: any) => row.productId === pid)
    return getSelectableRows(productRows).every((row: any) => row.selected)
  })
}

const areAllProductsDisabledByRows = (rows: any[] = []): boolean => {
  return getSelectableRows(rows).length === 0
}

const toggleAllSpecsByRows = (rows: any[] = [], checked: boolean) => {
  rows.forEach((row: any) => {
    if (checked && row.packageNumber >= row.enterNumber) {
      return
    }
    row.selected = checked
  })
}

const toggleProductByRows = (rows: any[] = [], productId: string | number, checked: boolean) => {
  rows.forEach((row: any) => {
    if (row.productId === productId) {
      row.selected = checked
    }
  })
}

/**
 * 判断是否所有规格都被选中（忽略禁用项）
 */
const isAllSpecsSelected = computed(() => isAllSpecsSelectedByRows(filteredProductList.value))

/**
 * 判断表头复选框是否应该被禁用（没有任何可勾选时禁用）
 */
const isAllSpecsDisabled = computed(() => isAllSpecsDisabledByRows(filteredProductList.value))

const isAllOrderSpecsSelected = computed(() => isAllSpecsSelectedByRows(currentOrder.value.products || []))

const isAllOrderSpecsDisabled = computed(() => isAllSpecsDisabledByRows(currentOrder.value.products || []))

/**
 * 全选/取消全选所有规格
 */
const handleSelectAllSpecs = (checked: boolean) => {
  toggleAllSpecsByRows(filteredProductList.value, checked)
}

const handleSelectAllOrderSpecs = (checked: boolean) => {
  toggleAllSpecsByRows(currentOrder.value.products || [], checked)
}

const isAllProductsDisabled = computed(() => areAllProductsDisabledByRows(filteredProductList.value))

const areAllOrderProductsSelected = computed(() => areAllProductsSelectedByRows(currentOrder.value.products || []))

const isAllOrderProductsDisabled = computed(() => areAllProductsDisabledByRows(currentOrder.value.products || []))

/**
 * 判断是否所有产品都被选中（忽略禁用项）
 */
const areAllProductsSelected = computed(() => areAllProductsSelectedByRows(filteredProductList.value))

/**
 * 全选/取消全选所有产品
 */
const handleSelectAllProducts = (checked: boolean) => {
  toggleAllSpecsByRows(filteredProductList.value, checked)
}

const handleSelectAllOrderProducts = (checked: boolean) => {
  toggleAllSpecsByRows(currentOrder.value.products || [], checked)
}

const handleProductSelectionChange = (rows: any[]) => {
  selectedProducts.value = rows
}

/**
 * 判断是否是产品的第一行
 */
const isProductFirstRow = (index: number): boolean => {
  return isGroupFirstRow(filteredProductList.value, index)
}

const isOrderProductFirstRow = (index: number): boolean => {
  return isGroupFirstRow(currentOrder.value.products || [], index)
}

/**
 * 判断产品所有规格是否被选中
 */
const isProductSelected = (productId: string | number): boolean => {
  return isGroupSelected(filteredProductList.value, productId)
}

const isOrderProductSelected = (productId: string | number): boolean => {
  return isGroupSelected(currentOrder.value.products || [], productId)
}

const handleProductCheckboxChange = (productId: string | number, checked: boolean) => {
  toggleProductByRows(filteredProductList.value, productId, checked)
}

const handleOrderProductCheckboxChange = (productId: string | number, checked: boolean) => {
  toggleProductByRows(currentOrder.value.products || [], productId, checked)
}

const handleSpecCheckboxChange = (row: any, checked: boolean) => {
  row.selected = checked
}

const handleOrderProductSelectionChange = (rows: any[]) => {
  selectedProducts.value = rows
}

const processItemListToProducts = (itemList: any[]) => {
  const processedProducts: any[] = []
  const productGroups: { [key: string]: any[] } = {}
  itemList
    .filter((item: any) => item.specificationId)
    .forEach((item: any) => {
      const productCode = item.productCode || ''
      if (!productGroups[productCode]) productGroups[productCode] = []
      productGroups[productCode].push(item)
    })
  Object.values(productGroups).forEach((group: any[]) => {
    let totalCount = 0
    group.forEach((item: any, index: number) => {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
        .filter((v: string) => v)
        .join('-')
      const specSegments = buildSpecSegments(specItems, specName)
      const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''
      if (index === 0) totalCount = group.reduce((sum, g) => sum + (g.number || 0), 0)
      processedProducts.push({
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        totalCount,
        specName: specName || '-',
        specSegments,
        specImage: imageUrl,
        image: imageUrl,
        location: item.locationName || '-',
        locationName: item.locationName || '-',
        number: item.number || 0,
        enterNumber: item.enterNumber || 0,
        packageNumber: item.packageNumber || 0,
        originalPackageNumber: item.packageNumber || 0,
        rowspan: index === 0 ? group.length : 0,
        description: item.specificationDesc || '',
        isCustomerStore: item.isCustomerStore || false
      })
    })
  })
  return processedProducts
}

const fetchProductDetail = async () => {
  const { data } = await getDeliveryDetail({ id: rowData.id, orderId: rowData.originalData?.orderId || null })
  if (data && data.itemList && data.itemList.length > 0) {
    productList.value = processItemListToProducts(data.itemList)
    syncPackageNumbers()
  }
}

const fetchOrderSubList = async () => {
  const { data } = await getDeliveryDetailOrder({ id: rowData.id })
  if (data && Array.isArray(data)) {
    orderList.value = data.map((item: any, index: number) => {
      const productComplete = !!item.productComplete
      const packingComplete = !!item.packageComplete
      return {
        id: item.orderId || String(index + 1),
        orderCode: item.orderCode ?? '',
        shippingMethodText: '',
        orderRemark: '',
        products: [],
        productQuantityStatus: productComplete ? 1 : 0,
        productQuantityStatusText: productComplete ? '产品齐全' : '产品不全',
        productQuantityStatusIcon: productComplete ? 'success' : 'error',
        packingStatus: packingComplete ? 1 : 0,
        packingStatusText: packingComplete ? '打包齐全' : '打包不全',
        packingStatusIcon: packingComplete ? 'success' : 'error'
      }
    })
    displayOrderList.value = buildSortedOrderList(orderList.value)
    if (orderList.value.length > 0) {
      await nextTick()
      // 加载所有订单的产品数据（产品视图转移时需要按订单拆分）
      await Promise.all(orderList.value.map(order => fetchOrderDetail(order.id)))
      if (displayOrderList.value.length > 0) {
        currentOrderId.value = displayOrderList.value[0].id
      }
    }
  }
}

const fetchOrderDetail = async (orderId: string) => {
  const { data } = await getDeliveryDetail({ id: rowData.id, orderId })
  if (data) {
    const order = orderList.value.find(item => item.id === orderId)
    if (!order) return
    order.shippingMethodText = getDeliveryTypeLabel(data.orderShippingMethod) || '-'
    order.shippingMethod = String(data.orderShippingMethod ?? '3')
    order.orderRemark = data.orderRemark ?? ''
    order.productQuantityStatus = data.orderProductComplete ? 1 : 0
    order.packingStatus = data.orderPackageComplete ? 1 : 0
    order.productQuantityStatusText = data.orderProductComplete ? '产品齐全' : '产品不全'
    order.packingStatusText = data.orderPackageComplete ? '打包齐全' : '打包不全'
    order.productQuantityStatusIcon = data.orderProductComplete ? 'success' : 'error'
    order.packingStatusIcon = data.orderPackageComplete ? 'success' : 'error'
    displayOrderList.value = buildSortedOrderList(orderList.value)
    if (data.itemList && data.itemList.length > 0) {
      order.products = processItemListToProducts(data.itemList)
      syncPackageNumbers()
    }
  }
}

const loadBaseData = async () => {
  pageLoading.value = true
  try {
    await fetchPackageList()
    // 并行加载产品列表和所有订单数据，确保产品视图转移时可按订单拆分
    await Promise.all([fetchProductDetail(), fetchOrderSubList()])
  } finally {
    pageLoading.value = false
  }
}

const loadViewData = async (type: string) => {
  pageLoading.value = true
  try {
    if (type === 'product') {
      await fetchProductDetail()
    } else if (type === 'order') {
      await fetchOrderSubList()
    }
  } finally {
    pageLoading.value = false
  }
}

const handleOrderChange = async (orderId: string) => {
  currentOrderId.value = orderId
  const order = orderList.value.find(item => item.id === orderId)
  if (order && order.products && order.products.length > 0) {
    syncPackageNumbers()
    return
  }
  pageLoading.value = true
  try {
    await fetchOrderDetail(orderId)
  } finally {
    pageLoading.value = false
  }
}

watch(viewType, newType => {
  selectedProducts.value = []
  void loadViewData(newType)
})

const normalizeCategoryId = (id: any): string => {
  if (id === null || id === undefined || id === '' || id === '-1' || id === 0 || id === '0') return ''
  return String(id)
}

/**
 * 计算某个规格在所有包裹中已经打包的总数量
 */
const getPackedTotal = (specificationId: string | number, categoryId?: string | number): number => {
  const normalizedCategoryId = normalizeCategoryId(categoryId)
  let total = 0
  packageList.value.forEach((pkg: any) => {
    if (pkg.isDeleted) return
    ;(pkg.products || []).forEach((product: any) => {
      if (
        product.specificationId === specificationId &&
        normalizeCategoryId(product.categorySpecificationItemId) === normalizedCategoryId
      ) {
        total += Number(product.actualCount || 0)
      }
    })
  })
  return total
}

/**
 * 计算某个规格在所有包裹中属于指定订单的已打包数量
 */
const getPackedTotalByOrder = (
  specificationId: string | number,
  categoryId: string | number | undefined,
  orderCode: string
): number => {
  const normalizedCategoryId = normalizeCategoryId(categoryId)
  let total = 0
  packageList.value.forEach((pkg: any) => {
    if (pkg.isDeleted) return
    ;(pkg.products || []).forEach((product: any) => {
      if (
        product.specificationId === specificationId &&
        normalizeCategoryId(product.categorySpecificationItemId) === normalizedCategoryId &&
        product.orderNo === orderCode
      ) {
        total += Number(product.actualCount || 0)
      }
    })
  })
  return total
}

const syncPackageNumbers = () => {
  if (productList.value && productList.value.length > 0) {
    productList.value.forEach((row: any) => {
      const packedInPackages = getPackedTotal(row.specificationId, row.categorySpecificationItemId)
      row.packageNumber = packedInPackages
    })
  }

  if (orderList.value && orderList.value.length > 0) {
    orderList.value.forEach((order: any) => {
      ;(order.products || []).forEach((row: any) => {
        row.packageNumber = getPackedTotalByOrder(row.specificationId, row.categorySpecificationItemId, order.orderCode)
      })
      // 有产品数据时联动更新订单状态
      // if (order.products && order.products.length > 0) {
      //   const isComplete = order.products.every((row: any) => row.packageNumber >= row.number)
      //   order.packingStatus = isComplete ? 1 : 0
      //   order.packingStatusText = isComplete ? '打包齐全' : '打包不全'
      //   order.packingStatusIcon = isComplete ? 'success' : 'error'
      // }
    })
    // displayOrderList.value = buildSortedOrderList(orderList.value)
  }
}

/**
 * 右侧包裹表格单元格合并：产品ID、规格名称、图片列对相同规格的多订单行合并
 */
const packageProductSpanMethod = ({ row, rowIndex, columnIndex }: any) => {
  if (columnIndex > 2) return { rowspan: 1, colspan: 1 }
  const products = currentPackage.value?.products || []
  const specKey = (r: any) =>
    `${r.productCode}||${r.specificationId}||${normalizeCategoryId(r.categorySpecificationItemId)}`
  const currentKey = specKey(row)
  if (rowIndex > 0 && specKey(products[rowIndex - 1]) === currentKey) {
    return { rowspan: 0, colspan: 0 }
  }
  let count = 1
  while (rowIndex + count < products.length && specKey(products[rowIndex + count]) === currentKey) {
    count++
  }
  return { rowspan: count, colspan: 1 }
}

/**
 * 计算某个规格（指定订单）在除当前包裹外还可打包的剩余数量
 */
const getRemainingPackableCount = (item: any, currentPackageId?: string | number): number => {
  const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)
  const orderNo = item.orderCode ?? item.orderNo ?? ''
  let otherPackagesTotal = 0

  packageList.value.forEach((pkg: any) => {
    if (pkg.isDeleted || pkg.id === currentPackageId) return
    ;(pkg.products || []).forEach((product: any) => {
      if (
        product.specificationId === item.specificationId &&
        normalizeCategoryId(product.categorySpecificationItemId) === normalizedCategoryId &&
        (orderNo === '' || product.orderNo === orderNo)
      ) {
        otherPackagesTotal += Number(product.actualCount || 0)
      }
    })
  })

  return Math.max(Number(item.enterNumber || 0) - otherPackagesTotal, 0)
}

/**
 * 产品视图转移时，将一个规格 item 按订单拆分成多个 item（每个带 orderCode）
 */
const expandItemByOrder = (item: any): any[] => {
  const result: any[] = []
  const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)
  orderList.value.forEach((order: any) => {
    const orderProduct = (order.products || []).find(
      (p: any) =>
        p.specificationId === item.specificationId &&
        normalizeCategoryId(p.categorySpecificationItemId) === normalizedCategoryId
    )
    if (orderProduct && Number(orderProduct.number || 0) > 0) {
      result.push({
        ...item,
        enterNumber: orderProduct.enterNumber,
        number: orderProduct.number,
        orderCode: order.orderCode
      })
    }
  })
  return result.length > 0 ? result : [{ ...item, orderCode: '' }]
}

const transferToPackage = async () => {
  let selectedItems: any[] = []
  if (viewType.value === 'product') {
    // 确保所有订单的产品数据已加载
    const unloadedOrders = orderList.value.filter((o: any) => !o.products?.length)
    if (unloadedOrders.length > 0) {
      await Promise.all(unloadedOrders.map((o: any) => fetchOrderDetail(o.id)))
    }
    const rawSelected = filteredProductList.value.filter((row: any) => row.selected)
    selectedItems = rawSelected.flatMap((item: any) => expandItemByOrder(item))
  } else if (viewType.value === 'order') {
    // 按订单分类
    selectedItems = (currentOrder.value.products || []).filter((row: any) => row.selected)
  }

  if (selectedItems.length === 0) {
    ElMessage.warning('请先选择要打包的产品')
    return
  }
  // 过滤实际打包数量已达仓库数量的产品
  selectedItems = selectedItems.filter((item: any) => item.packageNumber < item.enterNumber)
  if (selectedItems.length === 0) {
    ElMessage.warning('所有已选产品的打包数量已达仓库数量')
    return
  }
  const currentPkg = currentPackage.value
  if (!currentPkg || !currentPkg.id) {
    ElMessage.warning('请先新增包裹')
    return
  }
  const realPkgIndex = packageList.value.findIndex((p: any) => p.id === currentPkg.id)
  if (realPkgIndex === -1) return
  const realPkg = packageList.value[realPkgIndex]
  if (!realPkg.products) realPkg.products = []

  selectedItems.forEach((item: any) => {
    const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)

    const currentOrderNo = viewType.value === 'order' ? currentOrder.value.orderCode ?? '' : item.orderCode ?? ''

    const existing = realPkg.products.find(
      (p: any) =>
        p.specificationId === item.specificationId &&
        normalizeCategoryId(p.categorySpecificationItemId) === normalizedCategoryId &&
        p.orderNo === currentOrderNo
    )

    // 剩余可打包数量 = 仓库数量 - 其它包裹已打包数量
    const remainingCount = getRemainingPackableCount(item, realPkg.id)

    // 当前包裹中该规格（同订单）已有数量
    const currentPkgHas = existing ? Number(existing.actualCount || 0) : 0

    // 本次还可补入当前包裹的数量
    const fillCount = remainingCount - currentPkgHas

    if (fillCount <= 0) {
      return
    }

    if (existing) {
      // 当前包裹已存在同订单同规格行，补足数量
      existing.actualCount = currentPkgHas + fillCount
    } else {
      // 当前包裹不存在，新增行
      realPkg.products.push({
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: item.specName || '-',
        specSegments: item.specSegments || buildSpecSegments([], item.specName),
        image: item.specImage || item.image || '',
        locationName: item.location || item.locationName || '-',
        actualCount: fillCount,
        packCount: item.number || 0,
        enterNumber: item.enterNumber || 0,
        description: item.description || '',
        orderNo: currentOrderNo
      })
    }
  })
  ElMessage.success('已添加到当前包裹')
  if (productTableRef.value) productTableRef.value.clearSelection()
  if (orderProductTableRef.value) orderProductTableRef.value.clearSelection()
  selectedProducts.value = []
  if (viewType.value === 'product') {
    filteredProductList.value.forEach((row: any) => {
      row.selected = false
    })
  } else if (viewType.value === 'order') {
    ;(currentOrder.value.products || []).forEach((row: any) => {
      row.selected = false
    })
  }
  if (viewType.value === 'product') {
    productList.value.forEach((row: any) => {
      row._checkboxChecked = false
    })
  }
}

const currentPackageIndex = ref(0)
const scanCode = ref('')
const packageSidebarRef = ref<HTMLElement>()
const packageList = ref<any[]>([])

// 监听包裹列表变化
watch(
  packageList,
  () => {
    syncPackageNumbers()
  },
  { deep: true }
)

const visiblePackageList = computed(() => packageList.value.filter((pkg: any) => !pkg.isDeleted))

const currentPackage = computed(() => {
  if (currentPackageIndex.value >= 0 && currentPackageIndex.value < visiblePackageList.value.length) {
    return visiblePackageList.value[currentPackageIndex.value]
  }
  return { products: [], boxWeight: '', boxSize: '', id: '' }
})

const addNewPackage = async () => {
  packageList.value.push({
    id: String(Date.now()),
    boxCode: getNextBoxCode(),
    boxName: '',
    boxSize: '',
    boxWeight: '',
    products: [],
    isNew: true
  })
  currentPackageIndex.value = visiblePackageList.value.length - 1
  nextTick(() => {
    if (packageSidebarRef.value) packageSidebarRef.value.scrollTop = packageSidebarRef.value.scrollHeight
  })
}

const handleWeightInput = (val: string) => {
  if (!visiblePackageList.value.length) {
    return
  }
  const currentPkg = currentPackage.value
  if (currentPkg) currentPkg.boxWeight = validateDecimal(val)
}

const handleDimensionInput = (val: string) => {
  if (!visiblePackageList.value.length) {
    return
  }
  const currentPkg = currentPackage.value
  if (currentPkg) currentPkg.boxSize = val
}

const clearAllProducts = () => {
  scanCode.value = ''
  currentPackage.value.boxWeight = ''
  currentPackage.value.boxSize = ''
  if (currentPackage.value.products) {
    currentPackage.value.products = []
    ElMessage.success('已清空本包裹产品')
  }
}

const removePackage = (index: number) => {
  const pkg = visiblePackageList.value[index]
  if (!pkg) return
  if (pkg.isNew) {
    const realIndex = packageList.value.findIndex((p: any) => p.id === pkg.id)
    if (realIndex !== -1) packageList.value.splice(realIndex, 1)
  } else {
    const realIndex = packageList.value.findIndex((p: any) => p.id === pkg.id)
    if (realIndex !== -1) packageList.value[realIndex].isDeleted = 1
  }
  if (currentPackageIndex.value >= visiblePackageList.value.length) {
    currentPackageIndex.value = Math.max(0, visiblePackageList.value.length - 1)
  }
}

const removeProductWithSync = (pkgIndex: number, productIndex: number) => {
  const pkg = packageList.value[pkgIndex]
  if (!pkg || !pkg.products) return

  const product = pkg.products[productIndex]
  if (!product) return

  pkg.products.splice(productIndex, 1)
  ElMessage.success('已删除')
}

/**
 * 验证实际打包数量不能超过仓库数量
 */
const validateActualCount = (row: any) => {
  const normalizedCategoryId = normalizeCategoryId(row.categorySpecificationItemId)
  const rowOrderNo = row.orderNo ?? ''

  // 优先从 orderList 取该订单对应规格的实际仓库数量，避免加载后 enterNumber 被设为全单汇总值
  let warehouseCount = Number(row.enterNumber || 0)
  if (rowOrderNo) {
    const order = orderList.value.find((o: any) => o.orderCode === rowOrderNo)
    if (order?.products?.length) {
      const orderSpec = (order.products as any[]).find(
        (p: any) =>
          p.specificationId === row.specificationId &&
          normalizeCategoryId(p.categorySpecificationItemId) === normalizedCategoryId
      )
      if (orderSpec) {
        warehouseCount = Number(orderSpec.enterNumber || orderSpec.number || 0)
      }
    }
  }

  const currentActualCount = Number(row.actualCount || 0)

  let otherPackagesTotal = 0
  packageList.value.forEach((pkg: any) => {
    if (pkg.isDeleted) return
    ;(pkg.products || []).forEach((product: any) => {
      if (
        product !== row &&
        product.specificationId === row.specificationId &&
        normalizeCategoryId(product.categorySpecificationItemId) === normalizedCategoryId &&
        (rowOrderNo === '' || product.orderNo === rowOrderNo)
      ) {
        otherPackagesTotal += Number(product.actualCount || 0)
      }
    })
  })

  const maxCurrentCount = Math.max(warehouseCount - otherPackagesTotal, 0)

  if (currentActualCount > maxCurrentCount) {
    ElMessage.warning(`该产品在所有包裹中的实际打包总数不能超过仓库数量${warehouseCount}`)
    row.actualCount = maxCurrentCount
  }
}

const getNextBoxCode = (): string => {
  const maxNum = packageList.value.reduce((max: number, pkg: any) => {
    const num = parseInt(pkg.boxCode) || 0
    return num > max ? num : max
  }, 0)
  return String(maxNum + 1)
}

const fetchPackageList = async () => {
  const { code, data, message } = await getPackageList({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)
  if (data && Array.isArray(data) && data.length > 0) {
    packageList.value = await Promise.all(
      data.map(async (pkg: any) => {
        const products = await fetchPackageItems(pkg.id)
        return {
          ...pkg,
          id: pkg.id,
          boxCode: pkg.boxCode ?? getNextBoxCode(),
          boxSize: pkg.boxSize ?? '',
          boxWeight: pkg.boxWeight ?? '',
          products,
          isNew: false,
          isDeleted: 0
        }
      })
    )
  } else {
    packageList.value = [
      { id: String(Date.now()), boxCode: '1', boxName: '', boxSize: '', boxWeight: '', products: [], isNew: true }
    ]
  }
}

const fetchPackageItems = async (deliveryBoxId: string) => {
  try {
    const { code, data, message } = await getPackageItemList({ id: rowData.id, deliveryBoxId })
    if (code !== 200) {
      ElMessage.warning(message)
      return []
    }
    if (!data || !Array.isArray(data) || data.length === 0) return []

    const detailResult = await getDeliveryDetail({ id: rowData.id })
    if (detailResult.code !== 200) return []
    const deliveryItemMap = new Map<string, any>()
    if (detailResult.data && detailResult.data.itemList) {
      detailResult.data.itemList.forEach((item: any) => {
        const key = `${item.specificationId}_${normalizeCategoryId(item.categorySpecificationItemId)}`
        deliveryItemMap.set(key, { packCount: item.number || 0, enterNumber: item.enterNumber || 0 })
      })
    }

    return data.map((item: any) => {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
        .filter((v: string) => v)
        .join('-')
      const specSegments = buildSpecSegments(specItems, specName)
      const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''
      const key = `${item.specificationId}_${normalizeCategoryId(item.categorySpecificationItemId)}`
      const deliveryItem = deliveryItemMap.get(key) || { packCount: 0, enterNumber: 0 }
      return {
        id: item.id || null,
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: specName || '-',
        specSegments,
        image: imageUrl,
        locationName: item.locationName || '-',
        actualCount: item.number || 0,
        packCount: deliveryItem.packCount,
        enterNumber: deliveryItem.enterNumber,
        description: item.description || '',
        orderNo: item.orderNo || ''
      }
    })
  } catch {
    return []
  }
}

const handleScan = async () => {
  if (!scanCode.value) {
    ElMessage.warning('请输入扫码内容')
    return
  }
  const currentPkg = currentPackage.value
  if (!currentPkg || !currentPkg.id) {
    ElMessage.warning('请先选择包裹')
    return
  }

  let specificationId = '',
    categorySpecificationItemId = ''
  if (scanCode.value.includes('?')) {
    const params = new URLSearchParams(scanCode.value.split('?')[1])
    specificationId = params.get('specificationId') || ''
    categorySpecificationItemId = params.get('categorySpecificationItemId') || ''
  } else {
    specificationId = scanCode.value
  }

  const { data } = await deliveryScan({ id: rowData.id, specificationId, categorySpecificationItemId })
  if (data && Array.isArray(data) && data.length > 0) {
    const item = data[0]
    const specItems = item.itemList || []
    const specName = specItems
      .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
      .filter((v: string) => v)
      .join('-')
    const specSegments = buildSpecSegments(specItems, specName)
    const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''
    const realPkgIndex = packageList.value.findIndex((p: any) => p.id === currentPkg.id)
    if (realPkgIndex === -1) return
    const realPkg = packageList.value[realPkgIndex]
    if (!realPkg.products) realPkg.products = []
    const existingProduct = realPkg.products.find((p: any) => p.specificationId === item.specificationId)
    if (existingProduct) {
      existingProduct.actualCount = Number(existingProduct.actualCount) + 1
    } else {
      realPkg.products.push({
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: specName || '-',
        specSegments,
        image: imageUrl,
        locationName: item.locationName || '-',
        actualCount: item.number || 0,
        packCount: item.number || 0,
        enterNumber: item.enterNumber || 0,
        description: item.description || ''
      })
    }
    ElMessage.success('扫码成功')
    scanCode.value = ''
  } else {
    ElMessage.warning('未找到对应产品')
  }
}

const handlePrint = () => {
  const params = {
    id: 'printPreviewDialog',
    el: '#app',
    data: {
      rowData: rowData,
      printType: viewType.value === 'order' ? 'order' : 'product',
      selectedOrderId: viewType.value === 'order' ? currentOrderId.value : null,
      selectedOrderCode: viewType.value === 'order' ? currentOrder.value.orderCode ?? '' : ''
    },
    render: PrintPreviewDialog
  }
  dynamic.show(params)
}

/**
 * 判断所有包裹行中是否存在未满足发货条件（红色）的行
 */
const hasRedPackageStatus = computed(() => {
  return visiblePackageList.value.some((pkg: any) =>
    (pkg.products || []).some((row: any) => row.orderNo && !getOrderShippingConditionMet(row))
  )
})

/**
 * 判断所有包裹行是否全部满足发货条件（全绿）
 */
const allPackageGreen = computed(() => {
  const allRows = visiblePackageList.value.flatMap((pkg: any) => (pkg.products || []).filter((row: any) => row.orderNo))
  return allRows.length === 0 || allRows.every((row: any) => getOrderShippingConditionMet(row))
})

const handleNotifyBusiness = async () => {
  // 收集包裹中状态列为红色的订单号（去重）
  const redOrderCodes = new Set<string>()
  visiblePackageList.value.forEach((pkg: any) => {
    ;(pkg.products || []).forEach((row: any) => {
      if (row.orderNo && !getOrderShippingConditionMet(row)) {
        redOrderCodes.add(row.orderNo)
      }
    })
  })
  if (redOrderCodes.size === 0) {
    ElMessage.warning('没有需要通知的订单')
    return
  }
  const { code, message } = await sendPackageMessage({
    deliveryId: rowData.id,
    orderCodes: Array.from(redOrderCodes)
  })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
}

const handleSave = async (closeAfter = true): Promise<boolean> => {
  if (visiblePackageList.value.length === 0) {
    ElMessage.warning('请至少添加一个包裹')
    return false
  }
  // 校验所有包裹中同一规格的实际打包总数不能超过仓库数量
  const specTotals = new Map<string, { total: number; enterNumber: number; productCode: string; specName: string }>()
  visiblePackageList.value.forEach((pkg: any) => {
    ;(pkg.products || []).forEach((product: any) => {
      const orderNo = product.orderNo ?? ''
      const key = `${product.specificationId}_${normalizeCategoryId(product.categorySpecificationItemId)}_${orderNo}`
      if (!specTotals.has(key)) {
        specTotals.set(key, {
          total: 0,
          enterNumber: Number(product.enterNumber || 0),
          productCode: product.productCode || '',
          specName: product.specName || ''
        })
      }
      specTotals.get(key)!.total += Number(product.actualCount || 0)
    })
  })
  for (const [, v] of specTotals) {
    if (v.total > v.enterNumber) {
      ElMessage.warning(
        `产品${v.productCode} 规格${v.specName} 实际打包总数(${v.total})超过仓库数量(${v.enterNumber})，请修正后再暂存`
      )
      return false
    }
  }
  const packageData = packageList.value.map((pkg: any) => {
    const boxItemList = (pkg.products || []).map((product: any) => ({
      id: product.id || null,
      specificationId: product.specificationId,
      locationId: product.locationId || null,
      number: product.actualCount || 0,
      productId: product.productId,
      categorySpecificationItemId: product.categorySpecificationItemId || '',
      categorySpecificationItemName: product.categorySpecificationItemName || '',
      orderNo: product.orderNo || ''
    }))
    const d: any = {
      deliveryId: rowData.id,
      boxId: pkg.boxId || null,
      boxCode: pkg.boxCode,
      boxWeight: pkg.boxWeight ?? '',
      boxItemList,
      boxSize: pkg.boxSize ?? ''
    }
    if (!pkg.isNew && pkg.id) d.id = pkg.id
    if (!pkg.isNew && pkg.isDeleted) d.isDeleted = 1
    return d
  })
  // return console.log('packageData', packageData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await savePackage(packageData)
  if (code !== 200) {
    ElMessage.warning(message)
    return false
  }
  if (closeAfter) {
    ElMessage.success('暂存成功')
    dialogVisible.value = false
    if (callback) callback()
  }
  return true
}

const handleComplete = async () => {
  if (completeSubmitting.value) return
  const activePackages = visiblePackageList.value
  if (activePackages.length === 0) {
    ElMessage.warning('请至少添加一个包裹')
    return
  }
  for (let i = 0; i < activePackages.length; i++) {
    if (!activePackages[i].boxCode) {
      ElMessage.warning(`请为包裹${i + 1}输入箱号`)
      return
    }
    if (!activePackages[i].products || activePackages[i].products.length === 0) {
      ElMessage.warning(`请为包裹${i + 1}添加产品`)
      return
    }
  }

  // 先暂存，再直接提交完成
  completeSubmitting.value = true
  try {
    const saved = await handleSave(false)
    if (!saved) return

    await submitPackage()
  } finally {
    completeSubmitting.value = false
  }
}

const submitPackage = async () => {
  const packageData = packageList.value.map((pkg: any) => {
    const boxItemList = (pkg.products || []).map((product: any) => ({
      id: product.id || null,
      specificationId: product.specificationId,
      locationId: product.locationId || null,
      number: product.actualCount || 0,
      productId: product.productId,
      categorySpecificationItemId: product.categorySpecificationItemId || '',
      categorySpecificationItemName: product.categorySpecificationItemName || '',
      orderNo: product.orderNo || ''
    }))
    const d: any = {
      deliveryId: rowData.id,
      boxId: pkg.boxId || null,
      boxCode: pkg.boxCode,
      boxWeight: pkg.boxWeight ?? '',
      boxItemList,
      boxSize: pkg.boxSize ?? ''
    }
    if (!pkg.isNew && pkg.id) d.id = pkg.id
    if (!pkg.isNew && pkg.isDeleted) d.isDeleted = 1
    return d
  })
  const { code, message } = await savePackage(packageData)
  if (code !== 200) return ElMessage.warning(message)
  const completeResult = await completePackage({
    deliveryId: rowData.id
  })
  if (completeResult.code !== 200) return ElMessage.warning(completeResult.message)
  ElMessage.success('打包完成')
  dialogVisible.value = false
  if (callback) callback()
}

onMounted(async () => {
  await loadBaseData()
})
</script>

<style lang="scss" scoped>
:deep(.packing-combined-dialog) {
  max-height: 94vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .el-dialog__body {
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }
}

.customer-info {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;
}

.spec-name-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.spec-name-content {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 2px;
}

.spec-name-segment {
  color: #303133;
}

.spec-name-segment--colored {
  display: inline-block;
  padding: 1px 4px;
  border-radius: 4px;
}

.spec-name-separator {
  color: #303133;
}

.combined-content {
  display: flex;
  gap: 0;
  min-height: 0;
  height: 100%;
  max-height: 100%;
  overflow: hidden;
  align-items: stretch;

  .left-panel-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: 52%;
    min-height: 0;
    overflow: hidden;

    .left-panel {
      flex: 1;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      padding: 10px;
      overflow: auto;
      display: flex;
      flex-direction: column;
      min-height: 0;

      .detail-header {
        padding: 0 0 12px;
        font-size: 14px;
        color: #606266;
      }

      .order-status-bar {
        display: flex;
        gap: 20px;
        padding: 8px 0 12px;
        border-bottom: 1px solid #ebeef5;
        margin-bottom: 12px;

        .status-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #606266;
        }
      }

      .panel-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 12px;
      }

      .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .panel-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 0;
        }

        .update-time {
          font-size: 13px;
          color: #909399;
        }
      }

      .search-wrapper {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
      }

      .tabs-wrapper {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .tabs-left {
          display: flex;
          align-items: center;
        }

        .tabs-right {
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }

      .order-view {
        display: flex;
        gap: 10px;
        flex: 1;
        min-height: 0;

        .order-list {
          width: 120px;
          flex-shrink: 0;
          overflow-y: auto;

          .order-item {
            padding: 6px 5px;
            border-left: 3px solid transparent;
            cursor: pointer;
            margin-bottom: 4px;

            &.active {
              border-left-color: #409eff;
              background-color: #f5f7fa;
            }

            .order-label {
              font-size: 14px;
              font-weight: 500;
              color: #409eff;
              display: flex;
              align-items: center;
              .status-dot {
                width: 10px;
                height: 10px;
                border-radius: 50%;
                display: block;
                margin-right: 5px;

                &.complete {
                  background-color: #67c23a;
                }

                &.incomplete {
                  background-color: #f56c6c;
                }
              }
            }

            .order-code {
              margin-top: 5px;
              font-size: 13px;
              color: #909399;
            }
          }
        }

        .order-detail-wrapper {
          flex: 1;
          min-height: 0;

          .order-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 14px;
            color: #606266;
            padding: 8px 0;

            .order-info-left {
              display: flex;
              flex: 1;
              min-width: 0;
              flex-direction: column;
              gap: 6px;

              .order-info-base {
                display: flex;
                gap: 20px;
                flex-wrap: wrap;
              }

              .order-info-remark {
                display: flex;
                align-items: center;
                min-width: 0;

                .remark-label {
                  flex-shrink: 0;
                }

                .remark-text {
                  min-width: 0;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }
            }

            .order-info-right {
              display: flex;
              flex-shrink: 0;
              gap: 10px;

              .status-item {
                display: flex;
                align-items: center;
                gap: 6px;
              }
            }
          }
        }
      }
    }

    .left-panel-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 16px;
      padding-right: 0;
    }
  }

  .transfer-buttons {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 12px;

    .transfer-button {
      width: 48px;
      height: 120px;
      border-radius: 8px;
      padding: 0;
    }
  }

  .right-panel-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 0;
    overflow: hidden;

    .right-panel {
      flex: 1;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      padding: 10px;
      overflow: auto;
      display: flex;
      flex-direction: column;
      min-height: 0;

      .detail-header {
        padding: 0 0 12px;
        font-size: 14px;
        color: #606266;
      }

      .panel-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 12px;
      }

      .packing-content {
        display: flex;
        gap: 10px;
        flex: 1;
        min-height: 0;

        .package-sidebar {
          width: 120px;
          flex-shrink: 0;
          overflow-y: auto;
          max-height: 500px;

          .package-card {
            position: relative;
            padding: 10px;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            margin-bottom: 10px;
            cursor: pointer;

            &.active {
              border-color: #409eff;
              background-color: #ecf5ff;
            }

            .close-icon {
              position: absolute;
              top: 4px;
              right: 4px;
              font-size: 12px;
              color: #909399;
              cursor: pointer;

              &:hover {
                color: #f56c6c;
              }
            }

            .package-label {
              font-size: 14px;
              font-weight: 500;
              color: #409eff;
            }
          }
        }

        .package-main {
          flex: 1;

          .scan-area {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px;
            border: 2px dashed #409eff;
            border-radius: 8px;
            margin-bottom: 12px;
            background-color: #f5f7fa;

            .scan-input {
              flex: 1;
            }
          }
        }
      }
    }

    .right-panel-footer {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
