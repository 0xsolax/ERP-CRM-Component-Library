<template>
  <el-drawer v-model="drawerVisible" title="跟进" size="500px" direction="rtl" @close="onDestroy">
    <div class="drawer-header">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="-"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        value-format="YYYY-MM-DD HH:mm:ss"
        style="width: 200px"
      />
      <el-button type="primary" @click="handleAddFollow">跟进</el-button>
    </div>
    <div class="follow-timeline">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in followList"
          :key="index"
          :timestamp="item.time"
          placement="top"
          :color="'#409eff'"
        >
          <div class="timeline-card">
            <div class="timeline-title">跟进</div>
            <div class="timeline-content">
              <div class="detail-item">
                <span class="label">跟进人：</span>
                <span class="value">{{ item.createUserName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">主题：</span>
                <span class="value">{{ item.theme }}</span>
              </div>
              <div class="detail-item">
                <span class="label">当前进度：</span>
                <span class="value">{{ item.description }}</span>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
    <el-empty v-if="!followList.length" description="暂无数据" />

    <!-- 跟进弹窗 -->
    <el-dialog v-model="followDialogVisible" title="跟进" width="400px">
      <el-form ref="formRef" :model="followForm" :rules="formRules" label-width="80px">
        <el-form-item label="主题" prop="theme">
          <el-input v-model="followForm.theme" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="followForm.date"
            type="date"
            placeholder="请输入"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="当前进度" prop="description">
          <el-input v-model="followForm.description" type="textarea" :rows="3" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmitFollow">确定</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getPurchaseFollowList, followCreateOrUpdate } from '@/api/admin/purchase/purchased'

const attrs = useAttrs()
const { purchaseId, onDestroy } = attrs as any

const drawerVisible = ref(true)
const dateRange = ref<string[]>([])
const followList = ref<any[]>([])

const followDialogVisible = ref(false)
const formRef = ref()
const followForm = ref({
  theme: '',
  date: '',
  description: ''
})

const formRules = {
  theme: [{ required: true, message: '请输入主题', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  description: [{ required: true, message: '请输入当前进度', trigger: 'blur' }]
}

const loadFollowList = async () => {
  const params: any = {
    purchaseId: purchaseId,
    startTime: dateRange.value?.[0] || null,
    endTime: dateRange.value?.[1] || null
  }
  const { code, data, message } = await getPurchaseFollowList(params)
  if (code !== 200) return ElMessage.warning(message)
  followList.value = (data || []).map((item: any) => ({
    time: item.createTime || '',
    createUserName: item.createUserName || '-',
    theme: item.theme || '-',
    description: item.description || '-'
  }))
}

const handleAddFollow = () => {
  followForm.value = {
    theme: '',
    date: '',
    description: ''
  }
  console.log('555')

  followDialogVisible.value = true
}

const handleSubmitFollow = async () => {
  await formRef.value?.validate()

  const params = {
    purchaseId: purchaseId,
    theme: followForm.value.theme,
    contactPerson: followForm.value.theme,
    description: followForm.value.description
  }
  const { code, message } = await followCreateOrUpdate(params)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  followDialogVisible.value = false
  loadFollowList()
}

watch(dateRange, () => {
  loadFollowList()
})

onMounted(() => {
  loadFollowList()
})
</script>

<style scoped lang="scss">
.drawer-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.follow-timeline {
  padding: 20px;
  .el-timeline {
    padding-left: 0;
  }

  .timeline-card {
    .timeline-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 10px;
    }

    .timeline-content {
      .detail-item {
        font-size: 14px;
        line-height: 24px;
      }
    }
  }
}
</style>
