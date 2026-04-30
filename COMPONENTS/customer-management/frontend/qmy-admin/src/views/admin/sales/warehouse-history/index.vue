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
              <span class="value">{{ customerStore.productCode || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">规格名称：</span>
              <span class="value">{{ specName }}</span>
            </div>
            <div class="info-row">
              <span class="label">库位：</span>
              <span class="value">{{ customerStore.locationName || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon color="#409EFF" :size="32"><Box /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-label">实际库存</div>
            <div class="stats-value">{{ customerStore.storeNumber ?? '-' }}</div>
          </div>
        </div>
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon color="#409EFF" :size="32"><Van /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-label">实际在途</div>
            <div class="stats-value">{{ customerStore.transitNumber ?? '-' }}</div>
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
        <template #orderSubCodes="{ row }">
          <div v-if="row.orderSubCodes && row.orderSubCodes.length > 0">
            <div v-for="(no, index) in row.orderSubCodes" :key="index">{{ no }}</div>
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
        <template #inTransit="{ row }">
          <div class="stock-info">
            <div>期末：{{ row.inTransit.end }}</div>
            <div :style="{ color: getChangeColor(row.inTransit.change) }">
              变动量：{{ formatChange(row.inTransit.change) }}
            </div>
            <div>期初：{{ row.inTransit.begin }}</div>
          </div>
        </template>
      </bz-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Box, Van } from '@element-plus/icons-vue'
import { ColumnProps } from '@/interface/table'
import { getCustomerStoreRecord } from '@/api/admin/sales/customer'
import { businessTypeList, getBusinessTypeLabel } from '@/constant/yitang/warehouse'

const route = useRoute()

const productImage = ref('')
const customerStoreId = route.query.customerStoreId || ''
const customerStore = ref<any>({})
const specName = computed(() => {
  const itemList = customerStore.value.itemList || []
  return itemList.map((item: any) => item.categorySpecificationItemValue).join(' ')
})

const initParam = reactive({
  customerStoreId
})

const searchColumns = computed(() => [
  {
    label: '子订单号',
    prop: 'orderSubCode',
    search: {
      el: 'el-input',
      props: {
        placeholder: '子订单号',
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
        placeholder: '采购单号',
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
        placeholder: '业务类型',
        clearable: true
      }
    }
  }
])

const columns: ColumnProps[] = [
  {
    label: '发生时间',
    prop: 'createTime',
    align: 'center',
    width: 160
  },
  {
    label: '采购单',
    prop: 'purchaseCode',
    align: 'center',
    width: 160
  },
  {
    label: '子订单号',
    prop: 'orderSubCodes',
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
    label: '实际在途',
    prop: 'inTransit',
    align: 'center'
  }
]

const getHistoryList = (params: any) => {
  return getCustomerStoreRecord({
    customerStoreId: initParam.customerStoreId,
    recordType: params.type || '',
    orderSubCode: params.orderSubCode || '',
    purchaseCode: params.purchaseCode || ''
  })
}

const dataCallback = (data: any) => {
  // 保存 customerStore 数据用于头部显示
  if (data?.customerStore) {
    customerStore.value = data.customerStore
  }
  productImage.value = data.customerStore.imageList?.[0]?.url || ''

  let records = data?.recordList ?? []
  records = records.map((item: any) => ({
    ...item,
    createTime: item.createTime || '-',
    purchaseCode: item.purchaseCode || '-',
    orderSubCodes: item.orderSubCode ? [item.orderSubCode] : [],
    businessType: getBusinessTypeLabel(item.type),
    actualStock: {
      begin: item.realStoreInit || 0,
      change: item.realStoreChange || 0,
      end: (item.realStoreInit || 0) + (item.realStoreChange || 0)
    },
    inTransit: {
      begin: item.realTransitInit || 0,
      change: item.realTransitChange || 0,
      end: (item.realTransitInit || 0) + (item.realTransitChange || 0)
    }
  }))
  return {
    list: records,
    total: Number(data?.total || records.length)
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

        .stats-icon {
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stats-content {
          .stats-label {
            font-size: 13px;
            color: #909399;
            margin-bottom: 4px;
          }

          .stats-value {
            font-size: 22px;
            font-weight: 600;
            color: #303133;
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
      line-height: 1.8;

      div {
        white-space: nowrap;
      }
    }
  }
}
</style>
