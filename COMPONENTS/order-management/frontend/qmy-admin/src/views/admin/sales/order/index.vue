<template>
  <div class="table-box">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :searchCol="{ xs: 2, sm: 3, md: 4, lg: 8, xl: 8 }"
      :columns="columns"
      :requestApi="getOrderListByTab"
      :dataCallback="dataCallback"
      :searchClick="handleSearchWithCounts"
      :resetClick="handleResetWithCounts"
      selectId="id"
      :initParam="initParam"
      :hideTabs="false"
      :tabsColumns="tabsColumns"
      :tabsClick="handleTabsClick"
    >
      <template #status="scope">
        <span>{{ scope.row.statusText }}</span>
        <el-tooltip
          v-if="scope.row.status === 2 && scope.row.auditOpinion"
          :content="scope.row.auditOpinion"
          placement="top"
        >
          <el-icon style="margin-left: 4px; cursor: pointer; vertical-align: middle">
            <WarningFilled />
          </el-icon>
        </el-tooltip>
      </template>

      <template #tableHeader>
        <el-button type="primary" v-permission="'sal:yt:order:save'" @click="handleAddOrder">新增订单</el-button>
      </template>

      <template #operation="scope">
        <template v-if="scope.row.status === 1">
          <el-button
            size="small"
            type="warning"
            link
            v-permission="'sal:yt:order:audit'"
            @click="handleReview(scope.row)"
          >
            审核
          </el-button>
        </template>
        <template v-if="scope.row.status === 2">
          <el-tooltip v-if="scope.row.auditOpinion" :content="scope.row.auditOpinion" placement="top">
            <el-button
              size="small"
              type="danger"
              link
              v-permission="'sal:yt:order:audit'"
              @click="handleEdit(scope.row, 'rejected')"
            >
              审核拒绝
              <el-icon style="margin-left: 2px"><WarningFilled /></el-icon>
            </el-button>
          </el-tooltip>
          <el-button
            v-else
            size="small"
            type="danger"
            link
            v-permission="'sal:yt:order:audit'"
            @click="handleEdit(scope.row, 'rejected')"
          >
            审核拒绝
          </el-button>
        </template>
        <template v-if="scope.row.status === 3">
          <el-button
            size="small"
            type="primary"
            link
            v-permission="'sal:yt:order:save'"
            @click="handleEdit(scope.row, 'edit')"
          >
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            link
            v-permission="'sal:yt:order:delete'"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
        <template v-if="scope.row.status === 4">
          <el-button
            size="small"
            type="primary"
            link
            v-permission="'sal:yt:order:audit'"
            @click="handleEdit(scope.row, 'approved')"
          >
            审核通过
          </el-button>
        </template>
        <template v-if="scope.row.status === 4 || scope.row.status === 5">
          <el-button
            size="small"
            type="primary"
            link
            v-permission="'sal:yt:order:detail'"
            @click="handleDetail(scope.row)"
          >
            详情
          </el-button>
        </template>
        <el-button
          size="small"
          type="primary"
          link
          v-permission="'sal:yt:order:export'"
          @click="handleExport(scope.row)"
        >
          导出
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="sales-order">
import { ref, reactive, computed, onActivated, nextTick } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { WarningFilled } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import exportDialog from './components/export-dialog.vue'
import { getOrderList, deleteOrder } from '@/api/admin/sales/order'
import { getAllEmployee } from '@/api/admin/auth/org'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPlatformLabel,
  orderStatusList,
  orderTypeList,
  getOrderStatusLabel,
  getCurrencyLabel,
  getDeliveryTypeLabel,
  getIsWarehouseDeliveryLabel
} from '@/constant/yitang/sales'
import dayjs from 'dayjs'

const router = useRouter()
const tagsStore = useTagsStore()
const tableRef = ref()
const employeeList = ref<any[]>([])
const activeTab = ref('all')
const initParam = reactive({
  status: null as number | null,
  itemStatus: null as string | null
})
const tabCounts = reactive<Record<string, number>>({})
const hasInitialized = ref(true)
const fullOrderListCache = ref<any[]>([])
const fullOrderListCacheKey = ref('')
const fullOrderListPendingKey = ref('')
const fullOrderListPendingPromise = ref<Promise<any[]> | null>(null)
const FULL_LIST_PAGE_SIZE = 1000

onBeforeRouteLeave(to => {
  if (!to.path.startsWith('/sales/order')) {
    tagsStore.delCachedView('sales-order')
  }
})

const loadAllEmployeeList = async () => {
  const { code, data, message } = await getAllEmployee({})
  if (code !== 200) return ElMessage.warning(message)
  employeeList.value = data || []
}

