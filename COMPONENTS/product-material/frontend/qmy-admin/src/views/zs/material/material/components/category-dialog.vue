<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="360px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="formData.name" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="formData.sortNum" :min="0" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <el-button v-if="isEdit" type="danger" plain @click="handleDeleteCategory">删除分类</el-button>
        <span v-else />
        <div>
          <el-button @click="onDestroy">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, useAttrs, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  saveMaterialCategory,
  updateMaterialCategory,
  deleteMaterialCategory,
  getMaterialPage
} from '@/api/zs/material/material'

const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)

const formData = reactive<any>({
  id: null,
  name: '',
  sortNum: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    if (isEdit) {
      const { code, message } = await updateMaterialCategory(formData)
      if (code !== 200) return ElMessage.warning(message)
    } else {
      const { code, message } = await saveMaterialCategory(formData)
      if (code !== 200) return ElMessage.warning(message)
    }
    ElMessage.success('保存成功')
    callback?.()
    onDestroy()
  } finally {
    loading.value = false
  }
}

const handleDeleteCategory = async () => {
  await ElMessageBox.confirm('确认删除该分类吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code: checkCode, data: checkData } = await getMaterialPage({
    pageNum: 1,
    pageSize: 1,
    categoryId: formData.id
  })
  if (checkCode === 200 && checkData?.total > 0) {
    ElMessage.warning(`分类「${formData.name}」下还有 ${checkData.total} 条材料，请先删除或移出材料后再删除分类`)
    return
  }
  const { code, message } = await deleteMaterialCategory({ id: formData.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  callback?.()
  onDestroy()
}

onMounted(() => {
  if (isEdit && rowData) {
    Object.assign(formData, rowData)
  }
  nextTick(() => formRef.value?.clearValidate())
})
</script>
