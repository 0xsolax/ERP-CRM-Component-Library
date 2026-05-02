<template>
  <div class="batch-apply-container">
    <h2 class="page-title">新增申购</h2>

    <div class="table-section">
      <el-table :data="visibleProductList" border style="width: 100%" :span-method="spanMethod">
        <el-table-column label="产品ID" prop="productCode" align="center" />
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
        <el-table-column label="申购数量" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.quantity"
              placeholder="请输入"
              clearable
              @input="val => (row.quantity = validateInteger(val))"
            />
          </template>
        </el-table-column>
        <el-table-column label="供应商" align="center">
          <template #default="{ row }">
            <el-select
              v-if="row.supplierList?.length"
              v-model="row.selectedSupplier"
              placeholder="请选择供应商"
              filterable
              style="width: 100%"
              clearable
              @change="handleSupplierChange(row)"
            >
              <el-option
                v-for="supplier in row.supplierList"
                :key="supplier.id"
                :label="supplier.displayText"
                :value="supplier.id"
              />
            </el-select>
            <span v-else style="color: #999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="handleRemove($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />

    <footer-actions>
      <div class="statistics">
        <span class="stat-item">
          产品共计：
          <strong>{{ totalProducts }}件</strong>
        </span>
        <span class="stat-item">
          申购数量：
          <strong>{{ totalApplyQuantity }}件</strong>
        </span>
      </div>
      <div class="actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" v-permission="'pur:yt:storeWarning:submitApplyPurchase'" @click="handleSubmit">
          提交
        </el-button>
      </div>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import FooterActions from '@/components/footer-actions/index.vue'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import { getApplyDetail, submitApplyPurchase } from '@/api/admin/purchase/stock-warning'
import { usePermissionStore } from '@/views/admin/store/modules/permission'

const permissionStore = usePermissionStore()
const userPermissions = permissionStore.permissions
const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()

const productList = ref<any[]>([])
const visibleProductList = computed(() => productList.value.filter((item: any) => !item.isDeleted))
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const validateInteger = (value: string): string => {
  return value.replace(/[^\d]/g, '')
}

const spanMethod = ({ row, rowIndex, columnIndex }: any) => {
  if (columnIndex === 0) {
    const list = visibleProductList.value
    const currentProductCode = row.productCode
    const firstIndex = list.findIndex((item: any) => item.productCode === currentProductCode)

    if (rowIndex === firstIndex) {
      const count = list.filter((item: any) => item.productCode === currentProductCode).length
      return {
        rowspan: count,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
}

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const fetchDetail = async () => {
  const ids = route.query.ids
  if (!ids) return
  const warningIdList = JSON.parse(ids as string)
  const { code, data, message } = await getApplyDetail({ warningIdList })
  if (code !== 200) return ElMessage.warning(message)
  const products = data || []
  const productGroups: any = {}
  products.forEach((item: any) => {
    const productCode = item.productCode || ''
    if (!productGroups[productCode]) {
      productGroups[productCode] = []
    }
    productGroups[productCode].push(item)
  })

  const tableData: any[] = []
  Object.keys(productGroups).forEach(productCode => {
    const group = productGroups[productCode]

    group.forEach((item: any, index: number) => {
      const specName =
        item.itemList
          ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
          .join('/') || '-'

      const image = item.imageList?.[0]?.url || ''
      const supplierList = (item.supplierList || []).map((supplier: any) => ({
        id: supplier.id,
        supplierId: supplier.supplierId,
        name: supplier.supplierName,
        price: supplier.supplierPrice,
        minOrder: supplier.minNumber,
        displayText: `${supplier.supplierName}/¥${supplier.supplierPrice}/${supplier.minNumber}起订`
      }))

      tableData.push({
        ...item,
        specName,
        image,
        quantity: '',
        selectedSupplier: '',
        supplierName: '',
        supplierList,
        isFirstInGroup: index === 0,
        groupRowSpan: group.length
      })
    })
  })

  productList.value = tableData
}

onMounted(() => {
  if (userPermissions.includes('pur:yt:storeWarning:applyDetail')) {
    fetchDetail()
  }
})

const totalProducts = computed(() => {
  return productList.value.length
})

const totalApplyQuantity = computed(() => {
  return productList.value.reduce((sum, item) => sum + (Number(item.quantity) || 0), 0)
})

const handleSupplierChange = (row: any) => {
  const supplier = row.supplierList?.find((s: any) => s.id === row.selectedSupplier)
  if (supplier) {
    row.supplierName = supplier.displayText
    row.supplierId = supplier.supplierId
  }
}

const handleRemove = async (index: number) => {
  const visibleList = productList.value.filter((item: any) => !item.isDeleted)
  const item = visibleList[index]
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

const handleSubmit = async () => {
  if (visibleProductList.value.length === 0) {
    ElMessage.warning('请至少添加一个产品')
    return
  }

  const emptyQuantityProducts = visibleProductList.value.filter(item => !item.quantity || Number(item.quantity) <= 0)
  if (emptyQuantityProducts.length > 0) {
    ElMessage.warning('请为所有产品填写申购数量')
    return
  }

  const unselectedProducts = visibleProductList.value.filter(item => !item.selectedSupplier)
  if (unselectedProducts.length > 0) {
    ElMessage.warning('请为所有产品选择供应商')
    return
  }

  const submitData = productList.value.map(item => ({
    id: item.id,
    customerId: item.customerId || undefined,
    productId: item.productId,
    specificationId: item.specificationId,
    supplierId: item.supplierId,
    applyPurchaseNumber: Number(item.quantity) || 0,
    isDeleted: item.isDeleted || undefined
  }))

  // return console.log('submitData', submitData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await submitApplyPurchase(submitData)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('提交成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/purchase/pending')
}
</script>

<style scoped lang="scss">
.batch-apply-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 120px);
  padding-bottom: 100px;

  .page-title {
    font-size: 20px;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 15px;
    border-bottom: 1px solid #e5e5e5;
    color: #303133;
  }

  .table-section {
    margin-bottom: 20px;

    .spec-info {
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .footer-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .statistics {
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

    .actions {
      display: flex;
    }
  }
}

:deep(.el-input-number) {
  width: 120px;
}

:deep(.el-select) {
  .el-select-dropdown__item {
    height: auto;
    padding: 10px 20px;
    line-height: 1.5;
  }
}
</style>
