<template>
  <el-dialog v-model="dialogVisible" title="物流成本预估" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="总运输体积">
        <span>{{ detailData.totalTransportVolume }}m³</span>
      </el-form-item>
      <el-form-item label="总运箱数">
        <span>{{ detailData.totalTransportBox }}箱</span>
      </el-form-item>
      <el-form-item label="收货地址">
        <span>{{ detailData.receiveAddress || '-' }}</span>
      </el-form-item>
      <el-form-item label="物流总成本" prop="logisticsCost">
        <el-input v-model="form.logisticsCost" placeholder="请输入" style="width: 300px" />
      </el-form-item>
      <el-form-item label="备注" prop="logisticsRemark">
        <el-input v-model="form.logisticsRemark" placeholder="请输入" style="width: 300px" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getLogisticsDetail, logisticsConfirm } from '@/api/sed/sales/quotation'
import { useUserStore } from '@/views/sed/store/modules/user'

const userStore = useUserStore()
const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const loading = ref(false)
const formRef = ref<FormInstance>()

const detailData = ref({
  totalTransportVolume: 0,
  totalTransportBox: 0,
  receiveAddress: ''
})

const form = reactive({
  logisticsCost: '',
  logisticsRemark: ''
})

const rules = reactive<FormRules>({
  logisticsCost: [{ required: true, message: '请输入物流总成本', trigger: 'blur' }]
})

const loadDetail = async () => {
  if (!rowData?.id) return
  const { code, data, message } = await getLogisticsDetail({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)

  detailData.value = {
    totalTransportVolume: data?.totalTransportVolume || 0,
    totalTransportBox: data?.totalTransportBox || 0,
    receiveAddress: data?.receiveAddress || ''
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { code, message } = await logisticsConfirm({
      id: rowData.id,
      salesmanId: userStore.userId,
      logisticsCost: parseFloat(form.logisticsCost) || 0,
      logisticsRemark: form.logisticsRemark || ''
    })
    if (code !== 200) return ElMessage.warning(message)

    ElMessage.success('确认成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss"></style>
