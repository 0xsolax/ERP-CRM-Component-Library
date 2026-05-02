<template>
  <el-dialog v-model="dialogVisible" title="入库" width="950px" @close="handleClose">
    <div class="product-info">
      <img v-if="rowData.image" :src="rowData.image" class="product-image" />
      <div class="info-item">
        <div class="label">产品ID</div>
        <div class="value">{{ rowData.productCode || '-' }}</div>
      </div>
      <div class="info-item">
        <div class="label">规格名称</div>
        <div class="value">{{ rowData.specificationName || '-' }}</div>
      </div>
      <div class="info-item">
        <div class="label">采购单号</div>
        <div class="value">{{ rowData.purchaseCode || '-' }}</div>
      </div>
      <div class="info-item">
        <div class="label">供应商名称</div>
        <div class="value">{{ rowData.supplierName || '-' }}</div>
      </div>
      <div class="info-item">
        <div class="label">欠数</div>
        <div class="value">{{ rowData.owedCount ?? '-' }}</div>
      </div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" @submit.prevent>
      <el-form-item label="定制化属性" prop="labelName" v-if="rowData.labelId">
        <span>{{ rowData.labelName }}</span>
      </el-form-item>
      <div style="display: flex; align-items: center; gap: 40px">
        <el-form-item label="本次入库总数">
          <span style="font-weight: 600; font-size: 16px; color: #409eff">{{ totalEnterCount }}</span>
        </el-form-item>
        <el-form-item label="额外入库数量" prop="enterNumber">
          <el-input
            v-model="form.enterNumber"
            placeholder="请输入"
            style="width: 200px"
            @input="(val: string) => { form.enterNumber = validateInteger(val) }"
            @change="autoDistribute"
            @keyup.enter="handleSubmit"
          />
        </el-form-item>
      </div>
    </el-form>

    <div v-if="storeOccupyList.length > 0" class="occupy-section">
      <div class="section-title">占用订单信息</div>
      <el-table :data="storeOccupyList" border size="small" max-height="200">
        <el-table-column label="订单号" prop="orderCode" min-width="150" align="center" />
        <el-table-column label="客户名" prop="customerName" min-width="100" align="center" />
        <el-table-column label="业务员" prop="salesEmployeeName" min-width="80" align="center" />
        <el-table-column label="交货时间" min-width="100" align="center">
          <template #default="{ row }">
            <span>{{ formatDate(row.deliveryTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="占用数量" min-width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.orderOccupyNumber ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="入库数量" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.inputEnterNumber"
              placeholder="请输入"
              size="small"
              @input="(val: string) => {
                row.inputEnterNumber = validateInteger(val)
                const max = Number(row.orderOccupyNumber) || 0
                if (Number(row.inputEnterNumber) > max) row.inputEnterNumber = String(max)
              }"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="transitOccupyList.length > 0" class="occupy-section">
      <div class="section-title">占用在途信息</div>
      <el-table :data="transitOccupyList" border size="small" max-height="200">
        <el-table-column label="订单号" prop="orderCode" min-width="150" align="center" />
        <el-table-column label="客户名" prop="customerName" min-width="100" align="center" />
        <el-table-column label="业务员" prop="salesEmployeeName" min-width="80" align="center" />
        <el-table-column label="交货时间" min-width="100" align="center">
          <template #default="{ row }">
            <span>{{ formatDate(row.deliveryTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="占用数量" prop="occupyTransitNumber" min-width="80" align="center" />
        <el-table-column label="入库数量" min-width="120" align="center">
          <template #default="{ row }">
            <el-input
              v-model="row.inputEnterNumber"
              placeholder="请输入"
              size="small"
              @input="(val: string) => {
                row.inputEnterNumber = validateInteger(val)
                const max = Number(row.occupyTransitNumber) || 0
                if (Number(row.inputEnterNumber) > max) row.inputEnterNumber = String(max)
                normalizeFlexibleEnter(row)
              }"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
// eslint-disable-next-line prettier/prettier
import { enterStoreWithAllocation, getStoreOccupyDetail, getTransitOccupyDetail } from '@/api/admin/warehouse'
import { validateInteger } from '@/utils/validate'
import dayjs from 'dayjs'

defineProps(['onDestroy'])
const attrs = useAttrs()
const { rowData, callback } = attrs as any
const dialogVisible = ref(true)

const handleClose = () => {
  dialogVisible.value = false
}
const formRef = ref<FormInstance>()
const submitting = ref(false)

const storeOccupyList = ref<any[]>([])
const transitOccupyList = ref<any[]>([])

const form = reactive({
  enterNumber: ''
})

const rules = reactive<FormRules>({})

const totalEnterCount = computed(() => {
  const formNum = Number(form.enterNumber) || 0
  const tableNum = [...storeOccupyList.value, ...transitOccupyList.value].reduce(
    (sum: number, row: any) => sum + (Number(row.inputEnterNumber) || 0),
    0
  )
  return formNum + tableNum
})

const storeOccupyTotal = computed(() => {
  return storeOccupyList.value.reduce((sum: number, row: any) => sum + (Number(row.orderOccupyNumber) || 0), 0)
})

const storeOccupyEnterTotal = computed(() => {
  return storeOccupyList.value.reduce((sum: number, row: any) => sum + (Number(row.inputEnterNumber) || 0), 0)
})

const transitEnterTotal = computed(() => {
  return transitOccupyList.value.reduce((sum: number, row: any) => sum + (Number(row.inputEnterNumber) || 0), 0)
})

const remainingOwedCount = computed(() => {
  return Math.max((Number(rowData.owedCount) || 0) - totalEnterCount.value, 0)
})

const remainingStoreOccupyCount = computed(() => {
  return Math.max(storeOccupyTotal.value - storeOccupyEnterTotal.value, 0)
})

const maxFlexibleEnter = computed(() => {
  return Math.max((Number(rowData.owedCount) || 0) - storeOccupyTotal.value, 0)
})
const formatDate = (date: string) => (date ? dayjs(date).format('YYYY-MM-DD') : '-')

const sortByDeliveryTime = (list: any[]) => {
  return [...list].sort((a, b) => {
    const ta = a.deliveryTime ? new Date(a.deliveryTime).getTime() : Infinity
    const tb = b.deliveryTime ? new Date(b.deliveryTime).getTime() : Infinity
    return ta - tb
  })
}

const normalizeFlexibleEnter = (changedTransitRow?: any) => {
  const max = maxFlexibleEnter.value
  const extraEnterNumber = Number(form.enterNumber) || 0
  const transitSumExcludingCurrent = transitOccupyList.value.reduce((sum: number, row: any) => {
    if (changedTransitRow && row === changedTransitRow) return sum
    return sum + (Number(row.inputEnterNumber) || 0)
  }, 0)

  if (changedTransitRow) {
    const currentValue = Number(changedTransitRow.inputEnterNumber) || 0
    const allowedCurrentMax = Math.max(max - extraEnterNumber - transitSumExcludingCurrent, 0)
    if (currentValue > allowedCurrentMax) {
      changedTransitRow.inputEnterNumber = allowedCurrentMax > 0 ? String(allowedCurrentMax) : ''
    }
    return
  }

  const transitSum = transitEnterTotal.value
  const allowedExtraMax = Math.max(max - transitSum, 0)
  if (extraEnterNumber > allowedExtraMax) {
    form.enterNumber = allowedExtraMax > 0 ? String(allowedExtraMax) : ''
  }
}

const autoDistribute = () => {
  const owedCount = rowData.owedCount ?? 0
  let remaining = Math.min(Number(form.enterNumber) || 0, owedCount)

  // 先清空所有表格的入库数量
  storeOccupyList.value.forEach((row: any) => (row.inputEnterNumber = ''))
  transitOccupyList.value.forEach((row: any) => (row.inputEnterNumber = ''))

  if (remaining <= 0) return

  // 先按交货时间分配占用订单信息（用 orderOccupyNumber 作为上限）
  const sortedStore = sortByDeliveryTime(storeOccupyList.value)
  for (const sorted of sortedStore) {
    if (remaining <= 0) break
    const max = Number(sorted.orderOccupyNumber) || 0
    const alloc = Math.min(max, remaining)
    if (alloc > 0) {
      const original = storeOccupyList.value.find((r: any) => r === sorted)
      if (original) original.inputEnterNumber = String(alloc)
      remaining -= alloc
    }
  }

  // 再按交货时间分配占用在途信息
  const sortedTransit = sortByDeliveryTime(transitOccupyList.value)
  for (const sorted of sortedTransit) {
    if (remaining <= 0) break
    const max = Number(sorted.occupyTransitNumber) || 0
    const alloc = Math.min(max, remaining)
    if (alloc > 0) {
      const original = transitOccupyList.value.find((r: any) => r === sorted)
      if (original) original.inputEnterNumber = String(alloc)
      remaining -= alloc
    }
  }

  // 剩余的保留在 form.enterNumber（不清零，作为未分配的通用入库数量）
  form.enterNumber = remaining > 0 ? String(remaining) : ''
}

const loadOccupyData = async () => {
  const specificationId = rowData.specificationId
  if (!specificationId) return
  const storeOrderIdList = Array.isArray(rowData.storeOrderIdList) ? rowData.storeOrderIdList : []

  const [storeRes, transitRes] = await Promise.all([
    getStoreOccupyDetail({ specificationId, storeOrderIdList }),
    getTransitOccupyDetail({ specificationId, storeOrderIdList, labelId: rowData.labelId ?? undefined })
  ])

  const transitItems: any[] = transitRes.code === 200 && transitRes.data ? transitRes.data : []
  const storeItems: any[] = storeRes.code === 200 && storeRes.data ? storeRes.data : []

  // 占用订单信息：只展示当前采购单强绑定的待入库数量
  storeOccupyList.value = storeItems
    .filter((item: any) => (Number(item.currentBindNumber) || 0) > 0)
    .map((item: any) => ({
      ...item,
      inputEnterNumber: '',
      orderOccupyNumber: Number(item.currentBindNumber) || 0
    }))

  // 占用在途信息：独立展示订单现有的在途占用，不再和当前采购单强绑定数量混算
  transitOccupyList.value = transitItems
    .map((item: any) => {
      // const rawTransitNumber = Number(item.occupyTransitNumber) || 0
      // const transitEnteredNumber = Number(item.occupyTransitEnterNumber) || 0
      // const totalBindNumber = Number(item.totalBindNumber) || 0
      // const weakTransitNumber = Math.max(rawTransitNumber - transitEnteredNumber - totalBindNumber, 0)
      const weakTransitNumber = Math.max(item.occupyTransitWaitEnterNumber, 0)
      return {
        ...item,
        occupyTransitNumber: weakTransitNumber,
        inputEnterNumber: ''
      }
    })
    .filter((item: any) => (Number(item.occupyTransitNumber) || 0) > 0)
}

onMounted(() => {
  loadOccupyData()
})

const handleSubmit = async () => {
  // 占用订单信息：isTransit=false，走 enter() 路径（绑定采购单）
  const storeEnterList = storeOccupyList.value
    .filter((row: any) => row.inputEnterNumber && Number(row.inputEnterNumber) > 0)
    .map((row: any) => ({ orderSubItemId: row.id, enterNumber: Number(row.inputEnterNumber), isTransit: false }))
  // 占用在途信息：isTransit=true，全部走 enterTransitOccupy() 路径
  const transitEnterList = transitOccupyList.value
    .filter((row: any) => row.inputEnterNumber && Number(row.inputEnterNumber) > 0)
    .map((row: any) => ({ orderSubItemId: row.id, enterNumber: Number(row.inputEnterNumber), isTransit: true }))
  const orderEnterList = [...storeEnterList, ...transitEnterList]
  const extraEnterNumber = Number(form.enterNumber) || 0

  if (orderEnterList.length === 0 && extraEnterNumber === 0) {
    ElMessage.warning('请输入入库数量')
    return
  }

  const owedCount = rowData.owedCount ?? 0
  if (totalEnterCount.value > owedCount) {
    ElMessage.warning(`本次入库总数(${totalEnterCount.value})不能超过欠数(${owedCount})`)
    return
  }

  if (remainingOwedCount.value < remainingStoreOccupyCount.value) {
    ElMessage.warning(
      `当前入库数量会导致剩余欠数(${remainingOwedCount.value})小于未处理占用订单数量(${remainingStoreOccupyCount.value})`
    )
    return
  }

  submitting.value = true
  try {
    const { code, message } = await enterStoreWithAllocation({
      storeOrderIdList: rowData.storeOrderIdList ?? [],
      orderEnterList: orderEnterList,
      extraEnterNumber
    })
    if (code !== 200) {
      ElMessage.warning(message || '入库失败，数据可能已变更')
      await loadOccupyData()
      return
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    if (callback) callback()
  } catch (e: any) {
    ElMessage.warning(e?.message || '入库失败，请刷新后重试')
    await loadOccupyData()
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.product-info {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;

  .product-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 20px;
  }

  .info-item {
    margin-right: 40px;
    text-align: center;
    &:last-child {
      margin-right: 0;
    }

    .label {
      font-size: 14px;
      margin-bottom: 4px;
      color: #999;
    }

    .value {
      font-size: 14px;
      color: #303133;
    }
  }
}

.occupy-section {
  margin-top: 16px;

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    padding-left: 8px;
    border-left: 3px solid #409eff;
  }
}
</style>
