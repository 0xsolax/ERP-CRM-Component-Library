<template>
  <el-dialog v-model="dialogVisible" title="打印预览" width="420px" class="thermal-preview-dialog" @close="onDestroy">
    <div ref="printContent" class="print-content print-area" v-loading="loading">
      <!-- 按订单分类打印 或 批量打印 -->
      <template v-if="printType === 'order' || (isBatch && orderList.length > 0)">
        <div v-for="(order, orderIndex) in orderList" :key="orderIndex" class="order-section">
          <div class="ticket-header">
            <div class="ticket-title">发货单</div>
            <div class="ticket-divider" />
          </div>
          <div class="print-header">
            <div class="info-row">
              <span class="label">订单号：</span>
              <span class="value">{{ order.orderCode || '' }}</span>
            </div>
            <div class="info-row">
              <span class="label">业务员：</span>
              <span class="value">{{ order.salesEmployeeName || '' }}</span>
            </div>
            <div class="info-row">
              <span class="label">客户名称：</span>
              <span class="value">{{ order.customerName || '' }}</span>
            </div>
            <div class="info-row">
              <span class="label">收货人：</span>
              <span class="value">{{ order.consignee || '' }}</span>
            </div>
          </div>

          <div class="ticket-list">
            <div v-for="(row, rowIndex) in order.productList" :key="`${orderIndex}-${rowIndex}`" class="ticket-item">
              <div class="item-top">
                <span class="item-product">{{ row.productCode || '-' }} x{{ row.number || 0 }}</span>
              </div>
              <div class="item-spec">{{ row.specName || '-' }}</div>
              <div class="item-meta">库位：{{ row.locationName || '-' }}</div>
              <div class="item-meta">定制：{{ row.categorySpecificationItemName || '/' }}</div>
              <!-- 暂时不打印二维码，保留代码以便后续恢复
              <div
                class="qrcode-container"
                :data-code="`?specificationId=${row.specificationId}&categorySpecificationItemId=${
                  row.categorySpecificationItemId || ''
                }`"
              />
              -->
            </div>
          </div>
        </div>
      </template>

      <!-- 按产品分类打印 -->
      <template v-else>
        <div class="ticket-header">
          <div class="ticket-title">发货单</div>
          <div class="ticket-divider" />
        </div>
        <div class="print-header">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ orderCodes || '' }}</span>
          </div>
          <div class="info-row">
            <span class="label">业务员：</span>
            <span class="value">{{ detailData?.salesEmployeeName || '' }}</span>
          </div>
          <div class="info-row">
            <span class="label">客户名称：</span>
            <span class="value">{{ detailData?.customerName || '' }}</span>
          </div>
          <div class="info-row">
            <span class="label">收货人：</span>
            <span class="value">{{ detailData?.consignee || '' }}</span>
          </div>
        </div>

        <div class="ticket-list">
          <div v-for="(row, rowIndex) in productList" :key="rowIndex" class="ticket-item">
            <div class="item-top">
              <span class="item-product">{{ row.productCode || '-' }} x{{ row.number || 0 }}</span>
            </div>
            <div class="item-spec">{{ row.specName || '-' }}</div>
            <div class="item-meta">库位：{{ row.locationName || '-' }}</div>
            <div class="item-meta">定制：{{ row.categorySpecificationItemName || '/' }}</div>
            <!-- 暂时不打印二维码，保留代码以便后续恢复
            <div
              class="qrcode-container"
              :data-code="`?specificationId=${row.specificationId}&categorySpecificationItemId=${
                row.categorySpecificationItemId || ''
              }`"
            />
            -->
          </div>
        </div>
      </template>
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handlePrint">打印</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getDeliveryDetail, getDeliveryDetailOrder } from '@/api/admin/warehouse'
import QRCode from 'qrcode'
import { Print } from '@/utils/print'

const attrs = useAttrs()
const { rowData, printType, isBatch, onDestroy, selectedOrderId, selectedOrderCode } = attrs as any
const dialogVisible = ref(true)
const loading = ref(false)
const detailData = ref<any>({})
const productList = ref<any[]>([])
const orderList = ref<any[]>([])
const printContent = ref<HTMLElement>()
const orderCodes = ref<string>('')
const hasTriggeredPrint = ref(false)
let autoPrintTimer: ReturnType<typeof setTimeout> | null = null

const clearAutoPrintTimer = () => {
  if (autoPrintTimer) {
    clearTimeout(autoPrintTimer)
    autoPrintTimer = null
  }
}

const scheduleAutoPrint = () => {
  if (hasTriggeredPrint.value) return
  clearAutoPrintTimer()
  autoPrintTimer = setTimeout(() => {
    void handlePrint()
  }, 1000)
}

const processItemListToRows = (itemList: any[]) => {
  const processedList: any[] = []
  const productGroups: { [key: string]: any[] } = {}

  itemList.forEach((item: any) => {
    const productCode = item.productCode || '-'
    if (!productGroups[productCode]) {
      productGroups[productCode] = []
    }
    productGroups[productCode].push(item)
  })

  Object.values(productGroups).forEach((group: any[]) => {
    const groupSize = group.length
    group.forEach((item: any, index: number) => {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => spec.categorySpecificationItemValue || '')
        .filter((v: string) => v)
        .join('-')

      processedList.push({
        ...item,
        productCode: item.productCode || '-',
        specName: specName || '-',
        locationName: item.locationName || '-',
        number: item.number || 0,
        categorySpecificationItemName: item.categorySpecificationItemName || '/',
        rowspan: index === 0 ? groupSize : 0
      })
    })
  })

  return processedList
}

const fetchDetail = async () => {
  loading.value = true
  try {
    // 批量打印
    if (isBatch && rowData.ids && Array.isArray(rowData.ids)) {
      const deliveryList: any[] = []

      for (const deliveryId of rowData.ids) {
        const { code, data, message } = await getDeliveryDetail({ id: deliveryId })
        if (code !== 200) {
          ElMessage.warning(message)
          continue
        }

        deliveryList.push({
          orderCode: data?.orderCode ?? '',
          salesEmployeeName: data?.salesEmployeeName || '',
          customerName: data?.customerName || '',
          consignee: data?.consignee || '',
          productList: processItemListToRows(data.itemList || [])
        })
      }

      orderList.value = deliveryList
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      await generateQRCodes()
      scheduleAutoPrint()
    } else {
      // 单个发货单
      const { code, data, message } = await getDeliveryDetail({ id: rowData.id })
      if (code !== 200) {
        ElMessage.warning(message)
        return
      }

      detailData.value = data
      orderCodes.value = data?.orderCode ?? ''
      productList.value = processItemListToRows(data.itemList || [])

      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))
      await generateQRCodes()
      scheduleAutoPrint()
    }
  } finally {
    loading.value = false
  }
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const {
      code: orderCode,
      data: orderData,
      message: orderMessage
    } = await getDeliveryDetailOrder({
      id: rowData.id
    })
    if (orderCode !== 200) return ElMessage.warning(orderMessage)

    if (orderData && Array.isArray(orderData)) {
      const filteredOrderData = orderData.filter((item: any) => {
        if (!selectedOrderId && !selectedOrderCode) return true
        return item.orderId === selectedOrderId || item.orderCode === selectedOrderCode
      })
      const orders = await Promise.all(
        filteredOrderData.map(async (item: any) => {
          const { code, data, message } = await getDeliveryDetail({
            id: rowData.id,
            orderId: item.orderId
          })
          if (code !== 200) {
            ElMessage.warning(message)
            return null
          }
          return {
            orderCode: item.orderCode ?? '',
            salesEmployeeName: data?.salesEmployeeName || '',
            customerName: data?.customerName || '',
            consignee: data?.consignee || '',
            productList: processItemListToRows(data.itemList || [])
          }
        })
      )
      orderList.value = orders.filter(order => order !== null)
    }

    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))
    await generateQRCodes()
    scheduleAutoPrint()
  } finally {
    loading.value = false
  }
}

const generateQRCodes = async () => {
  if (!printContent.value) return
  const containers = printContent.value.querySelectorAll('.qrcode-container')
  for (const container of Array.from(containers) as any[]) {
    const code = container.getAttribute('data-code')
    if (!code) continue
    const dataUrl = await QRCode.toDataURL(code, {
      width: 80,
      margin: 1
    })
    const img = document.createElement('img')
    img.src = dataUrl
    img.style.width = '80px'
    img.style.height = '80px'
    container.innerHTML = ''
    container.appendChild(img)
  }
}

const handlePrint = async () => {
  if (!printContent.value) return
  if (hasTriggeredPrint.value) return
  hasTriggeredPrint.value = true
  clearAutoPrintTimer()
  await nextTick()
  await generateQRCodes()
  await nextTick()
  Print(printContent.value)
  dialogVisible.value = false
}

onMounted(() => {
  if (printType === 'order') {
    fetchOrderList()
  } else {
    fetchDetail()
  }
})

onBeforeUnmount(() => {
  clearAutoPrintTimer()
})
</script>

<style lang="scss">
@media print {
  * {
    box-sizing: border-box;
  }

  html,
  body {
    width: 58mm;
    min-width: 58mm;
    margin: 0;
    padding: 0;
    background: #fff;
  }

  .print-area {
    width: 58mm !important;
    max-width: 58mm !important;
    padding: 2mm !important;
    margin: 0 auto !important;
  }
}
</style>

<style lang="scss" scoped>
.print-content {
  width: 58mm;
  max-width: 58mm;
  margin: 0 auto;
  padding: 2mm;
  color: #000;
  background: #fff;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.4;

  .order-section {
    page-break-inside: avoid;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .ticket-header {
    text-align: center;
    margin-bottom: 8px;

    .ticket-title {
      font-size: inherit;
      font-weight: inherit;
      letter-spacing: 1px;
    }

    .ticket-divider {
      margin-top: 6px;
      border-top: 1px dashed #000;
    }
  }

  .print-header {
    margin-bottom: 8px;
    padding: 0;
    background: #fff;
    border-radius: 0;

    .info-row {
      display: block;
      margin-bottom: 6px;
      font-size: inherit;
      font-weight: inherit;
      line-height: inherit;
      word-break: break-all;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #000;
        font-weight: inherit;
      }

      .value {
        color: #000;
        font-weight: inherit;
      }
    }
  }

  .ticket-list {
    border-top: 1px dashed #000;
  }

  .ticket-item {
    padding: 6px 0;
    border-bottom: 1px dashed #000;

    .item-top {
      display: block;
      margin-bottom: 4px;
      font-size: inherit;
      font-weight: inherit;
    }

    .item-product {
      flex: 1;
      min-width: 0;
      word-break: break-all;
    }
    .item-spec {
      margin-bottom: 4px;
      font-size: inherit;
      font-weight: inherit;
      line-height: inherit;
      word-break: break-all;
    }

    .item-meta {
      font-size: inherit;
      font-weight: inherit;
      line-height: inherit;
      word-break: break-all;
    }
  }
}

@media print {
  @page {
    size: 58mm auto;
    margin: 0;
  }

  body {
    margin: 0;
  }

  .order-section {
    page-break-inside: avoid;
  }
}
</style>
