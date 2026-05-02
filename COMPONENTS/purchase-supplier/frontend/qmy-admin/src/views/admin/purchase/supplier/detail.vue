<template>
  <div class="supplier-detail-page">
    <div class="detail-content">
      <div class="detail-left-card">
        <div class="card-header">
          <div class="supplier-info">
            <el-avatar :size="40">
              {{ supplierData.name?.charAt(0) }}
            </el-avatar>
            <span class="supplier-name">{{ supplierData.name }}</span>
          </div>
          <el-icon class="edit-icon" v-permission="'pur:yt:purchaseSupplier:update'" @click="handleEdit">
            <Edit />
          </el-icon>
        </div>
        <div class="card-body">
          <div class="info-item">
            <div class="label">供应商ID</div>
            <div class="value">{{ supplierData.code }}</div>
          </div>
          <div class="info-item">
            <div class="label">简称</div>
            <div class="value">{{ supplierData.shortName || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">公司地址</div>
            <div class="value">{{ supplierData.address || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">备注</div>
            <div class="value">{{ supplierData.remark || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">创建时间</div>
            <div class="value">{{ supplierData.createTime }}</div>
          </div>
          <div class="info-item">
            <div class="label">最近跟进时间</div>
            <div class="value">{{ supplierData.recentFollowTime || '-' }}</div>
          </div>
          <div class="info-item">
            <div class="label">最近下单时间</div>
            <div class="value">{{ supplierData.recentPurchaseTime || '-' }}</div>
          </div>
          <div class="tag-section">
            <div class="label">标签</div>
            <div class="tag-list">
              <el-tag
                v-for="tag in supplierData.tags"
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
          <el-tab-pane label="产品信息对照表" name="product" />
        </el-tabs>
        <div v-if="activeTab === 'basic'" class="tab-content-wrapper">
          <div class="tab-content">
            <div class="tab-left">
              <!-- 联系人 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">联系人</span>
                  <div class="header-actions">
                    <el-button type="primary" size="small" @click="handleAddContact">新增</el-button>
                    <el-link type="primary" @click="showContactList" style="color: #999">更多></el-link>
                  </div>
                </div>
                <el-table :data="supplierData.contactPersonList" style="width: 100%">
                  <el-table-column label="联系人" align="center" prop="name" />
                  <el-table-column label="邮箱" align="center" prop="email" />
                  <el-table-column label="职位" align="center" prop="position" />
                  <el-table-column label="操作" align="center" width="180">
                    <template #default="{ row }">
                      <el-button size="small" type="primary" link @click="handleContactDetail(row)">详情</el-button>
                      <el-button size="small" type="primary" link @click="handleContactEdit(row)">编辑</el-button>
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
                <el-table :data="supplierData.followList" style="width: 100%">
                  <el-table-column label="主题" align="center" prop="theme" />
                  <el-table-column label="日期" align="center" prop="createTimeStr" />
                  <el-table-column label="下次回访日期" align="center" prop="nextVisitDateStr" />
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
              <!-- 采购趋势图表 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">采购趋势</span>
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

              <!-- 采购占比图表 -->
              <div class="section-card">
                <div class="section-header">
                  <span class="section-title">采购占比</span>
                </div>
                <div class="chart-container">
                  <el-empty v-if="!hasRatioData" description="暂无数据" :image-size="100" />
                  <div v-else ref="ratioChartRef" style="width: 100%; height: 300px" />
                </div>
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
              :columns="supplierProductColumns"
              :requestApi="getSupplierProductList"
              :dataCallback="supplierProductDataCallback"
              :span-method="productSpanMethod"
              :max-height="700"
            >
              <template #image="{ row }">
                <el-image
                  v-if="row.image"
                  :src="row.image"
                  style="width: 50px; height: 50px; cursor: pointer"
                  fit="cover"
                  @click="previewImage([row.image])"
                />
                <span v-else>-</span>
              </template>
              <template #supplierSpecification="{ row }">
                {{ row.supplierSpecification || '-' }}
              </template>
              <template #supplierSpecificationCode="{ row }">
                {{ row.supplierSpecificationCode || '-' }}
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

  <el-image-viewer
    v-if="showViewer"
    :url-list="viewerImageList"
    :initial-index="0"
    hide-on-click-modal
    @close="showViewer = false"
  />
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import {
  getSupplierDetail,
  deleteSupplierLabel,
  addSupplierLabel,
  deleteSupplierFollow,
  deleteSupplierContact,
  getSupplierSpecification,
  getPurchaseTrends,
  getPurchaseRatio
} from '@/api/admin/purchase/supplier'
import ContactDialog from './components/contact-dialog.vue'
import ContactDetailDialog from './components/contact-detail-dialog.vue'
import ContactListDialog from './components/contact-list-dialog.vue'
import FollowRecordDialog from './components/follow-record-dialog.vue'
import FollowRecordDetailDialog from './components/follow-record-detail-dialog.vue'
import FollowRecordListDialog from './components/follow-record-list-dialog.vue'
import SupplierEditDialog from './components/supplier-edit-dialog.vue'
import ProductEditDialog from './components/product-edit-dialog.vue'

const route = useRoute()
const trendChartRef = ref()
const productTableRef = ref()
const ratioChartRef = ref()
const tagInputRef = ref()
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const activeTab = ref('basic')
const dateRange = ref<[Date, Date]>()
const hasTrendData = ref(false)
const hasRatioData = ref(false)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const supplierData = reactive({
  id: '',
  name: '',
  code: '',
  shortName: '',
  address: '',
  remark: '',
  createTime: '',
  recentFollowTime: '',
  recentPurchaseTime: '',
  tags: [] as any[],
  contactPersonList: [] as any[],
  followList: [] as any[]
})

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
    prop: 'specificationName',
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
    label: '供应商规格',
    prop: 'supplierSpecification',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '供应商编号',
    prop: 'supplierSpecificationCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '供应商单价',
    prop: 'supplierPrice',
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
const supplierProductColumns: ColumnProps[] = [
  {
    label: '产品ID',
    prop: 'productCode',
    align: 'center'
  },
  {
    label: '规格名称',
    prop: 'specificationName',
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
    label: '供应商规格',
    prop: 'supplierSpecification',
    align: 'center'
  },
  {
    label: '供应商编号',
    prop: 'supplierSpecificationCode',
    align: 'center'
  },
  {
    label: '供应商单价',
    prop: 'supplierPrice',
    align: 'center'
  },
  {
    label: '起订量',
    prop: 'minNumber',
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    align: 'center',
    width: 100
  }
]

const getSupplierProductList = async (params: any) => {
  return await getSupplierSpecification({
    ...params,
    supplierId: supplierData.id
  })
}

const supplierProductDataCallback = (data: any) => {
  const list: any[] = []
  const productGroups = new Map()
  const items = Array.isArray(data.list) ? data.list : []

  items.forEach((item: any) => {
    const productKey = item.productCode || ''
    if (!productGroups.has(productKey)) {
      productGroups.set(productKey, [])
    }

    const specName =
      item.itemList
        ?.map((spec: any) => spec.categorySpecificationItemValue)
        .filter((v: string) => v)
        .join('-') || '-'

    productGroups.get(productKey).push({
      id: item.id,
      specificationId: item.specificationId,
      productCode: item.productCode || '-',
      specificationName: specName,
      image: item.imageList?.[0]?.url || '',
      supplierSpecification: item.supplierSpecification || '',
      supplierSpecificationCode: item.supplierSpecificationCode || '',
      supplierPrice: item.supplierPrice ?? '',
      minNumber: item.minNumber || '',
      _raw: item
    })
  })

  productGroups.forEach(specs => {
    specs.forEach((spec: any, index: number) => {
      list.push({
        ...spec,
        isFirstRow: index === 0,
        groupSize: specs.length,
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

const previewImage = (images: string[]) => {
  viewerImageList.value = images
  showViewer.value = true
}

const handleProductEdit = (row: any) => {
  const params = {
    id: 'productEditDialog',
    el: '#app',
    data: {
      row,
      supplierId: supplierData.id,
      supplierName: supplierData.name,
      supplierCode: supplierData.code,
      callback: () => {
        productTableRef.value?.getTableList()
      }
    },
    render: ProductEditDialog
  }
  dynamic.show(params)
}

// 获取供应商详情
const loadSupplierData = async () => {
  const id = route.query.id as string
  if (!id) return

  const { code, data } = await getSupplierDetail({ id })
  if (code !== 200) return

  const supplier = data || {}
  Object.assign(supplierData, {
    id: supplier.id,
    name: supplier.name,
    code: supplier.code,
    shortName: supplier.shortName,
    address: supplier.address,
    remark: supplier.remark,
    createTime: supplier.createTime,
    recentFollowTime: supplier.recentFollowTime ? dayjs(supplier.recentFollowTime).format('YYYY-MM-DD') : '',
    recentPurchaseTime: supplier.recentPurchaseTime ? dayjs(supplier.recentPurchaseTime).format('YYYY-MM-DD') : ''
  })

  supplierData.tags = (supplier.labelList || []).map((label: any) => ({
    id: label.id,
    value: label.value
  }))

  supplierData.contactPersonList = (supplier.contactPersonList || []).map((contact: any) => ({
    id: contact.id,
    supplierId: contact.supplierId,
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

  supplierData.followList = (supplier.followList || []).map((item: any) => ({
    ...item,
    createTimeStr: item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD') : '-',
    nextVisitDateStr: item.nextVisitDate ? dayjs(item.nextVisitDate).format('YYYY-MM-DD') : '-'
  }))

  nextTick(() => {
    loadPurchaseCharts()
  })
}

// 加载采购图表数据
const loadPurchaseCharts = async () => {
  const params: any = { supplierId: supplierData.id }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dayjs(dateRange.value[0]).format('YYYY-MM-DD HH:mm:ss')
    params.endTime = dayjs(dateRange.value[1]).format('YYYY-MM-DD HH:mm:ss')
  }

  const [trendsRes, ratioRes] = await Promise.all([getPurchaseTrends(params), getPurchaseRatio(params)])

  // 采购趋势
  if (trendsRes.code === 200 && trendsRes.data?.series?.length) {
    hasTrendData.value = true
    nextTick(() => initTrendChart(trendsRes.data.xAxis || [], trendsRes.data.series || []))
  } else {
    hasTrendData.value = false
  }

  // 采购占比
  if (ratioRes.code === 200 && ratioRes.data?.series?.length) {
    hasRatioData.value = true
    nextTick(() => initRatioChart(ratioRes.data.xAxis || [], ratioRes.data.series || []))
  } else {
    hasRatioData.value = false
  }
}

watch(dateRange, () => {
  loadPurchaseCharts()
})

// 初始化采购趋势图表
const initTrendChart = (xAxis: string[] = [], series: number[] = []) => {
  if (!trendChartRef.value) return

  const trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
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
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '采购金额',
        type: 'line',
        data: series,
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      }
    ]
  })
}

// 初始化采购占比图表
const initRatioChart = (xAxis: string[] = [], series: number[] = []) => {
  if (!ratioChartRef.value) return

  const ratioChart = echarts.init(ratioChartRef.value)
  ratioChart.setOption({
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
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '采购金额',
        type: 'bar',
        data: series,
        itemStyle: {
          color: '#409eff'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '¥{c}'
        }
      }
    ]
  })
}

// 编辑供应商
const handleEdit = () => {
  const params = {
    id: 'supplierEditDialog',
    el: '#app',
    data: {
      id: supplierData.id,
      code: supplierData.code,
      name: supplierData.name,
      shortName: supplierData.shortName,
      address: supplierData.address,
      remark: supplierData.remark,
      callback: async () => {
        await loadSupplierData()
      }
    },
    render: SupplierEditDialog
  }
  dynamic.show(params)
}

// 标签相关
const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagRemove = async (labelId: number | string) => {
  await ElMessageBox.confirm('确认删除该标签吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteSupplierLabel({ labelId })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  await loadSupplierData()
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

  if (supplierData.tags.some(tag => tag.value === value)) {
    ElMessage.warning('标签已存在')
    tagInputVisible.value = false
    tagInputValue.value = ''
    return
  }

  const { code, message } = await addSupplierLabel({
    masterId: supplierData.id,
    value: value
  })

  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  ElMessage.success('添加成功')
  tagInputVisible.value = false
  tagInputValue.value = ''
  await loadSupplierData()
}

// 联系人相关
const handleAddContact = () => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      fromType: 'supplier-detail',
      supplierId: supplierData.id,
      callback: async () => {
        await loadSupplierData()
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
      fromType: 'supplier-detail',
      supplierId: supplierData.id,
      callback: async () => {
        await loadSupplierData()
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

  const { code, message } = await deleteSupplierContact({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  await loadSupplierData()
  ElMessage.success('删除成功')
}

const showContactList = () => {
  const params = {
    id: 'contactListDialog',
    el: '#app',
    data: {
      supplierId: supplierData.id,
      onUpdate: (updatedContacts: any[]) => {
        supplierData.contactPersonList = updatedContacts
      }
    },
    render: ContactListDialog
  }
  dynamic.show(params)
}

// 跟进记录相关
const handleAddFollowRecord = () => {
  const params = {
    id: 'followRecordDialog',
    el: '#app',
    data: {
      fromType: 'supplier-detail',
      supplierId: supplierData.id,
      callback: async () => {
        await loadSupplierData()
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
      fromType: 'supplier-detail',
      supplierId: supplierData.id,
      callback: async () => {
        await loadSupplierData()
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
  await ElMessageBox.confirm('确定要删除该跟进记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteSupplierFollow({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  await loadSupplierData()
  ElMessage.success('删除成功')
}

const showFollowRecordList = () => {
  const params = {
    id: 'followRecordListDialog',
    el: '#app',
    data: {
      supplierId: supplierData.id,
      onUpdate: (updatedRecords: any[]) => {
        supplierData.followList = updatedRecords
      }
    },
    render: FollowRecordListDialog
  }
  dynamic.show(params)
}

const handleDetailTabsViewChange = async (tab: any) => {
  const tabName = tab.paneName || tab.props?.name
  console.log('handleDetailTabsViewChange', tabName)

  if (tabName === 'basic') {
    loadSupplierData()
  } else if (tabName === 'product') {
    productTableRef.value?.getTableList()
  }
}

onMounted(() => {
  loadSupplierData()
})
</script>

<style scoped lang="scss">
.supplier-detail-page {
  .detail-content {
    display: flex;
    gap: 10px;
    height: calc(100vh - 123px);
  }

  .detail-left-card {
    width: 280px;
    background: #fff;
    border-radius: 8px;
    flex-shrink: 0;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 16px;
      border-bottom: 1px solid #ebeef5;

      .supplier-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .supplier-name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }

      .edit-icon {
        font-size: 18px;
        color: #909399;
        cursor: pointer;

        &:hover {
          color: #409eff;
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
          .el-tag {
            margin-bottom: 10px;
          }
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
      }
    }

    .chart-container {
      margin-top: 16px;
    }
  }

  .product-info-content {
    padding: 0;
    :deep(.search) {
      padding-top: 0;
      margin-bottom: 0;
    }
  }
}
</style>
