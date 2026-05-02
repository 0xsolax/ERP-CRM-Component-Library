<template>
  <el-dialog v-model="dialogVisible" title="打包详情" width="1200" @close="onDestroy">
    <div class="detail-header">
      <span>客户名称：{{ customerName }}</span>
      <span style="margin-left: 40px">{{ status == 0 ? '最新更新时间' : '打包完成时间' }}：{{ updateTime }}</span>
    </div>

    <div class="tabs-wrapper">
      <el-radio-group v-model="viewType">
        <!-- 待打包 -->
        <template v-if="status == 0">
          <el-radio-button value="product">按产品分类</el-radio-button>
          <el-radio-button value="order">按订单分类</el-radio-button>
          <el-radio-button value="recommend">按推荐包裹</el-radio-button>
        </template>
        <!-- 待发货/已发货 -->
        <template v-else>
          <el-radio-button value="package">按打包包裹</el-radio-button>
          <el-radio-button value="product">按产品分类</el-radio-button>
          <el-radio-button value="order">按订单分类</el-radio-button>
        </template>
      </el-radio-group>
    </div>

    <!-- 按打包包裹（待发货/已发货） -->
    <div v-if="viewType === 'package'" class="package-view">
      <div class="package-list">
        <div
          v-for="(pkg, index) in packageList"
          :key="pkg.id"
          :class="['package-item', { active: currentPackageIndex === index }]"
          @click="handlePackageChange(index)"
        >
          <div class="package-label">包裹{{ index + 1 }}</div>
          <div class="package-size">{{ pkg.boxCode }}</div>
        </div>
      </div>
      <div class="package-detail-wrapper">
        <div class="package-info">
          <span>包裹重量：{{ currentPackage.weight || '-' }}kg</span>
          <span style="margin-left: 10px">打包箱尺寸：{{ currentPackage.boxSize || '-' }}</span>
        </div>
        <el-table :data="currentPackage.products" :span-method="productSpanMethod" max-height="500" border>
          <el-table-column label="产品ID" prop="productCode" width="100" align="center" />
          <el-table-column label="本次发货总数" prop="totalCount" align="center" />
          <el-table-column label="规格名称" align="center">
            <template #default="{ row }">
              <el-tooltip v-if="row.description" :content="row.description" placement="top">
                <div>{{ row.specName }}</div>
              </el-tooltip>
              <div v-else>{{ row.specName }}</div>
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
          <el-table-column label="本次发货数量" prop="shipCount" align="center" />
          <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
            <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 按产品分类 -->
    <el-table
      v-if="viewType === 'product'"
      :data="productList"
      :span-method="productSpanMethod"
      max-height="500"
      border
    >
      <el-table-column label="产品ID" prop="productCode" width="150" align="center" />
      <el-table-column label="本次发货总数" prop="totalCount" align="center" />
      <el-table-column label="规格名称" align="center">
        <template #default="{ row }">
          <el-tooltip v-if="row.description" :content="row.description" placement="top">
            <div>{{ row.specName }}</div>
          </el-tooltip>
          <div v-else>{{ row.specName }}</div>
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
          v-for="(order, index) in orderList"
          :key="order.id"
          :class="['order-item', { active: currentOrderIndex === index }]"
          @click="handleOrderChange(index)"
        >
          <div class="order-label">订单{{ index + 1 }}</div>
          <div class="order-code">{{ order.orderCode }}</div>
        </div>
      </div>
      <div class="order-detail-wrapper">
        <div class="order-info">
          <div class="order-info-left">
            <span>订单号：{{ currentOrder.orderCode }}</span>
            <span style="margin-left: 40px">发货形式：{{ currentOrder.shippingMethodText }}</span>
            <span style="margin-left: 40px">订单备注：{{ currentOrder.orderRemark }}</span>
          </div>
          <!-- <div class="order-info-right">
            <div class="status-item">
              <el-icon :size="16" :color="currentOrder.productQuantityStatusIcon === 'success' ? '#67C23A' : '#F56C6C'">
                <CircleCheck v-if="currentOrder.productQuantityStatusIcon === 'success'" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ currentOrder.productQuantityStatusText }}</span>
            </div>
            <div class="status-item">
              <el-icon :size="16" :color="currentOrder.packingStatusIcon === 'success' ? '#67C23A' : '#F56C6C'">
                <CircleCheck v-if="currentOrder.packingStatusIcon === 'success'" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ currentOrder.packingStatusText }}</span>
            </div>
          </div> -->
        </div>
        <el-table :data="currentOrder.products" :span-method="productSpanMethod" max-height="500" border>
          <el-table-column label="产品ID" prop="productCode" align="center" />
          <el-table-column label="本次发货总数" prop="totalCount" align="center" />
          <el-table-column label="规格名称" align="center">
            <template #default="{ row }">
              <el-tooltip v-if="row.description" :content="row.description" placement="top">
                <div>{{ row.specName }}</div>
              </el-tooltip>
              <div v-else>{{ row.specName }}</div>
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

    <!-- 按推荐包裹（待打包） -->
    <div v-if="viewType === 'recommend'" class="package-view">
      <div class="package-list">
        <div
          v-for="(pkg, index) in recommendPackageList"
          :key="pkg.id"
          :class="['package-item', { active: currentRecommendPackageIndex === index }]"
          @click="currentRecommendPackageIndex = index"
        >
          <div class="package-label">包裹{{ index + 1 }}</div>
          <div class="package-size">{{ pkg.size }}</div>
        </div>
      </div>
      <el-table :data="currentRecommendPackage.products" :span-method="productSpanMethod" max-height="500" border>
        <el-table-column label="产品ID" prop="productCode" width="100" align="center" />
        <el-table-column label="本次发货总数" prop="totalCount" align="center" />
        <el-table-column label="规格名称" align="center">
          <template #default="{ row }">
            <el-tooltip v-if="row.description" :content="row.description" placement="top">
              <div>{{ row.specName }}</div>
            </el-tooltip>
            <div v-else>{{ row.specName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="规格图片" align="center">
          <template #default="{ row }">
            <el-image v-if="row.specImage" :src="row.specImage" fit="cover" style="width: 40px; height: 40px" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="库位" prop="location" align="center" />
        <el-table-column label="本次发货数量" prop="shipCount" align="center" />
        <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
          <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer v-if="status == 0">
      <el-button type="primary" @click="handlePrint">打印</el-button>
      <el-button type="primary" @click="handlePackageComplete">打包完成</el-button>
      <el-button type="primary" @click="handleStartPacking">开始打包</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, watch } from 'vue'
// import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage } from 'element-plus'
import {
  getDeliveryDetail,
  getDeliveryDetailOrder,
  getPackageList,
  getPackageItemList,
  completePackage
} from '@/api/admin/warehouse'
import { getDeliveryTypeLabel } from '@/constant/yitang/sales'
import StartPackingDialog from './start-packing-dialog.vue'
import PrintPreviewDialog from './print-preview-dialog.vue'
import OrderConfirmDialog from './order-confirm-dialog.vue'

const attrs = useAttrs()
const { rowData, status: tabStatus, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const status = ref(tabStatus ?? 0) // 0: 待打包, 1: 待发货, 2: 已发货
const viewType = ref(status.value == 0 ? 'product' : 'package')
const currentOrderIndex = ref(0)
const currentPackageIndex = ref(0)
const currentRecommendPackageIndex = ref(0)
const loading = ref(false)

console.log('viewType', viewType)

const customerName = ref(rowData?.customerName || '')
const updateTime = ref(
  status.value == 0 ? rowData?.originalData?.updateTime || '' : rowData?.originalData?.packageTime || ''
)
const detailData = ref<any>({})
const orderList = ref<any[]>([])

const productList = ref<any[]>([])

const currentOrder = computed(() => {
  if (orderList.value.length > 0 && currentOrderIndex.value < orderList.value.length) {
    return orderList.value[currentOrderIndex.value]
  }
  return { products: [] }
})

// 按打包包裹（待发货/已发货）
const packageList = ref<any[]>([])

const currentPackage = computed(() => packageList.value[currentPackageIndex.value] || { products: [] })

// 按推荐包裹（待打包）
const recommendPackageList = ref([{ id: '1', size: '100*80*40', products: [] }])

const currentRecommendPackage = computed(
  () => recommendPackageList.value[currentRecommendPackageIndex.value] || { products: [] }
)

const productSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 1) {
    if (row.rowspan > 0) {
      return { rowspan: row.rowspan, colspan: 1 }
    } else if (row.rowspan === 0) {
      return { rowspan: 0, colspan: 0 }
    }
  }
  return { rowspan: 1, colspan: 1 }
}

