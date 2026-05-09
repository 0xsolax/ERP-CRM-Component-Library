<template>
  <div class="production-order-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getProductionOrderPage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button v-if="can(ZS_PERMISSIONS.production.orderSave)" type="primary" @click="openProductionDialog">
          新增生产单
        </el-button>
        <el-button v-if="route.query.orderId" @click="clearOrderFilter">查看全部生产总单</el-button>
      </template>

      <template #status="{ row }">
        <el-tag :type="orderStatusMeta(row.status).type">{{ orderStatusMeta(row.status).label }}</el-tag>
      </template>

      <template #totals="{ row }">
        {{ formatAmount(row.totalPlannedQty) }} / {{ formatAmount(row.totalInboundQty) }} /
        {{ formatAmount(row.totalDeliveredQty) }}
      </template>

      <template #deliveryDate="{ row }">{{ formatDate(row.deliveryDate) }}</template>
      <template #updateTime="{ row }">{{ formatDateTime(row.updateTime) }}</template>

      <template #code="{ row }">
        <div class="lineage-code">
          <span>{{ row.code || '-' }}</span>
          <span v-if="lineageSerialText(row)" class="lineage-code__serial">{{ lineageSerialText(row) }}</span>
        </div>
      </template>

      <template #operation="{ row }">
        <el-button v-if="can(ZS_PERMISSIONS.production.orderDetail)" type="primary" link @click="openDetail(row)">
          详情
        </el-button>
        <el-button
          v-if="row.orderId && can(ZS_PERMISSIONS.purchase.save)"
          type="success"
          link
          @click="openGenerateDrawer(row)"
        >
          安排采购
        </el-button>
        <el-button v-if="can(ZS_PERMISSIONS.document.actionLogList)" link @click="openLogDrawer(row)">日志</el-button>
        <el-button v-if="can(ZS_PERMISSIONS.production.orderExport)" link @click="handleExport(row)">导出</el-button>
      </template>
    </bz-table>

    <el-dialog v-model="productionDialogVisible" title="新增生产单" width="1080px" @open="loadCustomers">
      <el-form ref="productionFormRef" :model="productionForm" :rules="productionRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="生产单号">
              <el-input v-model="productionForm.code" placeholder="留空自动生成" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <quick-add-select
                v-model="productionForm.customerId"
                :options="customerOptions"
                :addable="can(ZS_PERMISSIONS.customer.save)"
                :create-option="createCustomerOption"
                placeholder="请选择或新增客户"
                @change="handleProductionCustomerChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="交期">
              <el-date-picker
                v-model="productionForm.deliveryDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="productionForm.remark" type="textarea" :rows="2" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="table-toolbar">
        <span>生产产品</span>
        <div class="toolbar-spacer" />
        <el-button type="primary" @click="openProductDialog">选择产品</el-button>
      </div>
      <el-table :data="productionForm.products" border>
        <el-table-column prop="productCode" label="产品编号" width="130" />
        <el-table-column prop="productName" label="产品" min-width="220" show-overflow-tooltip />
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="0.01" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="180">
          <template #default="{ row }">
            <el-input v-model="row.remark" placeholder="请输入" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link @click="productionForm.products.splice($index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="productionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="productionSaving" @click="handleSaveProductionOrder">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="productDialogVisible" title="选择产品" width="980px" append-to-body @open="loadProducts">
      <div class="product-picker-toolbar">
        <el-input v-model="productQuery.keywords" placeholder="产品编号/描述" clearable @keyup.enter="loadProducts" />
        <el-button type="primary" @click="loadProducts">查询</el-button>
      </div>
      <el-table
        v-loading="productLoading"
        :data="productRows"
        border
        height="430"
        @selection-change="handleProductSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="firstProductImage(row)" :src="firstProductImage(row)" fit="cover" class="product-image" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="productCode" label="产品编号" min-width="130" />
        <el-table-column label="产品类型" min-width="140">
          <template #default="{ row }">
            {{ (row.productTypes || []).map((t: any) => t.typeName).join('、') || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="descriptionEn" label="英文描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="sellingPrice" label="售价" width="100" />
      </el-table>
      <template #footer>
        <el-pagination
          v-model:current-page="productQuery.pageNum"
          v-model:page-size="productQuery.pageSize"
          layout="total, prev, pager, next"
          :total="productTotal"
          @current-change="loadProducts"
        />
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedProducts.length" @click="addSelectedProducts">
          加入生产单
        </el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="detailVisible"
      title="生产总单详情"
      size="min(1480px, calc(100vw - 96px))"
      @open="loadActiveDetail"
    >
      <div v-if="activeOrder" class="detail-layout">
        <div class="detail-toolbar">
          <el-tag :type="orderStatusMeta(activeOrder.status).type">
            {{ orderStatusMeta(activeOrder.status).label }}
          </el-tag>
          <el-tag
            v-if="activeOrder.lockState && activeOrder.lockState !== 'open'"
            :type="lockMeta(activeOrder.lockState).type"
          >
            {{ lockMeta(activeOrder.lockState).label }}
          </el-tag>
          <el-tag v-if="activeOrder.needsReconfirm" type="warning">待重新确认</el-tag>
          <span class="toolbar-text">{{ activeOrder.code }}</span>
          <div class="toolbar-spacer" />
          <el-button v-if="activeOrder.orderId" @click="openOrderPreview(activeOrder.orderId)">来源订单</el-button>
          <el-button v-if="activeOrder.orderId" @click="openPurchaseList(activeOrder)">采购单</el-button>
          <el-button v-if="can(ZS_PERMISSIONS.document.actionLogList)" @click="openLogDrawer(activeOrder)">
            日志
          </el-button>
          <el-button
            v-if="can(ZS_PERMISSIONS.production.orderExport)"
            :loading="exportingId === activeOrder.id"
            @click="handleExport(activeOrder)"
          >
            导出
          </el-button>
          <el-button
            v-if="can(ZS_PERMISSIONS.production.orderProgress)"
            type="primary"
            :disabled="!(activeOrder.progressRows || []).length"
            @click="openBatchDialog"
          >
            安排生产
          </el-button>
          <el-button
            v-if="activeOrder.orderId && can(ZS_PERMISSIONS.purchase.save)"
            type="success"
            @click="openGenerateDrawer"
          >
            安排采购
          </el-button>
        </div>

        <el-descriptions :column="3" border>
          <el-descriptions-item label="来源订单">{{ activeOrder.orderCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ activeOrder.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交期">{{ formatDate(activeOrder.deliveryDate) }}</el-descriptions-item>
          <el-descriptions-item label="订单数量">{{ formatAmount(activeOrder.totalOrderQty) }}</el-descriptions-item>
          <el-descriptions-item label="已安排">{{ formatAmount(activeOrder.totalPlannedQty) }}</el-descriptions-item>
          <el-descriptions-item label="剩余待交">
            {{ formatAmount(activeOrder.totalRemainingDeliveryQty) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title-row">
          <span>产品行进度与交货</span>
        </div>
        <production-progress-table :master="activeOrder" show-batches>
          <template #progressActions="{ row }">
            <el-button
              v-if="can(ZS_PERMISSIONS.production.orderDelivery)"
              type="primary"
              link
              :disabled="Number(row.remainingDeliveryQty || 0) <= 0"
              @click="handleDelivery(row)"
            >
              交货
            </el-button>
          </template>
        </production-progress-table>
      </div>
    </el-drawer>

    <el-dialog v-model="batchVisible" title="安排生产" width="900px">
      <div class="table-toolbar">
        <el-button @click="addBatchRow">添加批次</el-button>
      </div>
      <el-table :data="batchForm.batches" border>
        <el-table-column label="产品行" min-width="210">
          <template #default="{ row }">
            <el-select
              v-model="row.progressId"
              filterable
              placeholder="选择产品行"
              style="width: 100%"
              @change="handleProgressChange(row)"
            >
              <el-option
                v-for="item in batchCandidates"
                :key="item.id"
                :label="`${item.productCode || '-'} 可安排 ${formatAmount(item.availablePlanQty)}`"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="生产组" min-width="170">
          <template #default="{ row }">
            <quick-add-select
              v-model="row.productionGroupId"
              :options="groupOptions"
              :addable="can(ZS_PERMISSIONS.production.groupSave)"
              :create-option="createProductionGroupOption"
              placeholder="选择生产组"
            />
          </template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.batchQty" :min="0.01" :precision="2" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="计划交期" width="150">
          <template #default="{ row }">
            <el-date-picker v-model="row.plannedDeliveryDate" type="date" value-format="YYYY-MM-DD" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.remark" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button type="danger" link @click="batchForm.batches.splice($index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchSaving" @click="handleSaveBatches">保存安排</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="orderPreviewVisible" title="来源订单预览" size="860px">
      <el-descriptions v-if="orderPreview" :column="2" border>
        <el-descriptions-item label="订单号">{{ orderPreview.code }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ orderPreview.customerName }}</el-descriptions-item>
        <el-descriptions-item label="交期">{{ formatDate(orderPreview.deliveryDate) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ orderStatusText(orderPreview.status) }}</el-descriptions-item>
      </el-descriptions>
      <production-progress-table v-if="orderPreview" :master="orderPreview.productionMaster" compact />
      <el-table v-if="orderPreview" :data="orderPreview.products || []" border class="preview-table">
        <el-table-column prop="productCode" label="产品编号" width="130" />
        <el-table-column prop="descriptionEn" label="英文描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="100" />
      </el-table>
    </el-drawer>

    <document-action-log-drawer
      v-model="logDrawerVisible"
      document-type="production"
      :document-id="activeLogRow?.id"
      :title="`生产单动作日志 ${activeLogRow?.code || ''}`"
      :lock-state="activeLogRow?.lockState"
      :needs-reconfirm="activeLogRow?.needsReconfirm"
      @changed="handleLogChanged"
    />

    <purchase-generate-drawer
      v-model="purchaseGenerateVisible"
      :order-id="purchaseGenerateContext.orderId"
      :order-code="purchaseGenerateContext.orderCode"
      :production-order-id="purchaseGenerateContext.productionOrderId"
      @generated="handlePurchaseGenerated"
    />
  </div>
</template>

<script lang="ts" setup name="production-order">
import { computed, onActivated, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getOrderDetail } from '@/api/zs/order'
import {
  arrangeProductionBatches,
  buildProductionOrderProductSnapshots,
  downloadProductionBlobFile,
  exportProductionExcel,
  getProductionGroupOptions,
  getProductionOrderDetail,
  getProductionOrderPage,
  getProductionOrderProductPage,
  recordProductionDelivery,
  saveOrUpdateProductionGroup,
  saveOrUpdateProductionOrder
} from '@/api/zs/production'
import { getCustomerOptions, saveOrUpdateCustomer } from '@/api/zs/customer'
import DocumentActionLogDrawer from '@/components/document-action-log-drawer/index.vue'
import QuickAddSelect from '@/components/quick-add-select/index.vue'
import { DOCUMENT_UNLOCK_APPROVAL_LOCK_STATE_META, resolveStatusMeta } from '@/constant/document'
import { ZS_PERMISSIONS } from '@/constant/permissions'
import { ORDER_STATUS_META } from '@/constant/sales'
import { usePermissionStore } from '@/views/zs/store/modules/permission'
import PurchaseGenerateDrawer from '@/views/zs/purchase/components/purchase-generate-drawer.vue'
import ProductionProgressTable from './components/production-progress-table.vue'

const route = useRoute()
const router = useRouter()
const tableRef = ref()
const permissionStore = usePermissionStore()
const hasInitialized = ref(true)
const detailVisible = ref(false)
const activeOrder = ref<any>(null)
const batchVisible = ref(false)
const batchSaving = ref(false)
const exportingId = ref<any>(null)
const groupOptions = ref<any[]>([])
const orderPreviewVisible = ref(false)
const orderPreview = ref<any>(null)
const purchaseGenerateVisible = ref(false)
const logDrawerVisible = ref(false)
const activeLogRow = ref<any>(null)
const productionDialogVisible = ref(false)
const productionFormRef = ref()
const productionSaving = ref(false)
const customerOptions = ref<any[]>([])
const productDialogVisible = ref(false)
const productLoading = ref(false)
const productRows = ref<any[]>([])
const productTotal = ref(0)
const selectedProducts = ref<any[]>([])
const productQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  keywords: ''
})
const batchForm = reactive<any>({
  batches: []
})
const purchaseGenerateContext = reactive<any>({
  orderId: '',
  orderCode: '',
  productionOrderId: '',
  productionOrderCode: ''
})
const productionForm = reactive<any>({
  id: null,
  code: '',
  customerId: null,
  customerName: '',
  deliveryDate: '',
  remark: '',
  products: []
})

const productionRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }]
}

const can = (permission: string) =>
  permissionStore.permissions.includes('*') || permissionStore.permissions.includes(permission)

const searchColumns = ref([
  {
    prop: 'keyword',
    label: '生产单/订单/客户',
    search: { el: 'el-input', props: { placeholder: '生产单/流水/订单/客户', clearable: true } }
  },
  {
    prop: 'status',
    label: '状态',
    search: { el: 'el-select', props: { clearable: true } },
    enum: [
      { label: '草稿', value: 'draft' },
      { label: '生产中', value: 'in_production' },
      { label: '已完成', value: 'completed' },
      { label: '需核对', value: 'manual_reconcile' }
    ]
  }
])

const columns = ref([
  { prop: 'code', label: '生产总单号', minWidth: 150 },
  { prop: 'orderCode', label: '来源订单', minWidth: 140 },
  { prop: 'customerName', label: '客户', minWidth: 160, showOverflowTooltip: true },
  { prop: 'deliveryDate', label: '交期', minWidth: 110 },
  { prop: 'totals', label: '已安排/入库/交货', minWidth: 150 },
  { prop: 'status', label: '状态', minWidth: 110 },
  { prop: 'ownerName', label: '负责人', minWidth: 100 },
  { prop: 'updateTime', label: '更新时间', minWidth: 160 },
  { prop: 'operation', label: '操作', width: 240, fixed: 'right' }
])

const batchCandidates = computed(() =>
  (activeOrder.value?.progressRows || []).filter((item: any) => Number(item.availablePlanQty || 0) > 0)
)

const searchDataCallback = (params: any) => ({
  ...params,
  orderId: route.query.orderId || params.orderId
})

const dataCallback = (data: any) => ({
  list: data?.list || [],
  total: Number(data?.total || 0)
})

const lineageSerialText = (row: any) => {
  const serialCode = row?.serialCode
  return serialCode && serialCode !== row?.code ? `内部流水：${serialCode}` : ''
}

const resetProductionForm = () => {
  Object.assign(productionForm, {
    id: null,
    code: '',
    customerId: null,
    customerName: '',
    deliveryDate: '',
    remark: '',
    products: []
  })
  productionFormRef.value?.clearValidate?.()
}

const openProductionDialog = async () => {
  resetProductionForm()
  await loadCustomers()
  productionDialogVisible.value = true
}

const loadCustomers = async () => {
  const { code, data, message } = await getCustomerOptions({})
  if (code !== 200) return ElMessage.warning(message)
  customerOptions.value = data || []
}

const createCustomerOption = async (name: string) => {
  const { code, data, message } = await saveOrUpdateCustomer({ name, status: 1 })
  if (code !== 200) {
    ElMessage.warning(message)
    return null
  }
  await loadCustomers()
  return customerOptions.value.find(item => item.id === data) || { id: data, name }
}

const handleProductionCustomerChange = (_value: any, label: string) => {
  productionForm.customerName = label || ''
}

const openProductDialog = () => {
  selectedProducts.value = []
  productDialogVisible.value = true
}

const loadProducts = async () => {
  productLoading.value = true
  try {
    const { code, data, message } = await getProductionOrderProductPage(productQuery)
    if (code !== 200) return ElMessage.warning(message)
    productRows.value = data?.list || []
    productTotal.value = Number(data?.total || 0)
  } finally {
    productLoading.value = false
  }
}

const handleProductSelectionChange = (rows: any[]) => {
  selectedProducts.value = rows || []
}

const addSelectedProducts = async () => {
  const productIds = selectedProducts.value.map(item => item.id).filter(Boolean)
  const { code, data, message } = await buildProductionOrderProductSnapshots({ productIds })
  if (code !== 200) return ElMessage.warning(message)
  const existingIds = new Set(productionForm.products.map((item: any) => String(item.productId)))
  const rows = (data || [])
    .map(normalizeProductionProductRow)
    .filter((item: any) => !existingIds.has(String(item.productId)))
  productionForm.products.push(...rows)
  productDialogVisible.value = false
}

const normalizeProductionProductRow = (row: any) => ({
  ...row,
  productName: firstLine(row.descriptionEn) || firstLine(row.description) || row.productCode || '',
  quantity: Number(row.quantity || 1),
  remark: row.remark || ''
})

const buildProductionPayload = () => ({
  id: productionForm.id,
  code: productionForm.code,
  customerId: productionForm.customerId,
  customerName: productionForm.customerName,
  deliveryDate: productionForm.deliveryDate || null,
  remark: productionForm.remark,
  products: productionForm.products.map((item: any) => ({
    lineKey: item.lineKey,
    productId: item.productId,
    productCode: item.productCode,
    productName: item.productName,
    quantity: Number(item.quantity || 0),
    sourceSnapshotJson: JSON.stringify({ ...item, quantity: Number(item.quantity || 0) }),
    remark: item.remark
  }))
})

const handleSaveProductionOrder = async () => {
  await productionFormRef.value?.validate?.()
  if (!productionForm.products.length) return ElMessage.warning('请至少选择一个产品')
  productionSaving.value = true
  try {
    const { code, message } = await saveOrUpdateProductionOrder(buildProductionPayload())
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('生产单已保存')
    productionDialogVisible.value = false
    tableRef.value?.getTableList()
  } finally {
    productionSaving.value = false
  }
}

const normalizeBusinessId = (value: any) => {
  const raw = Array.isArray(value) ? value[0] : value
  const text = String(raw ?? '').trim()
  return text && text !== '0' ? text : ''
}

const openDetailById = async (id: any) => {
  const documentId = normalizeBusinessId(id)
  if (!documentId) return
  activeOrder.value = { id: documentId }
  detailVisible.value = true
  await loadActiveDetail()
}

const openDetail = async (row: any) => {
  await openDetailById(row.id)
}

const loadActiveDetail = async () => {
  if (!activeOrder.value?.id) return
  const { code, data, message } = await getProductionOrderDetail({ id: activeOrder.value.id })
  if (code !== 200) return ElMessage.warning(message)
  activeOrder.value = data
}

const clearOrderFilter = () => {
  router.replace({ path: '/production/order' })
}

const openPurchaseList = (row: any) => {
  router.push({
    path: '/purchase/index',
    query: { orderId: row.orderId, orderCode: row.orderCode, productionOrderId: row.id, productionOrderCode: row.code }
  })
}

const openBatchDialog = async () => {
  const { code, data, message } = await getProductionGroupOptions({})
  if (code !== 200) return ElMessage.warning(message)
  groupOptions.value = data || []
  batchForm.batches = []
  addBatchRow()
  batchVisible.value = true
}

const createProductionGroupOption = async (name: string) => {
  const { code, data, message } = await saveOrUpdateProductionGroup({ name, status: 1 })
  if (code !== 200) {
    ElMessage.warning(message)
    return null
  }
  const result = await getProductionGroupOptions({})
  if (result.code === 200) groupOptions.value = result.data || []
  return groupOptions.value.find(item => item.id === data) || { id: data, name }
}

const openGenerateDrawer = (row?: any) => {
  const target = row?.id ? row : activeOrder.value
  if (!target?.orderId) return ElMessage.warning('缺少来源订单，无法安排采购')
  Object.assign(purchaseGenerateContext, {
    orderId: normalizeBusinessId(target.orderId),
    orderCode: target.orderCode || '',
    productionOrderId: normalizeBusinessId(target.id),
    productionOrderCode: target.code || ''
  })
  purchaseGenerateVisible.value = true
}

const handlePurchaseGenerated = async () => {
  tableRef.value?.getTableList()
  if (detailVisible.value && activeOrder.value?.id) await loadActiveDetail()
}

const addBatchRow = () => {
  batchForm.batches.push({
    progressId: batchCandidates.value[0]?.id || null,
    productionGroupId: groupOptions.value[0]?.id || null,
    batchQty: Number(batchCandidates.value[0]?.availablePlanQty || 1),
    plannedDeliveryDate: activeOrder.value?.deliveryDate || '',
    remark: ''
  })
}

const handleProgressChange = (row: any) => {
  const progress = batchCandidates.value.find((item: any) => item.id === row.progressId)
  row.batchQty = Number(progress?.availablePlanQty || row.batchQty || 1)
}

const handleSaveBatches = async () => {
  if (!activeOrder.value?.id) return
  if (!batchForm.batches.length) return ElMessage.warning('请添加生产安排')
  batchSaving.value = true
  try {
    const { code, data, message } = await arrangeProductionBatches({
      productionOrderId: activeOrder.value.id,
      batches: batchForm.batches
    })
    if (code !== 200) return ElMessage.warning(message)
    activeOrder.value = data
    batchVisible.value = false
    tableRef.value?.getTableList()
    ElMessage.success('生产安排已保存')
  } finally {
    batchSaving.value = false
  }
}

const handleDelivery = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入本次交货数量', '产品行交货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'number',
    inputValue: row.remainingDeliveryQty
  })
  const qty = Number(value)
  if (!Number.isFinite(qty) || qty <= 0) return ElMessage.warning('请输入有效数量')
  const { code, data, message } = await recordProductionDelivery({
    productionOrderId: activeOrder.value.id,
    progressId: row.id,
    deliveryQty: qty
  })
  if (code !== 200) return ElMessage.warning(message)
  activeOrder.value = data
  tableRef.value?.getTableList()
  ElMessage.success('交货进度已更新')
}

