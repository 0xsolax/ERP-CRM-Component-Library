<template>
  <div class="quotation-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getSedQuotationList"
      :dataCallback="dataCallback"
      :searchDataCallback="searchDataCallback"
      :searchCol="searchCol"
      :customGridConfig="customGridConfig"
      :toolButton="false"
      :searchClick="handleSearchClick"
    >
      <template #tableHeader>
        <div class="tab-filters">
          <el-button-group>
            <el-button
              v-for="tab in tabList"
              :key="tab.value"
              :type="activeTab === tab.value ? 'primary' : 'default'"
              @click="handleTabChange(tab.value)"
            >
              {{ tab.label }}
            </el-button>
          </el-button-group>
        </div>
        <el-button type="primary" v-permission="'sal:sed:quotation:saveOrUpdate'" @click="handleAdd">新增</el-button>
        <el-button type="primary" @click="handleMergeToOrder">合并转订单</el-button>
      </template>
      <template #purchaseCost="{ row }">
        <template v-if="row.procurementCostState">
          <el-button
            class="pending-btn"
            type="warning"
            link
            v-permission="'sal:sed:quotation:procurementConfirm'"
            @click="handleProcurementCost(row)"
          >
            待确认
          </el-button>
        </template>
        <span v-else>{{ row.purchaseCost }}</span>
      </template>
      <template #logisticsCost="{ row }">
        <template v-if="row.logisticsCostState">
          <el-button
            class="pending-btn"
            type="warning"
            link
            v-permission="'sal:sed:quotation:logisticsConfirm'"
            @click="handleLogisticsCost(row)"
          >
            待确认
          </el-button>
        </template>
        <span v-else>{{ row.logisticsCost }}</span>
      </template>
      <template #totalCost="{ row }">
        <template v-if="row.totalCostState">
          <el-button class="pending-btn" type="warning" link style="cursor: default">待确认</el-button>
        </template>
        <span v-else>{{ row.totalCost }}</span>
      </template>
      <template #orderAmount="{ row }">
        <span v-if="row.currency == '1'">¥{{ row.orderAmount }}</span>
        <span v-else-if="row.currency == '2'">${{ row.orderAmount }}</span>
        <span v-else>{{ row.orderAmount }}</span>
      </template>
      <template #status="{ row }">
        <span :class="['status-text', getStatusClass(row.status)]">
          {{ row.statusLabel }}
        </span>
      </template>

      <template #operation="{ row }">
        <div class="operation-btns">
          <!-- 暂存 -->
          <template v-if="row.status === '0'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>

          <!-- 计算成本中 -->
          <template v-else-if="row.status === '1'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>

          <!-- 计算成本完毕 -->
          <template v-else-if="row.status === '2'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:submitAudit'"
              @click="handleSubmitAudit(row)"
            >
              提交审核
            </el-button>
          </template>

          <!-- 审核中 -->
          <template v-else-if="row.status === '3'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>

          <!-- 审核通过 -->
          <template v-else-if="row.status === '4'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleRecreate(row)"
            >
              再次创建
            </el-button>
            <el-button
              v-if="row.shiftStatus == '0'"
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:oneKeyToOrder'"
              @click="handleConvertOrder(row)"
            >
              一键转订单
            </el-button>
          </template>

          <!-- 总裁未审核，财务未审核 -->
          <template v-else-if="row.status === '5'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>

          <!-- 总裁审核通过，财务未审核 -->
          <template v-else-if="row.status === '6'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>

          <!-- 总裁未审核，财务审核通过 -->
          <template v-else-if="row.status === '7' || row.status === '8'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>

          <!-- 审核驳回 -->
          <template v-else-if="row.status === '-1'">
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:quotationDetail'"
              @click="handleDetail(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:exportQuotation'"
              @click="handleExport(row)"
            >
              导出
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleRecreate(row)"
            >
              再次创建
            </el-button>
          </template>

          <template v-else>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>
        </div>
      </template>
    </bz-table>
  </div>
</template>

<script lang="tsx" setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import { ColumnProps } from '@/interface/table'
import { dynamic } from '@bzlab/bz-core'
import { getSedQuotationList, submitQuotationAudit } from '@/api/sed/sales/quotation'
import { getQuotationStatusLabel } from '@/constant/sed/quotation'
import { getCurrencyLabel } from '@/constant/sed/sales'
import { useUserStore } from '@/views/sed/store/modules/user'
import QuotationDialog from './components/quotation-dialog.vue'
import DetailDialog from './components/detail-dialog.vue'
import ConvertOrderDialog from './components/convert-order-dialog.vue'
import ProcurementCostDialog from './components/procurement-cost-dialog.vue'
import LogisticsCostDialog from './components/logistics-cost-dialog.vue'
import MergeToOrderDialog from './components/merge-to-order-dialog.vue'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getSedAdminToken, getSedAdminTenantInfo } from '@/utils/auth'

