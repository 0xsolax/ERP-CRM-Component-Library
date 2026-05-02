<template>
  <div class="logistics-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getLogisticsList"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" v-permission="'sto:yt:transportCompany:add'" @click="handleAdd">
          新增物流公司
        </el-button>
      </template>
      <template #operation="{ row }">
        <el-button
          type="primary"
          link
          size="small"
          v-permission="'sto:yt:transportCompany:update'"
          @click="handleEdit(row)"
        >
          编辑
        </el-button>
        <el-button
          type="danger"
          link
          size="small"
          v-permission="'sto:yt:transportCompany:delete'"
          @click="handleDelete(row)"
        >
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
import { getLogisticsList, deleteLogistics } from '@/api/admin/warehouse'
import { getLogisticsTypeLabel } from '@/constant/yitang/warehouse'
import LogisticsDialog from './components/logistics-dialog.vue'

const tableRef = ref()

const searchColumns = computed(() => [
  {
    label: '物流公司名称',
    prop: 'name',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  }
])

const columns: ColumnProps[] = [
  { label: '物流公司ID', prop: 'code', align: 'center' },
  { label: '物流公司名称', prop: 'name', align: 'center' },
  { label: '物流公司类型', prop: 'type', align: 'center' },
  { label: '地址', prop: 'address', align: 'center' },
  { label: '创建时间', prop: 'createTime', align: 'center' },
  { label: '操作', prop: 'operation', align: 'center', width: 150, fixed: 'right' }
]

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  records = records.map((item: any) => ({
    ...item,
    type: getLogisticsTypeLabel(item.type)
  }))
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const handleAdd = () => {
  const params = {
    id: 'logisticsDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => {
        tableRef.value?.getTableList()
      }
    },
    render: LogisticsDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'logisticsDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => {
        tableRef.value?.getTableList()
      }
    },
    render: LogisticsDialog
  }
  dynamic.show(params)
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该物流公司吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteLogistics(row.id)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}
</script>

<style lang="scss" scoped>
.logistics-container {
  .link-text {
    color: #409eff;
    cursor: pointer;
  }
}
</style>
