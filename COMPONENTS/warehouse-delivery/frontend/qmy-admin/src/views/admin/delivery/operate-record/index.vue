<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :filterSearchFields="filterSearchFields"
      :columns="columns"
      :requestApi="operateRecordList"
      :initParam="initParam"
      :searchCol="searchCol"
      :dataCallback="dataCallback"
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
      <template #operateType="{ row }">
        <div v-if="row.operateType == 0">新增交货记录</div>
        <div v-if="row.operateType == 1">删除交货记录</div>
        <div v-if="row.operateType == 2">确认交货记录</div>
      </template>
      <template #pic="{ row }">
        <el-image style="width: 50px; height: 50px" :src="row.pic" :preview-src-list="[row.pic]" hide-on-click-modal />
      </template>
    </bz-table>
  </div>
</template>

<script lang="tsx" setup name="delivery-operate-record">
import { ref, reactive, onMounted } from 'vue'
import { ColumnProps } from '@/interface/table'
import { operateRecordList } from '@/api/admin/delivery'
import { operateStatusList } from '@/constant/yitang/delivery'

const bzTableRef = ref()
const initParam = reactive({})
const filterSearchFields = []
const searchCol = { xs: 2, sm: 3, md: 4, lg: 5, xl: 5 }

window.bzTableRef = bzTableRef

const dataCallback = (data: any) => {
  return {
    list: data?.records ?? [],
    total: Number(data?.total || 0)
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
    label: '操作类型',
    prop: 'operateType',
    enum: operateStatusList,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'el-select',
      key: 'operateType',
      props: {
        placeholder: '请选择',
        clearable: true
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
    label: '变动数量',
    prop: 'num'
  },
  {
    label: '采购单号',
    prop: 'purchaseNo'
  },
  {
    label: '操作时间',
    prop: 'createTime'
  },
  {
    label: '绑定流程单号',
    prop: 'processNo'
  },
  {
    label: '操作人',
    prop: 'operatorNickName'
  },
  {
    label: '操作类型',
    prop: 'operateType'
  }
]

function initLoad() {}

onMounted(() => {
  initLoad()
})
</script>

<style lang="scss" scoped></style>
