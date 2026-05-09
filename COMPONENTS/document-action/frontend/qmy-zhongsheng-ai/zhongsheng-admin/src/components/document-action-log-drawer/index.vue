<template>
  <el-drawer v-model="visible" :title="drawerTitle" size="680px" @open="loadLogs">
    <div class="document-action-toolbar">
      <el-tag :type="lockMeta.type">{{ lockMeta.label }}</el-tag>
      <el-tag v-if="needsReconfirm" type="warning">待重新确认</el-tag>
      <div class="toolbar-spacer" />
      <el-button v-if="canUnlock" :loading="actionLoading" @click="handleUnlock">解锁</el-button>
      <el-button v-if="canReconfirm" type="primary" :loading="actionLoading" @click="handleReconfirm">
        重新确认
      </el-button>
      <el-button v-if="canAssign" @click="openOwnerDialog">改派负责人</el-button>
    </div>

    <div v-loading="loading" class="log-bubble-shell">
      <div v-if="rows.length" class="log-bubble-overview">
        <div>
          <div class="log-bubble-overview-title">关键动作时间线</div>
          <div class="log-bubble-overview-meta">
            <span>共 {{ total || rows.length }} 条记录</span>
            <span>最近：{{ formatDateTime(rows[0]?.createTime) }}</span>
          </div>
        </div>
      </div>

      <div v-if="rows.length" class="log-bubble-list">
        <article v-for="(row, index) in rows" :key="logKey(row, index)" class="log-bubble-item">
          <div class="log-bubble-rail">
            <span class="log-bubble-dot" :class="`is-${actionTagType(row.actionType)}`" />
            <span v-if="index !== rows.length - 1" class="log-bubble-line" />
          </div>
          <div class="log-bubble-card">
            <div class="log-bubble-header">
              <div class="log-bubble-header-main">
                <el-tag size="small" effect="dark" :type="actionTagType(row.actionType)">
                  {{ actionLabel(row.actionType) }}
                </el-tag>
                <div class="log-bubble-summary">{{ row.diffSummary || buildLogFallback(row) }}</div>
              </div>
              <div class="log-bubble-header-side">
                <div class="log-bubble-time">{{ formatDateTime(row.createTime) }}</div>
                <div class="log-bubble-operator">{{ row.operatorName || 'system' }}</div>
              </div>
            </div>

            <div class="log-bubble-facts">
              <div v-if="hasStatusTransition(row)" class="log-fact-chip">
                <span class="log-fact-label">状态</span>
                <strong class="log-fact-value">{{ formatStatusTransition(row.beforeStatus, row.afterStatus) }}</strong>
              </div>
              <div v-if="hasLockTransition(row)" class="log-fact-chip">
                <span class="log-fact-label">锁定</span>
                <strong class="log-fact-value">
                  {{ formatLockTransition(row.beforeLockState, row.afterLockState) }}
                </strong>
              </div>
              <div v-if="row.actionReason" class="log-fact-chip log-fact-chip--reason">
                <span class="log-fact-label">原因</span>
                <strong class="log-fact-value">{{ row.actionReason }}</strong>
              </div>
            </div>

            <div class="log-bubble-detail-panel">
              <div class="log-detail-title">字段明细</div>
              <div v-if="row.diffDetail" class="log-detail-text">{{ row.diffDetail }}</div>
              <div v-else class="log-detail-empty">暂无字段明细</div>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else-if="!loading" :image-size="60" description="暂无动作日志" />
    </div>

    <el-pagination
      v-if="total > query.pageSize"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      layout="total, prev, pager, next"
      :total="total"
      @current-change="loadLogs"
    />

    <el-dialog v-model="ownerDialogVisible" title="改派负责人" width="420px" append-to-body>
      <el-form label-width="76px">
        <el-form-item label="负责人">
          <el-select v-model="ownerForm.ownerId" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in userOptions"
              :key="item.id"
              :label="item.nickName || item.userName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="ownerForm.reason" type="textarea" :rows="3" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ownerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleAssignOwner">确定</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerUserOptions } from '@/api/zs/customer'
import {
  assignDocumentOwner,
  getDocumentActionLogPage,
  reconfirmDocument,
  warningUnlockDocument
} from '@/api/zs/document'
import { ZS_PERMISSIONS } from '@/constant/permissions'
import { usePermissionStore } from '@/views/zs/store/modules/permission'

const props = defineProps<{
  modelValue: boolean
  documentType: string
  documentId: string | number | null
  title?: string
  lockState?: string
  needsReconfirm?: boolean
}>()

const emit = defineEmits<{
  (event: 'update:modelValue', value: boolean): void
  (event: 'changed'): void
}>()

const permissionStore = usePermissionStore()
const loading = ref(false)
const actionLoading = ref(false)
const rows = ref<any[]>([])
const total = ref(0)
const userOptions = ref<any[]>([])
const ownerDialogVisible = ref(false)
const ownerForm = reactive({ ownerId: null as any, reason: '' })
const query = reactive({ pageNum: 1, pageSize: 10 })

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const lockState = computed(() => props.lockState || 'open')
const needsReconfirm = computed(() => Boolean(props.needsReconfirm))
const drawerTitle = computed(() => props.title || '动作日志')

