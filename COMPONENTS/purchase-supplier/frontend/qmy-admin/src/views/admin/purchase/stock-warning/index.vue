<template>
  <div class="table-box">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getStockWarningList"
      :dataCallback="dataCallback"
      selectId="id"
      rowKey="id"
      @selection-change="handleSelectionChange"
    >
      <template #tableHeader>
        <el-button type="primary" v-permission="'pur:yt:storeWarning:submitApplyPurchase'" @click="handleBatchApply">
          批量申购
        </el-button>
        <el-button type="primary" @click="handleStoreWarningTest">扫描库存预警</el-button>
      </template>

      <template #image="scope">
        <div v-if="scope.row.imageUrl" class="image-preview-wrapper" @click="handleImagePreview(scope.row.imageUrl)">
          <el-image :src="scope.row.imageUrl" style="width: 50px; height: 50px; cursor: pointer" fit="cover" />
        </div>
        <span v-else style="color: #999">-</span>
      </template>
      <template #warningTime="scope">
        <span>{{ formatDate(scope.row.warningTime) }}</span>
      </template>

      <template #stockInfo="scope">
        <div class="stock-warning-cell">
          <div class="stock-item">
            <span class="stock-label">实际库存：</span>
            <span class="stock-value">{{ scope.row.realStore || 0 }}</span>
          </div>
          <div class="stock-item">
            <span class="stock-label">可用库存：</span>
            <span class="stock-value">{{ scope.row.enableStore || 0 }}</span>
          </div>
          <div class="stock-item">
            <span class="stock-label">占用库存：</span>
            <span class="stock-value">{{ scope.row.occupyStore || 0 }}</span>
          </div>
        </div>
      </template>

      <template #transitInfo="scope">
        <div class="stock-warning-cell">
          <div class="stock-item">
            <span class="stock-label">实际在途：</span>
            <span class="stock-value">{{ scope.row.realTransit || 0 }}</span>
          </div>
          <div class="stock-item">
            <span class="stock-label">可用在途：</span>
            <span class="stock-value">{{ scope.row.enableTransit || 0 }}</span>
          </div>
          <div class="stock-item">
            <span class="stock-label">占用在途：</span>
            <span class="stock-value">{{ scope.row.occupyTransit || 0 }}</span>
          </div>
        </div>
      </template>

      <template #operation="scope">
        <el-button
          size="small"
          type="primary"
          link
          v-if="!scope.row.isApplyPurchase"
          v-permission="'pur:yt:storeWarning:submitApplyPurchase'"
          @click="handleApply(scope.row)"
        >
          申购
        </el-button>
        <el-button size="small" type="default" link disabled v-if="scope.row.isApplyPurchase">已申购</el-button>
      </template>
    </bz-table>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ColumnProps } from '@/interface/table'
import { ElMessage } from 'element-plus'
import { getStockWarningList, storeWarningTest } from '@/api/admin/purchase/stock-warning'
import dayjs from 'dayjs'

const router = useRouter()
const tableRef = ref()
const selectedIds = ref<any[]>([])
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const formatDate = (date: string) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

const dataCallback = (data: any) => {
  const list = (data?.list || []).map((item: any) => ({
    ...item,
    imageUrl: item.imageList?.[0]?.url || '',
    specName:
      item.itemList
        ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
        .join('/') || '-'
  }))
  return {
    list,
    total: Number(data?.total || 0)
  }
}

// 搜索配置
const searchColumns = computed(() => [
  {
    label: '仓库',
    prop: 'storeName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入仓库名称',
        clearable: true
      }
    }
  },
  {
    label: '产品ID',
    prop: 'productCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
])

// 表格列配置
const columns: ColumnProps[] = [
  {
    type: 'selection',
    width: 50,
    fixed: 'left'
  },
  {
    label: '仓库名称',
    prop: 'storeName',
    align: 'center'
  },
  {
    label: '产品ID',
    prop: 'productCode',
    align: 'center',
    width: 170
  },
  {
    label: '规格名称',
    prop: 'specName',
    align: 'center'
  },
  {
    label: '图片',
    prop: 'image',
    align: 'center',
    showOverflowTooltip: false
  },
  {
    label: '库存信息',
    prop: 'stockInfo',
    align: 'center'
  },
  {
    label: '在途信息',
    prop: 'transitInfo',
    align: 'center'
  },
  {
    label: '预警信息',
    prop: 'warningReason',
    align: 'center'
  },
  {
    label: '预警时间',
    prop: 'warningTime',
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    width: 120,
    fixed: 'right',
    align: 'center'
  }
]

const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleImagePreview = (imageUrl: string) => {
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const handleApply = (row: any) => {
  router.push({
    path: '/purchase/stock-warning/batch-apply',
    query: {
      ids: JSON.stringify([row.id])
    }
  })
}

const handleStoreWarningTest = async () => {
  const { code, message } = await storeWarningTest(undefined)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
}

const handleBatchApply = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要申购的产品')
    return
  }
  router.push({
    path: '/purchase/stock-warning/batch-apply',
    query: {
      ids: JSON.stringify(selectedIds.value)
    }
  })
}
</script>

<style scoped lang="scss">
.image-preview-wrapper {
  display: inline-block;
  transition: all 0.3s;
}

.stock-warning-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 0;

  .stock-item {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;

    .stock-label {
      color: #606266;
      margin-right: 4px;
    }

    .stock-value {
      color: #303133;
      font-weight: 500;

      &.warning {
        color: #f56c6c;
      }
    }
  }
}
</style>
