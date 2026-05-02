<template>
  <el-dialog v-model="dialogVisible" title="占用详情" width="600px" @close="onDestroy">
    <div class="stat-cards">
      <div class="stat-card stock" :class="{ active: activeTab === 'stock' }" @click="handleTabClick('stock')">
        <div class="stat-icon">
          <el-icon :size="32" color="#409eff"><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">
            占用库存
            <el-tooltip content="仓库中已被占用的产品数量" placement="top">
              <el-icon><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-value">{{ formatNumber(occupiedStock) }}</div>
        </div>
      </div>
      <div class="stat-card transit" :class="{ active: activeTab === 'transit' }" @click="handleTabClick('transit')">
        <div class="stat-icon">
          <el-icon :size="32" color="#409eff"><Van /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">
            占用在途
            <el-tooltip content="在途中已被占用的产品数量" placement="top">
              <el-icon><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
          <div class="stat-value">{{ formatNumber(occupiedTransit) }}</div>
        </div>
      </div>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchText"
        placeholder="客户名称"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #suffix>
          <el-icon class="search-icon" @click="handleSearch"><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table :data="list" style="width: 100%" v-loading="loading" :max-height="300">
      <el-table-column label="客户名称" prop="customerName" align="center">
        <template #default="{ row }">
          {{ row.customerName || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="订单编号" prop="orderCode" align="center">
        <template #default="{ row }">
          {{ row.orderCode || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="占用数量" align="center">
        <template #default="{ row }">
          <span>
            {{ activeTab === 'stock' ? row.enterNumber || 0 : row.occupyTransitNumber || 0 }}
          </span>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button type="primary" @click="dialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { Search, Box, Van, InfoFilled } from '@element-plus/icons-vue'
import { getStoreOccupyDetail, getTransitOccupyDetail } from '@/api/admin/warehouse'
import { ElMessage } from 'element-plus'

const attrs = useAttrs()
const { rowData, onDestroy, initialTab } = attrs as any

const dialogVisible = ref(true)
const searchText = ref('')
const activeTab = ref(initialTab || 'stock')

const occupiedStock = ref(rowData?.occupyStore || 0)
const occupiedTransit = ref(rowData?.occupyTransit || 0)

const list = ref<any[]>([])
const loading = ref(false)

const loadData = async () => {
  console.log('rowData', rowData)
  loading.value = true
  try {
    const apiCall = activeTab.value === 'stock' ? getStoreOccupyDetail : getTransitOccupyDetail
    const { code, data, message } = await apiCall({
      specificationId: rowData.specificationId,
      customerName: searchText.value
    })

    if (code !== 200) return ElMessage.warning(message)
    list.value = data || []
  } finally {
    loading.value = false
  }
}

const handleTabClick = (tab: string) => {
  if (activeTab.value === tab) return
  activeTab.value = tab
  searchText.value = ''
  loadData()
}

const handleSearch = () => {
  loadData()
}

onMounted(() => {
  loadData()
})

const formatNumber = (num: number) => {
  return num.toLocaleString()
}
</script>

<style lang="scss" scoped>
.search-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;

  .search-icon {
    cursor: pointer;

    &:hover {
      color: #409eff;
    }
  }
}

.stat-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  .stat-card {
    flex: 1;
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    background: #f5f7fa;
    cursor: pointer;
    transition: all 0.3s;
    border: 1px solid transparent;

    &:hover {
      background: #ecf5ff;
    }

    &.active {
      background: linear-gradient(135deg, #e8f4ff 0%, #f0f9ff 100%);
      border-color: #409eff;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
      margin-right: 16px;
    }

    .stat-content {
      .stat-label {
        font-size: 15px;
        display: flex;
        align-items: center;
        gap: 4px;
        margin-bottom: 4px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
}
</style>
