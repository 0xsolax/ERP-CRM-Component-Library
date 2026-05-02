<template>
  <el-dialog v-model="dialogVisible" title="追加" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="供应商：">
        <span>{{ supplier.supplierName }}</span>
      </el-form-item>
      <el-form-item label="追加采购单：" prop="purchaseOrderId">
        <el-select v-model="formData.purchaseOrderId" placeholder="请选择暂存采购单" clearable style="width: 100%">
          <el-option v-for="item in purchaseOrderList" :key="item.id" :label="item.code" :value="item.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listTemporary, addPurchase } from '@/api/admin/purchase/pending'

const attrs = useAttrs()
const { onDestroy, callback, supplierId, supplier, selectedProducts } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()

const formData = reactive({
  purchaseOrderId: ''
})

const rules = {
  purchaseOrderId: [{ required: true, message: '请选择暂存采购单', trigger: 'change' }]
}

const purchaseOrderList = ref<any[]>([])

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const applyPurchaseIdList = (selectedProducts || []).map((item: any) => item.id)
  const reqBody = {
    applyPurchaseIdList,
    purchaseId: formData.purchaseOrderId,
    isInboundDelivery: supplier.isInboundDelivery,
    supplierId: supplierId
  }
  const { code, message } = await addPurchase(reqBody)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('操作成功')
  callback?.()
  onDestroy?.()
}

const loadPurchaseOrderList = async () => {
  const { code, data, message } = await listTemporary({
    supplierId: supplierId,
    isInboundDelivery: supplier.isInboundDelivery
  })
  if (code !== 200) return ElMessage.warning(message)
  purchaseOrderList.value = data ?? []
}

onMounted(() => {
  loadPurchaseOrderList()
})
</script>

<style scoped lang="scss">
:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
