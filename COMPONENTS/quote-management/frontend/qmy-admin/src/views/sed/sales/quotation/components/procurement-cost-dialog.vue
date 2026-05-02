<template>
  <el-dialog v-model="dialogVisible" title="采购成本确认" width="800px" @close="onDestroy">
    <div class="tabs-wrapper">
      <el-radio-group v-model="activeTab">
        <el-radio-button label="包材">
          <el-badge :value="packingTotalCount" :max="9999" :hidden="packingTotalCount === 0" class="badge-item">
            包材
          </el-badge>
        </el-radio-button>
        <el-radio-button label="零件">
          <el-badge :value="partTotalCount" :max="9999" :hidden="partTotalCount === 0" class="badge-item">
            零件
          </el-badge>
        </el-radio-button>
        <el-radio-button label="配件">
          <el-badge :value="fittingTotalCount" :max="9999" :hidden="fittingTotalCount === 0" class="badge-item">
            配件
          </el-badge>
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 包材表格 -->
    <el-table v-if="activeTab === '包材'" :data="packingList" border style="width: 100%; margin-top: 15px">
      <el-table-column label="类型" prop="type" align="center" width="100" />
      <el-table-column label="包材名称" prop="name" align="center" />
      <el-table-column label="尺寸" align="center">
        <template #default="{ row }">{{ formatPackageSize(row) }}</template>
      </el-table-column>
      <el-table-column label="数量" prop="boxMum" align="center" width="100" />
      <el-table-column label="成本单价" align="center" width="120">
        <template #default="{ row }">
          <el-input v-model="row.costPrice" style="width: 80px" />
        </template>
      </el-table-column>
    </el-table>

    <!-- 零件表格 -->
    <el-table v-if="activeTab === '零件'" :data="partList" border style="width: 100%; margin-top: 15px">
      <el-table-column label="零件名称" prop="name" align="center" />
      <el-table-column label="尺寸" align="center">
        <template #default="{ row }">{{ formatSize(row) }}</template>
      </el-table-column>
      <el-table-column label="数量" prop="boxMum" align="center" width="100" />
      <el-table-column label="成本单价" align="center" width="120">
        <template #default="{ row }">
          <el-input v-model="row.costPrice" style="width: 80px" />
        </template>
      </el-table-column>
    </el-table>

    <!-- 配件表格 -->
    <el-table v-if="activeTab === '配件'" :data="fittingList" border style="width: 100%; margin-top: 15px">
      <el-table-column label="配件名称" prop="name" align="center" />
      <el-table-column label="尺寸" align="center">
        <template #default="{ row }">{{ formatSize(row) }}</template>
      </el-table-column>
      <el-table-column label="数量" prop="boxMum" align="center" width="100" />
      <el-table-column label="成本单价" align="center" width="120">
        <template #default="{ row }">
          <el-input v-model="row.costPrice" style="width: 80px" />
        </template>
      </el-table-column>
    </el-table>

    <div class="total-row">
      <span>总计</span>
      <span class="total-value">¥{{ totalCost }}</span>
    </div>

    <div class="section-title">附件信息</div>
    <div class="attachment-list">
      <div v-for="(item, index) in attachmentList" :key="index" class="attachment-item">
        <!-- 图片类型：显示预览 -->
        <template v-if="isImageFile(item.url)">
          <el-image :src="item.url" fit="cover" :preview-src-list="[item.url]" class="attachment-image" />
        </template>
        <!-- 非图片类型：显示文件图标和下载按钮 -->
        <template v-else>
          <div class="file-item" @click="handleDownload(item)">
            <el-icon class="file-icon" :style="{ color: getFileColor(item.url) }">
              <Document />
            </el-icon>
            <div class="file-info">
              <div class="file-name">{{ item.fileName }}</div>
              <div class="file-type">{{ getFileExtension(item.url) }}</div>
            </div>
            <el-icon class="download-icon">
              <Download />
            </el-icon>
          </div>
        </template>
      </div>
      <div v-if="!attachmentList.length" class="no-attachment">暂无附件</div>
    </div>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download } from '@element-plus/icons-vue'
import { getProcurementDetail, procurementConfirm } from '@/api/sed/sales/quotation'
import { useUserStore } from '@/views/sed/store/modules/user'
import { downloadLinkFile } from '@/utils/download'

const userStore = useUserStore()
const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const loading = ref(false)
const activeTab = ref('包材')

const packingList = ref<any[]>([])
const partList = ref<any[]>([])
const fittingList = ref<any[]>([])

const formatPackageSize = (row: any) => {
  let packageSize = row.size ?? ''
  let packingSize = row.packingSize ?? ''
  if ((packageSize != '' && packingSize != '') || (packageSize == '' && packingSize != '')) {
    packageSize = packingSize
  }
  return packageSize || '-'
}

