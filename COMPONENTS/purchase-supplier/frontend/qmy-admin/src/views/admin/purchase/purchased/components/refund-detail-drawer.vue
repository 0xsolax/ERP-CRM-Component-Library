<template>
  <el-drawer v-model="drawerVisible" title="退货详情" size="500px" direction="rtl" @close="onDestroy">
    <div class="refund-detail-container">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        style="width: 70%; margin-bottom: 20px"
        value-format="YYYY-MM-DD"
        clearable
      />

      <el-timeline v-if="filteredRecords.length">
        <el-timeline-item v-for="(item, index) in filteredRecords" :key="index" :timestamp="item.time" placement="top">
          <div class="timeline-content">
            <div class="timeline-title">{{ item.title }}</div>
            <div class="timeline-detail">退货原因：{{ item.reason }}</div>
            <div class="timeline-detail">退货数量：{{ item.count }}</div>
            <div class="timeline-detail">退货人：{{ item.user }}</div>
            <div class="timeline-detail">采购数量：{{ item.purchaseCount }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无退货记录" />
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnDetail } from '@/api/admin/purchase/purchased'
import dayjs from 'dayjs'

const attrs = useAttrs()
const { purchaseItemId, onDestroy } = attrs as any

const drawerVisible = ref(true)
const dateRange = ref<string[]>([])
const refundRecords = ref<any[]>([])

const refundTypeMap: Record<number, string> = {
  1: '订单退货',
  2: '采购单退货'
}

const loadRefundDetail = async () => {
  const { code, data, message } = await getReturnDetail({ itemId: purchaseItemId })
  if (code !== 200) return ElMessage.warning(message)

  const returnList = data?.returnList || []
  refundRecords.value = returnList.map((item: any) => ({
    title: refundTypeMap[item.type] || '采购单退货',
    reason: item.reason || '-',
    count: item.returnNumber || 0,
    user: item.returnUserName || '-',
    purchaseCount: `${item.beforeReturnNumber || 0}—>${(item.beforeReturnNumber || 0) - (item.returnNumber || 0)}`,
    time: item.createTime || '-'
  }))
}

const filteredRecords = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    return refundRecords.value
  }

  const [startDate, endDate] = dateRange.value
  return refundRecords.value.filter(record => {
    if (!record.time) return false
    const recordDate = dayjs(record.time).format('YYYY-MM-DD')
    return recordDate >= startDate && recordDate <= endDate
  })
})

onMounted(() => {
  if (purchaseItemId) {
    loadRefundDetail()
  }
})
</script>

<style lang="scss" scoped>
.refund-detail-container {
  :deep(.el-timeline-item__timestamp) {
    font-size: 13px;
    color: #909399;
  }

  :deep(.el-timeline) {
    padding-left: 0;
  }
}

.timeline-content {
  padding: 10px 0;
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.timeline-detail {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 3px;
}
</style>
