<template>
  <el-dialog v-model="dialogVisible" title="报价单详情" width="1200px" @close="onDestroy">
    <div class="detail-content">
      <img v-if="detailInfo.status === '-1'" class="reject-icon" src="/images/sed/reject-icon.png" alt="驳回" />
      <div class="top-info">
        <div class="left-info">
          <div class="section-title">报价单信息</div>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">客户名称</span>
              <span class="value">{{ detailInfo.customerName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">业务员</span>
              <span class="value">{{ detailInfo.salesmanName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">收货地址</span>
              <span class="value">{{ detailInfo.receiveAddress || '-' }}</span>
            </div>
          </div>
          <div class="info-item full">
            <span class="label">特殊要求</span>
            <span class="value">{{ detailInfo.specialRequirements || '-' }}</span>
          </div>
          <div v-if="detailInfo.presidentWxAuditFileList.length" class="info-item full wx-audit-item">
            <span class="label">微信审核截图</span>
            <div class="wx-audit-list">
              <el-image
                v-for="(item, index) in detailInfo.presidentWxAuditFileList"
                :key="item.id || item.storageId || index"
                :src="item.url"
                :preview-src-list="presidentWxPreviewList"
                class="wx-audit-image"
                fit="cover"
              />
            </div>
          </div>
        </div>
        <div class="right-info">
          <div class="section-title">物流信息</div>
          <div class="logistics-grid">
            <div class="logistics-item">
              <span class="label">总运输体积</span>
              <span class="value">{{ detailInfo.volume }}m³</span>
            </div>
            <div class="logistics-item">
              <span class="label">物流总成本</span>
              <span class="value">{{ detailInfo.logisticsCost != null ? `¥${detailInfo.logisticsCost}` : '-' }}</span>
            </div>
            <div class="logistics-item">
              <span class="label">物流备注</span>
              <span class="value">{{ detailInfo.logisticsRemark || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="tab-section">
        <el-button-group>
          <el-button :type="activeTab === 'sku' ? 'primary' : ''" @click="activeTab = 'sku'">SKU信息</el-button>
          <el-button :type="activeTab === 'history' ? 'primary' : ''" @click="activeTab = 'history'">
            历史记录
          </el-button>
        </el-button-group>
      </div>

      <div v-show="activeTab === 'sku'" class="sku-section">
        <div class="section-header">SKU信息</div>
        <el-table :data="tableData" border :span-method="spanMethod" :max-height="560">
          <el-table-column label="产品编号" prop="productNo" align="center" width="120" />
          <el-table-column label="搭配名称" prop="combinationName" align="center" width="100" />
          <el-table-column label="SKU名称" prop="skuName" align="center" width="150" />
          <el-table-column label="成本" prop="cost" align="center" width="100">
            <template #default="{ row }">
              <span>
                {{ row.cost && row.cost !== '-' ? `${row.cost}` : '待厂长确认' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="图片" align="center" width="70">
            <template #default="{ row }">
              <el-image
                v-if="row.image"
                :src="row.image"
                v-image-preview="row.image"
                style="width: 40px; height: 40px"
                fit="cover"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="基础报价" align="center" width="100">
            <template #default="{ row }">
              {{ formatBasicPrice(row) }}
            </template>
          </el-table-column>
          <el-table-column label="报价" prop="price" align="center" width="80">
            <template #default="{ row }">
              <span>
                {{ row.price && row.price !== '-' ? `${currencySymbol(detailInfo.currency)}${row.price}` : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" align="center" width="80" />
          <el-table-column label="体积 (m³)" prop="volume" align="center" width="90" />
          <el-table-column label="包材名称" prop="packageName" align="center" width="120" />
          <el-table-column label="包材尺寸" align="center" width="100">
            <template #default="{ row }">{{ row.packageSize || '-' }}</template>
          </el-table-column>
          <el-table-column label="装箱数" prop="packingNumber" align="center" width="80" />
          <el-table-column label="包材成本价" align="center" width="100">
            <template #default="{ row }">
              <span>
                {{ row.packingCost && row.packingCost !== '-' ? `${row.packingCost}` : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" align="center" width="150">
            <template #default="{ row }">{{ row.remark || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="250" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                size="small"
                @click="handleHistoryPrice(row)"
                v-permission="'sal:sed:quotation:getHistoryQuotationInfo'"
              >
                历史报价
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleCostDetail(row)"
                v-permission="'sal:sed:quotation:getCostDetail'"
              >
                成本明细
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                v-if="detailInfo.status === '4' && row.shiftStatus == '0'"
                @click="handleSkuConvertOrder(row)"
              >
                一键转订单
              </el-button>
              <el-button
                type="default"
                link
                size="small"
                disabled
                v-if="detailInfo.status === '4' && row.shiftStatus == '1'"
                @click="handleSkuConvertOrder(row)"
              >
                已转订单
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-show="activeTab === 'history'" class="history-section">
        <div class="section-header">历史记录</div>
        <el-table :data="historyList" border max-height="450">
          <el-table-column label="操作时间" prop="time" align="center" />
          <el-table-column label="操作人" prop="operator" align="center" />
          <el-table-column label="操作内容" prop="content" align="center" />
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <div class="footer-item">
            <span class="footer-label">币种</span>
            <span class="footer-value">{{ currencyText }}</span>
          </div>
          <div v-if="detailInfo.currency === '2'" class="footer-item">
            <span class="footer-label">汇率</span>
            <span class="footer-value">{{ detailInfo.exchangeRate || '-' }}</span>
          </div>
          <div v-if="detailInfo.currency === '1'" class="footer-item">
            <span class="footer-label">是否含税</span>
            <span class="footer-value">{{ taxText }}</span>
          </div>
          <div class="footer-item">
            <span class="footer-label">FOB</span>
            <span class="footer-value">{{ detailInfo.fob || '-' }}</span>
          </div>
          <div class="footer-item">
            <span class="footer-label">EXW</span>
            <span class="footer-value">{{ detailInfo.exw || '-' }}</span>
          </div>
          <div class="footer-item">
            <span class="footer-label">优惠金额</span>
            <span class="footer-value">
              {{ currencySymbol(detailInfo.currency) }}{{ detailInfo.discountAmount || 0 }}
            </span>
          </div>
          <div class="footer-item">
            <span class="footer-label">毛利率</span>
            <span class="footer-value">
              {{ detailInfo.grossProfitMargin != null ? `${detailInfo.grossProfitMargin}%` : '-' }}
            </span>
          </div>
          <div class="footer-item">
            <span class="footer-label">物流占订单比例</span>
            <span class="footer-value">
              {{ detailInfo.logisticsProportion != null ? `${detailInfo.logisticsProportion}%` : '-' }}
            </span>
          </div>
          <div class="footer-item">
            <span class="footer-label">订单金额</span>
            <span class="footer-amount">
              {{ currencySymbol(detailInfo.currency) }}{{ detailInfo.orderAmount || 0 }}
            </span>
          </div>
          <!-- 审核驳回 -->
          <div v-if="detailInfo.status === '-1'" class="footer-item reject-item">
            <span class="footer-label reject-label">驳回原因</span>
            <el-tooltip :content="detailInfo.rejectReason" placement="top">
              <span class="footer-reject-reason">{{ detailInfo.rejectReason || '-' }}</span>
            </el-tooltip>
          </div>
        </div>
        <div class="footer-right">
          <!-- 总裁微信审核-->
          <el-button
            v-if="detailInfo.canPresidentWxApprove"
            type="primary"
            :loading="loading"
            @click="handlePresidentWxApprove"
          >
            总裁微信审核
          </el-button>

          <!-- 审核中 -->
          <template v-if="detailInfo.status === '3'">
            <el-button type="danger" :loading="loading" @click="handleReject" v-permission="'sal:sed:quotation:audit'">
              驳回
            </el-button>
            <el-button
              type="primary"
              :loading="loading"
              @click="handleApprove"
              v-permission="'sal:sed:quotation:audit'"
            >
              通过
            </el-button>
          </template>

          <!-- 总裁未审核，财务未审核；总裁审核通过，财务未审核；总裁未审核，财务审核通过；总裁微信审核通过，财务未审核 -->
          <template
            v-if="
              detailInfo.status === '5' ||
              detailInfo.status === '6' ||
              detailInfo.status === '7' ||
              detailInfo.status === '8'
            "
          >
            <el-button type="danger" :loading="loading" @click="handleReject" v-permission="'sal:sed:quotation:audit'">
              驳回
            </el-button>
            <el-button
              type="primary"
              :loading="loading"
              @click="handleApprove"
              v-permission="'sal:sed:quotation:audit'"
            >
              通过
            </el-button>
          </template>

          <!-- 审核通过 -->
          <template v-else-if="detailInfo.status === '4'">
            <el-button
              type="primary"
              @click="handleConvertOrder"
              v-if="detailInfo.shiftStatus == '0'"
              v-permission="'sal:sed:quotation:oneKeyToOrder'"
            >
              一键转订单
            </el-button>
            <el-button
              type="primary"
              disabled
              v-if="detailInfo.shiftStatus == '1'"
              v-permission="'sal:sed:quotation:oneKeyToOrder'"
            >
              已转订单
            </el-button>
          </template>
        </div>
      </div>
    </template>

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="报价单审核" width="300px">
      <el-form label-width="80px" label-position="top">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirmReject">确定</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { getQuotationDetail, quotationAudit, quotationJointAudit } from '@/api/sed/sales/quotation'
import { getQuotationOperationLabel } from '@/constant/sed/quotation'
import { getCurrencyLabel, getCurrencySymbol, getTaxLabel } from '@/constant/sed/sales'
import { useUserStore } from '@/views/sed/store/modules/user'
import HistoryPriceDrawer from './history-price-drawer.vue'
import CostDetailDrawer from './cost-detail-drawer.vue'
import ConvertOrderDialog from './convert-order-dialog.vue'
import PresidentWxAuditDialog from './president-wx-audit-dialog.vue'

const userStore = useUserStore()
const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const activeTab = ref('sku')
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const loading = ref(false)

// 人民币显示￥，美元显示$
const currencySymbol = val => {
  return getCurrencySymbol(val) || '¥'
}

const getPresidentWxAuditFileList = (data: any) => {
  return data?.presidentWxAuditImageList || []
}

// 报价单信息
const detailInfo = ref({
  customerName: '',
  customerId: '',
  salesmanName: '',
  receiveAddress: '',
  specialRequirements: '',
  volume: 0,
  logisticsCost: null as number | null,
  logisticsRemark: '',
  discountAmount: 0,
  grossProfitMargin: null as number | null,
  logisticsProportion: null as number | null,
  orderAmount: 0,
  rejectReason: '',
  status: '',
  shiftStatus: '0', // 0未转换 1已转换
  currency: '', // 币种 1=人民币 2=美元
  exchangeRate: null as number | null, // 汇率
  tax: '', // 是否含税 1=含税 2=不含税
  fob: '',
  exw: '',
  canFinanceApprove: false,
  canPresidentApprove: false,
  canPresidentWxApprove: false,
  presidentWxAuditFileList: [] as any[]
})

const skuList = ref<any[]>([])
const historyList = ref<any[]>([])

const presidentWxPreviewList = computed(() => detailInfo.value.presidentWxAuditFileList.map((item: any) => item.url))

const currencyText = computed(() => {
  return getCurrencyLabel(detailInfo.value.currency) || '-'
})

const taxText = computed(() => {
  return getTaxLabel(detailInfo.value.tax) || '-'
})

// 展开 SKU 列表用于表格显示
const tableData = computed(() => {
  const result: any[] = []
  skuList.value.forEach((sku, skuIndex) => {
    const packages = sku.packages || []
    if (packages.length === 0) {
      result.push({
        ...sku,
        skuRef: sku,
        skuIndex,
        packageName: '-',
        packageSize: '-',
        packingNumber: '-',
        packingCost: '-',
        cost: '-',
        packageIndex: 0,
        packageCount: 1
      })
    } else {
      packages.forEach((pkg: any, pkgIndex: number) => {
        result.push({
          ...sku,
          skuRef: sku,
          skuIndex,
          packageName: pkg.packageName ?? '-',
          packageSize: pkg.packageSize ?? '-',
          packingNumber: pkg.packingNumber ?? '-',
          packingCost: pkg.packingCost ?? '-',
          cost: pkg.cost || '-',
          packageIndex: pkgIndex,
          packageCount: packages.length
        })
      })
    }
  })
  return result
})

const loadDetail = async () => {
  if (!rowData?.id) return
  const { code, data, message } = await getQuotationDetail({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)

  detailInfo.value = {
    customerName: data.customerName || '',
    customerId: data.customerId || '',
    salesmanName: data.salesmanName || '',
    receiveAddress: data.receiveAddress || '',
    specialRequirements: data.specialRequirements || '',
    volume: data.volume || 0,
    logisticsCost: data.logisticsCost,
    logisticsRemark: data.logisticsRemark || '',
    discountAmount: data.discountAmount || 0,
    grossProfitMargin: data.grossProfitMargin,
    logisticsProportion: data.logisticsProportion,
    orderAmount: data.orderAmount || 0,
    rejectReason: data.rejectReason || '',
    status: data.status || rowData?.status || '',
    shiftStatus: data.shiftStatus || '0',
    currency: data.currency || '',
    exchangeRate: data.exchangeRate,
    tax: data.tax || '',
    fob: data.fob || '',
    exw: data.exw || '',
    canFinanceApprove: !!data.canFinanceApprove,
    canPresidentApprove: !!data.canPresidentApprove,
    canPresidentWxApprove: !!data.canPresidentWxApprove,
    presidentWxAuditFileList: getPresidentWxAuditFileList(data)
  }

  // 使用 skuMap 合并相同 quotationSkuId 的数据
  const skuMap = new Map<string, any>()
  ;(data.skuList || []).forEach((item: any) => {
    const key = item.quotationSkuId
    if (skuMap.has(key)) {
      const existSku = skuMap.get(key)
      if (item.packageName) {
        existSku.packages.push({
          packageName: item.packageName ?? '',
          packageSize: item.packageSize ?? '-',
          packingNumber: item.packingNumber || 0,
          packingCost: item.packingCost || 0,
          cost: item.cost ?? '-'
        })
      }
    } else {
      let packageSize = item.packageSize ?? ''
      let packingSize = item.packingSize ?? ''
      if ((packageSize != '' && packingSize != '') || (packageSize == '' && packingSize != '')) {
        packageSize = packingSize
      }
      skuMap.set(key, {
        quotationSkuId: item.quotationSkuId,
        productNo: item.modelName ?? '',
        combinationName: item.combinationName ?? '',
        skuName: item.skuName ?? '',
        cost: item.cost ?? 0,
        image: item.pic?.[0] ?? '',
        price: item.price ?? 0,
        basicPrice: item.basicPrice ?? 0,
        quantity: item.number ?? 0,
        volume: item.volume ?? 0,
        remark: item.remark ?? '',
        skuId: item.skuId,
        productId: item.productId,
        combinationId: item.combinationId,
        shiftStatus: item.shiftStatus || '0',
        packages: item.packageName
          ? [
              {
                packageName: item.packageName ?? '',
                packageSize: packageSize ?? '-',
                packingNumber: item.packingNumber ?? 0,
                packingCost: item.packingCost || 0,
                cost: item.cost ?? '-'
              }
            ]
          : []
      })
    }
  })
  skuList.value = Array.from(skuMap.values())

  historyList.value = (data.historyList || []).map((item: any) => ({
    time: item.operateTime || '',
    operator: item.operatePerson || '',
    content: getQuotationOperationLabel(item.operation)
  }))
}

onMounted(() => {
  loadDetail()
})

// 合并单元格
const spanMethod = ({ row, rowIndex, columnIndex }: { row: any; rowIndex: number; columnIndex: number }) => {
  const data = tableData.value
  // 包材相关列不合并 (包材名称、包材尺寸、装箱数)
  if (columnIndex >= 8 && columnIndex <= 10) {
    return { rowspan: 1, colspan: 1 }
  }

  // 产品编号列
  if (columnIndex === 0) {
    const currentProduct = row.productNo
    if (rowIndex === 0 || data[rowIndex - 1]?.productNo !== currentProduct) {
      let rowspan = 1
      for (let i = rowIndex + 1; i < data.length; i++) {
        if (data[i].productNo === currentProduct) {
          rowspan++
        } else {
          break
        }
      }
      return { rowspan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }

  // 搭配名称列
  if (columnIndex === 1) {
    const currentProduct = row.productNo
    const currentComb = row.combinationName
    if (
      rowIndex === 0 ||
      data[rowIndex - 1]?.productNo !== currentProduct ||
      data[rowIndex - 1]?.combinationName !== currentComb
    ) {
      let rowspan = 1
      for (let i = rowIndex + 1; i < data.length; i++) {
        if (data[i].productNo === currentProduct && data[i].combinationName === currentComb) {
          rowspan++
        } else {
          break
        }
      }
      return { rowspan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }

  // 其他列按 SKU 合并
  if (row.packageIndex === 0) {
    return { rowspan: row.packageCount, colspan: 1 }
  }
  return { rowspan: 0, colspan: 0 }
}

const formatBasicPrice = (sku: any): string => {
  if (!sku.basicPrice) return '-'
  return detailInfo.value.currency == '1' ? `¥${sku.basicPrice}` : `$${sku.basicPrice}`
}

const handleHistoryPrice = (row: any) => {
  const params = {
    id: 'historyPriceDrawer',
    el: '#app',
    data: {
      rowData: {
        skuId: row.skuId,
        productId: row.productId,
        quotationSkuId: row.quotationSkuId,
        matchId: row.combinationId,
        image: row.image,
        modelName: row.productNo,
        customerId: detailInfo.value.customerId
      }
    },
    render: HistoryPriceDrawer
  }
  dynamic.show(params)
}

const handleCostDetail = (row: any) => {
  const params = {
    id: 'costDetailDrawer',
    el: '#app',
    data: {
      rowData: { ...row, quotationId: rowData.id }
    },
    render: CostDetailDrawer
  }
  dynamic.show(params)
}

const handleReject = () => {
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const handleConfirmReject = async () => {
  if (!rejectReason.value.trim()) {
    return ElMessage.warning('请输入驳回原因')
  }
  loading.value = true
  try {
    const { code, message } = await quotationAudit({
      id: rowData.id,
      salesmanId: userStore.userId,
      auditResult: '-1',
      rejectReason: rejectReason.value
    })
    if (code !== 200) return ElMessage.warning(message)

    ElMessage.success('操作成功')
    rejectDialogVisible.value = false
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    loading.value = false
  }
}

const handleApprove = async () => {
  await ElMessageBox.confirm('确认通过？', '报价单审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  loading.value = true
  try {
    const statusActionMap: Record<string, string> = {
      '3': 'FINANCE_PASS',
      '5': 'FINANCE_PASS',
      '6': 'FINANCE_PASS',
      '7': 'PRESIDENT_PASS',
      '8': 'FINANCE_PASS'
    }
    let action = ''
    if (detailInfo.value.canFinanceApprove) action = 'FINANCE_PASS'
    else if (detailInfo.value.canPresidentApprove) action = 'PRESIDENT_PASS'
    else action = statusActionMap[detailInfo.value.status] || ''
    const { code, message } = await quotationJointAudit({
      id: rowData.id,
      action
    })

    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    loading.value = false
  }
}

const handlePresidentWxApprove = () => {
  const params = {
    id: 'presidentWxAuditDialogFromDetail',
    el: '#app',
    data: {
      rowData: {
        ...rowData,
        customerName: detailInfo.value.customerName,
        quotationCode: rowData?.quotationCode
      },
      callback: async () => {
        await loadDetail()
        callback?.()
      }
    },
    render: PresidentWxAuditDialog
  }
  dynamic.show(params)
}

const handleConvertOrder = () => {
  const params = {
    id: 'convertOrderDialog',
    el: '#app',
    data: {
      rowData: rowData,
      callback: () => {
        dialogVisible.value = false
        if (callback) callback()
      }
    },
    render: ConvertOrderDialog
  }
  dynamic.show(params)
}

const handleSkuConvertOrder = (row: any) => {
  const params = {
    id: 'convertOrderDialogFromSku',
    el: '#app',
    data: {
      rowData: rowData,
      skuData: row,
      fromSku: true,
      callback: () => {
        dialogVisible.value = false
        if (callback) callback()
      }
    },
    render: ConvertOrderDialog
  }
  dynamic.show(params)
}
</script>

<style scoped lang="scss">
.detail-content {
  position: relative;

  .reject-icon {
    position: absolute;
    top: -19px;
    right: 0;
    width: 68px;
    height: 53px;
    z-index: 1;
  }

  .top-info {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;

    .left-info {
      flex: 1;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 4px;

      .info-grid {
        display: flex;
        gap: 30px;
        margin-bottom: 10px;
      }

      .info-item {
        .label {
          color: #909399;
          font-size: 14px;
          margin-right: 10px;
        }

        .value {
          color: #303133;
          font-size: 14px;
        }

        &.full {
          margin-top: 10px;
        }

        &.wx-audit-item {
          display: flex;
          align-items: flex-start;

          .wx-audit-list {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: -4px;
          }

          .wx-audit-image {
            width: 60px;
            height: 60px;
            border-radius: 4px;
            border: 1px solid #dcdfe6;
          }
        }
      }
    }

    .right-info {
      width: 280px;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 4px;

      .logistics-grid {
        .logistics-item {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 8px;

          .label {
            color: #909399;
            font-size: 14px;
            width: 70px;
          }

          .value {
            color: #303133;
            font-size: 14px;
          }
        }
      }
    }
  }

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    padding-left: 10px;
    border-left: 3px solid #409eff;
    margin-bottom: 15px;
  }

  .tab-section {
    margin-bottom: 15px;
  }

  .section-header {
    font-size: 14px;
    color: #606266;
    margin-bottom: 10px;
  }

  .sku-section,
  .history-section {
    margin-bottom: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  .footer-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .footer-item {
      display: flex;
      align-items: center;
      gap: 5px;
    }

    .footer-label {
      color: #606266;
      font-size: 14px;
    }

    .footer-value {
      color: #303133;
      font-size: 14px;
    }

    .footer-amount {
      color: #f56c6c;
      font-size: 16px;
      font-weight: 600;
    }

    .footer-reject-reason {
      color: #f56c6c;
      font-size: 14px;
      max-width: 300px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      display: inline-block;
      vertical-align: middle;
    }
  }

  .footer-right {
    display: flex;
  }
}
</style>
