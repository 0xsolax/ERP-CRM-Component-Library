<template>
  <el-dialog v-model="dialogVisible" title="一键转订单" width="400px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单来源" prop="source">
        <el-select v-model="form.source" placeholder="请选择订单来源" style="width: 100%">
          <el-option v-for="item in orderSourceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNumber">
        <el-input v-model="form.contractNumber" placeholder="请输入合同编号" maxlength="50" clearable />
      </el-form-item>
      <el-form-item label="交货日期" prop="deliveryDate">
        <el-date-picker
          v-model="form.deliveryDate"
          type="date"
          placeholder="请选择交货日期"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { orderSourceList } from '@/constant/sed/sales'
import { convertToOrder, skuToOrder, mergeToOrder } from '@/api/sed/sales/quotation'

const attrs = useAttrs()
const { onDestroy, callback, rowData, fromSku, skuData, fromMerge, quotationSkuIds } = attrs as any
const dialogVisible = ref(true)
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = ref({
  source: '',
  contractNumber: '',
  deliveryDate: ''
})

const rules: FormRules = {
  source: [{ required: true, message: '请选择订单来源', trigger: 'change' }],
  contractNumber: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  deliveryDate: [{ required: true, message: '请选择交货日期', trigger: 'change' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    let result

    if (fromMerge) {
      // 从合并转订单
      result = await mergeToOrder({
        quotationSkuIds: quotationSkuIds,
        orderSource: form.value.source,
        contractNumber: form.value.contractNumber,
        deliveryDate: form.value.deliveryDate
      })
    } else if (fromSku) {
      // 从SKU转订单
      result = await skuToOrder({
        quotationId: rowData?.id,
        quotationSkuId: skuData.quotationSkuId,
        orderSource: form.value.source,
        contractNumber: form.value.contractNumber,
        deliveryDate: form.value.deliveryDate
      })
    } else {
      // 普通转订单
      result = await convertToOrder({
        quotationId: rowData?.id,
        orderSource: form.value.source,
        contractNumber: form.value.contractNumber,
        deliveryDate: form.value.deliveryDate
      })
    }

    const { code, message } = result
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    loading.value = false
  }
}
</script>
