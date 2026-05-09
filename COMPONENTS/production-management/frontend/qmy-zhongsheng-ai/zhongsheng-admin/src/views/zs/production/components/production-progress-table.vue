<template>
  <div class="production-progress">
    <el-empty v-if="!master?.id" description="暂无生产总单" :image-size="compact ? 48 : 80" />
    <template v-else>
      <div class="progress-summary">
        <div class="summary-item">
          <span>生产单号</span>
          <strong>{{ master.code || '-' }}</strong>
        </div>
        <div class="summary-item">
          <span>订单数量</span>
          <strong>{{ formatAmount(master.totalOrderQty) }}</strong>
        </div>
        <div class="summary-item">
          <span>已安排</span>
          <strong>{{ formatAmount(master.totalPlannedQty) }}</strong>
        </div>
        <div class="summary-item">
          <span>已入库</span>
          <strong>{{ formatAmount(master.totalInboundQty) }}</strong>
        </div>
        <div class="summary-item">
          <span>已交货</span>
          <strong>{{ formatAmount(master.totalDeliveredQty) }}</strong>
        </div>
        <div class="summary-item">
          <span>剩余待交</span>
          <strong>{{ formatAmount(master.totalRemainingDeliveryQty) }}</strong>
        </div>
      </div>

      <el-table :data="master.progressRows || []" border :size="compact ? 'small' : 'default'">
        <el-table-column label="图片" width="76">
          <template #default="{ row }">
            <el-image v-if="firstProductImage(row)" :src="firstProductImage(row)" fit="cover" class="product-image" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="productCode" label="产品编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="productName" label="产品" min-width="180" show-overflow-tooltip />
        <el-table-column prop="orderQty" label="订单数量" width="100">
          <template #default="{ row }">{{ formatAmount(row.orderQty) }}</template>
        </el-table-column>
        <el-table-column prop="purchasedQty" label="已采购" width="100">
          <template #default="{ row }">{{ formatAmount(row.purchasedQty) }}</template>
        </el-table-column>
        <el-table-column prop="inboundQty" label="已入库" width="100">
          <template #default="{ row }">{{ formatAmount(row.inboundQty) }}</template>
        </el-table-column>
        <el-table-column prop="plannedQty" label="已安排" width="100">
          <template #default="{ row }">{{ formatAmount(row.plannedQty) }}</template>
        </el-table-column>
        <el-table-column prop="availablePlanQty" label="可安排" width="100">
          <template #default="{ row }">{{ formatAmount(row.availablePlanQty) }}</template>
        </el-table-column>
        <el-table-column prop="deliveredQty" label="已交货" width="100">
          <template #default="{ row }">{{ formatAmount(row.deliveredQty) }}</template>
        </el-table-column>
        <el-table-column prop="remainingDeliveryQty" label="剩余待交" width="110">
          <template #default="{ row }">{{ formatAmount(row.remainingDeliveryQty) }}</template>
        </el-table-column>
        <el-table-column prop="progressStatus" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusMeta(row).type">{{ statusMeta(row).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="$slots.progressActions" label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <slot name="progressActions" :row="row" />
          </template>
        </el-table-column>
      </el-table>

      <el-table
        v-if="showBatches"
        :data="master.batches || []"
        border
        class="batch-table"
        :size="compact ? 'small' : 'default'"
      >
        <el-table-column prop="productionGroupName" label="生产组" min-width="140" />
        <el-table-column prop="productCode" label="产品编号" min-width="120" />
        <el-table-column prop="batchQty" label="批次数量" width="110">
          <template #default="{ row }">{{ formatAmount(row.batchQty) }}</template>
        </el-table-column>
        <el-table-column prop="plannedDeliveryDate" label="计划交期" width="120" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      </el-table>
    </template>
  </div>
</template>

<script lang="ts" setup name="production-progress-table">
withDefaults(
  defineProps<{
    master?: any
    compact?: boolean
    showBatches?: boolean
  }>(),
  {
    master: null,
    compact: false,
    showBatches: false
  }
)

const formatAmount = (value: any) => {
  const num = Number(value || 0)
  return Number.isFinite(num) ? num.toFixed(2) : '0.00'
}

const statusMeta = (row: any) => {
  const status = typeof row === 'string' ? row : row?.progressStatus
  if (status === 'released') {
    const availablePlanQty = Number(row?.availablePlanQty || 0)
    return availablePlanQty > 0 ? { label: '可安排生产', type: 'warning' } : { label: '待采购入库', type: 'warning' }
  }
  const map: Record<string, any> = {
    pending: { label: '待推进', type: 'info' },
    scheduled: { label: '已安排', type: 'primary' },
    delivering: { label: '交货中', type: 'warning' },
    completed: { label: '已完成', type: 'success' },
    manual_reconcile: { label: '需核对', type: 'danger' }
  }
  return map[status] || { label: status || '-', type: 'info' }
}

const firstProductImage = (row: any) => {
  const images = Array.isArray(row?.images) ? row.images : []
  const first = images[0]
  const snapshot = parseSnapshot(row?.sourceSnapshotJson)
  const snapshotImages = Array.isArray(snapshot.images) ? snapshot.images : []
  const snapshotFirst = snapshotImages[0]
  return row?.imageUrl || first?.url || first || snapshot.imageUrl || snapshotFirst?.url || snapshotFirst || ''
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
</script>

<style lang="scss" scoped>
.production-progress {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.summary-item {
  padding: 10px 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  background: var(--el-fill-color-lighter);

  span {
    display: block;
    margin-bottom: 4px;
    color: var(--el-text-color-secondary);
    font-size: 12px;
  }

  strong {
    color: var(--el-text-color-primary);
    font-size: 14px;
  }
}

.batch-table {
  margin-top: 4px;
}

.product-image {
  width: 46px;
  height: 46px;
  border-radius: 4px;
}
</style>
