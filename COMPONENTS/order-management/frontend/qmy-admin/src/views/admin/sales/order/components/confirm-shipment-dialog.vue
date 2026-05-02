<template>
  <el-dialog v-model="visible" title="确认发货" width="950px" @close="handleClose">
    <el-table :data="productList" border :span-method="spanMethod" v-loading="loading" max-height="400">
      <el-table-column label="产品ID" prop="productId" align="center" width="160" />
      <el-table-column label="规格图片" align="center" width="120">
        <template #default="{ row }">
          <el-image
            v-if="row.specImage"
            :src="row.specImage"
            fit="cover"
            style="width: 40px; height: 40px; cursor: pointer"
            @click="handleImagePreview(row.specImage)"
          />
          <span v-else style="color: #999">－</span>
        </template>
      </el-table-column>
      <el-table-column label="规格名称" prop="specName" align="center" width="150" />
      <el-table-column label="已发货/总数量" align="center" width="180">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; gap: 10px; padding: 0 10px">
            <el-progress
              :percentage="row.progressPercentage"
              :color="row.progressColor"
              :show-text="false"
              style="flex: 1"
            />
            <span style="font-size: 13px; color: #606266; white-space: nowrap">{{ row.progressText }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="定制化属性" prop="customAttr" align="center" width="150" />
      <el-table-column label="本次发货数量" align="center" min-width="150">
        <template #default="{ $index }">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-input
              :model-value="productList[$index].shipmentQuantity"
              @update:model-value="val => updateShipmentQuantity($index, val)"
              placeholder="请输入"
              style="flex: 1"
            />
          </div>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>

  <!-- 图片预览 -->
  <el-image-viewer
    v-if="showViewer"
    :url-list="viewerImageList"
    :initial-index="0"
    hide-on-click-modal
    @close="showViewer = false"
  />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { confirmDelivery, getOrderDetailProductList } from '@/api/admin/sales/order'

interface Props {
  orderId?: string
  onSuccess?: () => void
}

const props = defineProps<Props>()

const visible = ref(true)
const productList = ref<any[]>([])
const loading = ref(false)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const validateInteger = (value: string): string => {
  return value.replace(/[^\d]/g, '')
}

const updateShipmentQuantity = (index: number, value: string) => {
  let validatedValue = validateInteger(value)
  if (validatedValue) {
    const numValue = Number(validatedValue)
    if (numValue === 0) {
      validatedValue = ''
    } else if (validatedValue.length > 1 && validatedValue.startsWith('0')) {
      validatedValue = String(numValue)
    }
  }

  productList.value[index].shipmentQuantity = validatedValue
}

// 单元格合并
const spanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
    if (row.isFirstInGroup) {
      return {
        rowspan: row.groupRowSpan,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
}

const handleConfirm = async () => {
  const validProducts = productList.value.filter(item => item.shipmentQuantity && Number(item.shipmentQuantity) > 0)

  if (validProducts.length === 0) {
    ElMessage.warning('请至少填写一个产品的发货数量')
    return
  }

  const invalidItems = validProducts.filter(item => {
    const shipmentNum = Number(item.shipmentQuantity) || 0
    const remainingNum = item.totalNumber - item.deliveredNumber
    return shipmentNum > remainingNum
  })

  if (invalidItems.length > 0) {
    const errorMsg = invalidItems
      .map(item => {
        const remainingNum = item.totalNumber - item.deliveredNumber
        return `${item.productId} - ${item.specName}: 本次发货数量(${item.shipmentQuantity})不能大于剩余可发货数量(${remainingNum})`
      })
      .join('\n')
    ElMessage.warning(errorMsg)
    return
  }

  const deliveryList = validProducts.map(item => ({
    itemId: item.itemId,
    deliveryNumber: Number(item.shipmentQuantity)
  }))

  const { code, message } = await confirmDelivery(deliveryList)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('操作成功')
  props.onSuccess?.()
  visible.value = false
}

const handleClose = () => {
  visible.value = false
}

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const loadProductList = async () => {
  loading.value = true
  try {
    const { code, data, message } = await getOrderDetailProductList({
      orderId: props.orderId
    })

    if (code !== 200) return ElMessage.warning(message)

    const itemList = data?.subOrder?.itemList || []
    const groupedData: any = {}
    itemList.forEach((item: any) => {
      const productCode = item.productCode
      if (!groupedData[productCode]) {
        groupedData[productCode] = []
      }

      const specName = item.itemList?.map((spec: any) => spec.categorySpecificationItemValue).join('-') || '-'

      const totalNumber = Number(item.number) || 0
      const deliveredNumber = Number(item.deliveryNumber) || 0
      const progressPercentage = totalNumber > 0 ? Math.round((deliveredNumber / totalNumber) * 100) : 0
      const progressColor = progressPercentage === 100 ? '#67c23a' : '#409eff'
      const progressText = `${deliveredNumber}/${totalNumber}`

      groupedData[productCode].push({
        itemId: item.id,
        productId: item.productCode,
        specImage: item.imageList?.[0]?.url || '',
        specName,
        progressPercentage,
        progressColor,
        progressText,
        customAttr: item.labelName || '/',
        shipmentQuantity: '',
        totalNumber,
        deliveredNumber
      })
    })

    const tableData: any[] = []
    Object.values(groupedData).forEach((group: any) => {
      group.forEach((item: any, index: number) => {
        tableData.push({
          ...item,
          isFirstInGroup: index === 0,
          groupRowSpan: index === 0 ? group.length : 0
        })
      })
    })

    productList.value = tableData
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProductList()
})
</script>

<style lang="scss" scoped>
:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-table) {
  .el-progress {
    width: 100%;
  }
}
</style>