const searchCol = { xs: 2, sm: 3, md: 4, lg: 5, xl: 6 }
const customGridConfig = {
  xs: '1fr',
  sm: '1fr 1fr 1fr',
  md: '1fr 1fr 1fr 1fr',
  lg: '1fr 1fr 300px 310px 200px',
  xl: '250px 250px 300px 310px 200px'
}
const route = useRoute()
const userStore = useUserStore()
const tableRef = ref()
const activeTab = ref((route.query.tab as string) || 'all')

const tabList = [
  { label: '全部', value: 'all' },
  { label: '暂存', value: '0' },
  { label: '计算成本中', value: '1' },
  { label: '计算成本完毕', value: '2' },
  { label: '财务未审核', value: 'financePending' },
  { label: '总裁未审核', value: 'presidentPending' },
  { label: '审核通过', value: '4' },
  { label: '审核驳回', value: '-1' }
]

const handleTabChange = (value: string) => {
  activeTab.value = value
  tableRef.value?.getTableList()
}

const searchDataCallback = (params: any) => {
  params.status = undefined
  params.financePending = undefined
  params.presidentPending = undefined

  if (activeTab.value === 'financePending') {
    params.financePending = true
  } else if (activeTab.value === 'presidentPending') {
    params.presidentPending = true
  } else if (activeTab.value !== 'all') {
    params.status = activeTab.value
  }
  return params
}

const handleSearchClick = () => {
  activeTab.value = 'all'
}

const getStatusClass = (status: string) => {
  const classMap: Record<string, string> = {
    '0': 'status-draft',
    '1': 'status-calculating',
    '2': 'status-calculated',
    '3': 'status-auditing',
    '4': 'status-approved',
    '5': 'status-auditing',
    '6': 'status-approved',
    '7': 'status-approved',
    '8': 'status-approved',
    '-1': 'status-rejected'
  }
  return classMap[status] || ''
}

const searchColumns = computed(() => [
  {
    label: '报价单编号',
    prop: 'quotationCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '客户名称',
    prop: 'customerName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '创建时间',
    prop: 'createTime',
    search: {
      el: 'el-date-picker',
      props: {
        type: 'daterange',
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间',
        clearable: true,
        'value-format': 'YYYY-MM-DD HH:mm:ss'
      },
      event: {
        change: (scope: any) => {
          if (scope?.length) {
            tableRef.value.searchParams.createStartTime = scope[0]
            tableRef.value.searchParams.createEndTime = scope[1]
          } else {
            tableRef.value.searchParams.createStartTime = null
            tableRef.value.searchParams.createEndTime = null
          }
        }
      }
    }
  },
  {
    label: '上次编辑时间',
    prop: 'updateTime',
    search: {
      el: 'el-date-picker',
      props: {
        type: 'daterange',
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间',
        clearable: true,
        'value-format': 'YYYY-MM-DD HH:mm:ss'
      },
      event: {
        change: (scope: any) => {
          if (scope?.length) {
            tableRef.value.searchParams.lastEditStartTime = scope[0]
            tableRef.value.searchParams.lastEditEndTime = scope[1]
          } else {
            tableRef.value.searchParams.lastEditStartTime = null
            tableRef.value.searchParams.lastEditEndTime = null
          }
        }
      }
    }
  }
])

const columns: ColumnProps[] = [
  { type: 'selection', width: 55 },
  { label: '报价单编号', prop: 'quotationCode', align: 'center' },
  { label: '客户名称', prop: 'customerName', align: 'center' },
  { label: '业务员', prop: 'salesmanName', align: 'center' },
  { label: '创建时间', prop: 'createTime', align: 'center' },
  { label: '上次编辑时间', prop: 'updateTime', align: 'center' },
  {
    label: '采购预估成本',
    prop: 'purchaseCost',
    align: 'center',
    headerRender: () => (
      <div class="cost-header">
        <span>采购预估成本</span>
        <el-tooltip content="具体成本咨询采购部门" placement="top">
          <el-icon class="cost-tip-icon">
            <QuestionFilled />
          </el-icon>
        </el-tooltip>
      </div>
    )
  },
  {
    label: '物流预估成本',
    prop: 'logisticsCost',
    align: 'center',
    headerRender: () => (
      <div class="cost-header">
        <span>物流预估成本</span>
        <el-tooltip content="包括海运费、进仓费、拖车费、订舱费等" placement="top">
          <el-icon class="cost-tip-icon">
            <QuestionFilled />
          </el-icon>
        </el-tooltip>
      </div>
    )
  },
  { label: '总成本', prop: 'totalCost', align: 'center' },
  { label: '币种', prop: 'currencyValue', align: 'center' },
  { label: '订单金额', prop: 'orderAmount', align: 'center' },
  { label: '状态', prop: 'status', align: 'center', width: 110 },
  { label: '操作', prop: 'operation', align: 'center', width: 200, fixed: 'right' }
]

