<template>
  <div class="process-list-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getProcessPage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAdd">新增工序</el-button>
      </template>
      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="base-info-process">
import { ref, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getProcessPage, deleteProcess } from '@/api/zs/base-info/process'
import ProcessDialog from './components/process-dialog.vue'

const tableRef = ref()
const hasInitialized = ref(true)

const searchColumns = ref([
  {
    prop: 'likeName',
    label: '工序名称/编号',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  }
])

const columns = ref([
  { prop: 'name', label: '工序名称', align: 'left' },
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
    id: 'processDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => tableRef.value?.getTableList()
    },
    render: ProcessDialog
  })
}

const handleEdit = (row: any) => {
  dynamic.show({
    id: 'processDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: ProcessDialog
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该工序吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteProcess({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.process-list-container {
  height: 100%;
}
</style>
