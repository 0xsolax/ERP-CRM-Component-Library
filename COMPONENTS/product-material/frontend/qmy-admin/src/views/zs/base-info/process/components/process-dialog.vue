<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑工序' : '新增工序'" width="400px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <el-form-item label="工序名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { saveOrUpdateProcess } from '@/api/zs/base-info/process'

const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)

const formData = reactive<any>({
  id: null,
  name: ''
})

if (isEdit && rowData) {
  Object.assign(formData, rowData)
}

const rules = {
  name: [{ required: true, message: '请输入工序名称', trigger: 'blur' }]
}

const handleSave = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { code, message } = await saveOrUpdateProcess(formData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('保存成功')
    callback?.()
    onDestroy()
  } finally {
    loading.value = false
  }
}
</script>
