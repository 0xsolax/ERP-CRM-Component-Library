<template>
  <el-dialog v-model="dialogVisible" title="修改发货方式" width="300px" @close="onDestroy">
    <el-form label-width="80px">
      <el-form-item label="请选择">
        <el-select v-model="shippingMethod" placeholder="请选择" style="width: 100%">
          <el-option v-for="item in shippingMethodList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { shippingMethodList } from '@/constant/yitang/sales'
import { updateShippingMethod } from '@/api/admin/sales/order'

const attrs = useAttrs()
const { onDestroy, currentValue, orderId, onSuccess } = attrs as any

const dialogVisible = ref(true)
const shippingMethod = ref(String(currentValue || ''))
const loading = ref(false)

const handleConfirm = async () => {
  if (!shippingMethod.value) {
    ElMessage.warning('请选择发货方式')
    return
  }

  loading.value = true
  try {
    const { code, message } = await updateShippingMethod({
      orderId: orderId,
      shippingMethod: shippingMethod.value
    })

    if (code !== 200) {
      ElMessage.warning(message || '修改失败')
      return
    }

    ElMessage.success('发货方式修改成功')

    if (onSuccess && typeof onSuccess === 'function') {
      onSuccess()
    }

    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss"></style>