const dataCallback = (data: any) => {
  const list =
    data?.list?.map((item: any, index: number) => {
      const currencyName = item.currency == '0' ? '￥' : '$'
      const finishTimeList = [item.orderFinishTime, item.receiveFinishTime, item.shippingReceiveFinishTime].filter(
        (time: string | null | undefined) => Boolean(time)
      )
      const completionTime =
        finishTimeList.length === 3
          ? finishTimeList
              .map((time: string) => dayjs(time))
              .sort((prev, next) => next.valueOf() - prev.valueOf())[0]
              .format('YYYY-MM-DD HH:mm:ss')
          : '-'
      return {
        ...item,
        statusText: getOrderStatusLabel(item.orderStatus),
        shippingMethod: getDeliveryTypeLabel(item.shippingMethod) || '-',
        orderTime: item.orderTime ? dayjs(item.orderTime).format('YYYY-MM-DD') : '-',
        deliveryTime: item.deliveryTime ? dayjs(item.deliveryTime).format('YYYY-MM-DD') : '-',
        createTime: item.submitOrderTime || '-',
        completionTime,
        remark: item.remark || '-',
        isInboundDelivery:
          item.isInboundDelivery != null ? getIsWarehouseDeliveryLabel(item.isInboundDelivery ? '1' : '0') : '-',
        shippingCost:
          item.isCollectedShippingCost === 1 ? `${currencyName}${item.shippingCost || 0}` : `${currencyName}0`,
        discountAmount: `${currencyName}${item.discountAmount || 0}`,
        amount: `${currencyName}${item.amount || 0}`,
        rowIndex: index
      }
    }) || []

  return {
    list,
    total: Number(data?.total || 0)
  }
}

const formatTabCount = (count: number | undefined) => {
  if (!count || count < 0) return '0'
  return count > 99 ? '99+' : String(count)
}

const buildTabLabel = (label: string, key: string) => `${label} ${formatTabCount(tabCounts[key])}`

