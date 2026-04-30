<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑收货地址' : '新增收货地址'" width="700px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="130px">
      <el-form-item label="收货人" prop="consignee">
        <el-input v-model="formData.consignee" placeholder="请输入" maxlength="50" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="收货人联系方式" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入" maxlength="20" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="国家地区" prop="countryRegionId">
        <el-select
          v-model="formData.countryRegionId"
          placeholder="请选择国家地区"
          clearable
          filterable
          style="width: 100%"
          @change="handleCountryChange"
        >
          <el-option
            v-for="item in countryRegionOptions"
            :key="item.countryRegionId"
            :label="item.label"
            :value="item.countryRegionId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="省/市/区" prop="selectedArea" v-if="formData.countryRegionName === '中国'">
        <el-cascader
          ref="cascaderRef"
          v-model="formData.selectedArea"
          :options="regionOptions"
          :props="cascaderProps"
          :filter-method="regionFilterMethod"
          placeholder="请选择省/市/区"
          clearable
          filterable
          style="width: 100%"
          @change="handleRegionChange"
        />
      </el-form-item>
      <el-form-item label="详细地址" prop="detail">
        <el-input v-model="formData.detail" placeholder="请输入详细地址" maxlength="200" show-word-limit clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createOrUpdateAddress } from '@/api/admin/sales/customer'
import { getCountries, getAllRegion } from '@/api/admin/system/region'
// import { mobileReg } from '@/utils/rules'

const attrs = useAttrs()
const { onDestroy, callback, fromType, customerId, ...rowData } = attrs as any

console.log('rowData', rowData)

const dialogVisible = ref(true)
const formRef = ref()
const cascaderRef = ref()
const isEdit = ref(false)
const countryRegionOptions = ref<any[]>([])
const regionOptions = ref<any[]>([])

const formData = reactive({
  consignee: '',
  phone: '',
  countryRegionId: '',
  countryRegionName: '',
  selectedArea: [] as string[],
  provinceId: '',
  province: '',
  cityId: '',
  city: '',
  countyId: '',
  county: '',
  detail: ''
})

const rules = {
  consignee: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入收货人联系方式', trigger: 'blur' }
    // { pattern: mobileReg, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  countryRegionId: [{ required: true, message: '请选择国家地区', trigger: 'change' }],
  selectedArea: [{ required: true, message: '请选择省/市/区', trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 加载国家地区数据
const loadCountryRegions = async () => {
  const { code, data, message } = await getCountries()
  if (code !== 200) return ElMessage.warning(message)
  countryRegionOptions.value = (data || []).map((country: any) => ({
    value: country.regionName,
    label: country.regionName,
    countryRegionId: country.regionId,
    englishName: country.englishName
  }))
}

const cascaderProps = {
  value: 'regionId',
  label: 'regionName',
  children: 'children'
}

const regionFilterMethod = (node: any, keyword: string) => {
  const pathLabels = node.pathNodes.map((n: any) => n.label)
  const fullPath = pathLabels.join('')
  return fullPath.includes(keyword)
}

const loadRegionData = async () => {
  const { code, data, message } = await getAllRegion()
  if (code !== 200) return ElMessage.warning(message)
  regionOptions.value = data || []
}

const handleCountryChange = (regionId: string) => {
  const country = countryRegionOptions.value.find(item => item.countryRegionId === regionId)
  formData.countryRegionName = country?.label || ''
  formData.selectedArea = []
  formData.provinceId = ''
  formData.province = ''
  formData.cityId = ''
  formData.city = ''
  formData.countyId = ''
  formData.county = ''
}

const handleRegionChange = (value: string[]) => {
  if (value && value.length > 0) {
    const nodes = cascaderRef.value?.getCheckedNodes()
    if (nodes && nodes.length > 0) {
      const pathLabels = nodes[0].pathLabels || []
      const pathValues = nodes[0].pathValues || []
      formData.provinceId = pathValues[0] || ''
      formData.province = pathLabels[0] || ''
      formData.cityId = pathValues[1] || ''
      formData.city = pathLabels[1] || ''
      formData.countyId = pathValues[2] || ''
      formData.county = pathLabels[2] || ''
    }
  } else {
    formData.provinceId = ''
    formData.province = ''
    formData.cityId = ''
    formData.city = ''
    formData.countyId = ''
    formData.county = ''
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const province = formData.province || ''
  const city = formData.city || ''
  const county = formData.county || ''

  const result = {
    consignee: formData.consignee,
    phone: formData.phone,
    countryRegion: formData.countryRegionName,
    countryRegionId: formData.countryRegionId,
    regionId: formData.selectedArea[formData.selectedArea.length - 1] || '',
    provinceId: formData.provinceId,
    province,
    cityId: formData.cityId,
    city,
    countyId: formData.countyId,
    county,
    detail: formData.detail
  }

  if (fromType === 'customer-detail') {
    const apiData = {
      id: isEdit.value ? rowData?.id : undefined,
      customerId: customerId,
      consignee: formData.consignee,
      phone: formData.phone,
      countryRegion: formData.countryRegionName,
      countryRegionId: formData.countryRegionId || null,
      regionId: formData.selectedArea[formData.selectedArea.length - 1] || null,
      provinceId: formData.provinceId || '',
      province,
      cityId: formData.cityId || '',
      city,
      countyId: formData.countyId || '',
      county,
      detail: formData.detail
    }

    const { code, message } = await createOrUpdateAddress(apiData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
  }

  if (callback) {
    callback(result)
  }

  dialogVisible.value = false

  setTimeout(() => {
    onDestroy?.()
  }, 100)
}

const initEditData = async () => {
  if (!rowData) return

  isEdit.value = true
  formData.consignee = rowData.consignee || ''
  formData.phone = rowData.phone || ''
  formData.countryRegionId = rowData.countryRegionId || ''
  formData.countryRegionName = rowData.countryRegion || ''
  formData.provinceId = rowData.provinceId || ''
  formData.province = rowData.province || ''
  formData.cityId = rowData.cityId || ''
  formData.city = rowData.city || ''
  formData.countyId = rowData.countyId || ''
  formData.county = rowData.county || ''
  formData.detail = rowData.detail || ''

  if (formData.countryRegionName === '中国' && formData.provinceId) {
    const areaIds = [formData.provinceId]
    if (formData.cityId) {
      areaIds.push(formData.cityId)
      if (formData.countyId) {
        areaIds.push(formData.countyId)
      }
    }

    formData.selectedArea = [...new Set(areaIds)]
  }
}

initEditData()

onMounted(() => {
  loadCountryRegions()
  loadRegionData()
})
</script>

<style scoped lang="scss">
:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
