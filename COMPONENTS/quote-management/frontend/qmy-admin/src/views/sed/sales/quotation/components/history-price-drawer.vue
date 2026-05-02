<template>
  <el-drawer v-model="drawerVisible" title="历史报价" size="700px" @close="onDestroy">
    <div class="product-info">
      <div class="product-left">
        <el-image
          :src="detailData.pic?.[0]?.url"
          style="width: 80px; height: 80px; cursor: pointer"
          fit="cover"
          @click="handleImagePreview(detailData.pic?.[0]?.url)"
        />
        <div class="product-detail">
          <div class="model-name">{{ detailData.productCode }}</div>
          <div class="tags">
            <el-tag v-for="(effect, idx) in detailData.effectDetail" :key="idx" size="small">
              {{ effect.craftName }}：{{ effect.colorList?.map((c: any) => c.name).join('、') }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="product-right">
        <div class="price-column">
          <div class="price-row">
            <span class="label">本次报价</span>
            <span class="price">{{ formatPrice(detailData.thisPrice) }}</span>
            <!-- <el-tag type="danger" size="small">偏高</el-tag> -->
          </div>
          <div class="price-row">
            <span class="label">平均报价</span>
            <span class="price-normal">{{ formatPrice(detailData.averagePrice) }}</span>
          </div>
          <div class="price-row">
            <span class="label">报价中位数</span>
            <span class="price-normal">{{ formatPrice(detailData.medianPrice) }}</span>
          </div>
        </div>
        <div class="rate-column">
          <div class="rate-row">
            <span class="label">毛利率</span>
            <span class="value">{{ formatRate(detailData.grossProfitRate) }}</span>
          </div>
          <div class="rate-row">
            <span class="label">平均毛利率</span>
            <span class="value">{{ formatRate(detailData.averageGrossProfitRate) }}</span>
          </div>
          <div class="rate-row">
            <span class="label">毛利率中位数</span>
            <span class="value">{{ formatRate(detailData.medianGrossProfitRate) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="chart-section">
      <div class="chart-item">
        <div class="chart-title">所有客户报价趋势</div>
        <div ref="allChartRef" class="chart-container" />
      </div>
      <div class="chart-item">
        <div class="chart-title">本客户报价趋势</div>
        <div ref="currentChartRef" class="chart-container" />
      </div>
    </div>

    <div class="filter-tabs">
      <el-button-group>
        <el-button :type="filterType === 'all' ? 'primary' : ''" @click="filterType = 'all'">所有客户</el-button>
        <el-button :type="filterType === 'current' ? 'primary' : ''" @click="filterType = 'current'">
          只看本客户
        </el-button>
      </el-button-group>
    </div>

    <el-table :data="historyList" border style="width: 100%">
      <el-table-column label="客户名称" prop="customerName" align="center" />
      <el-table-column label="报价金额" align="center">
        <template #default="{ row }">
          <span>{{ formatPrice(row.price, row.currency) }}</span>
          <!-- <el-tag
            :type="row.priceLevel === '适中' ? 'success' : row.priceLevel === '偏低' ? 'warning' : 'danger'"
            size="small"
            style="margin-left: 5px"
          >
            {{ row.priceLevel }}
          </el-tag> -->
        </template>
      </el-table-column>
      <el-table-column label="报价日期" prop="quotationDate" align="center" />
      <el-table-column label="业务员" prop="salerName" align="center" />
    </el-table>
  </el-drawer>

  <!-- 图片预览 -->
  <el-image-viewer
    v-if="showViewer"
    :url-list="viewerImageList"
    :initial-index="0"
    :z-index="3000"
    :teleported="true"
    hide-on-click-modal
    @close="showViewer = false"
  />
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getHistoryQuotationInfo } from '@/api/sed/sales/quotation'
import { getCurrencySymbol } from '@/constant/sed/sales'

const attrs = useAttrs()
const { rowData, onDestroy } = attrs as any
const drawerVisible = ref(true)
const filterType = ref('all')

const allChartRef = ref()
const currentChartRef = ref()

const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const detailData = ref<any>({})

const historyList = computed(() => {
  if (filterType.value === 'all') {
    return detailData.value.allCustomer || []
  }
  return detailData.value.thisCustomer || []
})

const formatPrice = (val: number | undefined, currency?: string) => {
  if (val === undefined || val === null) return '--'
  const symbol = getCurrencySymbol(currency || detailData.value.currency) || '¥'
  return `${symbol}${val}`
}

const formatRate = (rate: number | undefined) => {
  if (rate === undefined || rate === null) return '--'
  return rate.toFixed(2) + '%'
}

const handleImagePreview = (imageUrl: string) => {
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

const loadDetail = async () => {
  const { code, data, message } = await getHistoryQuotationInfo({
    skuId: rowData?.skuId,
    productId: rowData?.productId,
    quotationSkuId: rowData?.quotationSkuId,
    matchId: rowData?.matchId,
    customerId: rowData?.customerId
  })
  if (code !== 200) return ElMessage.warning(message)
  detailData.value = data || {}
  nextTick(() => {
    setTimeout(() => {
      initCharts()
    }, 300)
  })
}

const initCharts = () => {
  const currencySymbol = getCurrencySymbol(detailData.value.currency) || '¥'

  // 所有客户报价趋势图
  if (allChartRef.value) {
    const allChart = echarts.init(allChartRef.value)
    const allTrend = detailData.value.allQuotationTrend || []
    allChart.setOption({
      tooltip: {
        trigger: 'axis',
        formatter: `{b}: ${currencySymbol}{c}`
      },
      grid: { top: 20, right: 10, bottom: 10, left: 10 },
      xAxis: {
        type: 'category',
        data: allTrend.map((item: any) => item.quotationDate),
        show: false,
        boundaryGap: false
      },
      yAxis: { type: 'value', show: false },
      series: [
        {
          type: 'line',
          data: allTrend.map((item: any) => item.averagePrice),
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          showSymbol: true,
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(147, 112, 219, 0.4)' },
                { offset: 1, color: 'rgba(147, 112, 219, 0.1)' }
              ]
            }
          },
          lineStyle: { color: '#9370DB', width: 2 },
          itemStyle: { color: '#9370DB', borderColor: '#fff', borderWidth: 2 }
        }
      ]
    })
  }

  // 本客户报价趋势图
  if (currentChartRef.value) {
    const currentChart = echarts.init(currentChartRef.value)
    const thisTrend = detailData.value.thisQuotationTrend || []
    currentChart.setOption({
      tooltip: {
        trigger: 'axis',
        formatter: `{b}: ${currencySymbol}{c}`
      },
      grid: { top: 10, right: 10, bottom: 10, left: 10 },
      xAxis: {
        type: 'category',
        data: thisTrend.map((item: any) => item.quotationDate),
        show: false
      },
      yAxis: { type: 'value', show: false },
      series: [
        {
          type: 'bar',
          data: thisTrend.map((item: any) => item.averagePrice),
          barWidth: '60%',
          itemStyle: {
            color: '#409EFF',
            borderRadius: [2, 2, 0, 0]
          }
        }
      ]
    })
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.product-info {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 20px;

  .product-left {
    display: flex;
    gap: 15px;

    .product-detail {
      .model-name {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 10px;
      }

      .tags {
        display: flex;
        flex-direction: column;
        gap: 5px;
      }
    }
  }

  .product-right {
    display: flex;
    gap: 40px;

    .price-column {
      .price-row {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        height: 24px;

        .label {
          color: #909399;
          font-size: 13px;
          width: 75px;
        }

        .price {
          color: #f56c6c;
          font-weight: 600;
          font-size: 16px;
          text-align: left;
        }

        .price-normal {
          color: #409eff;
          font-weight: 600;
          width: 100px;
          text-align: left;
        }

        .el-tag {
          margin-left: 10px;
        }
      }
    }

    .rate-column {
      .rate-row {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        height: 24px;

        .label {
          color: #909399;
          font-size: 13px;
          width: 80px;
        }

        .value {
          color: #303133;
          min-width: 50px;
          text-align: left;
        }
      }
    }
  }
}

.chart-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  .chart-item {
    flex: 1;

    .chart-title {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 10px;
    }

    .chart-container {
      height: 120px;
      background: #fafafa;
      border-radius: 4px;
    }
  }
}

.filter-tabs {
  margin-bottom: 15px;
}
</style>
