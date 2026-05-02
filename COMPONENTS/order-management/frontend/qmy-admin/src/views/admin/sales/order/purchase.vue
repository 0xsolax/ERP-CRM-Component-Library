<template>
  <div class="purchase-apply-container">
    <h2 class="page-title">申请采购</h2>

    <div v-for="(order, orderIndex) in orderDetails" :key="orderIndex" class="order-detail-card">
      <div class="card-header">
        <div class="section-title">订单详情</div>
      </div>

      <div class="form-section">
        <el-form :model="order" label-width="120px" label-position="top" class="purchase-form">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="订单类型" required>
                <el-select v-model="order.orderType" placeholder="请选择" disabled>
                  <el-option v-for="item in orderTypeList" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="product-section">
        <el-table
          :data="order.tableData"
          style="width: 100%"
          border
          :span-method="({ row, columnIndex }) => spanMethod({ row, columnIndex, order })"
          :cell-class-name="({ row, columnIndex }) => getCellClassName({ row, columnIndex, order })"
        >
          <el-table-column label="产品ID" width="150" align="center">
            <template #default="{ row }">
              <span>{{ row.productCode }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center">
            <template #header>
              <span>{{ order.orderType === '1' ? '产品图片' : '规格图片' }}</span>
            </template>
            <template #default="{ row }">
              <div class="product-image">
                <el-image v-if="row.specImage" v-image-preview="row.specImage" :src="row.specImage" fit="cover" />
                <span v-else>-</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column v-if="order.orderType !== '1'" label="规格名称" align="center">
            <template #default="{ row }">
              <span>{{ row.specName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" align="center">
            <template #default="{ row }">
              <span>{{ row.labelName || '/' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" align="center">
            <template #default="{ row }">
              <span>{{ row.number }}</span>
            </template>
          </el-table-column>
          <el-table-column label="可用库存" width="240" align="center">
            <template #default="{ row }">
              <div v-if="row.isCustomerStore" class="stock-warning">独立仓产品无需申购</div>
              <div v-else-if="row.isIncompleteProduct" class="stock-warning">半成品自动全量加入待采购列表</div>
              <div v-else-if="order.isSupplierDelivery" class="stock-warning">供应商发货自动全量加入待采购列表</div>
              <div v-else-if="row.hasCustomLabel" class="stock-warning">定制化属性自动全量加入待采购列表</div>
              <span v-else>{{ row.enabledStore }}</span>
            </template>
          </el-table-column>
          <el-table-column label="占用库存" align="center">
            <template #default="{ row }">
              <el-input
                v-if="!row.isCustomerStore && !row.isIncompleteProduct"
                v-model="order.productList[row.originalIndex].occupyStoreNumber"
                placeholder="请输入"
                @input="validateInteger(order.productList[row.originalIndex], 'occupyStoreNumber')"
                @blur="validateOccupyStore(order.productList[row.originalIndex])"
              />
            </template>
          </el-table-column>
          <el-table-column label="可用在途" align="center">
            <template #default="{ row }">
              <span v-if="!row.isCustomerStore && !row.isIncompleteProduct">{{ row.enabledTransit }}</span>
            </template>
          </el-table-column>
          <el-table-column label="占用在途" align="center">
            <template #default="{ row }">
              <el-input
                v-if="!row.isCustomerStore && !row.isIncompleteProduct"
                v-model="order.productList[row.originalIndex].occupyTransitNumber"
                placeholder="请输入"
                @input="validateInteger(order.productList[row.originalIndex], 'occupyTransitNumber')"
                @blur="validateOccupyTransit(order.productList[row.originalIndex])"
              />
            </template>
          </el-table-column>
          <el-table-column label="申购数量" align="center">
            <template #default="{ row }">
              <el-input
                v-if="!row.isCustomerStore && !row.isIncompleteProduct"
                placeholder="请输入"
                :model-value="calcPurchaseNumber(order.productList[row.originalIndex])"
                disabled
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <footer-actions>
      <div class="summary-info">
        <span style="margin-left: 20px">
          产品共计：
          <strong>{{ totalProducts }}件</strong>
        </span>
        <span>
          申购数量：
          <strong>{{ totalPurchaseQuantity }}件</strong>
        </span>
      </div>
      <div class="action-buttons">
        <el-button @click="handlePrevious">上一步</el-button>
        <el-button :loading="saveDraftLoading" @click="handleSaveDraft">暂存</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </div>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import FooterActions from '@/components/footer-actions/index.vue'
import { getOrderDetail, applyPurchase } from '@/api/admin/sales/order'
import { orderTypeList } from '@/constant/yitang/sales'
import { useTagsStore } from '@/views/admin/store/modules/tags'

const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()

const orderId = ref<number | undefined>(undefined)
const customerId = ref<number | undefined>(undefined)
const salesEmployeeId = ref<number | undefined>(undefined)
const saveDraftLoading = ref(false)
const submitLoading = ref(false)
const currency = ref<string>('0')

const orderDetails = ref<any[]>([])

const loadOrderDetail = async () => {
  const id = route.query.id
  const { code, data, message } = await getOrderDetail({ id })
  if (code !== 200) return ElMessage.warning(message)

  orderId.value = data.id
  customerId.value = data.customerId
  salesEmployeeId.value = data.saleEmployeeId
  currency.value = data.currency ? String(data.currency) : '0'

  if (data.subOrderList && Array.isArray(data.subOrderList) && data.subOrderList.length > 0) {
    orderDetails.value = data.subOrderList.map((subOrder: any) => {
      const orderDetail: any = {
        id: subOrder.id,
        orderType: subOrder.orderType ? String(subOrder.orderType) : '',
        productList: [],
        tableData: []
      }

      if (subOrder.itemList && subOrder.itemList.length > 0) {
        orderDetail.productList = subOrder.itemList.map((item: any) => {
          const specName =
            item.itemList
              ?.map(
                (spec: any) => `${spec.categorySpecificationName || ''}:${spec.categorySpecificationItemValue || ''}`
              )
              .join(' ') || ''
          const specImage = item.imageList?.[0]?.url || ''

          return {
            productId: item.productId || '',
            productCode: item.productCode || '',
            productSpecificationId: item.specificationId || '',
            supplierId: item.supplierId || '',
            categoryLabelId: item.labelId || '',
            labelName: item.labelName ?? '',
            hasCustomLabel: !!(item.labelName && item.labelId && item.labelId !== '0'),
            orderSubItemId: item.id || '',
            specImage,
            specName,
            price: item.price || 0,
            number: item.number || 0,
            supplierPrice: item.supplierPrice || 0,
            status: item.status || '',
            enabledStore: item.enabledStore || 0,
            occupyStoreNumber: item.occupyStoreNumber ? String(item.occupyStoreNumber) : '0',
            enabledTransit: item.enabledTransit || 0,
            occupyTransitNumber: item.occupyTransitNumber ? String(item.occupyTransitNumber) : '0',
            purchaseNumber: '',
            remark: item.remark ?? '',
            isCustomerStore: item.isCustomerStore || false,
            isIncompleteProduct: item.isIncompleteProduct || false
          }
        })

        orderDetail.tableData = generateTableData(orderDetail.productList)
      }

      return orderDetail
    })
  }
}

const generateTableData = (productList: any[]) => {
  const data: any[] = []
  const productGroups: any = {}

  productList.forEach((item, index) => {
    const productKey = String(item.productId)
    if (!productGroups[productKey]) {
      productGroups[productKey] = []
    }
    productGroups[productKey].push({ ...item, originalIndex: index })
  })

  Object.values(productGroups).forEach((group: any) => {
    group.forEach((item: any, idx: number) => {
      data.push({
        ...item,
        isFirstInGroup: idx === 0,
        groupRowSpan: group.length
      })
    })
  })

  return data
}

onMounted(() => {
  loadOrderDetail()
})

const getCellClassName = ({ row, columnIndex, order }: any) => {
  const needMerge = row.isCustomerStore || row.isIncompleteProduct || order.isSupplierDelivery || row.hasCustomLabel
  const isIncomplete = order.orderType === '1'
  const startCol = isIncomplete ? 4 : 5
  const endCol = isIncomplete ? 8 : 9
  if (needMerge && columnIndex >= startCol && columnIndex <= endCol) {
    return 'gray-cell'
  }
  return ''
}

// 合并单元格
const spanMethod = ({ row, columnIndex, order }: any) => {
  if (columnIndex === 0) {
    if (row.isFirstInGroup) {
      return {
        rowspan: row.groupRowSpan,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }

  const needMerge = row.isCustomerStore || row.isIncompleteProduct || order.isSupplierDelivery || row.hasCustomLabel

  if (needMerge) {
    const isIncomplete = order.orderType === '1'
    const mergeStartCol = isIncomplete ? 4 : 5
    const mergeEndCol = isIncomplete ? 8 : 9

    if (columnIndex === mergeStartCol) {
      return {
        rowspan: 1,
        colspan: 5
      }
    }
    if (columnIndex > mergeStartCol && columnIndex <= mergeEndCol) {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
  }
}

const totalProducts = computed(() => {
  return orderDetails.value.reduce((total, order) => {
    return total + order.productList.reduce((sum: number, item: any) => sum + item.number, 0)
  }, 0)
})

const totalPurchaseQuantity = computed(() => {
  return orderDetails.value.reduce((total, order) => {
    return (
      total +
      order.productList.reduce((sum: number, item: any) => {
        // 判断是否需要自动全量加入待采购列表（独立仓、半成品、供应商发货、定制化属性）
        const isAutoPurchase =
          item.isCustomerStore || item.isIncompleteProduct || order.isSupplierDelivery || item.hasCustomLabel
        const purchaseNum = isAutoPurchase ? item.number : calcPurchaseNumber(item)
        return sum + purchaseNum
      }, 0)
    )
  }, 0)
})

// 整数校验
const validateInteger = (row: any, field: string) => {
  const value = row[field]
  if (value && !/^\d+$/.test(value)) {
    row[field] = value.replace(/\D/g, '')
  }
}

// 占用库存不能大于 min(数量, 可用库存)
const validateOccupyStore = (row: any) => {
  const numValue = Number(row.occupyStoreNumber) || 0
  const quantity = Number(row.number) || 0
  const enabledStore = Number(row.enabledStore) || 0
  const maxValue = Math.min(quantity, enabledStore)
  if (numValue > maxValue) {
    row.occupyStoreNumber = String(maxValue)
    ElMessage.warning('占用库存不能大于数量和可用库存的较小值')
  }
}

// 占用在途不能大于 min(数量 - 占用库存, 可用在途)
const validateOccupyTransit = (row: any) => {
  const numValue = Number(row.occupyTransitNumber) || 0
  const quantity = Number(row.number) || 0
  const occupyStore = Number(row.occupyStoreNumber) || 0
  const enabledTransit = Number(row.enabledTransit) || 0
  const remainingQuantity = quantity - occupyStore
  const maxValue = Math.min(remainingQuantity, enabledTransit)
  if (numValue > maxValue) {
    row.occupyTransitNumber = String(Math.max(0, maxValue))
    ElMessage.warning('占用在途不能大于剩余数量和可用在途的较小值')
  }
}

// 计算申购数量：数量 - 占用库存 - 占用在途
const calcPurchaseNumber = (row: any) => {
  const number = Number(row.number) || 0
  const occupyStore = Number(row.occupyStoreNumber) || 0
  const occupyTransit = Number(row.occupyTransitNumber) || 0
  const result = number - occupyStore - occupyTransit
  return result >= 0 ? result : 0
}

const handlePrevious = () => {
  const orderId = route.query.id
  const action = route.query.action
  const path = action === 'add' ? '/sales/order/add' : action === 'edit' ? '/sales/order/edit' : ''
  if (!path) return

  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push({ path, query: { id: orderId, action } })
}

const handleSaveDraft = async () => {
  saveDraftLoading.value = true
  try {
    ElMessage.success('操作成功')
    setTimeout(() => {
      tagsStore.delVisitedView(route)
      tagsStore.delCachedView(route)
      router.push('/sales/order')
    }, 1000)
  } finally {
    saveDraftLoading.value = false
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    const purchaseList: any[] = []
    orderDetails.value.forEach(order => {
      order.productList.forEach((item: any) => {
        const isAutoPurchase =
          item.isCustomerStore || item.isIncompleteProduct || order.isSupplierDelivery || item.hasCustomLabel
        const purchaseNumber = isAutoPurchase ? item.number : calcPurchaseNumber(item)

        purchaseList.push({
          customerId: customerId.value,
          supplierId: item.supplierId,
          salesEmployeeId: salesEmployeeId.value,
          productId: item.productId,
          productSpecificationId: item.productSpecificationId,
          orderId: orderId.value,
          orderSubId: order.id,
          orderSubItemId: item.orderSubItemId,
          categoryLabelId: item.categoryLabelId,
          number: purchaseNumber || 0,
          orderNumber: item.number || 0,
          occupyStore: Number(item.occupyStoreNumber || 0),
          occupyTransit: Number(item.occupyTransitNumber || 0)
        })
      })
    })

    // return console.log(purchaseList)
    // eslint-disable-next-line no-unreachable
    const { code, message } = await applyPurchase(purchaseList)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
    tagsStore.delVisitedView(route)
    tagsStore.delCachedView(route)
    router.push('/sales/order')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.purchase-apply-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 60px);
  padding-bottom: 100px;

  .page-title {
    font-size: 20px;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 15px;
    border-bottom: 1px solid #e5e5e5;
  }

  .order-detail-card {
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    padding: 10px;
    background: #fafafa;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
  }

  .form-section {
    margin-bottom: 20px;

    .section-title {
      font-size: 16px;
      font-weight: bold;
      padding-left: 12px;
      margin-bottom: 20px;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: #409eff;
        border-radius: 2px;
      }
    }

    .purchase-form {
      :deep(.el-form-item) {
        margin-bottom: 18px;
      }

      :deep(.el-select),
      :deep(.el-input),
      :deep(.el-date-picker) {
        width: 100%;
      }
    }
  }

  .product-section {
    margin-bottom: 20px;

    .product-image {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto;

      .el-image {
        width: 100%;
        height: 100%;
      }
    }

    .stock-warning {
      color: #909399;
      font-size: 14px;
      padding: 0 5px;
    }

    :deep(.gray-cell) {
      background-color: #f5f7fa !important;
    }
  }

  .footer-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .summary-info {
      display: flex;
      align-items: center;
      gap: 30px;
      font-size: 14px;

      span {
        color: #606266;

        strong {
          color: #303133;
          font-weight: 600;
        }
      }
    }

    .action-buttons {
      display: flex;
    }
  }
}
</style>
