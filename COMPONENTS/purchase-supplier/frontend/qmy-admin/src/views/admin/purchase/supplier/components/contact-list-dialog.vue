<template>
  <el-dialog v-model="visible" title="联系人" width="800px" @close="handleClose">
    <div class="contact-list-content">
      <div class="list-header">
        <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="contactPersonList" style="width: 100%" height="400">
        <el-table-column label="联系人" align="center" prop="name" />
        <el-table-column label="邮箱" align="center" prop="email" />
        <el-table-column label="职位" align="center" prop="position" />
        <el-table-column label="操作" align="center" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <template #footer>
      <!-- <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button> -->
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { dynamic } from '@bzlab/bz-core'
import { ElMessage } from 'element-plus'
import { getSupplierDetail } from '@/api/admin/purchase/supplier'
import ContactDialog from './contact-dialog.vue'
import ContactDetailDialog from './contact-detail-dialog.vue'

const attrs = useAttrs() as any
const visible = ref(true)
const contactPersonList = ref<any[]>([])

const loadContactList = async () => {
  const { code, data, message } = await getSupplierDetail({ id: attrs.supplierId })
  if (code !== 200) return ElMessage.warning(message)

  contactPersonList.value = (data.contactPersonList || []).map((contact: any) => ({
    id: contact.id,
    supplierId: contact.supplierId,
    name: contact.name,
    email: contact.email,
    position: contact.position,
    birthday: contact.birthday,
    gender: contact.gender,
    remark: contact.remark,
    fileList: contact.fileList || [],
    socialList: contact.socialList || [],
    phoneList: contact.phoneList || []
  }))

  if (attrs.onUpdate) {
    attrs.onUpdate(contactPersonList.value)
  }
}

const handleAdd = () => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      supplierId: attrs.supplierId,
      fromType: 'supplier-detail',
      callback: async () => {
        await loadContactList()
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'contactDialog',
    el: '#app',
    data: {
      ...row,
      supplierId: attrs.supplierId,
      fromType: 'supplier-detail',
      callback: async () => {
        await loadContactList()
      }
    },
    render: ContactDialog
  }
  dynamic.show(params)
}

const handleDetail = (row: any) => {
  const params = {
    id: 'contactDetailDialog',
    el: '#app',
    data: row,
    render: ContactDetailDialog
  }
  dynamic.show(params)
}

const handleClose = () => {
  visible.value = false
  if (attrs.onClose) {
    attrs.onClose()
  }
}

// const handleConfirm = () => {
//   if (attrs.onConfirm) {
//     attrs.onConfirm(contactPersonList.value)
//   }
//   handleClose()
// }

onMounted(() => {
  loadContactList()
})
</script>

<style scoped lang="scss">
.contact-list-content {
  .list-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
  }
}
</style>