const hasPermission = (permission: string) =>
  permissionStore.permissions.includes('*') || permissionStore.permissions.includes(permission)

const canUnlock = computed(
  () =>
    hasPermission(ZS_PERMISSIONS.document.unlockApprove) &&
    (lockState.value === 'locked' || lockState.value === 'pending_unlock')
)
const canAssign = computed(() => hasPermission(ZS_PERMISSIONS.document.reassignOwner))
const canReconfirm = computed(
  () =>
    hasPermission(ZS_PERMISSIONS.document.unlockRequest) &&
    (lockState.value === 'temporary_unlocked' || needsReconfirm.value)
)

const lockMeta = computed(() => {
  const map: Record<string, any> = {
    open: { label: '未锁定', type: 'info' },
    locked: { label: '已锁定', type: 'danger' },
    pending_unlock: { label: '待审批', type: 'warning' },
    temporary_unlocked: { label: '已解锁', type: 'success' }
  }
  return map[lockState.value] || { label: lockState.value || '-', type: 'info' }
})

watch(
  () => props.modelValue,
  value => {
    if (value) loadLogs()
  }
)

const loadLogs = async () => {
  if (!props.documentId) return
  loading.value = true
  try {
    const { code, data, message } = await getDocumentActionLogPage({
      documentType: props.documentType,
      documentId: props.documentId,
      ...query
    })
    if (code !== 200) return ElMessage.warning(message)
    rows.value = data?.list || data?.records || []
    total.value = Number(data?.total || 0)
  } finally {
    loading.value = false
  }
}

const runReasonAction = async (title: string, api: any, required = true, promptMessage = '请输入原因') => {
  if (!props.documentId) return
  let reason = ''
  if (required) {
    const result = await ElMessageBox.prompt(promptMessage, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputValidator: value => Boolean(value?.trim()) || '原因不能为空'
    })
    reason = result.value
  }
  actionLoading.value = true
  try {
    const { code, message } = await api({ documentType: props.documentType, documentId: props.documentId, reason })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
    await loadLogs()
    emit('changed')
  } finally {
    actionLoading.value = false
  }
}

const handleUnlock = () =>
  runReasonAction('解锁', warningUnlockDocument, true, '解锁后当前单据可临时修改，保存后需要重新确认。请输入解锁原因。')
const handleReconfirm = () => runReasonAction('重新确认', reconfirmDocument, false)

const openOwnerDialog = async () => {
  ownerForm.ownerId = null
  ownerForm.reason = ''
  ownerDialogVisible.value = true
  if (!userOptions.value.length) {
    const { code, data, message } = await getCustomerUserOptions()
    if (code !== 200) return ElMessage.warning(message)
    userOptions.value = data || []
  }
}

const handleAssignOwner = async () => {
  if (!ownerForm.ownerId) return ElMessage.warning('请选择负责人')
  actionLoading.value = true
  try {
    const { code, message } = await assignDocumentOwner({
      documentType: props.documentType,
      documentId: props.documentId,
      ownerId: ownerForm.ownerId,
      reason: ownerForm.reason
    })
    if (code !== 200) return ElMessage.warning(message)
    ownerDialogVisible.value = false
    ElMessage.success('改派成功')
    await loadLogs()
    emit('changed')
  } finally {
    actionLoading.value = false
  }
}

const actionLabel = (value: string) => {
  const map: Record<string, string> = {
    confirm: '确认',
    cancel: '取消',
    convert_order: '转订单',
    create_from_quote: '报价生成',
    create_independent: '新增独立采购',
    save_independent: '保存独立采购',
    create_from_order: '订单生成采购',
    update_remark: '更新备注',
    update_inbound_status: '更新入库状态',
    receive_quantity: '数量入库',
    adjust_inbound: '入库调整',
    adjust_inbound_after_unlock: '入库调整',
    receive_all: '全部入库',
    complete: '自动完单',
    request_unlock: '申请解锁',
    warning_unlock: '解锁',
    approve_unlock: '同意解锁',
    reject_unlock: '拒绝解锁',
    reconfirm: '重新确认',
    assign_owner: '改派负责人',
    sync_assign_owner: '同步改派',
    update_after_unlock: '解锁后更新'
  }
  return map[value] || value || '-'
}

const actionTagType = (value: string) => {
  const map: Record<string, any> = {
    confirm: 'success',
    cancel: 'danger',
    convert_order: 'primary',
    create_from_quote: 'primary',
    create_independent: 'primary',
    save_independent: 'info',
    create_from_order: 'primary',
    update_remark: 'info',
    update_inbound_status: 'warning',
    receive_quantity: 'success',
    adjust_inbound: 'warning',
    adjust_inbound_after_unlock: 'warning',
    receive_all: 'success',
    complete: 'success',
    request_unlock: 'warning',
    warning_unlock: 'warning',
    approve_unlock: 'success',
    reject_unlock: 'danger',
    reconfirm: 'primary',
    assign_owner: 'warning',
    sync_assign_owner: 'info',
    update_after_unlock: 'primary'
  }
  return map[value] || 'info'
}

