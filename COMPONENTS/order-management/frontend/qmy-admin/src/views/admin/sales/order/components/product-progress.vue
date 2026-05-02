<template>
  <el-drawer v-model="drawerVisible" title="产品进度" size="500px" direction="rtl" @close="onDestroy">
    <div class="progress-timeline">
      <el-timeline>
        <el-timeline-item v-for="(item, index) in progressList" :key="index" :timestamp="item.time" placement="top">
          <div class="timeline-title">{{ item.title }}</div>
          <div class="timeline-content">
            <div v-for="(detail, idx) in item.details" :key="idx" class="detail-item">
              {{ detail }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
    <el-empty v-if="!progressList.length" description="暂无数据" />
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getItemOperation } from '@/api/admin/sales/order'
import { getOperationTypeLabel } from '@/constant/yitang/sales'

const attrs = useAttrs()
const { itemId, itemIds, onDestroy, currencySymbol = '¥' } = attrs as any

const drawerVisible = ref(true)
const progressList = ref<any[]>([])
const currencyPrefix =
  currencySymbol && typeof currencySymbol === 'object' && 'value' in currencySymbol
    ? currencySymbol.value
    : currencySymbol

// 加载产品进度
const loadProductProgress = async () => {
  const requestItemIds = Array.isArray(itemIds) && itemIds.length ? itemIds : itemId ? [itemId] : []
  if (!requestItemIds.length) {
    progressList.value = []
    return
  }

  const resultList = await Promise.all(
    requestItemIds.map((currentItemId: any) => getItemOperation({ itemId: currentItemId }))
  )
  const invalidResult = resultList.find((result: any) => result.code !== 200)
  if (invalidResult) {
    ElMessage.warning(invalidResult.message)
    return
  }

  const operationList = resultList.flatMap((result: any) => (Array.isArray(result.data) ? result.data : []))
  const uniqueOperationMap = new Map<string, any>()
  operationList.forEach((item: any) => {
    const key = [
      item.type ?? '',
      item.createTime ?? '',
      item.operationCount ?? '',
      item.operationCode ?? '',
      item.operationOrderCode ?? '',
      item.packageCode ?? '',
      item.purchaseItemId ?? '',
      item.orderSubItemId ?? ''
    ].join('_')
    if (!uniqueOperationMap.has(key)) {
      uniqueOperationMap.set(key, item)
    }
  })

  progressList.value = Array.from(uniqueOperationMap.values())
    .sort((a: any, b: any) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime())
    .map((item: any) => ({
      title: getOperationTypeLabel(item.type),
      time: item.createTime || '',
      details: formatDetails(item)
    }))
}

const formatDetails = (item: any) => {
  const details: string[] = []
  const type = Number(item.type)

  switch (type) {
    case 1: // 下单
      if (item.createUserName) {
        details.push(`下单人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`下单数量：${item.operationCount}`)
      }
      if (item.occupyStore !== undefined && item.occupyStore !== null) {
        details.push(`占用库存：${item.occupyStore}`)
      }
      if (item.occupyTransit !== undefined && item.occupyTransit !== null) {
        details.push(`占用在途：${item.occupyTransit}`)
      }
      if (item.applyPurchaseCount !== undefined && item.applyPurchaseCount !== null) {
        details.push(`申购数量：${item.applyPurchaseCount}`)
      }
      break

    case 2: // 采购
      if (item.createUserName) {
        details.push(`采购人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`采购数量：${item.operationCount}`)
      }
      if (item.operationOrderCode) {
        details.push(`采购单：${item.operationOrderCode}`)
      }
      break

    case 3: // 订单退货
      if (item.createUserName) {
        details.push(`退货人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`退货数量：${item.operationCount}`)
      }
      break

    case 4: // 入库
      if (item.createUserName) {
        details.push(`入库人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`入库数量：${item.operationCount}`)
      }
      break

    case 5: // 发货
      if (item.createUserName) {
        details.push(`发货人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`发货数量：${item.operationCount}`)
      }
      if (item.operationCode) {
        details.push(`所属订单：${item.operationCode}`)
      }
      if (item.packageCode) {
        details.push(`包裹号：${item.packageCode}`)
      }
      if (item.operationOrderCode) {
        details.push(`发货单号：${item.operationOrderCode}`)
      }
      break

    case 6: // 半成品确认规格
      if (item.createUserName) {
        details.push(`确认人：${item.createUserName}`)
      }
      if (item.operationCode) {
        details.push(`确认规格：${item.operationCode}`)
      }
      if (item.operationCount) {
        details.push(`确认数量：${item.operationCount}`)
      }
      break

    case 7: // 采购单退货
      if (item.createUserName) {
        details.push(`退货人：${item.createUserName}`)
      }
      if (item.operationCount) {
        details.push(`退货数量：${item.operationCount}`)
      }
      break

    case 8: // 关闭订单
      if (item.createUserName) {
        details.push(`操作人：${item.createUserName}`)
      }
      details.push(`关闭数量：${item.operationCount || 0}`)
      details.push(`关闭金额：${currencyPrefix || '¥'}${item.operationCode || 0}`)
      break

    default:
      break
  }

  return details
}

onMounted(() => {
  loadProductProgress()
})
</script>

<style scoped lang="scss">
.progress-timeline {
  .el-timeline {
    padding-left: 10px;
  }
  .timeline-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }

  .timeline-content {
    .detail-item {
      font-size: 14px;
      color: #666;
      line-height: 24px;
    }
  }

  :deep(.el-timeline-item__timestamp) {
    color: #909399;
    font-size: 13px;
  }

  :deep(.el-timeline-item__node) {
    background-color: #409eff;
  }
}
</style>
