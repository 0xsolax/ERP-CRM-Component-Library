<template>
  <div class="product-list-container">
    <bz-table
      ref="tableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getProductPage"
      :searchDataCallback="searchDataCallback"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #tableHeader>
        <el-button type="primary" @click="handleAdd">新增产品</el-button>
      </template>

      <template #images="{ row }">
        <el-image
          v-if="firstImage(row.images)"
          :src="firstImage(row.images)"
          v-image-preview="{ urlList: allImages(row.images) }"
          style="width: 56px; height: 56px; border-radius: 4px"
          fit="cover"
        />
        <span v-else>-</span>
      </template>

      <template #descriptionZh="{ row }">
        <span style="white-space: pre-line; font-size: 14px">{{ row.descriptionZh || '-' }}</span>
      </template>

      <template #grossWeight="{ row }">
        {{ row.grossWeight != null ? row.grossWeight + 'g' : '-' }}
      </template>

      <template #netWeight="{ row }">
        {{ row.netWeight != null ? row.netWeight + 'g' : '-' }}
      </template>

      <template #volumeInfo="{ row }">
        <div style="font-size: 14px; line-height: 1.8">
          <div v-if="row.volume">体积：{{ row.volume }} m³</div>
          <div v-if="row.smallCabinet">小柜：{{ row.smallCabinet }}</div>
          <div v-if="row.largeCabinet">高柜：{{ row.largeCabinet }}</div>
          <span v-if="!row.volume && !row.smallCabinet && !row.largeCabinet">-</span>
        </div>
      </template>

      <template #boxSpec="{ row }">
        {{ row.boxSpec || '-' }}
      </template>

      <template #boxCount="{ row }">
        {{ row.boxCount || '-' }}
      </template>

      <template #totalCost="{ row }">
        {{ row.totalCost != null ? '¥' + row.totalCost : '-' }}
      </template>

      <template #sellingPrice="{ row }">
        {{ row.sellingPrice != null ? '¥' + row.sellingPrice : '-' }}
      </template>

      <template #operation="{ row }">
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </bz-table>
  </div>
</template>

<script lang="ts" setup name="base-info-product-index">
import { ref, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { useTagsStore } from '@/views/zs/store/modules/tags'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ColumnProps } from '@/interface/table'
import { getProductPage, deleteProduct } from '@/api/zs/product'
import { listByNodeKey } from '@/api/zs/base-info/base-data'
import { getUmbrellaFramePage } from '@/api/zs/material/umbrella-frame'

const searchDataCallback = (params: any) => {
  return params
}

const router = useRouter()
const tableRef = ref()
const tagsStore = useTagsStore()
const hasInitialized = ref(true)

const categoryOptions = ref<any[]>([])
const frameTypeOptions = ref<any[]>([])
const frameSizeOptions = ref<any[]>([])
const frameFunctionOptions = ref<any[]>([])
const frameMaterialOptions = ref<any[]>([])
const fabricKindOptions = ref<any[]>([])
const printTypeOptions = ref<any[]>([])
const alignTypeOptions = ref<any[]>([])

const searchColumns = computed(() => [
  {
    label: '关键词',
    prop: 'keywords',
    search: { el: 'el-input', props: { placeholder: '请输入', clearable: true } }
  },
  {
    label: '产品类型',
    prop: 'productTypeId',
    enum: categoryOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '伞架类型',
    prop: 'frameTypeId',
    enum: frameTypeOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '伞架尺寸',
    prop: 'frameLengthId',
    enum: frameSizeOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '伞架功能',
    prop: 'frameFunctionId',
    enum: frameFunctionOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '伞架材料',
    prop: 'frameMaterialId',
    enum: frameMaterialOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '面料种类',
    prop: 'fabricTypeId',
    enum: fabricKindOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '印刷方式',
    prop: 'printTypeId',
    enum: printTypeOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  },
  {
    label: '对齐方式',
    prop: 'alignmentTypeId',
    enum: alignTypeOptions.value,
    search: { el: 'el-select', props: { placeholder: '请选择', clearable: true, filterable: true } }
  }
])

