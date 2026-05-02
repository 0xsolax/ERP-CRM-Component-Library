<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getSupplierList"
      :dataCallback="dataCallback"
      selectId="id"
      rowKey="id"
    >
      <template #tableHeader>
        <el-button type="primary" v-permission="'pur:yt:purchaseSupplier:add'" @click="handleAdd">新增供应商</el-button>
      </template>

      <template #labelList="{ row }">
        <el-tag v-for="tag in row.labelList" :key="tag.id" size="small" style="margin-right: 4px">
          {{ tag.value }}
        </el-tag>
        <span v-if="!row.labelList || row.labelList.length === 0">-</span>
      </template>

      <template #operation="scope">
        <el-button
          size="small"
          type="primary"
          link
          v-permission="'pur:yt:purchaseSupplier:detail'"
          @click="handleDetail(scope.row)"
        >
          详情
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="purchase-supplier">
import { ref, computed, onActivated } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { ColumnProps } from '@/interface/table'
import { getSupplierList } from '@/api/admin/purchase/supplier'
import { useTagsStore } from '@/views/admin/store/modules/tags'

const router = useRouter()
const bzTableRef = ref()
const tagsStore = useTagsStore()
const hasInitialized = ref(true)

onBeforeRouteLeave(to => {
  if (!to.path.startsWith('/purchase/supplier')) {
    tagsStore.delCachedView('purchase-supplier')
  }
})

const dataCallback = (data: any) => {
  const list = data?.list || []
  return {
    list,
    total: Number(data?.total || 0)
  }
}

// 搜索配置
const searchColumns = computed(() => [
  {
    label: '供应商ID',
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
    prop: 'name',
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
    label: '供应商ID',
    prop: 'code',
    align: 'center',
    width: 160
  },
  {
    label: '供应商名称',
    prop: 'name',
    align: 'center'
  },
  {
    label: '简称',
    prop: 'shortName',
    align: 'center'
  },
  {
    label: '地址',
    prop: 'address',
    align: 'center'
  },
  {
    label: '标签',
    prop: 'labelList',
    align: 'center'
  },
  {
    label: '备注',
    prop: 'remark',
    align: 'center'
  },
  {
    label: '创建时间',
    prop: 'createTime',
    align: 'center'
  },
  {
    label: '操作',
    prop: 'operation',
    width: 100,
    fixed: 'right',
    align: 'center'
  }
]

const handleAdd = () => {
  tagsStore.delCachedView('purchase-supplier')
  router.push('/purchase/supplier/add')
}

const handleDetail = (row: any) => {
  router.push({
    path: '/purchase/supplier/detail',
    query: { id: row.id }
  })
}

onActivated(() => {
  if (!hasInitialized.value) {
    bzTableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style scoped lang="scss"></style>
