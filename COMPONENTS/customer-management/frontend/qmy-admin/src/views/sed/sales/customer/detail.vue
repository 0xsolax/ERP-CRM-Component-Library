<template>
  <div class="customer-detail-page">
    <div class="detail-content">
      <div class="detail-left-card">
        <div class="card-header">
          <div class="customer-info">
            <el-avatar :size="40" :src="customerData.avatar">
              {{ customerData.customerName?.charAt(0) }}
            </el-avatar>
            <span class="customer-name">{{ displayCustomerName }}</span>
            <el-icon class="eye-icon" @click="toggleCustomerName">
              <component :is="showCustomerName ? View : Hide" />
            </el-icon>
          </div>
          <el-icon class="edit-icon" @click="handleEdit">
            <Edit />
          </el-icon>
        </div>
        <div class="card-body">
          <div class="info-item">
            <div class="label">客户编号</div>
            <div class="value">{{ customerData.customerCode }}</div>
          </div>
          <div class="info-item">
            <div class="label">归属</div>
            <div class="value">{{ customerData.belongEmployeeName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">跟进人</div>
            <div class="value">{{ customerData.followEmployeeName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">客户类型</div>
            <div class="value">{{ customerData.customerTypeValue }}</div>
          </div>
          <div class="info-item">
            <div class="label">客户来源</div>
            <div class="value">{{ customerData.customerSourceValue }}</div>
          </div>
          <div class="info-item">
            <div class="label">手动层级</div>
            <div class="value">{{ customerData.manualLevel }}</div>
          </div>
          <div class="info-item">
            <div class="label">近一年累计金额</div>
            <div class="value">
              {{ `¥${customerData.yearOrderAmount}` }}
            </div>
          </div>
          <div class="info-item">
            <div class="label">自动层级</div>
            <div class="value">{{ customerData.autoCustomerLevel || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">国家地区</div>
            <div class="value">{{ customerData.region || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">公司名称</div>
            <div class="value">{{ customerData.companyName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">公司官网</div>
            <div class="value">
              {{ customerData.website || '-' }}
            </div>
          </div>
          <div class="info-item">
            <div class="label">公司地址</div>
            <div class="value">{{ customerData.address || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">备注</div>
            <div class="value">{{ customerData.remark || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">创建时间</div>
            <div class="value">{{ customerData.createTime }}</div>
          </div>
          <div class="info-item">
            <div class="label">最近跟进时间</div>
            <div class="value">{{ formatDate(customerData.lastFollowTime) }}</div>
          </div>
          <div class="info-item">
            <div class="label">最近下单时间</div>
            <div class="value">{{ customerData.lastOrderTime || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">管家婆客户名称</div>
            <div class="value">{{ customerData.guanjiapoName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">管家婆客户编码</div>
            <div class="value">{{ customerData.guanjiapoCode || '-' }}</div>
          </div>
          <div class="tag-section">
            <div class="label">标签</div>
            <div class="tag-list">
              <el-tag
                v-for="tag in customerData.tags"
                :key="tag.id"
                closable
                size="large"
                @close="handleTagRemove(tag.id)"
                style="margin-right: 8px"
              >
                {{ tag.value }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                placeholder="请输入"
                v-model="tagInputValue"
                style="width: 100px"
                @blur="handleTagConfirm"
                @keyup.enter="handleTagConfirm"
              />
              <el-button v-else size="small" @click="showTagInput" v-permission="'sal:yt:customer:addLabel'">
                + 添加标签
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-right-card">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="基础信息" name="basic" />
        </el-tabs>
        <div v-if="activeTab === 'basic'" class="tab-content-wrapper">
          <div class="tab-content">
            <div class="tab-left">
              <!-- 收货地址 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">收货地址</span>
                  <div class="header-actions">
                    <el-icon class="eye-icon" @click="toggleAddressInfo" style="margin-right: 8px">
                      <component :is="showAddressInfo ? View : Hide" />
                    </el-icon>
                    <el-button
                      type="primary"
                      size="small"
                      @click="handleAddAddress"
                      v-permission="'sal:yt:customer:updateAddress'"
                    >
                      新增
                    </el-button>
                    <el-link type="primary" @click="showAddressList" style="color: #999">更多></el-link>
                  </div>
                </div>
                <el-table :data="customerData.addressList" style="width: 100%">
                  <el-table-column label="收货人" align="center">
                    <template #default="{ row }">
                      {{ desensitizeConsignee(row.consignee) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="联系方式" align="center">
                    <template #default="{ row }">
                      {{ desensitizePhone(row.phone) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="收货地址" align="center" width="200">
                    <template #default="{ row }">
                      <template v-if="row.countryRegion === '中国'">
                        {{
                          desensitizeAddress(
                            [
                              row.countryRegion,
                              row.province,
                              row.city,
                              row.county !== '0' && row.county !== 0 ? row.county : '',
                              row.detail
                            ]
                              .filter(Boolean)
                              .join('/')
                          )
                        }}
                      </template>
                      <template v-else>
                        {{ desensitizeAddress([row.countryRegion, row.detail].filter(Boolean).join('/')) }}
                      </template>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="180">
                    <template #default="{ row }">
                      <el-button
                        size="small"
                        type="primary"
                        link
                        v-permission="'sal:yt:customer:addressList'"
                        @click="handleAddressDetail(row)"
                      >
                        详情
                      </el-button>
                      <el-button
                        size="small"
                        type="primary"
                        link
                        v-permission="'sal:yt:customer:updateAddress'"
                        @click="handleAddressEdit(row)"
                      >
                        编辑
                      </el-button>
                      <el-button size="small" type="danger" link @click="handleAddressDelete(row)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 联系人 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">联系人</span>
                  <div class="header-actions">
                    <el-icon class="eye-icon" @click="toggleContactInfo" style="margin-right: 8px">
                      <component :is="showContactInfo ? View : Hide" />
                    </el-icon>
                    <el-button
                      type="primary"
                      size="small"
                      v-permission="'sal:yt:customer:updateContactPerson'"
                      @click="handleAddContact"
                    >
                      新增
                    </el-button>
                    <el-link type="primary" @click="showContactList" style="color: #999">更多></el-link>
                  </div>
                </div>
                <el-table :data="customerData.contactPersonList" style="width: 100%">
                  <el-table-column label="联系人" align="center">
                    <template #default="{ row }">
                      {{ desensitizeContactName(row.name) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="邮箱" align="center">
                    <template #default="{ row }">
                      {{ desensitizeEmail(row.email) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="职位" align="center" prop="position" />
                  <el-table-column label="操作" align="center" width="180">
                    <template #default="{ row }">
                      <el-button
                        size="small"
                        type="primary"
                        link
                        v-permission="'sal:yt:customer:detail'"
                        @click="handleContactDetail(row)"
                      >
                        详情
                      </el-button>
                      <el-button
                        size="small"
                        type="primary"
                        link
                        v-permission="'sal:yt:customer:updateContactPerson'"
                        @click="handleContactEdit(row)"
                      >
                        编辑
                      </el-button>
                      <el-button size="small" type="danger" link @click="handleContactDelete(row)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 跟进记录 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">跟进记录</span>
                  <div class="header-actions">
                    <el-button type="primary" size="small" @click="handleAddFollowRecord">新增</el-button>
                    <el-link type="primary" @click="showFollowRecordList" style="color: #999">更多></el-link>
                  </div>
                </div>
                <el-table :data="customerData.followList" style="width: 100%">
                  <el-table-column label="主题" align="center" prop="theme" />
                  <el-table-column label="日期" align="center">
                    <template #default="{ row }">
                      {{ formatDate(row.createTime) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="下次回访日期" align="center">
                    <template #default="{ row }">
                      {{ formatDate(row.nextVisitDate) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="180">
                    <template #default="{ row }">
                      <el-button size="small" type="primary" link @click="handleFollowRecordDetail(row)">
                        详情
                      </el-button>
                      <el-button size="small" type="primary" link @click="handleFollowRecordEdit(row)">编辑</el-button>
                      <el-button size="small" type="danger" link @click="handleFollowRecordDelete(row)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>

            <div class="tab-right">
              <!-- 消费趋势图表 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">消费趋势</span>
                  <div style="width: 240px; flex-shrink: 0">
                    <el-date-picker
                      v-model="dateRange"
                      type="daterange"
                      range-separator="-"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      style="width: 100%"
                    />
                  </div>
                </div>
                <div class="chart-container">
                  <el-empty v-if="!hasTrendData" description="暂无数据" :image-size="100" />
                  <div v-else ref="trendChartRef" style="width: 100%; height: 300px" />
                </div>
              </div>

              <!-- 消费占比图表 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">消费占比</span>
                </div>
                <div class="chart-container">
                  <el-empty v-if="!hasRatioData" description="暂无数据" :image-size="100" />
                  <div v-else ref="ratioChartRef" style="width: 100%; height: 300px" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, View, Hide } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import {
  getCustomerDetail,
  deleteLabel,
  addLabel,
  deleteAddress,
  deleteContactPerson,
  deleteFollow,
  getConsumptionTrends,
  getConsumptionRatio
} from '@/api/sed/sales/customer'
import { desensitize } from '@/utils'
import * as echarts from 'echarts'
import AddressDialog from './components/address-dialog.vue'
import AddressDetailDialog from './components/address-detail-dialog.vue'
import AddressListDialog from './components/address-list-dialog.vue'
import ContactDialog from './components/contact-dialog.vue'
import ContactDetailDialog from './components/contact-detail-dialog.vue'
import ContactListDialog from './components/contact-list-dialog.vue'
import FollowRecordDialog from './components/follow-record-dialog.vue'
import FollowRecordDetailDialog from './components/follow-record-detail-dialog.vue'
import FollowRecordListDialog from './components/follow-record-list-dialog.vue'
import CustomerEditDialog from './components/customer-edit-dialog.vue'
import dayjs from 'dayjs'

const formatDate = (date: string) => (date ? dayjs(date).format('YYYY-MM-DD') : '-')

const route = useRoute()
const trendChartRef = ref()
const ratioChartRef = ref()
const tagInputRef = ref()
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const activeTab = ref('basic')
const dateRange = ref<[Date, Date]>()
const hasTrendData = ref(false)
const hasRatioData = ref(false)

const showCustomerName = ref(false)
const showAddressInfo = ref(false)
const showContactInfo = ref(false)

const toggleCustomerName = () => {
  showCustomerName.value = !showCustomerName.value
}

const toggleAddressInfo = () => {
  showAddressInfo.value = !showAddressInfo.value
}

const toggleContactInfo = () => {
  showContactInfo.value = !showContactInfo.value
}

// 脱敏手机号
const desensitizePhone = (phone: string) => {
  if (!phone) return phone
  return showAddressInfo.value ? phone : desensitize(phone, 3, 4)
}

// 脱敏邮箱
const desensitizeEmail = (email: string) => {
  if (!email) return email
  if (showContactInfo.value) return email
  const [name, domain] = email.split('@')
  if (!name || !domain) return email
  return desensitize(name, 1, 1) + '@' + domain
}

// 脱敏地址
const desensitizeAddress = (address: string) => {
  if (!address) return address
  return showAddressInfo.value ? address : desensitize(address, 1, 1)
}

// 脱敏收货人
const desensitizeConsignee = (name: string) => {
  if (!name) return name
  return showAddressInfo.value ? name : desensitize(name, 1, 1)
}

// 脱敏联系人
const desensitizeContactName = (name: string) => {
  if (!name) return name
  return showContactInfo.value ? name : desensitize(name, 1, 1)
}

const displayCustomerName = computed(() => {
  return showCustomerName.value ? customerData.customerName : desensitize(customerData.customerName, 1, 1)
})

const customerData = reactive({
  customerId: '',
  customerCode: '',
  customerName: '',
  belongEmployeeName: '',
  belongEmployeeId: '',
  followEmployeeName: '',
  followEmployeeId: '',
  manualLevel: '',
  guanjiapoName: '',
  guanjiapoCode: '',
  customerType: '',
  customerSource: '',
  customerTypeValue: '',
  customerSourceValue: '',
  region: '',
  countryRegion: '',
  yearOrderAmount: 0,
  autoCustomerLevel: '',
  companyName: '',
  website: '',
  address: '',
  remark: '',
  createTime: '',
  lastFollowTime: '',
  lastOrderTime: '',
  avatar: '',
  tags: [] as Array<{ id: number | string; value: string }>,
  addressList: [] as any[],
  contactPersonList: [] as any[],
  followList: [] as any[]
})

if (route.query.id) {
  customerData.customerId = route.query.id as string
}

const loadCustomerData = async () => {
  const { data } = await getCustomerDetail({ id: customerData.customerId })
  const customer = data.customer || {}
  customerData.customerCode = customer.code || ''
  customerData.customerName = customer.name || ''
  customerData.belongEmployeeName = customer.belongEmployeeName || ''
  customerData.belongEmployeeId = customer.belongEmployeeId || ''
  customerData.followEmployeeName = customer.followEmployeeName || ''
  customerData.followEmployeeId = customer.followEmployeeId || ''
  customerData.customerType = customer.type || ''
  customerData.customerSource = customer.customerSource || ''
  customerData.customerTypeValue = customer.typeValue || ''
  customerData.customerSourceValue = customer.customerSourceValue || ''
  customerData.manualLevel = customer.handLevel || ''
  customerData.guanjiapoName = customer.guanjiapoName || ''
  customerData.guanjiapoCode = customer.guanjiapoCode || ''
  customerData.region = customer.countryRegionName || ''
  customerData.countryRegion = customer.countryRegion || ''
  customerData.companyName = customer.companyName || ''
  customerData.website = customer.companyWebsite || ''
  customerData.address = customer.companyAddress || ''
  customerData.remark = customer.remark || ''
  customerData.yearOrderAmount = customer.yearOrderAmount || 0
  customerData.createTime = customer.createTime || ''
  customerData.lastFollowTime = customer.followTime || ''
  customerData.lastOrderTime = customer.lastOrderTime || ''
  customerData.autoCustomerLevel = customer.autoCustomerLevel || ''
  customerData.addressList = (data.addressList || []).map((addr: any) => ({
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
    detail: addr.detail
  }))

  customerData.contactPersonList = (data.contactPersonList || []).map((contact: any) => ({
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
    phoneList: contact.phoneList || []
  }))

  customerData.tags = (data.labelList || []).map((label: any) => ({
    id: label.id,
    value: label.value
  }))
  customerData.followList = data.followList || []

  nextTick(() => {
    loadConsumptionCharts()
  })
}

const initTrendChart = (xAxis: string[] = [], series: number[] = []) => {
  if (!trendChartRef.value) return

  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: series,
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.1)'
        }
      }
    ]
  }
  chart.setOption(option)
}

const initRatioChart = (xAxis: string[] = [], series: number[] = []) => {
  if (!ratioChartRef.value) return

  const chart = echarts.init(ratioChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: series,
        type: 'bar',
        itemStyle: {
          color: '#409eff'
        },
        barWidth: '40%'
      }
    ]
  }
  chart.setOption(option)
}

const loadConsumptionCharts = async () => {
  const params: any = { customerId: customerData.customerId }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dayjs(dateRange.value[0]).format('YYYY-MM-DD')
    params.endTime = dayjs(dateRange.value[1]).format('YYYY-MM-DD')
  }

  const [trendsRes, ratioRes] = await Promise.all([getConsumptionTrends(params), getConsumptionRatio(params)])

  // 消费趋势
  if (trendsRes.code === 200 && trendsRes.data?.series?.length) {
    hasTrendData.value = true
    nextTick(() => initTrendChart(trendsRes.data.xAxis || [], trendsRes.data.series || []))
  } else {
    hasTrendData.value = false
  }

  // 消费占比
  if (ratioRes.code === 200 && ratioRes.data?.series?.length) {
    hasRatioData.value = true
    nextTick(() => initRatioChart(ratioRes.data.xAxis || [], ratioRes.data.series || []))
  } else {
    hasRatioData.value = false
  }
}

watch(dateRange, () => {
  loadConsumptionCharts()
})

const handleTagRemove = async (labelId: number | string) => {
  await ElMessageBox.confirm('确认删除该标签吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteLabel({ labelId })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  await loadCustomerData()
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagConfirm = async () => {
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

  if (customerData.tags.some(tag => tag.value === value)) {
    ElMessage.warning('标签已存在')
    tagInputVisible.value = false
    tagInputValue.value = ''
    return
  }

  const { code, message } = await addLabel({
    masterId: customerData.customerId,
    value: value,
    type: 'customer'
  })

  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  ElMessage.success('添加成功')
  tagInputVisible.value = false
  tagInputValue.value = ''
  await loadCustomerData()
}

const handleEdit = () => {
  const params = {
    id: 'customerEditDialog',
    el: '#app',
    data: {
      id: customerData.customerId,
      code: customerData.customerCode,
      name: customerData.customerName,
      belongEmployeeId: customerData.belongEmployeeId,
      belongEmployeeName: customerData.belongEmployeeName,
      followEmployeeId: customerData.followEmployeeId,
      followEmployeeName: customerData.followEmployeeName,
      type: customerData.customerType,
      customerSource: customerData.customerSource,
      handLevel: customerData.manualLevel,
      guanjiapoName: customerData.guanjiapoName,
      guanjiapoCode: customerData.guanjiapoCode,
      countryRegion: customerData.countryRegion,
      companyName: customerData.companyName,
      companyWebsite: customerData.website,
      companyAddress: customerData.address,
      remark: customerData.remark,
      callback: async () => {
        await loadCustomerData()
      }
    },
    render: CustomerEditDialog
  }
  dynamic.show(params)
}

const handleAddAddress = () => {
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
      }
    },
    render: AddressDialog
  }
  dynamic.show(params)
}

const handleAddressEdit = (row: any) => {
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      ...row,
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
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

const handleAddressDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteAddress({ addressId: row.id })
  if (code !== 200) {
    return ElMessage.warning(message)
  }

  await loadCustomerData()
  ElMessage.success('删除成功')
}

const handleAddContact = () => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleContactEdit = (row: any) => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      ...row,
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
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

const handleContactDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该联系人吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteContactPerson({ contactId: row.id })
  if (code !== 200) return ElMessage.warning(message)
  await loadCustomerData()
  ElMessage.success('删除成功')
}

const showAddressList = () => {
  const params = {
    id: 'addressListDialog',
    el: '#app',
    data: {
      customerId: customerData.customerId,
      onUpdate: (updatedAddresses: any[]) => {
        customerData.addressList = updatedAddresses
      }
    },
    render: AddressListDialog
  }
  dynamic.show(params)
}

const showContactList = () => {
  const params = {
    id: 'contactListDialog',
    el: '#app',
    data: {
      customerId: customerData.customerId,
      onUpdate: (updatedContacts: any[]) => {
        customerData.contactPersonList = updatedContacts
      }
    },
    render: ContactListDialog
  }
  dynamic.show(params)
}

const handleAddFollowRecord = () => {
  const params = {
    id: 'followRecordDialog',
    el: '#app',
    data: {
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
      }
    },
    render: FollowRecordDialog
  }
  dynamic.show(params)
}

const handleFollowRecordEdit = (row: any) => {
  const params = {
    id: 'followRecordDialog',
    el: '#app',
    data: {
      ...row,
      fromType: 'customer-detail',
      customerId: customerData.customerId,
      callback: async () => {
        await loadCustomerData()
      }
    },
    render: FollowRecordDialog
  }
  dynamic.show(params)
}

const handleFollowRecordDetail = (row: any) => {
  const params = {
    id: 'followRecordDetailDialog',
    el: '#app',
    data: row,
    render: FollowRecordDetailDialog
  }
  dynamic.show(params)
}

const handleFollowRecordDelete = async (row: any) => {
  console.log('handleFollowRecordDelete', row)
  await ElMessageBox.confirm('确定要删除该跟进记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteFollow({ followId: row.id })
  if (code !== 200) return ElMessage.warning(message)
  await loadCustomerData()
  ElMessage.success('删除成功')
}

const showFollowRecordList = () => {
  const params = {
    id: 'followRecordListDialog',
    el: '#app',
    data: {
      customerId: customerData.customerId,
      onUpdate: (updatedRecords: any[]) => {
        customerData.followList = updatedRecords
      }
    },
    render: FollowRecordListDialog
  }
  dynamic.show(params)
}

onMounted(() => {
  loadCustomerData()
})
</script>

<style lang="scss" scoped>
.customer-detail-page {
  min-height: calc(100vh - 106px);

  .detail-content {
    display: flex;
    gap: 10px;
    min-height: calc(100vh - 106px);
  }

  .detail-left-card {
    width: 310px;
    flex-shrink: 0;
    background: #fff;
    border-radius: 8px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #ebeef5;

      .customer-info {
        display: flex;
        align-items: center;
        gap: 10px;
        flex: 1;

        .customer-name {
          font-size: 14px;
          font-weight: bold;
          color: #303133;
        }

        .eye-icon {
          font-size: 16px;
          color: #909399;
          cursor: pointer;
          flex-shrink: 0;

          &:hover {
            color: #409eff;
          }
        }
      }

      .edit-icon {
        font-size: 16px;
        color: #409eff;
        cursor: pointer;
        flex-shrink: 0;

        &:hover {
          color: #66b1ff;
        }
      }
    }

    .card-body {
      padding: 16px;

      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 18px;
        gap: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .label {
          font-size: 14px;
          color: #909399;
          flex-shrink: 0;
          min-width: 80px;
        }

        .value {
          font-size: 12px;
          color: #303133;
          text-align: right;
          word-break: break-all;
          flex: 1;

          a {
            color: #409eff;
            text-decoration: none;

            &:hover {
              text-decoration: underline;
            }
          }
        }
      }

      .tag-section {
        padding-top: 12px;
        border-top: 1px solid #ebeef5;

        .label {
          font-size: 14px;
          color: #909399;
          flex-shrink: 0;
          min-width: 80px;
        }

        .tag-list {
          margin-top: 10px;
        }
      }
    }
  }

  .detail-right-card {
    flex: 1;
    background: #fff;
    border-radius: 8px;
    padding: 10px 20px;

    .detail-tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 20px;
      }
    }
    .tab-content {
      display: flex;
      gap: 20px;

      .tab-left {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 20px;
      }

      .tab-right {
        width: 450px;
        flex-shrink: 0;
        display: flex;
        flex-direction: column;
        gap: 20px;
      }
    }
  }

  .section-card {
    :deep(.el-table) {
      margin-top: 12px;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 0;

      .section-title {
        font-size: 15px;
        font-weight: bold;
        color: #303133;
        position: relative;
        padding-left: 12px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 14px;
          background: #409eff;
          border-radius: 2px;
        }
      }

      .header-actions {
        display: flex;
        align-items: center;
        gap: 8px;

        .eye-icon {
          font-size: 16px;
          color: #909399;
          cursor: pointer;

          &:hover {
            color: #409eff;
          }
        }
      }
    }

    .chart-container {
      margin-top: 16px;
    }
  }
}
</style>
