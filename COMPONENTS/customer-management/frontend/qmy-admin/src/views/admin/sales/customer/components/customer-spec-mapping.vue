<template>
  <el-dialog
    custom-class="customer-spec-mapping-dialog"
    v-model="dialogVisible"
    title="客户规格映射"
    width="1100px"
    @close="onDestroy"
  >
    <bz-table
      ref="bzTableRef"
      :searchColumns="searchColumns"
      :filterSearchFields="filterSearchFields"
      :columns="columns"
      :requestApi="getCustomerSpecList"
      :initParam="initParam"
      :pagination="false"
      :customGridConfig="customGridConfig"
      :height="500"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button size="small" @click="handleExport">导出</el-button>
        <el-button type="primary" size="small" @click="handleImport">导入</el-button>
      </template>
      <template #specName="{ row }">
        {{ row.specName }}
      </template>
      <template #customerSpecName="{ row }">
        {{ row.customerSpecName || '--' }}
      </template>
      <template #operation="{ row }">
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" v-if="row.id" @click="handleDelete(row)">删除</el-button>
      </template>
    </bz-table>

    <el-dialog v-model="editDialogVisible" title="编辑规格" width="500px" append-to-body>
      <el-form :model="editForm" ref="editFormRef" label-width="120px">
        <el-form-item label="规格名称" prop="specName">
          <el-input v-model="editForm.specName" placeholder="请输入规格名称" disabled />
        </el-form-item>
        <el-form-item label="客户规格名称" prop="customerSpecName">
          <el-input v-model="editForm.customerSpecName" placeholder="请输入客户规格名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { ColumnProps } from '@/interface/table'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getYitangAdminToken, getYitangAdminTenantInfo } from '@/utils/auth'
import {
  getCustomerSpecList,
  createOrUpdateCustomerSpec,
  importCustomerSpec,
  getCustomerSpecDetail,
  deleteCustomerSpec
} from '@/api/admin/sales/customer-spec'
import { ElMessageBox } from 'element-plus'

const customGridConfig = {
  xs: '1fr',
  sm: '2fr 2fr 1fr',
  md: '2fr 2fr 1fr 1fr',
  lg: '2fr 2fr 1fr 1fr',
  xl: '2fr 2fr 1fr 1fr'
}

const attrs = useAttrs() as any

defineProps(['onDestroy'])

const dialogVisible = ref(true)
const editDialogVisible = ref(false)
const editFormRef = ref()
const bzTableRef = ref()

const initParam = reactive({
  customerId: attrs.customerId || ''
})
const filterSearchFields: string[] = []

const editForm = reactive({
  id: null,
  specName: '',
  customerSpecName: ''
})

const dataCallback = (data: any) => {
  let records = data ?? []
  return {
    list: records,
    total: 0
  }
}

const searchColumns = [
  {
    label: '规格名称',
    prop: 'specification',
    search: {
      el: 'el-input',
      props: {
        placeholder: '规格名称',
        clearable: true
      }
    }
  },
  {
    label: '客户规格名称',
    prop: 'customerSpecification',
    search: {
      el: 'el-input',
      props: {
        placeholder: '客户规格名称',
        clearable: true
      }
    }
  }
]

const columns: ColumnProps[] = [
  {
    label: '规格名称',
    prop: 'specification'
  },
  {
    label: '客户规格名称',
    prop: 'customerSpecification'
  },
  {
    label: '操作',
    prop: 'operation',
    width: 120
  }
]

const handleExport = async () => {
  const { status, message } = await downloadAxiosBlobFile({
    url: `${import.meta.env.VITE_APP_YITANG_BASE_API}/sal/yt/customerSpecificationComparison/export`,
    headers: {
      'qiaomoyun-tenant': getYitangAdminTenantInfo()?.id,
      'qiaomoyun-token': getYitangAdminToken()
    },
    params: {
      customerId: initParam.customerId
    },
    method: 'get'
  })
  if (status !== 200) return ElMessage.error(message)
  ElMessage.success('导出成功')
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e: any) => {
    const file = e.target.files[0]
    if (!file) return
    const formData = new FormData()
    formData.append('file', file)
    formData.append('customerId', initParam.customerId)

    try {
      const { code, message } = await importCustomerSpec(formData)
      if (code !== 200) return ElMessage.warning(message)
      ElMessage.success('导入成功')
      bzTableRef.value?.getTableList()
      if (attrs.callback) {
        attrs.callback()
      }
    } catch (error) {
      console.error('导入失败:', error)
      ElMessage.error('导入失败')
    }
  }
  input.click()
}

const handleEdit = async (row: any) => {
  editForm.id = row.id
  editForm.specName = row.specification || ''
  editForm.customerSpecName = row.customerSpecification || ''
  editDialogVisible.value = true

  getCustomerSpecDetail
  // await getCustomerSpecDetail({ id: row.id })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该规格吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const { code, message } = await deleteCustomerSpec({ id: row.id })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  ElMessage.success('删除成功')
  bzTableRef.value?.getTableList()
  if (attrs.callback) {
    attrs.callback()
  }
}

const handleEditSubmit = async () => {
  const { code, message } = await createOrUpdateCustomerSpec({
    id: editForm.id,
    customerId: initParam.customerId,
    specification: editForm.specName,
    customerSpecification: editForm.customerSpecName
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  ElMessage.success('保存成功')
  editDialogVisible.value = false
  bzTableRef.value?.getTableList()
  if (attrs.callback) {
    attrs.callback()
  }
}
</script>

<style lang="scss" scoped>
.customer-spec-mapping-dialog {
  :deep(.el-dialog__body) {
    padding: 10px 20px;
  }
}
</style>
