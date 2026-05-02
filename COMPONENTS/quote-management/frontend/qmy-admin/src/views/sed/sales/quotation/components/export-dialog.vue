<template>
  <el-dialog v-model="dialogVisible" title="导出报价单" width="400px" @close="onDestroy">
    <el-radio-group v-model="exportType">
      <el-radio value="1">内部报价单</el-radio>
      <el-radio value="2">客户报价单</el-radio>
    </el-radio-group>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleExport">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getSedAdminToken, getSedAdminTenantInfo } from '@/utils/auth'

const attrs = useAttrs()
const { onDestroy, rowData } = attrs as any
const dialogVisible = ref(true)
const exportType = ref('1')
const loading = ref(false)

const handleExport = async () => {
  loading.value = true
  try {
    const { status, message } = await downloadAxiosBlobFile({
      url: '/api/sal/sed/quotation/exportQuotation',
      headers: {
        'qiaomoyun-tenant': getSedAdminTenantInfo()?.id,
        'qiaomoyun-token': getSedAdminToken()
      },
      data: {
        exportType: Number(exportType.value),
        quotationId: rowData?.id
      },
      method: 'post'
    })
    if (status !== 200) return ElMessage.error(message)
    ElMessage.success('导出成功')
    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}
</script>
