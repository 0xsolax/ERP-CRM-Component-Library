<template>
  <el-dialog v-model="dialogVisible" title="编辑" width="700px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
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
        <el-select v-model="formData.type" placeholder="请选择" clearable style="width: 100%">
          <el-option v-for="item in customerTypeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
      <el-form-item label="公司官网" prop="companyWebsite">
        <el-input v-model="formData.companyWebsite" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="公司地址" prop="companyAddress">
        <el-input v-model="formData.companyAddress" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="默认订单备注" prop="orderDefaultRemark">
        <el-input v-model="formData.orderDefaultRemark" type="textarea" :rows="3" placeholder="请输入" clearable />
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
import { updateCustomer } from '@/api/admin/sales/customer'
import { queryAccountList } from '@/api/qmy/auth/account'
import { getCountries } from '@/api/admin/system/region'
import { customerTypeList, manualLevelList } from '@/constant/yitang/customer'

const attrs = useAttrs()
const { onDestroy, callback, ...rowData } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const accountList = ref<any[]>([])
const countryRegionOptions = ref<any[]>([])

const formData = reactive({
  id: rowData.id || '',
  code: rowData.code || '',
  name: rowData.name || '',
  belongEmployeeId: rowData.belongEmployeeId || '',
  belongEmployeeName: rowData.belongEmployeeName || '',
  followEmployeeId: rowData.followEmployeeId || '',
  type: rowData.type || '',
  handLevel: rowData.handLevel || '',
  countryRegion: rowData.countryRegion || '',
  countryRegionName: rowData.countryRegionName || '',
  companyWebsite: rowData.companyWebsite || '',
  companyAddress: rowData.companyAddress || '',
  remark: rowData.remark || '',
  orderDefaultRemark: rowData.orderDefaultRemark || ''
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
    handLevel: formData.handLevel || '',
    countryRegion: formData.countryRegion,
    countryRegionName: formData.countryRegionName,
    companyWebsite: formData.companyWebsite || '',
    companyAddress: formData.companyAddress || '',
    remark: formData.remark || '',
    orderDefaultRemark: formData.orderDefaultRemark || ''
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
})
</script>

<style scoped lang="scss">
:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
