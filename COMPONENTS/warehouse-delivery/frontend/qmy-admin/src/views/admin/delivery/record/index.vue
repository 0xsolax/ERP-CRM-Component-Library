<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :filterSearchFields="filterSearchFields"
      :columns="columns"
      :requestApi="deliveryRecordList"
      :initParam="initParam"
      :searchCol="searchCol"
      :dataCallback="dataCallback"
      :resetClick="handleResetClick"
    >
      <template #productImg="{ row }">
        <el-image
          style="width: 50px; height: 50px"
          :src="row.productImg"
          :preview-src-list="[row.productImg]"
          hide-on-click-modal
          preview-teleported
        />
      </template>
      <template #status="{ row }">
        <div v-if="row.status == 0">未确认</div>
        <div v-if="row.status == 1">已确认</div>
      </template>
      <template #pic="{ row }">
        <el-image style="width: 50px; height: 50px" :src="row.pic" :preview-src-list="[row.pic]" hide-on-click-modal />
      </template>
      <template #operation="{ row }">
        <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="tsx" setup name="delivery-record">
import { ref, reactive, onMounted } from 'vue'
import { ColumnProps } from '@/interface/table'
import { useConfirm } from '@/hooks/handle/use-handle'
import { deliveryRecordList, deleteDeliveryRecord } from '@/api/admin/delivery'
import { useRoute } from 'vue-router'

const bzTableRef = ref()
const initParam = reactive<any>({})
const filterSearchFields = []
const searchCol = { xs: 2, sm: 3, md: 4, lg: 5, xl: 5 }
const route = useRoute()

console.log('route', route.query)

if (route.query) {
  initParam.purchaseNo = route.query.purchaseNo
  initParam.supplierSkuName = route.query.supplierSkuName
}

window.bzTableRef = bzTableRef

const handleResetClick = () => {
  initParam.purchaseNo = null
  initParam.supplierSkuName = null
  bzTableRef.value.searchInitParams.purchaseNo = null
  bzTableRef.value.searchInitParams.supplierSkuName = null
}

const handleDelete = async row => {
  const message = `确认删除?`
  await useConfirm(deleteDeliveryRecord, { recordId: row.recordId }, message)
  bzTableRef.value.getTableList()
}

const dataCallback = (data: any) => {
  return {
    list: data?.records ?? [],
    total: Number(data?.total || 0)
  }
}

const searchColumns = [
  {
    label: '供应商编号',
    prop: 'supplierSkuNumber',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '供应商规格',
    prop: 'supplierSkuName',
    search: {
      el: 'el-input',
      defaultValue: initParam.supplierSkuName,
      props: {
        placeholder: '请输入',
        clearable: true
      },
      event: {
        input: val => {
          initParam.supplierSkuName = val
        }
      }
    }
  },
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
    label: '采购单号',
    prop: 'purchaseNo',
    search: {
      el: 'el-input',
      defaultValue: initParam.purchaseNo,
      props: {
        placeholder: '请输入',
        clearable: true
      },
      event: {
        input: val => {
          initParam.purchaseNo = val
        }
      }
    }
  }
]

const columns: ColumnProps[] = [
  {
    label: '供应商编号',
    prop: 'supplierSkuNumber'
  },
  {
    label: '供应商规格',
    prop: 'supplierSkuName'
  },
  {
    label: '图片',
    prop: 'productImg',
    showOverflowTooltip: false
  },
  {
    label: '供应商交货数量',
    prop: 'supplierNum'
  },
  {
    label: '确认交货数量',
    prop: 'deliveredNum'
  },
  {
    label: '采购单号',
    prop: 'purchaseNo'
  },
  {
    label: '记录交货时间',
    prop: 'createTime'
  },
  {
    label: '绑定流程单号',
    prop: 'processNo'
  },
  {
    label: '状态',
    prop: 'status'
  },
  {
    label: '操作人',
    prop: 'operatorNickName'
  },
  {
    label: '操作',
    prop: 'operation',
    fixed: 'right',
    width: 100
  }
]

function initLoad() {}

onMounted(() => {
  initLoad()
})
</script>

<style lang="scss" scoped></style>
