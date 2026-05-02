<template>
  <div class="warehouse-history-page">
    <div class="page-header">
      <div class="header-left">
        <div class="product-info">
          <el-image
            v-if="productImage"
            :src="productImage"
            v-image-preview="productImage"
            style="width: 60px; height: 60px; margin-right: 16px"
            fit="cover"
          />
          <div class="info-text">
            <div class="info-row">
              <span class="label">产品ID：</span>
              <span class="value">{{ productCode }}</span>
            </div>
            <div class="info-row">
              <span class="label">规格名称：</span>
              <span class="value">{{ specName }}</span>
            </div>
            <div class="info-row">
              <span class="label">库位：</span>
              <span class="value">{{ location || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="stats-card" @click="handleOccupation('stock')">
          <div class="stats-icon">
            <el-icon color="#409EFF" :size="32"><Box /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-header">
              <span class="stats-label">实际库存</span>
              <el-tooltip content="仓库中产品总数量" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
            <div class="stats-value" style="cursor: pointer">
              {{ formatNumber(realStore) }}
            </div>
            <div class="stats-sub">
              <span>
                可用
                <b>{{ formatNumber(enableStore) }}</b>
              </span>
              <span>
                占用
                <b>{{ formatNumber(occupyStore) }}</b>
              </span>
            </div>
          </div>
        </div>
        <div class="stats-card" @click="handleOccupation('transit')">
          <div class="stats-icon">
            <el-icon color="#409EFF" :size="32"><Van /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-header">
              <span class="stats-label">实际在途</span>
              <el-tooltip content="在途中产品总数量" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
            <div class="stats-value" style="cursor: pointer">
              {{ formatNumber(realTransit) }}
            </div>
            <div class="stats-sub">
              <span>
                可用
                <b>{{ formatNumber(enableTransit) }}</b>
              </span>
              <span>
                占用
                <b>{{ formatNumber(occupyTransit) }}</b>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="page-content">
      <bz-table
        ref="tableRef"
        :fixedPagination="true"
        :searchColumns="searchColumns"
        :columns="columns"
        :requestApi="getHistoryList"
        :initParam="initParam"
        :dataCallback="dataCallback"
      >
        <template #createTime="{ row }">
          {{ row.createTime }}
        </template>
        <template #purchaseCode="{ row }">
          {{ row.purchaseCode || '-' }}
        </template>
        <template #orderCodes="{ row }">
          <div v-if="row.orderCodes && row.orderCodes.length > 0">
            <div v-for="(orderCode, index) in row.orderCodes" :key="index">{{ orderCode }}</div>
          </div>
          <span v-else>-</span>
        </template>
        <template #businessType="{ row }">
          {{ row.businessType }}
        </template>
        <template #actualStock="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.actualStock.end }}</div>
            <div :style="{ color: getChangeColor(row.actualStock.change) }">
              变动量：{{ formatChange(row.actualStock.change) }}
            </div>
            <div>期初：{{ row.actualStock.begin }}</div>
          </div>
        </template>
        <template #availableStock="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.availableStock.end }}</div>
            <div :style="{ color: getChangeColor(row.availableStock.change) }">
              变动量：{{ formatChange(row.availableStock.change) }}
            </div>
            <div>期初：{{ row.availableStock.begin }}</div>
          </div>
        </template>
        <template #occupiedStock="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.occupiedStock.end }}</div>
            <div :style="{ color: getChangeColor(row.occupiedStock.change) }">
              变动量：{{ formatChange(row.occupiedStock.change) }}
            </div>
            <div>期初：{{ row.occupiedStock.begin }}</div>
          </div>
        </template>
        <template #inTransit="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.inTransit.end }}</div>
            <div :style="{ color: getChangeColor(row.inTransit.change) }">
              变动量：{{ formatChange(row.inTransit.change) }}
            </div>
            <div>期初：{{ row.inTransit.begin }}</div>
          </div>
        </template>
        <template #availableTransit="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.availableTransit.end }}</div>
            <div :style="{ color: getChangeColor(row.availableTransit.change) }">
              变动量：{{ formatChange(row.availableTransit.change) }}
            </div>
            <div>期初：{{ row.availableTransit.begin }}</div>
          </div>
        </template>
        <template #occupiedTransit="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.occupiedTransit.end }}</div>
            <div :style="{ color: getChangeColor(row.occupiedTransit.change) }">
              变动量：{{ formatChange(row.occupiedTransit.change) }}
            </div>
            <div>期初：{{ row.occupiedTransit.begin }}</div>
          </div>
        </template>
      </bz-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Box, Van, InfoFilled } from '@element-plus/icons-vue'
import { ColumnProps } from '@/interface/table'
import { getStoreHistory, getStoreProduct } from '@/api/admin/warehouse'
import { businessTypeList, getBusinessTypeLabel } from '@/constant/yitang/warehouse'
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import OccupationDetailDialog from './components/occupation-detail-dialog.vue'

const route = useRoute()
const tableRef = ref()

const productCode = ref('')
const specName = ref('')
const location = ref('')
const productImage = ref('')
const realStore = ref(0)
const enableStore = ref(0)
const occupyStore = ref(0)
const realTransit = ref(0)
const enableTransit = ref(0)
const occupyTransit = ref(0)

const specificationId = (route.query.specificationId as string) || ''
const productId = (route.query.productId as string) || ''

const initParam = reactive({
  specificationId
})

const specData = ref<any>(null)

const loadProductInfo = async () => {
  const { code, data, message } = await getStoreProduct({ productId })
  if (code !== 200) return ElMessage.warning(message)
  if (data) {
    productCode.value = data.code || ''
    const stockList = data.stockList || []
    const matchedSpec = stockList.find((item: any) => item.specificationId === specificationId)
    if (matchedSpec) {
      specData.value = matchedSpec
      specName.value = matchedSpec.itemList?.map((i: any) => i.categorySpecificationItemValue).join('-') || ''
      location.value = matchedSpec.locationName || ''
      productImage.value = matchedSpec.imageList?.[0]?.url || ''
      realStore.value = matchedSpec.realStore || 0
      enableStore.value = matchedSpec.enableStore || 0
      occupyStore.value = matchedSpec.occupyStore || 0
      realTransit.value = matchedSpec.realTransit || 0
      enableTransit.value = matchedSpec.enableTransit || 0
      occupyTransit.value = matchedSpec.occupyTransit || 0
    }
  }
}

const handleOccupation = (type: 'stock' | 'transit' = 'stock') => {
  dynamic.show({
    id: 'occupationDetailDialog',
    el: '#app',
    data: {
      rowData: specData.value || {
        specificationId,
        occupyStore: occupyStore.value,
        occupyTransit: occupyTransit.value
      },
      initialTab: type
    },
    render: OccupationDetailDialog
  })
}

onMounted(() => {
  loadProductInfo()
})

const searchColumns = computed(() => [
  {
    label: '订单号',
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
    label: '采购单号',
    prop: 'purchaseCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '请输入',
        clearable: true
      }
    }
  },
  {
    label: '业务类型',
    prop: 'type',
    enum: businessTypeList,
    search: {
      el: 'el-select',
      props: {
        placeholder: '请选择',
        clearable: true
      }
    }
  }
])

