<template>
  <div class="shipping-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getDeliveryList"
      :dataCallback="dataCallback"
      :searchClick="handleSearchWithCounts"
      :resetClick="handleResetWithCounts"
      :initParam="initParam"
      :hideTabs="false"
      :tabsColumns="tabsColumns"
      :tabsClick="handleTabsClick"
      :span-method="spanMethod"
      :toolButton="false"
      selectId="_rowKey"
    >
      <template #tableHeader>
        <div class="table-header-btns">
          <!-- <el-button :icon="Printer" @click="handlePrintSetting" style="width: 35px" /> -->
          <el-button v-if="showBatchPrint && initParam.status === 0" type="primary" @click="handleBatchPrint">
            批量打印
          </el-button>
        </div>
      </template>

      <template #shipCode="{ row }">
        <span v-if="shouldRenderMergedCell(row)" class="link-text">{{ row.shipCode }}</span>
      </template>

      <template #customerInfo="{ row }">
        <!-- prettier-ignore -->
        <div
          v-if="shouldRenderMergedCell(row)"
          v-copy="`收件人：${row.consignee}\n联系方式：${row.phone}\n收货地址：${row.address}`"
          class="customer-info customer-info--copyable"
        >
          <div>客户名称：{{ row.customerName }}</div>
          <div>收件人：{{ row.consignee }}</div>
          <div>联系方式：{{ row.phone }}</div>
          <div>收货地址：{{ row.address }}</div>
        </div>
      </template>

      <template #orderCode="{ row }">
        <span class="link-text">{{ row.orderCode }}</span>
      </template>

      <template #status="{ row }">
        <div class="status-cell">
          <template v-if="row.productComplete === '-'">
            <span>-</span>
          </template>
          <template v-else>
            <div class="status-item">
              <el-icon :size="16" :color="row.productCompleteIcon === 'success' ? '#67C23A' : '#F56C6C'">
                <CircleCheck v-if="row.productCompleteIcon === 'success'" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ row.productComplete }}</span>
            </div>
            <div class="status-item">
              <el-icon :size="16" :color="row.packageCompleteIcon === 'success' ? '#67C23A' : '#F56C6C'">
                <CircleCheck v-if="row.packageCompleteIcon === 'success'" />
                <CircleClose v-else />
              </el-icon>
              <span>{{ row.packageComplete }}</span>
            </div>
          </template>
        </div>
      </template>

      <template #logisticsInfo="{ row }">
        <template v-if="shouldRenderMergedCell(row)">
          <div v-if="row.originalData.isChina" class="logistics-info domestic">
            <!-- <div class="logistics-row">
            <span>{{ row.originalData.packageCode || '-' }}</span>
            <el-button type="primary" link size="small" @click.stop="handleEditLogistics(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
          </div> -->
            <div class="logistics-row">
              <span>
                {{ row.originalData.stoYtTransportCompany?.name || '' }}
              </span>
              <span>{{ row.originalData.packageCode || '-' }}</span>
              <el-button type="primary" link size="small" @click.stop="handleEditLogistics(row)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </div>
            <div class="logistics-row">
              <span>
                地址:
                {{
                  row.originalData.stoYtTransportCompany?.isHomeService == 1
                    ? '上门取件'
                    : row.originalData.stoYtTransportCompany?.address || '-'
                }}
              </span>
            </div>
          </div>
          <div v-else class="logistics-info international">
            <div class="logistics-row">
              <span>
                {{ row.originalData.stoYtTransportCompany?.name || '' }}
              </span>
              <div class="waybill-actions">
                <el-image
                  v-if="row.originalData.transportOrderFileUrl"
                  :src="row.originalData.transportOrderFileUrl"
                  fit="cover"
                  style="width: 32px; height: 32px; border-radius: 4px"
                  :preview-src-list="[row.originalData.transportOrderFileUrl]"
                  preview-teleported
                />
                <el-button type="primary" link size="small" @click.stop="handleUploadWaybill(row)">
                  {{ row.originalData.transportOrderFileUrl ? '重新上传' : '上传面单' }}
                </el-button>
              </div>
            </div>
            <div class="logistics-row">
              <span>
                地址:
                {{
                  row.originalData.stoYtTransportCompany?.isHomeService == 1
                    ? '上门取件'
                    : row.originalData.stoYtTransportCompany?.address || '-'
                }}
              </span>
            </div>
          </div>
        </template>
      </template>

      <template #operation="{ row }">
        <template v-if="shouldRenderMergedCell(row)">
          <el-button
            v-if="initParam.status === 1 || initParam.status === 2"
            type="primary"
            v-permission="'sto:yt:delivery:detail'"
            link
            size="small"
            @click="handleDetail(row)"
          >
            详情
          </el-button>
          <el-button v-if="initParam.status === 1" type="primary" link size="small" @click="handleLogistics(row)">
            填写物流
          </el-button>
          <el-button
            v-if="initParam.status === 1"
            type="danger"
            link
            size="small"
            @click="handleReturnWaitPackage(row)"
          >
            退回
          </el-button>
          <!-- <el-button v-if="initParam.status === 0" type="primary" link size="small" @click="handlePrint(row)">
          打印
        </el-button> -->
          <el-button
            v-if="initParam.status === 0"
            type="primary"
            v-permission="'sto:yt:delivery:takePackage'"
            link
            size="small"
            @click="handleStartPacking(row)"
          >
            开始打包
          </el-button>
        </template>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import PrintSettingDialog from './components/print-setting-dialog.vue'
