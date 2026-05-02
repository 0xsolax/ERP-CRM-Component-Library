<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :searchCol="searchCol"
      :customGridConfig="customGridConfig"
      :requestApi="getPurchaseList"
      :beforeSearchSubmit="beforeSearchSubmit"
      :dataCallback="dataCallback"
      :searchClick="handleSearchWithCounts"
      :resetClick="handleResetWithCounts"
      selectId="id"
      rowKey="id"
      :initParam="initParam"
      :hideTabs="false"
      :tabsColumns="tabsColumns"
      :tabsClick="handleTabsClick"
    >
      <template #tableHeader>
        <!-- <el-button type="primary" v-permission="'pur:yt:purchase:createOrUpdate'" @click="handleAdd">新增</el-button> -->
      </template>
      <template #status="scope">
        <span>{{ getPurchaseStatusLabel(scope.row.status) }}</span>
      </template>

      <template #operation="scope">
        <el-button
          v-if="scope.row.status != 0"
          size="small"
          type="primary"
          link
          v-permission="'pur:yt:purchase:detail'"
          @click="handleDetail(scope.row)"
        >
          详情
        </el-button>
        <el-button
          v-if="scope.row.status == 0"
          size="small"
          type="primary"
          link
          v-permission="'pur:yt:purchase:createOrUpdate'"
          @click="handleEdit(scope.row)"
        >
          编辑
        </el-button>
        <el-button
          size="small"
          type="primary"
          link
          v-permission="'pur:yt:purchase:export'"
          @click="handleExport(scope.row)"
        >
          导出
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="purchase-purchased">
import { ref, computed, onActivated, reactive, nextTick } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { ColumnProps } from '@/interface/table'
import { ElMessage, ElMessageBox } from 'element-plus'
import { purchaseStatusList, getPurchaseStatusLabel } from '@/constant/yitang/purchase'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getYitangAdminToken, getYitangAdminTenantInfo } from '@/utils/auth'
import { getPurchaseList } from '@/api/admin/purchase/purchased'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import dayjs from 'dayjs'

const searchCol = { xs: 2, sm: 3, md: 4, lg: 5, xl: 6 }
const customGridConfig = {
  xs: '1fr',
  sm: '1fr 1fr 1fr',
  md: '1fr 1fr 1fr 1fr',
  lg: '19% 19% 19% 19% 19%',
  xl: '15% 15% 15% 15% 15% 1fr'
}

const router = useRouter()
const bzTableRef = ref()
const tagsStore = useTagsStore()
const hasInitialized = ref(true)
const activeTab = ref('all')
const initParam = reactive({
  status: null as number | string | null
})
const tabCounts = reactive<Record<string, number>>({})

onBeforeRouteLeave(to => {
  if (!to.path.startsWith('/purchase/purchased')) {
    tagsStore.delCachedView('purchase-purchased')
  }
})

const dataCallback = (data: any) => {
  const list = data?.list || []
  return {
    list,
    total: Number(data?.total || 0)
  }
}

const beforeSearchSubmit = (params: any) => {
  const { deliveryTime, ...rest } = params
  return {
    ...rest,
    deliveryTimeStart: deliveryTime?.[0] || null,
    deliveryTimeEnd: deliveryTime?.[1] || null
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
  ...purchaseStatusList.map(item => ({
    label: buildTabLabel(item.label, `status-${item.value}`),
    prop: `status-${item.value}`
  }))
])

const getCurrentSearchParams = () => {
  const rawSearchParams = bzTableRef.value?.searchParams || {}
  const params: Record<string, any> = {}
  Object.keys(rawSearchParams).forEach(key => {
    const value = rawSearchParams[key]
    if (value || value === false || value === 0) {
      params[key] = value
    }
  })
  return params
}

const buildBaseSearchParams = (rawParams: Record<string, any> = {}) => {
  const { deliveryTime, ...rest } = rawParams
  const params: Record<string, any> = {}
  Object.keys(rest).forEach(key => {
    if (['pageNum', 'pageSize', 'status'].includes(key)) return
    const value = rest[key]
    if (value || value === false || value === 0) {
      params[key] = value
    }
  })
  params.deliveryTimeStart = deliveryTime?.[0] || null
  params.deliveryTimeEnd = deliveryTime?.[1] || null
  return params
}

const fetchTabCount = async (key: string, params: Record<string, any>) => {
  const { code, data } = await getPurchaseList({
    ...params,
    pageNum: 1,
    pageSize: 1
  })
  tabCounts[key] = code === 200 ? Number(data?.total || 0) : 0
}