const columns: ColumnProps[] = [
  {
    label: '发生时间',
    prop: 'createTime',
    align: 'center'
  },
  {
    label: '采购单',
    prop: 'purchaseCode',
    align: 'center',
    width: 160
  },
  {
    label: '订单号',
    prop: 'orderCodes',
    align: 'center'
  },
  {
    label: '业务类型',
    prop: 'businessType',
    align: 'center'
  },
  {
    label: '实际库存',
    prop: 'actualStock',
    align: 'center'
  },
  {
    label: '可用库存',
    prop: 'availableStock',
    align: 'center'
  },
  {
    label: '占用库存',
    prop: 'occupiedStock',
    align: 'center'
  },
  {
    label: '实际在途',
    prop: 'inTransit',
    align: 'center'
  },
  {
    label: '可用在途',
    prop: 'availableTransit',
    align: 'center'
  },
  {
    label: '占用在途',
    prop: 'occupiedTransit',
    align: 'center'
  }
]

const getHistoryList = (params: any) => {
  return getStoreHistory({
    specificationId,
    ...params
  })
}

const dataCallback = (data: any) => {
  let records = data?.list ?? []
  if (records.length > 0) {
    const firstRecord = records[0]
    realStore.value = (firstRecord.realStoreInit || 0) + (firstRecord.realStoreChange || 0)
    realTransit.value = (firstRecord.realTransitInit || 0) + (firstRecord.realTransitChange || 0)
    if (!specName.value && firstRecord.specificationName) specName.value = firstRecord.specificationName
  }

  records = records.map((item: any) => ({
    ...item,
    createTime: item.createTime || '-',
    purchaseCode: item.purchaseCode || '-',
    orderCodes: item.orderCode ? item.orderCode.split(',').filter((s: string) => s.trim()) : [],
    businessType: getBusinessTypeLabel(item.type),
    actualStock: {
      begin: item.realStoreInit || 0,
      change: item.realStoreChange || 0,
      end: (item.realStoreInit || 0) + (item.realStoreChange || 0)
    },
    availableStock: {
      begin: item.enableStoreInit || 0,
      change: item.enableStoreChange || 0,
      end: (item.enableStoreInit || 0) + (item.enableStoreChange || 0)
    },
    occupiedStock: {
      begin: item.occupyStoreInit || 0,
      change: item.occupyStoreChange || 0,
      end: (item.occupyStoreInit || 0) + (item.occupyStoreChange || 0)
    },
    inTransit: {
      begin: item.realTransitInit || 0,
      change: item.realTransitChange || 0,
      end: (item.realTransitInit || 0) + (item.realTransitChange || 0)
    },
    availableTransit: {
      begin: item.enableTransitInit || 0,
      change: item.enableTransitChange || 0,
      end: (item.enableTransitInit || 0) + (item.enableTransitChange || 0)
    },
    occupiedTransit: {
      begin: item.occupyTransitInit || 0,
      change: item.occupyTransitChange || 0,
      end: (item.occupyTransitInit || 0) + (item.occupyTransitChange || 0)
    }
  }))
  return {
    list: records,
    total: Number(data?.total || 0)
  }
}

