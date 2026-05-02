<template>
  <div class="inbound-container">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getInboundOrderList"
      :dataCallback="dataCallback"
      :searchClick="handleSearchClick"
      :resetClick="handleResetClick"
      :toolButton="false"
    >
      <template #tableHeader>
        <div class="table-header-btns">
          <el-button type="primary" @click="handleRecord">出入库记录</el-button>
          <el-button type="primary" v-permission="'sto:yt:order:addStore'" @click="handleCreateOrder">
            创建独立出入库单
          </el-button>
          <el-button type="primary" v-permission="'sto:yt:order:enter'" @click="handleBatchInbound">批量入库</el-button>
        </div>
      </template>

      <template #image="{ row }">
        <el-image
          v-if="row.image"
          :src="row.image"
          fit="cover"
          style="width: 50px; height: 50px; cursor: pointer"
          @click="handleImagePreview(row.image)"
        />
        <span v-else>-</span>
      </template>
      <template #specificationName="{ row }">
        <el-tooltip v-if="row.description && row.description !== '-'" :content="row.description" placement="top">
          <div class="spec-name-content" style="cursor: pointer">
            <template v-for="(segment, index) in row.specSegments" :key="index">
              <span
                class="spec-name-segment"
                :class="{ 'spec-name-segment--colored': !!segment.color }"
                :style="{
                  backgroundColor: segment.color || undefined,
                  color: segment.color ? getContrastTextColor(segment.color) : undefined
                }"
              >
                {{ segment.text }}
              </span>
              <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
            </template>
          </div>
        </el-tooltip>
        <div v-else class="spec-name-content">
          <template v-for="(segment, index) in row.specSegments" :key="index">
            <span
              class="spec-name-segment"
              :class="{ 'spec-name-segment--colored': !!segment.color }"
              :style="{
                backgroundColor: segment.color || undefined,
                color: segment.color ? getContrastTextColor(segment.color) : undefined
              }"
            >
              {{ segment.text }}
            </span>
            <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
          </template>
        </div>
      </template>
      <template #progress="{ row }">
        <div class="progress-cell">
          <el-progress :percentage="row.progressPercentage" :show-text="false" />
          <span class="progress-text">{{ row.progressText }}</span>
        </div>
      </template>
      <template #orderCode="{ row }">
        <div class="multi-line-cell">
          <div v-for="(item, index) in row.orderInfoListDisplay" :key="index">{{ item.orderCode || '-' }}</div>
        </div>
      </template>
      <template #orderProgress="{ row }">
        <div class="multi-line-cell">
          <div v-for="(item, index) in row.orderInfoListDisplay" :key="index">{{ item.progressText }}</div>
        </div>
      </template>
      <template #operation="{ row }">
        <el-button
          v-if="row.status != 1"
          type="primary"
          link
          size="small"
          v-permission="'sto:yt:order:enter'"
          @click="handleInbound(row)"
        >
          入库
        </el-button>
        <el-button type="primary" link size="small" @click="handleProgress(row)">进度</el-button>
      </template>
    </bz-table>

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
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ColumnProps } from '@/interface/table'
import { dynamic } from '@bzlab/bz-core'
import { getInboundOrderList } from '@/api/admin/warehouse'
import { inboundStatusList, getInboundStatusLabel } from '@/constant/yitang/warehouse'
import InboundDialog from './components/inbound-dialog.vue'
import ProgressDialog from './components/progress-dialog.vue'
import BatchInboundDialog from './components/batch-inbound-dialog.vue'

const router = useRouter()
const bzTableRef = ref()
const hasOrderCodeSearch = ref(false)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const searchColumns = computed(() => [
  {
    label: '采购单号',
    prop: 'purchaseCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '供应商名称',
    prop: 'supplierName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '产品ID',
    prop: 'productCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '规格名称',
    prop: 'specificationName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '订单编号',
    prop: 'orderCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '状态',
    prop: 'status',
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true }
    },
    enum: inboundStatusList
  }
])

const handleSelectable = (row: any) => {
  return row.status != 1
}

const columns = computed<ColumnProps[]>(() => {
  return [
    { type: 'selection', width: 50, selectable: handleSelectable },
    { label: '采购单号', prop: 'purchaseCode', align: 'center' },
    { label: '供应商名称', prop: 'supplierName', align: 'center' },
    { label: '产品ID', prop: 'productCode', align: 'center' },
    { label: '图片', prop: 'image', align: 'center', showOverflowTooltip: false },
    { label: '规格名称', prop: 'specificationName', align: 'center' },
    { label: '规格描述', prop: 'description', align: 'center' },
    { label: '定制化属性', prop: 'labelName', align: 'center' },
    { label: '已入库/需要入库总数量', prop: 'progress', align: 'center', width: 180, showOverflowTooltip: false },
    { label: '欠数', prop: 'owedCount', align: 'center' },
    { label: '库位', prop: 'locationName', align: 'center' },
    { label: '采购单备注', prop: 'purchaseRemark', align: 'center' },
    { label: '订单编号', prop: 'orderCode', align: 'center' },
    {
      label: '订单已入库/需入库',
      prop: 'orderProgress',
      align: 'center',
      width: 180
    },
    { label: '状态', prop: 'statusLabel', align: 'center' },
    { label: '操作', prop: 'operation', align: 'center', width: 120, fixed: 'right' }
  ]
})

