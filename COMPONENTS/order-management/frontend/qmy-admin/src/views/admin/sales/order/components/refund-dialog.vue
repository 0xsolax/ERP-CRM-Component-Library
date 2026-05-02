<template>
  <el-dialog v-model="dialogVisible" title="订单退货" width="450px" @close="onDestroy">
    <el-form :model="form" label-width="80px">
      <el-form-item label="退货数量" required>
        <el-input-number
          v-model="form.number"
          :min="1"
          :step="1"
          :precision="0"
          placeholder="请输入"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="退货原因" required>
        <el-input v-model="form.reason" type="textarea" :rows="5" placeholder="请输入" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { orderReturnItem } from '@/api/admin/sales/order'

const attrs = useAttrs()
const { rowData, onDestroy, onSuccess } = attrs as any
const dialogVisible = ref(true)

const form = ref({
  number: 1,
  reason: ''
})

const handleCancel = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  if (!form.value.number || form.value.number < 1) {
    ElMessage.warning('请输入正确的退货数量')
    return
  }
  if (!form.value.reason || !form.value.reason.trim()) {
    ElMessage.warning('请输入退货原因')
    return
  }

  const { code, message } = await orderReturnItem({
    orderId: rowData.orderId,
    productId: rowData.productId,
    price: rowData.price,
    labelId: rowData.labelId,
    remark: rowData.remark || '',
    specificationId: rowData.specificationId,
    supplierPrice: rowData.supplierPrice,
    number: form.value.number,
    reason: form.value.reason
  })

  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('退货申请已提交')
  dialogVisible.value = false

  if (onSuccess && typeof onSuccess === 'function') {
    onSuccess()
  }
}
</script>

<style scoped lang="scss"></style>