import PackingDetailDialog from './components/packing-detail-dialog.vue'
// import StartPackingDialog from './components/start-packing-dialog.vue'
import PackingCombinedDialog from './components/packing-combined-dialog.vue'
import LogisticsDialog from './components/logistics-dialog.vue'
import PrintPreviewDialog from './components/print-preview-dialog.vue'
import UploadWaybillDialog from './components/upload-waybill-dialog.vue'
import { getDeliveryList, returnWaitPackage } from '@/api/admin/warehouse'
import { getDeliveryTypeLabel } from '@/constant/yitang/sales'
import {
  // productQuantityStatusList,
  // packingStatusList,
  getProductQuantityStatusLabel,
  getPackingStatusLabel
} from '@/constant/yitang/warehouse'

const tableRef = ref()
const showBatchPrint = false
const showSelectionColumn = false
const initParam = reactive({
  status: 0 as number
})
const tabCounts = reactive<Record<string, number>>({
  0: 0,
  1: 0,
  2: 0
})

const formatTabCount = (count: number | undefined) => {
  if (!count || count < 0) return '0'
  return count > 99 ? '99+' : String(count)
}

const tabsColumns = computed(() => [
  { label: `待打包 ${formatTabCount(tabCounts[0])}`, prop: 0, active: initParam.status === 0 },
  { label: `待发货 ${formatTabCount(tabCounts[1])}`, prop: 1, active: initParam.status === 1 },
  { label: `已发货 ${formatTabCount(tabCounts[2])}`, prop: 2, active: initParam.status === 2 }
])

const getCurrentSearchParams = () => {
  const rawSearchParams = tableRef.value?.searchParams || {}
  const params: Record<string, any> = {}
  Object.keys(rawSearchParams).forEach(key => {
    const value = rawSearchParams[key]
    if (value || value === false || value === 0) {
      params[key] = value
    }
  })
  return params
}

const refreshTabCounts = async () => {
  const searchParams = getCurrentSearchParams()
  const statuses = [0, 1, 2]
  await Promise.all(
    statuses.map(async status => {
      const { code, data } = await getDeliveryList({
        ...searchParams,
        status,
        pageNum: 1,
        pageSize: 1
      })
      tabCounts[status] = code === 200 ? Number(data?.total || 0) : 0
    })
  )
}

const handleTabsClick = (tab: any) => {
  initParam.status = Number(tab.paneName)
  tableRef.value?.getTableList()
  void refreshTabCounts()
}

const handleSearchWithCounts = () => {
  void refreshTabCounts()
}

const handleResetWithCounts = () => {
  void nextTick(() => refreshTabCounts())
}

const searchColumns = computed(() => {
  const baseSearchColumns = [
    {
      label: '发货单号',
      prop: 'code',
      search: {
        el: 'el-input',
        props: { placeholder: '请输入', clearable: true }
      }
    },
    {
      label: '客户名称',
      prop: 'customerName',
      search: {
        el: 'el-input',
        props: { placeholder: '请输入', clearable: true }
      }
    },
    {
      label: '订单号',
      prop: 'orderCode',
      search: {
        el: 'el-input',
        props: { placeholder: '请输入', clearable: true }
      }
    }
  ]

  // 待打包Tab
  if (initParam.status === 0) {
    // baseSearchColumns.push(
    //   {
    //     label: '产品数量状态',
    //     prop: 'productComplete',
    //     enum: productQuantityStatusList,
    //     search: {
    //       el: 'el-select',
    //       props: { placeholder: '请选择', clearable: true }
    //     }
    //   } as any,
    //   {
    //     label: '打包状态',
    //     prop: 'packageComplete',
    //     enum: packingStatusList,
    //     search: {
    //       el: 'el-select',
    //       props: { placeholder: '请选择', clearable: true }
    //     }
    //   } as any
    // )
  }

  return baseSearchColumns
})

