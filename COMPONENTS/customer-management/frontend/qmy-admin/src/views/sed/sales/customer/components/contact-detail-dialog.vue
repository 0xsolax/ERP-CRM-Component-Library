<template>
  <el-dialog v-model="dialogVisible" title="联系人详情" width="700px" @close="onDestroy">
    <div style="display: flex; justify-content: flex-end; margin-bottom: 12px">
      <el-icon :size="18" style="cursor: pointer; color: #909399" @click="toggleShowInfo" title="显示/隐藏敏感信息">
        <component :is="showInfo ? View : Hide" />
      </el-icon>
    </div>
    <el-descriptions :column="1" border label-class-name="detail-label">
      <el-descriptions-item label="联系人">
        {{ displayName }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ displayEmail }}
      </el-descriptions-item>
      <el-descriptions-item label="社交平台">
        <div v-if="contactData.socialList && contactData.socialList.length">
          <div v-for="(item, index) in contactData.socialList" :key="index" style="margin-bottom: 5px">
            {{ item.socialPlatform }}: {{ displaySocialValue(item.value) }}
          </div>
        </div>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        <div v-if="contactData.phoneList && contactData.phoneList.length">
          <div v-for="(item, index) in contactData.phoneList" :key="index" style="margin-bottom: 5px">
            {{ item.areaCode }} {{ displayPhone(item.phone) }}
          </div>
        </div>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="职位">
        {{ contactData.position }}
      </el-descriptions-item>
      <el-descriptions-item label="生日">
        {{ contactData.birthday || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ getGenderLabel(contactData.gender) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="名片/头像">
        <div v-if="contactData.fileList && contactData.fileList.length" class="card-images">
          <img
            v-for="(card, index) in contactData.fileList"
            :key="index"
            :src="card.url"
            class="card-image"
            @click="previewImage(card.url)"
          />
        </div>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="备注">
        {{ contactData.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <!-- <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleCancel">确定</el-button>
      </span> -->
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
import { ref, computed, useAttrs } from 'vue'
import { View, Hide } from '@element-plus/icons-vue'
import { desensitize } from '@/utils'

const attrs = useAttrs()
const { onDestroy, ...rowData } = attrs as any

console.log('rowData', rowData)

const genderList = [
  { label: '男', value: '0' },
  { label: '女', value: '1' },
  { label: '未知', value: '2' }
]

const getGenderLabel = (gender: number | string) => {
  const genderItem = genderList.find(item => item.value == gender)
  return genderItem ? genderItem.label : ''
}

const dialogVisible = ref(true)
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])
const showInfo = ref(false)

const toggleShowInfo = () => {
  showInfo.value = !showInfo.value
}

const contactData = ref({
  name: rowData?.name || '',
  email: rowData?.email || '',
  socialList: rowData?.socialList || [],
  phoneList: rowData?.phoneList || [],
  position: rowData?.position || '',
  birthday: rowData?.birthday || '',
  gender: rowData?.gender || '',
  fileList: rowData?.fileList || [],
  remark: rowData?.remark || ''
})

const displayName = computed(() => {
  if (!contactData.value.name) return ''
  return showInfo.value ? contactData.value.name : desensitize(contactData.value.name, 1, 1)
})

const displayEmail = computed(() => {
  if (!contactData.value.email) return ''
  if (showInfo.value) return contactData.value.email
  const [name, domain] = contactData.value.email.split('@')
  if (!name || !domain) return contactData.value.email
  return desensitize(name, 1, 1) + '@' + domain
})

const displaySocialValue = (value: string) => {
  if (!value) return ''
  return showInfo.value ? value : desensitize(value, 2, 2)
}

const displayPhone = (phone: string) => {
  if (!phone) return ''
  return showInfo.value ? phone : desensitize(phone, 3, 4)
}

const previewImage = (url: string) => {
  viewerImageList.value = [url]
  showViewer.value = true
}

// const handleCancel = () => {
//   dialogVisible.value = false
//   onDestroy?.()
// }
</script>

<style scoped lang="scss">
:deep(.detail-label) {
  width: 150px;
  text-align: right;
}

:deep(.el-descriptions__body) {
  background-color: #fff;
}

:deep(.el-descriptions__label) {
  background-color: #fafafa;
}

.card-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  .card-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 4px;
    cursor: pointer;
    border: 1px solid #e8e8e8;

    &:hover {
      border-color: #409eff;
      transform: scale(1.05);
      transition: all 0.3s;
    }
  }
}
</style>
