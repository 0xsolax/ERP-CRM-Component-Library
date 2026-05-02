<template>
  <div class="purchase-add-container">
    <h2 class="page-title">生成采购单</h2>

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
              <el-select
                v-model="form.supplierId"
                placeholder="请选择"
                filterable
                disabled
                @change="handleSupplierChange"
              >
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
                :model-value="form.shippingFee"
                @update:model-value="val => (form.shippingFee = validateDecimal(val))"
                placeholder="请输入"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠金额" prop="discountAmount">
              <el-input
                :model-value="form.discountAmount"
                @update:model-value="val => (form.discountAmount = validateDecimal(val))"
                placeholder="请输入"
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
            <el-form-item label="付款方式" prop="paymentMethod">
              <el-select v-model="form.paymentMethod" placeholder="请选择" @change="handlePaymentMethodChange">
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
      </el-form>
    </div>

    <div class="product-section">
      <el-table :data="visibleProductList" border style="width: 100%" :span-method="spanMethod">
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
              v-else-if="row.specImage"
              :src="row.specImage"
              style="width: 50px; height: 50px; cursor: pointer"
              fit="cover"
              @click="handleImagePreview(row.specImage)"
            />
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>

        <el-table-column label="申购数量" min-width="90" align="center">
          <template #default="{ row }">
            <span>{{ row.quantity }}</span>
          </template>
        </el-table-column>

        <el-table-column label="供应商规格" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.supplierSpec }}</span>
          </template>
        </el-table-column>

        <el-table-column label="采购数量" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              :model-value="row.purchaseQuantity"
              :disabled="!!(row.categoryLabelName && row.categoryLabelName !== '-')"
              @update:model-value="val => updateField(row, 'purchaseQuantity', val)"
              placeholder="请输入"
            />
          </template>
        </el-table-column>

        <el-table-column label="供应商单价" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              :model-value="row.supplierPrice"
              @update:model-value="val => updateField(row, 'supplierPrice', val)"
              placeholder="请输入"
            />
          </template>
        </el-table-column>

        <el-table-column label="起订量" min-width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.minOrder }}</span>
          </template>
        </el-table-column>

        <el-table-column label="订单规格备注" min-width="200" align="center">
          <template #default="{ row }">
            <div class="remark-cell">
              <span>{{ row.orderRemark }}</span>
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
            <span>{{ row.categoryLabelName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="客户名称" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.customerName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="所属订单编号" min-width="150" align="center">
          <template #default="{ row }">
            <span>{{ row.orderCode }}</span>
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
        <el-button @click="handleSaveDraft">暂存</el-button>
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
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import FooterActions from '@/components/footer-actions/index.vue'
import ProductSelector from '@/components/product-selector/index.vue'
import { getSupplierSelect } from '@/api/admin/product'
import { getSaveDetail } from '@/api/admin/purchase/pending'
import { createOrUpdatePurchase, getPurchaseDetail as getPurchaseDetailApi } from '@/api/admin/purchase/purchased'
import { paymentMethodList } from '@/constant/yitang/purchase'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()
const formRef = ref()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

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
  customerId: ''
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
}

const supplierList = ref<any[]>([])
const isInboundDelivery = ref(true)

const productList = ref<any[]>([])
const visibleProductList = computed(() => productList.value.filter((item: any) => !item.isDeleted))

// 总采购数量
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

// 加载供应商列表
const loadSupplierList = async () => {
  const { code, data, message } = await getSupplierSelect()
  if (code !== 200) return ElMessage.warning(message)
  supplierList.value = data ?? []
}

const handleSupplierChange = () => {
  // 供应商变更
}

const handlePaymentMethodChange = () => {
  formRef.value?.validateField('platformCode')
}

// 校验小数（指定小数位数）
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

const validateInteger = (value: string): string => {
  return value.replace(/[^\d]/g, '')
}

const updateField = (row: any, field: string, value: string) => {
  let validatedValue = value
  if (field === 'supplierPrice') {
    validatedValue = validateDecimal(value)
  } else if (field === 'purchaseQuantity') {
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
  row[field] = validatedValue
}

const spanMethod = ({ row, rowIndex, columnIndex }: any) => {
  const list = visibleProductList.value

  if (columnIndex === 0) {
    const currentProductCode = row.productCode
    const firstIndex = list.findIndex((item: any) => item.productCode === currentProductCode)
    if (rowIndex === firstIndex) {
      const count = list.filter((item: any) => item.productCode === currentProductCode).length
      return { rowspan: count, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }

  // 规格名称列合并（只合并相邻且相同规格的行）
  if (columnIndex === 1) {
    const getMergeKey = (item: any) => `${item.productCode}_${item.specName}_${item.handProductLevel || ''}`
    const mergeKey = getMergeKey(row)
    if (rowIndex > 0 && getMergeKey(list[rowIndex - 1]) === mergeKey) {
      return { rowspan: 0, colspan: 0 }
    }
    let count = 1
    while (rowIndex + count < list.length && getMergeKey(list[rowIndex + count]) === mergeKey) {
      count++
    }
    return { rowspan: count, colspan: 1 }
  }
}

const insertProduct = (item: any) => {
  const code = item.productCode
  if (code) {
    let lastIndex = -1
    productList.value.forEach((p, i) => {
      if (p.productCode === code) lastIndex = i
    })
    if (lastIndex >= 0) {
      productList.value.splice(lastIndex + 1, 0, item)
      return
    }
  }
  productList.value.push(item)
}

const handleAddProduct = () => {
  const params = {
    id: 'productSelector',
    el: '#app',
    data: {
      orderType: 'finished',
      tabMode: 'product',
      isCreateSpecification: true,
      supplierId: form.value.supplierId ?? null,
      supplierDisabled: true,
      callback: (selectedProducts: any[]) => {
        console.log('选中的产品', selectedProducts)

        selectedProducts.forEach((product: any) => {
          if (product.type === 'product') {
            const productId = String(product.productInfo?.id || '')
            const productCode = product.productInfo?.code || ''
            product.specs.forEach((spec: any) => {
              const supplierInfo = spec.supplierList?.[0] || {}
              const specName =
                spec.specificationItemList
                  ?.map((si: any) => `${si.categorySpecificationName}-${si.categorySpecificationItemValue}`)
                  .join('/') ||
                spec.name ||
                ''
              insertProduct({
                productId,
                productCode,
                productSpecificationId: spec.id || '',
                isSemiFinished: false,
                specName,
                specImage: spec.image || '',
                handProductLevel: spec.autoProductLevel || spec.handProductLevel || '',
                quantity: 0,
                purchaseQuantity: '',
                supplierSpec: supplierInfo.supplierSpecification || spec.name || '',
                supplierPrice: supplierInfo.supplierPrice ? String(supplierInfo.supplierPrice) : '',
                minOrder: supplierInfo.minNumber || '-',
                orderSubCode: '-',
                orderCode: '-',
                salesEmployeeName: '-',
                categoryLabelName: '-',
                customerName: '',
                orderTime: '-',
                deliveryTime: '-',
                orderRemark: '-',
                purchaseRemark: ''
              })
            })
          } else if (product.type === 'combination' && product.combinationProduct) {
            const combo = product.combinationProduct
            const productId = String(combo.productId || '')
            const productCode = product.productInfo?.code || combo.productCode || ''
            const specImage = combo.imageList?.[0]?.url || combo.productImage || ''
            const specName =
              combo.productSpecificationItemList
                ?.map(
                  (item: any) => `${item.categorySpecificationName || ''}-${item.categorySpecificationItemValue || ''}`
                )
                .join(' / ') || ''
            const supplierInfo = combo.specificationSupplier || {}
            insertProduct({
              productId,
              productCode,
              productSpecificationId: combo.productSpecificationId || '',
              isSemiFinished: false,
              specName,
              specImage,
              handProductLevel: combo.autoProductLevel || combo.handProductLevel || '',
              quantity: combo.number || 0,
              purchaseQuantity: '',
              supplierSpec: supplierInfo.supplierSpecification || specName,
              supplierPrice: supplierInfo.supplierPrice ? String(supplierInfo.supplierPrice) : '',
              minOrder: supplierInfo.minNumber || '-',
              orderSubCode: '-',
              orderCode: '-',
              salesEmployeeName: '-',
              categoryLabelName: '-',
              customerName: '-',
              orderTime: '-',
              deliveryTime: '-',
              orderRemark: '-',
              purchaseRemark: ''
            })
          }
        })
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
}

const handleCancel = () => {
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.back()
}

const buildSubmitData = (status: string) => {
  const supplier = supplierList.value.find((s: any) => s.id === form.value.supplierId)
  return {
    deliveryTime: dayjs(form.value.deliveryTime).format('YYYY-MM-DD HH:mm:ss') || '',
    discountAmount: Number(form.value.discountAmount) || 0,
    isInboundDelivery: isInboundDelivery.value,
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
      productId: item.productId || null,
      customerId: item.customerId || null,
      customerName: item.customerName || null,
      salesEmployeeId: item.salesEmployeeId || null,
      applyPurchaseId: item.id || null,
      specificationId: item.productSpecificationId || null,
      orderSubId: item.orderSubId || null,
      orderSubItemId: item.orderSubItemId || null,
      categorySpecificationItemId: item.categoryLabelId || null,
      categorySpecificationItemName: item.categoryLabelName || null,
      number: Number(item.purchaseQuantity) || 0,
      supplierPrice: Number(item.supplierPrice) || 0,
      remark: item.purchaseRemark || '',
      orderRemark: item.orderRemark || '',
      isDeleted: item.isDeleted || undefined
    }))
  }
}

const handleSaveDraft = async () => {
  try {
    await formRef.value?.validate()
  } catch (error) {
    return
  }

  if (visibleProductList.value.length === 0) {
    ElMessage.warning('请至少添加一个产品')
    return
  }

  const invalidItems = visibleProductList.value.filter((item: any) => !item.purchaseQuantity || !item.supplierPrice)
  if (invalidItems.length > 0) {
    ElMessage.warning('请填写所有产品的采购数量和供应商单价')
    return
  }

  const submitData = buildSubmitData('0')
  // return console.log('submitData', submitData)
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

  if (visibleProductList.value.length === 0) {
    ElMessage.warning('请至少添加一个产品')
    return
  }

  const invalidItems = visibleProductList.value.filter((item: any) => !item.purchaseQuantity || !item.supplierPrice)
  if (invalidItems.length > 0) {
    ElMessage.warning('请填写所有产品的采购数量和供应商单价')
    return
  }

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

const getPurchaseDetail = async () => {
  const id = '222'
  const { code, data, message } = await getPurchaseDetailApi(id)
  if (code !== 200) return ElMessage.warning(message)
  console.log('getPurchaseDetail', data)
}
getPurchaseDetail

const getDetail = async () => {
  await loadSupplierList()

  const { from, applyPurchaseIdList, isInboundDelivery: isInboundDeliveryQuery } = route.query
  if (from !== 'pending' || !applyPurchaseIdList) return
  isInboundDelivery.value = isInboundDeliveryQuery === '1'
  const parsedApplyPurchaseIdList = JSON.parse(applyPurchaseIdList as string)
  const { code, data, message } = await getSaveDetail({ applyPurchaseIdList: parsedApplyPurchaseIdList })
  if (code !== 200) return ElMessage.warning(message)
  const productData = data || []

  if (productData.length > 0 && productData[0].supplierId) {
    form.value.supplierId = productData[0].supplierId
  }

  const productGroups = new Map()
  productData.forEach((item: any) => {
    const key = item.productCode
    if (!productGroups.has(key)) {
      productGroups.set(key, [])
    }
    productGroups.get(key).push(item)
  })

  const processedProducts: any[] = []
  productGroups.forEach((items: any[]) => {
    // 按规格名称+等级排序，确保相同规格的行相邻
    items.sort((a: any, b: any) => {
      const getKey = (it: any) => {
        const sn =
          it.itemList
            ?.map((s: any) => `${s.categorySpecificationName}-${s.categorySpecificationItemValue}`)
            .join('/') || '-'
        return `${sn}_${it.handProductLevel || ''}`
      }
      return getKey(a).localeCompare(getKey(b))
    })
    items.forEach((item: any, index: number) => {
      const specName =
        item.itemList
          ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
          .join('/') || '-'
      const specImage = item.imageList?.[0]?.url || ''
      const isSemiFinished = !item.productSpecificationId
      const specRemark = item.orderRemark || ''
      const orderNote = item.orderNote || ''
      // 将订单规格备注和订单备注合并，作为采购规格备注的默认值
      const combinedRemark = [specRemark, orderNote].filter(r => r && r !== '-').join('\n')
      processedProducts.push({
        ...item,
        isSemiFinished,
        productCode: item.productCode || '',
        specName,
        specImage,
        quantity: item.number || 0,
        purchaseQuantity: item.number || '',
        supplierSpec: item.supplierSpecification || '-',
        supplierPrice: item.supplierPrice || '',
        minOrder: item.minNumber || '-',
        orderSubCode: item.orderSubCode || '-',
        orderCode: item.orderCode || '-',
        salesEmployeeName: item.salesEmployeeName || '-',
        categoryLabelName: item.categoryLabelName || '-',
        customerName: item.customerName || '-',
        handProductLevel: item.handProductLevel || '',
        orderTime: item.orderTime || '-',
        deliveryTime: item.deliveryTime || '-',
        orderRemark: specRemark || '-',
        orderNote,
        purchaseRemark: combinedRemark,
        isFirstRow: index === 0,
        rowSpan: items.length
      })
    })
  })

  productList.value = processedProducts
}

onMounted(() => {
  getDetail()
})
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
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }

      .el-button {
        font-size: 14px;
        width: 100%;
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
