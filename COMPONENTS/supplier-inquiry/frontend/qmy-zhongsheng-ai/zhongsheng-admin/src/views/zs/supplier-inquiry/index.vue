<template>
  <div class="supplier-inquiry-container">
    <div class="toolbar-panel">
      <div class="page-title">
        <strong>供应商询价台账</strong>
        <span>记录采购前供应商历史询价，不影响采购单和材料主档价格</span>
      </div>
      <el-button
        v-if="hasActionPermission(ZS_PERMISSIONS.supplierInquiry.save)"
        type="primary"
        :icon="Plus"
        @click="handleAdd"
      >
        新增询价
      </el-button>
    </div>

    <div class="filter-panel">
      <el-form :model="filters" class="filter-form" label-position="top" @submit.prevent>
        <el-form-item label="关键词" class="filter-item filter-item--keyword">
          <el-input v-model="filters.keyword" placeholder="供应商/对象/规格" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item label="供应商" class="filter-item">
          <quick-add-select
            v-model="filters.supplierId"
            :options="supplierOptions"
            label-key="name"
            value-key="id"
            placeholder="请选择"
            add-button-text="新增供应商"
            input-placeholder="请输入供应商名称"
            :create-option="createSupplierOption"
          />
        </el-form-item>
        <el-form-item label="对象类型" class="filter-item">
          <quick-add-select
            v-model="filters.targetType"
            :options="targetTypeOptions"
            label-key="label"
            value-key="value"
            placeholder="请选择"
            add-button-text="新增类型"
            input-placeholder="请输入类型名称"
            :create-option="createTargetTypeOption"
          />
        </el-form-item>
        <el-form-item label="币种" class="filter-item filter-item--currency">
          <el-select v-model="filters.currency" placeholder="请选择" clearable>
            <el-option v-for="item in currencyOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报价日期" class="filter-item filter-item--date">
          <el-date-picker
            v-model="filters.quoteDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="有效状态" class="filter-item filter-item--status">
          <el-select v-model="filters.validStatus" placeholder="全部" clearable>
            <el-option label="有效" value="effective" />
            <el-option label="已过期" value="expired" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="stats-row">
      <div class="stat-item">
        <span>记录数</span>
        <strong>{{ pagination.total }}</strong>
      </div>
      <div class="stat-item">
        <span>当前页有效</span>
        <strong>{{ effectiveCount }}</strong>
      </div>
      <div class="stat-item">
        <span>当前页最低价</span>
        <strong>{{ minPriceText }}</strong>
      </div>
      <div class="stat-item">
        <span>最近报价</span>
        <strong>{{ latestQuoteDate }}</strong>
      </div>
    </div>

    <div class="table-panel">
      <el-table v-loading="loading" :data="list" border height="100%">
        <el-table-column prop="quoteDate" label="报价日期" width="110" />
        <el-table-column label="供应商" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <el-button
              v-if="hasActionPermission(ZS_PERMISSIONS.supplierInquiry.page)"
              type="primary"
              link
              class="supplier-history-link"
              @click="handleSupplierHistory(row)"
            >
              {{ row.supplierName || '-' }}
            </el-button>
            <div v-else class="main-line">{{ row.supplierName || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="询价对象" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="object-cell">
              <el-tag size="small" effect="plain">{{ targetTypeLabel(row.targetType) }}</el-tag>
              <el-button
                v-if="hasActionPermission(ZS_PERMISSIONS.supplierInquiry.page)"
                type="primary"
                link
                class="target-history-link"
                @click="handleTargetHistory(row)"
              >
                {{ row.targetName || '-' }}
              </el-button>
              <span v-else class="main-line">{{ row.targetName || '-' }}</span>
            </div>
            <div class="sub-line">
              <span v-if="row.targetCode">编号：{{ row.targetCode }}</span>
              <span v-if="row.specification">规格：{{ row.specification }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价/单位" width="140" align="right">
          <template #default="{ row }">
            <div class="price-unit-cell">
              {{ priceUnitText(row.price, row.currency, row.unit) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="起订量" width="100" align="right">
          <template #default="{ row }">
            {{ quantityText(row.moq) }}
          </template>
        </el-table-column>
        <el-table-column label="交期" min-width="100" show-overflow-tooltip>
          <template #default="{ row }">
            {{ deliveryDaysText(row.deliveryDays) }}
          </template>
        </el-table-column>
        <el-table-column prop="validUntil" label="有效期" width="110" />
        <el-table-column label="税率" width="90" align="right">
          <template #default="{ row }">
            {{ percentText(row.taxRate) }}
          </template>
        </el-table-column>
        <el-table-column label="联系人" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="main-line">{{ row.contactName || '-' }}</div>
            <div class="sub-line">{{ row.contactPhone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="录入/更新" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="main-line">{{ row.ownerName || '-' }}</div>
            <div class="sub-line">{{ formatDateTime(row.updateTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="96" fixed="right">
          <template #default="{ row }">
            <div class="operation-stack">
              <el-button
                v-if="hasActionPermission(ZS_PERMISSIONS.supplierInquiry.detail)"
                type="primary"
                link
                :icon="View"
                @click="handleDetail(row)"
              >
                查看
              </el-button>
              <el-button
                v-if="hasActionPermission(ZS_PERMISSIONS.supplierInquiry.delete)"
                type="danger"
                link
                :icon="Delete"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-row">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[15, 30, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadList"
        @current-change="loadList"
      />
    </div>

    <el-dialog v-model="dialog.visible" :title="dialogTitle" width="920px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" :disabled="isDetailMode" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <quick-add-select
                v-model="form.supplierId"
                :options="supplierOptions"
                label-key="name"
                value-key="id"
                placeholder="请选择供应商"
                add-button-text="新增供应商"
                input-placeholder="请输入供应商名称"
                :disabled="isDetailMode"
                :create-option="createSupplierOption"
                @change="handleSupplierChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对象类型" prop="targetType">
              <quick-add-select
                v-model="form.targetType"
                :options="targetTypeOptions"
                label-key="label"
                value-key="value"
                placeholder="请选择"
                add-button-text="新增类型"
                input-placeholder="请输入类型名称"
                :disabled="isDetailMode"
                :create-option="createTargetTypeOption"
                @change="handleTargetTypeChange"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="询价对象" prop="targetName">
              <el-select
                v-model="targetSelectValue"
                placeholder="请选择或新增询价对象"
                clearable
                filterable
                popper-class="quick-add-select-popper supplier-inquiry-target-popper"
                :disabled="isDetailMode"
                :remote="isMasterTargetType"
                :remote-method="loadTargetOptions"
                :loading="targetLoading"
                @change="handleTargetSelect"
              >
                <el-option v-for="item in targetOptions" :key="item.value" :label="item.label" :value="item.value">
                  <div class="target-option">
                    <strong>{{ item.name }}</strong>
                    <span>{{ item.specification || item.code || '' }}</span>
                  </div>
                </el-option>
                <template v-if="!isDetailMode" #footer>
                  <el-button
                    v-if="!targetAdding"
                    text
                    bg
                    size="small"
                    class="quick-add-select__add-button"
                    @click="targetAdding = true"
                  >
                    新增询价对象
                  </el-button>
                  <template v-else>
                    <el-input
                      v-model="newTargetName"
                      placeholder="请输入询价对象名称"
                      @keyup.enter="handleCreateTarget"
                    />
                    <div class="quick-add-select__footer-actions">
                      <el-button type="primary" size="small" @click="handleCreateTarget">确认</el-button>
                      <el-button size="small" @click="resetTargetCreate">取消</el-button>
                    </div>
                  </template>
                </template>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对象编号">
              <el-input v-model="form.targetCode" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规格">
              <el-input v-model="form.specification" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="单价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="4" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="币种">
              <el-select v-model="form.currency">
                <el-option v-for="item in currencyOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="税率%">
              <el-input-number v-model="form.taxRate" :min="0" :precision="2" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="起订量">
              <el-input-number v-model="form.moq" :min="0" :precision="4" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="交期">
              <el-input v-model="form.deliveryDays" placeholder="如 15 天" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="报价日期" prop="quoteDate">
              <el-date-picker v-model="form.quoteDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="有效期">
              <el-date-picker v-model="form.validUntil" type="date" value-format="YYYY-MM-DD" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人">
              <el-input v-model="form.contactName" placeholder="可从供应商带出" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系方式">
              <el-input v-model="form.contactPhone" placeholder="可从供应商带出" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div v-if="isDetailMode" class="change-log-section">
        <div class="section-title">修改日志</div>
        <el-empty v-if="!changeLogs.length" description="暂无修改日志" :image-size="64" />
        <div v-else class="change-log-list">
          <div v-for="(log, index) in changeLogs" :key="index" class="change-log-item">
            <div class="change-log-meta">
              <strong>{{ log.operatorName || '系统' }}</strong>
              <span>{{ formatDateTime(log.time) }}</span>
            </div>
            <div class="change-log-lines">
              <div v-for="(change, changeIndex) in log.changes || []" :key="changeIndex" class="change-log-line">
                <span class="field">{{ change.field }}</span>
                <span class="before">{{ change.before || '-' }}</span>
                <span class="arrow">→</span>
                <span class="after">{{ change.after || '-' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialog.visible = false">{{ isDetailMode ? '关闭' : '取消' }}</el-button>
        <el-button
          v-if="isDetailMode && hasActionPermission(ZS_PERMISSIONS.supplierInquiry.save)"
          type="primary"
          :icon="EditPen"
          @click="switchDetailToEdit"
        >
          编辑
        </el-button>
        <el-button v-if="!isDetailMode" type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="history.visible" :title="history.title" size="720px" destroy-on-close>
      <div v-if="history.row" class="history-title">
        <strong>{{ history.subject }}</strong>
        <span>{{ history.subtitle }}</span>
      </div>
      <el-table v-loading="history.loading" :data="history.list" border>
        <el-table-column prop="quoteDate" label="报价日期" width="110" />
        <el-table-column prop="supplierName" label="供应商" min-width="150" show-overflow-tooltip />
        <el-table-column prop="targetName" label="询价对象" min-width="160" show-overflow-tooltip />
        <el-table-column label="单价/单位" width="130" align="right">
          <template #default="{ row }">
            {{ priceUnitText(row.price, row.currency, row.unit) }}
          </template>
        </el-table-column>
        <el-table-column label="税率%" width="80" align="right">
          <template #default="{ row }">
            {{ percentText(row.taxRate, false) }}
          </template>
        </el-table-column>
        <el-table-column prop="validUntil" label="有效期" width="110" />
        <el-table-column prop="ownerName" label="录入人" width="100" />
      </el-table>
    </el-drawer>
  </div>
</template>

<script lang="ts" setup name="supplier-inquiry">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, EditPen, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getSupplierOptions, saveOrUpdateSupplier } from '@/api/zs/supplier'
import {
  deleteSupplierInquiry,
  getSupplierInquiryDetail,
  getSupplierInquiryHistory,
  getSupplierInquiryPage,
  saveOrUpdateSupplierInquiry
} from '@/api/zs/supplier-inquiry'
import { getMaterialPage } from '@/api/zs/material/material'
import { getFabricPage } from '@/api/zs/material/fabric'
import { getPackagingPage } from '@/api/zs/material/packaging'
import { getUmbrellaFramePage } from '@/api/zs/material/umbrella-frame'
import { getProductPage } from '@/api/zs/product'
import { ZS_PERMISSIONS } from '@/constant/permissions'
import { LOCAL_DEFAULT_CURRENCY, SUPPLIER_INQUIRY_CURRENCY_OPTIONS, resolveCurrencySymbol } from '@/constant/currency'
import {
  SUPPLIER_INQUIRY_DEFAULT_TARGET_TYPE_OPTIONS,
  SUPPLIER_INQUIRY_MASTER_TARGET_TYPES,
  SUPPLIER_INQUIRY_TARGET_TYPES
} from '@/constant/supplier-inquiry'
import { usePermissionStore } from '@/views/zs/store/modules/permission'
import QuickAddSelect from '@/components/quick-add-select/index.vue'

const permissionStore = usePermissionStore()
const loading = ref(false)
const saving = ref(false)
const targetLoading = ref(false)
const list = ref<any[]>([])
const supplierOptions = ref<any[]>([])
const targetOptions = ref<any[]>([])
const targetSelectValue = ref<any>(null)
const targetAdding = ref(false)
const newTargetName = ref('')
const formRef = ref()

const filters = reactive<any>({
  keyword: '',
  supplierId: null,
  targetType: '',
  currency: '',
  quoteDateRange: [],
  validStatus: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 15,
  total: 0
})

const dialog = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit' | 'detail'
})

const form = reactive<any>({
  id: null,
  supplierId: null,
  targetType: SUPPLIER_INQUIRY_TARGET_TYPES.manual,
  targetId: null,
  targetCode: '',
  targetName: '',
  specification: '',
  unit: '',
  price: null,
  currency: LOCAL_DEFAULT_CURRENCY,
  taxRate: 0,
  moq: null,
  deliveryDays: '',
  quoteDate: dayjs().format('YYYY-MM-DD'),
  validUntil: '',
  contactName: '',
  contactPhone: '',
  remark: '',
  changeLogs: []
})

const history = reactive<any>({
  visible: false,
  loading: false,
  row: null,
  title: '供应商报价历史',
  subject: '',
  subtitle: '',
  list: []
})

const targetTypeOptions = ref<any[]>([...SUPPLIER_INQUIRY_DEFAULT_TARGET_TYPE_OPTIONS])
const currencyOptions = SUPPLIER_INQUIRY_CURRENCY_OPTIONS

const rules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  targetType: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
  targetName: [{ required: true, message: '请填写或选择询价对象', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  quoteDate: [{ required: true, message: '请选择报价日期', trigger: 'change' }]
}

const isDetailMode = computed(() => dialog.mode === 'detail')
const isMasterTargetType = computed(() => SUPPLIER_INQUIRY_MASTER_TARGET_TYPES.includes(form.targetType as any))
const changeLogs = computed(() => (Array.isArray(form.changeLogs) ? form.changeLogs : []))
const dialogTitle = computed(() => {
  if (dialog.mode === 'detail') return '询价详情'
  return dialog.mode === 'edit' ? '编辑询价' : '新增询价'
})

const effectiveCount = computed(() => {
  const today = dayjs().format('YYYY-MM-DD')
  return list.value.filter(item => !item.validUntil || item.validUntil >= today).length
})

const minPriceText = computed(() => {
  const priced = list.value.filter(item => item.price != null)
  if (!priced.length) return '-'
  const min = priced.reduce((prev, cur) => (Number(cur.price) < Number(prev.price) ? cur : prev))
  return moneyText(min.price, min.currency)
})

const latestQuoteDate = computed(() => {
  return list.value[0]?.quoteDate || '-'
})

const hasActionPermission = (permission: string) => {
  return permissionStore.permissions.includes('*') || permissionStore.permissions.includes(permission)
}

const targetTypeLabel = (value: string) => {
  return targetTypeOptions.value.find(item => item.value === value)?.label || value || '-'
}

const formatDateTime = (value: string) => {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-'
}

const moneyText = (price: any, currency = LOCAL_DEFAULT_CURRENCY) => {
  if (price == null || price === '') return '-'
  return `${resolveCurrencySymbol(currency)}${Number(price).toFixed(2)}`
}

const priceUnitText = (price: any, currency: string, unit: string) => {
  const text = moneyText(price, currency)
  if (text === '-') return text
  const unitText = String(unit || '').trim()
  return unitText ? `${text}/${unitText}` : text
}

const deliveryDaysText = (value: any) => {
  if (value == null || value === '') return '-'
  const text = String(value).trim()
  if (!text) return '-'
  return /天$|days?$/i.test(text) ? text : `${text} 天`
}

const decimalText = (value: any) => {
  if (value == null || value === '') return ''
  const num = Number(value)
  if (!Number.isFinite(num)) return String(value)
  return num.toFixed(4).replace(/\.?0+$/, '')
}

const percentText = (value: any, withSymbol = true) => {
  const text = decimalText(value)
  if (!text) return '-'
  return withSymbol ? `${text}%` : text
}

const quantityText = (value: any) => {
  return decimalText(value) || '-'
}

const loadSupplierOptions = async (keyword = '') => {
  try {
    const { code, data } = await getSupplierOptions({ keyword })
    if (code === 200) supplierOptions.value = data || []
  } catch {
    // 下拉加载失败时保留已有选项和快捷新增入口，避免正在录入的询价被中断。
    if (!supplierOptions.value.length) supplierOptions.value = []
  }
}

const sameId = (left: any, right: any) => String(left) === String(right)

const ensureSupplierOption = (supplier: any) => {
  if (!supplier?.id) return null
  const existing = supplierOptions.value.find(item => sameId(item.id, supplier.id))
  if (existing) {
    Object.assign(existing, supplier)
    return existing
  }
  supplierOptions.value.unshift(supplier)
  return supplier
}

const buildQuickSupplierCode = () => `SUP${dayjs().format('YYMMDDHHmmssSSS')}`

const createSupplierOption = async (name: string) => {
  const supplierName = name.trim()
  if (!supplierName) return null
  if (supplierName.length > 64) {
    ElMessage.warning('供应商名称最多 64 个字符')
    return null
  }
  const supplierCode = buildQuickSupplierCode()
  const { code, data, message } = await saveOrUpdateSupplier({
    code: supplierCode,
    name: supplierName,
    status: 1
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return null
  }
  const fallback = ensureSupplierOption({
    id: data,
    code: supplierCode,
    name: supplierName,
    contact: '',
    phone: ''
  })
  await loadSupplierOptions()
  ensureSupplierOption(fallback)
  return supplierOptions.value.find(item => sameId(item.id, data)) || fallback
}

const loadList = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      supplierId: filters.supplierId || undefined,
      targetType: filters.targetType || undefined,
      currency: filters.currency || undefined,
      validStatus: filters.validStatus || undefined,
      quoteDateFrom: filters.quoteDateRange?.[0],
      quoteDateTo: filters.quoteDateRange?.[1]
    }
    const { code, data, message } = await getSupplierInquiryPage(params)
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    list.value = data?.list || []
    mergeTargetTypesFromRows(list.value)
    pagination.total = Number(data?.total || 0)
  } finally {
    loading.value = false
  }
}

const ensureTargetTypeOption = (value: string, label = value) => {
  const name = value?.trim()
  if (!name) return null
  const existing = targetTypeOptions.value.find(item => item.value === name || item.label === label)
  if (existing) return existing
  const option = { label: label?.trim() || name, value: name }
  targetTypeOptions.value.push(option)
  return option
}

const mergeTargetTypesFromRows = (rows: any[]) => {
  rows.forEach(row => ensureTargetTypeOption(row.targetType))
}

const createTargetTypeOption = async (label: string) => {
  const name = label.trim()
  if (!name) return null
  if (name.length > 32) {
    ElMessage.warning('类型名称最多 32 个字符')
    return null
  }
  return ensureTargetTypeOption(name, name)
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadList()
}

const handleReset = () => {
  Object.assign(filters, {
    keyword: '',
    supplierId: null,
    targetType: '',
    currency: '',
    quoteDateRange: [],
    validStatus: ''
  })
  handleSearch()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    supplierId: null,
    targetType: SUPPLIER_INQUIRY_TARGET_TYPES.manual,
    targetId: null,
    targetCode: '',
    targetName: '',
    specification: '',
    unit: '',
    price: null,
    currency: LOCAL_DEFAULT_CURRENCY,
    taxRate: 0,
    moq: null,
    deliveryDays: '',
    quoteDate: dayjs().format('YYYY-MM-DD'),
    validUntil: '',
    contactName: '',
    contactPhone: '',
    remark: '',
    changeLogs: []
  })
  targetOptions.value = []
  targetSelectValue.value = null
  resetTargetCreate()
  formRef.value?.clearValidate?.()
}

const handleAdd = async () => {
  resetForm()
  await loadSupplierOptions()
  dialog.mode = 'create'
  dialog.visible = true
}

const openWithDetail = async (row: any, mode: 'edit' | 'detail') => {
  const { code, data, message } = await getSupplierInquiryDetail({ id: row.id })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  resetForm()
  Object.assign(form, data || {})
  ensureTargetTypeOption(form.targetType)
  if (form.supplierId && !supplierOptions.value.some(item => item.id === form.supplierId)) {
    supplierOptions.value.push({
      id: form.supplierId,
      code: form.supplierCode,
      name: form.supplierName,
      contact: form.contactName,
      phone: form.contactPhone
    })
  }
  if (form.targetId) {
    const option = buildTargetOption(
      { id: form.targetId },
      form.targetCode,
      form.targetName,
      form.specification,
      form.unit,
      form.price
    )
    targetOptions.value = [option]
    targetSelectValue.value = option.value
  } else if (form.targetName) {
    const option = buildManualTargetOption(form.targetName)
    targetOptions.value = [option]
    targetSelectValue.value = option.value
  }
  dialog.mode = mode
  dialog.visible = true
}

const handleDetail = (row: any) => openWithDetail(row, 'detail')

const switchDetailToEdit = () => {
  dialog.mode = 'edit'
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认删除该询价记录吗？删除后将无法恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const { code, message } = await deleteSupplierInquiry({ id: row.id })
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    ElMessage.success('删除成功')
    loadList()
  } catch {
    // 用户取消删除。
  }
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  saving.value = true
  try {
    const payload = { ...form }
    delete payload.changeLogs
    const { code, message } = await saveOrUpdateSupplierInquiry({ ...payload })
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    loadList()
  } finally {
    saving.value = false
  }
}

const handleSupplierChange = (supplierId: any, _label = '', option?: any) => {
  const supplier = option || supplierOptions.value.find(item => sameId(item.id, supplierId))
  if (!supplier) return
  if (!form.contactName) form.contactName = supplier.contact || ''
  if (!form.contactPhone) form.contactPhone = supplier.phone || ''
}

const handleTargetTypeChange = () => {
  Object.assign(form, {
    targetId: null,
    targetCode: '',
    targetName: '',
    specification: '',
    unit: '',
    price: null
  })
  targetSelectValue.value = null
  targetOptions.value = []
  resetTargetCreate()
  if (isMasterTargetType.value) {
    loadTargetOptions('')
  }
}

const handleTargetSelect = (value: string) => {
  if (!value) {
    Object.assign(form, {
      targetId: null,
      targetCode: '',
      targetName: '',
      specification: '',
      unit: ''
    })
    return
  }
  const target = targetOptions.value.find(item => item.value === value)
  if (!target) return
  form.targetId = target.manual ? null : target.id
  form.targetCode = target.manual ? '' : target.code || ''
  form.targetName = target.name || ''
  form.specification = target.specification || ''
  form.unit = target.unit || ''
  if (target.price != null && form.price == null) {
    form.price = Number(target.price)
  }
  formRef.value?.clearValidate?.('targetName')
}

const loadTargetOptions = async (keyword = '') => {
  if (!form.targetType || !isMasterTargetType.value) return
  targetLoading.value = true
  try {
    const params = targetQueryParams(form.targetType, keyword)
    const request = targetRequest(form.targetType)
    const { code, data } = await request(params)
    if (code === 200) {
      const manualOptions = targetOptions.value.filter(item => item.manual)
      const masterOptions = (data?.list || []).map((item: any) => mapTargetOption(form.targetType, item))
      targetOptions.value = [
        ...manualOptions,
        ...masterOptions.filter((item: any) => !manualOptions.some(manual => manual.value === item.value))
      ]
    }
  } finally {
    targetLoading.value = false
  }
}

const targetQueryParams = (type: string, keyword: string) => {
  const base: any = { pageNum: 1, pageSize: 20 }
  if (!keyword) return base
  if (type === SUPPLIER_INQUIRY_TARGET_TYPES.material) return { ...base, likeName: keyword }
  return { ...base, keywords: keyword }
}

const targetRequest = (type: string) => {
  const requestMap: Record<string, (data: any) => Promise<any>> = {
    [SUPPLIER_INQUIRY_TARGET_TYPES.material]: getMaterialPage,
    [SUPPLIER_INQUIRY_TARGET_TYPES.fabric]: getFabricPage,
    [SUPPLIER_INQUIRY_TARGET_TYPES.packaging]: getPackagingPage,
    [SUPPLIER_INQUIRY_TARGET_TYPES.umbrellaFrame]: getUmbrellaFramePage,
    [SUPPLIER_INQUIRY_TARGET_TYPES.product]: getProductPage
  }
  return requestMap[type]
}

const resetTargetCreate = () => {
  targetAdding.value = false
  newTargetName.value = ''
}

const handleCreateTarget = () => {
  if (!form.targetType) {
    ElMessage.warning('请先选择询价类型')
    return
  }
  const label = newTargetName.value.trim()
  if (!label) return
  const option = buildManualTargetOption(label)
  if (!targetOptions.value.some(item => item.value === option.value)) {
    targetOptions.value.unshift(option)
  }
  targetSelectValue.value = option.value
  handleTargetSelect(option.value)
  ElMessage.success('新增成功')
  resetTargetCreate()
}

const mapTargetOption = (type: string, item: any) => {
  if (type === SUPPLIER_INQUIRY_TARGET_TYPES.material) {
    return buildTargetOption(item, '', item.name, item.size, '', item.price)
  }
  if (type === SUPPLIER_INQUIRY_TARGET_TYPES.fabric) {
    return buildTargetOption(
      item,
      '',
      [item.typeName, item.modelName].filter(Boolean).join('-'),
      item.widthName ? `门幅 ${item.widthName}` : '',
      item.unit,
      item.price
    )
  }
  if (type === SUPPLIER_INQUIRY_TARGET_TYPES.packaging) {
    return buildTargetOption(
      item,
      '',
      [item.typeName, item.name].filter(Boolean).join('-'),
      item.size,
      '个',
      item.price
    )
  }
  if (type === SUPPLIER_INQUIRY_TARGET_TYPES.umbrellaFrame) {
    return buildTargetOption(
      item,
      '',
      [item.functionName, item.typeName, item.lengthName, item.diameterName, item.ribCountName, item.materialName]
        .filter(Boolean)
        .join('/'),
      item.specificAttribute,
      item.unit,
      item.price
    )
  }
  return buildTargetOption(
    item,
    item.productCode,
    item.productCode || '产品',
    item.descriptionZh,
    '支',
    item.sellingPrice
  )
}

const buildTargetOption = (item: any, code: string, name: string, specification: string, unit: string, price: any) => {
  return {
    id: item.id,
    value: `master:${item.id}`,
    manual: false,
    code: code || '',
    name: name || '-',
    specification: specification || '',
    unit: unit || '',
    price,
    label: [code, name, specification].filter(Boolean).join(' / ')
  }
}

const buildManualTargetOption = (name: string) => {
  return {
    id: null,
    value: `manual:${name}`,
    manual: true,
    code: '',
    name,
    specification: '',
    unit: '',
    price: null,
    label: name
  }
}

const handleSupplierHistory = async (row: any) => {
  history.visible = true
  history.row = row
  history.title = '供应商报价历史'
  history.subject = row.supplierName || '-'
  history.subtitle = '该供应商的全部询价记录'
  history.loading = true
  history.list = []
  try {
    const { code, data, message } = await getSupplierInquiryHistory({
      pageNum: 1,
      pageSize: 50,
      supplierId: row.supplierId
    })
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    history.list = data?.list || []
  } finally {
    history.loading = false
  }
}

const handleTargetHistory = async (row: any) => {
  history.visible = true
  history.row = row
  history.title = '询价对象报价历史'
  history.subject = row.targetName || '-'
  history.subtitle = '该询价对象的全部历史报价'
  history.loading = true
  history.list = []
  try {
    const params: any = {
      pageNum: 1,
      pageSize: 50,
      targetType: row.targetType
    }
    if (row.targetId) {
      params.targetId = row.targetId
    } else {
      params.keyword = row.targetName
    }
    const { code, data, message } = await getSupplierInquiryHistory(params)
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    history.list = data?.list || []
  } finally {
    history.loading = false
  }
}

onMounted(() => {
  loadSupplierOptions()
  loadList()
})
</script>

<style lang="scss" scoped>
.supplier-inquiry-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
  padding: 16px;
  overflow: hidden;
  background: #f5f7fb;
}

.toolbar-panel,
.filter-panel,
.table-panel,
.stats-row {
  border-radius: 6px;
  background: #fff;
  box-shadow: 0 1px 2px rgb(15 23 42 / 4%);
}

.toolbar-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}

.page-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #1f2937;

  span {
    color: #8a95a7;
    font-size: 13px;
  }
}

.filter-panel {
  padding: 16px;
}

.filter-form {
  display: grid;
  grid-template-columns:
    minmax(220px, 1.15fr)
    minmax(220px, 1.15fr)
    minmax(180px, 0.9fr)
    minmax(140px, 0.65fr)
    minmax(360px, 1.55fr)
    minmax(160px, 0.75fr)
    auto;
  gap: 12px 16px;
  align-items: end;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-form-item__label) {
    height: auto;
    margin-bottom: 6px;
    padding: 0;
    color: #64748b;
    font-size: 13px;
    line-height: 18px;
  }

  :deep(.el-form-item__content) {
    width: 100%;
  }

  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-date-editor),
  :deep(.quick-add-select) {
    width: 100%;
  }
}

