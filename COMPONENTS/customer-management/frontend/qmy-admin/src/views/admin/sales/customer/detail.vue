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
          <el-icon class="edit-icon" v-permission="'sal:yt:customer:update'" @click="handleEdit">
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
            <div class="value">{{ getCustomerTypeLabel(customerData.customerType) }}</div>
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
            <div class="label">订单默认备注</div>
            <div class="value">{{ customerData.orderDefaultRemark || '-' }}</div>
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
              <el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-right-card">
        <el-tabs v-model="activeTab" class="detail-tabs" @tab-click="handleDetailTabsViewChange">
          <el-tab-pane label="基础信息" name="basic" />
          <el-tab-pane label="独立仓" name="warehouse" />
          <el-tab-pane label="产品信息对照表" name="product" />
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
                    <el-button type="primary" size="small" @click="handleAddAddress">新增</el-button>
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
                      <el-button size="small" type="primary" link @click="handleAddressDetail(row)">详情</el-button>
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
                    <el-button type="primary" size="small" @click="handleAddContact">新增</el-button>
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
                      <el-button size="small" type="primary" link @click="handleContactDetail(row)">详情</el-button>
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
        <div v-if="activeTab === 'warehouse'" class="tab-content-wrapper">
          <div class="warehouse-tab-content">
            <div v-if="customerData.storeStatus !== 3" class="warehouse-content">
              <el-steps
                :active="customerData.storeStatus === 2 ? 1 : customerData.storeStatus + 1"
                align-center
                class="warehouse-steps"
              >
                <el-step title="启用独立仓" />
                <el-step title="复核" />
                <el-step title="开始使用" />
              </el-steps>

              <div class="warehouse-icon">
                <img src="/images/sales/warehouse-icon.jpg" alt="独立仓" style="width: 200px; height: 200px" />
              </div>

              <div v-if="customerData.storeStatus === 0 || customerData.storeStatus === 2" class="warehouse-action">
                <el-button
                  type="primary"
                  size="large"
                  v-permission="'sal:yt:customer:enableStore'"
                  @click="handleEnableWarehouse"
                >
                  启用独立仓
                </el-button>
                <div v-if="customerData.storeStatus === 2" class="reject-info">
                  上次驳回账号：{{ rejectInfo.account }} 驳回时间：{{ rejectInfo.time }}
                </div>
              </div>

              <div v-else-if="customerData.storeStatus === 1" class="warehouse-pending">
                <template v-if="!showAdminActions">
                  <div class="pending-text">等待管理员复核</div>
                </template>

                <template v-else>
                  <div class="admin-actions">
                    <el-button
                      type="primary"
                      size="large"
                      class="confirm-btn"
                      v-permission="'sal:yt:customer:auditStore'"
                      @click="handleConfirmEnable"
                    >
                      确认开启
                    </el-button>
                    <el-button
                      type="danger"
                      size="large"
                      style="margin-left: 0"
                      class="reject-btn"
                      v-permission="'sal:yt:customer:auditStore'"
                      @click="handleReject"
                    >
                      驳回
                    </el-button>
                  </div>
                </template>
              </div>
            </div>

            <div v-else class="warehouse-enabled-content">
              <div class="warehouse-left-category">
                <div class="category-header">
                  <el-input
                    v-model="warehouseCategorySearch"
                    placeholder="请输入"
                    clearable
                    @keyup.enter="handleSearchWarehouseCategory"
                  >
                    <template #suffix>
                      <el-icon style="cursor: pointer" @click="handleSearchWarehouseCategory">
                        <Search />
                      </el-icon>
                    </template>
                  </el-input>
                </div>
                <div class="category-list">
                  <div
                    v-for="item in warehouseCategoryList"
                    :key="item.id"
                    :class="['category-item', { active: selectedWarehouseCategory === item.id }]"
                    @click="selectWarehouseCategory(item.id)"
                  >
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>

              <div class="warehouse-right-content">
                <el-tabs v-model="warehouseViewMode" class="warehouse-tabs" @tab-click="handleWarehouseViewChange">
                  <el-tab-pane label="全部" name="all" />
                  <el-tab-pane label="启用独立仓" name="enabled" />
                </el-tabs>

                <bz-table
                  ref="warehouseTableRef"
                  :fixedPagination="true"
                  :searchColumns="warehouseSearchColumns"
                  :columns="warehouseColumns"
                  :requestApi="getWarehouseProductList"
                  :dataCallback="warehouseDataCallback"
                  :initFetch="false"
                  :initParam="warehouseInitParam"
                >
                  <template #tableHeader>
                    <el-button type="primary" size="small" @click="handleWarningRule">预警规则</el-button>
                  </template>
                  <template #productId="{ row }">
                    {{ row.productId }}
                  </template>
                  <template #images="{ row }">
                    <div class="image-cell">
                      <el-image
                        v-if="row.productImage && row.productImage.length"
                        :src="row.productImage[0].url"
                        :preview-src-list="row.productImage.map((item: any) => item.url)"
                        :preview-teleported="true"
                        hide-on-click-modal
                        fit="cover"
                        style="width: 60px; height: 60px"
                      />
                      <div v-else>-</div>
                    </div>
                  </template>
                  <template #categoryName="{ row }">
                    {{ row.categoryName }}
                  </template>
                  <template #status="{ row }">
                    <span>{{ getStatusLabel(row.status) || '-' }}</span>
                  </template>
                  <template #operation="{ row }">
                    <el-button type="primary" link size="small" @click="handleWarehouseProductDetail(row)">
                      详情
                    </el-button>
                  </template>
                </bz-table>
              </div>
            </div>
          </div>
        </div>
        <div v-if="activeTab === 'product'" class="tab-content-wrapper">
          <div class="product-info-content">
            <bz-table
              ref="productTableRef"
              :fixedPagination="true"
              :searchColumns="productSearchColumns"
              :columns="productColumns"
              :requestApi="getCustomerProductList"
              :dataCallback="productDataCallback"
              :span-method="productSpanMethod"
            >
              <template #tableHeader>
                <el-button type="primary" @click="handleCustomerSpecMapping">客户规格映射</el-button>
              </template>
              <template #image="{ row }">
                <el-image
                  v-if="row.image"
                  :src="row.image"
                  style="width: 50px; height: 50px"
                  fit="cover"
                  v-image-preview="row.image"
                />
                <span v-else>-</span>
              </template>
              <template #customerSpecification="{ row }">
                {{ row.customerSpecification || '-' }}
              </template>
              <template #itemNumber="{ row }">
                {{ row.itemNumber || '-' }}
              </template>
              <template #operation="{ row }">
                <el-button size="small" type="primary" link @click="handleProductEdit(row)">编辑</el-button>
              </template>
            </bz-table>
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
import { Edit, Search, View, Hide } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import { getCustomerTypeLabel } from '@/constant/yitang/customer'
import { desensitize } from '@/utils'
import {
  getCustomerDetail,
  deleteLabel,
  addLabel,
  enableStore,
  auditStore,
  deleteAddress,
  deleteContactPerson,
  deleteFollow,
  getConsumptionTrends,
  getConsumptionRatio
} from '@/api/admin/sales/customer'
import { getCustomerSpecification } from '@/api/admin/sales/customer-spec'
import { getCategoryList, getProductList } from '@/api/admin/product'
import { fetchUserInfo } from '@/api/admin/auth/user'
import { statusList, getStatusLabel } from '@/constant/yitang/product'
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
import CustomerSpecMapping from './components/customer-spec-mapping.vue'
import ProductEditDialog from './components/product-edit-dialog.vue'
import WarningRuleDialog from './components/warning-rule-dialog.vue'
import WarehouseProductDetail from './components/warehouse-product-detail.vue'
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
const productTableRef = ref()
const showAdminActions = ref(false) // 是否显示管理员操作按钮
const hasTrendData = ref(false)
const hasRatioData = ref(false)
const showCustomerName = ref(true)
const showAddressInfo = ref(true)
const showContactInfo = ref(true)
const rejectInfo = reactive({
  account: '',
  time: ''
})

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

