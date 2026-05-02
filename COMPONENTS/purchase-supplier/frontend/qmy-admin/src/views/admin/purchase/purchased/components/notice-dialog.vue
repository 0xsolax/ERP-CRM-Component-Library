<template>
  <el-dialog v-model="dialogVisible" title="通知" width="400px" @close="onDestroy">
    <div class="notice-tip">确认已通知供应商规格颜色</div>
    <el-form :model="form" label-position="left">
      <el-form-item label="产品ID：">
        <div class="info-item">{{ productCode }}</div>
      </el-form-item>
      <el-form-item label="规格：">
        <div class="info-item">{{ specName }}</div>
      </el-form-item>
      <el-form-item label="供应商单价：">
        <el-input
          v-model="form.supplierPrice"
          placeholder="请输入"
          @input="(val: string) => (form.supplierPrice = validateDecimal(val))"
        >
          <template #prefix>¥</template>
        </el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { notifyPurchase } from '@/api/admin/purchase/purchased'

const attrs = useAttrs()
const { purchaseItemId, productCode, specName, onDestroy, onSuccess } = attrs as any
const dialogVisible = ref(true)

const form = ref({
  supplierPrice: ''
})

// 校验数值（指定小数位数）
const validateDecimal = (value: string, decimalPlaces = 2): string => {
  let result = value.replace(/[^\d.]/g, '')
  const parts = result.split('.')
  if (parts.length > 2) {
    result = parts[0] + '.' + parts.slice(1).join('')
  }
  if (parts.length === 2 && parts[1].length > decimalPlaces) {
    result = parts[0] + '.' + parts[1].substring(0, decimalPlaces)
  }
  return result
}

const handleSubmit = async () => {
  const { code, message } = await notifyPurchase({
    purchaseItemId: purchaseItemId,
    supplierPrice: form.value.supplierPrice
  })

  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  dialogVisible.value = false

  if (onSuccess && typeof onSuccess === 'function') {
    onSuccess()
  }
}
</script>

<style scoped lang="scss">
.notice-tip {
  margin-top: 10px;
  margin-bottom: 24px;
}
</style>
