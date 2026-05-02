<template>
  <el-dialog
    v-model="dialogVisible"
    title="预警规则"
    width="400px"
    :before-close="handleBeforeClose"
    @close="onDestroy"
  >
    <el-form ref="formRef" :model="form" :rules="rules">
      <el-form-item label="可用库存+可用在途小于等于" prop="threshold">
        <el-input
          v-model="form.threshold"
          placeholder="请输入"
          @input="(val: string) => (form.threshold = validateInteger(val))"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleBeforeClose()">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, useAttrs, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWarningRule, saveWarningRule, getSpecWarningRule, saveSpecWarningRule } from '@/api/admin/warehouse'
import { validateInteger } from '@/utils/validate'

const attrs = useAttrs()
const { dialogType, storeId, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const formRef = ref()

const form = reactive({
  threshold: ''
})

const rules = reactive({
  threshold: [{ required: true, message: '请输入预警阈值', trigger: 'blur' }]
})

const hasContent = computed(() => !!form.threshold)

const handleBeforeClose = async (done?: () => void) => {
  if (!hasContent.value) {
    if (done) done()
    else onDestroy?.()
    return
  }
  await ElMessageBox.confirm('有内容未保存，是否确认关闭？', '提示', {
    confirmButtonText: '确认关闭',
    cancelButtonText: '取消',
    type: 'warning'
  })
  if (done) done()
  else onDestroy?.()
}

const loadWarningRule = async () => {
  let apiCall
  if (dialogType === 'single') {
    apiCall = getSpecWarningRule(storeId)
  } else {
    apiCall = getWarningRule()
  }

  const { code, data, message } = await apiCall
  if (code !== 200) return ElMessage.warning(message)
  form.threshold = data || ''
}

onMounted(() => {
  loadWarningRule()
})
const handleSubmit = async () => {
  await formRef.value.validate()
  let apiCall
  if (dialogType === 'single') {
    apiCall = saveSpecWarningRule(storeId, form.threshold)
  } else {
    apiCall = saveWarningRule(form.threshold)
  }

  const { code, message } = await apiCall
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  if (callback) callback()
}
</script>

<style scoped lang="scss"></style>
