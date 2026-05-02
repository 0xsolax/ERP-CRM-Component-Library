<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑联系人' : '新增联系人'" width="700px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="联系人" prop="name">
        <el-input v-model="formData.name" placeholder="请输入" maxlength="10" show-word-limit clearable />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入" maxlength="50" show-word-limit clearable />
      </el-form-item>

      <el-form-item label="社交平台" prop="socialPlatforms">
        <div class="dynamic-list">
          <div v-for="(item, index) in formData.socialPlatforms" :key="index" class="dynamic-item">
            <el-select v-model="item.platform" placeholder="请选择" clearable style="width: 120px">
              <el-option label="Facebook" value="Facebook" />
              <el-option label="Instagram" value="Instagram" />
              <el-option label="YouTube" value="YouTube" />
              <el-option label="WeChat" value="WeChat" />
              <el-option label="TikTok" value="TikTok" />
              <el-option label="LinkedIn" value="LinkedIn" />
              <el-option label="阿里TM" value="阿里TM" />
              <el-option label="Whatsapp" value="Whatsapp" />
              <el-option label="Skype" value="Skype" />
              <el-option label="企业微信" value="企业微信" />
              <el-option label="QQ" value="QQ" />
              <el-option label="Messenger" value="Messenger" />
              <el-option label="Line" value="Line" />
              <el-option label="VK" value="VK" />
              <el-option label="Telegram" value="Telegram" />
              <el-option label="AngelList" value="AngelList" />
              <el-option label="钉钉" value="钉钉" />
              <el-option label="Pinterest" value="Pinterest" />
              <el-option label="Snapchat" value="Snapchat" />
            </el-select>
            <el-input v-model="item.account" placeholder="请输入" style="flex: 1; margin: 0 10px" />
            <el-button
              v-if="index === formData.socialPlatforms.length - 1"
              circle
              size="small"
              style="width: 20px; height: 20px"
              @click="addSocialPlatform"
            >
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button
              v-if="formData.socialPlatforms.length > 1"
              circle
              size="small"
              style="width: 20px; height: 20px"
              @click="removeSocialPlatform(index)"
            >
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="联系电话" prop="phones">
        <div class="dynamic-list">
          <div v-for="(item, index) in formData.phones" :key="index" class="dynamic-item">
            <el-input v-model="item.areaCode" placeholder="请输入" maxlength="10" style="width: 120px" />
            <el-input
              v-model="item.number"
              placeholder="请输入"
              maxlength="20"
              show-word-limit
              style="flex: 1; margin: 0 10px"
            />
            <el-button
              v-if="index === formData.phones.length - 1"
              circle
              size="small"
              @click="addPhone"
              style="width: 20px; height: 20px"
            >
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button
              v-if="formData.phones.length > 1"
              circle
              size="small"
              @click="removePhone(index)"
              style="width: 20px; height: 20px"
            >
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="职位" prop="position">
        <el-input v-model="formData.position" placeholder="请输入" maxlength="20" show-word-limit clearable />
      </el-form-item>

      <el-form-item label="生日" prop="birthday">
        <el-date-picker
          v-model="formData.birthday"
          type="date"
          placeholder="请选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
          clearable
        />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-select v-model="formData.gender" placeholder="请选择" clearable style="width: 100%">
          <el-option label="男" value="0" />
          <el-option label="女" value="1" />
          <el-option label="未知" value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="名片/头像" prop="cards">
        <div class="upload-wrapper">
          <bz-upload
            class="card-uploader"
            module-path="yt/purchase"
            :accept="uploadData.accept"
            :before-upload="beforeUpload"
            :on-success="handleCardSuccess"
            :on-error="uploadError"
          >
            <el-button type="primary">点击上传</el-button>
          </bz-upload>
          <div class="el-upload__tip">只能上传jpg/png文件</div>
          <div v-if="formData.cards.length" class="file-list">
            <div v-for="(card, index) in formData.cards" :key="index" class="file-item">
              <img :src="card.url" class="file-preview" @click="previewImage(card.url)" />
              <span class="file-name">{{ card.name || `图片${index + 1}` }}</span>
              <el-icon class="delete-icon" @click="handleCardDelete(index)">
                <CircleClose />
              </el-icon>
            </div>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入"
          maxlength="100"
          show-word-limit
          clearable
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Minus, CircleClose } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import { saveOrUpdateSupplierContact } from '@/api/admin/purchase/supplier'

const attrs = useAttrs()
const { onDestroy, callback, fromType, supplierId, ...rowData } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const isEdit = ref(false)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const uploadData = reactive({
  action: (import.meta as any).env.VITE_APP_BASE_API + '/storage/create',
  accept: imageFileType.join()
})

