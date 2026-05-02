<template>
  <el-dialog v-model="dialogVisible" title="确认打包完成" width="520px" @close="handleCancel">
    <div class="dialog-content">
      <div class="actions">
        <el-button class="action-btn" @click="handleSendMessage">给业务员发信息，让他改发货形式</el-button>
        <el-button type="danger" class="action-btn" @click="handleComplete">
          现有就{{ completeNumber }}个订单已经打包完毕，就发这些，我已经把多余货品拿出
        </el-button>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'

const attrs = useAttrs()
const { completeNumber, onDestroy, onSendMessage, onComplete } = attrs as any

const dialogVisible = ref(true)

const handleCancel = () => {
  dialogVisible.value = false
  if (onDestroy) onDestroy()
}

const handleSendMessage = async () => {
  if (onSendMessage) await onSendMessage()
  dialogVisible.value = false
  if (onDestroy) onDestroy()
}

const handleComplete = async () => {
  if (onComplete) await onComplete()
  dialogVisible.value = false
  if (onDestroy) onDestroy()
}
</script>

<style lang="scss" scoped>
.dialog-content {
  .desc {
    margin-bottom: 16px;
    color: #606266;
    font-size: 14px;
  }

  .actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .action-btn {
    width: 100%;
    margin-left: 0;
    white-space: normal;
    height: auto;
    padding: 12px;
  }
}
</style>