const handlePrint = () => {
  const params = {
    id: 'printPreviewDialog',
    el: '#app',
    data: {
      rowData: rowData,
      printType: viewType.value === 'order' ? 'order' : 'product'
    },
    render: PrintPreviewDialog
  }
  dynamic.show(params)
}

const handlePackageComplete = () => {
  const params = {
    id: 'orderConfirmDialog',
    el: '#app',
    data: {
      deliveryId: rowData.id,
      rowData: rowData,
      onConfirm: async (selectedRows: any[]) => {
        const completeData = {
          deliveryId: rowData.id,
          orderIdList: selectedRows.map((row: any) => row.orderId)
        }

        const { code, message } = await completePackage(completeData)
        if (code !== 200) return ElMessage.warning(message)

        ElMessage.success('打包完成')
        dialogVisible.value = false
        if (callback) callback()
      }
    },
    render: OrderConfirmDialog
  }
  dynamic.show(params)
}

const handleStartPacking = () => {
  const params = {
    id: 'startPackingDialog',
    el: '#app',
    data: {
      rowData: rowData,
      callback: () => {
        dialogVisible.value = false
        if (callback) callback()
      }
    },
    render: StartPackingDialog
  }
  dynamic.show(params)
}

const processItemListToProducts = (itemList: any[]) => {
  const processedProducts: any[] = []
  const productGroups: { [key: string]: any[] } = {}
  itemList.forEach((item: any) => {
    const productCode = item.productCode || ''
    if (!productGroups[productCode]) {
      productGroups[productCode] = []
    }
    productGroups[productCode].push(item)
  })

  Object.values(productGroups).forEach((group: any[]) => {
    const groupSize = group.length
    let totalCount = 0

    group.forEach((item: any, index: number) => {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
        .filter((v: string) => v)
        .join('-')

      const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''

      if (index === 0) {
        totalCount = group.reduce((sum, g) => sum + (g.number || 0), 0)
      }

      processedProducts.push({
        productCode: item.productCode || '',
        totalCount: totalCount,
        specName: specName || '-',
        specImage: imageUrl,
        location: item.locationName || '-',
        shipCount: item.number || 0,
        categorySpecificationItemName: item.categorySpecificationItemName || '',
        shippedCount: item.shippedNumber || 0,
        totalQty: item.number || 0,
        progressPercent: item.number > 0 ? Math.round(((item.shippedNumber || 0) / item.number) * 100) : 0,
        rowspan: index === 0 ? groupSize : 0,
        number: item.number || 0, // 要求数量
        enterNumber: item.enterNumber || 0, // 仓库数量
        packageNumber: item.packageNumber || 0 // 实际打包数量
      })
    })
  })

  return processedProducts
}

