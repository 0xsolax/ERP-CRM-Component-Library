<template>
  <el-dialog v-model="dialogVisible" title="编辑" width="700px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="110px">
      <el-form-item label="客户名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="归属" prop="belongEmployeeId">
        <el-select v-model="formData.belongEmployeeId" placeholder="请选择" clearable style="width: 100%">
          <el-option v-for="item in accountList" :key="item.userId" :label="item.nickName" :value="item.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="跟进人" prop="followEmployeeId">
        <el-select v-model="formData.followEmployeeId" placeholder="请选择" clearable style="width: 100%">
          <el-option v-for="item in accountList" :key="item.userId" :label="item.nickName" :value="item.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户类型" prop="type">
        <LabelSelect
          v-model="formData.type"
          :options="customerTypeList"
          :multiple="false"
          :show-edit-icon="false"
          :add-label-api="addCustomerType"
          :update-label-api="updateCustomerType"
          @refresh="loadCustomerTypeList"
        />
      </el-form-item>
      <el-form-item label="客户来源" prop="customerSource">
        <LabelSelect
          v-model="formData.customerSource"
          :options="customerSourceList"
          :multiple="false"
          :show-edit-icon="false"
          :add-label-api="addCustomerSource"
          :update-label-api="updateCustomerSource"
          @refresh="loadCustomerSourceList"
        />
      </el-form-item>
      <el-form-item label="手动层级" prop="handLevel">
        <el-select v-model="formData.handLevel" placeholder="请选择" clearable style="width: 100%">
          <el-option v-for="item in manualLevelList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="国家地区" prop="countryRegion">
        <el-select
          v-model="formData.countryRegion"
          placeholder="请选择国家地区"
          clearable
          filterable
          style="width: 100%"
        >
          <el-option
            v-for="item in countryRegionOptions"
            :key="item.regionId"
            :label="item.label"
            :value="item.regionId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="公司名称" prop="companyName">
        <el-input v-model="formData.companyName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="公司官网" prop="companyWebsite">
        <el-input v-model="formData.companyWebsite" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="公司地址" prop="companyAddress">
        <el-input v-model="formData.companyAddress" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="管家婆客户名称" prop="guanjiapoName">
        <el-input v-model="formData.guanjiapoName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="管家婆客户编码" prop="guanjiapoCode">
        <el-input v-model="formData.guanjiapoCode" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入" clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import LabelSelect from '@/components/label-select/index.vue'
import { updateCustomer, getDictionaryByCode, saveDictionary } from '@/api/sed/sales/customer'
import { queryAccountList } from '@/api/qmy/auth/account'
import { getCountries } from '@/api/sed/system/region'
import { manualLevelList } from '@/constant/sed/customer'

const attrs = useAttrs()
const { onDestroy, callback, ...rowData } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const accountList = ref<any[]>([])
const countryRegionOptions = ref<any[]>([])
const customerTypeList = ref<any[]>([])
const customerSourceList = ref<any[]>([])

const formData = reactive({
  id: rowData.id || '',
  code: rowData.code || '',
  name: rowData.name || '',
  belongEmployeeId: rowData.belongEmployeeId || '',
  belongEmployeeName: rowData.belongEmployeeName || '',
  followEmployeeId: rowData.followEmployeeId || '',
  type: rowData.type || '',
  customerSource: rowData.customerSource || '',
  handLevel: rowData.handLevel || '',
  guanjiapoName: rowData.guanjiapoName || '',
  guanjiapoCode: rowData.guanjiapoCode || '',
  countryRegion: rowData.countryRegion || '',
  countryRegionName: rowData.countryRegionName || '',
  companyName: rowData.companyName || '',
  companyWebsite: rowData.companyWebsite || '',
  companyAddress: rowData.companyAddress || '',
  remark: rowData.remark || ''
})

const rules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