const tabsColumns = computed(() => [
  {
    label: buildTabLabel('全部', 'all'),
    prop: 'all',
    active: true
  },
  {
    label: buildTabLabel('审核中', 'reviewing'),
    prop: 'reviewing'
  },
  ...orderStatusList.map(item => ({
    label: buildTabLabel(item.label, `order-status-${item.value}`),
    prop: `order-status-${item.value}`
  }))
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

const getOrderStatusTabValue = (tabName: string) =>
  tabName.startsWith('order-status-') ? tabName.replace('order-status-', '') : ''

const buildBaseSearchParams = (rawParams: Record<string, any> = {}) => {
  const params: Record<string, any> = {}
  Object.keys(rawParams).forEach(key => {
    if (['pageNum', 'pageSize', 'status', 'itemStatus', 'orderStatus'].includes(key)) return
    const value = rawParams[key]
    if (value || value === false || value === 0) {
      params[key] = value
    }
  })
  return params
}

const fetchTabCount = async (key: string, params: Record<string, any>) => {
  const { code, data } = await getOrderList({
    ...params,
    pageNum: 1,
    pageSize: 1
  })
  tabCounts[key] = code === 200 ? Number(data?.total || 0) : 0
}

const getTabBaseSearchParams = () => {
  return buildBaseSearchParams(getCurrentSearchParams())
}

const resetFullOrderListCache = () => {
  fullOrderListCacheKey.value = ''
  fullOrderListCache.value = []
  fullOrderListPendingKey.value = ''
  fullOrderListPendingPromise.value = null
}

const fetchFullOrderList = async (params: Record<string, any>, force = false) => {
  const baseParams = buildBaseSearchParams(params)
  const cacheKey = JSON.stringify(baseParams)
  if (!force && fullOrderListCacheKey.value === cacheKey) {
    return fullOrderListCache.value
  }

  if (fullOrderListPendingKey.value === cacheKey && fullOrderListPendingPromise.value) {
    return fullOrderListPendingPromise.value
  }

  const requestFullList = async (pageSize: number) => {
    const { code, data } = await getOrderList({
      ...baseParams,
      status: null,
      itemStatus: null,
      pageNum: 1,
      pageSize
    })
    return {
      code,
      list: data?.list || [],
      total: Number(data?.total || 0)
    }
  }

  const pendingPromise = (async () => {
    let { code, list, total } = await requestFullList(FULL_LIST_PAGE_SIZE)
    if (code !== 200) {
      fullOrderListCacheKey.value = cacheKey
      fullOrderListCache.value = []
      return []
    }

    if (total > list.length && total > FULL_LIST_PAGE_SIZE) {
      const fullResult = await requestFullList(total)
      code = fullResult.code
      list = fullResult.list
      total = fullResult.total
    }

    fullOrderListCacheKey.value = cacheKey
    fullOrderListCache.value = list
    return list
  })()

  fullOrderListPendingKey.value = cacheKey
  fullOrderListPendingPromise.value = pendingPromise

  try {
    return await pendingPromise
  } finally {
    if (fullOrderListPendingPromise.value === pendingPromise) {
      fullOrderListPendingKey.value = ''
      fullOrderListPendingPromise.value = null
    }
  }
}

const getOrderListByTab = async (params: Record<string, any>) => {
  if (activeTab.value === 'reviewing') {
    return getOrderList(params)
  }

  const selectedStatus = getOrderStatusTabValue(activeTab.value)
  if (!selectedStatus) {
    return getOrderList(params)
  }

  const fullList = await fetchFullOrderList(params)
  const filteredList = fullList.filter(item => String(item.orderStatus) === selectedStatus)
  const pageNum = Number(params.pageNum || 1)
  const pageSize = Number(params.pageSize || 15)
  const start = (pageNum - 1) * pageSize

  return Promise.resolve({
    code: 200,
    data: {
      list: filteredList.slice(start, start + pageSize),
      total: filteredList.length
    }
  })
}

const refreshTabCounts = async (force = false) => {
  const searchParams = getTabBaseSearchParams()
  const [list] = await Promise.all([
    fetchFullOrderList(searchParams, force),
    fetchTabCount('reviewing', { ...searchParams, status: 1, itemStatus: null })
  ])

  tabCounts.all = list.length

  const statusMap = list.reduce((map: Record<string, number>, item: any) => {
    const key = `order-status-${item.orderStatus}`
    map[key] = (map[key] || 0) + 1
    return map
  }, {})

  orderStatusList.forEach(item => {
    tabCounts[`order-status-${item.value}`] = statusMap[`order-status-${item.value}`] || 0
  })
}

const handleTabsClick = (tab: any) => {
  console.log('切换到tab:', tab.paneName)
  activeTab.value = String(tab.paneName)
  if (tab.paneName === 'all') {
    initParam.status = null
    initParam.itemStatus = null
  } else if (tab.paneName === 'reviewing') {
    initParam.status = 1
    initParam.itemStatus = null
  } else {
    initParam.status = null
    initParam.itemStatus = null
  }
  tableRef.value?.getTableList()
}

const handleSearchWithCounts = () => {
  resetFullOrderListCache()
  void refreshTabCounts(true)
}

const handleResetWithCounts = () => {
  Object.keys(tabCounts).forEach(key => {
    tabCounts[key] = 0
  })
  resetFullOrderListCache()
  void nextTick(() => refreshTabCounts(true))
}

// 搜索配置
const searchColumns = computed(() =>
  [
    {
      label: '订单编号',
      prop: 'code',
      search: {
        el: 'el-input',
        props: {
          placeholder: '请输入',
          clearable: true
        }
      }
    },
    {
      label: '客户名称',
      prop: 'customerName',
      search: {
        el: 'el-input',
        props: {
          placeholder: '请输入',
          clearable: true
        }
      }
    },
    {
      label: '平台单号',
      prop: 'platformOrderCode',
      search: {
        el: 'el-input',
        props: {
          placeholder: '请输入',
          clearable: true
        }
      }
    },
    {
      label: '产品编号',
      prop: 'productCode',
      search: {
        el: 'el-input',
        props: {
          placeholder: '请输入',
          clearable: true
        }
      }
    },
    {
      label: '业务员',
      prop: 'saleEmployeeId',
      enum: employeeList.value,
      fieldNames: { label: 'nickName', value: 'userId' },
      search: {
        el: 'el-select',
        props: {
          placeholder: '请选择',
          clearable: true
        }
      }
    },
    {
      label: '跟进人',
      prop: 'followEmployeeId',
      enum: employeeList.value,
      fieldNames: { label: 'nickName', value: 'userId' },
      search: {
        el: 'el-select',
        props: {
          placeholder: '请选择',
          clearable: true
        }
      }
    },
    {
      label: '订单类型',
      prop: 'orderType',
      search: {
        el: 'el-select',
        props: {
          placeholder: '请选择',
          clearable: true
        }
      },
      enum: orderTypeList
    },
    {
      label: '订单状态',
      prop: 'itemStatus',
      isShow: false,
      search: {
        el: 'el-select',
        props: {
          placeholder: '请选择',
          clearable: true
        }
      },
      enum: orderStatusList
    }
  ].filter((item: any) => item.isShow !== false)
)

// 表格列配置
const columns: ColumnProps[] = [
  {
    label: '订单编号',
    prop: 'code',
    align: 'center',
    width: 180,
    fixed: 'left'
  },
  {
    label: '客户编号',
    prop: 'customerCode',
    isShow: false,
    align: 'center',
    width: 140
  },
  {
    label: '客户名称',
    prop: 'customerName',
    align: 'center',
    width: 140,
    fixed: 'left'
  },
  {
    label: '平台',
    prop: 'sourcePlatform',
    align: 'center',
    width: 100,
    render: (scope: any) => getPlatformLabel(scope.row.sourcePlatform)
  },
  {
    label: '平台单号',
    prop: 'platformOrderCode',
    align: 'center',
    width: 200
  },
  {
    label: '币种',
    prop: 'currency',
    isShow: false,
    align: 'center',
    render: (scope: any) => getCurrencyLabel(scope.row.currency)
  },
  {
    label: '总金额',
    prop: 'amount',
    align: 'center',
    width: 120
  },
  {
    label: '优惠金额',
    prop: 'discountAmount',
    align: 'center',
    width: 120
  },
  {
    label: '运费金额',
    prop: 'shippingCost',
    align: 'center',
    width: 120
  },
  {
    label: '订单备注',
    prop: 'remark',
    align: 'center',
    width: 150
  },
  {
    label: '订单状态',
    prop: 'statusText',
    align: 'center',
    width: 120
  },
  {
    label: '发货形式',
    prop: 'shippingMethod',
    align: 'center',
    width: 120
  },
  {
    label: '是否入库发货',
    prop: 'isInboundDelivery',
    align: 'center',
    width: 120
  },
  {
    label: '下单时间',
    prop: 'orderTime',
    align: 'center',
    width: 120
  },
  {
    label: '交货时间',
    prop: 'deliveryTime',
    align: 'center',
    width: 120
  },
  {
    label: '提交时间',
    prop: 'createTime',
    align: 'center',
    width: 120,
    render: (scope: any) => scope.row.createTime || '-'
  },
  {
    label: '完成时间',
    prop: 'completionTime',
    align: 'center',
    width: 120
  },
  {
    label: '业务员',
    prop: 'salesEmployeeName',
    align: 'center',
    width: 150,
    render: (scope: any) => `${scope.row.salesEmployeeName || '-'}（${scope.row.saleRatio || 0}%）`
  },
  {
    label: '跟进人',
    prop: 'followEmployeeName',
    align: 'center',
    width: 150,
    render: (scope: any) =>
      scope.row.followEmployeeName ? `${scope.row.followEmployeeName}（${scope.row.followRatio || 0}%）` : '-'
  },
  {
    label: '操作',
    prop: 'operation',
    width: 200,
    fixed: 'right',
    align: 'center'
  }
]

const handleAddOrder = () => {
  tagsStore.delCachedView('sales-order')
  tagsStore.delCachedView('sales-order-add')
  router.push({ path: '/sales/order/add', query: { action: 'add' } })
}

const handleEdit = (row: any, actionType = 'edit') => {
  console.log('编辑订单:', row, actionType)
  tagsStore.delCachedView('sales-order-edit')
  router.push({
    path: '/sales/order/edit',
    query: {
      id: row.id,
      action: actionType // edit, approved, rejected
    }
  })
}

// 审核订单
const handleReview = (row: any) => {
  console.log('审核订单:', row)
  tagsStore.delCachedView('sales-order-edit')
  router.push({
    path: '/sales/order/edit',
    query: {
      id: row.id,
      action: 'review'
    }
  })
}

// 父订单详情
const handleDetail = (row: any) => {
  console.log('父订单详情:', row)
  router.push({
    path: '/sales/order/detail',
    query: {
      id: row.id
    }
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteOrder({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  resetFullOrderListCache()
  tableRef.value?.getTableList()
  void refreshTabCounts(true)
}

const handleExport = (row: any) => {
  console.log('导出订单:', row)
  const params = {
    id: 'exportDialog',
    el: '#app',
    data: {
      orderId: row.id
    },
    render: exportDialog
  }
  dynamic.show(params)
}

onActivated(() => {
  loadAllEmployeeList()
  resetFullOrderListCache()
  void refreshTabCounts(true)
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style scoped lang="scss">
.table-box {
  :deep(.bz-tabs) {
    background: #fff;
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    padding-left: 15px;
  }
}
</style>
