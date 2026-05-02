<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑物流公司' : '新增物流公司'"
    width="600px"
    :before-close="handleBeforeClose"
    @close="onDestroy"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" label-position="top">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="物流公司名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="物流公司类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in logisticsTypeList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="是否上门取件" prop="isHomeService">
            <el-select v-model="form.isHomeService" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in homeServiceList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="!isHomeService">
          <el-form-item label="地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入" type="textarea" :rows="2" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="handleBeforeClose()">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addLogistics, updateLogistics, getLogisticsDetail } from '@/api/admin/warehouse'
import { logisticsTypeList, homeServiceList } from '@/constant/yitang/warehouse'
import { usePermissionStore } from '@/views/admin/store/modules/permission'

const permissionStore = usePermissionStore()
const userPermissions = permissionStore.permissions
const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const formRef = ref()

const form = reactive({
  id: '',
  code: '',
  name: '',
  type: '',
  isHomeService: '',
  address: ''
})

const rules = reactive({
  name: [{ required: true, message: '请输入物流公司名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择物流公司类型', trigger: 'change' }],
  isHomeService: [{ required: true, message: '请选择是否上门取件', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
})

const hasContent = computed(() => {
  return !!(form.name || form.type || form.address)
})

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

const isHomeService = computed(() => {
  return form.isHomeService == '1'
})

watch(
  () => form.isHomeService,
  val => {
    if (val == '1') {
      rules.address = [] as any
      form.address = ''
      formRef.value?.clearValidate?.('address')
    } else {
      rules.address = [{ required: true, message: '请输入地址', trigger: 'blur' }] as any
    }
  },
  { immediate: true }
)

const loadDetail = async () => {
  if (!isEdit) return
  const { code, data, message } = await getLogisticsDetail(rowData.id)
  if (code !== 200) return ElMessage.warning(message)
  if (data) {
    form.id = data.id || ''
    form.code = data.code || ''
    form.name = data.name || ''
    form.type = data.type || ''
    form.isHomeService = data.isHomeService || 0
    form.address = data.address || ''
  }
}

onMounted(() => {
  if (userPermissions.includes('sto:yt:transportCompany:get')) {
    loadDetail()
  }
})

const handleSubmit = async () => {
  await formRef.value.validate()
  const submitData: any = {
    name: form.name,
    type: form.type,
    address: form.address || '',
    isHomeService: form.isHomeService
  }

  let res
  if (isEdit) {
    submitData.id = form.id
    res = await updateLogistics(submitData)
  } else {
    res = await addLogistics(submitData)
  }

  const { code, message } = res
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success(isEdit ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  if (callback) callback()
}
</script>

<style scoped lang="scss"></style>
