<template>
  <div class="packaging-list-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getPackagingPage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAdd">新增包材</el-button>
        <el-button @click="handleBoxPrice">纸箱单价</el-button>
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
</template>

<script lang="ts" setup name="material-packaging">
import { ref, computed, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getPackagingPage, deletePackaging } from '@/api/zs/material/packaging'
import { listByNodeKey } from '@/api/zs/base-info/base-data'
import PackagingDialog from './components/packaging-dialog.vue'
import BoxPriceDialog from './components/box-price-dialog.vue'

const tableRef = ref()
const hasInitialized = ref(true)

const typeOptions = ref<any[]>([])

const loadFilterOptions = async () => {
  const { code, data } = await listByNodeKey({ nodeKey: 'PACKAGING_TYPE' })
  if (code === 200) {
    typeOptions.value = (data || [])
      .filter((d: any) => d.value2 === '0')
      .map((d: any) => ({ label: d.value1, value: d.id }))
  }
}

const searchColumns = computed(() => [
  {
    prop: 'keywords',
    label: '关键词',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    prop: 'typeId',
    label: '包材类型',
    enum: typeOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
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
  { prop: 'typeName', label: '包材类型' },
  { prop: 'name', label: '包材名称' },
  { prop: 'size', label: '尺寸' },
  { prop: 'price', label: '单价' },
  { prop: 'operation', label: '操作', width: 150, fixed: 'right' }
])

const searchDataCallback = (params: any) => {
  return { ...params, defaultTypeFlag: 0 }
}

const dataCallback = (data: any) => {
  return {
    list: data?.list || [],
    total: Number(data?.total || 0)
  }
}

const handleAdd = () => {
  dynamic.show({
    id: 'packagingDialog',
    el: '#app',
    data: { isEdit: false, callback: () => tableRef.value?.getTableList() },
    render: PackagingDialog
  })
}

const handleEdit = (row: any) => {
  dynamic.show({
    id: 'packagingDialog',
    el: '#app',
    data: { isEdit: true, rowData: row, callback: () => tableRef.value?.getTableList() },
    render: PackagingDialog
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该包材吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deletePackaging({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

const handleBoxPrice = () => {
  dynamic.show({
    id: 'boxPriceDialog',
    el: '#app',
    data: {},
    render: BoxPriceDialog
  })
}

onMounted(loadFilterOptions)

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.packaging-list-container {
  height: 100%;
}
</style>
