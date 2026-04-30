<template>
  <el-dialog v-model="dialogVisible" title="收货地址详情" width="700px" @close="onDestroy">
    <el-descriptions :column="1" border label-class-name="detail-label">
      <el-descriptions-item label="收货人">
        {{ addressData.consignee }}
      </el-descriptions-item>
      <el-descriptions-item label="收货人联系方式">
        {{ addressData.phone }}
      </el-descriptions-item>
      <el-descriptions-item label="收货地址">
        <div style="white-space: pre-line; line-height: 1.8">
          {{ fullAddress }}
        </div>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <!-- <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleCancel">确定</el-button>
      </span> -->
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs } from 'vue'

const attrs = useAttrs()
const { onDestroy, ...rowData } = attrs as any

const dialogVisible = ref(true)

const addressData = ref({
  consignee: rowData?.consignee || '',
  phone: rowData?.phone || '',
  countryRegion: rowData?.countryRegion || '',
  province: rowData?.province || '',
  city: rowData?.city || '',
  county: rowData?.county || '',
  detail: rowData?.detail || ''
})

const fullAddress = computed(() => {
  const parts: string[] = []

  if (addressData.value.countryRegion === '中国') {
    // 中国地址：中国/省/市/县
    if (addressData.value.countryRegion) parts.push(addressData.value.countryRegion)
    if (addressData.value.province) parts.push(addressData.value.province)
    if (addressData.value.city) parts.push(addressData.value.city)
    if (addressData.value.county && addressData.value.county != '0') {
      parts.push(addressData.value.county)
    }
  } else {
    // 其他国家：国家/地区
    if (addressData.value.countryRegion) parts.push(addressData.value.countryRegion)
  }

  const firstLine = parts.join('/')
  const secondLine = addressData.value.detail

  return firstLine && secondLine ? `${firstLine}\n${secondLine}` : firstLine || secondLine
})

// const handleCancel = () => {
//   dialogVisible.value = false
//   onDestroy?.()
// }
</script>

<style scoped lang="scss">
:deep(.el-descriptions__label) {
  width: 190px;
  background-color: #fafafa;
  text-align: right;
}

:deep(.el-descriptions__body) {
  background-color: #fff;
}
</style>