const columns = computed<ColumnProps[]>(() => {
  const baseColumns: ColumnProps[] = [{ label: '发货单号', prop: 'shipCode', width: 160, align: 'center' }]

  if (showSelectionColumn) {
    baseColumns.unshift({ type: 'selection', width: 40 })
  }

  baseColumns.push({ label: '客户信息', prop: 'customerInfo', width: 300, align: 'center' })

  if (initParam.status === 0) {
    baseColumns.push({ label: '更新时间', prop: 'updateTime', align: 'center' })
  } else if (initParam.status === 1) {
    baseColumns.push({ label: '打包完成时间', prop: 'packageTime', align: 'center' })
  } else if (initParam.status === 2) {
    baseColumns.push({ label: '发货时间', prop: 'deliveryTime', align: 'center' })
    baseColumns.push({ label: '物流信息', prop: 'logisticsInfo', width: 250, align: 'center' })
  }

  baseColumns.push({ label: '订单号', prop: 'orderCode', align: 'center' })
  baseColumns.push({ label: '发货形式', prop: 'shippingMethod', align: 'center' })

  // 待打包Tab
  if (initParam.status === 0) {
    baseColumns.push({ label: '状态', prop: 'status', width: 200, align: 'center' })
  }

  baseColumns.push(
    { label: '订单备注', prop: 'orderRemark', align: 'center' },
    { label: '业务员', prop: 'saleEmployeeName', align: 'center' },
    { label: '交货时间', prop: 'orderDeliveryTime', align: 'center' }
  )

  let operationWidth = 120
  if (initParam.status === 1) {
    operationWidth = 190
  }
  baseColumns.push({ label: '操作', prop: 'operation', width: operationWidth, align: 'center', fixed: 'right' })

  return baseColumns
})

const dataCallback = (data: any) => {
  const processedList: any[] = []
  data?.list?.forEach((item: any) => {
    const itemList = (item.itemList || []).filter((i: any) => i !== null)
    const rowCount = itemList.length || 1

    if (itemList.length === 0) {
      const rowData: any = {
        _rowKey: `${item.id}_0`,
        id: item.id,
        shipCode: item.code,
        customerName: item.customerName,
        address: item.address,
        consignee: item.consignee ?? '-',
        phone: item.phone ?? '-',
        orderCode: '-',
        shippingMethod: '-',
        orderRemark: '-',
        saleEmployeeName: '-',
        orderDeliveryTime: '-',
        rowspan: 1,
        originalData: item
      }

      if (initParam.status === 0) {
        rowData.updateTime = item.updateTime
        rowData.productComplete = '-'
        rowData.packageComplete = '-'
        rowData.productCompleteIcon = 'error'
        rowData.packageCompleteIcon = 'error'
      } else if (initParam.status === 1) {
        rowData.packageTime = item.packageTime
      } else if (initParam.status === 2) {
        rowData.deliveryTime = item.deliveryTime
      }

      processedList.push(rowData)
    } else {
      itemList.forEach((subItem: any, index: number) => {
        const rowData: any = {
          _rowKey: `${item.id}_${index}`,
          id: item.id,
          shipCode: item.code,
          customerName: item.customerName,
          address: item.address,
          consignee: item.consignee ?? '-',
          phone: item.phone ?? '-',
          orderCode: subItem.orderCode,
          shippingMethod: getDeliveryTypeLabel(subItem.shippingMethod),
          orderRemark: subItem.orderRemark,
          saleEmployeeName: subItem.saleEmployeeName,
          orderDeliveryTime: subItem.orderDeliveryTime ? dayjs(subItem.orderDeliveryTime).format('YYYY-MM-DD') : '-',
          rowspan: index === 0 ? rowCount : 0,
          originalData: item
        }

        if (initParam.status === 0) {
          rowData.updateTime = item.updateTime

          const productComplete = subItem.productComplete ? '1' : '0'
          const packageComplete = subItem.packageComplete ? '1' : '0'

          rowData.productComplete = getProductQuantityStatusLabel(productComplete)
          rowData.packageComplete = getPackingStatusLabel(packageComplete)
          rowData.productCompleteIcon = productComplete == '1' ? 'success' : 'error'
          rowData.packageCompleteIcon = packageComplete == '1' ? 'success' : 'error'
        } else if (initParam.status === 1) {
          rowData.packageTime = item.packageTime
        } else if (initParam.status === 2) {
          rowData.deliveryTime = item.deliveryTime
        }

        processedList.push(rowData)
      })
    }
  })

  return {
    list: processedList,
    total: Number(data?.total || 0)
  }
}

const mergedColumnProps = computed(() => {
  const props = ['shipCode', 'customerInfo', 'operation']
  if (initParam.status === 0) {
    props.push('updateTime')
  } else if (initParam.status === 1) {
    props.push('packageTime')
  } else if (initParam.status === 2) {
    props.push('deliveryTime', 'logisticsInfo')
  }
  return new Set(props)
})

