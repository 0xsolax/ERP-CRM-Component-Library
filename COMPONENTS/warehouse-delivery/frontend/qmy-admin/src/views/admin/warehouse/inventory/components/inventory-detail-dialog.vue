<template>
  <el-drawer
    v-model="drawerVisible"
    custom-class="inventory-detail-drawer"
    title="产品详情"
    size="80%"
    direction="rtl"
    @close="onDestroy"
  >
    <div class="product-info-header">
      <div class="info-left">
        <img
          v-if="productData.image"
          :src="productData.image"
          class="product-image"
          @click="previewImage([productData.image])"
        />
        <div>
          <span class="label">产品ID：</span>
          <span class="value">{{ productData.productCode || '-' }}</span>
          <span class="label" style="margin-left: 40px">产品备注：</span>
          <span class="value">{{ productData.remark || '-' }}</span>
        </div>
      </div>
      <div class="info-right">
        <transition name="search-expand">
          <el-input v-if="showSearch" v-model="searchText" placeholder="搜索规格名称" clearable style="width: 200px" />
        </transition>
        <el-icon class="search-icon" @click="toggleSearch">
          <Search />
        </el-icon>
      </div>
    </div>

    <div class="spec-table-wrapper">
      <el-table :data="filteredSpecList" style="width: 100%">
        <el-table-column label="规格名称" width="120" align="center">
          <template #default="{ row }">
            <div class="spec-name-cell">
              <el-tooltip v-if="row.description" :content="row.description" placement="top">
                <div>{{ row.specName }}</div>
              </el-tooltip>
              <div v-if="!row.description">{{ row.specName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" align="center">
          <template #default="{ row }">
            <img v-if="row.image" :src="row.image" class="table-image" @click="previewImage([row.image])" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="库存信息" align="center">
          <template #default="{ row }">
            <div class="info-cell">
              <div>实际库存：{{ row.realStore || 0 }}</div>
              <div>可用库存：{{ row.enableStore || 0 }}</div>
              <div>占用库存：{{ row.occupyStore || 0 }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="在途信息" align="center">
          <template #default="{ row }">
            <div class="info-cell">
              <div>实际在途：{{ row.realTransit || 0 }}</div>
              <div>可用在途：{{ row.enableTransit || 0 }}</div>
              <div>占用在途：{{ row.occupyTransit || 0 }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库位" align="center">
          <template #default="{ row }">
            {{ row.locationName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" align="center">
          <template #default="{ row }">
            <el-button
              type="warning"
              link
              size="small"
              v-if="row.id"
              v-permission="'sto:yt:store:setWarning'"
              @click="handleWarning(row)"
            >
              预警规则
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              v-permission="'sto:yt:store:history'"
              @click="handleHistory(row)"
            >
              历史流向
            </el-button>
            <el-button type="primary" link size="small" v-if="row.specificationId" @click="handleOccupation(row)">
              占用详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- <template #footer>
      <el-button type="primary" @click="drawerVisible = false">关闭</el-button>
    </template> -->

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getStoreProduct } from '@/api/admin/warehouse'
import OccupationDetailDialog from './occupation-detail-dialog.vue'
import WarningRuleDialog from './warning-rule-dialog.vue'

const router = useRouter()
const attrs = useAttrs()
const { rowData, onDestroy } = attrs as any

const drawerVisible = ref(true)
const showSearch = ref(false)
const searchText = ref('')
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const productData = ref<any>({
  productCode: rowData?.code || '',
  image: rowData?.productImage?.[0]?.url || '',
  remark: rowData?.remark || ''
})

const specList = ref<any[]>([])

onMounted(async () => {
  await loadProductDetail()
})

const loadProductDetail = async () => {
  if (!rowData?.id) return
  const { code, data, message } = await getStoreProduct({ productId: rowData.id })
  if (code !== 200) return ElMessage.warning(message)
  if (data) {
    productData.value = {
      productCode: data.code || '',
      image: '',
      remark: data.remark || ''
    }
    specList.value =
      data.stockList?.map((item: any) => {
        const specName = item.itemList?.map((i: any) => i.categorySpecificationItemValue).join('-') || ''
        const image = item.imageList?.[0]?.url || ''
        return {
          ...item,
          specName,
          image,
          realStore: item.realStore || 0,
          enableStore: item.enableStore || 0,
          occupyStore: item.occupyStore || 0,
          realTransit: item.realTransit || 0,
          enableTransit: item.enableTransit || 0,
          occupyTransit: item.occupyTransit || 0
        }
      }) || []
  }
}

const filteredSpecList = computed(() => {
  if (!searchText.value) {
    return specList.value
  }
  return specList.value.filter(item => item.specName.includes(searchText.value))
})

const toggleSearch = () => {
  showSearch.value = !showSearch.value
  if (!showSearch.value) {
    searchText.value = ''
  }
}

const previewImage = (images: string[]) => {
  viewerImageList.value = images
  showViewer.value = true
}

const handleHistory = (row: any) => {
  router.push({
    path: '/warehouse/inventory/history',
    query: {
      specificationId: row.specificationId,
      productId: rowData?.id || ''
    }
  })
  drawerVisible.value = false
}

const handleOccupation = (row: any) => {
  const params = {
    id: 'occupationDetailDialog',
    el: '#app',
    data: {
      rowData: row
    },
    render: OccupationDetailDialog
  }
  dynamic.show(params)
}

const handleWarning = (row: any) => {
  const params = {
    id: 'warningRuleDialog',
    el: '#app',
    data: {
      dialogType: 'single',
      storeId: row.id
    },
    render: WarningRuleDialog
  }
  dynamic.show(params)
}
</script>

<style lang="scss" scoped>
.product-info-header {
  padding: 15px 0;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 63px;

  .info-left {
    display: flex;
    align-items: center;
    flex: 1;

    .product-image {
      width: 50px;
      height: 50px;
      margin-right: 16px;
      object-fit: cover;
      cursor: pointer;
      border-radius: 4px;

      &:hover {
        opacity: 0.8;
      }
    }

    .label {
      color: #909399;
      margin-right: 8px;
    }

    .value {
      color: #303133;
    }
  }

  .info-right {
    display: flex;
    align-items: center;
    gap: 10px;

    .search-icon {
      font-size: 20px;
      color: #909399;
      cursor: pointer;
      transition: color 0.3s;
      flex-shrink: 0;

      &:hover {
        color: #409eff;
      }
    }
  }
}

.spec-table-wrapper {
  .spec-name-cell {
    .el-tag {
      margin-bottom: 4px;
    }
  }

  .table-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    cursor: pointer;
    border-radius: 4px;

    &:hover {
      opacity: 0.8;
    }
  }

  .info-cell {
    font-size: 13px;
    color: #606266;
    text-align: center;
  }
}

.search-expand-enter-active,
.search-expand-leave-active {
  transition: all 0.3s ease;
}

.search-expand-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.search-expand-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>

<style lang="scss">
.inventory-detail-drawer {
  .el-drawer__body {
    padding-top: 0;
  }
  .el-drawer__header {
    margin-bottom: 0;
  }
}
</style>
