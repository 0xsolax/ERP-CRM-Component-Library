<template>
  <el-dialog v-model="visible" title="跟进详情" width="600px" @close="handleClose">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="主题">{{ recordData.theme || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ recordData.contactPerson || '-' }}</el-descriptions-item>
      <el-descriptions-item label="日期">{{ recordData.createTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="行动描述">{{ recordData.description || '-' }}</el-descriptions-item>
      <el-descriptions-item label="附件/现场照片">
        <div v-if="recordData.fileList && recordData.fileList.length > 0" class="attachment-list">
          <div v-for="(file, index) in recordData.fileList" :key="index" class="attachment-item">
            <img :src="file.url" class="attachment-preview" @click="previewImage(index)" />
            <span class="attachment-name">{{ file.name }}</span>
          </div>
        </div>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="下次回访日期">{{ recordData.nextVisitDate || '-' }}</el-descriptions-item>
    </el-descriptions>
    <template #footer />
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="currentIndex"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElImageViewer } from 'element-plus'
import { getExtractFilename } from '@/utils'

const attrs = useAttrs() as any
const visible = ref(true)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])
const currentIndex = ref(0)

const recordData = reactive({
  theme: attrs.theme || '',
  contactPerson: attrs.contactPerson || '',
  createTime: attrs.createTime || '',
  description: attrs.description || '',
  fileList: attrs.fileList || [],
  nextVisitDate: attrs.nextVisitDate || ''
})

const previewImage = (index: number) => {
  viewerImageList.value = recordData.fileList.map((item: any) => item.url)
  currentIndex.value = index
  showViewer.value = true
}

const handleClose = () => {
  visible.value = false
  if (attrs.onClose) {
    attrs.onClose()
  }
}

onMounted(() => {
  if (recordData?.fileList?.length) {
    recordData.fileList = recordData.fileList.map((item: any) => ({
      name: getExtractFilename(item.url || ''),
      url: item.url || '',
      storageId: item.storageId
    }))
  }
})
</script>

<style scoped lang="scss">
.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .attachment-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;

    .attachment-preview {
      width: 60px;
      height: 60px;
      object-fit: cover;
      border-radius: 4px;
      cursor: pointer;
      border: 1px solid #dcdfe6;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        transform: scale(1.05);
      }
    }

    .attachment-name {
      font-size: 12px;
      color: #606266;
      max-width: 60px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
