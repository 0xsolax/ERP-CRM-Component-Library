<template>
  <el-dialog v-model="visible" :title="isEdit ? '编辑跟进' : '新增跟进'" width="600px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="主题" prop="theme">
        <el-input v-model="formData.theme" placeholder="请输入" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="formData.contactPerson" placeholder="请输入" maxlength="20" show-word-limit />
      </el-form-item>
      <el-form-item label="日期" prop="createTime">
        <el-date-picker
          v-model="formData.createTime"
          type="date"
          placeholder="请输入"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="行动描述" prop="description">
        <el-input
          v-model="formData.description"
          placeholder="请输入"
          type="textarea"
          :rows="3"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="附件/现场照片" prop="fileList">
        <div class="upload-wrapper">
          <bz-upload
            module-path="yt/purchase"
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
          <div v-if="formData.fileList.length" class="file-list">
            <div v-for="(file, index) in formData.fileList" :key="index" class="file-item">
              <img :src="file.url" class="file-preview" @click="previewImage(file.url)" />
              <span class="file-name">{{ file.name }}</span>
              <el-icon class="delete-icon" @click="removeFile(index)">
                <CircleClose />
              </el-icon>
            </div>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="下次回访日期" prop="nextVisitDate">
        <el-date-picker
          v-model="formData.nextVisitDate"
          type="date"
          placeholder="请输入"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
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
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage, ElImageViewer } from 'element-plus'
import { CircleClose } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import { createOrUpdateSupplierFollow } from '@/api/admin/purchase/supplier'
import { getExtractFilename } from '@/utils'

const attrs = useAttrs() as any
const { fromType, supplierId, callback, onClose, ...rowData } = attrs
const visible = ref(true)
const formRef = ref()
const isEdit = ref(!!rowData.title || !!rowData.theme)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const formData = reactive({
  theme: rowData.title || rowData.theme || '',
  contactPerson: rowData.contactPerson || '',
  createTime: rowData.createTime || '',
  description: rowData.description || '',
  fileList: rowData.fileList || [],
  nextVisitDate: rowData.nextVisitDate || ''
})

const uploadData = reactive({
  action: (import.meta as any).env.VITE_APP_BASE_API + '/storage/create',
  accept: imageFileType.join()
})

const rules = {
  theme: [{ required: true, message: '请输入主题', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  createTime: [{ required: true, message: '请选择日期', trigger: 'change' }],
  nextVisitDate: [{ required: true, message: '请选择下次回访日期', trigger: 'change' }]
}

const beforeUpload = (file: any) => {
  // const size = file.size / 1024
  if (!imageFileType.includes(file.fileType.toLowerCase())) {
    ElMessage.warning('请上传' + imageFileType.join() + '格式的图片')
    return false
  }
  // if (size > 500) {
  //   ElMessage.warning('图片大小不能超过500KB')
  //   return false
  // }
  return true
}

const handleUploadSuccess = (res: any) => {
  formData.fileList.push({
    url: res.data.url,
    storageId: res.data.id,
    name: getExtractFilename(res.data.url)
  })
  ElMessage.success('上传成功')
}

const uploadError = () => {
  ElMessage.error('上传失败')
}

const removeFile = (index: number) => {
  formData.fileList.splice(index, 1)
}

const previewImage = (url: string) => {
  viewerImageList.value = [url]
  showViewer.value = true
}

const handleClose = () => {
  visible.value = false
  if (onClose) {
    onClose()
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const result = {
    theme: formData.theme,
    contactPerson: formData.contactPerson,
    createTime: formData.createTime,
    description: formData.description,
    fileList: formData.fileList,
    nextVisitDate: formData.nextVisitDate
  }

  if (fromType === 'supplier-detail') {
    const apiData = {
      id: isEdit.value ? rowData?.id : undefined,
      supplierId: supplierId,
      theme: formData.theme,
      contactPerson: formData.contactPerson,
      createTime: formData.createTime,
      description: formData.description,
      fileList: formData.fileList,
      nextVisitDate: formData.nextVisitDate
    }

    const { code, message } = await createOrUpdateSupplierFollow(apiData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
  }

  if (callback) {
    callback(result)
  }
  handleClose()
}

onMounted(() => {
  if (formData?.fileList?.length) {
    formData.fileList = formData.fileList.map((item: any) => ({
      name: getExtractFilename(item.url || ''),
      url: item.url || '',
      storageId: item.storageId
    }))
  }
})
</script>

<style scoped lang="scss">
.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;

  .file-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background: #fff;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      background: #f5f7fa;
    }

    .file-preview {
      width: 40px;
      height: 40px;
      object-fit: cover;
      border-radius: 4px;
      cursor: pointer;
      flex-shrink: 0;
    }

    .file-name {
      flex: 1;
      margin-left: 12px;
      font-size: 14px;
      color: #606266;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .delete-icon {
      font-size: 20px;
      color: #f56c6c;
      cursor: pointer;
      flex-shrink: 0;
      transition: transform 0.2s;

      &:hover {
        transform: scale(1.1);
      }
    }
  }
}
.upload-wrapper {
  width: 100%;
}
</style>
