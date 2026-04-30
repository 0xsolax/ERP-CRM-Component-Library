<template>
  <el-dialog v-model="visible" title="收货地址" width="800px" @close="handleClose">
    <div class="address-list-content">
      <div class="list-header">
        <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="addressList" style="width: 100%" height="400">
        <el-table-column label="收货人" align="center" prop="consignee" />
        <el-table-column label="联系方式" align="center" prop="phone" />
        <el-table-column label="收货地址" align="center">
          <template #default="{ row }">
            <template v-if="row.countryRegion === '中国'">
              {{ [row.countryRegion, row.province, row.city, row.county, row.detail].filter(Boolean).join('/') }}
            </template>
            <template v-else>
              {{ [row.countryRegion, row.detail].filter(Boolean).join('/') }}
            </template>
          </template>
        </el-table-column>
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
import AddressDialog from './address-dialog.vue'
import AddressDetailDialog from './address-detail-dialog.vue'

const attrs = useAttrs() as any
const visible = ref(true)
const addressList = ref<any[]>([])

// 从接口加载地址列表
const loadAddressList = async () => {
  const { code, data, message } = await getCustomerDetail({ id: attrs.customerId })
  if (code !== 200) return ElMessage.warning(message)

  addressList.value = (data.addressList || []).map((addr: any) => ({
    id: addr.id,
    customerId: addr.customerId,
    consignee: addr.consignee,
    phone: addr.phone,
    countryRegion: addr.countryRegion,
    countryRegionId: addr.countryRegionId,
    regionId: addr.regionId,
    province: addr.province,
    provinceId: addr.provinceId,
    city: addr.city,
    cityId: addr.cityId,
    county: addr.county,
    countyId: addr.countyId,
    detail: addr.detail
  }))

  if (attrs.onUpdate) {
    attrs.onUpdate(addressList.value)
  }
}

const handleAdd = () => {
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      customerId: attrs.customerId,
      fromType: 'customer-detail',
      callback: async () => {
        await loadAddressList()
      }
    },
    render: AddressDialog
  }
  dynamic.show(params)
}

const handleEdit = (row: any) => {
  const params = {
    id: 'addressDialog',
    el: '#app',
    data: {
      ...row,
      customerId: attrs.customerId,
      fromType: 'customer-detail',
      callback: async () => {
        await loadAddressList()
      }
    },
    render: AddressDialog
  }
  dynamic.show(params)
}

const handleDetail = (row: any) => {
  const params = {
    id: 'addressDetailDialog',
    el: '#app',
    data: row,
    render: AddressDetailDialog
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
//     attrs.onConfirm(addressList.value)
//   }
//   handleClose()
// }

onMounted(() => {
  loadAddressList()
})
</script>

<style scoped lang="scss">
.address-list-content {
  .list-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
  }
}
</style>
