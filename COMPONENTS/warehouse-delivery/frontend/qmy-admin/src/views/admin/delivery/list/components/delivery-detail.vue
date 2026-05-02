<template>
  <el-dialog custom-class="delivery-detail-dialog" v-model="dialogVisible" width="80%" @close="onDestroy">
    <el-table row-key="id" v-loading="loading" :data="tableData" border>
      <el-table-column align="center" label="供应商编号" prop="supplierSkuNumber" />
      <el-table-column align="center" label="供应商规格" prop="supplierSkuName" />
      <el-table-column align="center" label="图片" prop="productImg">
        <template v-slot="scope">
          <el-image
            style="width: 50px; height: 50px"
            :src="scope.row.productImg"
            :preview-src-list="[scope.row.productImg]"
            hide-on-click-modal
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="采购数量" prop="purchaseNum" />
      <el-table-column align="center" label="供应商交货数量" prop="supplierNum" />
      <el-table-column align="center" label="确认交货数量" prop="deliveredNum" />
      <el-table-column align="center" label="入库分配数量" prop="allotNum" />
      <el-table-column align="center" label="额外入库数量" prop="subscribeNum" />
      <el-table-column align="center" label="欠数" prop="oweNum" />
      <el-table-column align="center" label="本次交货数量">
        <template v-slot="{ row }">
          <el-popover ref="popoverRef" :visible="row.showPopover" placement="bottom" :width="220" trigger="click">
            <template #default>
              <el-input v-model="row.addDeliveryNumCopy" type="number" placeholder="请输入" />
              <div style="text-align: right; margin: 10px 0 0">
                <el-button size="small" @click="cancelPopover(row)">取消</el-button>
                <el-button size="small" type="primary" @click="confirmInput(row)">确定</el-button>
              </div>
            </template>
            <template #reference>
              <el-icon :size="20" @click="openPopover(row)" class="edit-icon" style="cursor: pointer"><Edit /></el-icon>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="100">
        <template v-slot="{ row }">
          <el-button size="small" type="primary" link @click="handleDeliveryRecord(row)">交货记录</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template> -->
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { addDeliveryNum } from '@/api/admin/delivery'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

interface DialogProps {
  title?: string
  isAdd?: boolean
  rowData?: any
  callback?: () => Promise<any>
}

const props = defineProps(['onDestroy'])

const { rowData, callback } = useAttrs() as DialogProps
const dialogVisible = ref(true)
const router = useRouter()

const loading = ref(false)
const tableData = ref<any>([{}])

const openPopover = row => {
  tableData.value.forEach(item => (item.showPopover = false))
  row.showPopover = true
}

const confirmInput = async row => {
  if (!row.addDeliveryNumCopy) return ElMessage.warning('请输入本次交货数量')
  const reqBody = {
    purchaseNo: rowData.purchaseNo,
    skuId: row.skuId,
    addDeliveryNum: row.addDeliveryNumCopy
  }
  const { code, message } = await addDeliveryNum(reqBody)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('操作成功')
  row.showPopover = false
  callback!()
}

const cancelPopover = row => {
  row.showPopover = false
}

const fetchDetail = async () => {
  console.log('rowData', rowData)
  tableData.value = rowData.productList ?? []
  tableData.value.forEach(item => {
    item.addDeliveryNumCopy = item.addDeliveryNum
    item.showPopover = false
  })
}

const handleDeliveryRecord = row => {
  props.onDestroy()
  router.push({
    path: '/delivery/record',
    query: { purchaseNo: rowData.purchaseNo, supplierSkuName: row.supplierSkuName }
  })
}

function initLoad() {
  // document.addEventListener('click', e => {
  //   const tagName = e.target.tagName
  //   const isPopover = e.target.closest('.el-popover')
  //   const isTriggerBtn = e.target.closest('.el-button')
  //   if (tagName === 'svg' || tagName === 'path') return
  //   if (!isPopover && !isTriggerBtn) {
  //     tableData.value.forEach(row => (row.showPopover = false))
  //   }
  // })
}

onMounted(() => {
  initLoad()
  fetchDetail()
})

defineExpose({
  fetchDetail
})
</script>
