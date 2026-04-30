<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getCustomerList"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" v-permission="'sal:yt:customer:save'" @click="handleAdd">新增客户</el-button>
        <el-button v-permission="'sal:yt:customer:setAutoLevel'" @click="handleAutoLevel">自动客户层级规则</el-button>
      </template>
      <template #operation="scope">
        <el-button
          size="small"
          type="primary"
          link
          v-permission="'sal:yt:customer:detail'"
          @click="handleDetail(scope.row)"
        >
          详情
        </el-button>
        <el-button
          size="small"
          type="danger"
          link
          v-permission="'sal:yt:customer:delete'"
          @click="handleDelete(scope.row)"
        >
          删除
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { ColumnProps } from '@/interface/table'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus'
import { View, Hide } from '@element-plus/icons-vue'
import { getCustomerList, deleteCustomer } from '@/api/sed/sales/customer'
import { getAllEmployee } from '@/api/sed/auth/org'
import AutoCustomerLevel from './components/auto-customer-level.vue'
import { desensitize } from '@/utils'
import dayjs from 'dayjs'

const router = useRouter()
const bzTableRef = ref()
const employeeList = ref<any[]>([])
const showCustomerName = ref(false)

const loadAllEmployeeList = async () => {
  const { code, data, message } = await getAllEmployee({})
  if (code !== 200) return ElMessage.warning(message)
  employeeList.value = data || []
}

const toggleCustomerNameVisibility = () => {
  showCustomerName.value = !showCustomerName.value
}

// 搜索列配置
const searchColumns = computed(() => [
  {
    label: '客户编号',
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
    label: '归属',
    prop: 'belongEmployeeId',
    enum: employeeList.value,
    fieldNames: { label: 'nickName', value: 'userId' },
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  }
])

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

// 表格列配置
const columns: ColumnProps[] = [
  {
    label: '客户编号',
    prop: 'code',
    width: 170,
    align: 'center'
  },
  {
    label: '客户名称',
    prop: 'name',
    width: 150,
    align: 'center',
    headerRender: () => {
      return h('div', { style: 'display: flex; align-items: center; justify-content: center; gap: 8px;' }, [
        h('span', '客户名称'),
        h(
          ElIcon,
          {
            style: 'cursor: pointer; font-size: 16px;',
            onClick: (e: Event) => {
              e.stopPropagation()
              toggleCustomerNameVisibility()
            }
          },
          () => h(showCustomerName.value ? View : Hide)
        )
      ])
    },
    render: ({ row }) => {
      return showCustomerName.value ? row.name : desensitize(row.name, 1, 1)
    }
  },
  {
    label: '公司名称',
    prop: 'companyName',
    align: 'center'
  },
  {
    label: '归属',
    prop: 'belongEmployeeName',
    width: 120,
    align: 'center'
  },
  {
    label: '跟进人',
    prop: 'followEmployeeName',
    align: 'center'
  },
  {
    label: '客户类型',
    prop: 'typeValue',
    align: 'center'
  },
  {
    label: '手动层级',
    prop: 'handLevel',
    align: 'center'
  },
  {
    label: '近一年累计金额',
    prop: 'yearOrderAmount',
    width: 150,
    align: 'center',
    render: ({ row }) => {
      return row.yearOrderAmount ? `¥${row.yearOrderAmount.toFixed(2)}` : '-'
    }
  },
  {
    label: '自动层级',
    prop: 'autoCustomerLevel',
    align: 'center'
  },
  {
    label: '标签',
    prop: 'tags',
    width: 150,
    align: 'center',
    render: ({ row }) => {
      if (row.labelList && Array.isArray(row.labelList)) {
        return row.labelList.map((item: any) => item.value).join(', ')
      }
      return '-'
    }
  },
  {
    label: '国家地区',
    prop: 'countryRegionName',
    align: 'center'
  },
  {
    label: '创建时间',
    prop: 'createTime',
    width: 180,
    align: 'center'
  },
  {
    label: '最近跟进时间',
    prop: 'followTime',
    width: 180,
    align: 'center',
    render: ({ row }) => (row.followTime ? dayjs(row.followTime).format('YYYY-MM-DD') : '-')
  },
  {
    label: '最近下单时间',
    prop: 'storeOperationTime',
    width: 180,
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    width: 120,
    fixed: 'right',
    align: 'center'
  }
]

const handleAdd = () => {
  router.push('/sales/customer/add')
}

const handleDetail = (row: any) => {
  router.push({
    path: '/sales/customer/detail',
    query: { id: row.id }
  })
}

const handleAutoLevel = () => {
  const params = {
    id: 'autoCustomerLevel',
    el: '#app',
    data: {},
    render: AutoCustomerLevel
  }
  dynamic.show(params)
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该客户吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteCustomer({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  bzTableRef.value?.getTableList()
}

onMounted(() => {
  loadAllEmployeeList()
})
</script>

<style lang="scss" scoped></style>
