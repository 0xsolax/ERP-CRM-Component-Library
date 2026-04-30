<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="queryRoleList"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button v-permission="'sys:role:create'" type="primary" @click="handleAddRole">添加角色</el-button>
      </template>
      <template #enabled="scope">
        <el-switch
          v-model="scope.row.statusValue"
          v-permission="'sys:role:updateStatus'"
          @change="handleStatusChange(scope.row)"
        />
      </template>
      <template #operation="scope">
        <el-button
          v-permission="'sys:role:read'"
          size="small"
          type="primary"
          link
          @click="handlePermissionDetail(scope.row)"
        >
          权限详情
        </el-button>
        <el-button v-permission="'sys:role:delete'" size="small" type="danger" link @click="handleDelete(scope.row)">
          删除
        </el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="system-role">
import { ref, onActivated } from 'vue'
import { ColumnProps } from '@/interface/table'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useConfirm } from '@/hooks/handle/use-handle'
import { queryRoleList, roleUpdateStatus, roleDelete } from '@/api/admin/auth/role'

const router = useRouter()
const bzTableRef = ref()
const hasInitialized = ref(true)

const dataCallback = (data: any) => {
  const list = (data?.list || []).map((item: any) => ({
    ...item,
    statusValue: item.enabled == '1'
  }))
  return {
    list,
    total: Number(data?.total || 0)
  }
}

const searchColumns = [
  {
    label: '角色名称',
    prop: 'name',
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
    prop: 'enabled',
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    },
    enum: [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  }
]

const columns: ColumnProps[] = [
  {
    label: '角色编号',
    prop: 'id',
    align: 'center'
  },
  {
    label: '角色名称',
    prop: 'name',
    align: 'center'
  },
  {
    label: '状态',
    prop: 'enabled',
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
    width: 150,
    fixed: 'right',
    align: 'center'
  }
]

// 状态改变处理
const handleStatusChange = async (row: any) => {
  await roleUpdateStatus({ enabled: row.statusValue ? '1' : '0', id: row.id })
  ElMessage.success('状态修改成功')
}

const handleDelete = async (row: any) => {
  const message = `确认删除?`
  await useConfirm(roleDelete, { id: row.id }, message)
  bzTableRef.value.getTableList()
}

// 添加角色
const handleAddRole = () => {
  router.push('/system/role/add')
}

// 权限详情
const handlePermissionDetail = (row: any) => {
  router.push({
    path: '/system/role/auth',
    query: {
      id: row.id
    }
  })
}

onActivated(() => {
  if (!hasInitialized.value) {
    bzTableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>
