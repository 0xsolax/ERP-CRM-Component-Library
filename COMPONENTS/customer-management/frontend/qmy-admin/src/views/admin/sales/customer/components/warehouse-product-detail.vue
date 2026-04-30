<template>
  <el-drawer
    v-model="drawerVisible"
    custom-class="warehouse-product-detail-drawer"
    title="产品详情"
    size="70%"
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
          <span class="value">{{ productData.productId || '-' }}</span>
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
        <el-table-column label="规格名称" width="180" align="center">
          <template #default="{ row }">
            <el-tooltip v-if="row.description" :content="row.description" placement="top">
              <span style="cursor: pointer">{{ row.name }}</span>
            </el-tooltip>
            <span v-else>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="180" align="center">
          <template #default="{ row }">
            <div class="table-images">
              <img
                v-for="(img, index) in row.images"
                :key="index"
                :src="img"
                class="table-image"
                @click="previewImage(row.images)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存信息" width="180" align="center">
          <template #default="{ row }">
            <div class="info-cell" v-if="row.storeStatus == '1'">实际库存：{{ row?.storeNumber || 0 }}</div>
            <div v-else>--</div>
          </template>
        </el-table-column>
        <el-table-column label="在途信息" width="180" align="center">
          <template #default="{ row }">
            <div class="info-cell" v-if="row.storeStatus == '1'">实际在途：{{ row?.transitNumber || 0 }}</div>
            <div v-else>--</div>
          </template>
        </el-table-column>
        <el-table-column label="库位" width="150" align="center">
          <template #default="{ row }">
            <div class="location-cell">
              <template v-if="row.storeStatus == '1'">
                {{ row.locationName || '--' }}
                <el-icon v-if="row.locationName" style="margin-left: 4px; cursor: pointer">
                  <Link />
                </el-icon>
              </template>
              <template v-else>--</template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="启用独立仓" width="150" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.warehouseEnabled" @change="handleWarehouseSwitch(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.storeStatus == '1'">
              <el-button type="primary" link size="small" @click="handleHistory(row)">历史流向</el-button>
              <el-button type="warning" link size="small" @click="handleWarning(row)">预警规则</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <!-- <el-button type="primary" @click="handleClose">关闭</el-button> -->
      </div>
    </template>
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
import { ElMessage, ElImageViewer } from 'element-plus'
import { Search, Link } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { useRouter } from 'vue-router'
import { getProductDetail } from '@/api/admin/product'
import { updateStoreStatus, getSpecificationList } from '@/api/admin/sales/customer'
import WarningRuleDialog from './warning-rule-dialog.vue'

const router = useRouter()
const attrs = useAttrs()
const { rowData, onDestroy } = attrs as any

const drawerVisible = ref(true)
const showSearch = ref(false)
const searchText = ref('')
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const productData = ref<any>({})
const specList = ref<any[]>([])

const loadProductDetail = async () => {
  if (!rowData || !rowData.id) return
  const { code: productCode, data: productInfo, message: productMsg } = await getProductDetail({ id: rowData.id })
  if (productCode !== 200) return ElMessage.warning(productMsg)

  productData.value = {
    productId: productInfo?.code || '',
    image: productInfo?.image || '',
    remark: productInfo?.remark || ''
  }

  // 获取规格列表
  const { code, data, message } = await getSpecificationList({
    customerId: rowData.customerId,
    productId: rowData.id
  })
  if (code !== 200) return ElMessage.warning(message)

  specList.value =
    (data || []).map((item: any) => {
      const images = item.specificationImages?.map((img: any) => img.url) || []
      return {
        ...item,
        id: item.id,
        images: images,
        locationName: item.locationName || '',
        storeStatus: item.storeStatus,
        warehouseEnabled: item.storeStatus == '1'
      }
    }) || []
}

onMounted(() => {
  loadProductDetail()
})

const filteredSpecList = computed(() => {
  let list = specList.value

  if (rowData.warehouseViewMode === 'enabled') {
    list = list.filter(item => item.storeStatus == '1')
  }

  if (!searchText.value) {
    return list
  }
  return list.filter(item => item.name?.includes(searchText.value))
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

const handleWarehouseSwitch = async (row: any) => {
  const { code, message } = await updateStoreStatus({
    customerId: rowData.customerId,
    specificationId: row.id,
    status: row.warehouseEnabled ? '1' : '0'
  })
  if (code !== 200) {
    row.warehouseEnabled = !row.warehouseEnabled
    return ElMessage.warning(message)
  }
  ElMessage.success(row.warehouseEnabled ? '已启用独立仓' : '已禁用独立仓')
  loadProductDetail()
}

const handleHistory = (row: any) => {
  router.push({
    path: '/sales/customer/detail/warehouse-history',
    query: {
      id: rowData.customerId,
      customerStoreId: row.customerStoreId || ''
    }
  })
  drawerVisible.value = false
}

const handleWarning = (row: any) => {
  const params = {
    id: 'warningRuleDialog',
    el: '#app',
    data: {
      customerId: rowData.customerId,
      specificationId: row.id,
      warningNumber: row.warningNumber || ''
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
  .table-images {
    display: flex;
    justify-content: center;
    align-items: center;

    .table-image {
      width: 40px;
      height: 40px;
      margin-right: 5px;
      object-fit: cover;
      cursor: pointer;
      border-radius: 4px;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .info-cell {
    font-size: 13px;
    color: #606266;
  }

  .location-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    color: #606266;
  }

  :deep(.el-table) {
    font-size: 13px;

    .el-table__cell {
      padding: 12px 0;
    }

    &::before,
    &::after {
      display: none;
    }

    .el-table__inner-wrapper::before {
      display: none;
    }

    th.el-table__cell,
    td.el-table__cell {
      border: none;
    }

    thead {
      th {
        background-color: #fafafa;
        border-bottom: 1px solid #e8e8e8;
      }
    }

    tbody {
      tr {
        &:hover > td {
          background-color: #f5f7fa;
        }
      }

      td {
        border-bottom: 1px solid #f0f0f0;
      }
    }
  }
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #e8e8e8;
}

// 搜索框展开动画
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
.warehouse-product-detail-drawer {
  .el-drawer__body {
    padding-top: 0;
  }
  .el-drawer__header {
    margin-bottom: 0;
  }
}
</style>