const statusLabel = (value: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    confirmed: '已确认',
    converted: '已转订单',
    purchasing: '采购中',
    in_production: '生产中',
    completed: '已完成',
    cancelled: '已取消',
    manual_reconcile: '需核对'
  }
  return value ? map[value] || value : '-'
}

const lockLabel = (value: string) => {
  const map: Record<string, string> = {
    open: '未锁定',
    locked: '已锁定',
    pending_unlock: '待审批',
    temporary_unlocked: '已解锁'
  }
  return value ? map[value] || value : '-'
}

const hasStatusTransition = (row: any) => Boolean(row?.beforeStatus || row?.afterStatus)
const hasLockTransition = (row: any) => Boolean(row?.beforeLockState || row?.afterLockState)
const formatStatusTransition = (beforeStatus: string, afterStatus: string) =>
  `${statusLabel(beforeStatus)} -> ${statusLabel(afterStatus)}`
const formatLockTransition = (beforeLockState: string, afterLockState: string) =>
  `${lockLabel(beforeLockState)} -> ${lockLabel(afterLockState)}`
const buildLogFallback = (row: any) =>
  row?.actionReason ? `${actionLabel(row.actionType)}：${row.actionReason}` : actionLabel(row?.actionType)
const logKey = (row: any, index: number) => row?.id || `${row?.createTime || 'log'}-${index}`
const formatDateTime = (value: any) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-')
</script>

<style lang="scss" scoped>
.document-action-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.toolbar-spacer {
  flex: 1;
}

.log-bubble-shell {
  min-height: 220px;
  padding-bottom: 8px;
}

.log-bubble-overview {
  margin-bottom: 10px;
  padding: 10px 12px;
  border: 1px solid #dbe5f5;
  border-radius: 8px;
  background: #f7faff;
}

.log-bubble-overview-title {
  font-size: 14px;
  font-weight: 700;
  color: #1f2f4f;
}

.log-bubble-overview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 12px;
  margin-top: 4px;
  font-size: 12px;
  color: #667085;
}

.log-bubble-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.log-bubble-item {
  display: grid;
  grid-template-columns: 18px minmax(0, 1fr);
  gap: 10px;
  align-items: stretch;
}

.log-bubble-rail {
  position: relative;
  display: flex;
  justify-content: center;
}

.log-bubble-dot {
  position: relative;
  z-index: 1;
  width: 10px;
  height: 10px;
  margin-top: 14px;
  border-radius: 999px;
  background: #6c7ea6;
  box-shadow: 0 0 0 4px rgba(108, 126, 166, 0.12);
}

.log-bubble-dot.is-success {
  background: #18a957;
  box-shadow: 0 0 0 4px rgba(24, 169, 87, 0.12);
}

.log-bubble-dot.is-warning {
  background: #d78b10;
  box-shadow: 0 0 0 4px rgba(215, 139, 16, 0.12);
}

.log-bubble-dot.is-danger {
  background: #d84a4a;
  box-shadow: 0 0 0 4px rgba(216, 74, 74, 0.12);
}

.log-bubble-dot.is-primary {
  background: #1d6eff;
  box-shadow: 0 0 0 4px rgba(29, 110, 255, 0.12);
}

.log-bubble-line {
  position: absolute;
  top: 26px;
  bottom: -12px;
  width: 1px;
  background: #d6e0ef;
}

.log-bubble-card {
  padding: 12px 14px;
  border: 1px solid #dbe5f5;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 6px 16px rgba(17, 38, 80, 0.06);
}

.log-bubble-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: flex-start;
}

.log-bubble-header-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.log-bubble-summary {
  font-size: 14px;
  line-height: 1.45;
  font-weight: 600;
  color: #1f2f4f;
  word-break: break-word;
}

.log-bubble-header-side {
  min-width: 136px;
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #667085;
}

.log-bubble-time {
  font-weight: 700;
  color: #355384;
}

.log-bubble-operator {
  color: #667085;
  word-break: break-word;
}

.log-bubble-facts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.log-fact-chip {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 7px 10px;
  border-radius: 8px;
  background: #f4f7fc;
  border: 1px solid #e4eaf3;
}

.log-fact-chip--reason {
  background: #fff7e8;
  border-color: #f3d19e;
}

.log-fact-label {
  font-size: 11px;
  color: #667085;
}

.log-fact-value {
  font-size: 12px;
  line-height: 1.45;
  color: #24324d;
  white-space: pre-wrap;
  word-break: break-word;
}

.log-bubble-detail-panel {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e4eaf3;
}

.log-detail-title {
  margin-bottom: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #606266;
}

.log-detail-text {
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 13px;
  color: #303133;
  word-break: break-word;
}

.log-detail-empty {
  color: #909399;
}

@media (max-width: 1080px) {
  .log-bubble-header {
    flex-direction: column;
  }

  .log-bubble-header-side {
    min-width: 0;
    text-align: left;
  }
}
</style>
