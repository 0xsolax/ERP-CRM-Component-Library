<template>
  <el-dialog v-model="dialogVisible" title="总裁微信审核" width="560px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
      <el-form-item label="报价单编号">
        <span>{{ form.quotationCode || '-' }}</span>
      </el-form-item>
      <el-form-item label="客户名称">
        <span>{{ form.customerName || '-' }}</span>
      </el-form-item>
      <el-form-item label="微信审核截图" prop="fileList" required>
        <div class="upload-wrapper">
          <bz-upload
            module-path="sed/sales"
            :accept="uploadData.accept"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="uploadError"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件</div>
            </template>
          </bz-upload>
          <div v-if="form.fileList.length" class="file-list">
            <div v-for="(file, index) in form.fileList" :key="index" class="file-item">
              <img :src="file.url" class="file-preview" @click="previewImage(file.url)" />
              <span class="file-name">{{ file.name }}</span>
              <el-icon class="delete-icon" @click="removeFile(index)">
                <CircleClose />
              </el-icon>
            </div>
          </div>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
  <el-image-viewer
    v-if="showViewer"
    :url-list="viewerImageList"
    :initial-index="0"
    hide-on-click-modal
    @close="showViewer = false"
  />
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs } from 'vue'
import { ElMessage, ElImageViewer } from 'element-plus'
import { CircleClose } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import { presidentWxAudit } from '@/api/sed/sales/quotation'

const attrs = useAttrs() as any
const { rowData, callback, onDestroy } = attrs
const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const form = reactive({
  quotationCode: rowData?.quotationCode || '',
  customerName: rowData?.customerName || '',
  fileList: [] as any[]
})

const uploadData = reactive({
  action: (import.meta as any).env.VITE_APP_BASE_API + '/storage/create',
  accept: imageFileType.join()
})

const rules = {
  fileList: [
    {
      validator: (_rule: any, value: any[], callbackFn: (error?: Error) => void) => {
        if (Array.isArray(value) && value.length > 0) {
          callbackFn()
          return
        }
        callbackFn(new Error('请上传微信审核截图'))
      },
      trigger: 'change'
    }
  ]
}

const beforeUpload = (file: any) => {
  if (!imageFileType.includes(file.fileType.toLowerCase())) {
    ElMessage.warning('请上传' + imageFileType.join() + '格式的图片')
    return false
  }
  return true
}

const handleUploadSuccess = (res: any, file, files) => {
  console.log('111', res, file, files)

  form.fileList.push({
    url: res.data.url,
    storageId: res.data.id,
    name: file.name
  })
  formRef.value?.validateField('fileList')
  ElMessage.success('上传成功')
}

const uploadError = () => {
  ElMessage.error('上传失败')
}

const removeFile = (index: number) => {
  form.fileList.splice(index, 1)
  formRef.value?.validateField('fileList')
}

const previewImage = (url: string) => {
  viewerImageList.value = [url]
  showViewer.value = true
}

const handleClose = () => {
  dialogVisible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const { code, message } = await presidentWxAudit({
      id: rowData.id,
      imageList: form.fileList.map((item: any) => ({ storageId: item.storageId }))
    })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
    callback?.()
    handleClose()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
}

.file-preview {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
  cursor: pointer;
}

.file-name {
  flex: 1;
  color: #606266;
  font-size: 13px;
}

.delete-icon {
  cursor: pointer;
  color: #f56c6c;
  margin-left: 10px;
}
</style>