const getChangeColor = (change: number) => {
  if (change > 0) return '#67C23A'
  if (change < 0) return '#F56C6C'
  return '#606266'
}

const formatChange = (change: number) => {
  if (change > 0) return `+${change}`
  return change.toString()
}

const formatNumber = (num: number) => {
  return num.toLocaleString()
}
</script>

<style lang="scss" scoped>
.warehouse-history-page {
  height: calc(100vh - 106px);
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .page-header {
    background-color: #fff;
    padding: 16px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 8px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .product-info {
        display: flex;
        align-items: center;

        .info-text {
          .info-row {
            margin-bottom: 4px;
            font-size: 14px;

            &:last-child {
              margin-bottom: 0;
            }

            .label {
              color: #909399;
              margin-right: 8px;
            }

            .value {
              color: #303133;
            }
          }
        }
      }
    }

    .header-right {
      display: flex;
      gap: 16px;

      .stats-card {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 20px;
        background-color: #f5f9ff;
        border-radius: 8px;
        min-width: 180px;
        cursor: pointer;

        .stats-icon {
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stats-content {
          .stats-header {
            display: flex;
            align-items: center;
            gap: 4px;
            margin-bottom: 4px;

            .stats-label {
              font-size: 13px;
              color: #909399;
            }

            .info-icon {
              font-size: 14px;
              color: #c0c4cc;
              cursor: pointer;

              &:hover {
                color: #409eff;
              }
            }
          }

          .stats-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 8px;
          }

          .stats-sub {
            display: flex;
            gap: 16px;
            font-size: 13px;
            color: #909399;

            b {
              color: #303133;
              font-weight: 500;
            }
          }
        }
      }
    }
  }

  .page-content {
    flex: 1;
    padding: 10px;
    overflow: auto;
    background-color: #fff;
    margin-top: 10px;
    border-radius: 8px;

    .stock-info {
      font-size: 13px;
      line-height: 1.6;
      text-align: center;
      padding-left: 10px;

      div {
        white-space: nowrap;
      }
    }
  }
}
</style>