const handleExport = async (row: any) => {
  exportingId.value = row.id
  try {
    const { blob, fileName } = await exportProductionExcel({ id: row.id })
    downloadProductionBlobFile(blob, fileName)
  } finally {
    exportingId.value = null
  }
}

const openOrderPreview = async (orderId: any) => {
  const { code, data, message } = await getOrderDetail({ id: orderId })
  if (code !== 200) return ElMessage.warning(message)
  orderPreview.value = data
  orderPreviewVisible.value = true
}

const orderStatusMeta = (status: string) => {
  const map: Record<string, any> = {
    draft: { label: '草稿', type: 'info' },
    in_production: { label: '生产中', type: 'warning' },
    completed: { label: '已完成', type: 'success' },
    manual_reconcile: { label: '需核对', type: 'danger' }
  }
  return map[status] || { label: status || '-', type: 'info' }
}

const orderStatusText = (status: string) => {
  return ORDER_STATUS_META[status]?.label || status || '-'
}

const lockMeta = (lockState: string) => resolveStatusMeta(DOCUMENT_UNLOCK_APPROVAL_LOCK_STATE_META, lockState)

const openLogDrawer = (row: any) => {
  activeLogRow.value = row
  logDrawerVisible.value = true
}

const handleLogChanged = async () => {
  tableRef.value?.getTableList()
  if (detailVisible.value) await loadActiveDetail()
}

