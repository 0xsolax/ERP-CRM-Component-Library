<template>
  <div class="inventory-container">
    <div class="left-category">
      <div class="category-header">
        <el-input
          v-model="categorySearch"
          placeholder="请输入"
          prefix-icon="Search"
          clearable
          @keyup.enter="handleCategorySearch"
          @clear="loadCategoryList"
        />
      </div>
      <div class="category-list">
        <div
          v-for="item in categoryList"
          :key="item.id"
          :class="['category-item', { active: selectedCategory === item.id }]"
          @click="selectCategory(item.id)"
        >
          <span>{{ item.name }}</span>
        </div>
      </div>
    </div>

    <div class="right-content">
      <div class="table-wrapper">
        <bz-table
          ref="bzTableRef"
          :fixedPagination="true"
          :searchColumns="searchColumns"
          :columns="columns"
          :requestApi="getInventoryList"
          :dataCallback="dataCallback"
          :initParam="initParam"
          :initFetch="false"
          :toolButton="false"
        >
          <template #tableHeader>
            <div class="table-header-content">
              <div class="header-right">
                <el-button type="warning" v-permission="'sto:yt:store:setWarning'" @click="handleWarningRule">
                  预警规则
                </el-button>
              </div>
            </div>
          </template>

          <template #image="{ row }">
            <div class="image-cell">
              <el-image
                v-if="row.productImage && row.productImage.length"
                :src="row.productImage[0].url"
                fit="cover"
                style="width: 60px; height: 60px; cursor: pointer"
                @click="handleImagePreview(row.productImage[0].url)"
              />
              <div v-else>-</div>
            </div>
          </template>
          <template #categoryName="{ row }">
            {{ row.categoryName || '-' }}
          </template>
          <template #status="{ row }">
            <span>{{ getStatusLabel(row.status) || '-' }}</span>
          </template>
          <template #operation="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)" v-permission="'sto:yt:store:list'">
              详情
            </el-button>
          </template>
        </bz-table>
      </div>
    </div>

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </div>
</template>

<script lang="ts" setup name="warehouse-inventory">
import { ref, reactive, computed, watch, onActivated, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { ColumnProps } from '@/interface/table'
import { getCategoryList, getProductList } from '@/api/admin/product'
import { statusList, getStatusLabel } from '@/constant/yitang/product'
import { dynamic } from '@bzlab/bz-core'
import WarningRuleDialog from './components/warning-rule-dialog.vue'
import InventoryDetailDialog from './components/inventory-detail-dialog.vue'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import { onBeforeRouteLeave } from 'vue-router'

const categorySearch = ref('')
const selectedCategory = ref('')
const categoryList = ref<any[]>([])
const bzTableRef = ref()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])
const hasInitialized = ref(true)
const tagsStore = useTagsStore()

const initParam = reactive({
  categoryId: ''
})

const handleCategorySearch = () => {
  loadCategoryList()
}

onBeforeRouteLeave(to => {
  if (!to.path.startsWith('/warehouse/inventory')) {
    tagsStore.delCachedView('warehouse-inventory')
  }
})

const loadCategoryList = async () => {
  const params = { name: categorySearch.value }
  const { code, data, message } = await getCategoryList(params)
  if (code !== 200) return ElMessage.warning(message)
  categoryList.value = data ?? []
  const isFirstLoad = !selectedCategory.value
  if (categoryList.value.length > 0 && isFirstLoad) {
    selectedCategory.value = categoryList.value[0].id
    initParam.categoryId = categoryList.value[0].id
    await nextTick()
    bzTableRef.value?.getTableList()
  }
}

const selectCategory = (id: string) => {
  selectedCategory.value = id
  initParam.categoryId = id
  bzTableRef.value?.handleReset()
}

watch(
  () => selectedCategory.value,
  newVal => {
    initParam.categoryId = newVal
  }
)

onActivated(() => {
  loadCategoryList()
  if (!hasInitialized.value) {
    bzTableRef.value?.getTableList()
  }
  hasInitialized.value = false
})

const searchColumns = computed(() => [
  {
    label: '产品ID',
    prop: 'code',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '状态',
    prop: 'status',
    enum: statusList,
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  }
])

const dataCallback = (data: any) => {
  let records = data?.records ?? data?.list ?? []
  // 根据搜索结果的分类自动选中左侧分类
  const searchParams = bzTableRef.value?.searchParams || {}
  const ignore = ['categoryId', 'pageNum', 'pageSize']
  const hasSearch = Object.entries(searchParams).some(([k, v]) => {
    return !ignore.includes(k) && v !== undefined && v !== null && v !== ''
  })
  if (hasSearch && records.length > 0) {
    const categoryIds = [...new Set(records.map((item: any) => item.categoryId).filter(Boolean))]
    selectedCategory.value = categoryIds.length === 1 ? String(categoryIds[0]) : ''
  }
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const columns: ColumnProps[] = [
  { type: 'selection', width: 50 },
  { label: '产品ID', prop: 'code', align: 'center' },
  { label: '图片', prop: 'image', align: 'center', width: 120, showOverflowTooltip: false },
  { label: '产品分类', prop: 'categoryName', align: 'center' },
  { label: '状态', prop: 'status', align: 'center' },
  { label: '操作', prop: 'operation', align: 'center', width: 180 }
]

const getInventoryList = (params: any) => {
  const hasSearch = ['code'].some(key => params[key] !== undefined && params[key] !== null && params[key] !== '')
  if (hasSearch) {
    const rest = Object.fromEntries(Object.entries(params).filter(([k]) => k !== 'categoryId'))
    return getProductList(rest)
  }
  return getProductList({ ...params, categoryId: params.categoryId || selectedCategory.value })
}

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const handleWarningRule = () => {
  const params = {
    id: 'warningRuleDialog',
    el: '#app',
    data: {
      dialogType: 'full',
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: WarningRuleDialog
  }
  dynamic.show(params)
}

const handleDetail = (row: any) => {
  const params = {
    id: 'inventoryDetailDialog',
    el: '#app',
    data: {
      rowData: row
    },
    render: InventoryDetailDialog
  }
  dynamic.show(params)
}
</script>

<style lang="scss" scoped>
.inventory-container {
  display: flex;
  height: 100%;
  background-color: #fff;
  border-radius: 4px;

  .left-category {
    width: 180px;
    border-right: 1px solid #e8e8e8;
    padding: 15px 10px;
    background: #fafafa;
    position: relative;
    z-index: 1;

    .category-header {
      margin-bottom: 15px;

      :deep(.el-input__suffix) {
        display: flex;
        align-items: center;
      }
    }

    .category-list {
      .category-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 12px;
        margin-bottom: 5px;
        cursor: pointer;
        border-radius: 4px;
        transition: all 0.3s;
        font-size: 14px;

        &:hover {
          background: #f5f7fa;
        }

        &.active {
          background: #ecf5ff;
          color: #409eff;
          font-weight: 500;
        }

        .settings-icon {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .right-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .table-wrapper {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: auto;
      .table-header-content {
        display: flex;
        justify-content: flex-end;
        width: 100%;
      }
      .image-cell {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
      }
    }
  }
}
</style>
