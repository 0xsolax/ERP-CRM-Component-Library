<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑材料' : '新增材料'" width="550px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="材料名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属分类" prop="categoryId">
            <el-select v-model="formData.categoryId" clearable placeholder="请选择分类" style="width: 100%">
              <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="尺寸">
            <el-input v-model="formData.size" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input
              v-model="formData.price"
              placeholder="请输入"
              @input="(val: string) => (formData.price = validateDecimal(val))"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="图片" style="margin-top: 12px">
        <div class="image-list">
          <div v-for="(img, idx) in visibleImageList" :key="idx" class="image-item">
            <img :src="img.url" class="image-preview" />
            <div class="image-overlay">
              <el-icon class="overlay-icon" @click.stop="handleImagePreview(img.url)">
                <ZoomIn />
              </el-icon>
              <el-icon class="overlay-icon" @click.stop="handleRemoveImage(idx)">
                <Delete />
              </el-icon>
            </div>
          </div>
          <bz-upload
            v-if="visibleImageList.length < 10"
            class="img-uploader"
            module-path="zs/material"
            :accept="uploadData.accept"
            :before-upload="beforeUpload"
            :on-success="uploadSuccess"
            :on-error="uploadError"
          >
            <el-icon class="img-uploader-icon"><Plus /></el-icon>
          </bz-upload>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
    </template>
    <el-image-viewer
      v-if="previewVisible"
      :url-list="[previewImageUrl]"
      :initial-index="0"
      hide-on-click-modal
      @close="previewVisible = false"
    />
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, useAttrs, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import { saveMaterial, updateMaterial } from '@/api/zs/material/material'
import { validateDecimal } from '@/utils/validate'

const attrs = useAttrs()
const { isEdit, rowData, categories, defaultCategoryId, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)
const imageList = ref<any[]>([])
const visibleImageList = computed(() => imageList.value.filter((img: any) => !img.isDeleted))
const previewVisible = ref(false)
const previewImageUrl = ref('')
const uploadData = reactive({
  accept: imageFileType.join()
})
const categoryList = ref<any[]>(categories || [])

const formData = reactive<any>({
  id: null,
  name: '',
  categoryId: null,
  size: '',
  price: null,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入材料名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const beforeUpload = (file: any) => {
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

const uploadSuccess = (res: any) => {
  imageList.value.push({ storageId: res.data.id, url: res.data.url })
  ElMessage.success('上传成功')
}

const uploadError = () => {
  ElMessage.error('上传失败')
}

const handleImagePreview = (url: string) => {
  previewImageUrl.value = url
  previewVisible.value = true
}

const handleRemoveImage = (idx: number) => {
  const actual = imageList.value.indexOf(visibleImageList.value[idx])
  if (imageList.value[actual]?.id) {
    imageList.value[actual].isDeleted = 1
  } else {
    imageList.value.splice(actual, 1)
  }
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const payload = {
      id: formData.id,
      categoryId: formData.categoryId,
      name: formData.name,
      size: formData.size,
      price: formData.price,
      remark: formData.remark,
      images: imageList.value
        .filter((img: any) => img.isDeleted === 1 || img.storageId)
        .map((img: any) => ({
          ...(img.id && { id: img.id }),
          storageId: img.storageId,
          url: img.url,
          ...(img.isDeleted === 1 && { isDeleted: 1 })
        }))
    }
    if (isEdit) {
      const { code, message } = await updateMaterial(payload)
      if (code !== 200) return ElMessage.warning(message)
    } else {
      const { code, message } = await saveMaterial(payload)
      if (code !== 200) return ElMessage.warning(message)
    }
    ElMessage.success('保存成功')
    callback?.()
    onDestroy()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (isEdit && rowData) {
    Object.assign(formData, rowData)
    if (Array.isArray(rowData.images)) {
      imageList.value = rowData.images
        .filter((img: any) => img.isDeleted !== 1)
        .map((img: any) => ({ id: img.id, storageId: img.storageId, url: img.url }))
    }
  } else if (defaultCategoryId) {
    formData.categoryId = defaultCategoryId
  }
  nextTick(() => formRef.value?.clearValidate())
})
</script>

<style lang="scss" scoped>
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .image-item {
    position: relative;
    width: 80px;
    height: 80px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    overflow: hidden;
    background: #ccc;

    .image-preview {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 15px;
      opacity: 0;
      transition: opacity 0.3s;
      cursor: pointer;

      &:hover {
        opacity: 1;
      }

      .overlay-icon {
        font-size: 20px;
        color: #fff;
        transition: transform 0.2s;

        &:hover {
          transform: scale(1.2);
        }
      }
    }
  }

  .img-uploader {
    :deep(.bz-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      width: 80px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409eff;
      }
    }

    .img-uploader-icon {
      font-size: 20px;
      color: #8c939d;
    }
  }
}
</style>