const fetchProductDetail = async () => {
  loading.value = true
  try {
    const { data } = await getDeliveryDetail({
      id: rowData.id,
      orderId: rowData.originalData?.orderId || null
    })

    if (data) {
      detailData.value = data
      if (data.itemList && data.itemList.length > 0) {
        productList.value = processItemListToProducts(data.itemList)
      }
    }
  } finally {
    loading.value = false
  }
}

// 获取订单子单列表
const fetchOrderSubList = async () => {
  loading.value = true
  try {
    const { data } = await getDeliveryDetailOrder({
      id: rowData.id
    })

    console.log('fetchOrderSubList', data)

    if (data && Array.isArray(data)) {
      orderList.value = data.map((item: any, index: number) => ({
        id: item.orderId || String(index + 1),
        orderCode: item.orderCode ?? '',
        products: []
      }))

      if (orderList.value.length > 0) {
        currentOrderIndex.value = 0
        await fetchOrderDetail(orderList.value[0].id)
      }
    }
  } finally {
    loading.value = false
  }
}

// 获取指定订单的详情
const fetchOrderDetail = async (orderId: string) => {
  loading.value = true
  try {
    const { data } = await getDeliveryDetail({
      id: rowData.id,
      orderId: orderId
    })

    if (data) {
      const currentOrder = orderList.value[currentOrderIndex.value]
      currentOrder.shippingMethodText = getDeliveryTypeLabel(data.orderShippingMethod) || '-'
      currentOrder.orderRemark = data.orderRemark ?? ''
      currentOrder.productQuantityStatus = data.orderProductComplete ? 1 : 0
      currentOrder.packingStatus = data.orderPackageComplete ? 1 : 0
      currentOrder.productQuantityStatusText = data.orderProductComplete ? '产品齐全' : '产品不全'
      currentOrder.packingStatusText = data.orderPackageComplete ? '打包齐全' : '打包不全'
      currentOrder.productQuantityStatusIcon = data.orderProductComplete ? 'success' : 'error'
      currentOrder.packingStatusIcon = data.orderPackageComplete ? 'success' : 'error'
      if (data.itemList && data.itemList.length > 0) {
        const products = processItemListToProducts(data.itemList)
        if (currentOrder) {
          currentOrder.products = products
        }
      }
    }
  } finally {
    loading.value = false
  }
}

