<template>
  <el-dialog v-model="dialogVisible" title="填写物流" width="500px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="客户名称">
        <span>{{ rowData?.customerName || '-' }}</span>
      </el-form-item>
      <el-form-item label="发货单号">
        <span>{{ rowData?.shipCode || rowData?.code || '-' }}</span>
      </el-form-item>

      <template v-if="isChina">
        <el-form-item label="国内物流单号" prop="packageCode">
          <el-input v-model="formData.packageCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="国内物流" prop="transportCompanyId">
          <el-select v-model="formData.transportCompanyId" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="item in chinaCompanyList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item label="国际物流" prop="transportCompanyId">
          <el-select v-model="formData.transportCompanyId" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="item in transportCompanyList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <span>{{ transportCompanyAddress }}</span>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { confirmDelivery, updateTransport, getTransportCompanyList } from '@/api/admin/warehouse'

const attrs = useAttrs()
const { rowData, onDestroy, callback, isEdit } = attrs as any
const dialogVisible = ref(true)

const isChina = ref(rowData?.isChina !== false)

const formRef = ref<any>()

const formData = reactive({
  packageCode: isEdit ? rowData?.packageCode : '',
  transportCompanyId: isEdit ? rowData?.transportCompanyId : ''
})

const rules = computed(() => {
  return {
    packageCode: isChina.value
      ? [
          {
            required: true,
            message: '请输入国内物流单号',
            trigger: 'blur'
          }
        ]
      : [],
    transportCompanyId: [
      {
        required: true,
        message: isChina.value ? '请选择国内物流' : '请选择国际物流',
        trigger: 'change'
      }
    ]
  }
})

const transportCompanyList = ref<any[]>([])
const chinaCompanyList = ref<any[]>([])

const currentTransportCompany = computed(() => {
  const id = formData.transportCompanyId
  if (!id) return undefined
  return transportCompanyList.value.find(item => item.id == id)
})

const transportCompanyAddress = computed(() => {
  const company = currentTransportCompany.value
  if (!company) return '-'
  const isHomeService = company.isHomeService == 1
  return isHomeService ? '上门取件' : company.address || '-'
})

const loadTransportCompanyList = async () => {
  const { code, data, message } = await getTransportCompanyList()
  if (code !== 200) return ElMessage.warning(message)
  transportCompanyList.value = (data || []).filter((item: any) => item.type == '1')
  chinaCompanyList.value = (data || []).filter((item: any) => item.type == '2')

  // 新增模式下才默认选第一项，编辑模式保留原值
  if (isChina.value && !isEdit) {
    formData.transportCompanyId = chinaCompanyList?.value?.[0]?.id ?? ''
  }
}

onMounted(() => {
  loadTransportCompanyList()
})

const handleConfirm = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const params: any = {
    id: rowData.id
  }

  if (isChina.value) {
    params.packageCode = formData.packageCode
    params.transportCompanyId = formData.transportCompanyId
  } else {
    params.transportCompanyId = formData.transportCompanyId
  }

  let result
  if (isEdit) {
    result = await updateTransport(params)
  } else {
    result = await confirmDelivery(params)
  }

  const { code, message } = result
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  if (callback) callback()
}
</script>

<style lang="scss" scoped>
:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