.filter-actions {
  justify-self: start;

  :deep(.el-form-item__label) {
    display: none;
  }

  :deep(.el-form-item__content) {
    display: flex;
    flex-wrap: nowrap;
    gap: 8px;
  }

  :deep(.el-button) {
    min-width: 72px;
    margin-left: 0;
  }
}

@media (max-width: 1600px) {
  .filter-form {
    grid-template-columns: minmax(220px, 1.2fr) minmax(200px, 1fr) minmax(180px, 0.9fr) minmax(140px, 0.7fr);
  }

  .filter-item--date,
  .filter-actions {
    grid-column: span 2;
  }
}

@media (max-width: 1080px) {
  .filter-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .filter-item--date,
  .filter-actions {
    grid-column: span 2;
  }
}

@media (max-width: 720px) {
  .filter-form {
    grid-template-columns: minmax(0, 1fr);
  }

  .filter-item--date,
  .filter-actions {
    grid-column: auto;
  }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1px;
  overflow: hidden;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 16px;
  background: #fff;

  span {
    color: #8a95a7;
    font-size: 13px;
  }

  strong {
    color: #1f2937;
    font-size: 18px;
  }
}

.table-panel {
  flex: 1;
  min-height: 0;
  padding: 16px;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  padding: 8px 0 0;
}

