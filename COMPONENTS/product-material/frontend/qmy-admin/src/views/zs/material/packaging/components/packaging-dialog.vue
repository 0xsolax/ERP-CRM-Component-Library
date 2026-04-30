<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑包材' : '新增包材'" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="包材类型" prop="typeId">
            <ZsLabelSelect
              v-model="formData.typeId"
              nodeKey="PACKAGING_TYPE"
              :fallbackLabel="formData.typeName"
              :resultFilterFn="(item: any) => item.value2 === '0'"
              :addExtraData="{ value2: '0' }"
              @change="(v, l) => (formData.typeName = l)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="包材名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="尺寸">
            <el-input v-model="formData.size" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单价" prop="price">
            <el-input
              v-model="formData.price"
              placeholder="请输入"
              @input="(val: string) => (formData.price = validateDecimal(val))"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, useAttrs, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { savePackaging, updatePackaging } from '@/api/zs/material/packaging'
import ZsLabelSelect from '@/components/zs-label-select/index.vue'
import { validateDecimal } from '@/utils/validate'

const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)

const formData = reactive<any>({
  id: null,
  typeId: null,
  typeName: '',
  name: '',
  size: '',
  price: null
})

const rules = {
  typeId: [{ required: true, message: '请选择包材类型', trigger: 'change' }],
  name: [{ required: true, message: '请输入包材名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const handleSave = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (isEdit) {
      const { code, message } = await updatePackaging(formData)
      if (code !== 200) return ElMessage.warning(message)
    } else {
      const { code, message } = await savePackaging(formData)
      if (code !== 200) return ElMessage.warning(message)
    }
    ElMessage.success('保存成功')
    callback?.()
    onDestroy()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (isEdit && rowData) {
    Object.assign(formData, rowData)
  }
  nextTick(() => formRef.value?.clearValidate())
})
</script>
