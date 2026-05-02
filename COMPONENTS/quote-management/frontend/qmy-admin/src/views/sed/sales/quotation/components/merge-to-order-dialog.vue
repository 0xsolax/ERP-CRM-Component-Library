<template>
  <el-dialog v-model="dialogVisible" title="请选择产品" width="1100px" @close="onDestroy">
    <div class="merge-dialog-content">
      <bz-table
        ref="tableRef"
        :searchColumns="searchColumns"
        :columns="columns"
        :requestApi="getQuotationList"
        :dataCallback="dataCallback"
        :toolButton="false"
        row-key="id"
        max-height="480"
        :searchCol="searchCol"
        :customGridConfig="customGridConfig"
        @expand-change="handleExpandChange"
        @row-click="handleRowClick"
      >
        <template #expand="{ row }">
          <div class="expand-content">
            <div v-if="row.loading" class="loading-wrapper">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
            <el-table
              v-else
              :ref="(el: any) => setExpandTableRef(el, row.id)"
              :data="row.skuList || []"
              @selection-change="(selection: any[]) => handleSkuSelectionChange(selection, row)"
              @row-click="(clickedRow: any) => handleExpandRowClick(clickedRow, row)"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column label="型号名称" prop="modelName" align="center" />
              <el-table-column label="搭配名称" prop="combinationName" align="center" />
              <el-table-column label="SKU名称" prop="skuName" align="center" />
              <el-table-column label="图片" align="center">
                <template #default="{ row: skuRow }">
                  <el-image
                    v-if="skuRow.image"
                    :src="skuRow.image"
                    v-image-preview="skuRow.image"
                    style="width: 50px; height: 50px"
                    fit="cover"
                  />
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="报价" prop="quotationPrice" align="center" />
              <el-table-column label="数量" prop="quantity" align="center" />
            </el-table>
          </div>
        </template>
      </bz-table>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">合并转订单</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import { getMergeList, getMergeSkuList } from '@/api/sed/sales/quotation'
import ConvertOrderDialog from './convert-order-dialog.vue'

const searchCol = { xs: 2, sm: 3, md: 4, lg: 4, xl: 4 }
const customGridConfig = {
  xs: '1fr',
  sm: '1fr 1fr 1fr',
  md: '1fr 1fr 1fr 1fr',
  lg: '1fr 1fr 1fr 1fr',
  xl: '1fr 1fr 1fr 1fr'
}

const attrs = useAttrs()
const { onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const tableRef = ref()

const selectedSkuMap = ref<Map<string, any[]>>(new Map())
const expandTableRefsMap = ref<Map<string, any>>(new Map())

const setExpandTableRef = (el: any, rowId: string) => {
  if (el) {
    expandTableRefsMap.value.set(rowId, el)
  }
}

const handleExpandRowClick = (clickedRow: any, parentRow: any) => {
  const expandTableRef = expandTableRefsMap.value.get(parentRow.id)
  if (expandTableRef) {
    expandTableRef.toggleRowSelection(clickedRow)
  }
}

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
    label: 'SKU名称',
    prop: 'skuName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '搭配名称',
    prop: 'matchName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  }
])

const columns: ColumnProps[] = [
  { type: 'expand', width: 30 },
  { label: '报价单编号', prop: 'quotationCode', align: 'center' },
  { label: '客户名称', prop: 'customerName', align: 'center' },
  { label: '业务员', prop: 'salesmanName', align: 'center' },
  { label: '创建时间', prop: 'createTime', align: 'center' }
]

const dataCallback = (data: any) => {
  const elTable = tableRef.value?.tableRef
  if (elTable) {
    const tableData = elTable.data || []
    tableData.forEach((row: any) => {
      elTable.toggleRowExpansion(row, false)
    })
  }

  const list = (data.list || []).map((item: any) => ({
    id: item.id,
    quotationCode: item.quotationCode,
    customerName: item.customerName,
    salesmanName: item.salesmanName,
    createTime: item.createTime,
    skuList: [],
    loading: false
  }))

  return {
    list,
    total: Number(data?.total || 0)
  }
}

const getQuotationList = (params: any) => {
  return getMergeList({
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    quotationCode: params.quotationCode || '',
    skuName: params.skuName || '',
    matchName: params.matchName || ''
  })
}

const handleExpandChange = async (row: any, expandedRows: any[]) => {
  console.log('handleExpandChange', row)
  const isExpanded = expandedRows.some((r: any) => r.id === row.id)
  if (isExpanded) {
    row.loading = true
    try {
      const { code, data } = await getMergeSkuList({ quotationId: row.id })
      if (code === 200) {
        row.skuList = data || []
      }
    } finally {
      row.loading = false
    }
  }
}

const handleSkuSelectionChange = (selection: any[], row: any) => {
  if (selection.length > 0) {
    selectedSkuMap.value.set(row.id, selection)
  } else {
    selectedSkuMap.value.delete(row.id)
  }
}

const handleRowClick = (row: any, column: any) => {
  if (column?.property === 'operation') return
  tableRef.value?.tableRef?.toggleRowExpansion(row)
}

const handleCancel = () => {
  dialogVisible.value = false
}

const handleConfirm = () => {
  const selectedSkus: any[] = []
  selectedSkuMap.value.forEach(skus => {
    selectedSkus.push(...skus)
  })

  if (!selectedSkus.length) {
    return ElMessage.warning('请至少选择一个SKU')
  }

  const quotationSkuIds = selectedSkus.map(sku => sku.quotationSkuId)
  console.log('quotationSkuIds', quotationSkuIds)

  const params = {
    id: 'convertOrderDialogFromMerge',
    el: '#app',
    data: {
      fromMerge: true,
      quotationSkuIds: quotationSkuIds,
      callback: () => {
        dialogVisible.value = false
        if (callback) callback()
      }
    },
    render: ConvertOrderDialog
  }
  dynamic.show(params)
}
</script>

<style scoped lang="scss">
.expand-content {
  padding: 0;
}

.loading-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #909399;

  .el-icon {
    margin-right: 8px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