const formData = reactive({
  name: '',
  email: '',
  socialPlatforms: [{ id: undefined as any, platform: '', account: '' }],
  phones: [{ id: undefined as any, areaCode: '', number: '' }],
  deletedSocialItems: [] as any[],
  deletedPhoneItems: [] as any[],
  position: '',
  birthday: '',
  gender: '',
  cards: [] as any[],
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入联系人', trigger: 'blur' }]
}

const addSocialPlatform = () => {
  formData.socialPlatforms.push({ id: undefined, platform: '', account: '' })
  formRef.value?.validateField('socialPlatforms')
}

const removeSocialPlatform = (index: number) => {
  const item = formData.socialPlatforms[index]
  if (item.id != null) formData.deletedSocialItems.push(item)
  formData.socialPlatforms.splice(index, 1)
  formRef.value?.validateField('socialPlatforms')
}

const addPhone = () => {
  formData.phones.push({ id: undefined, areaCode: '', number: '' })
  formRef.value?.validateField('phones')
}

const removePhone = (index: number) => {
  const item = formData.phones[index]
  if (item.id != null) formData.deletedPhoneItems.push(item)
  formData.phones.splice(index, 1)
  formRef.value?.validateField('phones')
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

const uploadError = () => {
  ElMessage.error('上传失败')
}

const handleCardSuccess = (res: any, file: any) => {
  formData.cards.push({
    url: res.data.url,
    storageId: res.data.id,
    name: file.name
  })
  formRef.value?.validateField('cards')
  ElMessage.success('上传成功')
}

const previewImage = (url: string) => {
  viewerImageList.value = [url]
  showViewer.value = true
}

const handleCardDelete = (index: number) => {
  formData.cards.splice(index, 1)
  formRef.value?.validateField('cards')
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const result = {
    name: formData.name,
    email: formData.email,
    position: formData.position,
    birthday: formData.birthday,
    gender: formData.gender,
    remark: formData.remark,
    fileList: formData.cards
      .filter(card => card.url)
      .map(card => ({
        type: 'contact_card',
        storageId: card.storageId,
        url: card.url
      })),
    socialList: [
      ...formData.socialPlatforms
        .filter(item => item.account)
        .map(item => ({
          ...(item.id != null ? { id: item.id } : {}),
          socialPlatform: item.platform,
          value: item.account
        })),
      ...formData.deletedSocialItems.map(item => ({
        id: item.id,
        socialPlatform: item.platform,
        value: item.account,
        isDeleted: 1
      }))
    ],
    phoneList: [
      ...formData.phones
        .filter(item => item.number)
        .map(item => ({
          ...(item.id != null ? { id: item.id } : {}),
          areaCode: item.areaCode,
          phone: item.number
        })),
      ...formData.deletedPhoneItems.map(item => ({
        id: item.id,
        areaCode: item.areaCode,
        phone: item.number,
        isDeleted: 1
      }))
    ]
  }

  if (fromType === 'supplier-detail') {
    const apiData = {
      id: isEdit.value ? rowData?.id : undefined,
      supplierId: supplierId,
      name: formData.name,
      email: formData.email,
      position: formData.position,
      birthday: formData.birthday,
      gender: formData.gender,
      remark: formData.remark,
      fileList: result.fileList,
      socialList: result.socialList,
      phoneList: result.phoneList
    }

    const { code, message } = await saveOrUpdateSupplierContact(apiData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
  }

  if (callback) {
    callback(result)
  }

  dialogVisible.value = false

  setTimeout(() => {
    onDestroy?.()
  }, 100)
}

onMounted(() => {
  if (rowData && rowData.name) {
    isEdit.value = true

    const socialPlatforms =
      rowData.socialList && rowData.socialList.length > 0
        ? rowData.socialList.map((item: any) => ({
            id: item.id,
            platform: item.socialPlatform || '',
            account: item.value || ''
          }))
        : [{ id: undefined, platform: '', account: '' }]

    const phones =
      rowData.phoneList && rowData.phoneList.length > 0
        ? rowData.phoneList.map((item: any) => ({
            id: item.id,
            areaCode: item.areaCode || '',
            number: item.phone || item.number || ''
          }))
        : [{ id: undefined, areaCode: '', number: '' }]

    const cards =
      rowData.fileList && rowData.fileList.length > 0
        ? rowData.fileList.map((item: any) => ({
            name: item.fileName || '',
            url: item.url || '',
            storageId: item.storageId || 0
          }))
        : []

    Object.assign(formData, {
      name: rowData.name || '',
      email: rowData.email || '',
      socialPlatforms,
      phones,
      position: rowData.position || '',
      birthday: rowData.birthday || '',
      gender: rowData.gender || '',
      cards,
      remark: rowData.remark || ''
    })

    console.log('formData', formData)
  }
})
</script>

<style scoped lang="scss">
:deep(.el-form-item) {
  margin-bottom: 20px;
}

.dynamic-list {
  width: 100%;

  .dynamic-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.upload-wrapper {
  width: 100%;
}

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

.el-upload__tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
