<template>
  <div class="operation-log-page">
    <section class="filter-bar">
      <el-form :model="query" inline label-width="76px">
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            range-separator="至"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-time="defaultTime"
          />
        </el-form-item>
        <el-form-item label="业务模块">
          <el-select v-model="query.moduleType" clearable placeholder="请选择" class="filter-select">
            <el-option label="销售管理" value="sales" />
            <el-option label="采购管理" value="purchase" />
            <el-option label="生产管理" value="production" />
          </el-select>
        </el-form-item>
        <el-form-item label="单据类型">
          <el-select v-model="query.documentType" clearable placeholder="请选择" class="filter-select">
            <el-option label="报价单" value="quote" />
            <el-option label="订单" value="order" />
            <el-option label="采购单" value="purchase" />
            <el-option label="生产总单" value="production" />
          </el-select>
        </el-form-item>
        <el-form-item label="动作">
          <el-select v-model="query.actionType" clearable placeholder="请选择" class="filter-select">
            <el-option v-for="item in actionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作账号">
          <el-input v-model="query.operatorKeyword" clearable placeholder="请输入账号/姓名" class="filter-input" />
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" clearable placeholder="单据号/摘要/原因" class="filter-input" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-panel">
      <el-table v-loading="loading" :data="rows" border height="100%" empty-text="暂无操作日志">
        <el-table-column prop="createTime" label="操作时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作账号" min-width="120" />
        <el-table-column prop="moduleLabel" label="业务模块" width="110" />
        <el-table-column label="操作对象" min-width="190">
          <template #default="{ row }">
            <div class="document-cell">
              <span>{{ row.documentTypeLabel || '-' }}</span>
              <strong>{{ row.documentCode || '-' }}</strong>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="actionLabel" label="动作" width="110">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.actionType)" effect="plain">
              {{ row.actionLabel || row.actionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态变化" width="150">
          <template #default="{ row }">{{ statusTransition(row) }}</template>
        </el-table-column>
        <el-table-column prop="actionReason" label="操作原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="diffSummary" label="摘要" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="92" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[15, 30, 50, 100]"
        :total="total"
        @size-change="loadRows"
        @current-change="loadRows"
      />
    </div>

    <el-drawer v-model="detailVisible" title="操作日志详情" size="680px">
      <div v-if="activeRow" class="detail-shell">
        <div class="detail-header">
          <el-tag :type="actionTagType(activeRow.actionType)" effect="dark">
            {{ activeRow.actionLabel || activeRow.actionType }}
          </el-tag>
          <div>
            <div class="detail-title">{{ activeRow.diffSummary || '操作记录' }}</div>
            <div class="detail-meta">
              {{ formatDateTime(activeRow.createTime) }} / {{ activeRow.operatorName || 'system' }}
            </div>
          </div>
        </div>

        <div class="detail-grid">
          <div>
            <span>业务模块</span>
            <strong>{{ activeRow.moduleLabel || '-' }}</strong>
          </div>
          <div>
            <span>操作对象</span>
            <strong>{{ activeRow.documentTypeLabel || '-' }} {{ activeRow.documentCode || '-' }}</strong>
          </div>
          <div>
            <span>状态变化</span>
            <strong>{{ statusTransition(activeRow) }}</strong>
          </div>
          <div>
            <span>锁定变化</span>
            <strong>{{ lockTransition(activeRow) }}</strong>
          </div>
        </div>

        <div v-if="activeRow.actionReason" class="detail-block">
          <div class="detail-block-title">操作原因</div>
          <div class="detail-block-text">{{ activeRow.actionReason }}</div>
        </div>

        <div class="detail-block">
          <div class="detail-block-title">字段明细</div>
          <pre v-if="activeRow.diffDetail" class="detail-pre">{{ activeRow.diffDetail }}</pre>
          <el-empty v-else :image-size="54" description="暂无字段明细" />
        </div>

        <div class="detail-actions">
          <el-button v-if="documentUrl(activeRow)" type="primary" @click="openDocument(activeRow)">打开单据</el-button>
          <span v-else class="deleted-hint">单据已删除或暂不支持跳转，审计快照仍保留。</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="system-operation-log">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { RefreshRight, Search } from '@element-plus/icons-vue'
import { getSystemOperationLogPage } from '@/api/zs/system/operation-log'

const router = useRouter()
const loading = ref(false)
const rows = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const activeRow = ref<any>(null)
const defaultTime = [new Date(2000, 0, 1, 0, 0, 0), new Date(2000, 0, 1, 23, 59, 59)] as [Date, Date]
const timeRange = ref<string[]>([
  dayjs().subtract(30, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
  dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
])

const query = reactive({
  pageNum: 1,
  pageSize: 15,
  moduleType: '',
  documentType: '',
  actionType: '',
  operatorKeyword: '',
  keyword: ''
})

const actionOptions = [
  { label: '确认', value: 'confirm' },
  { label: '删除', value: 'delete' },
  { label: '取消', value: 'cancel' },
  { label: '转订单', value: 'convert_order' },
  { label: '解锁', value: 'warning_unlock' },
  { label: '解锁后修改', value: 'update_after_unlock' },
  { label: '重新确认', value: 'reconfirm' },
  { label: '改派负责人', value: 'assign_owner' },
  { label: '入库', value: 'inbound' },
  { label: '完单', value: 'complete' }
]

const buildParams = () => ({
  ...query,
  startTime: timeRange.value?.[0] || null,
  endTime: timeRange.value?.[1] || null
})

const loadRows = async () => {
  loading.value = true
  try {
    const { code, data, message } = await getSystemOperationLogPage(buildParams())
    if (code !== 200) return ElMessage.warning(message)
    rows.value = data?.list || data?.records || []
    total.value = Number(data?.total || 0)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadRows()
}

const handleReset = () => {
  query.pageNum = 1
  query.pageSize = 15
  query.moduleType = ''
  query.documentType = ''
  query.actionType = ''
  query.operatorKeyword = ''
  query.keyword = ''
  timeRange.value = [
    dayjs().subtract(30, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
  ]
  loadRows()
}

const openDetail = (row: any) => {
  activeRow.value = row
  detailVisible.value = true
}

const openDocument = (row: any) => {
  const url = documentUrl(row)
  if (!url) return
  router.push(url)
}

const documentUrl = (row: any) => {
  if (!row?.documentId || row.actionType === 'delete') return ''
  const query = { id: row.documentId }
  if (row.documentType === 'quote') return { path: '/quote/edit', query }
  if (row.documentType === 'order') return { path: '/order/edit', query }
  if (row.documentType === 'purchase') return { path: '/purchase/index', query: { detailId: row.documentId } }
  if (row.documentType === 'production') return { path: '/production/order', query: { id: row.documentId } }
  return ''
}

const actionTagType = (actionType: string) => {
  const typeMap: Record<string, any> = {
    confirm: 'success',
    delete: 'danger',
    cancel: 'warning',
    warning_unlock: 'warning',
    update_after_unlock: 'warning',
    reconfirm: 'success',
    inbound: 'success',
    complete: 'success'
  }
  return typeMap[actionType] || 'info'
}

const statusTransition = (row: any) => {
  if (!row?.beforeStatus && !row?.afterStatus) return '-'
  if (row.beforeStatus === row.afterStatus) return row.afterStatus || row.beforeStatus || '-'
  return `${row.beforeStatus || '-'} -> ${row.afterStatus || '-'}`
}

const lockTransition = (row: any) => {
  if (!row?.beforeLockState && !row?.afterLockState) return '-'
  if (row.beforeLockState === row.afterLockState) return row.afterLockState || row.beforeLockState || '-'
  return `${row.beforeLockState || '-'} -> ${row.afterLockState || '-'}`
}

const formatDateTime = (value: string) => {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-'
}

onMounted(loadRows)
</script>

<style scoped lang="scss">
.operation-log-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: calc(100vh - 120px);
  padding: 16px;
}

.filter-bar,
.table-panel {
  background: #fff;
  border-radius: 6px;
}

.filter-bar {
  padding: 16px 16px 0;
}

.filter-select {
  width: 150px;
}

.filter-input {
  width: 180px;
}

.table-panel {
  flex: 1;
  min-height: 420px;
  padding: 16px;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  padding: 4px 0;
}

.document-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  line-height: 1.35;

  span {
    color: #909399;
    font-size: 12px;
  }

  strong {
    color: #1f2d3d;
    font-weight: 600;
  }
}

.detail-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-header {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #f8fafc;
}

.detail-title {
  color: #1f2d3d;
  font-size: 16px;
  font-weight: 600;
}

.detail-meta {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;

  div {
    min-height: 64px;
    padding: 12px;
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    background: #fff;
  }

  span {
    display: block;
    margin-bottom: 8px;
    color: #909399;
    font-size: 12px;
  }

  strong {
    color: #303133;
    font-weight: 600;
    word-break: break-word;
  }
}

.detail-block {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.detail-block-title {
  padding: 10px 12px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8fafc;
  color: #303133;
  font-weight: 600;
}

.detail-block-text,
.detail-pre {
  margin: 0;
  padding: 12px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-height: 36px;
}

.deleted-hint {
  color: #909399;
  font-size: 13px;
}
</style>
