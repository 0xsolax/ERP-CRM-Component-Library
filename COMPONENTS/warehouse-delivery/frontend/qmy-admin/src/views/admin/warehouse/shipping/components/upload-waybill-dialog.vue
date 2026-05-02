<template>
  <el-dialog v-model="dialogVisible" title="上传面单" width="500px" @close="onDestroy">
    <el-form :model="formData" label-width="80px">
      <el-form-item label="客户名称">
        <span>{{ rowData?.customerName }}</span>
      </el-form-item>

      <el-form-item label="发货单号">
        <span>{{ rowData?.code }}</span>
      </el-form-item>

      <el-form-item label="面单" prop="image">
        <div class="image-upload">
          <bz-upload
            class="img-uploader"
            module-path="yt/warehouse"
            :accept="uploadData.accept"
            :before-upload="beforeUpload"
            :on-success="uploadSuccess"
            :on-error="uploadError"
          >
            <div v-if="formData.image" class="image-preview-wrapper">
              <img :src="formData.image" class="image-preview" />
              <div class="image-overlay">
                <el-icon class="overlay-icon" @click.stop="handleImagePreview">
                  <ZoomIn />
                </el-icon>
                <el-icon class="overlay-icon" @click.stop="handleImageDelete">
                  <Delete />
                </el-icon>
              </div>
            </div>
            <el-icon v-else class="img-uploader-icon"><Plus /></el-icon>
          </bz-upload>
        </div>
      </el-form-item>
    </el-form>

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { updateTransport } from '@/api/admin/warehouse'
import { imageFileType } from '@/constant/file-type'

const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)

const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const formData = reactive({
  image: '',
  storageId: ''
})

const uploadData = reactive({
  action: import.meta.env.VITE_APP_BASE_API + '/storage/create',
  accept: imageFileType.join()
})

const beforeUpload = (file: any) => {
  console.log('beforeUpload', file)
  const size = file.size / 1024 / 1024
  if (!imageFileType.includes(file.fileType.toLowerCase())) {
    ElMessage.warning('请上传' + imageFileType.join() + '格式的图片')
    return false
  } else if (size > 10) {
    ElMessage.warning('请上传10M以内的文件')
    return false
  }
  return true
}

const uploadSuccess = (response: any) => {
  formData.image = response.data.url
  formData.storageId = response.data.id
  ElMessage.success('上传成功')
}

const uploadError = () => {
  ElMessage.error('上传失败，请重试')
}

const handleImagePreview = () => {
  console.log('formData.image', formData.image)
  if (!formData.image) return
  viewerImageList.value = [formData.image]
  showViewer.value = true
}

const handleImageDelete = () => {
  formData.image = ''
  formData.storageId = ''
}

const handleConfirm = async () => {
  // if (!formData.image) {
  //   return ElMessage.warning('请上传面单')
  // }

  const { code, message } = await updateTransport({
    id: rowData.id,
    packageCode: rowData.packageCode || '',
    transportCompanyId: rowData.transportCompanyId || '',
    transportOrderFileId: formData.storageId
  })

  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('上传成功')
  dialogVisible.value = false
  if (callback) callback()
}

onMounted(() => {
  if (rowData.transportOrderFileId) {
    formData.image = rowData.transportOrderFileUrl
    formData.storageId = rowData.transportOrderFileId
  }
})
</script>

<style lang="scss" scoped>
.image-upload {
  .img-uploader {
    :deep(.bz-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;
      width: 148px;
      height: 148px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409eff;
      }
    }
  }

  .img-uploader-icon {
    font-size: 28px;
    color: #8c939d;
  }

  .image-preview-wrapper {
    position: relative;
    width: 148px;
    height: 148px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;

    .image-preview {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      opacity: 0;
      transition: opacity 0.3s;

      .overlay-icon {
        font-size: 20px;
        color: #fff;
        cursor: pointer;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    &:hover .image-overlay {
      opacity: 1;
    }
  }
}
</style>
