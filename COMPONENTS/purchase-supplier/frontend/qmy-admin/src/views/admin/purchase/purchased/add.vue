<template>
  <div class="purchase-add-container">
    <h2 class="page-title">{{ isEdit ? '编辑采购单' : '直接采购' }}</h2>

    <div class="form-section">
      <div class="section-title">采购商详情</div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="120px"
        label-position="top"
        class="purchase-form"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="供应商名称" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择" :disabled="form.supplierDisabled" filterable>
                <el-option
                  v-for="supplier in supplierList"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="1688单号" prop="platformCode" :required="form.paymentMethod === '2'">
              <el-input v-model="form.platformCode" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="运费" prop="shippingFee">
              <el-input
                v-model="form.shippingFee"
                placeholder="请输入"
                @input="(val: string) => (form.shippingFee = validateDecimal(val))"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠金额" prop="discountAmount">
              <el-input
                v-model="form.discountAmount"
                placeholder="请输入"
                @input="(val: string) => (form.discountAmount = validateDecimal(val))"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="交货时间" prop="deliveryTime">
              <el-date-picker
                v-model="form.deliveryTime"
                type="date"
                placeholder="请选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款方式" prop="paymentMethod" @change="handlePaymentMethodChange">
              <el-select v-model="form.paymentMethod" placeholder="请选择">
                <el-option
                  v-for="item in paymentMethodList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款形式" prop="paymentForm">
              <el-input v-model="form.paymentForm" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="采购单备注" prop="purchaseNote">
              <el-input v-model="form.purchaseNote" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="绑定客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择" filterable>
                <el-option
                  v-for="customer in customerList"
                  :key="customer.id"
                  :label="customer.name"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row> -->
      </el-form>
    </div>

    <div class="product-section">
      <el-table :data="visibleProductList" border style="width: 100%" :span-method="productSpanMethod">
        <el-table-column label="产品ID" prop="productCode" min-width="120" align="center" />

        <el-table-column label="规格名称" prop="specName" min-width="150" align="center">
          <template #default="{ row }">
            <span v-if="row.isSemiFinished">半成品</span>
            <div v-else-if="row.specName" style="display: inline-flex; align-items: center; gap: 4px">
              <span>{{ row.specName }}</span>
              <el-tag v-if="row.handProductLevel" size="small" type="warning">{{ row.handProductLevel }}</el-tag>
            </div>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>

        <el-table-column label="规格图片" min-width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.isSemiFinished">半成品</span>
            <el-image
              v-else-if="row.image || row.specImage"
              :src="row.image || row.specImage"
              style="width: 50px; height: 50px; cursor: pointer"
              fit="cover"
              @click="handleImagePreview(row.image || row.specImage)"
            />
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>

        <el-table-column label="申购数量" min-width="90" align="center">
          <template #default="{ row }">
            <span>{{ row.quantity || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="供应商规格" prop="supplierSpec" min-width="150" align="center" />

        <el-table-column label="采购数量" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.purchaseQuantity"
              placeholder="请输入"
              @input="(val: string) => (row.purchaseQuantity = validateInteger(val))"
            />
          </template>
        </el-table-column>

        <el-table-column label="供应商单价" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.supplierPrice"
              placeholder="请输入"
              @input="(val: string) => (row.supplierPrice = validateDecimal(val))"
            />
          </template>
        </el-table-column>

        <el-table-column label="起订量" min-width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.minOrder || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="订单规格备注" min-width="200" align="center">
          <template #default="{ row }">
            <div class="remark-cell">
              <span>{{ row.orderRemark || '-' }}</span>
              <template v-if="row.orderNote && row.orderNote !== '-'">
                <el-divider style="margin: 4px 0" />
                <span class="order-note">{{ row.orderNote }}</span>
              </template>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="采购规格备注" min-width="200" align="center">
          <template #default="{ row }">
            <el-input v-model="row.purchaseRemark" type="textarea" :rows="3" placeholder="请输入" />
          </template>
        </el-table-column>

        <el-table-column label="定制化属性" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.categoryLabelName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="客户名称" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.customerName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="所属订单编号" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.orderCode || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="handleRemove($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="add-product-btn" @click="handleAddProduct">
        <el-button type="text">+ 请选择产品</el-button>
      </div>
    </div>

    <footer-actions>
      <div class="summary-info">
        <span class="stat-item">
          采购金额：
          <strong>¥{{ totalAmount.toFixed(2) }}</strong>
        </span>
        <span class="stat-item">
          采购数量：
          <strong>{{ totalQuantity }}件</strong>
        </span>
      </div>
      <div class="action-buttons">
        <el-button @click="handleCancel">取消</el-button>
        <!-- prettier-ignore -->
        <el-button v-if="isEdit && visibleProductList.length === 0" type="danger" @click="handleDeletePurchase">删除采购单</el-button>
        <el-button v-else @click="handleSaveDraft">暂存</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </footer-actions>

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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import FooterActions from '@/components/footer-actions/index.vue'
import ProductSelector from '@/components/product-selector/index.vue'
import { createOrUpdatePurchase, getPurchaseDetail, deletePurchase } from '@/api/admin/purchase/purchased'
import { getSupplierSelect } from '@/api/admin/product'
import { getCustomerList } from '@/api/admin/sales/customer'
import { paymentMethodList } from '@/constant/yitang/purchase'
import { validateDecimal, validateInteger } from '@/utils/validate'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()
const formRef = ref()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const isEdit = computed(() => !!route.query.id)
const purchaseId = computed(() => route.query.id as string)

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const form = ref({
  supplierId: '',
  platformCode: '',
  shippingFee: '',
  discountAmount: '',
  deliveryTime: '',
  paymentMethod: '',
  paymentForm: '',
  purchaseNote: '',
  customerId: '',
  supplierDisabled: false
})

const formRules = {
  supplierId: [{ required: true, message: '请选择供应商名称', trigger: 'change' }],
  platformCode: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (form.value.paymentMethod === '2' && !value) {
          callback(new Error('请输入1688单号'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  shippingFee: [{ required: true, message: '请输入运费', trigger: 'blur' }],
  discountAmount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
  deliveryTime: [{ required: true, message: '请选择交货时间', trigger: 'change' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', trigger: 'change' }]
  // customerId: [{ required: true, message: '请选择绑定客户', trigger: 'change' }]
}

const supplierList = ref<any[]>([])

const loadSupplierList = async () => {
  const { code, data, message } = await getSupplierSelect()
  if (code !== 200) return ElMessage.warning(message)
  supplierList.value = data ?? []
}

loadSupplierList()

const customerList = ref<any[]>([])

const loadCustomerList = async () => {
  const { code, data, message } = await getCustomerList({
    pageNum: 1,
    pageSize: 1000
  })
  if (code !== 200) return ElMessage.warning(message)
  customerList.value = data?.list || []
}

onMounted(() => {
  loadCustomerList()
  if (isEdit.value) {
    loadPurchaseDetail()
  }
})

const handlePaymentMethodChange = () => {
  formRef.value?.validateField('platformCode')
}

// 加载采购单详情
const loadPurchaseDetail = async () => {
  const { code, data, message } = await getPurchaseDetail(purchaseId.value)
  if (code !== 200) return ElMessage.warning(message)

  const purchase = data?.purchase || {}
  const items = data?.items || []
  const isApplyPurchase = data?.isApplyPurchase || false
  form.value.supplierId = purchase.supplierId || ''
  form.value.platformCode = purchase.orderPlatformCode || ''
  form.value.shippingFee = String(purchase.shippingCost || '0')
  form.value.discountAmount = String(purchase.discountAmount || '0')
  form.value.deliveryTime = purchase.deliveryTime ? dayjs(purchase.deliveryTime).format('YYYY-MM-DD') : ''
  form.value.paymentMethod = purchase.payMethod || ''
  form.value.paymentForm = purchase.payWay || ''
  form.value.purchaseNote = purchase.remark || ''
  form.value.customerId = items[0]?.customerId || ''
  form.value.supplierDisabled = isApplyPurchase

  productList.value = items.map((item: any, index: number) => {
    // 规格名称：白K-全AB
    const specName =
      item.specificationItemList
        ?.map((spec: any) => spec.categorySpecificationItemValue || '')
        .filter(Boolean)
        .join('-') || '-'

    // 获取规格图片
    const image = item.specificationImageList?.[0]?.url || item.productImageList?.[0]?.url || ''
    const isSemiFinished = !item.specificationId

    return {
      id: item.id,
      productCode: item.productCode || '',
      isSemiFinished,
      specName: isSemiFinished ? '半成品' : specName,
      image: image,
      quantity: item.applyNumber || 0,
      purchaseQuantity: String(item.number || ''),
      supplierSpec: item.supplierSpecification || '-',
      supplierPrice: String(item.supplierPrice || ''),
      minOrder: item.minNumber || 0,
      purchaseRemark: item.remark || '',
      isFirstRow: index === 0,
      rowSpan: index === 0 ? items.length : 0,
      productId: item.productId,
      specificationId: item.specificationId,
      categorySpecificationItemId: item.categorySpecificationItemId,
      categorySpecificationItemName: item.categorySpecificationItemName || '',
      categoryLabelName: item.categorySpecificationItemName || '',
      applyPurchaseId: item.applyPurchaseId,
      orderSubId: item.orderSubId,
      orderSubItemId: item.orderSubItemId,
      customerId: item.customerId,
      customerName: item.customerName,
      salesEmployeeId: item.salesEmployeeId,
      orderRemark: item.orderRemark || '',
      orderNote: item.orderNote || '',
      orderCode: item.orderCode || '-',
      handProductLevel: item.handProductLevel || ''
    }
  })

  recalculateRowSpan()
}

const productList = ref<any[]>([])
const visibleProductList = computed(() => productList.value.filter((item: any) => !item.isDeleted))

const totalQuantity = computed(() => {
  return visibleProductList.value.reduce((sum, item) => sum + (Number(item.purchaseQuantity) || 0), 0)
})

// 总金额（采购数量 × 供应商单价 - 优惠金额 + 运费）
const totalAmount = computed(() => {
  const productTotal = visibleProductList.value.reduce((sum, item) => {
    const quantity = Number(item.purchaseQuantity) || 0
    const price = Number(item.supplierPrice) || 0
    return sum + quantity * price
  }, 0)
  const discount = Number(form.value.discountAmount) || 0
  const shipping = Number(form.value.shippingFee) || 0
  return productTotal - discount + shipping
})

// 合并产品ID列的单元格
const productSpanMethod = ({ row, rowIndex, columnIndex }: any) => {
  if (columnIndex === 0) {
    const list = visibleProductList.value
    const currentProductCode = row.productCode
    const firstIndex = list.findIndex((item: any) => item.productCode === currentProductCode)
    if (rowIndex === firstIndex) {
      const count = list.filter((item: any) => item.productCode === currentProductCode).length
      return { rowspan: count, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
}

const insertProduct = (item: any) => {
  const code = item.productCode
  if (code) {
    let lastIndex = -1
    productList.value.forEach((p: any, i: number) => {
      if (p.productCode === code && !p.isDeleted) lastIndex = i
    })
    if (lastIndex >= 0) {
      productList.value.splice(lastIndex + 1, 0, item)
      return
    }
  }
  productList.value.push(item)
}

const handleAddProduct = () => {
  if (!form.value.supplierId) return ElMessage.warning('请先选择供应商')
  const params = {
    id: 'productSelector',
    el: '#app',
    data: {
      orderType: 'finished', // 默认成品单
      tabMode: 'product',
      isCreateSpecification: true,
      supplierId: form.value.supplierId ?? null,
      callback: (selectedProducts: any[]) => {
        console.log('选中的产品', selectedProducts)
        // 收集已存在的规格ID
        const existingSpecIds = new Set(
          visibleProductList.value.map((item: any) => item.specificationId).filter(Boolean)
        )
        let skippedCount = 0

        selectedProducts.forEach(product => {
          if (product.type === 'product') {
            const specs = product.specs || []
            specs.forEach((spec: any) => {
              if (existingSpecIds.has(spec.id)) {
                skippedCount++
                return
              }
              const supplierInfo = spec.supplierList?.[0] || {}

              insertProduct({
                productCode: product.productInfo?.code || '',
                specName: spec.name || '',
                image: spec.image || '',
                quantity: '',
                purchaseQuantity: '',
                supplierSpec: supplierInfo.supplierSpecification || spec.name || '',
                supplierPrice: supplierInfo.supplierPrice || 0,
                minOrder: supplierInfo.minNumber || 0,
                purchaseRemark: '',
                handProductLevel: spec.handProductLevel || '',
                productId: spec.productId || product.productInfo?.id || null,
                specificationId: spec.id || null,
                categorySpecificationItemId: null,
                categorySpecificationItemName: ''
              })
            })
          } else if (product.type === 'combination' && product.combinationProduct) {
            const comboSpecId =
              product.combinationProduct.specificationId || product.combinationProduct.productSpecificationId
            if (existingSpecIds.has(comboSpecId)) {
              skippedCount++
            } else {
              // 处理组合产品
              const specName =
                product.combinationProduct.productSpecificationItemList
                  ?.map(
                    (item: any) =>
                      `${item.categorySpecificationName || ''}-${item.categorySpecificationItemValue || ''}`
                  )
                  .join(' / ') || ''

              const specImage = product.combinationProduct.imageList?.[0]?.url || ''
              const supplierInfo = product.combinationProduct.specificationSupplier || {}

              insertProduct({
                productCode: product.combinationProduct.productCode || '',
                specName: specName,
                image: specImage,
                quantity: product.combinationProduct.number || '',
                purchaseQuantity: product.combinationProduct.number || '',
                supplierSpec: specName,
                supplierPrice: supplierInfo.supplierPrice || 0,
                minOrder: supplierInfo.minNumber || 0,
                purchaseRemark: '',
                handProductLevel:
                  product.combinationProduct.handProductLevel || product.productInfo?.handProductLevel || '',
                productId: product.combinationProduct.productId || null,
                specificationId: comboSpecId || null,
                categorySpecificationItemId: null,
                categorySpecificationItemName: ''
              })
            }
          }
        })

        if (skippedCount > 0) {
          ElMessage.warning(`已自动跳过 ${skippedCount} 个已存在的规格`)
        }

        recalculateRowSpan()
      }
    },
    render: ProductSelector
  }
  dynamic.show(params)
}

const handleRemove = (index: number) => {
  const item = visibleProductList.value[index]
  if (item.id) {
    item.isDeleted = 1
  } else {
    const realIndex = productList.value.indexOf(item)
    productList.value.splice(realIndex, 1)
  }
  recalculateRowSpan()
}

// 重新计算产品ID列的行合并信息
const recalculateRowSpan = () => {
  const productCodeMap = new Map<string, number[]>()
  visibleProductList.value.forEach((item, index) => {
    const code = item.productCode
    if (!productCodeMap.has(code)) {
      productCodeMap.set(code, [])
    }
    productCodeMap.get(code)!.push(index)
  })

  visibleProductList.value.forEach((item, index) => {
    const indices = productCodeMap.get(item.productCode) || []
    item.isFirstRow = indices[0] === index
    item.rowSpan = item.isFirstRow ? indices.length : 0
  })
}

const handleCancel = () => {
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.back()
}

const buildSubmitData = (status: string) => {
  const supplier = supplierList.value.find((s: any) => s.id === form.value.supplierId)
  return {
    id: isEdit.value ? purchaseId.value : undefined,
    deliveryTime: dayjs(form.value.deliveryTime).format('YYYY-MM-DD HH:mm:ss') || '',
    discountAmount: Number(form.value.discountAmount) || 0,
    isInboundDelivery: true, // true入库发货 false供应商发货
    orderPlatformCode: form.value.platformCode || '',
    payMethod: form.value.paymentMethod || '',
    payWay: form.value.paymentForm || '',
    remark: form.value.purchaseNote || '',
    shippingCost: Number(form.value.shippingFee) || 0,
    status,
    supplierId: Number(form.value.supplierId) || 0,
    supplierName: supplier?.name || '',
    totalAmount: totalAmount.value,
    itemList: productList.value.map((item: any) => ({
      id: item.id || undefined,
      productId: item.productId || null,
      customerId: item.customerId || null,
      customerName: item.customerName || null,
      salesEmployeeId: item.salesEmployeeId || null,
      applyPurchaseId: item.applyPurchaseId || null,
      specificationId: item.specificationId || null,
      orderSubId: item.orderSubId || null,
      orderSubItemId: item.orderSubItemId || null,
      categorySpecificationItemId: item.categorySpecificationItemId || null,
      categorySpecificationItemName: item.categorySpecificationItemName || '',
      number: Number(item.purchaseQuantity) || 0,
      supplierPrice: Number(item.supplierPrice) || 0,
      remark: item.purchaseRemark || '',
      orderRemark: item.orderRemark || '',
      isDeleted: item.isDeleted || undefined
    }))
  }
}

const validateProductList = () => {
  if (visibleProductList.value.length === 0) {
    ElMessage.warning('请至少添加一个产品')
    return false
  }
  for (let i = 0; i < visibleProductList.value.length; i++) {
    const item = visibleProductList.value[i]
    if (!item.purchaseQuantity) {
      ElMessage.warning(`第${i + 1}行采购数量不能为空`)
      return false
    }
    if (!item.supplierPrice && item.supplierPrice !== 0) {
      ElMessage.warning(`第${i + 1}行供应商单价不能为空`)
      return false
    }
  }
  return true
}

const handleDeletePurchase = async () => {
  await ElMessageBox.confirm('该采购单将被删除，关联的申购记录将恢复到待采购列表', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deletePurchase({ id: purchaseId.value })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/purchase/purchased')
}

const handleSaveDraft = async () => {
  await formRef.value?.validate()
  if (!validateProductList()) return

  const submitData = buildSubmitData('0')
  // return console.log('handleSaveDraft', submitData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await createOrUpdatePurchase(submitData)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('暂存成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/purchase/purchased')
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  if (!validateProductList()) return

  const submitData = buildSubmitData('1')
  // return console.log('submitData', submitData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await createOrUpdatePurchase(submitData)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('提交成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/purchase/purchased')
}
</script>

<style scoped lang="scss">
.remark-cell {
  display: flex;
  flex-direction: column;
  align-items: center;

  .order-note {
    color: #909399;
    font-size: 12px;
  }
}

.purchase-add-container {
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
    color: #303133;
  }

  .form-section {
    margin-bottom: 20px;

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
        background: #409eff;
        border-radius: 2px;
      }
    }

    .purchase-form {
      :deep(.el-form-item) {
        margin-bottom: 20px;
      }

      :deep(.el-input),
      :deep(.el-select) {
        width: 100%;
      }
    }
  }

  .product-section {
    margin-bottom: 20px;

    .add-product-btn {
      margin-top: 10px;
      text-align: center;
      border: 1px dashed #dcdfe6;
      cursor: pointer;
      width: 100%;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }

      .el-button {
        font-size: 14px;
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

      .stat-item {
        color: #606266;

        strong {
          color: #303133;
          font-weight: 600;
          margin-left: 5px;
        }
      }
    }

    .action-buttons {
      display: flex;
    }
  }
}

:deep(.el-input-number) {
  width: 100%;
}
</style>
