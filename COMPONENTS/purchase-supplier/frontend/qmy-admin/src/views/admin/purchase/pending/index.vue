<template>
  <div class="pending-purchase-container">
    <bz-table
      ref="bzTableRef"
      :fixedPagination="true"
      :columns="[]"
      :searchColumns="searchColumns"
      :requestApi="getApplyPurchaseList"
      :dataCallback="dataCallback"
      :searchCol="searchCol"
      :customGridConfig="customGridConfig"
      :hideTable="true"
      :hideTableHeader="true"
      :toolButton="false"
    >
      <template #extra>
        <div class="supplier-cards">
          <div v-for="(supplier, idx) in supplierGroups" :key="idx" class="supplier-card">
            <div class="card-header">
              <div class="header-left">
                <span class="header-info-item">
                  <span class="info-label">供应商名称：</span>
                  <span class="info-value">{{ supplier.supplierName }}</span>
                  <el-tag type="primary" effect="plain" size="small" style="margin-left: 10px">
                    {{ supplier.isInboundDelivery ? '入库发货' : '供应商发货' }}
                  </el-tag>
                </span>
                <span class="header-info-item">
                  <span class="info-label">预计采购数量：</span>
                  <span class="info-value">{{ supplier.purchaseTotalCount }}</span>
                </span>
                <span class="header-info-item">
                  <span class="info-label">预计采购总价：</span>
                  <span class="info-value">¥{{ supplier.purchaseTotalAmount?.toFixed(2) || '0.00' }}</span>
                </span>
              </div>
              <div class="header-right">
                <el-button
                  type="primary"
                  :disabled="hasSelectedSemiFinished(idx)"
                  v-permission="'pur:yt:applyPurchase:replaceSupplier'"
                  @click="handleChangeSupplier(idx, supplier)"
                >
                  更换供应商
                </el-button>
                <el-button
                  type="primary"
                  v-permission="'pur:yt:applyPurchase:addPurchase'"
                  @click="handleAppend(idx, supplier)"
                  style="margin-left: 0"
                >
                  追加至暂存采购单
                </el-button>
                <el-button
                  type="primary"
                  v-permission="'pur:yt:applyPurchase:saveOrUpdate'"
                  @click="handleGenerateNewOrder(idx, supplier)"
                  style="margin-left: 0"
                >
                  生成新采购单
                </el-button>
              </div>
            </div>
            <div class="card-body">
              <el-table
                :data="supplier.products"
                border
                :span-method="spanMethod"
                :max-height="400"
                @selection-change="selection => handleSelectionChange(idx, selection)"
              >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="产品ID" prop="productCode" align="center" width="150" />
                <el-table-column label="规格名称" prop="specName" align="center">
                  <template #default="{ row }">
                    <span v-if="row.isSemiFinished">半成品</span>
                    <span v-else-if="row.specName">{{ row.specName }}</span>
                    <span v-else style="color: #999">-</span>
                  </template>
                </el-table-column>
                <el-table-column label="规格图片" align="center">
                  <template #default="{ row }">
                    <span v-if="row.isSemiFinished">半成品</span>
                    <el-image
                      v-else-if="row.specImage"
                      :src="row.specImage"
                      style="width: 50px; height: 50px; cursor: pointer"
                      fit="cover"
                      @click="handleImagePreview(row.specImage)"
                    />
                    <span v-else style="color: #999">-</span>
                  </template>
                </el-table-column>
                <el-table-column label="申购数量" prop="number" align="center" />
                <el-table-column label="定制化属性" prop="categoryLabelName" align="center" />
                <el-table-column label="订单编号" prop="orderCode" align="center" />
                <el-table-column label="业务员" prop="salesEmployeeName" align="center" />
                <el-table-column label="客户名称" prop="customerName" align="center" />
                <el-table-column label="下单时间" align="center">
                  <template #default="{ row }">
                    <span>{{ formatDate(row.orderTime) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="交货时间" align="center">
                  <template #default="{ row }">
                    <span>{{ formatDate(row.deliveryTime) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="订单规格备注" align="center">
                  <template #default="{ row }">
                    <div class="remark-cell">
                      <span>{{ row.orderRemark }}</span>
                      <template v-if="row.orderNote && row.orderNote !== '-'">
                        <el-divider style="margin: 4px 0" />
                        <span class="order-note">{{ row.orderNote }}</span>
                      </template>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="供应商单价" align="center">
                  <template #default="{ row }">
                    <span v-if="row.supplierPrice != null">¥{{ row.supplierPrice }}</span>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="220" fixed="right">
                  <template #default="{ row }">
                    <el-button
                      type="primary"
                      link
                      size="small"
                      v-permission="'pur:yt:applyPurchase:addPurchase'"
                      @click="handleSingleAppend(row, supplier)"
                    >
                      追加
                    </el-button>
                    <el-button
                      type="primary"
                      link
                      size="small"
                      v-if="!row.isSemiFinished"
                      v-permission="'pur:yt:applyPurchase:replaceSupplier'"
                      @click="handleChangeSingleSupplier(row, supplier)"
                    >
                      更换供应商
                    </el-button>
                    <!-- prettier-ignore -->
                    <el-button type="danger" link size="small" v-if="!row.orderCode" @click="handleWithdraw(row)">
                      退回
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <el-empty v-if="!supplierGroups.length" description="暂无数据" />
        </div>
      </template>
    </bz-table>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerImageList"
      :initial-index="0"
      hide-on-click-modal
      @close="showViewer = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getAllEmployee } from '@/api/admin/auth/org'
import { getApplyPurchaseList, withdrawApplyPurchase } from '@/api/admin/purchase/pending'
import { dynamic } from '@bzlab/bz-core'
import ChangeSupplierDialog from './components/change-supplier-dialog.vue'
import AppendDialog from './components/append-dialog.vue'
import AppendSingleDialog from './components/append-single-dialog.vue'
import ChangeSingleSupplierDialog from './components/change-single-supplier-dialog.vue'
import dayjs from 'dayjs'

const searchCol = { xs: 3, sm: 4, md: 5, lg: 6, xl: 7 }
const customGridConfig = {
  xs: '1fr',
  sm: '1fr 1fr 1fr',
  md: '1fr 1fr 1fr 1fr',
  lg: '0.5fr 0.5fr 0.5fr 0.5fr 0.5fr 0.5fr',
  xl: '0.5fr 0.5fr 0.5fr 0.5fr 0.5fr 0.5fr 0.5fr'
}
const router = useRouter()
const bzTableRef = ref()
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

// 搜索列配置
const searchColumns = computed(() => [
  {
    label: '订单编号',
    prop: 'orderCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '产品ID',
    prop: 'productCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '图片',
    prop: 'specImage',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '规格名称',
    prop: 'specificationName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '业务员',
    prop: 'salesEmployeeId',
    enum: employeeList.value,
    fieldNames: { label: 'nickName', value: 'userId' },
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  },
  {
    label: '供应商名称',
    prop: 'supplierName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '客户名称',
    prop: 'customerName',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  }
])

const employeeList = ref<any[]>([])
const selectionMap = ref<Record<number, any[]>>({})

const dataCallback = (data: any) => {
  return {
    list: data?.list || [],
    total: Number(data?.total || 0)
  }
}

const supplierGroups = computed(() => {
  const list = bzTableRef.value?.tableData || []
  return list.map((supplier: any) => {
    const products = supplier.applyPurchaseList || []
    const productGroups: any = {}
    products.forEach((item: any) => {
      const productCode = item.productCode || ''
      if (!productGroups[productCode]) {
        productGroups[productCode] = []
      }
      productGroups[productCode].push(item)
    })

    const productsWithSpan: any[] = []
    Object.keys(productGroups).forEach(productCode => {
      const productGroup = productGroups[productCode]
      productGroup.forEach((item: any, index: number) => {
        const isSemiFinished = !item.productSpecificationId
        const specName =
          item.itemList
            ?.map((spec: any) => `${spec.categorySpecificationName}-${spec.categorySpecificationItemValue}`)
            .join('/') || '-'
        const specImage = item.imageList?.[0]?.url || ''

        productsWithSpan.push({
          ...item,
          specName,
          specImage,
          isSemiFinished,
          categoryLabelName: item.categoryLabelName || '-',
          salesEmployeeName: item.salesEmployeeName || '-',
          orderRemark: item.orderRemark || '-',
          orderNote: item.orderNote || '',
          isFirstInGroup: index === 0,
          groupRowSpan: productGroup.length
        })
      })
    })

    return {
      ...supplier,
      products: productsWithSpan
    }
  })
})

const loadAllEmployeeList = async () => {
  const { code, data, message } = await getAllEmployee({})
  if (code !== 200) return ElMessage.warning(message)
  employeeList.value = data || []
}

const handleSelectionChange = (idx: number, selection: any[]) => {
  selectionMap.value[idx] = selection
}

const getSelectedProducts = (idx: number) => {
  return selectionMap.value[idx] || []
}

const hasSelectedSemiFinished = (idx: number) => {
  return getSelectedProducts(idx).some((p: any) => p.isSemiFinished)
}

const spanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 1) {
    if (row.isFirstInGroup) {
      return {
        rowspan: row.groupRowSpan,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
}

const handleChangeSingleSupplier = (row: any, supplier: any) => {
  const params = {
    id: 'changeSingleSupplierDialog',
    el: '#app',
    data: {
      productCode: row.productCode,
      specName: row.specName,
      originalSupplier: supplier.supplierName,
      rowData: row,
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: ChangeSingleSupplierDialog
  }
  dynamic.show(params)
}

const handleWithdraw = async (row: any) => {
  await ElMessageBox.confirm('确认退回该申购记录？退回后将从待采购列表中移除', '确认退回', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const { code, message } = await withdrawApplyPurchase({ id: row.id })
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('退回成功')
  bzTableRef.value?.getTableList()
}

const handleChangeSupplier = (idx: number, supplier: any) => {
  const selectedProducts = getSelectedProducts(idx)
  if (selectedProducts.length === 0) {
    ElMessage.warning('请选择要更换供应商的产品')
    return
  }
  const params = {
    id: 'changeSupplierDialog',
    el: '#app',
    data: {
      selectedProducts,
      originalSupplier: supplier.supplierName,
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: ChangeSupplierDialog
  }
  dynamic.show(params)
}

const handleSingleAppend = (row: any, supplier: any) => {
  const params = {
    id: 'appendSingleDialog',
    el: '#app',
    data: {
      productCode: row.productCode,
      specName: row.isSemiFinished ? '半成品' : row.specName,
      supplierId: row.supplierId,
      supplier: supplier,
      rowData: row,
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: AppendSingleDialog
  }
  dynamic.show(params)
}

// 追加至暂存采购单
const handleAppend = (idx: number, supplier: any) => {
  const selectedProducts = getSelectedProducts(idx)
  if (selectedProducts.length === 0) {
    ElMessage.warning('请选择要追加的产品')
    return
  }
  const params = {
    id: 'appendDialog',
    el: '#app',
    data: {
      supplierId: supplier.supplierId,
      supplier: supplier,
      selectedProducts,
      callback: () => {
        bzTableRef.value?.getTableList()
      }
    },
    render: AppendDialog
  }
  dynamic.show(params)
}

const handleGenerateNewOrder = (idx: number, supplier: any) => {
  const selectedProducts = getSelectedProducts(idx)
  if (selectedProducts.length === 0) {
    ElMessage.warning('请选择要生成采购单的产品')
    return
  }
  const applyPurchaseIdList = selectedProducts.map(item => item.id)

  router.push({
    path: '/purchase/pending/generate-order',
    query: {
      from: 'pending',
      applyPurchaseIdList: JSON.stringify(applyPurchaseIdList),
      isInboundDelivery: supplier.isInboundDelivery ? '1' : '0'
    }
  })
}

onMounted(() => {
  loadAllEmployeeList()
})
</script>

<style scoped lang="scss">
.pending-purchase-container {
  background: #f5f7fa;
  min-height: calc(100vh - 100px);

  .search-section {
    background: #fff;
    padding: 20px 20px 0 20px;
    margin-bottom: 10px;
    border-radius: 8px;
  }

  .remark-cell {
    display: flex;
    flex-direction: column;
    align-items: center;

    .order-note {
      color: #909399;
      font-size: 12px;
    }
  }

  .supplier-cards {
    background: #fff;
    border-radius: 4px;
    padding-bottom: 50px;
    .supplier-card {
      background: #fff;
      border-radius: 8px;
      margin-bottom: 10px;
      overflow: hidden;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 14px;

        .header-left {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 20px;

          .header-info-item {
            font-size: 14px;

            .info-label {
              color: #606266;
            }

            .info-value {
              color: #303133;
              margin-left: 4px;
            }
          }
        }

        .header-right {
          display: flex;
          gap: 10px;
        }
      }

      .card-body {
        padding: 0;
      }
    }
  }
}
</style>
