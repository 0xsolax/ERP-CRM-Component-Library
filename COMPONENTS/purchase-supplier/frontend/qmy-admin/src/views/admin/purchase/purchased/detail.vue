<template>
  <div class="purchase-detail-container">
    <div class="detail-content">
      <div class="content-left">
        <el-card class="info-card" shadow="never">
          <div class="detail-header">
            <div class="header-left">
              <span class="order-label">采购单号：</span>
              <span v-copy="purchaseInfo.code" class="order-number order-number--copyable" title="点击复制">
                {{ purchaseInfo.code }}
              </span>
              <el-tag type="info" size="large" class="status-tag status-primary">
                {{ purchaseInfo.isInboundDeliveryLabel }}
              </el-tag>
            </div>
            <div class="header-right">
              <el-button type="primary" v-permission="'pur:yt:purchase:export'" @click="handleExport">导出</el-button>
              <el-button type="primary" v-permission="'pur:yt:purchase:follow:createOrUpdate'" @click="handleFollow">
                跟进
              </el-button>
            </div>
          </div>

          <div class="info-section">
            <div class="info-left">
              <div class="info-row">
                <div class="info-item">
                  <span class="label">1688单号：</span>
                  <span class="value">{{ purchaseInfo.orderPlatformCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">完成时间：</span>
                  <span class="value">{{ purchaseInfo.completedTime }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">采购时间：</span>
                  <span class="value">{{ purchaseInfo.createTime }}</span>
                </div>
                <div class="info-item">
                  <span class="label">交货时间：</span>
                  <span class="value">{{ purchaseInfo.deliveryTime }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">优惠金额：</span>
                  <span class="value">¥{{ purchaseInfo.discountAmount }}</span>
                </div>
                <div class="info-item">
                  <span class="label">运费金额：</span>
                  <span class="value">¥{{ purchaseInfo.shippingCost }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">付款方式：</span>
                  <span class="value">{{ purchaseInfo.payMethodLabel || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">付款形式：</span>
                  <span class="value">{{ purchaseInfo.payWay || '-' }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item full-width">
                  <span class="label">采购单备注：</span>
                  <span class="value">{{ purchaseInfo.remark || '-' }}</span>
                </div>
              </div>
            </div>
            <div class="info-right">
              <div class="status-item">
                <div class="status-label">状态</div>
                <div class="status-value">{{ purchaseInfo.statusLabel }}</div>
              </div>
              <div class="amount-item">
                <div class="amount-label">采购总价</div>
                <div class="amount-value">¥{{ purchaseInfo.totalAmount }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="content-right">
        <div class="section-card">
          <div class="section-title">上下游信息</div>
          <div class="section-content">
            <div class="info-item">
              <span class="label">供应商名称：</span>
              <span class="value">{{ purchaseInfo.supplierName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-card class="tabs-card" shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="产品" name="product" />
        <el-tab-pane name="semiFinished">
          <template #label>
            <span>半成品</span>
            <el-badge v-if="hasUnconfirmedProducts" is-dot />
          </template>
        </el-tab-pane>
        <el-tab-pane label="退货" name="refund" />
      </el-tabs>

      <!-- 产品Tab -->
      <div v-if="activeTab === 'product'" class="tab-content">
        <div class="tab-header">
          <div class="filter-group">
            <el-button :class="{ active: productFilter === 'all' }" @click="handleFilterChange('all')">
              全部 ({{ statusCount.all }})
            </el-button>
            <el-button :class="{ active: productFilter === 'purchasing' }" @click="handleFilterChange('purchasing')">
              采购中 ({{ statusCount.purchasing }})
            </el-button>
            <el-button
              v-if="purchaseInfo.isInboundDelivery"
              :class="{ active: productFilter === 'inbound' }"
              @click="handleFilterChange('inbound')"
            >
              已入库 ({{ statusCount.inbound }})
            </el-button>
            <el-button
              v-if="!purchaseInfo.isInboundDelivery"
              :class="{ active: productFilter === 'shipped' }"
              @click="handleFilterChange('shipped')"
            >
              已发货 ({{ statusCount.shipped }})
            </el-button>
          </div>
          <div class="search-group">
            <el-input v-model="searchForm.orderCode" placeholder="订单编号" style="width: 130px" clearable />
            <el-input v-model="searchForm.specificationName" placeholder="规格名称" style="width: 130px" clearable />
            <el-input v-model="searchForm.productCode" placeholder="产品ID" style="width: 130px" clearable />
            <el-input v-model="searchForm.image" placeholder="图片" style="width: 130px" clearable />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset" style="margin-left: 0">重置</el-button>
          </div>
        </div>

        <el-table :data="productList" border style="margin-top: 15px" :span-method="productSpanMethod">
          <el-table-column label="产品信息" align="center" width="230">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  v-if="row.productImage"
                  :src="row.productImage"
                  fit="cover"
                  style="width: 40px; height: 40px; cursor: pointer"
                  @click="handleImagePreview(row.productImage)"
                />
                <div class="product-text">
                  <div>产品ID：{{ row.productCode }}</div>
                  <div>供应商编号：{{ row.supplierSpecificationCode }}</div>
                  <div>产品总数：{{ row.productTotalNumber }}</div>
                  <div>产品总成本：¥{{ row.productTotalCost }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="规格信息" align="center" width="230">
            <template #default="{ row }">
              <div class="spec-info">
                <el-image
                  v-if="row.specImage"
                  :src="row.specImage"
                  fit="cover"
                  style="width: 40px; height: 40px; cursor: pointer"
                  @click="handleImagePreview(row.specImage)"
                />
                <div class="spec-text">
                  <div>规格名称：{{ row.specName }}</div>
                  <div>规格总数：{{ row.specificationTotalNumber }}</div>
                  <div>供应商单价：¥{{ row.supplierPrice }}</div>
                  <div>规格成本：¥{{ row.specificationTotalCost }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :label="purchaseInfo.isInboundDelivery ? '已入库/总采购数量' : '已发货/总采购数量'"
            align="center"
            width="160"
          >
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 8px; padding: 0 8px">
                <el-progress :percentage="row.progressPercentage" :show-text="false" style="flex: 1" />
                <span style="font-size: 13px; color: #606266; white-space: nowrap">{{ row.progressText }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属订单编号" prop="orderCode" align="center" />
          <el-table-column label="业务员" prop="salesEmployeeName" align="center" />
          <el-table-column label="客户名称" prop="customerName" align="center">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; justify-content: center">
                <span>{{ row.customerName }}</span>
                <el-tag v-if="row.isCustomerStore" size="small" type="primary" style="margin-left: 4px">独</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="订单规格备注" prop="orderRemark" align="center" />
          <el-table-column label="采购规格备注" prop="remark" align="center" />
          <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center" />
          <el-table-column label="状态" prop="statusLabel" align="center" />
          <el-table-column label="操作" fixed="right" align="center" width="130">
            <template #default="{ row }">
              <div class="operation-cell">
                <!-- <el-button
                  type="primary"
                  link
                  v-permission="'pur:yt:purchase:return'"
                  @click="handleRefundProduct(row)"
                >
                  退货
                </el-button> -->
                <el-button type="primary" link @click="handleProductFollow(row)">产品进度</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 半成品Tab -->
      <div v-if="activeTab === 'semiFinished'" class="tab-content">
        <div class="tab-header">
          <div class="filter-group">
            <el-button :class="{ active: semiFilter === 'all' }" @click="handleSemiFilterChange('all')">
              全部 ({{ semiStatusCount.all }})
            </el-button>
            <el-button :class="{ active: semiFilter === 'pending' }" @click="handleSemiFilterChange('pending')">
              待确认 ({{ semiStatusCount.pending }})
            </el-button>
            <el-button :class="{ active: semiFilter === 'confirmed' }" @click="handleSemiFilterChange('confirmed')">
              已确认 ({{ semiStatusCount.confirmed }})
            </el-button>
          </div>
          <div class="search-group">
            <el-input v-model="semiSearchForm.orderCode" placeholder="订单编号" style="width: 130px" clearable />
            <el-input v-model="semiSearchForm.productCode" placeholder="产品ID" style="width: 130px" clearable />
            <el-input v-model="semiSearchForm.image" placeholder="图片" style="width: 130px" clearable />
            <el-button type="primary" @click="handleSemiSearch">搜索</el-button>
            <el-button @click="handleSemiReset" style="margin-left: 0">重置</el-button>
          </div>
        </div>

        <el-table :data="semiProductList" border style="margin-top: 15px" :span-method="semiSpanMethod">
          <el-table-column label="产品信息" align="center" width="250">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  v-if="row.productImage"
                  :src="row.productImage"
                  fit="cover"
                  style="width: 40px; height: 40px; cursor: pointer"
                  @click="handleImagePreview(row.productImage)"
                />
                <div class="product-text">
                  <div>产品ID：{{ row.productCode }}</div>
                  <div>供应商编号：{{ row.supplierSpecificationCode }}</div>
                  <div>产品总数：{{ row.productTotalNumber }}</div>
                  <div v-if="row.confirmStatus == 1">产品总成本：¥{{ row.productTotalCost }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="规格信息" align="center" width="200">
            <template #default="{ row }">
              <div class="spec-info">
                <el-image
                  v-if="row.specImage"
                  :src="row.specImage"
                  fit="cover"
                  style="width: 40px; height: 40px; cursor: pointer"
                  @click="handleImagePreview(row.specImage)"
                />
                <div class="spec-text">
                  <div>规格名称：{{ row.specName }}</div>
                  <div>规格总数：{{ row.specificationTotalNumber }}</div>
                  <div>供应商单价：¥{{ row.supplierPrice }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="已确认/总采购数量" align="center">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 8px; padding: 0 8px">
                <el-progress :percentage="row.progressPercentage" :show-text="false" style="flex: 1" />
                <span style="font-size: 13px; color: #606266; white-space: nowrap">{{ row.progressText }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属订单编号" prop="orderCode" align="center" width="160" />
          <el-table-column label="业务员" prop="salesEmployeeName" align="center" />
          <el-table-column label="客户名称" prop="customerName" align="center">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; justify-content: center">
                <span>{{ row.customerName }}</span>
                <el-tag v-if="row.isCustomerStore" size="small" type="primary" style="margin-left: 4px">独</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="订单规格备注" prop="orderRemark" align="center" />
          <el-table-column label="采购规格备注" prop="remark" align="center" />
          <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center" />
          <el-table-column label="状态" prop="statusLabel" align="center" />
          <el-table-column label="操作" fixed="right" align="center" width="170">
            <template #default="{ row }">
              <div class="operation-cell">
                <!-- <el-button type="primary" link v-permission="'pur:yt:purchase:return'" @click="handleSemiRefund(row)">
                  退货
                </el-button> -->
                <el-button type="primary" link @click="handleSemiFollow(row)">产品进度</el-button>
                <el-button type="primary" v-if="!row.isNotice" link @click="handleSemiNotice(row)">通知</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 退货Tab -->
      <div v-if="activeTab === 'refund'" class="tab-content">
        <div class="tab-header">
          <div class="search-group">
            <el-input v-model="refundSearchForm.productCode" placeholder="产品ID" style="width: 130px" clearable />
            <el-input v-model="refundSearchForm.image" placeholder="图片" style="width: 130px" clearable />
            <el-input v-model="refundSearchForm.specName" placeholder="规格名称" style="width: 130px" clearable />
            <el-date-picker
              v-if="refundViewType === 'list'"
              v-model="refundSearchForm.dateRange"
              type="daterange"
              range-separator="-"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD"
              style="width: 260px"
            />
            <el-button type="primary" @click="handleRefundSearch">搜索</el-button>
            <el-button @click="handleRefundReset" style="margin-left: 0">重置</el-button>
          </div>
          <el-button-group>
            <el-button :type="refundViewType === 'list' ? 'primary' : ''" @click="refundViewType = 'list'">
              <el-icon><Operation /></el-icon>
            </el-button>
            <el-button :type="refundViewType === 'detail' ? 'primary' : ''" @click="refundViewType = 'detail'">
              <el-icon><Grid /></el-icon>
            </el-button>
          </el-button-group>
        </div>

        <!-- 详情视图 -->
        <el-table v-if="refundViewType === 'detail'" :data="refundDetailList" border style="margin-top: 15px">
          <el-table-column label="产品ID" prop="productCode" align="center" width="160" />
          <el-table-column label="规格图片" align="center">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.specImage)"
              />
              <span v-else style="color: #999">-</span>
            </template>
          </el-table-column>
          <el-table-column label="规格名称" prop="specName" align="center" />
          <el-table-column label="订单编号" prop="orderCode" align="center" />
          <el-table-column label="订单退货信息" align="center">
            <template #default="{ row }">
              <div class="refund-info">
                <span>初始数量：{{ row.orderInitCount }}</span>
                <span>总退货数量：{{ row.orderRefundCount }}</span>
                <span>当前数量：{{ row.orderCurrentCount }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="采购单退货信息" align="center">
            <template #default="{ row }">
              <div class="refund-info">
                <span>初始数量：{{ row.purchaseInitCount }}</span>
                <span>总退货数量：{{ row.purchaseRefundCount }}</span>
                <span>当前数量：{{ row.purchaseCurrentCount }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" align="center" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleRefundDetail(row)">详情</el-button>
              <!-- <el-button type="primary" link @click="handleMarkRefund(row)">标记</el-button> -->
            </template>
          </el-table-column>
        </el-table>

        <!-- 列表视图 -->
        <el-table v-if="refundViewType === 'list'" :data="refundList" border style="margin-top: 15px">
          <el-table-column label="产品ID" prop="productCode" align="center" width="160" />
          <el-table-column label="规格图片" align="center" width="150">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.specImage)"
              />
              <span v-else style="color: #999">-</span>
            </template>
          </el-table-column>
          <el-table-column label="规格名称" prop="specName" align="center" />
          <el-table-column label="采购单退货数量" prop="returnNumber" align="center" />
          <el-table-column label="退货人" prop="returnUserName" align="center" />
          <el-table-column label="退货时间" prop="createTime" align="center" />
          <el-table-column label="采购单退货原因" prop="reason" align="center" />
        </el-table>
      </div>
    </el-card>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Grid, Operation } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import {
  getPurchaseDetail,
  getPurchaseProductList,
  getPurchaseSemiFinishedList,
  getPurchaseReturnRecord,
  getReturnStats
} from '@/api/admin/purchase/purchased'
import { getPurchaseStatusLabel, getSemiFinishedStatusLabel, getPaymentMethodLabel } from '@/constant/yitang/purchase'
import followDrawer from './components/follow-drawer.vue'
import refundDetailDrawer from './components/refund-detail-drawer.vue'
import refundDialog from './components/refund-dialog.vue'
import noticeDialog from './components/notice-dialog.vue'
import productProgress from './components/product-progress.vue'
import dayjs from 'dayjs'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getYitangAdminToken, getYitangAdminTenantInfo } from '@/utils/auth'

const route = useRoute()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const purchaseInfo = ref<any>({
  code: '',
  isInboundDelivery: true,
  isInboundDeliveryLabel: '',
  orderPlatformCode: '',
  completedTime: '',
  createTime: '',
  deliveryTime: '',
  discountAmount: '0.00',
  shippingCost: '0.00',
  remark: '',
  statusLabel: '',
  totalAmount: '0.00',
  supplierName: ''
})

const activeTab = ref('product')

// 产品Tab
const productFilter = ref('all')
const statusCount = ref({ all: 0, purchasing: 0, inbound: 0, shipped: 0 })
const searchForm = ref({ orderCode: '', specificationName: '', productCode: '', image: '' })
const productList = ref<any[]>([])

// 半成品Tab
const semiFilter = ref('all')
const semiStatusCount = ref({ all: 0, pending: 0, confirmed: 0 })
const semiSearchForm = ref({ orderCode: '', productCode: '', image: '' })
const semiProductList = ref<any[]>([])

const hasUnconfirmedProducts = computed(() => {
  return semiStatusCount.value.pending > 0
})

// 退货Tab
const refundViewType = ref('detail')
const refundSearchForm = ref({ productCode: '', image: '', specName: '', dateRange: [] as string[] })
const refundList = ref<any[]>([])
const refundDetailList = ref<any[]>([])

const productSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
    if (row.isFirstInGroup) {
      return { rowspan: row.groupRowSpan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
}

const semiSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 2) {
    if (row.isFirstInGroup) {
      return { rowspan: row.groupRowSpan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
}

const purchaseId = ref<string>('')

const loadDetail = async () => {
  const id = route.query.id
  if (!id) return

  purchaseId.value = id as string
  const { code, data, message } = await getPurchaseDetail(id as string)
  if (code !== 200) return ElMessage.warning(message)

  const purchase = data.purchase || {}
  purchaseInfo.value = {
    code: purchase.code || '',
    isInboundDelivery: purchase.isInboundDelivery,
    isInboundDeliveryLabel: purchase.isInboundDelivery ? '入库发货' : '供应商发货',
    orderPlatformCode: purchase.orderPlatformCode || '-',
    completedTime: purchase.completedTime || '-',
    createTime: purchase.createTime || '-',
    deliveryTime: purchase.deliveryTime ? dayjs(purchase.deliveryTime).format('YYYY-MM-DD') : '-',
    discountAmount: purchase.discountAmount ?? '0.00',
    shippingCost: purchase.shippingCost ?? '0.00',
    remark: purchase.remark || '',
    statusLabel: getPurchaseStatusLabel(purchase.status),
    totalAmount: purchase.totalAmount ?? '0.00',
    supplierName: purchase.supplierName || '-',
    payMethodLabel: getPaymentMethodLabel(purchase.payMethod),
    payWay: purchase.payWay || ''
  }

  const statusNumber = data.statusNumber || {}
  const purchasingCount = statusNumber['1'] || 0
  const inboundCount = statusNumber['2'] || 0
  const shippedCount = statusNumber['3'] || 0
  statusCount.value = {
    all: 0,
    purchasing: purchasingCount,
    inbound: inboundCount,
    shipped: shippedCount
  }

  if (purchaseInfo.value.isInboundDelivery) {
    statusCount.value.all = purchasingCount + inboundCount
  } else {
    statusCount.value.all = purchasingCount + shippedCount
  }

  const inCompletedStatusNumber = data.inCompletedStatusNumber || {}
  const pendingCount = inCompletedStatusNumber['0'] || 0
  const confirmedCount = inCompletedStatusNumber['1'] || 0
  semiStatusCount.value = {
    all: Number(pendingCount) + Number(confirmedCount),
    pending: pendingCount,
    confirmed: confirmedCount
  }
}

// 加载产品列表
const loadProductList = async () => {
  if (!purchaseId.value) return
  const statusObj = {
    all: null,
    purchasing: '1',
    inbound: '2',
    shipped: '3'
  }
  const params: any = {
    purchaseId: purchaseId.value,
    orderCode: searchForm.value.orderCode || null,
    specificationName: searchForm.value.specificationName || null,
    productCode: searchForm.value.productCode || null,
    status: statusObj[productFilter.value]
  }
  const { code, data, message } = await getPurchaseProductList(params)
  if (code !== 200) return ElMessage.warning(message)
  processProductList(data || [])
}

// 加载半成品列表
const loadSemiFinishedList = async () => {
  if (!purchaseId.value) return
  const params: any = {
    purchaseId: purchaseId.value,
    orderCode: semiSearchForm.value.orderCode || null,
    productCode: semiSearchForm.value.productCode || null,
    status: semiFilter.value === 'all' ? null : semiFilter.value === 'pending' ? '0' : '1'
  }
  const { code, data, message } = await getPurchaseSemiFinishedList(params)
  if (code !== 200) return ElMessage.warning(message)
  processSemiProductList(data || [])
}

// 加载退货记录（详情视图）
const loadReturnRecord = async () => {
  if (!purchaseId.value) return
  const params: any = {
    purchaseId: purchaseId.value,
    productCode: refundSearchForm.value.productCode || null,
    specificationName: refundSearchForm.value.specName || null
  }
  const { code, data, message } = await getPurchaseReturnRecord(params)
  if (code !== 200) return ElMessage.warning(message)
  processRefundDetailList(data || [])
}

// 加载退货统计（列表视图）
const loadReturnStats = async () => {
  if (!purchaseId.value) return
  const [startTime, endTime] = refundSearchForm.value.dateRange || []
  const params: any = {
    purchaseId: purchaseId.value,
    productCode: refundSearchForm.value.productCode || '',
    specificationName: refundSearchForm.value.specName || ''
  }
  if (startTime) {
    params.startTime = dayjs(startTime).format('YYYY-MM-DD HH:mm:ss')
  }
  if (endTime) {
    params.endTime = dayjs(endTime).format('YYYY-MM-DD HH:mm:ss')
  }
  const { code, data, message } = await getReturnStats(params)
  if (code !== 200) return ElMessage.warning(message)
  processRefundStatsList(data || [])
}

const processProductList = (items: any[]) => {
  const productGroups: any = {}
  items.forEach((item: any) => {
    const productId = item.productId || ''
    if (!productGroups[productId]) {
      productGroups[productId] = []
    }
    productGroups[productId].push(item)
  })

  const tableData: any[] = []
  Object.keys(productGroups).forEach(productId => {
    const group = productGroups[productId]
    const groupSupplierSpecificationCode =
      group.find((item: any) => item.supplierSpecificationCode && String(item.supplierSpecificationCode).trim())
        ?.supplierSpecificationCode || '-'
    group.forEach((item: any, index: number) => {
      // 入库发货enterNumber，供应商发货deliveryNumber
      const progressNum = purchaseInfo.value.isInboundDelivery ? item.enterNumber || 0 : item.deliveryNumber || 0
      const totalNum = item.number || 0
      const percentage = totalNum > 0 ? Math.round((progressNum / totalNum) * 100) : 0
      const specName =
        item.specificationItemList?.map((spec: any) => `${spec.categorySpecificationItemValue}`).join('-') || '-'

      tableData.push({
        ...item,
        productCode: item.productCode || item.productId || '',
        supplierSpecificationCode: groupSupplierSpecificationCode,
        productImage: item.productImageList?.[0]?.url || '',
        specImage: item.specificationImageList?.[0]?.url || '',
        specName,
        supplierPrice: item.supplierPrice ?? '0.00',
        progressPercentage: percentage,
        progressText: `${progressNum}/${totalNum}`,
        orderCode: item.orderCode || '-',
        salesEmployeeName: item.salesEmployeeName || '-',
        customerName: item.customerName || '-',
        isCustomerStore: item.isCustomerStore || false,
        orderRemark: item.orderRemark || '-',
        remark: item.remark || '-',
        categorySpecificationItemName: item.categorySpecificationItemName || '-',
        statusLabel: getPurchaseStatusLabel(item.status),
        isFirstInGroup: index === 0,
        groupRowSpan: group.length
      })
    })
  })

  productList.value = tableData
}

const processSemiProductList = (items: any[]) => {
  const tableData: any[] = []

  items.forEach((item: any) => {
    const confirmItemList = item.confirmItemList || []
    const groupSupplierSpecificationCode =
      confirmItemList.find((ci: any) => ci.supplierSpecificationCode && String(ci.supplierSpecificationCode).trim())
        ?.supplierSpecificationCode ||
      item.supplierSpecificationCode ||
      '-'
    const totalNum = item.number || 0
    // 已确认数量 = confirmItemList中所有number相加
    const confirmedNum = confirmItemList.reduce((sum: number, ci: any) => sum + (Number(ci.number) || 0), 0)
    const percentage = totalNum > 0 ? Math.round((confirmedNum / totalNum) * 100) : 0

    // 产品总成本 = 已确认的规格数量*供应商单价
    const productTotalCost = confirmItemList.reduce(
      (sum: number, ci: any) => sum + (Number(ci.number) || 0) * (Number(ci.supplierPrice) || 0),
      0
    )

    if (confirmItemList.length === 0) {
      tableData.push({
        ...item,
        productCode: item.productCode || '',
        supplierSpecificationCode: groupSupplierSpecificationCode,
        productImage: item.productImageList?.[0]?.url || '',
        productTotalNumber: totalNum,
        productTotalCost: productTotalCost.toFixed(2),
        specImage: '',
        specName: '-',
        specificationTotalNumber: '-',
        supplierPrice: '-',
        progressPercentage: percentage,
        progressText: `${confirmedNum}/${totalNum}`,
        orderCode: item.orderCode || '-',
        salesEmployeeName: item.salesEmployeeName || '-',
        customerName: item.customerName || '-',
        isCustomerStore: item.isCustomerStore || false,
        orderRemark: item.orderRemark || '-',
        remark: item.remark || '-',
        categorySpecificationItemName: '-',
        statusLabel: getSemiFinishedStatusLabel(item.confirmStatus),
        isFirstInGroup: true,
        groupRowSpan: 1,
        hasSpec: false
      })
    } else {
      confirmItemList.forEach((ci: any, index: number) => {
        const specName =
          ci.specificationItemList
            ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
            .join('/') || '-'

        tableData.push({
          ...item,
          ...ci,
          productCode: item.productCode || '',
          supplierSpecificationCode: groupSupplierSpecificationCode,
          productImage: item.productImageList?.[0]?.url || '',
          productTotalNumber: totalNum,
          productTotalCost: productTotalCost.toFixed(2),
          specImage: ci.specificationImageList?.[0]?.url || '',
          specName,
          specificationTotalNumber: ci.number || 0,
          supplierPrice: ci.supplierPrice || '0.00',
          progressPercentage: percentage,
          progressText: `${confirmedNum}/${totalNum}`,
          orderCode: item.orderCode || '-',
          salesEmployeeName: item.salesEmployeeName || '-',
          customerName: item.customerName || '-',
          isCustomerStore: item.isCustomerStore || false,
          orderRemark: item.orderRemark || '-',
          remark: item.remark || '-',
          categorySpecificationItemName: ci.categorySpecificationItemName || '-',
          statusLabel: getSemiFinishedStatusLabel(item.confirmStatus),
          isFirstInGroup: index === 0,
          groupRowSpan: confirmItemList.length,
          hasSpec: true
        })
      })
    }
  })

  semiProductList.value = tableData
}

// 处理详情视图数据
const processRefundDetailList = (items: any[]) => {
  refundDetailList.value = items.map((item: any) => {
    const specName =
      item.itemList
        ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
        .join('/') || '-'
    return {
      productCode: item.productCode || '',
      specImage: item.imageList?.[0]?.url || '',
      specName,
      orderCode: item.orderCode || '-',
      orderInitCount: item.orderInitNumber || 0,
      orderRefundCount: item.orderTotalReturnNumber || 0,
      orderCurrentCount: item.orderCurrentNumber || 0,
      purchaseInitCount: item.purchaseInitNumber || 0,
      purchaseRefundCount: item.purchaseTotalReturnNumber || 0,
      purchaseCurrentCount: item.purchaseCurrentNumber || 0,
      purchaseItemId: item.purchaseItemId
    }
  })
}

// 处理列表视图数据
const processRefundStatsList = (items: any[]) => {
  refundList.value = items.map((item: any) => {
    const specName = item.specificationId
      ? item.itemList
          ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
          .join('/') || '-'
      : '半成品'
    return {
      productCode: item.productCode || '',
      specImage: item.imageList?.[0]?.url || '',
      specName,
      returnNumber: item.returnNumber || 0,
      returnUserName: item.returnUserName || '',
      createTime: item.createTime || '',
      reason: item.reason || ''
    }
  })
}

const handleFilterChange = (filter: string) => {
  productFilter.value = filter
  loadProductList()
}

const handleSearch = () => {
  loadProductList()
}

const handleReset = () => {
  searchForm.value = { orderCode: '', specificationName: '', productCode: '', image: '' }
  loadProductList()
}

const handleRefundProduct = (row: any) => {
  const params = {
    id: 'refundDialog',
    el: '#app',
    data: {
      purchaseItemId: row.id,
      onSuccess: () => {
        loadProductList()
      }
    },
    render: refundDialog
  }
  dynamic.show(params)
}
handleRefundProduct

const handleProductFollow = (row: any) => {
  const params = {
    id: 'productProgress',
    el: '#app',
    data: {
      itemId: row.id
    },
    render: productProgress
  }
  dynamic.show(params)
}

const handleSemiFilterChange = (filter: string) => {
  semiFilter.value = filter
  loadSemiFinishedList()
}

const handleSemiSearch = () => {
  loadSemiFinishedList()
}

const handleSemiReset = () => {
  semiSearchForm.value = { orderCode: '', productCode: '', image: '' }
  loadSemiFinishedList()
}

const handleSemiRefund = (row: any) => {
  const params = {
    id: 'refundDialog',
    el: '#app',
    data: {
      purchaseItemId: row.id,
      onSuccess: () => {
        loadSemiFinishedList()
      }
    },
    render: refundDialog
  }
  dynamic.show(params)
}
handleSemiRefund

const handleSemiFollow = (row: any) => {
  const params = {
    id: 'productProgress',
    el: '#app',
    data: {
      title: '产品进度',
      itemId: row.id
    },
    render: productProgress
  }
  dynamic.show(params)
}

const handleSemiNotice = (row: any) => {
  const params = {
    id: 'noticeDialog',
    el: '#app',
    data: {
      purchaseItemId: row.id,
      productCode: row.productCode,
      specName: row.specName,
      onSuccess: () => {
        loadSemiFinishedList()
      }
    },
    render: noticeDialog
  }
  dynamic.show(params)
}

const handleRefundSearch = () => {
  if (refundViewType.value === 'detail') {
    loadReturnRecord()
  } else {
    loadReturnStats()
  }
}

const handleRefundReset = () => {
  refundSearchForm.value = { productCode: '', image: '', specName: '', dateRange: [] }
  if (refundViewType.value === 'detail') {
    loadReturnRecord()
  } else {
    loadReturnStats()
  }
}

const handleRefundDetail = (row: any) => {
  const params = {
    id: 'refundDetailDrawer',
    el: '#app',
    data: {
      purchaseItemId: row.purchaseItemId
    },
    render: refundDetailDrawer
  }
  dynamic.show(params)
}

// const handleMarkRefund = (row: any) => {
//   ElMessage.info(`标记: ${row.productCode}`)
// }

const handleExport = async () => {
  await ElMessageBox.confirm(`确定要导出采购单 ${purchaseInfo.value.code} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { status, message } = await downloadAxiosBlobFile({
    url: `${import.meta.env.VITE_APP_YITANG_BASE_API}/pur/yt/purchase/export`,
    headers: {
      'qiaomoyun-tenant': getYitangAdminTenantInfo()?.id,
      'qiaomoyun-token': getYitangAdminToken()
    },
    params: {
      purchaseId: purchaseId.value
    },
    method: 'get'
  })
  if (status !== 200) return ElMessage.warning(message)
  ElMessage.success('导出成功')
}

const handleFollow = () => {
  const params = {
    id: 'followDrawer',
    el: '#app',
    data: {
      purchaseId: route.query.id
    },
    render: followDrawer
  }
  dynamic.show(params)
}

watch(
  activeTab,
  async newTab => {
    if (newTab === 'product') {
      await loadProductList()
    } else if (newTab === 'semiFinished') {
      await loadSemiFinishedList()
    } else if (newTab === 'refund') {
      if (refundViewType.value === 'detail') {
        await loadReturnRecord()
      } else {
        await loadReturnStats()
      }
    }
  },
  { immediate: false }
)

// 监听退货视图类型切换
watch(
  refundViewType,
  async newType => {
    if (activeTab.value === 'refund') {
      if (newType === 'detail') {
        await loadReturnRecord()
      } else {
        await loadReturnStats()
      }
    }
  },
  { immediate: false }
)

onMounted(async () => {
  await loadDetail()
  await loadProductList()
  await loadSemiFinishedList()
})
</script>

<style scoped lang="scss">
.purchase-detail-container {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);

  .detail-content {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;

    .content-left {
      flex: 1;

      .info-card {
        border: 0;

        .detail-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-bottom: 15px;
          margin-bottom: 15px;
          border-bottom: 1px solid #f0f0f0;

          .header-left {
            display: flex;
            align-items: center;

            .order-label {
              font-weight: 600;
              color: #333;
              font-size: 14px;
            }

            .order-number {
              font-size: 16px;
              font-weight: 600;
              margin-right: 10px;
              color: #333;

              &.order-number--copyable {
                cursor: pointer;

                &:hover {
                  color: #409eff;
                }
              }
            }

            .status-tag {
              margin: 0 5px;

              &.status-primary {
                background: #e3f2fd;
                color: #2196f3;
                border-color: #90caf9;
              }
            }
          }

          .header-right {
            display: flex;
          }
        }

        .info-section {
          display: flex;
          gap: 30px;

          .info-left {
            flex: 1;

            .info-row {
              display: flex;
              gap: 30px;
              margin-bottom: 12px;

              &:last-child {
                margin-bottom: 0;
              }

              .info-item {
                display: flex;
                font-size: 14px;
                flex: 1;

                &.full-width {
                  flex: 100%;
                }

                .label {
                  color: #909399;
                  white-space: nowrap;
                }

                .value {
                  color: #333;
                }
              }
            }
          }

          .info-right {
            display: flex;
            gap: 30px;

            .status-item,
            .amount-item {
              text-align: right;
              font-size: 14px;

              .status-label,
              .amount-label {
                color: #909399;
                margin-bottom: 8px;
              }

              .status-value {
                font-size: 18px;
                font-weight: 600;
                color: #e6a23c;
              }

              .amount-value {
                font-size: 22px;
                font-weight: 600;
                color: #333;
              }
            }
          }
        }
      }
    }

    .content-right {
      width: 200px;

      .section-card {
        background: #fff;
        border-radius: 4px;
        padding: 15px;
        height: 100%;

        .section-title {
          font-weight: 600;
          font-size: 14px;
          color: #333;
          padding-bottom: 10px;
          border-bottom: 1px solid #f0f0f0;
          margin-bottom: 15px;
        }

        .section-content {
          .info-item {
            font-size: 14px;

            .label {
              color: #909399;
            }

            .value {
              color: #333;
            }
          }
        }
      }
    }
  }

  .tabs-card {
    border: 0;

    .tab-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 15px;

      .filter-group {
        display: flex;

        .el-button {
          padding: 5px 15px;
          border: 1px solid #dcdfe6;
          background: #fff;
          color: #606266;

          &.active {
            color: #409eff;
            border-color: #409eff;
          }

          &:hover {
            color: #409eff;
            border-color: #409eff;
          }
        }
      }

      .search-group {
        display: flex;
        gap: 10px;
        align-items: center;
      }
    }
  }

  .product-info,
  .spec-info {
    display: flex;
    align-items: center;
    gap: 10px;
    text-align: left;
    font-size: 12px;

    .product-text,
    .spec-text {
      line-height: 1.6;
    }
  }

  .refund-info {
    display: flex;
    flex-direction: column;
    gap: 5px;
    font-size: 13px;

    &.warning {
      background: #fef0f0;
      color: #f56c6c;
      padding: 8px;
      border-radius: 4px;
    }
  }
}
</style>
