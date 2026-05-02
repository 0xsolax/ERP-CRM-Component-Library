<template>
  <el-drawer v-model="drawerVisible" title="进度" size="500px" direction="rtl" @close="onDestroy">
    <div class="date-filter">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="-"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        value-format="YYYY-MM-DD HH:mm:ss"
        style="width: 300px"
        @change="handleDateChange"
      />
    </div>

    <div class="progress-timeline" v-loading="loading">
      <el-timeline v-if="progressList.length > 0">
        <el-timeline-item
          v-for="(item, index) in progressList"
          :key="index"
          :timestamp="item.time"
          placement="top"
          :color="index === 0 ? '#409eff' : ''"
        >
          <div class="timeline-content" :class="{ highlight: index === 0 }">
            <div class="timeline-title">{{ typeEnum[item.type] }}</div>
            <!-- <div class="timeline-time">{{ item.time }}</div> -->
            <div class="timeline-details">
              <div>入库人：{{ item.createUserName }}</div>
              <div>
                <span v-if="item.type == 1">入库数量：{{ item.number }}</span>
                <span v-if="item.type == 2">还原数量：{{ item.number }}</span>
              </div>
              <div>自动分配订单号：{{ item.detail || '--' }}</div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无数据" />
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStoreOrderProgressList } from '@/api/admin/warehouse'

const attrs = useAttrs()
const { storeOrderIdList, onDestroy } = attrs as any

const drawerVisible = ref(true)
const loading = ref(false)
const dateRange = ref<string[]>([])
const progressList = ref<any[]>([])

const typeEnum = {
  '1': '入库',
  '2': '入库（还原）'
}

const loadProgressList = async () => {
  loading.value = true
  try {
    const params: any = {
      storeOrderIdList
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const { code, data, message } = await getStoreOrderProgressList(params)
    if (code !== 200) return ElMessage.warning(message)
    progressList.value = (data || []).map((item: any) => {
      let detailText = '--'
      if (item?.operationDetail && item?.operationDetail?.length) {
        const detailArray = item.operationDetail
        detailText = detailArray.map((d: any) => d.orderCode).join('、')
      }
      return {
        ...item,
        id: item.id,
        time: item.createTime || '',
        createUserName: item.createUserName || '--',
        number: item.number || 0,
        detail: detailText
      }
    })
  } finally {
    loading.value = false
  }
}

const handleDateChange = () => {
  loadProgressList()
}

onMounted(() => {
  loadProgressList()
})
</script>

<style lang="scss" scoped>
.date-filter {
  margin-bottom: 20px;
}

.progress-timeline {
  .el-timeline {
    padding-left: 0;
  }
  .timeline-content {
    padding: 12px;
    background-color: #f5f7fa;
    border-radius: 4px;

    &.highlight {
      .timeline-title {
        color: #333;
      }
    }

    .timeline-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 8px;
    }

    .timeline-time {
      font-size: 13px;
      color: #909399;
      margin-bottom: 8px;
    }

    .timeline-details {
      font-size: 13px;
      color: #606266;
      line-height: 1.8;
      white-space: pre-line;
    }
  }
}
</style>
