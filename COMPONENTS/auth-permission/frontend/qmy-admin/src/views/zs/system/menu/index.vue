<template>
  <div class="table-box">
    <bz-table
      ref="bzTableRef"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getMenuList"
      :initParam="initParam"
      :dataCallback="dataCallback"
      :pagination="false"
      row-key="id"
      :tree-props="{ children: 'childTreeList', hasChildren: 'hasChildren' }"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAddMenu('新增菜单')">新增菜单</el-button>
      </template>
      <template #operation="scope">
        <el-button size="small" type="primary" link @click="handleAddMenu('修改菜单', scope.row)">修改</el-button>
        <el-button size="small" type="danger" link @click="handleDeleteMenu(scope.row)">删除</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="tsx" setup>
import { ref, reactive } from 'vue'
import addMenu from './components/add-menu.vue'
import { getMenuList, deleteMenu } from '@/api/admin/auth/menu'
import { dynamic } from '@bzlab/bz-core'
import { ColumnProps } from '@/interface/table'
import Icon from '@/components/icon/index.vue'
import { useConfirm } from '@/hooks/handle/use-handle'

const bzTableRef = ref()
const initParam = reactive({})

const handleAddMenu = (title: string, rowData?) => {
  const params = {
    id: 'addMenu', // 组件id
    el: '#app', // 挂载节点
    data: {
      title,
      rowData,
      tableData: bzTableRef.value.tableData,
      isAdd: title === '新增菜单',
      callback: () => bzTableRef.value.getTableList()
    },
    render: addMenu
  }
  dynamic.show(params)
}

async function handleDeleteMenu(row) {
  const message = `确认删除此菜单?`
  await useConfirm(deleteMenu, { menuId: row.id }, message)
  bzTableRef.value.getTableList()
}

const dataCallback = (data: any) => {
  return {
    list: data
  }
}

const searchColumns = [
  {
    label: '菜单名称',
    prop: 'menuName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
]

const columns: ColumnProps[] = [
  {
    label: '菜单名称',
    prop: 'menuName',
    align: 'left',
    render: ({ row }) => {
      return (
        <>
          <Icon icon={row.menuIcon || 'Menu'} style={{ verticalAlign: 'middle', marginBottom: '4px' }} />
          <span style={{ marginLeft: '3px' }}>{row.menuName}</span>
        </>
      )
    }
  },
  {
    label: '类型',
    prop: 'menuType',
    render: ({ row }) => {
      return (
        <>
          {row.menuType === 1 && <el-tag>目录</el-tag>}
          {row.menuType === 2 && <el-tag type="success">菜单</el-tag>}
          {row.menuType === 3 && <el-tag type="warning">按钮</el-tag>}
        </>
      )
    }
  },
  {
    label: '权限标识',
    prop: 'menuCode'
  },
  {
    label: '组件地址',
    prop: 'menuComponents'
  },
  {
    label: '组件名称',
    prop: 'menuRoute'
  },
  {
    label: '排序',
    prop: 'menuSort'
  },
  {
    label: '是否缓存',
    prop: 'cache',
    render: ({ row }) => {
      return (
        <>
          {row.menuType === 2 && (
            <el-tag type={row.cache == 0 ? 'danger' : ''}>{row.cache == 0 ? '禁用' : '启用'}</el-tag>
          )}
        </>
      )
    }
  },
  {
    label: '是否显示',
    prop: 'cache',
    render: ({ row }) => {
      return <>{<el-tag type={row.hiddenFlag == 0 ? 'danger' : ''}>{row.hiddenFlag == 0 ? '隐藏' : '显示'}</el-tag>}</>
    }
  },
  {
    label: '状态',
    prop: 'cache',
    render: ({ row }) => {
      return <>{<el-tag type={row.status == 0 ? '' : 'danger'}>{row.status == 0 ? '启用' : '禁用'}</el-tag>}</>
    }
  },
  {
    label: '创建时间',
    prop: 'createTime'
  },
  {
    label: '操作',
    prop: 'operation',
    fixed: 'right',
    width: 100
  }
]
</script>
