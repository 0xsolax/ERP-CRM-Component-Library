<template>
  <div class="production-group-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getProductionGroupPage"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button v-if="can(ZS_PERMISSIONS.production.groupSave)" type="primary" @click="openDialog('create')">
          新增生产组
        </el-button>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
      </template>

      <template #updateTime="{ row }">{{ formatDateTime(row.updateTime) }}</template>

      <template #operation="{ row }">
        <el-button
          v-if="can(ZS_PERMISSIONS.production.groupDetail)"
          type="primary"
          link
          @click="openDialog('detail', row)"
        >
          详情
        </el-button>
        <el-button v-if="can(ZS_PERMISSIONS.production.groupSave)" type="primary" link @click="openDialog('edit', row)">
          编辑
        </el-button>
        <el-button v-if="can(ZS_PERMISSIONS.production.groupDelete)" type="danger" link @click="handleDelete(row)">
          删除
        </el-button>
      </template>
    </bz-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="formData" :rules="rules" :disabled="dialogMode === 'detail'" label-width="90px">
        <el-form-item label="编码">
          <el-input v-model="formData.code" placeholder="为空则自动生成" clearable />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio-button :label="1">启用</el-radio-button>
            <el-radio-button :label="0">停用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="dialogMode !== 'detail'" type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="production-group">
import { computed, onActivated, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import {
  deleteProductionGroup,
  getProductionGroupDetail,
  getProductionGroupPage,
  saveOrUpdateProductionGroup
} from '@/api/zs/production'
import { ZS_PERMISSIONS } from '@/constant/permissions'
import { usePermissionStore } from '@/views/zs/store/modules/permission'

const tableRef = ref()
const formRef = ref()
const permissionStore = usePermissionStore()
const hasInitialized = ref(true)
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit' | 'detail'>('create')
const saving = ref(false)
const formData = reactive<any>({
  id: null,
  code: '',
  name: '',
  status: 1,
  remark: ''
})

const can = (permission: string) =>
  permissionStore.permissions.includes('*') || permissionStore.permissions.includes(permission)

const dialogTitle = computed(() => {
  const map = { create: '新增生产组', edit: '编辑生产组', detail: '生产组详情' }
  return map[dialogMode.value]
})

const rules = {
  name: [{ required: true, message: '请输入生产组名称', trigger: 'blur' }]
}

const searchColumns = ref([
  {
    prop: 'keyword',
    label: '编码/名称',
    search: { el: 'el-input', props: { placeholder: '请输入', clearable: true } }
  },
  {
    prop: 'status',
    label: '状态',
    search: { el: 'el-select', props: { clearable: true } },
    enum: [
      { label: '启用', value: 1 },
      { label: '停用', value: 0 }
    ]
  }
])

const columns = ref([
  { prop: 'code', label: '编码', minWidth: 120 },
  { prop: 'name', label: '名称', minWidth: 180, showOverflowTooltip: true },
  { prop: 'status', label: '状态', width: 90 },
  { prop: 'remark', label: '备注', minWidth: 220, showOverflowTooltip: true },
  { prop: 'updateTime', label: '更新时间', minWidth: 160 },
  { prop: 'operation', label: '操作', width: 170, fixed: 'right' }
])

const dataCallback = (data: any) => ({
  list: data?.list || [],
  total: Number(data?.total || 0)
})

const resetForm = () => {
  Object.assign(formData, { id: null, code: '', name: '', status: 1, remark: '' })
}

const openDialog = async (mode: 'create' | 'edit' | 'detail', row?: any) => {
  dialogMode.value = mode
  resetForm()
  if (row?.id) {
    const { code, data, message } = await getProductionGroupDetail({ id: row.id })
    if (code !== 200) return ElMessage.warning(message)
    Object.assign(formData, data || {})
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    const { code, message } = await saveOrUpdateProductionGroup({ ...formData })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    tableRef.value?.getTableList()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该生产组吗？', '删除生产组', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteProductionGroup({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

const formatDateTime = (value: any) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-')

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.production-group-container {
  height: 100%;
}
</style>
