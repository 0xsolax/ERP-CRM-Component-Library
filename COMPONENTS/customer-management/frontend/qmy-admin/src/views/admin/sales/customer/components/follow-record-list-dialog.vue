<template>
  <el-dialog v-model="visible" title="跟进记录" width="800px" @close="handleClose">
    <div class="follow-record-list-content">
      <div class="list-header">
        <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="followList" style="width: 100%" height="400">
        <el-table-column label="主题" align="center" prop="theme" />
        <el-table-column label="日期" align="center" prop="createTime" />
        <el-table-column label="下次回访日期" align="center" prop="nextVisitDate" />
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
import { getCustomerDetail } from '@/api/admin/sales/customer'
import FollowRecordDialog from './follow-record-dialog.vue'
import FollowRecordDetailDialog from './follow-record-detail-dialog.vue'

const attrs = useAttrs() as any
const visible = ref(true)
const followList = ref<any[]>([])

const loadFollowList = async () => {
  const { code, data, message } = await getCustomerDetail({ id: attrs.customerId })
  if (code !== 200) return ElMessage.warning(message)
  followList.value = data.followList || []

  if (attrs.onUpdate) {
    attrs.onUpdate(followList.value)
  }
}

const handleAdd = () => {
  const params = {
    id: 'followRecordDialog',
    el: '#app',
    data: {
      customerId: attrs.customerId,
      fromType: 'customer-detail',
      callback: async () => {
        await loadFollowList()
      }
    },
    render: FollowRecordDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'followRecordDialog',
    el: '#app',
    data: {
      ...row,
      customerId: attrs.customerId,
      fromType: 'customer-detail',
      callback: async () => {
        await loadFollowList()
      }
    },
    render: FollowRecordDialog
  }
  dynamic.show(params)
}

const handleDetail = (row: any) => {
  const params = {
    id: 'followRecordDetailDialog',
    el: '#app',
    data: row,
    render: FollowRecordDetailDialog
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
//     attrs.onConfirm(followList.value)
//   }
//   handleClose()
// }

onMounted(() => {
  loadFollowList()
})
</script>

<style scoped lang="scss">
.follow-record-list-content {
  .list-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
  }
}
</style>
