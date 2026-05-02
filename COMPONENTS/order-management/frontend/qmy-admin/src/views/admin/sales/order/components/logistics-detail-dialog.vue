<template>
  <el-dialog v-model="dialogVisible" title="物流详情" width="1200px" @close="onDestroy">
    <div class="detail-header">
      <span>发货单号：{{ deliveryInfo.deliveryCode }}</span>
      <span>发货时间：{{ deliveryInfo.deliveryTime }}</span>
      <span>物流公司：{{ deliveryInfo.transportCompanyName }}</span>
      <span>物流单号：{{ deliveryInfo.packageCode }}</span>
    </div>
    <div class="detail-header">
      <span>收货地址：{{ deliveryInfo.address }}</span>
    </div>

    <div class="package-view">
      <div class="package-list">
        <div
          v-for="(pkg, index) in packageList"
          :key="pkg.id || index"
          :class="['package-item', { active: currentPackageIndex === index }]"
          @click="handlePackageChange(index)"
        >
          <div class="package-label">包裹{{ index + 1 }}</div>
          <div class="package-size">{{ pkg.boxCode || '-' }}</div>
        </div>
      </div>

      <div class="package-detail-wrapper">
        <div class="package-info">
          <span>打包箱箱号：{{ currentPackage.boxCode || '-' }}</span>
          <span>打包箱尺寸：{{ currentPackage.boxSize || '-' }}</span>
          <span>打包箱重量：{{ currentPackage.weight ? `${currentPackage.weight}kg` : '-' }}</span>
        </div>

        <el-table :data="currentPackage.products" :span-method="productSpanMethod" max-height="500" border>
          <el-table-column label="产品ID" prop="productCode" align="center" width="140" />
          <el-table-column label="本次发货总数" prop="totalCount" align="center" width="120" />
          <el-table-column label="规格名称" align="center">
            <template #default="{ row }">
              <div>{{ row.specName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="规格图片" align="center" width="100">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.specImage)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="库位" prop="location" align="center" width="120" />
          <el-table-column label="本次发货数量" prop="shipCount" align="center" width="120" />
          <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
            <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { getPackageDetail } from '@/api/admin/sales/order'

interface DialogProps {
  rowData?: any
  onDestroy?: () => void
}

const attrs = useAttrs()
const { rowData, onDestroy } = attrs as DialogProps

const dialogVisible = ref(true)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])
const currentPackageIndex = ref(0)
const packageList = ref<any[]>([])

const deliveryInfo = ref({
  deliveryCode: rowData?.deliveryCode || '-',
  deliveryTime: rowData?.deliveryTime || '-',
  address: rowData?.address || '-',
  transportCompanyName: rowData?.transportCompanyName || '-',
  packageCode: rowData?.packageCode || '-'
})

const currentPackage = computed(
  () => packageList.value[currentPackageIndex.value] || { boxCode: '', boxSize: '', weight: '', products: [] }
)

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const productSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 1) {
    if (row.rowspan > 0) {
      return { rowspan: row.rowspan, colspan: 1 }
    }
    if (row.rowspan === 0) {
      return { rowspan: 0, colspan: 0 }
    }
  }
  return { rowspan: 1, colspan: 1 }
}

const processItemListToProducts = (itemList: any[]) => {
  const processedProducts: any[] = []
  const productGroups: Record<string, any[]> = {}

  itemList.forEach((item: any) => {
    const productCode = item.productCode || '-'
    if (!productGroups[productCode]) {
      productGroups[productCode] = []
    }
    productGroups[productCode].push(item)
  })

  Object.values(productGroups).forEach((group: any[]) => {
    const groupSize = group.length
    const totalCount = group.reduce((sum, item) => sum + (item.number || 0), 0)

    group.forEach((item: any, index: number) => {
      const specName =
        item.itemList
          ?.map((spec: any) => spec.categorySpecificationItemValue || '')
          .filter((value: string) => value)
          .join('-') || '-'

      processedProducts.push({
        productCode: item.productCode || '-',
        totalCount,
        specName,
        specImage: item.imageList?.[0]?.url || '',
        location: item.locationName || '-',
        shipCount: item.number || 0,
        categorySpecificationItemName: item.categorySpecificationItemName || '',
        rowspan: index === 0 ? groupSize : 0
      })
    })
  })

  return processedProducts
}

const loadPackageList = async () => {
  let rawBoxList: any[] = []
  if (Array.isArray(rowData?.boxList) && rowData.boxList.length) {
    rawBoxList = rowData.boxList
  } else if (rowData?.boxId) {
    rawBoxList = [{ id: rowData.boxId }]
  }
  if (!rawBoxList.length) {
    packageList.value = []
    return
  }

  const result = await Promise.all(
    rawBoxList.map(async (box: any) => {
      const { code, data, message } = await getPackageDetail({ deliveryBoxId: box.id })
      if (code !== 200) {
        ElMessage.warning(message)
        return {
          id: box.id,
          boxCode: box.boxCode || '-',
          boxSize: box.boxSize || '-',
          weight: box.boxWeight || '',
          products: []
        }
      }

      return {
        id: data.id,
        boxCode: data.boxCode || '-',
        boxSize: data.boxSize || '-',
        weight: data.boxWeight || '',
        products: processItemListToProducts(data.boxItemList || [])
      }
    })
  )

  packageList.value = result
  currentPackageIndex.value = 0
}

const handlePackageChange = (index: number) => {
  currentPackageIndex.value = index
}

onMounted(() => {
  loadPackageList()
})
</script>

<style scoped lang="scss">
.detail-header {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 32px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.package-view {
  display: flex;
  gap: 16px;
}

.package-list {
  width: 180px;
  max-height: 520px;
  overflow-y: auto;
  border-right: 1px solid #ebeef5;
  padding-right: 12px;
}

.package-item {
  padding: 12px;
  margin-bottom: 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.package-item.active {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.package-label {
  font-weight: 600;
  color: #303133;
}

.package-size {
  margin-top: 6px;
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}

.package-detail-wrapper {
  flex: 1;
  min-width: 0;
}

.package-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 24px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}
</style>
