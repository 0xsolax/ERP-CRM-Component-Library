<template>
  <el-dialog v-model="dialogVisible" title="发货" width="1000px" @close="onDestroy">
    <div style="padding: 20px 0">
      <div style="margin-bottom: 15px; font-size: 14px; color: #606266">请选择可发货的订单</div>
      <div style="max-height: 400px; overflow-y: auto">
        <el-table ref="tableRef" v-loading="loading" :data="orderList" border style="width: 100%">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="订单号" prop="orderCode" align="center" />
          <el-table-column label="发货形式" prop="shippingMethodText" align="center" />
          <el-table-column label="状态" align="center" width="200">
            <template #default="{ row }">
              <div class="status-cell">
                <div class="status-item">
                  <el-icon :size="16" :color="row.productCompleteIcon === 'success' ? '#67C23A' : '#F56C6C'">
                    <CircleCheck v-if="row.productCompleteIcon === 'success'" />
                    <CircleClose v-else />
                  </el-icon>
                  <span>{{ row.productComplete }}</span>
                </div>
                <div class="status-item">
                  <el-icon :size="16" :color="row.packageCompleteIcon === 'success' ? '#67C23A' : '#F56C6C'">
                    <CircleCheck v-if="row.packageCompleteIcon === 'success'" />
                    <CircleClose v-else />
                  </el-icon>
                  <span>{{ row.packageComplete }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="订单备注" prop="orderRemark" align="center">
            <template #default="{ row }">{{ row.orderRemark || '-' }}</template>
          </el-table-column>
          <el-table-column label="业务员" prop="saleEmployeeName" align="center">
            <template #default="{ row }">{{ row.saleEmployeeName || '-' }}</template>
          </el-table-column>
          <el-table-column label="交货时间" prop="orderDeliveryTime" align="center">
            <template #default="{ row }">{{ row.orderDeliveryTime || '-' }}</template>
          </el-table-column>
          <!-- <el-table-column label="操作" align="center" width="80">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            </template>
          </el-table-column> -->
        </el-table>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { getDeliveryOrderDetail } from '@/api/admin/warehouse'
import { getDeliveryTypeLabel } from '@/constant/yitang/sales'
import PackingDetailDialog from './packing-detail-dialog.vue'

const attrs = useAttrs()
const { deliveryId, rowData, onDestroy, onConfirm } = attrs as any

const dialogVisible = ref(true)
const tableRef = ref()
const orderList = ref<any[]>([])
const loading = ref(false)

const fetchOrderList = async () => {
  loading.value = true
  try {
    const { code, data, message } = await getDeliveryOrderDetail({ id: deliveryId })
    if (code !== 200) return ElMessage.warning(message)

    if (data && Array.isArray(data)) {
      orderList.value = data.map((item: any) => ({
        ...item,
        orderCode: item.orderCode || '-',
        shippingMethodText: getDeliveryTypeLabel(item.shippingMethod) || '-',
        orderRemark: item.orderRemark || '-',
        saleEmployeeName: item.saleEmployeeName || '-',
        orderDeliveryTime: item.orderDeliveryTime || '-',
        productComplete: item.productComplete ? '产品齐全' : '产品不全',
        packageComplete: item.packageComplete ? '打包齐全' : '打包不全',
        productCompleteIcon: item.productComplete ? 'success' : 'error',
        packageCompleteIcon: item.packageComplete ? 'success' : 'error'
      }))
    }
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  dialogVisible.value = false
  if (onDestroy) onDestroy()
}

const handleConfirm = () => {
  const selectedRows = tableRef.value?.getSelectionRows() || []
  if (selectedRows.length === 0) {
    ElMessage.warning('请至少选择一个订单')
    return
  }
  if (onConfirm) onConfirm(selectedRows)
  dialogVisible.value = false
}

const handleDetail = (row: any) => {
  row
  const params = {
    id: 'packingDetailDialog',
    el: '#app',
    data: {
      rowData,
      status: 0,
      callback: () => {}
    },
    render: PackingDetailDialog
  }
  dynamic.show(params)
}
handleDetail
onMounted(() => {
  fetchOrderList()
})
</script>

<style lang="scss" scoped>
.status-cell {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;

  .status-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
  }
}
</style>
