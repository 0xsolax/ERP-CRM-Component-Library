<template>
  <div class="customer-add-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑客户' : '新增客户' }}</h2>
    </div>

    <!-- 基础信息 -->
    <div class="form-content">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="top">
        <div class="section-title">基础信息</div>
        <div class="form-row form-row-4">
          <el-form-item label="客户名称" prop="customerName" required>
            <el-input v-model="formData.customerName" placeholder="请输入" maxlength="20" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="业务员" prop="salesman" required>
            <el-select v-model="formData.salesman" placeholder="请选择" clearable style="width: 100%">
              <el-option v-for="item in employeeList" :key="item.userId" :label="item.nickName" :value="item.userId" />
            </el-select>
          </el-form-item>
          <el-form-item label="跟进人" prop="follower">
            <el-select v-model="formData.follower" placeholder="请选择" clearable style="width: 100%">
              <el-option v-for="item in employeeList" :key="item.userId" :label="item.nickName" :value="item.userId" />
            </el-select>
          </el-form-item>
          <el-form-item label="客户类型" prop="customerType" required>
            <LabelSelect
              v-model="formData.customerType"
              :options="customerTypeList"
              :multiple="false"
              :show-edit-icon="false"
              :add-label-api="addCustomerType"
              :update-label-api="updateCustomerType"
              @refresh="loadCustomerTypeList"
            />
          </el-form-item>
        </div>

        <div class="form-row form-row-4">
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
          <el-form-item label="手动层级" prop="manualLevel">
            <el-select v-model="formData.manualLevel" placeholder="请选择" clearable style="width: 100%">
              <el-option v-for="item in manualLevelList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="国家地区" prop="regionId" required>
            <el-select
              v-model="formData.regionId"
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
            <el-input v-model="formData.companyName" placeholder="请输入" maxlength="100" show-word-limit clearable />
          </el-form-item>
        </div>

        <div class="form-row form-row-4">
          <el-form-item label="公司官网" prop="website">
            <el-input v-model="formData.website" placeholder="请输入" maxlength="100" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="公司地址" prop="companyAddress">
            <el-input
              v-model="formData.companyAddress"
              placeholder="请输入"
              maxlength="100"
              show-word-limit
              clearable
            />
          </el-form-item>
          <el-form-item label="管家婆客户名称" prop="guanjiapoName">
            <el-input v-model="formData.guanjiapoName" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="管家婆客户编码" prop="guanjiapoCode">
            <el-input v-model="formData.guanjiapoCode" placeholder="请输入" clearable />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="3"
              maxlength="100"
              show-word-limit
              placeholder="请输入"
              clearable
            />
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 标签 -->
    <div class="form-content label-content">
      <div class="section-title">标签</div>
      <div class="tags-wrapper">
        <el-tag
          v-for="tag in formData.tags"
          :key="tag.id"
          closable
          size="large"
          @close="handleTagRemove(tag.id)"
          style="margin-right: 10px"
        >
          {{ tag.value }}
        </el-tag>
        <el-input
          v-if="tagInputVisible"
          ref="tagInputRef"
          placeholder="请输入"
          v-model="tagInputValue"
          style="width: 150px"
          maxlength="10"
          show-word-limit
          @blur="handleTagConfirm"
          @keyup.enter="handleTagConfirm"
        />
        <el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
      </div>
    </div>

    <div class="cards-row">
      <!-- 收货地址卡片 -->
      <div class="card-item">
        <div class="card-header">
          <div class="section-title">收货地址</div>
          <el-button type="primary" size="small" @click="handleAddAddress">新增</el-button>
        </div>
        <el-table :data="formData.addressList" border style="width: 100%">
          <el-table-column label="收货人" align="center">
            <template #default="{ row }">
              {{ row.consignee }}
            </template>
          </el-table-column>
          <el-table-column label="联系方式" align="center">
            <template #default="{ row }">
              {{ row.phone }}
            </template>
          </el-table-column>
          <el-table-column label="收货地址" align="center" width="200">
            <template #default="{ row }">
              {{ [row.countryRegion, row.province, row.city, row.county].filter(v => v && v !== '0').join('/') }}
            </template>
          </el-table-column>
          <el-table-column label="详细地址" align="center">
            <template #default="{ row }">
              {{ row.detail }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="handleAddressDetail(row)">详情</el-button>
              <el-button size="small" type="primary" link @click="handleAddressEdit(row.id)">编辑</el-button>
              <el-button size="small" type="danger" link @click="handleAddressDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 联系人卡片 -->
      <div class="card-item">
        <div class="card-header">
          <div class="section-title">联系人</div>
          <el-button type="primary" size="small" @click="handleAddContact">新增</el-button>
        </div>
        <el-table :data="formData.contactPersonList" border style="width: 100%">
          <el-table-column label="联系人" align="center" prop="name" />
          <el-table-column label="邮箱" align="center" prop="email" />
          <el-table-column label="职位" align="center" prop="position" />
          <el-table-column label="操作" align="center" width="180">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="handleContactDetail(row)">详情</el-button>
              <el-button size="small" type="primary" link @click="handleContactEdit(row.id)">编辑</el-button>
              <el-button size="small" type="danger" link @click="handleContactDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <footer-actions>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认</el-button>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup>
import FooterActions from '@/components/footer-actions/index.vue'
import LabelSelect from '@/components/label-select/index.vue'
import AddressDialog from './components/address-dialog.vue'
import AddressDetailDialog from './components/address-detail-dialog.vue'
import ContactDialog from './components/contact-dialog.vue'
import ContactDetailDialog from './components/contact-detail-dialog.vue'
import { ref, reactive, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import {
  saveCustomer,
  getCustomerDetail,
  getDictionaryByCode,
  saveDictionary,
  validateContact
} from '@/api/sed/sales/customer'
import { getAllEmployee } from '@/api/sed/auth/org'
import { getCountries } from '@/api/sed/system/region'
import { manualLevelList } from '@/constant/sed/customer'
import { useUserStore } from '@/views/sed/store/modules/user'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const formRef = ref()
const isEdit = ref(false)
const tagInputRef = ref()
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const employeeList = ref<any[]>([])
const countryRegionOptions = ref<any[]>([])
const customerTypeList = ref<any[]>([])
const customerSourceList = ref<any[]>([])

// 生成临时ID
const generateRandomId = () => {
  return 'temp_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now().toString(36)
}

const loadAllEmployeeList = async () => {
  const { code, data, message } = await getAllEmployee({})
  if (code !== 200) return ElMessage.warning(message)
  employeeList.value = data || []
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

const formData = reactive({
  id: 0,
  code: '',
  customerName: '',
  salesman: '',
  follower: '',
  customerType: '',
  customerSource: '',
  manualLevel: '',
  guanjiapoName: '',
  guanjiapoCode: '',
  regionId: '',
  companyName: '',
  website: '',
  companyAddress: '',
  remark: '',
  tags: [] as Array<{ id: number | string; value: string; isNew?: boolean }>,
  addressList: [] as any[],
  contactPersonList: [] as any[]
})

;(window as any).formData = formData

const rules = {
  customerName: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { max: 20, message: '最多20个字', trigger: 'blur' }
  ],
  salesman: [{ required: true, message: '请选择业务员', trigger: 'change' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  regionId: [{ required: true, message: '请选择国家地区', trigger: 'change' }],
  companyName: [{ max: 100, message: '最多100个字', trigger: 'blur' }],
  website: [{ max: 100, message: '最多100个字', trigger: 'blur' }],
  companyAddress: [{ max: 100, message: '最多100个字', trigger: 'blur' }],
  remark: [{ max: 100, message: '最多100个字', trigger: 'blur' }]
}

const loadCustomerData = async () => {
  const id = route.query.id
  if (!id) return

  const { code, data, message } = await getCustomerDetail({ id })
  if (code !== 200) return ElMessage.warning(message)

  const customer = data.customer || {}
  formData.id = customer.id || 0
  formData.code = customer.code || ''
  formData.customerName = customer.name || ''
  formData.salesman = customer.belongEmployeeId || ''
  formData.follower = customer.followEmployeeId || ''
  formData.customerType = customer.type || ''
  formData.customerSource = customer.source || ''
  formData.manualLevel = customer.handLevel || ''
  formData.guanjiapoName = customer.guanjiapoName || ''
  formData.guanjiapoCode = customer.guanjiapoCode || ''
  formData.regionId = customer.countryRegion || ''
  formData.companyName = customer.companyName || ''
  formData.website = customer.companyWebsite || ''
  formData.companyAddress = customer.companyAddress || ''
  formData.remark = customer.remark || ''

  formData.addressList = (data.addressList || []).map((addr: any) => ({
    id: addr.id,
    customerId: addr.customerId,
    consignee: addr.consignee,
    phone: addr.phone,
    countryRegion: addr.countryRegion,
    countryRegionId: addr.countryRegionId,
    regionId: addr.regionId,
    province: addr.province,
    provinceId: addr.provinceId,
    city: addr.city,
    cityId: addr.cityId,
    county: addr.county,
    countyId: addr.countyId,
    detail: addr.detail,
    isNew: false
  }))

  formData.contactPersonList = (data.contactPersonList || []).map((contact: any) => ({
    id: contact.id,
    customerId: contact.customerId,
    name: contact.name,
    email: contact.email,
    position: contact.position,
    birthday: contact.birthday,
    gender: contact.gender,
    remark: contact.remark,
    fileList: contact.fileList || [],
    socialList: contact.socialList || [],
    phoneList: contact.phoneList || [],
    isNew: false
  }))

  formData.tags = (data.labelList || []).map((label: any) => ({
    id: label.id,
    value: label.value,
    isNew: false
  }))
}

const handleTagRemove = (tagId: number | string) => {
  const index = formData.tags.findIndex(tag => tag.id === tagId)
  if (index !== -1) {
    formData.tags.splice(index, 1)
  }
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagConfirm = () => {
  const value = tagInputValue.value.trim()
  if (!value) {
    tagInputVisible.value = false
    tagInputValue.value = ''
    return
  }

  if (value.length > 10) {
    ElMessage.warning('标签最多10个字')
    return
  }

  if (!formData.tags.some(tag => tag.value === value)) {
    formData.tags.push({
      id: generateRandomId(),
      value,
      isNew: true
    })
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

const handleAddAddress = () => {
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      callback: (result: any) => {
        formData.addressList.push({
          ...result,
          id: generateRandomId(),
          isNew: true
        })
      }
    },
    render: AddressDialog
  }
  dynamic.show(params)
}

const handleAddressEdit = (addressId: number | string) => {
  const index = formData.addressList.findIndex(addr => addr.id === addressId)
  if (index === -1) return

  const row = formData.addressList[index]
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      ...row,
      callback: (result: any) => {
        formData.addressList[index] = {
          ...result,
          id: row.id,
          isNew: row.isNew
        }
      }
    },
    render: AddressDialog
  }
  dynamic.show(params)
}

const handleAddressDetail = (row: any) => {
  const params = {
    id: 'addressDetailDialog',
    el: '#app',
    data: row,
    render: AddressDetailDialog
  }
  dynamic.show(params)
}

const handleAddressDelete = (addressId: number | string) => {
  const index = formData.addressList.findIndex(addr => addr.id === addressId)
  if (index !== -1) {
    formData.addressList.splice(index, 1)
  }
}

const handleAddContact = () => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      callback: (result: any) => {
        formData.contactPersonList.push({
          ...result,
          id: generateRandomId(),
          isNew: true
        })
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleContactEdit = (contactId: number | string) => {
  const index = formData.contactPersonList.findIndex(contact => contact.id === contactId)
  if (index === -1) return

  const row = formData.contactPersonList[index]
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      ...row,
      callback: (result: any) => {
        formData.contactPersonList[index] = {
          ...result,
          id: row.id,
          isNew: row.isNew
        }
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleContactDetail = (row: any) => {
  const params = {
    id: 'contactDetailDialog',
    el: '#app',
    data: row,
    render: ContactDetailDialog
  }
  dynamic.show(params)
}

const handleContactDelete = (contactId: number | string) => {
  const index = formData.contactPersonList.findIndex(contact => contact.id === contactId)
  if (index !== -1) {
    formData.contactPersonList.splice(index, 1)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const submitData = {
    id: isEdit.value ? formData.id : undefined,
    code: formData.code || '',
    name: formData.customerName,
    belongEmployeeId: formData.salesman || '',
    followEmployeeId: formData.follower || '',
    type: formData.customerType || '',
    customerSource: formData.customerSource || '',
    handLevel: formData.manualLevel,
    guanjiapoName: formData.guanjiapoName || '',
    guanjiapoCode: formData.guanjiapoCode || '',
    countryRegion: formData.regionId || '',
    countryRegionName: '',
    companyName: formData.companyName,
    companyWebsite: formData.website,
    companyAddress: formData.companyAddress,
    remark: formData.remark,
    customerAddressList: formData.addressList.map((addr: any) => ({
      id: addr.isNew ? undefined : addr.id,
      consignee: addr.consignee,
      phone: addr.phone,
      countryRegion: addr.countryRegion || '',
      countryRegionId: addr.countryRegionId || '',
      regionId: addr.regionId || undefined,
      province: addr.province || '',
      provinceId: addr.provinceId || '',
      city: addr.city || '',
      cityId: addr.cityId || '',
      county: addr.county || '',
      countyId: addr.countyId || '',
      detail: addr.detail || ''
    })),
    contactPersonList: formData.contactPersonList.map((contact: any) => ({
      id: contact.isNew ? undefined : contact.id,
      name: contact.name,
      email: contact.email,
      position: contact.position,
      birthday: contact.birthday || '',
      gender: contact.gender || '',
      remark: contact.remark || '',
      fileList: contact.fileList || [],
      socialList: contact.socialList || [],
      phoneList: contact.phoneList || []
    })),
    labelList: formData.tags.map(tag => ({
      id: tag.isNew ? undefined : tag.id,
      value: tag.value,
      type: 'customer'
    }))
  }

  const { code: validateCode, data: validateData, message: validateMessage } = await validateContact(submitData)
  if (validateCode !== 200) {
    ElMessage.warning(validateMessage)
    return
  }

  if (validateData && validateData.agreeSubmit == 'warn') {
    await ElMessageBox.confirm(validateData.message, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  }
  if (validateData && validateData.agreeSubmit == 'danger') {
    return ElMessage.warning(validateData.message)
  }

  // return console.log('submitData', submitData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await saveCustomer(submitData)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
  router.push('/sales/customer')
}

const handleCancel = () => {
  router.push('/sales/customer')
}

onMounted(() => {
  loadAllEmployeeList()
  loadCountryRegions()
  loadCustomerTypeList()
  loadCustomerSourceList()
  isEdit.value = !!route.query.id
  if (isEdit.value) {
    loadCustomerData()
  } else {
    formData.salesman = userStore.userId
  }
})
</script>

<style scoped lang="scss">
.customer-add-container {
  background: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 80px;

  .page-header {
    background: #fff;
    padding: 15px 15px 0;
    border-radius: 4px;

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }

  .form-content {
    background: #fff;
    padding: 15px;
    border-radius: 4px;
    &.label-content {
      margin-top: 10px;
      .section-title {
        margin-top: 0;
      }
    }

    .section-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      padding-left: 10px;
      border-left: 3px solid #409eff;
      margin: 30px 0 20px 0;
      display: flex;
      align-items: center;
    }

    .form-row {
      display: grid;
      gap: 20px;
      margin-bottom: 20px;

      &.form-row-4 {
        grid-template-columns: repeat(4, 1fr);
      }
    }

    .tags-wrapper {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
    }
  }

  .cards-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    margin-top: 10px;
    margin-bottom: 20px;

    .card-item {
      background: #fff;
      padding: 20px;
      border-radius: 4px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .section-title {
          margin: 0;
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          padding-left: 10px;
          border-left: 3px solid #409eff;
        }
      }
    }
  }
}
</style>