.main-line {
  color: #303133;
}

.supplier-history-link,
.target-history-link {
  height: auto;
  min-height: 22px;
  padding: 0;
  white-space: normal;
  text-align: left;
}

.sub-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.object-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-unit-cell {
  display: inline-flex;
  width: 100%;
  justify-content: flex-end;
  white-space: nowrap;
}

.operation-stack {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;

  :deep(.el-button) {
    margin-left: 0;
  }
}

.target-option {
  display: flex;
  justify-content: space-between;
  gap: 12px;

  span {
    color: #909399;
    font-size: 12px;
  }
}

.history-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 12px;

  span {
    color: #909399;
  }
}

.section-title {
  margin: 4px 0 12px;
  color: #1f2937;
  font-weight: 600;
}

.change-log-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.change-log-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.change-log-item {
  padding: 10px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #f8fafc;
}

.change-log-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;

  span {
    color: #909399;
    font-size: 12px;
  }
}

.change-log-lines {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.change-log-line {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr) 16px minmax(0, 1fr);
  gap: 8px;
  color: #606266;
  font-size: 12px;

  .field {
    color: #1f2937;
    font-weight: 500;
  }

  .before,
  .after {
    overflow-wrap: anywhere;
  }

  .arrow {
    color: #909399;
    text-align: center;
  }
}

:deep(.el-form-item) {
  margin-right: 16px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-date-editor.el-input),
:deep(.el-date-editor.el-input__wrapper),
:deep(.el-select),
:deep(.el-input) {
  width: 100%;
}
</style>
