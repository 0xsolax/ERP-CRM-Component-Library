<template>
  <div class="record-container">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :searchColumns="searchColumns"
      :columns="columns"
      :requestApi="getEnterOutRecords"
      :dataCallback="dataCallback"
      :toolButton="false"
    >
      <template #code="{ row }">
        <span class="link-text">{{ row.code || '-' }}</span>
      </template>
      <template #type="{ row }">
        <span class="link-text">{{ row.type || '-' }}</span>
      </template>
      <template #image="{ row }">
        <el-image
          v-if="row.image"
          :src="row.image"
          fit="cover"
          style="width: 50px; height: 50px; cursor: pointer"
          @click="handleImagePreview(row.image)"
        />
        <span v-else>-</span>
      </template>
      <template #operationNumber="{ row }">
        <span :style="{ color: row.operationNumber > 0 ? '#67c23a' : '#f56c6c' }">
          {{ row.operationNumber > 0 ? '+' : '' }}{{ row.operationNumber }}
        </span>
      </template>
      <!-- <template #operation="{ row }">
        <el-button type="primary" link size="small" @click="handleRestore(row)">还原</el-button>
      </template> -->
    </bz-table>

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { ColumnProps } from '@/interface/table'
import { ElMessageBox } from 'element-plus'
import { getEnterOutRecords } from '@/api/admin/warehouse'
import { enterOutRecordTypeList, getEnterOutRecordTypeLabel } from '@/constant/yitang/warehouse'

const bzTableRef = ref()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const searchColumns = computed(() => [
  {
    label: '操作人',
    prop: 'createUserName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '客户名称',
    prop: 'customerName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '产品ID',
    prop: 'productCode',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '图片',
    prop: 'image',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '规格名称',
    prop: 'specificationName',
    search: {
      el: 'el-input',
      props: { placeholder: '请输入', clearable: true }
    }
  },
  {
    label: '出入库类型',
    prop: 'type',
    search: {
      el: 'el-select',
      props: { placeholder: '请选择', clearable: true }
    },
    enum: enterOutRecordTypeList
  }
])

const columns: ColumnProps[] = [
  { label: '出入库编号', prop: 'code', align: 'center', width: 160 },
  { label: '出入库类型', prop: 'type', align: 'center' },
  { label: '产品ID', prop: 'productCode', align: 'center' },
  { label: '图片', prop: 'image', align: 'center', showOverflowTooltip: false },
  { label: '规格名称', prop: 'specName', align: 'center' },
  { label: '操作数量', prop: 'operationNumber', align: 'center' },
  { label: '库位', prop: 'locationName', align: 'center' },
  { label: '操作人', prop: 'createUserName', align: 'center' },
  { label: '客户名称', prop: 'customerName', align: 'center' },
  { label: '操作备注', prop: 'remark', align: 'center' },
  { label: '操作时间', prop: 'createTime', align: 'center', width: 160 }
  // { label: '操作', prop: 'operation', align: 'center', width: 80, fixed: 'right' }
]

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  records = records.map((item: any) => {
    const specItems = item.itemList || []
    const specName = specItems
      .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
      .filter((v: string) => v)
      .join('-')
    const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : null

    return {
      ...item,
      type: getEnterOutRecordTypeLabel(item.type),
      image: imageUrl,
      operationNumber: item.operationNumber || 0,
      locationName: item.locationName || '-',
      createUserName: item.createUserName || '-',
      customerName: item.customerName || '-',
      productCode: item.productCode || '-',
      specName: specName || '-',
      remark: item.remark || '-'
    }
  })
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const handleRestore = async (row: any) => {
  await ElMessageBox.confirm('确认还原该记录吗？', '还原确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  row
  // TODO: 调用API还原
  // const { code, message } = await restoreRecord()
  // if (code !== 200) return ElMessage.warning(message)
}
handleRestore
</script>

<style lang="scss" scoped>
.record-container {
  border-radius: 4px;
}
</style>
