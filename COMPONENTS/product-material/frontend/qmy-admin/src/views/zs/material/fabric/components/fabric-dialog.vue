<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑面料' : '新增面料'" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="种类" prop="typeId">
            <ZsLabelSelect
              v-model="formData.typeId"
              nodeKey="FABRIC_TYPE"
              :fallbackLabel="formData.typeName"
              @change="(v, l) => (formData.typeName = l)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="型号" prop="modelId">
            <ZsLabelSelect
              v-model="formData.modelId"
              nodeKey="FABRIC_MODEL"
              :fallbackLabel="formData.modelName"
              @change="(v, l) => (formData.modelName = l)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="门幅" prop="widthId">
            <div style="display: flex; align-items: center; gap: 4px; width: 100%">
              <ZsLabelSelect
                v-model="formData.widthId"
                nodeKey="FABRIC_WIDTH"
                :fallbackLabel="formData.widthName"
                @change="(v, l) => (formData.widthName = l)"
                style="flex: 1; min-width: 0"
              />
              <span style="color: #909399; font-size: 12px; white-space: nowrap">cm</span>
            </div>
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
        <el-col :span="12" style="display: flex; justify-content: flex-end">
          <el-form-item label="单位" prop="unit">
            <el-radio-group v-model="formData.unit">
              <el-radio value="米">米</el-radio>
              <el-radio value="码">码</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, useAttrs, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { saveFabric, updateFabric } from '@/api/zs/material/fabric'
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
  modelId: null,
  widthId: null,
  price: null,
  unit: ''
})

const rules = {
  typeId: [{ required: true, message: '请选择种类', trigger: 'change' }],
  modelId: [{ required: true, message: '请选择型号', trigger: 'change' }],
  widthId: [{ required: true, message: '请选择门幅', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }]
}

const handleSave = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (isEdit) {
      const { code, message } = await updateFabric(formData)
      if (code !== 200) return ElMessage.warning(message)
    } else {
      const { code, message } = await saveFabric(formData)
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
