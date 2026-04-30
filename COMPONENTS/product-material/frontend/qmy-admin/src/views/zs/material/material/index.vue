<template>
  <div class="material-page">
    <div class="category-sidebar">
      <div class="category-search">
        <el-input
          v-model="categorySearch"
          placeholder="搜索分类"
          clearable
          @keyup.enter="loadCategories(categorySearch)"
          @clear="loadCategories()"
        />
        <el-button type="primary" link @click="handleAddCategory">
          <el-icon><Plus /></el-icon>
          添加
        </el-button>
      </div>
      <div class="category-list">
        <div class="category-item" :class="{ 'is-active': activeCategory === '' }" @click="handleCategorySelect('')">
          <span class="item-text">全部材料</span>
        </div>
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          :class="{ 'is-active': activeCategory === String(cat.id) }"
          @click="handleCategorySelect(String(cat.id))"
        >
          <span class="item-text">{{ cat.name }}</span>
          <el-button class="edit-icon" circle text :icon="Setting" @click.stop="handleEditCategory(cat)" />
        </div>
      </div>
    </div>

    <div class="content-area">
      <bz-table
        ref="tableRef"
        :fixedPagination="true"
        :searchColumns="searchColumns"
        :columns="columns"
        :requestApi="requestApi"
        :searchDataCallback="searchDataCallback"
        :dataCallback="dataCallback"
        :toolButton="false"
      >
        <template #tableHeader>
          <el-button type="primary" @click="handleAdd">新增材料</el-button>
        </template>

        <template #images="{ row }">
          <el-image
            v-if="firstImage(row.images)"
            :src="firstImage(row.images)"
            v-image-preview="firstImage(row.images)"
            fit="cover"
            style="width: 48px; height: 48px; border-radius: 4px"
          />
          <span v-else>-</span>
        </template>

        <template #price="{ row }">
          {{ row.price ? '¥' + row.price : '-' }}
        </template>

        <template #operation="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </bz-table>
    </div>
  </div>
</template>

<script lang="ts" setup name="material-other">
import { ref, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Setting } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { getMaterialPage, deleteMaterial, getMaterialCategoryList } from '@/api/zs/material/material'
import MaterialDialog from './components/material-dialog.vue'
import CategoryDialog from './components/category-dialog.vue'

const tableRef = ref()
const hasInitialized = ref(true)

const categories = ref<any[]>([])
const activeCategory = ref('')
const categorySearch = ref('')

const loadCategories = async (likeName = '') => {
  const params: any = {}
  if (likeName) params.likeName = likeName
  const { code, data } = await getMaterialCategoryList(params)
  if (code === 200) categories.value = data || []
}

const handleCategorySelect = (id: string) => {
  activeCategory.value = id
  tableRef.value?.getTableList()
}

const requestApi = (params: any) => {
  if (activeCategory.value) {
    params.categoryId = activeCategory.value
  } else {
    delete params.categoryId
  }
  return getMaterialPage(params)
}

const searchColumns = ref([
  {
    prop: 'likeName',
    label: '材料名称',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    prop: 'likeSize',
    label: '尺寸',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  }
])

const columns = ref([
  { prop: 'categoryName', label: '所属分类', minWidth: 80 },
  { prop: 'name', label: '材料名称', minWidth: 100 },
  { prop: 'images', label: '图片', minWidth: 100 },
  { prop: 'size', label: '尺寸', minWidth: 100 },
  { prop: 'price', label: '价格', minWidth: 80 },
  { prop: 'operation', label: '操作', width: 150, fixed: 'right' }
])

const searchDataCallback = (params: any) => {
  return params
}

const dataCallback = (data: any) => {
  return {
    list: data?.list || [],
    total: Number(data?.total || 0)
  }
}

const firstImage = (images: any) => {
  if (!images) return null
  if (Array.isArray(images)) return images[0]?.url || null
  return null
}

const handleAdd = () => {
  const selectedCat = categories.value.find((c: any) => String(c.id) === activeCategory.value)
  dynamic.show({
    id: 'materialDialog',
    el: '#app',
    data: {
      isEdit: false,
      categories: categories.value,
      defaultCategoryId: selectedCat?.id || null,
      callback: () => tableRef.value?.getTableList()
    },
    render: MaterialDialog
  })
}

const handleEdit = (row: any) => {
  dynamic.show({
    id: 'materialDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      categories: categories.value,
      callback: () => tableRef.value?.getTableList()
    },
    render: MaterialDialog
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该材料吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteMaterial({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

const handleAddCategory = () => {
  dynamic.show({
    id: 'categoryDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => loadCategories()
    },
    render: CategoryDialog
  })
}

const handleEditCategory = (cat: any) => {
  dynamic.show({
    id: 'categoryDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: cat,
      callback: () => {
        loadCategories()
        tableRef.value?.getTableList()
      }
    },
    render: CategoryDialog
  })
}

onMounted(loadCategories)

onActivated(() => {
  if (!hasInitialized.value) {
    loadCategories()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.material-page {
  display: flex;
  gap: 10px;
  height: 100%;

  .category-sidebar {
    width: 220px;
    flex-shrink: 0;
    background: #fff;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  .category-search {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 14px 0;
  }
  .category-list {
    flex: 1;
    overflow-y: auto;
    padding: 0 14px 16px;
  }
  .category-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 40px;
    padding-left: 10px;
    border-radius: 8px;
    cursor: pointer;
    margin-top: 4px;
    &:first-child {
      margin-top: 10px;
    }
    &:hover {
      background-color: rgba(39, 126, 255, 0.03);
    }
    &.is-active {
      background-color: rgba(39, 126, 255, 0.05);
      .item-text {
        color: rgba(39, 126, 255, 1);
        font-weight: 600;
      }
      .edit-icon {
        color: rgba(39, 126, 255, 1) !important;
      }
    }
  }
  .item-text {
    flex: 1;
    font-size: 14px;
    color: rgba(21, 35, 54, 1);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .edit-icon {
    width: 24px !important;
    height: 24px !important;
    min-height: unset !important;
    padding: 0 !important;
    color: rgba(169, 173, 180, 1) !important;
    flex-shrink: 0;
  }
  .content-area {
    flex: 1;
    min-width: 0;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }
}
</style>
