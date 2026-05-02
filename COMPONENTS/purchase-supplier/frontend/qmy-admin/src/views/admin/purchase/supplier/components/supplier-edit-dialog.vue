<template>
  <el-dialog v-model="dialogVisible" title="编辑" width="600px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="供应商名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入" maxlength="50" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="简称" prop="shortName">
        <el-input v-model="formData.shortName" placeholder="请输入" maxlength="20" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="公司地址" prop="address">
        <el-input v-model="formData.address" placeholder="请输入" maxlength="100" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入"
          maxlength="100"
          show-word-limit
          clearable
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateSupplier } from '@/api/admin/purchase/supplier'

const attrs = useAttrs()
const { onDestroy, callback, ...rowData } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()

const formData = reactive({
  id: '',
  code: '',
  name: '',
  shortName: '',
  address: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const submitData = {
    id: formData.id,
    code: formData.code,
    name: formData.name,
    shortName: formData.shortName,
    address: formData.address,
    remark: formData.remark
  }

  const { code, message } = await updateSupplier(submitData)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('更新成功')

  if (callback) {
    callback()
  }

  dialogVisible.value = false
  setTimeout(() => {
    onDestroy?.()
  }, 100)
}

onMounted(() => {
  if (rowData) {
    Object.assign(formData, {
      id: rowData.id || '',
      code: rowData.code || '',
      name: rowData.name || '',
      shortName: rowData.shortName || '',
      address: rowData.address || '',
      remark: rowData.remark || ''
    })
  }
})
</script>

<style scoped lang="scss"></style>
