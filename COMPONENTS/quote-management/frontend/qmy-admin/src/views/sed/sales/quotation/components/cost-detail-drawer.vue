<template>
  <el-drawer v-model="drawerVisible" title="成本明细" size="500px" @close="onDestroy">
    <div v-loading="loading" class="cost-detail-content">
      <div class="section">
        <div class="section-title">配件明细</div>
        <el-table :data="accessoryList" border>
          <el-table-column label="配件名称" prop="name" align="center" />
          <el-table-column label="数量" prop="quantity" align="center" width="80" />
          <el-table-column label="图片" align="center" width="80">
            <template #default="{ row }">
              <el-image
                v-if="row.image"
                :src="row.image"
                v-image-preview="row.image"
                style="width: 30px; height: 30px"
                fit="cover"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="克重" prop="weight" align="center" width="80" />
          <el-table-column label="成本单价" align="center" width="90">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span>总成本：</span>
          <span class="total-value" v-if="accessoryTotal">¥{{ accessoryTotal }}</span>
          <span class="total-value" v-else>待厂长确认</span>
        </div>
      </div>

      <div class="section">
        <div class="section-title">效果明细</div>
        <div class="effect-info">
          <div class="effect-row">
            <span class="label">效果名称：</span>
            <span class="value">{{ effectInfo.name }}</span>
          </div>
          <div class="effect-grid">
            <div class="effect-item">
              <span class="label">人工工序量：</span>
              <span class="value">{{ effectInfo.laborProcess }}</span>
            </div>
            <div class="effect-item">
              <span class="label">人工成本：</span>
              <span class="value">¥{{ effectInfo.laborCost }}</span>
            </div>
          </div>
          <div class="effect-grid">
            <div class="effect-item">
              <span class="label">油漆工序量：</span>
              <span class="value">{{ effectInfo.paintProcess }}</span>
            </div>
            <div class="effect-item">
              <span class="label">油漆成本：</span>
              <span class="value">¥{{ effectInfo.paintCost }}</span>
            </div>
          </div>
        </div>
        <el-table :data="effectList" border style="margin-top: 10px">
          <el-table-column label="工艺名称" prop="craftName" align="center" />
          <el-table-column label="工艺属性" prop="craftAttributeName" align="center" />
          <el-table-column label="颜色" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.color" size="small">{{ row.color }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span>总成本：</span>
          <span class="total-value" v-if="effectTotal">¥{{ effectTotal }}</span>
          <span class="total-value" v-else>待厂长确认</span>
        </div>
      </div>

      <div class="section">
        <div class="section-title">包材明细</div>
        <el-table :data="packageList" border>
          <el-table-column label="包材名称" prop="name" align="center" />
          <el-table-column label="包材尺寸" prop="size" align="center" />
          <el-table-column label="装箱数" prop="quantity" align="center" width="80" />
          <el-table-column label="成本" align="center" width="80">
            <template #default="{ row }">¥{{ row.cost }}</template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span>总成本：</span>
          <span class="total-value" v-if="packageTotal">¥{{ packageTotal }}</span>
          <span class="total-value" v-else>待厂长确认</span>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCostDetail } from '@/api/sed/sales/quotation'

const attrs = useAttrs()
const { rowData, onDestroy } = attrs as any
const drawerVisible = ref(true)
const loading = ref(false)

const accessoryList = ref<any[]>([])
const accessoryTotal = ref(0)

const effectInfo = ref({
  name: '',
  laborProcess: '0',
  laborCost: '0',
  paintProcess: '0',
  paintCost: '0'
})

const effectList = ref<any[]>([])
const effectTotal = ref(0)

const packageList = ref<any[]>([])
const packageTotal = ref(0)

const loadCostDetail = async () => {
  if (!rowData) return
  loading.value = true
  try {
    const { code, data, message } = await getCostDetail({
      quotationId: rowData.quotationId,
      quotationSkuId: rowData.quotationSkuId,
      matchId: rowData.combinationId,
      skuId: rowData.skuId
    })
    if (code !== 200) return ElMessage.warning(message)

    accessoryList.value = (data.fittingDetail || []).map((item: any) => ({
      name: item.fittingName || '',
      quantity: item.fittingNum || 0,
      image: item.pic?.[0] || '',
      weight: item.weight || '-',
      price: item.unitCost || 0
    }))
    accessoryTotal.value = Number(data.fittingCost) || 0

    effectInfo.value = {
      name: data.effectName || '',
      laborProcess: data.manualProcessQuantity || '0',
      laborCost: data.manualProcessCost || '0',
      paintProcess: data.paintingProcessQuantity || '0',
      paintCost: data.paintingProcessCost || '0'
    }
    effectList.value = (data.effectDetail || []).map((item: any) => ({
      craftName: item.craftName || '',
      craftAttributeName: item.craftAttributeName || '',
      color: (item.colorList || []).map((c: any) => c.name).join('、') || ''
    }))
    effectTotal.value = Number(data.effectCost) || 0

    packageList.value = (data.packagingDetail || []).map((item: any) => ({
      name: item.packagingName || '',
      size: item.packagingSize || '-',
      quantity: item.packagingNum || 0,
      cost: item.cost || 0
    }))
    packageTotal.value = Number(data.packagingCost) || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCostDetail()
})
</script>

<style scoped lang="scss">
.cost-detail-content {
  min-height: 200px;
}

.section {
  margin-bottom: 25px;

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    padding-left: 10px;
    border-left: 3px solid #409eff;
    margin-bottom: 15px;
  }

  .total-row {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
    font-size: 14px;

    .total-value {
      color: #f56c6c;
      font-weight: 600;
    }
  }
}

.effect-info {
  background: #fafafa;
  padding: 10px 15px;
  border-radius: 4px;

  .effect-row {
    margin-bottom: 8px;

    .label {
      color: #909399;
      font-size: 13px;
    }

    .value {
      color: #303133;
      font-size: 13px;
    }
  }

  .effect-grid {
    display: flex;
    gap: 30px;
    margin-bottom: 5px;

    .effect-item {
      .label {
        color: #909399;
        font-size: 13px;
      }

      .value {
        color: #303133;
        font-size: 13px;
      }
    }
  }
}
</style>
