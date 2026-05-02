<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑打包箱' : '新增打包箱'"
    width="600px"
    :before-close="handleBeforeClose"
    @close="onDestroy"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" label-position="top">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="打包箱长度" prop="length">
            <el-input
              v-model="form.length"
              placeholder="请输入"
              maxlength="10"
              @input="(val: string) => (form.length = validateDecimal(val))"
            >
              <template #suffix>cm</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="打包箱宽度" prop="width">
            <el-input
              v-model="form.width"
              placeholder="请输入"
              maxlength="10"
              @input="(val: string) => (form.width = validateDecimal(val))"
            >
              <template #suffix>cm</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="打包箱高度" prop="height">
            <el-input
              v-model="form.height"
              placeholder="请输入"
              maxlength="10"
              @input="(val: string) => (form.height = validateDecimal(val))"
            >
              <template #suffix>cm</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="打包箱重量" prop="weight">
            <el-input
              v-model="form.weight"
              placeholder="请输入"
              maxlength="10"
              @input="(val: string) => (form.weight = validateDecimal(val))"
            >
              <template #suffix>g</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="handleBeforeClose()">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, useAttrs, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveOrUpdatePackingBox, getPackingBoxDetail } from '@/api/admin/warehouse'
import { validateDecimal } from '@/utils/validate'
import { usePermissionStore } from '@/views/admin/store/modules/permission'

const permissionStore = usePermissionStore()
const userPermissions = permissionStore.permissions
const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const formRef = ref()

const form = reactive({
  id: '',
  code: '',
  length: '',
  width: '',
  height: '',
  weight: ''
})

const rules = reactive({
  length: [{ required: true, message: '请输入打包箱长度', trigger: 'blur' }],
  width: [{ required: true, message: '请输入打包箱宽度', trigger: 'blur' }],
  height: [{ required: true, message: '请输入打包箱高度', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入打包箱重量', trigger: 'blur' }]
})

const hasContent = computed(() => {
  return !!(form.length || form.width || form.height || form.weight)
})

const handleBeforeClose = async (done?: () => void) => {
  if (!hasContent.value) {
    if (done) done()
    else onDestroy?.()
    return
  }
  await ElMessageBox.confirm('有内容未保存，是否确认关闭？', '提示', {
    confirmButtonText: '确认关闭',
    cancelButtonText: '取消',
    type: 'warning'
  })
  if (done) done()
  else onDestroy?.()
}

const loadDetail = async () => {
  if (!isEdit) return
  const { code, data, message } = await getPackingBoxDetail(rowData.id)
  if (code !== 200) return ElMessage.warning(message)
  if (data) {
    form.id = data.id || ''
    form.code = data.code || ''
    form.length = data.length || ''
    form.width = data.width || ''
    form.height = data.height || ''
    form.weight = data.weight || ''
  }
}

onMounted(() => {
  if (userPermissions.includes('sto:yt:box:detail')) {
    loadDetail()
  }
})

const handleSubmit = async () => {
  await formRef.value.validate()

  const submitData: any = {
    length: Number(form.length) || 0,
    width: Number(form.width) || 0,
    height: Number(form.height) || 0,
    weight: Number(form.weight) || 0
  }

  if (isEdit && form.id) {
    submitData.id = form.id
  }

  if (form.code) {
    submitData.code = form.code
  }

  const { code, message } = await saveOrUpdatePackingBox(submitData)
  if (code !== 200) return ElMessage.warning(message)

  ElMessage.success(isEdit ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  if (callback) callback()
}
</script>

<style scoped lang="scss"></style>
