<template>
  <el-dialog v-model="visible" title="编辑" width="600px" @close="handleClose" v-loading="loading">
    <el-form :model="formData" label-width="120px">
      <el-form-item label="产品ID">
        <div class="readonly-text">{{ formData.productCode }}</div>
      </el-form-item>
      <el-form-item label="规格名称">
        <div class="readonly-text">{{ formData.specification }}</div>
      </el-form-item>
      <el-form-item label="图片">
        <el-image
          v-if="formData.image"
          :src="formData.image"
          style="width: 50px; height: 50px"
          fit="cover"
          v-image-preview="formData.image"
        />
        <span v-else>-</span>
      </el-form-item>
      <el-form-item label="客户规格名称">
        <div class="readonly-text">{{ formData.customerSpecification }}</div>
      </el-form-item>
      <el-form-item label="客户货号">
        <el-input v-model="formData.itemNumber" placeholder="请输入" clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose" :disabled="loading">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { createOrUpdateItemNumber } from '@/api/admin/sales/customer-spec'

const attrs = useAttrs() as any
const visible = ref(true)
const loading = ref(false)

console.log('attrs', attrs)

const formData = reactive({
  id: attrs.row?.id || null,
  productId: attrs.row?.productId || '',
  productCode: attrs.row?.productCode || '',
  specification: attrs.row?.specification || '',
  specificationId: attrs.row?.id || '',
  image: attrs.row?.image || '',
  customerSpecification: attrs.row?.customerSpecification || '',
  itemNumber: attrs.row?.itemNumber || ''
})

const handleClose = () => {
  visible.value = false
  if (attrs.onClose) {
    attrs.onClose()
  }
}

const handleConfirm = async () => {
  loading.value = true
  try {
    const { code, message } = await createOrUpdateItemNumber({
      customerId: attrs.customerId,
      specificationId: formData.specificationId,
      productId: attrs.row.productId,
      itemNumber: formData.itemNumber
    })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('保存成功')
    if (attrs.callback) {
      attrs.callback()
    }
    handleClose()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.readonly-text {
  color: #606266;
  font-size: 14px;
}
</style>
