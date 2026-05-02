<template>
  <el-dialog v-model="visible" title="退货详情" width="500px" @close="handleClose">
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

      <el-timeline>
        <el-timeline-item v-for="(item, index) in filteredRecords" :key="index" :timestamp="item.time" placement="top">
          <div class="timeline-content">
            <div class="timeline-title">{{ item.title }}</div>
            <div class="timeline-detail">退货原因：{{ item.reason }}</div>
            <div class="timeline-detail">退货数量：{{ item.count }}</div>
            <div class="timeline-detail">退货人：{{ item.user }}</div>
            <div class="timeline-detail">订单数量：{{ item.orderCount }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnListBySpec } from '@/api/admin/sales/order'
import dayjs from 'dayjs'

interface Props {
  refundData?: any
}

const props = defineProps<Props>()

const visible = ref(true)
const dateRange = ref<string[]>([])
const refundRecords = ref<any[]>([])

const refundTypeMap: Record<number, string> = {
  1: '订单退货',
  2: '采购单退货'
}

const loadRefundRecords = async () => {
  const { code, data, message } = await getReturnListBySpec({
    orderSubItemId: props.refundData.orderSubItemId
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  refundRecords.value = (data || []).map((item: any) => ({
    title: refundTypeMap[item.type] || '订单退货',
    reason: item.reason || '-',
    count: item.returnNumber || 0,
    user: item.returnUserName || '-',
    orderCount: `${item.beforeReturnNumber || 0}—>${(item.beforeReturnNumber || 0) - (item.returnNumber || 0)}`,
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

const handleClose = () => {
  visible.value = false
}

onMounted(() => {
  loadRefundRecords()
})
</script>

<style lang="scss" scoped>
.refund-detail-container {
  :deep(.el-timeline-item__timestamp) {
    font-size: 13px;
    color: #909399;
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
