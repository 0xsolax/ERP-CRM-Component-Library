<template>
  <el-dialog v-model="dialogVisible" title="新增包材名称" width="400px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="包材名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入包材名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { addPackingName } from '@/api/sed/product/packing'

const attrs = useAttrs()
const { type, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const loading = ref(false)

const formRef = ref()
const form = ref({ name: '' })
const rules = {
  name: [{ required: true, message: '请输入包材名称', trigger: 'blur' }]
}

const handleConfirm = async () => {
  await formRef.value?.validate()
  loading.value = true
  const { code, message } = await addPackingName({ type, name: form.value.name })
  loading.value = false
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  if (callback) callback()
  dialogVisible.value = false
}
</script>
