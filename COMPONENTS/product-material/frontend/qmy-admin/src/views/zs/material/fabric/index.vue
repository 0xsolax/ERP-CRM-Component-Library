<template>
  <div class="fabric-list-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getFabricPage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAdd">新增面料</el-button>
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

<script lang="ts" setup name="material-fabric">
import { ref, computed, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getFabricPage, deleteFabric } from '@/api/zs/material/fabric'
import { listByNodeKey } from '@/api/zs/base-info/base-data'
import FabricDialog from './components/fabric-dialog.vue'

const tableRef = ref()
const hasInitialized = ref(true)

const nameOptions = ref<any[]>([])
const codeOptions = ref<any[]>([])

const loadFilterOptions = async () => {
  const [nRes, cRes] = await Promise.all([
    listByNodeKey({ nodeKey: 'FABRIC_TYPE' }),
    listByNodeKey({ nodeKey: 'FABRIC_MODEL' })
  ])
  if (nRes.code === 200) nameOptions.value = (nRes.data || []).map((d: any) => ({ label: d.value1, value: d.id }))
  if (cRes.code === 200) codeOptions.value = (cRes.data || []).map((d: any) => ({ label: d.value1, value: d.id }))
}

const searchColumns = computed(() => [
  {
    prop: 'keywords',
    label: '关键词',
    search: {
      el: 'el-input',
      props: { placeholder: '种类/型号/门幅', clearable: true }
    }
  },
  {
    prop: 'typeId',
    label: '种类',
    enum: nameOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
    }
  },
  {
    prop: 'modelId',
    label: '型号',
    enum: codeOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
    }
  }
])

const columns = ref([
  { prop: 'typeName', label: '种类', minWidth: 120 },
  { prop: 'modelName', label: '型号', minWidth: 120 },
  { prop: 'widthName', label: '门幅(cm)', minWidth: 100 },
  { prop: 'price', label: '单价', minWidth: 100 },
  { prop: 'unit', label: '单位', minWidth: 90 },
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

const handleAdd = () => {
  dynamic.show({
    id: 'fabricDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => {
        tableRef.value?.getTableList()
        loadFilterOptions()
      }
    },
    render: FabricDialog
  })
}

const handleEdit = (row: any) => {
  dynamic.show({
    id: 'fabricDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => {
        tableRef.value?.getTableList()
        loadFilterOptions()
      }
    },
    render: FabricDialog
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该面料吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteFabric({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
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
.fabric-list-container {
  height: 100%;
}
</style>
