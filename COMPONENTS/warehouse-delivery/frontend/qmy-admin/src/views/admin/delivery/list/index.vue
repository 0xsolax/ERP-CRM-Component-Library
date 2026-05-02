<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :filterSearchFields="filterSearchFields"
      :columns="columns"
      :requestApi="deliveryList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleDeliveryRecord">交货记录</el-button>
        <el-button type="primary" @click="handleOperateRecord">操作记录</el-button>
      </template>
      <template #index="{ row }">
        {{ row.index }}
      </template>
      <template #status="{ row }">
        <div v-if="row.status == 0">暂存</div>
        <div v-if="row.status == 1">采购中</div>
        <div v-if="row.status == 2">已完成</div>
      </template>
      <template #processNo="{ row }">
        <div class="process-no">
          <span>{{ row.processNo }}</span>
          <el-popover ref="popoverRef" :visible="row.showPopover" placement="bottom" :width="220" trigger="click">
            <template #default>
              <el-input v-model="row.processNoCopy" placeholder="请输入" />
              <div style="text-align: right; margin: 10px 0 0">
                <el-button size="small" @click="cancelPopover(row)">取消</el-button>
                <el-button size="small" type="primary" @click="confirmInput(row)">确定</el-button>
              </div>
            </template>
            <template #reference>
              <el-icon :size="20" @click="openPopover(row)" class="edit-icon" style="cursor: pointer; margin-left: 5px">
                <Edit />
              </el-icon>
            </template>
          </el-popover>
        </div>
      </template>
      <template #operation="{ row }">
        <el-button size="small" type="primary" link @click="handleDetail('查看', row)">查看</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="tsx" setup name="delivery-list">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ColumnProps } from '@/interface/table'
import { deliveryList, bindProcessNo } from '@/api/admin/delivery'
import { statusList } from '@/constant/yitang/delivery'
import deliveryDetail from './components/delivery-detail.vue'
import { dynamic } from '@bzlab/bz-core'

const popoverRef = ref()
const router = useRouter()
const bzTableRef = ref()
const initParam = reactive({})
const filterSearchFields = []

;(window as any).bzTableRef = bzTableRef

const openPopover = row => {
  bzTableRef.value.tableData.forEach(item => (item.showPopover = false))
  row.showPopover = true
}

const confirmInput = async row => {
  if (!row.processNoCopy) return ElMessage.warning('请输入流程单号')
  const { code, message } = await bindProcessNo({ purchaseNo: row.purchaseNo, processNo: row.processNoCopy })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  bzTableRef.value.getTableList()
  row.showPopover = false
}

const cancelPopover = row => {
  row.showPopover = false
}

const handleDetail = (title, rowData) => {
  const params = {
    id: 'deliveryDetail',
    el: '#app',
    data: {
      searchParams: bzTableRef.value.searchParams,
      title,
      rowData,
      callback: () => {
        bzTableRef.value.getTableList()
      }
    },
    render: deliveryDetail
  }
  dynamic.show(params)
}

const handleDeliveryRecord = row => {
  row
  router.push({ path: '/delivery/record' })
}

const handleOperateRecord = row => {
  row
  router.push({ path: '/delivery/operate-record' })
}

const dataCallback = (data: any) => {
  let records = data?.records ?? []
  let total = Number(data?.total || 0)
  let paginationParams = bzTableRef.value.paginationParams
  records.forEach((item, idx) => {
    item.tempIndex = idx
    item.index = idx + 1 + (paginationParams.pageNum - 1) * paginationParams.pageSize
    item.processNoCopy = item.processNo
    item.showPopover = false
  })

  const deliveryDetail = dynamic.getInstances('deliveryDetail')
  if (deliveryDetail) {
    const component = deliveryDetail.vm.component
    const index = component.attrs.rowData.tempIndex
    component.attrs.rowData.productList = records[index].productList
    component.exposeProxy.fetchDetail()
  }

  return {
    list: records,
    total: total
  }
}

const searchColumns = [
  {
    label: '采购单号',
    prop: 'purchaseNo',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  // {
  //   label: '供应商ID',
  //   prop: 'supplierId',
  //   search: {
  //     el: 'el-input',
  //     props: {
  //       placeholder: '请输入',
  //       clearable: true
  //     }
  //   }
  // },
  {
    label: '绑定流程单号',
    prop: 'processNo',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '状态',
    prop: 'status',
    enum: statusList,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'el-select',
      key: 'status',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  }
]

const columns: ColumnProps[] = [
  {
    label: '序号',
    prop: 'index'
  },
  {
    label: '采购时间',
    prop: 'purchaseTime'
  },
  {
    label: '采购单号',
    prop: 'purchaseNo'
  },
  {
    label: '采购数量',
    prop: 'purchaseNum'
  },
  {
    label: '确认交货数量',
    prop: 'deliveredNum'
  },
  {
    label: '入库分配数量',
    prop: 'deliveredNum22'
  },
  {
    label: '欠数',
    prop: 'oweNum'
  },
  {
    label: '采购单备注',
    prop: 'remark'
  },
  {
    label: '交货时间',
    prop: 'deliveryTime'
  },
  {
    label: '绑定流程单号',
    prop: 'processNo',
    width: 200
  },
  {
    label: '交货状态',
    prop: 'status'
  },
  {
    label: '操作',
    prop: 'operation',
    fixed: 'right',
    width: 100
  }
]

function initLoad() {
  // document.addEventListener('click', (e: any) => {
  //   const tagName = e.target.tagName
  //   const isPopover = e.target.closest('.el-popover')
  //   const isTriggerBtn = e.target.closest('.el-button')
  //   if (tagName === 'svg' || tagName === 'path') return
  //   if (!isPopover && !isTriggerBtn) {
  //     if (!bzTableRef.value) return
  //     bzTableRef.value.tableData.forEach(row => (row.showPopover = false))
  //   }
  // })
}

onMounted(() => {
  initLoad()
})
</script>

<style lang="scss" scoped>
.process-no {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