const refreshTabCounts = async () => {
  const searchParams = buildBaseSearchParams(getCurrentSearchParams())
  await Promise.all([
    fetchTabCount('all', { ...searchParams, status: null }),
    ...purchaseStatusList.map(item => fetchTabCount(`status-${item.value}`, { ...searchParams, status: item.value }))
  ])
}

const handleTabsClick = (tab: any) => {
  activeTab.value = String(tab.paneName)
  if (tab.paneName === 'all') {
    initParam.status = null
  } else {
    initParam.status = String(tab.paneName).replace('status-', '')
  }
  bzTableRef.value?.getTableList()
}

const handleSearchWithCounts = () => {
  void refreshTabCounts()
}

const handleResetWithCounts = () => {
  Object.keys(tabCounts).forEach(key => {
    tabCounts[key] = 0
  })
  void nextTick(() => refreshTabCounts())
}

const searchColumns = computed(() => [
  {
    label: '采购单号',
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
    label: '供应商名称',
    prop: 'supplierName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '订单编号',
    prop: 'orderCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '产品ID',
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
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
])

// 表格列配置
const columns: ColumnProps[] = [
  {
    label: '采购单号',
    prop: 'code',
    align: 'center',
    width: 180
  },
  {
    label: '供应商名称',
    prop: 'supplierName',
    align: 'center'
  },
  {
    label: '1688单号',
    prop: 'orderPlatformCode',
    align: 'center'
  },
  {
    label: '采购总价',
    prop: 'totalAmount',
    align: 'center',
    render: (scope: any) => (scope.row.totalAmount ? `¥${scope.row.totalAmount}` : '-')
  },
  {
    label: '运费',
    prop: 'shippingCost',
    align: 'center',
    render: (scope: any) => (scope.row.shippingCost ? `¥${scope.row.shippingCost}` : '¥0.00')
  },
  {
    label: '优惠',
    prop: 'discountAmount',
    align: 'center',
    render: (scope: any) => (scope.row.discountAmount ? `¥${scope.row.discountAmount}` : '¥0.00')
  },
  {
    label: '采购单备注',
    prop: 'remark',
    align: 'center'
  },
  {
    label: '采购时间',
    prop: 'createTime',
    align: 'center',
    render: (scope: any) => (scope.row.createTime ? dayjs(scope.row.createTime).format('YYYY-MM-DD') : '-')
  },
  {
    label: '交货时间',
    prop: 'deliveryTime',
    align: 'center',
    render: (scope: any) => (scope.row.deliveryTime ? dayjs(scope.row.deliveryTime).format('YYYY-MM-DD') : '-')
  },
  {
    label: '是否入库发货',
    prop: 'isInboundDelivery',
    align: 'center',
    width: 120,
    render: (scope: any) => (scope.row.isInboundDelivery ? '入库发货' : '供应商发货')
  },
  {
    label: '状态',
    prop: 'status',
    align: 'center'
  },
  {
    label: '未入库数量',
    prop: 'waitEnterNumber',
    align: 'center'
  },
  {
    label: '提交日期',
    prop: 'submitPurchaseTime',
    align: 'center',
    width: 120,
    render: (scope: any) =>
      scope.row.submitPurchaseTime ? dayjs(scope.row.submitPurchaseTime).format('YYYY-MM-DD HH:mm:ss') : '-'
  },
  {
    label: '完成时间',
    prop: 'completedTime',
    align: 'center',
    width: 120
  },
  {
    label: '操作',
    prop: 'operation',
    width: 150,
    fixed: 'right',
    align: 'center'
  }
]

const handleAdd = () => {
  tagsStore.delCachedView('purchase-purchased')
  router.push('/purchase/purchased/add')
}
handleAdd

const handleEdit = (row: any) => {
  router.push({ path: '/purchase/purchased/add', query: { id: row.id } })
}

const handleDetail = (row: any) => {
  router.push({ path: '/purchase/purchased/detail', query: { id: row.id } })
}

const handleExport = async (row: any) => {
  await ElMessageBox.confirm(`确定要导出采购单 ${row.code} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { status, message } = await downloadAxiosBlobFile({
    url: `${import.meta.env.VITE_APP_YITANG_BASE_API}/pur/yt/purchase/export`,
    headers: {
      'qiaomoyun-tenant': getYitangAdminTenantInfo()?.id,
      'qiaomoyun-token': getYitangAdminToken()
    },
    params: {
      purchaseId: row.id
    },
    method: 'get'
  })
  if (status !== 200) return ElMessage.warning(message)
  ElMessage.success('导出成功')
}

onActivated(() => {
  if (!hasInitialized.value) {
    bzTableRef.value?.getTableList()
  }
  void refreshTabCounts()
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