const handleOrderChange = async (index: number) => {
  currentOrderIndex.value = index
  const order = orderList.value[index]
  if (order && order.id) {
    await fetchOrderDetail(order.id)
  }
}

// 获取打包包裹列表（待发货/已发货）
const fetchPackageList = async () => {
  loading.value = true
  try {
    const { data } = await getPackageList({ id: rowData.id })
    if (data && Array.isArray(data)) {
      packageList.value = await Promise.all(
        data.map(async (pkg: any) => {
          const products = await fetchPackageItems(pkg.id)
          return {
            id: pkg.id,
            boxCode: pkg.boxCode || '',
            boxSize: pkg.boxSize || '',
            weight: pkg.boxWeight || '',
            products: products
          }
        })
      )
      if (packageList.value.length > 0) {
        currentPackageIndex.value = 0
      }
    }
  } finally {
    loading.value = false
  }
}

// 获取包裹内产品列表
const fetchPackageItems = async (deliveryBoxId: string) => {
  try {
    const { data } = await getPackageItemList({ id: rowData.id, deliveryBoxId })
    if (data && Array.isArray(data)) {
      return processItemListToProducts(data)
    }
    return []
  } catch {
    return []
  }
}

// 切换包裹
const handlePackageChange = async (index: number) => {
  currentPackageIndex.value = index
}

watch(
  viewType,
  newType => {
    console.log('newType', newType)
    if (newType === 'product') {
      fetchProductDetail()
    } else if (newType === 'order') {
      fetchOrderSubList()
    } else if (newType === 'package') {
      fetchPackageList()
    }
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.detail-header {
  padding: 10px 0 20px;
  font-size: 14px;
  color: #606266;
}

.tabs-wrapper {
  margin-bottom: 16px;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 10px;

  .el-progress {
    flex: 1;
  }
}

.order-view {
  display: flex;
  gap: 10px;

  .order-detail-wrapper {
    flex: 1;
  }

  .order-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    font-size: 13px;
    color: #606266;

    .order-info-left {
      display: flex;
      align-items: center;
    }

    .order-info-right {
      display: flex;
      align-items: center;
      gap: 16px;

      .status-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
      }
    }
  }

  .order-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;
    width: 142px;

    .order-item {
      padding: 8px 12px;
      border-left: 3px solid transparent;
      cursor: pointer;

      &.active {
        border-left-color: #409eff;
        background-color: #f5f7fa;
      }

      .order-label {
        font-size: 14px;
        font-weight: 500;
        color: #409eff;
      }

      .order-code {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.package-view {
  display: flex;
  gap: 10px;

  .package-detail-wrapper {
    flex: 1;
  }

  .package-info {
    padding: 10px 0;
    font-size: 13px;
    color: #606266;
  }

  .package-list {
    width: 100px;
    flex-shrink: 0;

    .package-item {
      padding: 8px 12px;
      border-left: 3px solid transparent;
      cursor: pointer;
      margin-bottom: 8px;

      &.active {
        border-left-color: #409eff;
        background-color: #f5f7fa;
      }

      .package-label {
        font-size: 14px;
        font-weight: 500;
        color: #409eff;
      }

      .package-size {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>
