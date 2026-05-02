<template>
  <el-dialog v-model="dialogVisible" title="导出" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
      <el-form-item label="导出：" prop="exportType">
        <el-radio-group v-model="form.exportType">
          <el-radio v-for="item in exportTypeOptions" :key="item.value" :label="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="规格名称：" prop="specNameType">
        <el-radio-group v-model="form.specNameType">
          <el-radio label="chinese">中文</el-radio>
          <el-radio label="english">英文</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="showLabelOptions" label="配置选项：" prop="configOptions">
        <el-checkbox-group v-model="form.configOptions">
          <el-checkbox label="customerItemNumber">客户货号</el-checkbox>
          <el-checkbox label="showSpecification">包含规格名(SKU)</el-checkbox>
          <el-checkbox label="showMade">MADE IN CHINA</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item v-if="showLabelOptions" label="抛数：" prop="abandonNumber">
        <el-input-number v-model="form.abandonNumber" :min="1" :step="1" :precision="0" style="width: 150px" />
      </el-form-item>

      <el-form-item v-if="showLabelOptions" label="规格对照表：">
        <el-checkbox v-model="form.exportComparison">同时导出</el-checkbox>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" :disabled="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, ref, useAttrs, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getYitangAdminToken, getYitangAdminTenantInfo } from '@/utils/auth'

const attrs = useAttrs()
const { onDestroy, orderId } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const submitting = ref(false)

// 导出类型选项: 1订单, 3标签
const exportTypeOptions = [
  { label: '订单', value: '1' },
  { label: '标签', value: '3' }
]

const form = ref({
  exportType: '',
  specNameType: '',
  configOptions: [] as string[],
  abandonNumber: undefined as number | undefined,
  exportComparison: false
})

const showLabelOptions = computed(() => form.value.exportType === '3')

const formRules = {
  exportType: [{ required: true, message: '请选择导出类型', trigger: 'change' }],
  specNameType: [{ required: true, message: '请选择规格名称', trigger: 'change' }],
  configOptions: [
    {
      validator: (_rule: any, _value: string[], callback: any) => {
        callback()
      },
      trigger: 'change'
    }
  ],
  abandonNumber: [
    {
      validator: (_rule: any, value: number | undefined, callback: any) => {
        if (!showLabelOptions.value) {
          callback()
          return
        }
        if (value === undefined || value === null) {
          callback(new Error('请输入抛数'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

watch(
  () => form.value.exportType,
  newValue => {
    if (newValue !== '3') {
      form.value.configOptions = []
      form.value.abandonNumber = undefined
      form.value.exportComparison = false
      formRef.value?.clearValidate(['configOptions', 'abandonNumber'])
    } else {
      formRef.value?.clearValidate(['configOptions', 'abandonNumber'])
    }
  }
)

const exportOrderFile = async (type: string) => {
  const { status, message } = await downloadAxiosBlobFile({
    url: `${import.meta.env.VITE_APP_YITANG_BASE_API}/sal/yt/order/export`,
    headers: {
      'qiaomoyun-tenant': getYitangAdminTenantInfo()?.id,
      'qiaomoyun-token': getYitangAdminToken()
    },
    data: {
      orderId: orderId,
      type,
      isEnglish: form.value.specNameType === 'english' ? 1 : 0,
      isShowMade: form.value.configOptions.includes('showMade') ? 1 : 0,
      isCustomerItemNumber: form.value.configOptions.includes('customerItemNumber') ? 1 : 0,
      isShowSpecification: form.value.configOptions.includes('showSpecification') ? 1 : 0,
      abandonNumber: showLabelOptions.value && form.value.abandonNumber ? String(form.value.abandonNumber) : ''
    },
    method: 'post'
  })
  return { status, message }
}

const handleSubmit = async () => {
  if (submitting.value) return

  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true

  try {
    const labelResult = await exportOrderFile(form.value.exportType)
    if (labelResult.status !== 200) return ElMessage.warning(labelResult.message)

    if (showLabelOptions.value && form.value.exportComparison) {
      const comparisonResult = await exportOrderFile('2')
      if (comparisonResult.status !== 200) return ElMessage.warning(comparisonResult.message)
    }

    ElMessage.success('导出成功')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
:deep(.el-checkbox-group),
:deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 20px;
}

:deep(.el-checkbox),
:deep(.el-radio) {
  margin-right: 0;
}
</style>
