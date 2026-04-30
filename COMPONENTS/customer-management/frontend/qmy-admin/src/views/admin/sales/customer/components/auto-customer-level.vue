<template>
  <el-dialog
    class="auto-customer-level-dialog"
    v-model="dialogVisible"
    title="自动客户层级规则"
    width="600px"
    :before-close="handleBeforeClose"
    @close="onDestroy"
  >
    <el-form :model="form" label-width="140px">
      <div class="level-section">
        <div class="level-title">A级</div>
        <el-form-item label="累计订单金额：">
          <el-input
            v-model="form.customerLevelOrderAmountA"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelOrderAmountA = validateInteger(val))"
          />
        </el-form-item>
        <el-form-item label="累计订单单数：">
          <el-input
            v-model="form.customerLevelOrderNumberA"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelOrderNumberA = validateInteger(val))"
          />
        </el-form-item>
        <el-form-item label="时间周期（月）：">
          <el-input
            v-model="form.customerLevelMonthRangeA"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelMonthRangeA = validateInteger(val))"
          />
        </el-form-item>
      </div>
      <div class="level-section">
        <div class="level-title">B级</div>
        <el-form-item label="累计订单金额：">
          <el-input
            v-model="form.customerLevelOrderAmountB"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelOrderAmountB = validateInteger(val))"
          />
        </el-form-item>
        <el-form-item label="累计订单单数：">
          <el-input
            v-model="form.customerLevelOrderNumberB"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelOrderNumberB = validateInteger(val))"
          />
        </el-form-item>
        <el-form-item label="时间周期（月）：">
          <el-input
            v-model="form.customerLevelMonthRangeB"
            placeholder="请输入"
            @input="(val: string) => (form.customerLevelMonthRangeB = validateInteger(val))"
          />
        </el-form-item>
      </div>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleBeforeClose()">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAutoCustomerLevel, saveAutoCustomerLevel } from '@/api/admin/sales/customer'
import { validateInteger } from '@/utils/validate'

const props = defineProps(['onDestroy'])

const dialogVisible = ref(true)

const form = reactive<any>({
  customerLevelOrderAmountA: '',
  customerLevelOrderProductNumberA: '',
  customerLevelOrderNumberA: '',
  customerLevelMonthRangeA: '',
  customerLevelOrderAmountB: '',
  customerLevelOrderProductNumberB: '',
  customerLevelOrderNumberB: '',
  customerLevelMonthRangeB: ''
})

const hasContent = computed(() => {
  return !!(
    form.customerLevelOrderAmountA ||
    form.customerLevelOrderNumberA ||
    form.customerLevelMonthRangeA ||
    form.customerLevelOrderAmountB ||
    form.customerLevelOrderNumberB ||
    form.customerLevelMonthRangeB
  )
})

const handleBeforeClose = async (done?: () => void) => {
  if (!hasContent.value) {
    if (done) done()
    else props.onDestroy?.()
    return
  }
  await ElMessageBox.confirm('有内容未保存，是否确认关闭？', '提示', {
    confirmButtonText: '确认关闭',
    cancelButtonText: '取消',
    type: 'warning'
  })
  if (done) done()
  else props.onDestroy?.()
}

const loadData = async () => {
  const { code, data, message } = await getAutoCustomerLevel({ code: 'autoCustomerLevel' })
  if (code !== 200) return ElMessage.warning(message)

  if (Array.isArray(data)) {
    const formKeys = Object.keys(form)
    data.forEach((item: any) => {
      if (formKeys.includes(item.key)) {
        form[item.key] = item.value || ''
      }
    })
  }
}

const handleSubmit = async () => {
  const { code, message } = await saveAutoCustomerLevel(form)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success('设置成功')
  dialogVisible.value = false
  props.onDestroy?.()
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.auto-customer-level-dialog {
  .level-section {
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    padding: 20px;
    margin-bottom: 20px;
    position: relative;

    &:last-child {
      margin-bottom: 0;
    }

    .level-title {
      position: absolute;
      top: -12px;
      left: 10px;
      background: #fff;
      padding: 0 10px;
      font-size: 14px;
      font-weight: 500;
      color: #606266;
    }

    .el-form-item {
      margin-bottom: 18px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
