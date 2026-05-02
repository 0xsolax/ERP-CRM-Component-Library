<template>
  <div class="order-add-container">
    <h2 class="page-title">新增订单</h2>

    <div class="form-section">
      <div class="section-title">基础信息</div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="140px"
        label-position="top"
        class="order-form"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="选择平台" prop="platform">
              <el-select v-model="form.platform" placeholder="请选择" :disabled="isFormDisabled">
                <el-option
                  v-for="platform in platformList"
                  :key="platform.value"
                  :label="platform.label"
                  :value="platform.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="平台单号" prop="platformOrderNo">
              <el-input v-model="form.platformOrderNo" placeholder="请输入" :disabled="isFormDisabled" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="币种" prop="currency">
              <el-select v-model="form.currency" placeholder="请选择" :disabled="isFormDisabled">
                <el-option
                  v-for="currency in currencyList"
                  :key="currency.value"
                  :label="currency.label"
                  :value="currency.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户" prop="customerId">
              <el-select
                v-model="form.customerId"
                placeholder="请选择"
                filterable
                :disabled="isFormDisabled"
                @change="handleCustomerChange"
              >
                <el-option
                  v-for="customer in customerList"
                  :key="customer.id"
                  :label="customer.name"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="收货地址" prop="customerAddressId">
              <el-select
                v-model="form.customerAddressId"
                placeholder="请选择"
                clearable
                :disabled="isFormDisabled"
                @change="handleAddressChange"
              >
                <el-option
                  v-for="address in addressList"
                  :key="address.id"
                  :label="address.fullAddress"
                  :value="address.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务员" prop="salesmanId">
              <el-select v-model="form.salesmanId" placeholder="请选择客户" disabled>
                <el-option
                  v-for="employee in employeeList"
                  :key="employee.userId"
                  :label="employee.nickName"
                  :value="employee.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务员业绩占比(%)" prop="salesmanRate">
              <el-input
                v-model="form.salesmanRate"
                placeholder="请输入"
                :disabled="isFormDisabled"
                @input="handleSalesmanRateInput"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="跟进人" prop="followerId">
              <el-select v-model="form.followerId" placeholder="请选择跟进人" disabled>
                <el-option
                  v-for="employee in employeeList"
                  :key="employee.userId"
                  :label="employee.nickName"
                  :value="employee.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="跟进人业绩占比(%)" prop="followerRate">
              <el-input
                v-model="form.followerRate"
                placeholder="请输入"
                :disabled="isFormDisabled"
                @input="handleFollowerRateInput"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="发货形式" prop="shippingMethod">
              <el-select v-model="form.shippingMethod" placeholder="请选择" :disabled="isFormDisabled">
                <el-option
                  v-for="shippingMethod in shippingMethodList"
                  :key="shippingMethod.value"
                  :label="shippingMethod.label"
                  :value="shippingMethod.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="下单时间" prop="orderTime">
              <el-date-picker
                v-model="form.orderTime"
                type="date"
                placeholder="请选择时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled="isFormDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="交货时间" prop="deliveryTime">
              <el-date-picker
                v-model="form.deliveryTime"
                type="date"
                placeholder="请选择时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled="isFormDisabled"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="优惠金额" prop="discountAmount">
              <el-input
                :model-value="form.discountAmount"
                @update:model-value="val => (form.discountAmount = validateDecimal(val))"
                placeholder="请输入"
                :disabled="isFormDisabled"
              >
                <template #prefix>{{ currencySymbol }}</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="订单备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入" :disabled="isFormDisabled" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="是否入库发货" prop="isInboundDelivery">
              <el-select v-model="form.isInboundDelivery" placeholder="请选择" :disabled="isFormDisabled">
                <el-option
                  v-for="inboundDelivery in isWarehouseDeliveryList"
                  :key="inboundDelivery.value"
                  :label="inboundDelivery.label"
                  :value="inboundDelivery.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="运费" prop="isCollectedShippingCost">
              <el-select v-model="form.isCollectedShippingCost" placeholder="请选择" :disabled="isFormDisabled">
                <el-option
                  v-for="collectedShippingCost in collectedShippingCostList"
                  :key="collectedShippingCost.value"
                  :label="collectedShippingCost.label"
                  :value="collectedShippingCost.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="Number(form.isCollectedShippingCost) === 1" :gutter="20">
          <el-col :span="6">
            <el-form-item label="运费金额" prop="shippingCost">
              <el-input
                :model-value="form.shippingCost"
                @update:model-value="val => (form.shippingCost = validateDecimal(val))"
                placeholder="请输入"
                :disabled="isFormDisabled"
              >
                <template #prefix>{{ currencySymbol }}</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div v-for="(order, index) in orderDetails" :key="index" v-show="!order.isDeleted" class="order-detail-card">
      <div class="card-header">
        <div class="section-title">订单详情</div>
        <el-button
          v-if="orderDetails.length > 1 && !isFormDisabled"
          type="danger"
          link
          @click="handleDeleteOrder(index)"
        >
          删除订单
        </el-button>
      </div>

      <el-form
        :ref="el => (orderFormRefs[index] = el)"
        :model="order"
        :rules="orderFormRules"
        label-width="140px"
        label-position="top"
        class="order-form"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单类型" prop="orderType">
              <el-select
                v-model="order.orderType"
                placeholder="请选择"
                :disabled="isFormDisabled"
                @change="() => handleOrderTypeChange(index)"
              >
                <el-option
                  v-for="orderType in orderTypeList"
                  :key="orderType.value"
                  :label="orderType.label"
                  :value="orderType.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div v-if="order.orderType === '0'" class="product-section" @click="currentOrderIndex = index">
        <el-table
          :data="order.tableData"
          style="width: 100%"
          :span-method="productSpanMethod"
          :row-class-name="tableRowClassName"
        >
          <el-table-column label="产品ID" prop="productCode" width="80" align="center" fixed="left">
            <template #default="{ row }">
              <span>{{ row.productCode }}</span>
            </template>
          </el-table-column>
          <el-table-column label="总数" width="80" align="center" fixed="left">
            <template #default="{ row }">
              <span>{{ row.totalQuantity }}</span>
            </template>
          </el-table-column>
          <el-table-column label="销售总价" width="80" align="center" fixed="left">
            <template #default="{ row }">
              <span>{{ currencySymbol }}{{ row.totalSalePrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="规格图片" align="center" fixed="left">
            <template #default="{ row }">
              <div
                style="
                  width: 60px;
                  height: 60px;
                  border: 1px dashed #ddd;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin: 0 auto;
                  cursor: pointer;
                "
              >
                <el-image
                  v-if="row.specImage"
                  :src="row.specImage"
                  hide-on-click-modal
                  style="width: 100%; height: 100%"
                  fit="cover"
                />
                <span v-else style="color: #999; font-size: 12px">+</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="规格名称" align="center" fixed="left">
            <template #default="{ row }">
              <span>{{ row.specName || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="销售单价" align="center">
            <template #default="{ row }">
              <el-input
                class="sale-price-input"
                :model-value="row.salePrice"
                @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'salePrice', val)"
                placeholder="请输入"
                :disabled="isFormDisabled"
              >
                <template #prefix>{{ currencySymbol }}</template>
                <template v-if="!isFormDisabled" #append>
                  <el-button
                    type="primary"
                    link
                    @click="applyToSameProduct(row.productCode, 'salePrice', row.salePrice)"
                    title="批量应用到产品"
                  >
                    <el-icon><Setting /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="数量" align="center">
            <template #default="{ row }">
              <div class="quantity-cell">
                <el-input
                  class="quantity-input"
                  :model-value="row.quantity"
                  @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'quantity', val)"
                  placeholder="请输入"
                  :disabled="isFormDisabled"
                >
                  <template v-if="!isFormDisabled" #append>
                    <el-button
                      type="primary"
                      link
                      @click="applyToSameProduct(row.productCode, 'quantity', row.quantity)"
                      title="批量应用到产品"
                    >
                      <el-icon><Setting /></el-icon>
                    </el-button>
                  </template>
                </el-input>
                <div v-if="isLessThanMinOrder(row)" class="min-order-tip">供应商起订量: {{ row.minNumber }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" width="150" align="center">
            <template #default="{ row }">
              <div style="display: flex; align-items: center">
                <el-select
                  :model-value="row.labelId"
                  @update:model-value="val => handleLabelChange(row.productIndex, row.specIndex, val)"
                  placeholder="请选择"
                  filterable
                  clearable
                  :disabled="isFormDisabled"
                  style="flex: 1"
                >
                  <el-option
                    v-for="label in getCategoryLabels(row.productId)"
                    :key="label.id"
                    :label="label.value"
                    :value="String(label.id)"
                  />
                </el-select>
                <el-button
                  v-if="!isFormDisabled"
                  type="primary"
                  link
                  size="small"
                  @click="applyToSameProduct(row.productCode, 'labelId', row.labelId, row.labelName)"
                  title="批量应用到产品"
                  style="padding-left: 5px"
                >
                  <el-icon><Setting /></el-icon>
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="订单规格备注" align="center">
            <template #default="{ row }">
              <el-input
                :model-value="row.specNote"
                @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'specNote', val)"
                placeholder="请输入"
                :disabled="isFormDisabled"
              />
            </template>
          </el-table-column>
          <el-table-column label="供应商名称" width="120" align="center">
            <template #default="{ row }">
              <span style="color: var(--el-color-warning)">{{ row.supplierName || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="供应商单价" width="100" align="center">
            <template #default="{ row }">
              <span>￥{{ row.supplierPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="供应商总成本" width="110" align="center">
            <template #default="{ row }">
              <span>￥{{ row.totalSupplierCost }}</span>
            </template>
          </el-table-column>
          <el-table-column label="本规格销售总价" width="130" align="center">
            <template #default="{ row }">
              <span>{{ currencySymbol }}{{ row.specTotalPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="70" fixed="right">
            <template #default="{ $index }">
              <el-button :disabled="isFormDisabled" type="danger" link size="small" @click="removeRow($index, index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="add-product-btn" @click="() => !isFormDisabled && addProduct(index)">
          <el-button type="text" :disabled="isFormDisabled">+ 请选择产品</el-button>
        </div>
      </div>

      <div v-else-if="order.orderType === '1'" class="product-section" @click="currentOrderIndex = index">
        <el-table :data="order.tableData" style="width: 100%" :row-class-name="tableRowClassName">
          <el-table-column label="产品ID" prop="productCode" width="150" align="center">
            <template #default="{ row }">
              <span>{{ row.productCode }}</span>
            </template>
          </el-table-column>
          <el-table-column label="产品图片" align="center">
            <template #default="{ row }">
              <div
                style="
                  width: 60px;
                  height: 60px;
                  border: 1px dashed #ddd;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  cursor: pointer;
                  margin: 0 auto;
                "
              >
                <el-image
                  v-if="row.specImage"
                  :src="row.specImage"
                  hide-on-click-modal
                  style="width: 100%; height: 100%"
                  fit="cover"
                />
                <span v-else style="color: #999; font-size: 12px">+</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="销售总价" align="center">
            <template #default="{ row }">
              <span>{{ currencySymbol }}{{ row.totalSalePrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="销售单价" align="center">
            <template #default="{ row }">
              <el-input
                class="sale-price-input"
                :model-value="row.salePrice"
                @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'salePrice', val)"
                placeholder="请输入"
                :disabled="isFormDisabled"
              >
                <template #prefix>{{ currencySymbol }}</template>
                <template v-if="!isFormDisabled" #append>
                  <el-button
                    type="primary"
                    link
                    @click="applyToSameProduct(row.productCode, 'salePrice', row.salePrice)"
                    title="批量应用到产品"
                  >
                    <el-icon><Setting /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="数量" align="center">
            <template #default="{ row }">
              <div class="quantity-cell">
                <el-input
                  class="quantity-input"
                  :model-value="row.quantity"
                  @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'quantity', val)"
                  placeholder="请输入"
                  :disabled="isFormDisabled"
                >
                  <template v-if="!isFormDisabled" #append>
                    <el-button
                      type="primary"
                      link
                      @click="applyToSameProduct(row.productCode, 'quantity', row.quantity)"
                      title="批量应用到产品"
                    >
                      <el-icon><Setting /></el-icon>
                    </el-button>
                  </template>
                </el-input>
                <div v-if="isLessThanMinOrder(row)" class="min-order-tip">起订量：{{ row.minNumber }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" align="center">
            <template #default="{ row }">
              <div style="display: flex; align-items: center">
                <el-select
                  :model-value="row.labelId"
                  @update:model-value="val => handleLabelChange(row.productIndex, row.specIndex, val)"
                  placeholder="请选择"
                  clearable
                  :disabled="isFormDisabled"
                  style="flex: 1"
                >
                  <el-option
                    v-for="label in getCategoryLabels(row.productId)"
                    :key="label.id"
                    :label="label.value"
                    :value="String(label.id)"
                  />
                </el-select>
                <el-button
                  v-if="!isFormDisabled"
                  type="primary"
                  link
                  size="small"
                  @click="applyToSameProduct(row.productCode, 'labelId', row.labelId, row.labelName)"
                  title="批量应用到产品"
                  style="padding-left: 5px"
                >
                  <el-icon><Setting /></el-icon>
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="备注" align="center">
            <template #default="{ row }">
              <el-input
                :model-value="row.specNote"
                @update:model-value="val => updateSpec(row.productIndex, row.specIndex, 'specNote', val)"
                placeholder="请输入"
                :disabled="isFormDisabled"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button :disabled="isFormDisabled" type="danger" link size="small" @click="removeRow($index, index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="add-product-btn" @click="() => !isFormDisabled && addProduct(index)">
          <el-button type="text" :disabled="isFormDisabled">+ 请选择产品</el-button>
        </div>
      </div>
    </div>

    <div v-if="!isFormDisabled" class="add-order-btn-wrapper">
      <el-button type="primary" @click="handleAddOrder">+ 添加订单</el-button>
    </div>

    <footer-actions>
      <div class="summary-info">
        <span>
          产品共计：
          <strong>{{ totalProductCount }}件</strong>
        </span>
        <span>
          预估成本：
          <strong>￥{{ estimatedCost.toFixed(2) }}</strong>
        </span>
        <span>
          预计毛利 (按汇率换算)：
          <strong>￥{{ estimatedProfit.toFixed(2) }}</strong>
        </span>
        <span>
          预计毛利率：
          <strong>{{ estimatedProfitRate.toFixed(2) }}%</strong>
        </span>
        <span>
          总金额 (含客户运费)：
          <strong>{{ currencySymbol }}{{ totalAmountWithShipping.toFixed(2) }}</strong>
        </span>
      </div>
      <div class="action-buttons">
        <el-button v-if="buttonConfig.showCancel" @click="handleCancel">取消</el-button>
        <el-button v-if="buttonConfig.showSaveDraft" :loading="saveDraftLoading" @click="handleSaveDraft">
          暂存
        </el-button>
        <el-button v-if="buttonConfig.showReviewReject" type="danger" @click="handleReviewReject">审核拒绝</el-button>
        <el-button v-if="buttonConfig.showReviewApprove" type="primary" @click="handleReviewApprove">
          审核通过
        </el-button>
        <el-button v-if="buttonConfig.showNext" type="primary" :loading="nextLoading" @click="handleNext">
          下一步
        </el-button>
        <el-button
          v-if="buttonConfig.showSubmitReview"
          type="primary"
          :loading="submitReviewLoading"
          @click="handleSubmitReview"
        >
          提交审核
        </el-button>
      </div>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup name="sales-order-add">
import { ref, computed, onMounted, onActivated, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import FooterActions from '@/components/footer-actions/index.vue'
import ProductSelector from '@/components/product-selector/index.vue'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import { saveOrUpdateOrder, getOrderDetail, auditOrder, getExchangeRate } from '@/api/admin/sales/order'
import { getCustomerSelectList, getCustomerAddressList } from '@/api/admin/sales/customer'
import { getAllEmployee } from '@/api/admin/auth/org'
import { getCategoryLabelList } from '@/api/admin/product'
import {
  platformList,
  currencyList,
  orderTypeList,
  shippingMethodList,
  isWarehouseDeliveryList,
  collectedShippingCostList
} from '@/constant/yitang/sales'

const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()

const orderId = ref<number | undefined>(undefined)
const actionType = computed(() => (route.query.action as string) || '')

// 检查是否需要审批（产品层级=C，且可用库存+可用在途 < 数量 且 < 供应商起订量）
const hasLessThanMinOrder = computed(() => {
  return productList.value.some(product =>
    product.specs.some(spec => {
      const quantity = Number(spec.quantity) || 0
      const minNumber = Number(spec.minNumber) || 0
      const enabledStore = Number(spec.enabledStore) || 0
      const enabledTransit = Number(spec.enabledTransit) || 0
      const autoProductLevel = spec.autoProductLevel || ''

      // 只有产品层级是C时，才判断库存逻辑
      if (autoProductLevel === 'C') {
        const availableStock = enabledStore + enabledTransit
        return quantity > 0 && minNumber > 0 && availableStock < quantity && quantity < minNumber
      }

      return false
    })
  )
})

const isFormDisabled = computed(() => {
  return actionType.value === 'review' || actionType.value === 'approved'
})

// 人民币显示￥，美元显示$
const currencySymbol = computed(() => {
  return form.value.currency === '0' ? '￥' : '$'
})

// 汇率配置（美元兑人民币）
const exchangeRate = ref(1)

const loadExchangeRate = async () => {
  const { code, data } = await getExchangeRate({ code: 'exchangeRate' })
  if (code === 200 && Array.isArray(data) && data.length > 0) {
    exchangeRate.value = Number(data[0].value) || 1
  }
}

const customerList = ref<any[]>([])
const addressList = ref<any[]>([])
const employeeList = ref<any[]>([])
const categoryLabelMap = ref<Map<string, any[]>>(new Map())

const buttonConfig = computed(() => {
  switch (actionType.value) {
    case 'review':
      return {
        showCancel: true,
        showSaveDraft: false,
        showNext: false,
        showSubmitReview: false,
        showReviewReject: true,
        showReviewApprove: true
      }
    case 'approved':
      return {
        showCancel: true,
        showSaveDraft: true,
        showNext: true,
        showSubmitReview: false,
        showReviewReject: false,
        showReviewApprove: false
      }
    case 'rejected':
      if (hasLessThanMinOrder.value) {
        return {
          showCancel: true,
          showSaveDraft: true,
          showNext: false,
          showSubmitReview: true,
          showReviewReject: false,
          showReviewApprove: false
        }
      }
      return {
        showCancel: true,
        showSaveDraft: true,
        showNext: true,
        showSubmitReview: false,
        showReviewReject: false,
        showReviewApprove: false
      }
    case 'edit':
      if (hasLessThanMinOrder.value) {
        return {
          showCancel: true,
          showSaveDraft: true,
          showNext: false,
          showSubmitReview: true,
          showReviewReject: false,
          showReviewApprove: false
        }
      }
      return {
        showCancel: true,
        showSaveDraft: true,
        showNext: true,
        showSubmitReview: false,
        showReviewReject: false,
        showReviewApprove: false
      }
    default:
      console.log('444', hasLessThanMinOrder.value)

      if (hasLessThanMinOrder.value) {
        return {
          showCancel: true,
          showSaveDraft: true,
          showNext: false,
          showSubmitReview: true,
          showReviewReject: false,
          showReviewApprove: false
        }
      }
      return {
        showCancel: true,
        showSaveDraft: true,
        showNext: true,
        showSubmitReview: false,
        showReviewReject: false,
        showReviewApprove: false
      }
  }
})

const formRef = ref()
const orderFormRefs = ref<any[]>([])
const saveDraftLoading = ref(false)
const nextLoading = ref(false)
const submitReviewLoading = ref(false)

const form = ref({
  platform: '',
  platformOrderNo: '',
  currency: '0',
  customerId: '',
  customerName: '',
  customerAddressId: '',
  salesmanId: '',
  salesmanRate: '',
  followerId: '',
  followerRate: '',
  address: '',
  shippingMethod: '',
  orderTime: '',
  deliveryTime: '',
  discountAmount: '',
  remark: '',
  isInboundDelivery: '',
  isCollectedShippingCost: '',
  shippingCost: ''
})

const formRules = {
  platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
  platformOrderNo: [{ required: true, message: '请输入平台单号', trigger: 'blur' }],
  currency: [{ required: true, message: '请选择币种', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  customerAddressId: [{ required: true, message: '请选择收货地址', trigger: 'change' }],
  salesmanId: [{ required: true, message: '请选择业务员', trigger: 'change' }],
  salesmanRate: [{ required: true, message: '请输入业务员业绩占比', trigger: 'blur' }],
  shippingMethod: [{ required: true, message: '请选择发货形式', trigger: 'change' }],
  orderTime: [{ required: true, message: '请选择下单时间', trigger: 'change' }],
  deliveryTime: [{ required: true, message: '请选择交货时间', trigger: 'change' }],
  discountAmount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
  isInboundDelivery: [{ required: true, message: '请选择是否入库发货', trigger: 'change' }],
  isCollectedShippingCost: [{ required: true, message: '请选择运费', trigger: 'change' }],
  shippingCost: [{ required: true, message: '请输入运费金额', trigger: 'blur' }]
  // followerId: [{ required: true, message: '请选择跟进人', trigger: 'change' }],
  // followerRate: [{ required: true, message: '请输入跟进人业绩占比', trigger: 'blur' }]
}

const orderFormRules = {
  orderType: [{ required: true, message: '请选择订单类型', trigger: 'change' }]
}

const orderDetails = ref<any[]>([
  {
    orderType: '0',
    productList: [],
    tableData: []
  }
])

const currentOrderIndex = ref(0)

const currentOrder = computed(() => orderDetails.value[currentOrderIndex.value])

const productList = computed({
  get: () => currentOrder.value.productList,
  set: val => {
    currentOrder.value.productList = val
  }
})

const tableData = computed({
  get: () => currentOrder.value.tableData,
  set: val => {
    currentOrder.value.tableData = val
  }
})

const totalProductCount = computed(() => {
  return orderDetails.value.reduce((total, order) => {
    return (
      total +
      order.productList.reduce((sum, product) => {
        return sum + product.specs.reduce((specSum, spec) => specSum + (Number(spec.quantity) || 0), 0)
      }, 0)
    )
  }, 0)
})

// 预计成本（供应商总成本）- 所有订单的总成本
const estimatedCost = computed(() => {
  return orderDetails.value.reduce((total, order) => {
    if (order.orderType === '1') {
      return total
    }
    return (
      total +
      (order.productList || []).reduce((sum, product) => {
        return (
          sum +
          product.specs.reduce((specSum, spec) => {
            return specSum + (Number(spec.supplierPrice) || 0) * (Number(spec.quantity) || 0)
          }, 0)
        )
      }, 0)
    )
  }, 0)
})

const totalSalesAmount = computed(() => {
  return orderDetails.value.reduce((total, order) => {
    return (
      total +
      (order.productList || []).reduce((sum, product) => {
        return (
          sum +
          product.specs.reduce((specSum, spec) => {
            return specSum + (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0)
          }, 0)
        )
      }, 0)
    )
  }, 0)
})

// 总金额（含客户运费）= 产品销售价之和 + 已收运费 - 优惠金额
const totalAmountWithShipping = computed(() => {
  let total = totalSalesAmount.value
  if (Number(form.value.isCollectedShippingCost) === 1) {
    total += Number(form.value.shippingCost) || 0
  }
  total -= Number(form.value.discountAmount) || 0
  return total
})

// 预计毛利（按汇率换算）= 总金额（含客户运费）× 汇率 - 预估成本（人民币）
const estimatedProfit = computed(() => {
  // 如果是美元，先换算成人民币再减去成本
  if (form.value.currency !== '0') {
    return totalAmountWithShipping.value * exchangeRate.value - estimatedCost.value
  }
  // 如果是人民币，直接减去成本
  return totalAmountWithShipping.value - estimatedCost.value
})

// 预计毛利率 = 预计毛利 / (总金额 × 汇率)
const estimatedProfitRate = computed(() => {
  // 计算换算后的总金额（人民币）
  const totalInRMB =
    form.value.currency !== '0' ? totalAmountWithShipping.value * exchangeRate.value : totalAmountWithShipping.value
  if (totalInRMB === 0) return 0
  return (estimatedProfit.value / totalInRMB) * 100
})

const sortSpecsByName = (specs: any[]) => {
  return [...specs].sort((prev, next) =>
    (prev.specName || '').localeCompare(next.specName || '', 'zh-Hans-CN', {
      numeric: true,
      sensitivity: 'base'
    })
  )
}

const generateFinishedProductTableData = () => {
  const rows: any[] = []
  productList.value.forEach((product, productIndex) => {
    const activeSpecs = sortSpecsByName(product.specs.filter(spec => !spec.isDeleted))
    if (activeSpecs.length === 0) return

    const totalQuantity = activeSpecs.reduce((sum, spec) => sum + (Number(spec.quantity) || 0), 0)
    const totalSalePrice = activeSpecs.reduce(
      (sum, spec) => sum + (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0),
      0
    )

    activeSpecs.forEach((spec, activeIndex) => {
      const specIndex = product.specs.indexOf(spec)
      const specTotalPrice = (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0)
      const specSupplierCost = (Number(spec.supplierPrice) || 0) * (Number(spec.quantity) || 0)

      rows.push({
        productId: product.productId,
        productCode: product.productCode,
        productIndex,
        specIndex,
        totalQuantity,
        totalSalePrice: totalSalePrice.toFixed(2),
        totalSupplierCost: specSupplierCost.toFixed(2),
        specImage: spec.specImage,
        specName: spec.specName,
        salePrice: spec.salePrice,
        quantity: spec.quantity,
        labelId: spec.labelId,
        labelName: spec.labelName,
        specNote: spec.specNote,
        supplierName: spec.supplierName,
        supplierPrice: spec.supplierPrice,
        specTotalPrice: specTotalPrice.toFixed(2),
        minNumber: spec.minNumber,
        enabledStore: spec.enabledStore || 0,
        enabledTransit: spec.enabledTransit || 0,
        handProductLevel: spec.handProductLevel || '',
        autoProductLevel: spec.autoProductLevel || '',
        isFirstRow: activeIndex === 0,
        rowSpan: activeSpecs.length
      })
    })
  })
  return rows
}

const generateSemiFinishedTableData = () => {
  const rows: any[] = []
  productList.value.forEach((product, productIndex) => {
    const activeSpecs = sortSpecsByName(product.specs.filter(spec => !spec.isDeleted))
    if (activeSpecs.length === 0) return

    activeSpecs.forEach(spec => {
      const specIndex = product.specs.indexOf(spec)
      const specTotalPrice = (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0)

      rows.push({
        productId: product.productId,
        productCode: product.productCode,
        productIndex,
        specIndex,
        totalSalePrice: specTotalPrice.toFixed(2),
        specImage: spec.specImage,
        specName: spec.specName,
        salePrice: spec.salePrice,
        quantity: spec.quantity,
        labelId: spec.labelId,
        labelName: spec.labelName,
        specNote: spec.specNote,
        minNumber: spec.minNumber,
        enabledStore: spec.enabledStore || 0,
        enabledTransit: spec.enabledTransit || 0,
        handProductLevel: spec.handProductLevel || '',
        autoProductLevel: spec.autoProductLevel || ''
      })
    })
  })
  return rows
}

const handleAddOrder = () => {
  orderDetails.value.push({
    orderType: '0',
    productList: [],
    tableData: []
  })
}

const handleDeleteOrder = (index: number) => {
  if (orderDetails.value.length <= 1) {
    ElMessage.warning('至少保留一个订单')
    return
  }

  const order = orderDetails.value[index]
  if (order.id) {
    order.isDeleted = 1
  } else {
    orderDetails.value.splice(index, 1)
  }

  if (currentOrderIndex.value >= orderDetails.value.length) {
    currentOrderIndex.value = 0
  }
}

const handleOrderTypeChange = (index: number) => {
  const order = orderDetails.value[index]
  order.productList = []
  order.tableData = []
}

const productSpanMethod = ({ row, columnIndex }: any) => {
  const columnsToMerge = [0, 1, 2]

  if (columnsToMerge.includes(columnIndex)) {
    if (row.isFirstRow) {
      return {
        rowspan: row.rowSpan,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
}

// 审核状态下，小于起订量的行标红
const tableRowClassName = ({ row }: any) => {
  if (actionType.value === 'review') {
    const quantity = Number(row.quantity) || 0
    const minNumber = Number(row.minNumber) || 0
    const enabledStore = Number(row.enabledStore) || 0
    const enabledTransit = Number(row.enabledTransit) || 0
    const autoProductLevel = row.handProductLevel || row.autoProductLevel || ''
    if (autoProductLevel === 'C') {
      const availableStock = enabledStore + enabledTransit
      if (quantity > 0 && minNumber > 0 && availableStock < quantity && quantity < minNumber) {
        return 'row-less-than-min-order'
      }
    }
  }
  return ''
}

// 校验数值（指定小数位数）
const validateDecimal = (value: string, decimalPlaces = 2): string => {
  let result = value.replace(/[^\d.]/g, '')
  const parts = result.split('.')
  if (parts.length > 2) {
    result = parts[0] + '.' + parts.slice(1).join('')
  }
  if (parts.length === 2 && parts[1].length > decimalPlaces) {
    result = parts[0] + '.' + parts[1].substring(0, decimalPlaces)
  }
  return result
}

// 去除末尾多余的0
const formatRate = (value: number): string => {
  const fixed = value.toFixed(2)
  return parseFloat(fixed).toString()
}

const handleSalesmanRateInput = (value: string) => {
  let validatedValue = validateDecimal(value)
  const numValue = Number(validatedValue) || 0
  if (numValue > 100) {
    validatedValue = '100'
  }
  form.value.salesmanRate = validatedValue

  const salesmanRate = Number(form.value.salesmanRate) || 0
  form.value.followerRate = formatRate(100 - salesmanRate)

  nextTick(() => {
    formRef.value?.validateField('followerRate')
  })
}

const handleFollowerRateInput = (value: string) => {
  let validatedValue = validateDecimal(value)
  const numValue = Number(validatedValue) || 0
  if (numValue > 100) {
    validatedValue = '100'
  }
  form.value.followerRate = validatedValue
  const followerRate = Number(form.value.followerRate) || 0
  form.value.salesmanRate = formatRate(100 - followerRate)

  nextTick(() => {
    formRef.value?.validateField('salesmanRate')
  })
}

// 校验整数
const validateInteger = (value: string): string => {
  return value.replace(/[^\d]/g, '')
}

const updateSpec = (productIndex: number, specIndex: number, field: string, value: string) => {
  let validatedValue = value
  if (field === 'salePrice') {
    validatedValue = validateDecimal(value)
  } else if (field === 'quantity') {
    validatedValue = validateInteger(value)
    if (validatedValue) {
      const numValue = Number(validatedValue)
      if (numValue === 0) {
        validatedValue = ''
      } else if (validatedValue.length > 1 && validatedValue.startsWith('0')) {
        validatedValue = String(numValue)
      }
    }
  }

  productList.value[productIndex].specs[specIndex][field] = validatedValue

  tableData.value =
    currentOrder.value.orderType === '0' ? generateFinishedProductTableData() : generateSemiFinishedTableData()
}

// 判断是否需要审批（产品层级=C，且可用库存+可用在途 < 数量 且 < 供应商起订量）
const isLessThanMinOrder = (row: any) => {
  const quantity = Number(row.quantity) || 0
  const minNumber = Number(row.minNumber) || 0
  const enabledStore = Number(row.enabledStore) || 0
  const enabledTransit = Number(row.enabledTransit) || 0
  const autoProductLevel = row.autoProductLevel || ''

  // 只有产品层级是C时，才判断库存逻辑
  if (autoProductLevel === 'C') {
    const availableStock = enabledStore + enabledTransit
    return quantity > 0 && minNumber > 0 && availableStock < quantity && quantity < minNumber
  }

  return false
}

const handleLabelChange = (productIndex: number, specIndex: number, labelId: string) => {
  const productId = productList.value[productIndex].productId
  const labels = getCategoryLabels(productId)
  const label = labels.find(item => String(item.id) === labelId)
  productList.value[productIndex].specs[specIndex].labelId = labelId || ''
  productList.value[productIndex].specs[specIndex].labelName = label?.value || ''

  tableData.value =
    currentOrder.value.orderType === '0' ? generateFinishedProductTableData() : generateSemiFinishedTableData()
}

// 批量应用到相同产品编号
const applyToSameProduct = (productCode: string, field: string, value: string, labelName?: string) => {
  if (value == '') {
    return ElMessage.warning('请输入内容')
  }
  let appliedCount = 0

  productList.value.forEach((product, productIndex) => {
    if (product.productCode === productCode) {
      product.specs.forEach((spec: any, specIndex: number) => {
        if (!spec.isDeleted) {
          if (field === 'labelId') {
            productList.value[productIndex].specs[specIndex].labelId = value
            productList.value[productIndex].specs[specIndex].labelName = labelName || ''
          } else {
            productList.value[productIndex].specs[specIndex][field] = value
          }
          appliedCount++
        }
      })
    }
  })

  tableData.value =
    currentOrder.value.orderType === '0' ? generateFinishedProductTableData() : generateSemiFinishedTableData()

  ElMessage.success(`已批量应用到 ${appliedCount} 个规格`)
}

const addProduct = (index?: number) => {
  if (index !== undefined) {
    currentOrderIndex.value = index
  }

  if (!form.value.customerId) return ElMessage.warning('请先选择客户')
  const params = {
    id: 'productSelector',
    el: '#app',
    data: {
      orderType: currentOrder.value.orderType === '0' ? 'finished' : 'semi-finished',
      tabMode: currentOrder.value.orderType === '0' ? 'both' : 'product',
      customerId: form.value.customerId ?? null,
      bindCustomerId: form.value.customerId ?? null,
      isCreateSpecification: true,
      dialogOrigin: 'order-add',
      callback: (selectedProducts: any[]) => {
        console.log('选中的产品', selectedProducts)

        selectedProducts.forEach(product => {
          if (product.type === 'semi-product') {
            // 半成品单：添加产品
            const productId = String(product.productInfo?.id || '')
            const productCode = product.productInfo?.code || ''
            const existingProductIndex = productList.value.findIndex(p => p.productId === productId)
            const productImage = product.productInfo?.productImage?.[0]?.url || ''

            const defaultSpec = {
              specificationId: '',
              supplierId: '',
              specImage: productImage,
              specName: productCode || '',
              salePrice: '',
              quantity: '',
              labelId: '',
              labelName: '',
              specNote: '',
              supplierName: '',
              supplierPrice: '',
              minNumber: 0
            }

            if (existingProductIndex > -1) {
              productList.value[existingProductIndex].specs.push(defaultSpec)
            } else {
              productList.value.push({
                productId: productId,
                productCode: productCode,
                specs: [defaultSpec]
              })
            }

            if (productId) {
              loadCategoryLabelList(productId)
            }
          } else if (product.type === 'combination' && product.combinationProduct) {
            console.log('处理组合产品:', product.combinationProduct)

            const productId = String(product.combinationProduct.productId || '')
            const productCode = product.combinationProduct.productCode || ''
            const existingProductIndex = productList.value.findIndex(p => p.productId === productId)

            const specName =
              product.combinationProduct.productSpecificationItemList
                ?.map((item: any) => item.categorySpecificationItemValue || '')
                .filter((v: string) => v)
                .join('-') || ''

            const specImage = product.combinationProduct.imageList?.[0]?.url || ''
            console.log('规格名称:', specName, '规格图片:', specImage)

            const supplierPrice = product.combinationProduct.specificationSupplier?.supplierPrice || 0
            const minNumber = product.combinationProduct.specificationSupplier?.minNumber || 0
            const supplierName = product.combinationProduct.specificationSupplier?.supplierName || ''

            const convertedSpecs = product.specs.map(() => ({
              specificationId: product.combinationProduct.productSpecificationId || '',
              supplierId: product.combinationProduct.supplierId || '',
              specImage: specImage,
              specName: specName,
              salePrice: '',
              quantity: product.combinationProduct.number || '',
              labelId: '',
              labelName: '',
              specNote: '',
              supplierName: supplierName,
              supplierPrice: supplierPrice,
              minNumber: minNumber
            }))

            console.log('转换后的规格:', convertedSpecs)

            if (existingProductIndex > -1) {
              productList.value[existingProductIndex].specs.push(...convertedSpecs)
            } else {
              productList.value.push({
                productId: productId,
                productCode: productCode,
                specs: convertedSpecs
              })
            }

            if (productId) {
              loadCategoryLabelList(productId)
            }
          } else {
            const productId = String(product.productInfo?.id || '')
            const productCode = product.productInfo?.code || ''
            const existingProductIndex = productList.value.findIndex(p => p.productId === productId)
            let convertedSpecs: any[] = []

            if (product.type === 'product') {
              convertedSpecs = product.specs.map((spec: any) => {
                const selectedSupplier =
                  spec.supplierList?.find((s: any) => s.supplierId === spec.supplierId) || spec.supplierList?.[0]
                const supplierId = selectedSupplier?.supplierId || ''
                const supplierName = selectedSupplier?.supplierName || ''
                const supplierPrice = selectedSupplier?.supplierPrice ?? ''
                const minNumber = selectedSupplier?.minNumber || 0
                const customerEnableStore = spec?.customerEnableStore
                const customerEnableTransit = spec?.customerEnableTransit
                let enableStore = spec?.stoYtStore?.enableStore || 0
                let enableTransit = spec?.stoYtStore?.enableTransit || 0
                if (customerEnableStore !== null && customerEnableTransit !== null) {
                  enableStore = customerEnableStore
                  enableTransit = customerEnableTransit
                }

                return {
                  specificationId: spec.id || '',
                  supplierId: supplierId,
                  specImage: spec.image || '',
                  specName: spec.name || '',
                  salePrice: '',
                  quantity: '',
                  labelId: '',
                  labelName: '',
                  specNote: '',
                  supplierName: supplierName,
                  supplierPrice: supplierPrice,
                  minNumber: minNumber,
                  enabledStore: enableStore,
                  enabledTransit: enableTransit,
                  handProductLevel: spec.handProductLevel || '',
                  autoProductLevel: spec.autoProductLevel || ''
                }
              })
            } else if (product.type === 'combination') {
              convertedSpecs = product.specs.map((spec: any) => {
                const customerEnableStore = spec?.customerEnableStore
                const customerEnableTransit = spec?.customerEnableTransit
                let enableStore = spec?.stoYtStore?.enableStore || 0
                let enableTransit = spec?.stoYtStore?.enableTransit || 0
                if (customerEnableStore !== null && customerEnableTransit !== null) {
                  enableStore = customerEnableStore
                  enableTransit = customerEnableTransit
                }
                return {
                  specificationId: spec.id || '',
                  supplierId: spec.supplierId || spec.supplierList?.[0]?.supplierId || '',
                  specImage: spec.image || '',
                  specName: spec.name || '',
                  salePrice: '',
                  quantity: '',
                  labelId: '',
                  labelName: '',
                  specNote: '',
                  supplierName:
                    spec.supplierList?.find((s: any) => s.supplierId === spec.supplierId)?.supplierName ||
                    spec.supplierList?.[0]?.supplierName ||
                    '',
                  supplierPrice:
                    spec.supplierList?.find((s: any) => s.supplierId === spec.supplierId)?.supplierPrice ?? '',
                  minNumber:
                    spec.supplierList?.find((s: any) => s.supplierId === spec.supplierId)?.minNumber ||
                    spec.supplierList?.[0]?.minNumber ||
                    0,
                  enabledStore: enableStore,
                  enabledTransit: enableTransit,
                  handProductLevel: spec.handProductLevel || '',
                  autoProductLevel: spec.autoProductLevel || ''
                }
              })
            }

            if (existingProductIndex > -1) {
              productList.value[existingProductIndex].specs.push(...convertedSpecs)
            } else {
              productList.value.push({
                productId: productId,
                productCode: productCode,
                specs: convertedSpecs
              })
            }

            if (productId) {
              loadCategoryLabelList(productId)
            }
          }
        })

        tableData.value =
          currentOrder.value.orderType === '0' ? generateFinishedProductTableData() : generateSemiFinishedTableData()
      }
    },
    render: ProductSelector
  }
  dynamic.show(params)
}

const removeRow = (rowIndex: number, orderIndex: number) => {
  const order = orderDetails.value[orderIndex]
  const row = order.tableData[rowIndex]
  const product = order.productList[row.productIndex]
  const spec = product.specs[row.specIndex]

  if (spec.id) {
    spec.isDeleted = 1
  } else {
    if (product.specs.length === 1) {
      order.productList.splice(row.productIndex, 1)
    } else {
      product.specs.splice(row.specIndex, 1)
    }
  }

  const newTableData =
    order.orderType === '0'
      ? generateTableDataForOrder(order.productList)
      : generateSemiFinishedDataForOrder(order.productList)

  order.tableData.splice(0, order.tableData.length, ...newTableData)
}

const handleCancel = () => {
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.back()
}

const loadOrderDetail = async () => {
  const id = route.query.id
  const { code, data, message } = await getOrderDetail({ id })
  if (code !== 200) return ElMessage.warning(message)
  const order = data || {}

  orderId.value = order.id
  form.value.platform = order.sourcePlatform ? String(order.sourcePlatform) : ''
  form.value.platformOrderNo = order.platformOrderCode ?? ''
  form.value.currency = order.currency ? String(order.currency) : ''
  form.value.customerId = order.customerId ?? ''
  form.value.customerName = order.customerName ?? ''

  if (order.customerId && order.customerName) {
    const exists = customerList.value.some((c: any) => c.id === order.customerId)
    if (!exists) {
      customerList.value.unshift({ id: order.customerId, name: order.customerName })
    }
  }

  form.value.customerAddressId = order.customerAddressId ?? ''
  form.value.address = order.customerAddress ?? ''
  if (order.saleEmployeeId == '0' || order.saleEmployeeId == null) {
    form.value.salesmanId = ''
  } else {
    form.value.salesmanId = order.saleEmployeeId === '0' ? '' : order.saleEmployeeId
  }
  form.value.salesmanRate = order.saleRatio ?? ''
  if (order.followEmployeeId == '0' || order.followEmployeeId == null) {
    form.value.followerId = ''
  } else {
    form.value.followerId = order.followEmployeeId
  }
  form.value.followerRate = order.followRatio ?? ''

  form.value.shippingMethod = order.shippingMethod ? String(order.shippingMethod) : ''
  form.value.orderTime = order.orderTime ?? ''
  form.value.deliveryTime = order.deliveryTime ?? ''
  form.value.discountAmount = order.discountAmount ?? ''
  form.value.remark = order.remark ?? ''
  form.value.isInboundDelivery = order.isInboundDelivery ? '1' : '0'
  form.value.isCollectedShippingCost = order.isCollectedShippingCost || 0
  form.value.shippingCost = order.shippingCost ?? ''

  if (form.value.customerId) {
    await loadCustomerAddressList(form.value.customerId)
  }

  if (order.subOrderList && Array.isArray(order.subOrderList) && order.subOrderList.length > 0) {
    orderDetails.value = order.subOrderList.map(subOrder => {
      const orderType = subOrder.orderType ? String(subOrder.orderType) : '0'

      const orderDetail: any = {
        id: subOrder.id,
        orderType,
        productList: [],
        tableData: []
      }

      if (subOrder.itemList && Array.isArray(subOrder.itemList)) {
        const productMap = new Map<string, any[]>()
        subOrder.itemList.forEach((item: any) => {
          const productId = String(item.productId || '')
          if (!productMap.has(productId)) {
            productMap.set(productId, [])
          }

          const specName =
            item.itemList
              ?.map((spec: any) => spec.categorySpecificationItemValue || '')
              .filter((v: string) => v)
              .join('-') || ''

          const specImage = item.imageList?.[0]?.url || ''

          productMap.get(productId)?.push({
            id: String(item.id || ''),
            specImage,
            specificationId: String(item.specificationId || ''),
            supplierId: String(item.supplierId || ''),
            specName,
            salePrice: String(item.price ?? ''),
            quantity: String(item.number || ''),
            labelId: String(item.labelId || ''),
            labelName: item.labelName || '',
            specNote: item.remark || '',
            supplierName: item.supplierName || '',
            supplierPrice: String(item.supplierPrice || ''),
            status: item.status || '', // 订单项状态
            minNumber: item.minNumber || 0, // 供应商起订量
            enabledStore: item.enabledStore || 0, // 可用库存
            enabledTransit: item.enabledTransit || 0, // 可用在途
            handProductLevel: item.handProductLevel || '', // 手动产品层级
            autoProductLevel: item.autoProductLevel || '' // 自动产品层级
          })
        })

        const productList = Array.from(productMap.entries()).map(([productId, specs]) => {
          const firstItem = subOrder.itemList?.find((item: any) => String(item.productId) === productId)
          return {
            productId,
            productCode: firstItem?.productCode || '',
            specs
          }
        })

        orderDetail.productList = productList
        orderDetail.tableData =
          orderDetail.orderType === '0'
            ? generateTableDataForOrder(productList)
            : generateSemiFinishedDataForOrder(productList)

        productList.forEach(product => {
          if (product.productId) {
            loadCategoryLabelList(product.productId)
          }
        })
      }

      return orderDetail
    })
  }
}

const generateTableDataForOrder = (productList: any[]) => {
  const rows: any[] = []
  productList.forEach((product, productIndex) => {
    const activeSpecs = sortSpecsByName(product.specs.filter((spec: any) => spec.isDeleted != 1))
    const totalQuantity = activeSpecs.reduce((sum: number, spec: any) => sum + (Number(spec.quantity) || 0), 0)
    const totalSalePrice = activeSpecs.reduce(
      (sum: number, spec: any) => sum + (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0),
      0
    )

    activeSpecs.forEach((spec: any, specIndex: number) => {
      const specTotalPrice = (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0)
      const specSupplierCost = (Number(spec.supplierPrice) || 0) * (Number(spec.quantity) || 0)

      rows.push({
        productId: product.productId,
        productCode: product.productCode,
        productIndex,
        specIndex: product.specs.indexOf(spec),
        totalQuantity,
        totalSalePrice: totalSalePrice.toFixed(2),
        totalSupplierCost: specSupplierCost.toFixed(2),
        ...spec,
        specTotalPrice: specTotalPrice.toFixed(2),
        isFirstRow: specIndex === 0,
        rowSpan: activeSpecs.length
      })
    })
  })
  return rows
}

const generateSemiFinishedDataForOrder = (productList: any[]) => {
  const rows: any[] = []
  productList.forEach((product, productIndex) => {
    const activeSpecs = sortSpecsByName(product.specs.filter((spec: any) => spec.isDeleted !== 1))

    activeSpecs.forEach((spec: any) => {
      const totalSalePrice = (Number(spec.salePrice) || 0) * (Number(spec.quantity) || 0)
      rows.push({
        productId: product.productId,
        productCode: product.productCode,
        productIndex,
        specIndex: product.specs.indexOf(spec),
        totalSalePrice: totalSalePrice.toFixed(2),
        ...spec
      })
    })
  })
  return rows
}

// 加载客户列表
const loadCustomerList = async () => {
  const { code, data, message } = await getCustomerSelectList({
    code: '',
    belongEmployeeId: '',
    customerName: ''
  })
  if (code !== 200) return ElMessage.warning(message)
  customerList.value = data || []
}

// 加载员工列表
const loadEmployeeList = async () => {
  const { code, data, message } = await getAllEmployee({})
  if (code !== 200) return ElMessage.warning(message)
  employeeList.value = data || []
}

const loadCustomerAddressList = async (customerId: string) => {
  if (!customerId) {
    addressList.value = []
    return
  }
  const { code, data, message } = await getCustomerAddressList({ id: customerId })
  if (code !== 200) return ElMessage.warning(message)
  addressList.value = (data || []).map((addr: any) => {
    const address = [addr.province, addr.city, addr.county, addr.detail].filter(val => val && val !== '0').join('')
    const prefix = addr.consignee && addr.consignee !== '-' ? `(${addr.consignee}) ${addr.phone} ` : ''
    return {
      ...addr,
      fullAddress: prefix + address
    }
  })
}

// 加载产品定制化属性列表
const loadCategoryLabelList = async (productId: string | number) => {
  const { code, data, message } = await getCategoryLabelList({ productId })
  if (code !== 200) return ElMessage.warning(message)
  categoryLabelMap.value.set(String(productId), data || [])
}

const getCategoryLabels = (productId: string) => {
  return categoryLabelMap.value.get(productId) || []
}

const handleCustomerChange = async (customerId: string) => {
  form.value.customerAddressId = ''
  form.value.address = ''
  await loadCustomerAddressList(customerId)
  if (addressList.value.length > 0 && addressList.value[0].orderDefaultRemark) {
    form.value.remark = addressList.value[0].orderDefaultRemark
  } else {
    form.value.remark = ''
  }
  // 根据客户带出业务员和跟进人
  const selectedCustomer = customerList.value.find(c => c.id == customerId)
  if (selectedCustomer) {
    form.value.salesmanId = selectedCustomer.belongEmployeeId || ''
    form.value.followerId = selectedCustomer.followEmployeeId || ''
    if (!route.query.id) {
      form.value.salesmanRate = '100'
      form.value.followerRate = '0'
    }
  }
}

const handleAddressChange = (addressId: number) => {
  const selectedAddress = addressList.value.find(addr => addr.id === addressId)
  if (selectedAddress) {
    form.value.address = selectedAddress.fullAddress || ''
  }
}

const saveOrder = async (status: number) => {
  const orderSubList = orderDetails.value.map(order => {
    const itemList = order.productList.flatMap(product => {
      return product.specs.map(spec => {
        const item: any = {
          productId: Number(product.productId) || null,
          specificationId: order.orderType === '1' ? null : Number(spec.specificationId) || null,
          price: Number(spec.salePrice) || 0,
          number: Number(spec.quantity) || 0,
          labelId: Number(spec.labelId) || '',
          labelName: spec.labelName ?? '',
          remark: spec.specNote ?? '',
          supplierId: Number(spec.supplierId) ?? '',
          supplierPrice: Number(spec.supplierPrice) || 0
        }
        if (spec.id) {
          item.id = Number(spec.id) || undefined
        }
        if (spec.status !== undefined) {
          item.status = spec.status
        }
        if (spec.isDeleted) {
          item.isDeleted = 1
        }
        return item
      })
    })

    const subOrder: any = {
      id: order.id,
      orderType: order.orderType,
      itemList
    }
    if (order.isDeleted) {
      subOrder.isDeleted = 1
    }
    return subOrder
  })

  let orderData = {
    id: orderId.value,
    sourcePlatform: form.value.platform,
    platformOrderCode: form.value.platformOrderNo,
    currency: form.value.currency,
    customerId: Number(form.value.customerId) || '',
    customerAddressId: form.value.customerAddressId,
    followEmployeeId: Number(form.value.followerId) || '',
    followRatio: Number(form.value.followerRate) || 0,
    saleEmployeeId: Number(form.value.salesmanId) || '',
    saleRatio: Number(form.value.salesmanRate) || 0,
    shippingMethod: form.value.shippingMethod,
    orderTime: form.value.orderTime,
    deliveryTime: form.value.deliveryTime,
    discountAmount: Number(form.value.discountAmount) || 0,
    remark: form.value.remark,
    isInboundDelivery: Number(form.value.isInboundDelivery),
    isCollectedShippingCost: form.value.isCollectedShippingCost,
    shippingCost: Number(form.value.shippingCost) || 0,
    status,
    orderSubList
  }
  // return console.log('orderData', orderData)
  // eslint-disable-next-line no-unreachable
  const { code, message, data } = await saveOrUpdateOrder(orderData)
  if (code !== 200) {
    ElMessage.warning(message)
    return null
  }
  return data.id
}

const handleSaveDraft = async () => {
  if (!(await validateForm())) {
    return
  }
  saveDraftLoading.value = true
  try {
    const id = await saveOrder(3) // 3为暂存
    if (id) {
      ElMessage.success('操作成功')
      tagsStore.delVisitedView(route)
      tagsStore.delCachedView(route)
      router.push('/sales/order')
    }
  } finally {
    saveDraftLoading.value = false
  }
}

const validateForm = async (): Promise<boolean> => {
  try {
    await formRef.value?.validate()
  } catch (error) {
    return false
  }

  if (orderDetails.value.length === 0) {
    ElMessage.warning('请至少添加一个订单')
    return false
  }

  for (let i = 0; i < orderDetails.value.length; i++) {
    const orderFormRef = orderFormRefs.value[i]
    if (orderFormRef) {
      try {
        await orderFormRef.validate()
      } catch (error) {
        return false
      }
    }

    const order = orderDetails.value[i]
    const orderNum = i + 1

    if (!order.productList || order.productList.length === 0) {
      ElMessage.warning(`订单${orderNum}：请至少添加一个产品`)
      return false
    }

    for (let j = 0; j < order.productList.length; j++) {
      const product = order.productList[j]
      for (let k = 0; k < product.specs.length; k++) {
        const spec = product.specs[k]
        if (!spec.salePrice && spec.salePrice !== '0') {
          ElMessage.warning(`订单详情${orderNum}：产品 ${product.productCode} 的销售单价不能为空`)
          return false
        }
        if (!spec.quantity || spec.quantity === '0') {
          ElMessage.warning(`订单详情${orderNum}：产品 ${product.productCode} 的数量不能为空或0`)
          return false
        }
      }
    }
  }

  return true
}

const handleNext = async () => {
  if (!(await validateForm())) {
    return
  }

  // 如果是审核通过或审核拒绝状态，直接跳转
  if (actionType.value === 'approved' || actionType.value === 'rejected') {
    tagsStore.delVisitedView(route)
    tagsStore.delCachedView(route)
    router.push({
      path: '/sales/order/purchase',
      query: {
        id: orderId.value,
        action: actionType.value
      }
    })
    return
  }

  nextLoading.value = true
  try {
    const id = await saveOrder(3)
    if (id) {
      tagsStore.delVisitedView(route)
      tagsStore.delCachedView(route)
      router.push({
        path: '/sales/order/purchase',
        query: {
          id: id,
          action: actionType.value
        }
      })
    }
  } finally {
    nextLoading.value = false
  }
}

const handleSubmitReview = async () => {
  if (!(await validateForm())) {
    return
  }
  submitReviewLoading.value = true
  try {
    // 小于起订量的订单提交审核，状态为 1
    const id = await saveOrder(1)
    if (id) {
      ElMessage.success('操作成功')
      tagsStore.delVisitedView(route)
      tagsStore.delCachedView(route)
      router.push('/sales/order')
    }
  } finally {
    submitReviewLoading.value = false
  }
}

const handleReviewReject = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝理由', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputValidator: (val: string) => {
        return val ? true : '请输入拒绝理由'
      }
    })

    const { code, message } = await auditOrder({
      id: orderId.value,
      status: 2, // 2拒绝
      auditOpinion: value
    })

    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
    tagsStore.delVisitedView(route)
    tagsStore.delCachedView(route)
    router.push('/sales/order')
  } catch (error) {
    // 用户取消
  }
}

const handleReviewApprove = async () => {
  await ElMessageBox.confirm('确认审核通过该订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await auditOrder({
    id: orderId.value,
    status: 4 // 4通过
  })

  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/sales/order')
}

onMounted(async () => {
  await loadEmployeeList()
  await loadCustomerList()
  loadExchangeRate()
  if (route.query.id) {
    loadOrderDetail()
  }
})

onActivated(() => {
  if (route.query.id) {
    loadOrderDetail()
  }
})
</script>

<style scoped lang="scss">
.order-add-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 60px);
  padding-bottom: 100px;

  .page-title {
    font-size: 20px;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 15px;
    border-bottom: 1px solid #e5e5e5;
  }

  .sale-price-input {
    :deep(.el-input-group__append) {
      padding: 0;
      font-size: 13px;
    }
    :deep(.el-button) {
      color: #0c67ff;
    }
  }

  .quantity-input {
    :deep(.el-input-group__append) {
      padding: 0;
      font-size: 13px;
    }
    :deep(.el-button) {
      color: #0c67ff;
    }
  }

  .form-section {
    margin-bottom: 15px;

    .section-title {
      font-size: 16px;
      font-weight: bold;
      padding-left: 12px;
      margin-bottom: 20px;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: #409eff;
        border-radius: 2px;
      }
    }

    .order-form {
      :deep(.el-form-item) {
        margin-bottom: 18px;
      }

      :deep(.el-select),
      :deep(.el-input) {
        width: 100%;
      }
    }
  }

  .order-detail-card {
    margin-bottom: 20px;
    padding: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 3px;
    background: #fafafa;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .section-title {
        font-size: 16px;
        font-weight: bold;
        padding-left: 12px;
        margin-bottom: 0;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 16px;
          background-color: #409eff;
          border-radius: 2px;
        }
      }
    }

    .order-form {
      :deep(.el-form-item) {
        margin-bottom: 18px;
      }

      :deep(.el-select),
      :deep(.el-input) {
        width: 100%;
      }
    }
  }

  .add-order-btn-wrapper {
    text-align: center;
    margin-bottom: 30px;
    .el-button {
      width: 100%;
    }
  }

  .product-section {
    margin-bottom: 30px;

    .add-product-btn {
      text-align: center;
      border: 1px dashed #dcdfe6;
      margin-top: 15px;
      cursor: pointer;

      &:hover {
        border-color: #409eff;
      }
    }
  }

  .footer-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .summary-info {
      display: flex;
      gap: 30px;
      font-size: 14px;

      span {
        color: #333;

        strong {
          color: #333;
          font-weight: 600;
        }
      }
    }
  }
}

:deep(.min-order-warning) {
  .el-message-box__header {
    padding: 15px 20px;
    background-color: #fef0f0;
    border-bottom: 1px solid #fde2e2;

    .el-message-box__title {
      color: #f56c6c;
      font-weight: 600;
    }
  }

  .el-message-box__content {
    padding: 20px;
  }

  .min-order-message {
    .min-order-row {
      display: flex;
      align-items: center;
      font-size: 14px;
      color: #606266;
    }
  }
}

:deep(.row-less-than-min-order) {
  background-color: #f8d0d5 !important;

  &:hover > td {
    background-color: #f8d0d5 !important;
  }

  td {
    background-color: #f8d0d5 !important;
  }
}

.quantity-cell {
  display: flex;
  flex-direction: column;
  align-items: center;

  .min-order-tip {
    color: #f56c6c;
    font-size: 12px;
    margin-top: 4px;
    white-space: nowrap;
  }
}
</style>