const formatSize = (row: any) => {
  if (row.latitude && row.value) {
    return `${row.latitude}: ${row.value}`
  }
  return row.size || '-'
}

// 计算包材总数量
const packingTotalCount = computed(() => {
  return packingList.value.reduce((sum, item) => sum + (parseInt(item.boxMum) || 0), 0)
})

// 计算零件总数量
const partTotalCount = computed(() => {
  return partList.value.reduce((sum, item) => sum + (parseInt(item.boxMum) || 0), 0)
})

// 计算配件总数量
const fittingTotalCount = computed(() => {
  return fittingList.value.reduce((sum, item) => sum + (parseInt(item.boxMum) || 0), 0)
})

const totalCost = computed(() => {
  let total = 0
  ;[...packingList.value, ...partList.value, ...fittingList.value].forEach((item: any) => {
    total += (parseFloat(item.costPrice) || 0) * (parseInt(item.boxMum) || 0)
  })
  return total.toFixed(2)
})

const attachmentList = ref<any[]>([])

// 判断是否为图片文件
const isImageFile = (url: string) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  const extension = url.toLowerCase().substring(url.lastIndexOf('.'))
  return imageExtensions.includes(extension)
}

// const getFileName = (url: string) => {
//   const parts = url.split('/')
//   const filename = parts[parts.length - 1]
//   return filename.split('?')[0]
// }

const getFileExtension = (url: string) => {
  const extension = url.toLowerCase().substring(url.lastIndexOf('.') + 1)
  return extension.toUpperCase()
}

const getFileColor = (url: string) => {
  const ext = getFileExtension(url).toLowerCase()
  const colorMap: Record<string, string> = {
    pdf: '#f56c6c',
    doc: '#409eff',
    docx: '#409eff',
    xls: '#67c23a',
    xlsx: '#67c23a',
    txt: '#909399'
  }
  return colorMap[ext] || '#909399'
}

const handleDownload = (item: any) => {
  downloadLinkFile(item.url)
}

const loadDetail = async () => {
  if (!rowData?.id) return
  const { code, data, message } = await getProcurementDetail({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)

  if (data?.procurementMap) {
    let packingListData = data.procurementMap['包材'] || []
    packingList.value = packingListData.map(item => {
      let size = item.size
      let packingSize = item.packingSize
      if (size && packingSize) {
        size = packingSize
      }
      item.size = size
      item.costPrice = item.cost
      return item
    })
    partList.value = data.procurementMap['零件'] || []
    fittingList.value = data.procurementMap['配件'] || []
  }
  attachmentList.value = data?.attachmentsLists || []
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const { code, message } = await procurementConfirm({
      id: rowData.id,
      salesmanId: userStore.userId,
      procurementCost: parseFloat(totalCost.value),
      packingInfo: packingList.value.map((item: any) => ({
        packingId: parseInt(item.packingId) || 0,
        costPrice: parseFloat(item.costPrice) || 0
      })),
      partInfo: partList.value.map((item: any) => ({
        partId: parseInt(item.partId) || 0,
        costPrice: parseFloat(item.costPrice) || 0
      })),
      fittingInfo: fittingList.value.map((item: any) => ({
        fittingId: parseInt(item.fittingId) || 0,
        costPrice: parseFloat(item.costPrice) || 0
      }))
    })
    if (code !== 200) return ElMessage.warning(message)

    ElMessage.success('确认成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.tabs-wrapper {
  margin-bottom: 10px;

  .badge-item {
    :deep(.el-badge__content) {
      transform: translateY(-50%) translateX(50%);
      top: -10px;
      right: -8px;
    }
  }
}

.total-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  margin-top: 15px;
  font-size: 14px;

  .total-value {
    font-size: 16px;
    font-weight: 600;
    color: #f56c6c;
  }
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-left: 10px;
  border-left: 3px solid #409eff;
  margin: 20px 0 15px;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .attachment-item {
    width: 100px;
    height: 100px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;

    .attachment-image {
      width: 100%;
      height: 100%;
    }

    .file-item {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 5px;
      cursor: pointer;
      transition: all 0.3s;
      padding: 10px;
      position: relative;

      &:hover {
        background-color: #f5f7fa;

        .download-icon {
          opacity: 1;
        }
      }

      .file-icon {
        font-size: 32px;
      }

      .file-info {
        text-align: center;
        width: 100%;

        .file-name {
          font-size: 12px;
          color: #606266;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          width: 100%;
        }

        .file-type {
          font-size: 10px;
          color: #909399;
          margin-top: 2px;
        }
      }

      .download-icon {
        position: absolute;
        top: 5px;
        right: 5px;
        font-size: 16px;
        color: #409eff;
        opacity: 0;
        transition: opacity 0.3s;
      }
    }
  }

  .no-attachment {
    color: #909399;
    font-size: 14px;
  }
}
</style>
