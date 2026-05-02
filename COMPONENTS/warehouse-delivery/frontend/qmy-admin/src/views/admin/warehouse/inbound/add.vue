<template>
  <div class="inbound-add-container">
    <h2 class="page-title">创建独立出入库单</h2>

    <div class="form-section">
      <div class="section-title">出入库详情</div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="120px"
        label-position="top"
        class="inbound-form"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="出入库类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择">
                <el-option v-for="item in inboundTypeList" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="isIndependentType">
            <el-form-item label="客户名称" prop="customerId">
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
          <el-col :span="8">
            <el-form-item label="操作备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="product-section">
      <el-table :data="productList" border style="width: 100%" :span-method="productSpanMethod">
        <el-table-column label="产品ID" prop="productCode" width="160" align="center" />
        <el-table-column label="规格名称" prop="specName" align="center" />
        <el-table-column label="规格图片" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              style="width: 50px; height: 50px; cursor: pointer"
              fit="cover"
              @click="handleImagePreview(row.image)"
            />
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="库位" prop="locationName" align="center" />
        <el-table-column v-if="!isIndependentType" label="公共仓可用库存" prop="enableStore" align="center" />
        <el-table-column v-if="isIndependentType" label="独立仓可用库存" prop="customerEnableStore" align="center" />
        <el-table-column label="操作数量" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.enterNumber"
              placeholder="请输入"
              @input="(val: string) => (row.enterNumber = validateInteger(val))"
            />
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
          数量：
          <strong>{{ totalCount }}件</strong>
        </span>
      </div>
      <div class="action-buttons">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </footer-actions>

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
import { ElMessage } from 'element-plus'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import FooterActions from '@/components/footer-actions/index.vue'
import ProductSelector from '@/components/product-selector/index.vue'
import { addStoreOrder } from '@/api/admin/warehouse'
import { getCustomerList } from '@/api/admin/sales/customer'
import { inboundTypeList } from '@/constant/yitang/warehouse'
import { validateInteger } from '@/utils/validate'

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
  type: '',
  customerId: '',
  remark: ''
})

const formRules = computed(() => ({
  type: [{ required: true, message: '请选择出入库类型', trigger: 'change' }],
  customerId: isIndependentType.value ? [{ required: true, message: '请选择客户名称', trigger: 'change' }] : []
}))

// 是否为独立仓类型（独立库转入5、独立库转出7）
const isIndependentType = computed(() => {
  return form.value.type === '5' || form.value.type === '7'
})

const customerList = ref<any[]>([])

const loadCustomerList = async () => {
  const { code, data, message } = await getCustomerList({
    pageNum: 1,
    pageSize: 1000,
    customerStoreStatus: 3
  })
  if (code !== 200) return ElMessage.warning(message)
  customerList.value = data?.list || []
}

onMounted(() => {
  loadCustomerList()
})

const productList = ref<any[]>([])

const totalCount = computed(() => {
  return productList.value.reduce((sum, item) => sum + (Number(item.enterNumber) || 0), 0)
})

const productSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
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

const handleAddProduct = () => {
  const params = {
    id: 'productSelector',
    el: '#app',
    data: {
      orderType: 'finished',
      tabMode: 'product',
      isIndependentType: isIndependentType.value,
      customerId: isIndependentType.value ? form.value.customerId : undefined,
      callback: (selectedProducts: any[]) => {
        console.log('selectedProducts', selectedProducts)
        selectedProducts.forEach(product => {
          if (product.type === 'product') {
            const specs = product.specs || []
            specs.forEach((spec: any, index: number) => {
              const customerEnableStore = spec?.customerEnableStore
              let enableStore = spec?.stoYtStore?.enableStore || 0
              if (customerEnableStore !== null) {
                enableStore = customerEnableStore
              }

              productList.value.push({
                productCode: product.productInfo?.code || '',
                specName: spec.name || '',
                image: spec.image || '',
                locationName: spec.locationName || '',
                enableStore: enableStore,
                customerEnableStore: customerEnableStore,
                enterNumber: '',
                isFirstRow: index === 0,
                rowSpan: index === 0 ? specs.length : 0,
                productId: spec.productId || product.productInfo?.id || null,
                specificationId: spec.id || null,
                locationId: spec.locationId || null
              })
            })
          }
        })
        recalculateRowSpan()
      }
    },
    render: ProductSelector
  }
  dynamic.show(params)
}

const handleRemove = (index: number) => {
  productList.value.splice(index, 1)
  recalculateRowSpan()
}

const recalculateRowSpan = () => {
  const productCodeMap = new Map<string, number[]>()
  productList.value.forEach((item, index) => {
    const code = item.productCode
    if (!productCodeMap.has(code)) {
      productCodeMap.set(code, [])
    }
    productCodeMap.get(code)!.push(index)
  })

  productList.value.forEach((item, index) => {
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

const handleSubmit = async () => {
  await formRef.value?.validate()

  if (productList.value.length === 0) {
    return ElMessage.warning('请添加产品')
  }

  for (let i = 0; i < productList.value.length; i++) {
    const item = productList.value[i]
    if (!item.enterNumber) {
      ElMessage.warning(`第${i + 1}行操作数量不能为空`)
      return
    }
  }

  const submitData = productList.value.map(item => ({
    type: form.value.type,
    productId: item.productId || null,
    specificationId: item.specificationId || null,
    customerId: form.value.customerId || null,
    enterNumber: Number(item.enterNumber) || 0,
    remark: form.value.remark || ''
  }))

  // return console.log('submitData', submitData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await addStoreOrder(submitData)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('提交成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/warehouse/inbound/record')
  // router.back()
}
</script>

<style lang="scss" scoped>
.inbound-add-container {
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

    .inbound-form {
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
</style>