const columns: ColumnProps[] = [
  { label: '产品编号', prop: 'productCode', align: 'center', minWidth: 130 },
  { label: '产品类型', prop: 'productTypeName', align: 'center', minWidth: 130 },
  { label: '图片', prop: 'images', align: 'center', showOverflowTooltip: false },
  { label: '货品描述', prop: 'descriptionZh', align: 'center', minWidth: 180, showOverflowTooltip: false },
  { label: '毛重', prop: 'grossWeight', align: 'center' },
  { label: '净重', prop: 'netWeight', align: 'center' },
  { label: '体积/装柜数', prop: 'volumeInfo', align: 'center', minWidth: 120, showOverflowTooltip: false },
  { label: '箱规', prop: 'boxSpec', align: 'center' },
  { label: '装箱数', prop: 'boxCount', align: 'center' },
  { label: '成本', prop: 'totalCost', align: 'center' },
  { label: '售价', prop: 'sellingPrice', align: 'center' },
  { label: '操作', prop: 'operation', align: 'center', width: 120, fixed: 'right' }
]

const parseImages = (images: any): string[] => {
  let arr: any[] = []
  if (Array.isArray(images)) {
    arr = images
  } else if (typeof images === 'string') {
    try {
      arr = JSON.parse(images) || []
    } catch {
      return []
    }
  }
  return arr.map((item: any) => item?.url || item || '').filter(Boolean)
}

const firstImage = (images: any) => parseImages(images)[0] || ''

const allImages = (images: any) => parseImages(images)

const dataCallback = (data: any) => {
  const list = (data?.list || []).map((item: any) => ({
    ...item,
    productTypeName: (item.productTypes || []).map((t: any) => t.typeName).join('、') || '-'
  }))
  return { list, total: Number(data?.total || 0) }
}

const handleAdd = () => {
  router.push('/base-info/product/add')
}

const handleEdit = (row: any) => {
  tagsStore.delCachedView('base-info-product-edit')
  router.push({ path: '/base-info/product/edit', query: { id: row.id } })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确认删除该产品吗？删除后将无法恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await deleteProduct({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('删除成功')
  tableRef.value?.getTableList()
}

const loadOptions = async () => {
  const nodeKeys = [
    { key: 'PRODUCT_TYPE', target: categoryOptions },
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_TYPE', target: frameTypeOptions },
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_FUNCTION', target: frameFunctionOptions },
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_MATERIAL', target: frameMaterialOptions },
    { key: 'FABRIC_TYPE', target: fabricKindOptions },
    { key: 'FIELD_MGMT_PRINTING_METHOD', target: printTypeOptions },
    { key: 'FIELD_MGMT_ALIGNMENT_METHOD', target: alignTypeOptions }
  ]
  for (const { key, target } of nodeKeys) {
    const { code, data } = await listByNodeKey({ nodeKey: key })
    if (code === 200) {
      target.value = (data || []).map((d: any) => ({ label: d.value1, value: d.id }))
    }
  }

  const { code, data } = await getUmbrellaFramePage({ pageNum: 1, pageSize: 9999 })
  if (code === 200) {
    const list = data?.list || []
    const sizeMap = new Map<string, any>()
    list.forEach((r: any) => {
      const parts: string[] = []
      if (r.lengthName) parts.push(r.lengthName)
      if (r.diameterName) parts.push(r.diameterName)
      if (r.ribCountName) parts.push(r.ribCountName)
      const label = parts.join('×')
      if (label && !sizeMap.has(label)) {
        sizeMap.set(label, { label, value: r.id })
      }
    })
    frameSizeOptions.value = Array.from(sizeMap.values())
  }
}

onMounted(() => {
  loadOptions()
})

onActivated(() => {
  if (!hasInitialized.value) {
    tableRef.value?.getTableList()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.product-list-container {
}
</style>
