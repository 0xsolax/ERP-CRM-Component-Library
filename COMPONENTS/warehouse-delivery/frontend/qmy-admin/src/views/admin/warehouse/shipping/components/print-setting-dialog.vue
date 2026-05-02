<template>
  <el-dialog v-model="dialogVisible" title="打印设置" width="500px" @close="onDestroy">
    <div class="print-options">
      <div
        v-for="item in printOptions"
        :key="item.value"
        :class="['print-option', { active: selectedType === item.value }]"
        @click="selectedType = item.value"
      >
        <div class="option-icon">
          <el-icon :size="40">
            <component :is="item.icon" />
          </el-icon>
        </div>
        <div class="option-label">{{ item.label }}</div>
      </div>
    </div>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, markRaw } from 'vue'
import { Aim, Document, Box } from '@element-plus/icons-vue'

const attrs = useAttrs()
const { onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const selectedType = ref('product')

const printOptions = [
  {
    value: 'product',
    label: '按产品',
    icon: markRaw(Aim)
  },
  {
    value: 'order',
    label: '按订单',
    icon: markRaw(Document)
  },
  {
    value: 'package',
    label: '按包裹',
    icon: markRaw(Box)
  }
]

const handleConfirm = () => {
  if (callback) callback(selectedType.value)
  dialogVisible.value = false
}
</script>

<style scoped lang="scss">
.print-options {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding: 30px 0;

  .print-option {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;

    .option-icon {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
      border: 2px solid transparent;
      background: #f0f0f0;
    }

    .option-label {
      font-size: 14px;
      color: #606266;
    }

    &.active {
      .el-icon {
        color: #409eff;
      }
      .option-icon {
        border-color: #409eff;
      }
      .option-label {
        color: #409eff;
      }
    }

    &:hover {
      .el-icon {
        color: #409eff;
      }
      .option-icon {
        border-color: #409eff;
      }
    }
  }
}
</style>
