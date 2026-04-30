<template>
  <div class="umbrella-frame-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getUmbrellaFramePage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAdd">新增伞架</el-button>
      </template>

      <template #size="{ row }">
        {{ formatSize(row) }}
      </template>

      <template #images="{ row }">
        <el-image
          v-if="firstImage(row.images)"
          :src="firstImage(row.images)"
          v-image-preview="firstImage(row.images)"
          fit="cover"
          style="width: 48px; height: 48px; border-radius: 4px"
        />
        <span v-else>-</span>
      </template>

      <template #boundMaterials="{ row }">
        <span v-if="row.materials && row.materials.length">{{ formatMaterials(row.materials) }}</span>
        <span v-else>-</span>
      </template>

      <template #price="{ row }">
        {{ row.price ? '¥' + row.price : '-' }}
      </template>

      <template #unit="{ row }">
        {{ row.unit || '-' }}
      </template>

      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="material-umbrella-frame">
import { ref, computed, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getUmbrellaFramePage, deleteUmbrellaFrame } from '@/api/zs/material/umbrella-frame'
import { listByNodeKey } from '@/api/zs/base-info/base-data'
import UmbrellaFrameDialog from './components/umbrella-frame-dialog.vue'

const tableRef = ref()
const hasInitialized = ref(true)

const functionOptions = ref<any[]>([])
const typeOptions = ref<any[]>([])
const materialOptions = ref<any[]>([])

const loadFilterOptions = async () => {
  const [fnRes, tRes, mRes] = await Promise.all([
    listByNodeKey({ nodeKey: 'FIELD_MGMT_UMBRELLA_FRAME_FUNCTION' }),
    listByNodeKey({ nodeKey: 'FIELD_MGMT_UMBRELLA_FRAME_TYPE' }),
    listByNodeKey({ nodeKey: 'FIELD_MGMT_UMBRELLA_FRAME_MATERIAL' })
  ])
  if (fnRes.code === 200) functionOptions.value = (fnRes.data || []).map((d: any) => ({ label: d.value1, value: d.id }))
  if (tRes.code === 200) typeOptions.value = (tRes.data || []).map((d: any) => ({ label: d.value1, value: d.id }))
  if (mRes.code === 200) materialOptions.value = (mRes.data || []).map((d: any) => ({ label: d.value1, value: d.id }))
}

const searchColumns = computed(() => [
  {
    prop: 'keywords',
    label: '关键词',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    prop: 'functionId',
    label: '功能',
    enum: functionOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
    }
  },
  {
    prop: 'typeId',
    label: '类型',
    enum: typeOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
    }
  },
  {
    prop: 'materialId',
    label: '材料',
    enum: materialOptions.value,
    fieldNames: { label: 'label', value: 'value' },
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true, filterable: true }
    }
  }
])

const columns = ref([
  { prop: 'functionName', label: '功能', minWidth: 100 },
  { prop: 'typeName', label: '类型', minWidth: 100 },
  { prop: 'size', label: '尺寸', minWidth: 160 },
  { prop: 'materialName', label: '材料', minWidth: 100 },
  { prop: 'specificAttribute', label: '特定属性', minWidth: 120, showOverflowTooltip: true },
  { prop: 'images', label: '图片', minWidth: 80 },
  { prop: 'boundMaterials', label: '绑定材料', minWidth: 120, showOverflowTooltip: true },
  { prop: 'price', label: '单价', minWidth: 80 },
  { prop: 'unit', label: '单位', minWidth: 80 },
  { prop: 'operation', label: '操作', width: 120, fixed: 'right' }
])

const searchDataCallback = (params: any) => {
  return params
}

const dataCallback = (data: any) => {
  return {
    list: data?.list || [],
    total: Number(data?.total || 0)
  }
}

const formatSize = (row: any) => {
  const parts: string[] = []
  if (row.lengthName) parts.push(row.lengthName)
  if (row.diameterName) parts.push(row.diameterName)
  if (row.ribCountName) parts.push(row.ribCountName)
  return parts.join(' / ') || '-'
}

const firstImage = (images: any[]) => {
  if (!Array.isArray(images) || images.length === 0) return null
  return images[0]?.url || null
}

const formatMaterials = (materials: any[]) => {
  return materials
    .map((m: any) => m.materialName)
    .filter(Boolean)
    .join('、')
}

const handleAdd = () => {
  dynamic.show({
    id: 'umbrellaFrameDialog',
    el: '#app',
    data: {
      isEdit: false,
      callback: () => tableRef.value?.getTableList()
    },
    render: UmbrellaFrameDialog
  })
}

const handleEdit = (row: any) => {
  dynamic.show({
    id: 'umbrellaFrameDialog',
    el: '#app',
    data: {
      isEdit: true,
      rowData: row,
      callback: () => tableRef.value?.getTableList()
    },
    render: UmbrellaFrameDialog
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该伞架吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteUmbrellaFrame({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

onMounted(loadFilterOptions)

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.umbrella-frame-container {
  height: 100%;
}
</style>
