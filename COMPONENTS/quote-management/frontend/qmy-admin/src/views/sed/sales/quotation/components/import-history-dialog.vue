<template>
  <el-dialog
    v-model="dialogVisible"
    modal-class="import-history-dialog"
    title="请选择历史报价单"
    width="900px"
    @close="onDestroy"
  >
    <bz-table
      ref="tableRef"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getHistoryList"
      :dataCallback="dataCallback"
      :toolButton="false"
      :searchCol="searchCol"
      :customGridConfig="customGridConfig"
      @selection-change="handleSelectionChange"
    >
      <template #quotationNo="{ row }">
        <span>{{ row.quotationNo }}</span>
      </template>
    </bz-table>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { ColumnProps } from '@/interface/table'
import { getHistoryQuotation, getHistoryQuotationDetail } from '@/api/sed/sales/quotation'

const searchCol = { xs: 2, sm: 3, md: 4, lg: 4, xl: 4 }
const customGridConfig = {
  xs: '1fr',
  sm: '1fr 1fr 1fr',
  md: '1fr 1fr 1fr',
  lg: '1fr 1fr 1fr 1fr',
  xl: '200px 200px 200px 1fr'
}
const attrs = useAttrs()
const { onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const tableRef = ref()
const loading = ref(false)

const selectedList = ref<any[]>([])

const searchColumns = computed(() => [
  {
    label: '报价单编号',
    prop: 'quotationCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '客户名称',
    prop: 'customerName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '创建时间',
    prop: 'createTime',
    search: {
      el: 'el-date-picker',
      props: {
        type: 'date',
        placeholder: '请选择时间',
        clearable: true,
        'value-format': 'YYYY-MM-DD'
      }
    }
  }
])

const columns: ColumnProps[] = [
  { type: 'selection', width: 55 },
  { label: '报价单编号', prop: 'quotationCode', align: 'center' },
  { label: '客户名称', prop: 'customerName', align: 'center' },
  { label: '创建时间', prop: 'createTime', align: 'center' }
]

const dataCallback = (data: any) => {
  return {
    list: data?.list ?? [],
    total: Number(data?.total || 0)
  }
}

const getHistoryList = (params: any) => {
  return getHistoryQuotation({
    quotationCode: params.quotationCode || '',
    customerName: params.customerName || '',
    createTime: params.createTime || '',
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10
  })
}

const handleSelectionChange = (selection: any[]) => {
  selectedList.value = selection
}

const handleConfirm = async () => {
  if (selectedList.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }

  loading.value = true
  try {
    const ids = selectedList.value.map((item: any) => item.quotationId).join(',')
    const { code, data, message } = await getHistoryQuotationDetail({ ids })
    if (code !== 200) return ElMessage.warning(message)
    if (!data || !data.length) {
      ElMessage.warning('未查询到历史报价数据')
      return
    }

    if (callback) {
      callback(data)
    }
    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss">
.import-history-dialog {
  .el-table {
    padding-bottom: 0 !important;
  }
  .pagination {
    margin-top: 0 !important;
    position: static !important;
    padding: 16px 0 !important;
    background: #fff !important;
    border-top: none !important;
    z-index: 100 !important;
    box-shadow: none !important;
  }
}
</style>