const formatDate = (value: any) => (value ? dayjs(value).format('YYYY-MM-DD') : '-')
const formatDateTime = (value: any) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-')
const formatAmount = (value: any) => {
  const num = Number(value || 0)
  return Number.isFinite(num) ? num.toFixed(2) : '0.00'
}
const firstLine = (value: any) =>
  String(value || '')
    .trim()
    .split(/\r?\n/)
    .find(Boolean) || ''
const firstProductImage = (row: any) => {
  const images = Array.isArray(row.images) ? row.images : []
  const first = images[0]
  const snapshot = parseSnapshot(row.sourceSnapshotJson)
  const snapshotImages = Array.isArray(snapshot.images) ? snapshot.images : []
  const snapshotFirst = snapshotImages[0]
  return row.imageUrl || first?.url || first || snapshot.imageUrl || snapshotFirst?.url || snapshotFirst || ''
}

const parseSnapshot = (value: any) => {
  if (!value) return {}
  if (typeof value === 'object') return value
  try {
    return JSON.parse(String(value))
  } catch {
    return {}
  }
}

const openRouteDetail = async () => {
  const documentId = normalizeBusinessId(route.query.detailId)
  if (!documentId) return
  if (detailVisible.value && String(activeOrder.value?.id || '') === documentId) return
  await openDetailById(documentId)
}

watch(() => route.query.detailId, openRouteDetail, { immediate: true })

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
  openRouteDetail()
})
</script>

<style lang="scss" scoped>
.production-order-container {
  height: 100%;
}

.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-toolbar,
.table-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-spacer {
  flex: 1;
}

.toolbar-text {
  font-weight: 600;
}

.section-title-row {
  margin-top: 4px;
  font-weight: 600;
}

.product-picker-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;

  .el-input {
    width: 280px;
  }
}

.product-image {
  width: 46px;
  height: 46px;
  border-radius: 4px;
}

.preview-table {
  margin-top: 12px;
}

.lineage-code {
  display: flex;
  flex-direction: column;
  gap: 2px;
  line-height: 1.4;
}

.lineage-code__serial {
  color: #909399;
  font-size: 12px;
}
</style>