const handleSearchClick = (params: any) => {
  hasOrderCodeSearch.value = !!params?.orderCode
}

const handleResetClick = () => {
  hasOrderCodeSearch.value = false
}

const getContrastTextColor = (backgroundColor?: string) => {
  if (!backgroundColor) return '#303133'
  const normalized = backgroundColor.trim().replace('#', '')
  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) return '#303133'

  const red = parseInt(normalized.slice(0, 2), 16)
  const green = parseInt(normalized.slice(2, 4), 16)
  const blue = parseInt(normalized.slice(4, 6), 16)
  const brightness = red * 0.299 + green * 0.587 + blue * 0.114
  return brightness > 186 ? '#303133' : '#ffffff'
}

const buildSpecSegments = (itemList: any[] = [], fallbackName = '') => {
  if (!Array.isArray(itemList) || itemList.length === 0) {
    return [{ text: fallbackName || '-', color: '' }]
  }

  return itemList.map((item: any) => ({
    text: item.categorySpecificationItemValue || item.value || '',
    color: item.color || ''
  }))
}

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  records = records.map((item: any) => {
    const specificationName = item.itemList?.map((i: any) => i.categorySpecificationItemValue).join('-') || ''
    const specSegments = buildSpecSegments(item.itemList, specificationName)
    const image = item.imageList?.[0]?.url || ''
    const enterNumber = item.enterNumber || 0
    const totalNumber = item.totalNumber || 0
    const owedCount = Math.max(0, totalNumber - enterNumber)
    const progress = totalNumber > 0 ? `${enterNumber}/${totalNumber}` : '0/0'
    const progressPercentage = totalNumber > 0 ? Math.round((enterNumber / totalNumber) * 100) : 0
    const progressText = `${enterNumber}/${totalNumber}`
    const orderEnterNumber = item.orderEnterNumber ?? enterNumber
    const orderTotalNumber = item.orderTotalNumber ?? totalNumber
    const orderProgress = orderTotalNumber > 0 ? `${orderEnterNumber}/${orderTotalNumber}` : '0/0'

    let status = 0 // 入库中
    if (enterNumber >= totalNumber && totalNumber > 0) {
      status = 1 // 已完成
    }

    const orderInfoListDisplay = (item.orderInfoList || []).filter(Boolean).map((order: any) => ({
      orderCode: order.orderCode || '-',
      progressText: `${order.enter_number || 0}/${order.totalNumber || 0}`
    }))

    return {
      ...item,
      image,
      specificationName,
      specSegments,
      description: item.description || item.specificationDesc || item.specificationDescription || '-',
      progress,
      progressPercentage,
      progressText,
      owedCount,
      purchaseRemark: item.purchaseItemRemark || item.purchaseRemark || '',
      orderCode: item.orderCode || '-',
      orderProgress,
      orderInfoListDisplay,
      status,
      statusLabel: getInboundStatusLabel(status)
    }
  })
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const handleRecord = () => {
  router.push('/warehouse/inbound/record')
}

const handleCreateOrder = () => {
  router.push('/warehouse/inbound/add')
}

const handleInbound = (row: any) => {
  // 收集同规格下所有其他入库单绑定的订单号
  const tableData = bzTableRef.value?.tableData || []
  const allBoundOrderCodes = new Set<string>()
  tableData.forEach((item: any) => {
    if (item.specificationId === row.specificationId && item !== row) {
      ;(item.orderInfoList || []).filter(Boolean).forEach((o: any) => {
        if (o.orderCode) allBoundOrderCodes.add(o.orderCode)
      })
    }
  })

  const params = {
    id: 'inboundDialog',
    el: '#app',
    data: {
      rowData: row,
      otherBoundOrderCodes: Array.from(allBoundOrderCodes),
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: InboundDialog
  }
  dynamic.show(params)
}

const handleBatchInbound = () => {
  const selectedRows = bzTableRef.value?.getSelection?.() || []
  if (!selectedRows.length) {
    ElMessage.warning('请先选择要入库的记录')
    return
  }
  const params = {
    id: 'batchInboundDialog',
    el: '#app',
    data: {
      selectedRows,
      showOrderCode: hasOrderCodeSearch.value,
      searchParams: { ...(bzTableRef.value?.searchParams || {}) },
      callback: () => {
        bzTableRef.value?.clearSelection?.()
        bzTableRef.value?.getTableList?.()
      }
    },
    render: BatchInboundDialog
  }
  dynamic.show(params)
}

const handleProgress = (row: any) => {
  const params = {
    id: 'progressDialog',
    el: '#app',
    data: {
      storeOrderIdList: row.storeOrderIdList ?? []
    },
    render: ProgressDialog
  }
  dynamic.show(params)
}
</script>

<style lang="scss" scoped>
.inbound-container {
  .table-header-btns {
    display: flex;
  }

  .progress-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .el-progress {
      flex: 1;
    }

    .progress-text {
      font-size: 13px;
      color: #606266;
      white-space: nowrap;
    }
  }

  .spec-name-content {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    gap: 2px;
  }

  .spec-name-segment {
    color: #303133;
  }

  .spec-name-segment--colored {
    display: inline-block;
    padding: 1px 2px;
    border-radius: 4px;
  }

  .spec-name-separator {
    color: #303133;
  }

  .multi-line-cell {
    display: flex;
    flex-direction: column;
    gap: 4px;
    line-height: 1.4;
  }
}
</style>