const dataCallback = (data: any) => {
  const list = (data?.list || []).map((item: any) => ({
    ...item,
    salesmanName: item.salesmanName || '-',
    purchaseCost: item.procurementCost,
    procurementCostState: item.procurementCostState == '0',
    logisticsCostState: item.logisticsCostState == '0',
    totalCostState: item.totalCostState == '0',
    currency: item.currency,
    currencyValue: getCurrencyLabel(item.currency) || '-',
    statusLabel: getQuotationStatusLabel(item.status)
  }))
  return {
    list,
    total: Number(data?.total || 0)
  }
}

const handleAdd = () => {
  const params = {
    id: 'quotationAddDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => tableRef.value?.getTableList()
    },
    render: QuotationDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'quotationEditDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: QuotationDialog
  }
  dynamic.show(params)
}

const handleExport = async (row: any) => {
  await ElMessageBox.confirm(`确定要导出报价单 ${row.quotationCode} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { status, message } = await downloadAxiosBlobFile({
    url: '/api/sal/sed/quotation/exportQuotation',
    headers: {
      'qiaomoyun-tenant': getSedAdminTenantInfo()?.id,
      'qiaomoyun-token': getSedAdminToken()
    },
    data: { quotationId: row.id },
    method: 'post'
  })
  if (status !== 200) return ElMessage.error(message)
  ElMessage.success('导出成功')
}

const handleSubmitAudit = async (row: any) => {
  await ElMessageBox.confirm('确认提交审核？', '提交审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await submitQuotationAudit({
    id: row.id,
    salesmanId: userStore.userId
  })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('提交成功')
  tableRef.value?.getTableList()
}

const handleDetail = (row: any) => {
  const params = {
    id: 'quotationDetailDialog',
    el: '#app',
    data: {
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: DetailDialog
  }
  dynamic.show(params)
}

const handleRecreate = (row: any) => {
  const params = {
    id: 'quotationAddDialog',
    el: '#app',
    data: {
      isEdit: false,
      isRecreate: true,
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: QuotationDialog
  }
  dynamic.show(params)
}

const handleConvertOrder = (row: any) => {
  const params = {
    id: 'convertOrderDialog',
    el: '#app',
    data: {
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: ConvertOrderDialog
  }
  dynamic.show(params)
}

const handleProcurementCost = (row: any) => {
  const params = {
    id: 'procurementCostDialog',
    el: '#app',
    data: {
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: ProcurementCostDialog
  }
  dynamic.show(params)
}

const handleLogisticsCost = (row: any) => {
  const params = {
    id: 'logisticsCostDialog',
    el: '#app',
    data: {
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: LogisticsCostDialog
  }
  dynamic.show(params)
}

const handleMergeToOrder = () => {
  const params = {
    id: 'mergeToOrderDialog',
    el: '#app',
    data: {
      callback: () => tableRef.value?.getTableList()
    },
    render: MergeToOrderDialog
  }
  dynamic.show(params)
}
</script>

<style lang="scss" scoped>
.quotation-container {
  .tab-filters {
    margin-bottom: 15px;
    width: 100%;
  }

  .cost-tag {
    margin-left: 5px;
    border-radius: 50%;
    min-width: 20px;
    height: 20px;
    padding: 0 5px;
  }

  .status-text {
    &.status-draft {
      color: #409eff;
    }
    &.status-calculating {
      color: #e6a23c;
    }
    &.status-calculated {
      color: #303133;
    }
    &.status-auditing {
      color: #409eff;
    }
    &.status-approved {
      color: #67c23a;
    }
    &.status-finance-approved,
    &.status-president-approved {
      color: #e6a23c;
    }
    &.status-rejected {
      color: #f56c6c;
    }
  }

  .pending-btn {
    font-weight: 400;
  }

  .status-count-tag {
    margin-left: 5px;
    border-radius: 50%;
    min-width: 20px;
    height: 20px;
    padding: 0 5px;
  }

  .operation-btns {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
<style lang="scss">
.quotation-container {
  .cost-header {
    .cost-tip-icon {
      color: #909399;
      font-size: 14px;
      cursor: pointer;
      vertical-align: middle;
      margin-bottom: 4px;
    }
  }
}
</style>
