<template>
  <el-dialog
    v-model="dialogVisible"
    title="产品入库信息"
    width="1100px"
    :before-close="handleBeforeClose"
    @close="handleCancel"
  >
    <div class="summary-info" v-if="summaryInfoList.length">
      <div class="info-item" v-for="item in summaryInfoList" :key="item.label">
        <span class="label">{{ item.label }}</span>
        <span class="value">{{ item.value }}</span>
      </div>
    </div>

    <el-table :data="tableData" border style="width: 100%" max-height="600">
      <el-table-column label="产品图片" align="center" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            v-image-preview="row.image"
            fit="cover"
            style="width: 40px; height: 40px"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="产品ID" prop="productCode" align="center" />
      <el-table-column label="规格名称" prop="specificationName" align="center" />
      <el-table-column label="供应商名称" prop="supplierName" align="center">
        <template #default="{ row }">{{ row.supplierName || '-' }}</template>
      </el-table-column>
      <el-table-column label="定制化属性" prop="labelName" align="center">
        <template #default="{ row }">{{ row.labelName || '-' }}</template>
      </el-table-column>
      <el-table-column
        :label="showOrderCode ? '订单已入库/需入库' : '采购单已入库/需入库'"
        prop="progressText"
        align="center"
        width="180"
      />
      <el-table-column label="入库数量" align="center" width="120">
        <template #default="{ row }">
          <el-input
            v-model="row.enterNumber"
            placeholder="请输入"
            style="width: 80px"
            @input="(val: string) => (row.enterNumber = validateInteger(val))"
          />
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleBeforeClose()">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, useAttrs } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchEnterStore, getInboundOrderList } from '@/api/admin/warehouse'
import { validateInteger } from '@/utils/validate'

const attrs = useAttrs()
const { selectedRows, showOrderCode, searchParams, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const tableData = ref<any[]>([])

const formatTableData = (list: any[] = []) => {
  return list.map((item: any) => {
    const enterNumber = item.enterNumber || 0
    const totalNumber = item.totalNumber || 0
    const owedCount = Math.max(0, totalNumber - enterNumber)
    const progressText = totalNumber > 0 ? `${enterNumber}/${totalNumber}` : '0/0'

    return {
      ...item,
      image: item.imageList?.[0]?.url || item.image || '',
      specificationName:
        item.specificationName || item.itemList?.map((i: any) => i.categorySpecificationItemValue).join('-') || '',
      progressText,
      owedCount,
      enterNumber: String(owedCount)
    }
  })
}

const loadTableData = async () => {
  const params = {
    ...(searchParams || {}),
    pageNum: 1,
    pageSize: 9999
  }
  const { code, data, message } = await getInboundOrderList(params)
  if (code !== 200) return ElMessage.warning(message)

  const selectedIds = new Set((selectedRows || []).flatMap((item: any) => item.storeOrderIdList || []))
  const list = (data?.list || []).filter((item: any) => {
    const rowIds = item.storeOrderIdList || []
    return rowIds.some((id: number) => selectedIds.has(id))
  })

  tableData.value = formatTableData(list)
}

const summaryInfoList = computed(() => {
  const infoList = [
    { label: '采购单号', value: searchParams?.purchaseCode },
    { label: '订单编号', value: showOrderCode ? searchParams?.orderCode : '' },
    { label: '产品ID', value: searchParams?.productCode },
    { label: '产品规格', value: searchParams?.specificationName }
  ]

  return infoList.filter(item => item.value !== undefined && item.value !== null && item.value !== '')
})

const hasContent = computed(() => {
  return tableData.value.some((row: any) => Number(row.enterNumber) > 0)
})

const handleBeforeClose = async (done?: () => void) => {
  if (!hasContent.value) {
    if (done) done()
    else handleCancel()
    return
  }
  await ElMessageBox.confirm('有内容未保存，是否确认关闭？', '提示', {
    confirmButtonText: '确认关闭',
    cancelButtonText: '取消',
    type: 'warning'
  })
  if (done) done()
  else handleCancel()
}

const handleCancel = () => {
  dialogVisible.value = false
  if (onDestroy) onDestroy()
}

onMounted(() => {
  loadTableData()
})

const handleSubmit = async () => {
  const invalidRow = tableData.value.find(
    (item: any) => item.enterNumber === '' || item.enterNumber === null || item.enterNumber === undefined
  )
  if (invalidRow) {
    ElMessage.warning('请输入入库数量')
    return
  }

  const params = {
    stoYtStoreOrderAddParams: tableData.value.map((item: any) => ({
      storeOrderIdList: item.storeOrderIdList ?? [],
      enterNumber: Number(item.enterNumber || 0)
    }))
  }

  const { code, message } = await batchEnterStore(params)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  if (callback) callback()
  if (onDestroy) onDestroy()
}
</script>

<style scoped lang="scss">
.summary-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
  padding: 12px 16px;
  margin-bottom: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;

  .info-item {
    display: flex;
    align-items: center;
    gap: 8px;

    .label {
      color: #909399;
      font-size: 14px;
    }

    .value {
      color: #303133;
      font-size: 14px;
    }
  }
}
</style>
