<template>
  <el-dialog v-model="visible" title="编辑" width="600px" @close="handleClose" v-loading="loading">
    <el-form :model="formData" label-width="120px">
      <el-form-item label="产品ID">
        <div class="readonly-text">{{ formData.productCode }}</div>
      </el-form-item>
      <el-form-item label="规格名称">
        <div class="readonly-text">{{ formData.specificationName }}</div>
      </el-form-item>
      <el-form-item label="图片">
        <el-image
          v-if="formData.image"
          :src="formData.image"
          style="width: 50px; height: 50px"
          fit="cover"
          :preview-src-list="[formData.image]"
        />
        <span v-else>-</span>
      </el-form-item>
      <el-form-item label="供应商规格">
        <el-input v-model="formData.supplierSpecification" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="供应商编号">
        <el-input v-model="formData.supplierSpecificationCode" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="供应商单价">
        <el-input v-model="formData.supplierPrice" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="起订量">
        <el-input v-model="formData.minNumber" placeholder="请输入" clearable />
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
import { updateSupplierSpecification } from '@/api/admin/purchase/supplier'

const attrs = useAttrs() as any
const visible = ref(true)
const loading = ref(false)

const formData = reactive({
  id: attrs.row?.id || null,
  productId: attrs.row?.productId || '',
  productCode: attrs.row?.productCode || '',
  specificationName: attrs.row?.specificationName || '',
  specificationId: attrs.row?.specificationId || '',
  image: attrs.row?.image || '',
  supplierSpecification: attrs.row?.supplierSpecification || '',
  supplierSpecificationCode: attrs.row?.supplierSpecificationCode || '',
  supplierPrice: attrs.row?.supplierPrice || '',
  minNumber: attrs.row?.minNumber || ''
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
    const { code, message } = await updateSupplierSpecification({
      id: formData.id,
      supplierId: attrs.supplierId,
      supplierSpecification: formData.supplierSpecification,
      minNumber: formData.minNumber,
      supplierPrice: formData.supplierPrice,
      supplierSpecificationCode: formData.supplierSpecificationCode
    })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
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