const loadAccountList = async () => {
  const { code, data, message } = await queryAccountList({})
  if (code !== 200) return ElMessage.warning(message)
  const list = data?.list || []
  const employees = [
    { id: rowData.belongEmployeeId, name: rowData.belongEmployeeName },
    { id: rowData.followEmployeeId, name: rowData.followEmployeeName }
  ]
  employees.forEach(({ id, name }) => {
    if (id && name && !list.some((item: any) => item.userId === id)) {
      list.unshift({ userId: id, nickName: name })
    }
  })
  accountList.value = list
}

// 加载国家地区数据
const loadCountryRegions = async () => {
  const { code, data, message } = await getCountries()
  if (code !== 200) return ElMessage.warning(message)
  countryRegionOptions.value = (data || []).map((country: any) => ({
    value: country.regionName,
    label: country.regionName,
    regionId: country.regionId,
    englishName: country.englishName
  }))
}

// 加载客户类型列表
const loadCustomerTypeList = async () => {
  const { code, data, message } = await getDictionaryByCode({ code: 'customerType' })
  if (code !== 200) return ElMessage.warning(message)
  customerTypeList.value = (data || []).map(item => ({
    label: item.value,
    value: item.id,
    key: item.key
  }))
}

// 加载客户来源列表
const loadCustomerSourceList = async () => {
  const { code, data, message } = await getDictionaryByCode({ code: 'customerSource' })
  if (code !== 200) return ElMessage.warning(message)
  customerSourceList.value = (data || []).map(item => ({
    label: item.value,
    value: item.id,
    key: item.key
  }))
}

// 添加客户类型
const addCustomerType = async (params: { value: string }) => {
  const maxKey =
    customerTypeList.value.length > 0 ? Math.max(...customerTypeList.value.map(item => parseInt(item.key || '0'))) : 0
  const nextKey = String(maxKey + 1)

  return await saveDictionary({
    code: 'customerType',
    key: nextKey,
    value: params.value
  })
}

// 更新客户类型
const updateCustomerType = async (params: { oldValue: string; value: string }) => {
  const maxKey =
    customerTypeList.value.length > 0 ? Math.max(...customerTypeList.value.map(item => parseInt(item.key || '0'))) : 0
  const nextKey = String(maxKey + 1)

  return await saveDictionary({
    code: 'customerType',
    key: nextKey,
    value: params.value
  })
}

// 添加客户来源
const addCustomerSource = async (params: { value: string }) => {
  const maxKey =
    customerSourceList.value.length > 0
      ? Math.max(...customerSourceList.value.map(item => parseInt(item.key || '0')))
      : 0
  const nextKey = String(maxKey + 1)

  return await saveDictionary({
    code: 'customerSource',
    key: nextKey,
    value: params.value
  })
}

// 更新客户来源
const updateCustomerSource = async (params: { oldValue: string; value: string }) => {
  const maxKey =
    customerSourceList.value.length > 0
      ? Math.max(...customerSourceList.value.map(item => parseInt(item.key || '0')))
      : 0
  const nextKey = String(maxKey + 1)

  return await saveDictionary({
    code: 'customerSource',
    key: nextKey,
    value: params.value
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const apiData = {
    id: formData.id,
    code: formData.code,
    name: formData.name,
    belongEmployeeId: formData.belongEmployeeId || '',
    followEmployeeId: formData.followEmployeeId || '',
    type: formData.type || '',
    customerSource: formData.customerSource,
    handLevel: formData.handLevel || '',
    guanjiapoName: formData.guanjiapoName || '',
    guanjiapoCode: formData.guanjiapoCode || '',
    countryRegion: formData.countryRegion,
    countryRegionName: formData.countryRegionName,
    companyName: formData.companyName,
    companyWebsite: formData.companyWebsite || '',
    companyAddress: formData.companyAddress || '',
    remark: formData.remark || ''
  }

  const { code, message } = await updateCustomer(apiData)
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  if (callback) {
    callback()
  }

  ElMessage.success('操作成功')
  dialogVisible.value = false

  setTimeout(() => {
    onDestroy?.()
  }, 100)
}

onMounted(() => {
  loadAccountList()
  loadCountryRegions()
  loadCustomerTypeList()
  loadCustomerSourceList()
})
</script>

<style scoped lang="scss">
:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
