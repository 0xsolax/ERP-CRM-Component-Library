<template>
  <div class="packing-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :columns="columns"
      :searchColumns="searchColumns"
      :requestApi="getPackingBoxList"
      :dataCallback="dataCallback"
      :paginationConfig="paginationConfig"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" v-permission="'sto:yt:box:saveOrUpdate'" @click="handleAdd">新增打包箱</el-button>
      </template>

      <template #operation="{ row }">
        <el-button type="primary" link size="small" v-permission="'sto:yt:box:saveOrUpdate'" @click="handleEdit(row)">
          编辑
        </el-button>
        <el-button type="danger" link size="small" v-permission="'sto:yt:box:delete'" @click="handleDelete(row)">
          删除
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ColumnProps } from '@/interface/table'
import { getPackingBoxList, deletePackingBox } from '@/api/admin/warehouse'
import PackingBoxDialog from './components/packing-box-dialog.vue'

const paginationConfig = {
  pageSize: 15
}
const tableRef = ref()

const searchColumns = computed(() => [
  {
    label: '打包箱ID',
    prop: 'code',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
])

const columns: ColumnProps[] = [
  { label: '打包箱ID', prop: 'code', align: 'center' },
  { label: '打包箱尺寸（cm）', prop: 'size', align: 'center' },
  { label: '打包箱重量（g）', prop: 'weight', align: 'center' },
  { label: '创建时间', prop: 'createTime', align: 'center' },
  { label: '操作', prop: 'operation', align: 'center', width: 150, fixed: 'right' }
]

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  records = records.map((item: any) => ({
    ...item,
    size: `${item.length || 0}*${item.width || 0}*${item.height || 0}`
  }))
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const handleAdd = () => {
  const params = {
    id: 'packingBoxDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => {
        tableRef.value?.getTableList()
      }
    },
    render: PackingBoxDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'packingBoxDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => {
        tableRef.value?.getTableList()
      }
    },
    render: PackingBoxDialog
  }
  dynamic.show(params)
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该打包箱吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deletePackingBox(row.id)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}
</script>

<style lang="scss" scoped>
.packing-container {
}
</style>