const shouldRenderMergedCell = (row: any) => row.rowspan !== 0

// 合并单元格方法
const spanMethod = ({ row, column }: any) => {
  const isSelectionColumn = column?.type === 'selection'
  const shouldMerge = isSelectionColumn || (column?.property && mergedColumnProps.value.has(column.property))

  if (shouldMerge) {
    if (row.rowspan > 0) {
      return { rowspan: row.rowspan, colspan: 1 }
    } else if (row.rowspan === 0) {
      return { rowspan: 0, colspan: 0 }
    }
  }
  return { rowspan: 1, colspan: 1 }
}

const handlePrintSetting = () => {
  const params = {
    id: 'printSettingDialog',
    el: '#app',
    data: {
      callback: (type: string) => {
        console.log('打印类型:', type)
        window.print()
      }
    },
    render: PrintSettingDialog
  }
  dynamic.show(params)
}
handlePrintSetting

const handleBatchPrint = () => {
  const selectedList = tableRef.value?.getSelection() || []
  if (!selectedList || selectedList.length === 0) {
    ElMessage.warning('请先选择要打印的记录')
    return
  }

  const uniqueDeliveryIds = [...new Set(selectedList.map((row: any) => row.id))]
  if (uniqueDeliveryIds.length === 1) {
    const row = selectedList.find((r: any) => r.id === uniqueDeliveryIds[0])
    if (row) {
      handlePrint(row)
    }
    return
  }

  const params = {
    id: 'printPreviewDialog',
    el: '#app',
    data: {
      rowData: {
        ids: uniqueDeliveryIds
      },
      printType: 'product',
      isBatch: true
    },
    render: PrintPreviewDialog
  }
  dynamic.show(params)
}

const handleDetail = (row: any) => {
  const params = {
    id: 'packingDetailDialog',
    el: '#app',
    data: {
      rowData: row,
      status: initParam.status,
      callback: () => {
        tableRef.value?.getTableList()
        void refreshTabCounts()
      }
    },
    render: PackingDetailDialog
  }
  dynamic.show(params)
}

const handlePrint = (row: any) => {
  const params = {
    id: 'printPreviewDialog',
    el: '#app',
    data: {
      rowData: row
    },
    render: PrintPreviewDialog
  }
  dynamic.show(params)
}

const handleStartPacking = (row: any) => {
  const params = {
    id: 'startPackingDialog',
    el: '#app',
    data: {
      rowData: row,
      callback: () => {
        tableRef.value?.getTableList()
        void refreshTabCounts()
      }
    },
    render: PackingCombinedDialog
  }
  dynamic.show(params)
}

const handleLogistics = (row: any) => {
  const params = {
    id: 'logisticsDialog',
    el: '#app',
    data: {
      rowData: { ...row.originalData, shipCode: row.shipCode },
      callback: () => {
        tableRef.value?.getTableList()
        void refreshTabCounts()
      }
    },
    render: LogisticsDialog
  }
  dynamic.show(params)
}

const handleReturnWaitPackage = async (row: any) => {
  await ElMessageBox.confirm('退回后将保留当前打包箱数据，并将状态恢复为待打包暂存，是否继续？', '退回待打包', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await returnWaitPackage({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('退回成功')
  tableRef.value?.getTableList()
  void refreshTabCounts()
}

const handleEditLogistics = (row: any) => {
  const params = {
    id: 'logisticsDialog',
    el: '#app',
    data: {
      rowData: { ...row.originalData, shipCode: row.shipCode },
      isEdit: true,
      callback: () => {
        tableRef.value?.getTableList()
        void refreshTabCounts()
      }
    },
    render: LogisticsDialog
  }
  dynamic.show(params)
}

const handleUploadWaybill = (row: any) => {
  const params = {
    id: 'uploadWaybillDialog',
    el: '#app',
    data: {
      rowData: row.originalData,
      callback: () => {
        tableRef.value?.getTableList()
        void refreshTabCounts()
      }
    },
    render: UploadWaybillDialog
  }
  dynamic.show(params)
}

onMounted(() => {
  void refreshTabCounts()
})
</script>

<style lang="scss" scoped>
.shipping-container {
  :deep(.bz-tabs) {
    background: #fff;
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    padding-left: 15px;
  }

  .customer-info {
    text-align: center;

    &--copyable {
      cursor: pointer;

      &:hover {
        color: var(--el-color-primary);
        text-decoration: underline dotted;
      }
    }
  }

  .status-cell {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    gap: 8px;

    .status-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
    }
  }

  .logistics-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;

    &.domestic {
      .logistics-row {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    &.international {
      .logistics-row {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
      }

      .waybill-actions {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
  }
}
</style>
