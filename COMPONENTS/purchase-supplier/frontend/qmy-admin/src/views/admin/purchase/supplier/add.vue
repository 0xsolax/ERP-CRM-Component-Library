<template>
  <div class="supplier-add-container">
    <div class="page-header">
      <h2>新增供应商</h2>
    </div>

    <!-- 基础信息 -->
    <div class="form-content">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="top">
        <div class="section-title">基础信息</div>
        <div class="form-row form-row-4">
          <el-form-item label="供应商名称" prop="supplierName" required>
            <el-input v-model="formData.supplierName" placeholder="请输入" maxlength="50" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="简称" prop="shortName">
            <el-input v-model="formData.shortName" placeholder="请输入" maxlength="20" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="公司地址" prop="address">
            <el-input v-model="formData.address" placeholder="请输入" maxlength="100" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="formData.remark" placeholder="请输入" maxlength="100" show-word-limit clearable />
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 标签 -->
    <div class="form-content label-content">
      <div class="section-title">标签</div>
      <div class="tags-wrapper">
        <el-tag
          v-for="tag in formData.tags"
          :key="tag"
          closable
          size="large"
          @close="handleTagRemove(tag)"
          style="margin-right: 10px; margin-bottom: 10px"
        >
          {{ tag }}
        </el-tag>
        <el-input
          v-if="tagInputVisible"
          ref="tagInputRef"
          placeholder="请输入"
          v-model="tagInputValue"
          style="width: 150px"
          maxlength="10"
          show-word-limit
          @blur="handleTagConfirm"
          @keyup.enter="handleTagConfirm"
        />
        <el-button v-else size="small" @click="showTagInput" style="margin-bottom: 10px">+ 添加标签</el-button>
      </div>
    </div>

    <div class="cards-row">
      <!-- 联系人卡片 -->
      <div class="card-item">
        <div class="card-header">
          <div class="section-title">联系人</div>
          <el-button type="primary" size="small" @click="handleAddContact">新增</el-button>
        </div>
        <el-table :data="formData.contactPersonList" border style="width: 100%">
          <el-table-column label="联系人" prop="name" align="center" />
          <el-table-column label="邮箱" prop="email" align="center" />
          <el-table-column label="职位" prop="position" align="center" />
          <el-table-column label="操作" align="center" width="180">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="handleContactDetail(row)">详情</el-button>
              <el-button size="small" type="primary" link @click="handleContactEdit(row.id)">编辑</el-button>
              <el-button size="small" type="danger" link @click="handleContactDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <footer-actions>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" v-permission="'pur:yt:purchaseSupplier:add'" @click="handleSubmit">确认</el-button>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { useTagsStore } from '@/views/admin/store/modules/tags'
import FooterActions from '@/components/footer-actions/index.vue'
import { addSupplier } from '@/api/admin/purchase/supplier'
import ContactDialog from './components/contact-dialog.vue'
import ContactDetailDialog from './components/contact-detail-dialog.vue'

const router = useRouter()
const route = useRoute()
const tagsStore = useTagsStore()
const formRef = ref()
const tagInputRef = ref()

// 生成临时ID
const generateRandomId = () => {
  return 'temp_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now().toString(36)
}

const formData = reactive({
  supplierName: '',
  shortName: '',
  address: '',
  remark: '',
  tags: [] as string[],
  contactPersonList: [] as any[]
})

const rules = {
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

// 标签相关
const tagInputVisible = ref(false)
const tagInputValue = ref('')

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagRemove = (tag: string) => {
  formData.tags = formData.tags.filter(t => t !== tag)
}

const handleTagConfirm = () => {
  if (tagInputValue.value) {
    if (!formData.tags.includes(tagInputValue.value)) {
      formData.tags.push(tagInputValue.value)
    }
    tagInputValue.value = ''
  }
  tagInputVisible.value = false
}

// 联系人相关
const handleAddContact = () => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      callback: (result: any) => {
        formData.contactPersonList.push({
          ...result,
          id: generateRandomId(),
          isNew: true
        })
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleContactDetail = (row: any) => {
  const params = {
    id: 'contactDetailDialog',
    el: '#app',
    data: row,
    render: ContactDetailDialog
  }
  dynamic.show(params)
}

const handleContactEdit = (contactId: number | string) => {
  const index = formData.contactPersonList.findIndex(contact => contact.id === contactId)
  if (index === -1) return

  const row = formData.contactPersonList[index]
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      ...row,
      callback: (result: any) => {
        formData.contactPersonList[index] = {
          ...result,
          id: row.id,
          isNew: row.isNew
        }
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleContactDelete = (contactId: number | string) => {
  const index = formData.contactPersonList.findIndex(contact => contact.id === contactId)
  if (index === -1) return

  formData.contactPersonList.splice(index, 1)
}

const handleCancel = () => {
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.back()
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const submitData = {
    name: formData.supplierName,
    shortName: formData.shortName,
    address: formData.address,
    remark: formData.remark,
    labelList: formData.tags.map((tag: any) => ({
      id: tag.isNew ? undefined : tag.id,
      value: typeof tag === 'string' ? tag : tag.value
    })),
    contactPersonList: formData.contactPersonList.map((contact: any) => ({
      id: contact.isNew ? undefined : contact.id,
      name: contact.name,
      email: contact.email,
      position: contact.position,
      birthday: contact.birthday || '',
      gender: contact.gender || '',
      remark: contact.remark || '',
      supplierId: undefined,
      fileList: contact.fileList || [],
      socialList: contact.socialList || [],
      phoneList: contact.phoneList || []
    }))
  }

  const { code, message } = await addSupplier(submitData)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('新增成功')
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/purchase/supplier')
}
</script>

<style scoped lang="scss">
.supplier-add-container {
  background: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 80px;

  .page-header {
    background: #fff;
    padding: 15px 15px 0;
    border-radius: 4px;

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }

  .form-content {
    background: #fff;
    padding: 15px;
    border-radius: 4px;

    &.label-content {
      margin-top: 10px;

      .section-title {
        margin-top: 0;
      }
    }

    .section-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      padding-left: 10px;
      border-left: 3px solid #409eff;
      margin: 30px 0 20px 0;
      display: flex;
      align-items: center;
    }

    .form-row {
      display: grid;
      gap: 20px;
      margin-bottom: 20px;

      &.form-row-4 {
        grid-template-columns: repeat(4, 1fr);
      }
    }

    .tags-wrapper {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
    }
  }

  .cards-row {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
    margin-top: 10px;
    margin-bottom: 20px;

    .card-item {
      background: #fff;
      padding: 20px;
      border-radius: 4px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .section-title {
          margin: 0;
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          padding-left: 10px;
          border-left: 3px solid #409eff;
        }
      }
    }
  }
}
</style>
