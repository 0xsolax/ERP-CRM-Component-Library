<template>
  <el-dialog v-model="dialogVisible" title="更换供应商" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="产品ID：">
        <span>{{ productCode }}</span>
      </el-form-item>
      <el-form-item label="规格名称：">
        <span>{{ specName }}</span>
      </el-form-item>
      <el-form-item label="原供应商：">
        <span>{{ originalSupplier }}</span>
      </el-form-item>
      <el-form-item label="新供应商：" prop="supplierId">
        <el-select v-model="formData.supplierId" placeholder="请选择" clearable style="width: 100%">
          <el-option
            v-for="item in supplierList"
            :key="item.supplierId"
            :label="item.supplierName"
            :value="item.supplierId"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { replaceSupplier, listReplaceableSuppliers } from '@/api/admin/purchase/pending'

const attrs = useAttrs()
const { onDestroy, callback, productCode, specName, originalSupplier, rowData } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()

const formData = reactive({
  supplierId: ''
})

const rules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

const supplierList = ref<any[]>([])

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const { code, message } = await replaceSupplier({
    applyPurchaseIdList: [rowData?.id],
    supplierId: Number(formData.supplierId)
  })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  callback?.()
  onDestroy?.()
}

const loadSupplierList = async () => {
  const productSpecificationIds = rowData?.productSpecificationId ? [rowData?.productSpecificationId] : []
  const { code, data, message } = await listReplaceableSuppliers(productSpecificationIds)
  if (code !== 200) return ElMessage.warning(message)
  supplierList.value = data ?? []
}

onMounted(() => {
  loadSupplierList()
})
</script>

<style scoped lang="scss"></style>