// 独立仓产品管理
const warehouseCategorySearch = ref('')
const selectedWarehouseCategory = ref('')
const warehouseCategoryList = ref<any[]>([])
const warehouseTableRef = ref()
const warehouseViewMode = ref('all')
const warehouseInitParam = reactive<{ categoryId: string; customerId?: string }>({
  categoryId: '',
  customerId: undefined
})

const warehouseSearchColumns = [
  {
    label: '产品ID',
    prop: 'code',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '状态',
    prop: 'status',
    enum: statusList,
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  }
]

const warehouseColumns: ColumnProps[] = [
  {
    label: '产品ID',
    prop: 'code',
    align: 'center'
  },
  {
    label: '图片',
    prop: 'images',
    align: 'center',
    showOverflowTooltip: false,
    width: 100
  },
  {
    label: '产品分类',
    prop: 'categoryName',
    align: 'center'
  },
  {
    label: '状态',
    prop: 'status',
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    align: 'center',
    width: 100
  }
]

// 产品信息对照表搜索列配置
const productSearchColumns = [
  {
    label: '产品ID',
    prop: 'productCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '规格名称',
    prop: 'specification',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '客户规格名称',
    prop: 'customerSpecification',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '客户货号',
    prop: 'itemNumber',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
]

// 产品信息对照表列配置
const productColumns: ColumnProps[] = [
  {
    label: '产品ID',
    prop: 'productCode',
    align: 'center'
  },
  {
    label: '规格名称',
    prop: 'specification',
    align: 'center'
  },
  {
    label: '图片',
    prop: 'image',
    align: 'center',
    showOverflowTooltip: false,
    width: 100
  },
  {
    label: '客户规格名称',
    prop: 'customerSpecification',
    align: 'center'
  },
  {
    label: '客户货号',
    prop: 'itemNumber',
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    align: 'center',
    width: 100
  }
]

const customerData = reactive({
  customerId: '',
  customerCode: '',
  customerName: '',
  belongEmployeeName: '',
  belongEmployeeId: '',
  followEmployeeName: '',
  followEmployeeId: '',
  manualLevel: '',
  customerType: '',
  region: '',
  countryRegion: '',
  yearOrderAmount: 0,
  autoCustomerLevel: '',
  website: '',
  address: '',
  remark: '',
  orderDefaultRemark: '',
  createTime: '',
  lastFollowTime: '',
  lastOrderTime: '',
  avatar: '',
  tags: [] as Array<{ id: number | string; value: string }>,
  addressList: [] as any[],
  contactPersonList: [] as any[],
  followList: [] as any[],
  storeStatus: 0
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
  customerData.manualLevel = customer.handLevel || ''
  customerData.region = customer.countryRegionName || ''
  customerData.countryRegion = customer.countryRegion || ''
  customerData.website = customer.companyWebsite || ''
  customerData.address = customer.companyAddress || ''
  customerData.remark = customer.remark || ''
  customerData.orderDefaultRemark = customer.orderDefaultRemark ?? ''
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
  // customer.storeStatus = 3
  customerData.storeStatus = customer.storeStatus ?? 0

  // 设置驳回信息
  if (customer.storeStatus == 2) {
    rejectInfo.account = customer.storeOperationUserName || ''
    rejectInfo.time = customer.storeOperationTime || ''
  } else {
    rejectInfo.account = ''
    rejectInfo.time = ''
  }

  checkSuperAdmin()

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

  const { code, message } = await deleteLabel({ labelId, customerId: customerData.customerId })
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
      handLevel: customerData.manualLevel,
      countryRegion: customerData.countryRegion,
      companyWebsite: customerData.website,
      companyAddress: customerData.address,
      remark: customerData.remark,
      orderDefaultRemark: customerData.orderDefaultRemark,
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

  const { code, message } = await deleteAddress({ addressId: row.id, customerId: customerData.customerId })
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

  const { code, message } = await deleteContactPerson({ contactId: row.id, customerId: customerData.customerId })
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

  const { code, message } = await deleteFollow({ followId: row.id, customerId: customerData.customerId })
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

const getCustomerProductList = async (params: any) => {
  return await getCustomerSpecification({
    ...params,
    customerId: customerData.customerId
  })
}

const productDataCallback = (data: any) => {
  const list: any[] = []
  const productGroups = new Map()

  data?.list?.forEach((product: any) => {
    if (product.specifications && Array.isArray(product.specifications)) {
      const productKey = product.code
      if (!productGroups.has(productKey)) {
        productGroups.set(productKey, {
          productCode: product.code,
          productId: product.id,
          specifications: []
        })
      }

      product.specifications.forEach((spec: any) => {
        const firstImage = spec.specificationImages?.[0]?.url || ''
        const firstSpecItem = spec.specificationItemList?.[0] || {}
        productGroups.get(productKey).specifications.push({
          id: spec.id,
          specification: spec.name,
          image: firstImage,
          customerSpecification: firstSpecItem.customerSpecification || '',
          itemNumber: spec.itemNumber || '',
          _rawProduct: product,
          _rawSpec: spec
        })
      })
    }
  })

  productGroups.forEach(group => {
    group.specifications.forEach((spec: any, index: number) => {
      list.push({
        ...spec,
        productId: group.productId,
        productCode: group.productCode,
        isFirstRow: index === 0,
        groupSize: group.specifications.length,
        rowIndex: index
      })
    })
  })

  return {
    list,
    total: Number(data?.total || 0)
  }
}

// 单元格合并方法
const productSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
    if (row.isFirstRow) {
      return {
        rowspan: row.groupSize,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
  return {
    rowspan: 1,
    colspan: 1
  }
}

const handleCustomerSpecMapping = () => {
  const params = {
    id: 'customerSpecMapping',
    el: '#app',
    data: {
      customerId: customerData.customerId,
      callback: () => {}
    },
    render: CustomerSpecMapping
  }
  dynamic.show(params)
}

const handleProductEdit = (row: any) => {
  const params = {
    id: 'productEditDialog',
    el: '#app',
    data: {
      row,
      customerId: customerData.customerId,
      callback: () => {
        productTableRef.value?.getTableList()
      }
    },
    render: ProductEditDialog
  }
  dynamic.show(params)
}

const handleEnableWarehouse = async () => {
  const { code, message } = await enableStore({ customerId: customerData.customerId })
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('已提交启用申请，等待管理员复核')
  customerData.storeStatus = 1
  rejectInfo.account = ''
  rejectInfo.time = ''
  checkSuperAdmin()
}

const handleConfirmEnable = async () => {
  const { code, message } = await auditStore({
    customerId: customerData.customerId,
    auditResult: 3 // 3表示通过
  })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  await loadCustomerData()
}

const handleReject = async () => {
  const { code, message } = await auditStore({
    customerId: customerData.customerId,
    auditResult: 2 // 2表示驳回
  })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  await loadCustomerData()
}

watch(
  () => selectedWarehouseCategory.value,
  newVal => {
    warehouseInitParam.categoryId = newVal
  }
)

const selectWarehouseCategory = (id: string) => {
  selectedWarehouseCategory.value = id
  warehouseInitParam.categoryId = id
  warehouseTableRef.value?.handleReset()
}

const handleSearchWarehouseCategory = () => {
  loadWarehouseCategoryList()
}

const loadWarehouseCategoryList = async () => {
  return new Promise(async resolve => {
    const params = { name: warehouseCategorySearch.value, customerId: customerData.customerId }
    const { code, data, message } = await getCategoryList(params)
    if (code !== 200) return ElMessage.warning(message)
    warehouseCategoryList.value = data ?? []
    const isFirstLoad = !selectedWarehouseCategory.value
    if (warehouseCategoryList.value.length > 0 && isFirstLoad) {
      selectedWarehouseCategory.value = warehouseCategoryList.value[0].id
      warehouseInitParam.categoryId = warehouseCategoryList.value[0].id
      await nextTick()
      warehouseTableRef.value?.getTableList()
    }
    resolve(true)
  })
}

const getWarehouseProductList = async (params: any) => {
  const hasSearch = ['code'].some(key => params[key] !== undefined && params[key] !== null && params[key] !== '')
  if (hasSearch) {
    const rest = Object.fromEntries(Object.entries(params).filter(([k]) => k !== 'categoryId'))
    return await getProductList({ ...rest, customerId: warehouseInitParam.customerId })
  }
  return await getProductList({ ...params, ...warehouseInitParam })
}

const warehouseDataCallback = (data: any) => {
  const records = data?.records ?? data?.list ?? []
  // 根据搜索结果的分类自动选中左侧分类
  const searchParams = warehouseTableRef.value?.searchParams || {}
  const ignore = ['categoryId', 'pageNum', 'pageSize']
  const hasSearch = Object.entries(searchParams).some(([k, v]) => {
    return !ignore.includes(k) && v !== undefined && v !== null && v !== ''
  })
  if (hasSearch && records.length > 0) {
    const categoryIds = [...new Set(records.map((item: any) => item.categoryId).filter(Boolean))]
    selectedWarehouseCategory.value = categoryIds.length === 1 ? String(categoryIds[0]) : ''
  }
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const handleWarningRule = () => {
  const params = {
    id: 'warningRuleDialog',
    el: '#app',
    data: {
      customerId: customerData.customerId
    },
    render: WarningRuleDialog
  }
  dynamic.show(params)
}

const handleWarehouseProductDetail = (row: any) => {
  const params = {
    id: 'warehouseProductDetail',
    el: '#app',
    data: {
      rowData: {
        ...row,
        customerId: customerData.customerId,
        warehouseViewMode: warehouseViewMode.value
      }
    },
    render: WarehouseProductDetail
  }
  dynamic.show(params)
}

const handleWarehouseViewChange = (tab: any) => {
  const tabName = tab.paneName || tab.props?.name
  if (tabName === 'enabled') {
    warehouseInitParam.customerId = customerData.customerId
  } else {
    warehouseInitParam.customerId = undefined
  }
  warehouseTableRef.value?.getTableList()
}

const handleDetailTabsViewChange = async (tab: any) => {
  const tabName = tab.paneName || tab.props?.name
  console.log('handleDetailTabsViewChange', tabName)

  selectedWarehouseCategory.value = ''
  if (tabName === 'basic') {
    loadCustomerData()
  } else if (tabName === 'warehouse') {
    await loadWarehouseCategoryList()
    warehouseTableRef.value?.getTableList()
  }
}

// 检查是否为超级管理员
const checkSuperAdmin = async () => {
  const { data } = await fetchUserInfo()
  const sysRoleList = data?.user?.sysRoleList || []
  const isSuperAdmin = sysRoleList.some((role: any) => role.roleKey === 'superAdmin')

  // 只有当客户状态为1（等待复核）且当前用户是超管时，显示审核按钮
  if (isSuperAdmin && customerData.storeStatus === 1) {
    showAdminActions.value = true
  } else {
    showAdminActions.value = false
  }
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

  .product-info-content {
    padding: 0;
  }

  .warehouse-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 500px;
    padding: 40px 20px;

    .warehouse-steps {
      width: 100%;
      max-width: 600px;
      margin-bottom: 60px;
    }

    .warehouse-icon {
      margin-bottom: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .warehouse-action {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 20px;

      .el-button {
        width: 200px;
      }

      .reject-info {
        font-size: 14px;
        color: #606266;
        text-align: center;
      }
    }

    .warehouse-pending {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 30px;

      .pending-text {
        font-size: 16px;
        color: #303133;
        font-weight: 500;
      }

      .refresh-btn {
        border: 1px dashed #d9d9d9;
        color: #606266;
        background-color: #fff;

        &:hover {
          border-color: #409eff;
          color: #409eff;
        }
      }

      .admin-actions {
        display: flex;
        flex-direction: column;
        gap: 16px;
        width: 280px;

        .confirm-btn,
        .reject-btn {
          width: 100%;
        }
      }
    }

    .warehouse-enabled {
      .el-button {
        width: 200px;
      }
    }
  }
  .warehouse-enabled-content {
    display: flex;
    width: 100%;
    height: calc(100vh - 250px);
    gap: 16px;

    .warehouse-left-category {
      width: 160px;
      background-color: #f5f7fa;
      border-radius: 4px;
      padding: 16px 12px;
      flex-shrink: 0;
      overflow-y: auto;

      .category-header {
        margin-bottom: 16px;

        :deep(.el-input__wrapper) {
          background-color: #fff;
        }
      }

      .category-list {
        .category-item {
          padding: 14px 12px;
          cursor: pointer;
          border-radius: 4px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          transition: all 0.3s;
          background-color: #fff;
          font-size: 14px;
          color: #606266;

          &:hover {
            background-color: #f0f2f5;
          }

          &.active {
            background-color: #ecf2fe;
            color: #1890ff;
            font-weight: 500;
          }

          span {
            font-size: 14px;
          }
        }
      }
    }

    .warehouse-right-content {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;

      .warehouse-tabs {
        margin-bottom: 16px;

        :deep(.el-tabs__header) {
          margin-bottom: 0;
        }
      }

      .warehouse-header-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;

        .header-left {
          display: flex;
          gap: 8px;
        }

        .header-right {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
}
</style>
