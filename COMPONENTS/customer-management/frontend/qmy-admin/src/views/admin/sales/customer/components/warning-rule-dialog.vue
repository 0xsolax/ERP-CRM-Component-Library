<template>
  <el-dialog v-model="dialogVisible" title="预警规则" width="600px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="200px">
      <el-form-item label="实际库存+实际在途小于等于" prop="warningNumber">
        <el-input v-model="formData.warningNumber" placeholder="请输入" clearable @input="handleNumberInput" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import {
  setCustomerStoreWarning,
  setProductStoreWarning,
  getStoreWarningCount,
  getProductStoreWarningCount
} from '@/api/admin/sales/customer'

const attrs = useAttrs() as any

defineProps(['onDestroy'])

const dialogVisible = ref(true)
const loading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  warningNumber: ''
})

const rules = {
  warningNumber: [{ required: true, message: '请输入预警阈值', trigger: 'blur' }]
}

// 只允许输入正整数
const handleNumberInput = (value: string) => {
  let result = value.replace(/[^\d]/g, '')
  result = result.replace(/^0+/, '') || '0'
  if (result === '0' && value.length > 1) {
    result = '0'
  }
  formData.warningNumber = result
}

const loadWarningData = async () => {
  if (attrs.specificationId) {
    // 产品预警规则
    const { code, data, message } = await getProductStoreWarningCount({
      customerId: attrs.customerId,
      specificationId: attrs.specificationId
    })
    if (code !== 200) return ElMessage.warning(message)
    formData.warningNumber = data?.warningNumber ?? ''
  } else {
    // 客户预警规则
    const { code, data, message } = await getStoreWarningCount({ customerId: attrs.customerId })
    if (code !== 200) return ElMessage.warning(message)
    formData.warningNumber = data?.storeWarningNumber ?? ''
  }
}

onMounted(() => {
  loadWarningData()
})

const handleClose = () => {
  dialogVisible.value = false
}

const handleConfirm = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  loading.value = true
  try {
    let res
    if (attrs.specificationId) {
      // 产品预警规则
      res = await setProductStoreWarning({
        customerId: attrs.customerId,
        specificationId: attrs.specificationId,
        warningNumber: Number(formData.warningNumber)
      })
    } else {
      // 客户预警规则
      res = await setCustomerStoreWarning({
        customerId: attrs.customerId,
        warningNumber: Number(formData.warningNumber)
      })
    }

    if (res.code !== 200) return ElMessage.warning(res.message)
    ElMessage.success('保存成功')
    attrs.callback?.()
    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped></style>
