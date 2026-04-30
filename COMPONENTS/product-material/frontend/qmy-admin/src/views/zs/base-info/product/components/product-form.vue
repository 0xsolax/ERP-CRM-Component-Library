<template>
  <div class="form-page">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑产品' : '新增产品' }}</h2>
    </div>
    <div class="form-content">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" v-loading="loading">
        <div class="section-title">基本信息</div>
        <div class="basic-outer-layout">
          <div class="basic-main-area">
            <div class="two-col-layout">
              <div class="basic-left-col">
                <el-form-item label="产品编号" prop="productCode">
                  <el-input v-model="formData.productCode" placeholder="请输入产品编号" />
                </el-form-item>
                <el-form-item label="产品类型" prop="productTypeIdList">
                  <ZsLabelSelect v-model="formData.productTypeIdList" nodeKey="PRODUCT_TYPE" :multiple="true" />
                </el-form-item>
              </div>
              <div class="basic-middle-col">
                <div class="weight-row">
                  <el-form-item label="毛重" label-width="40px" class="inline-form-item">
                    <div class="weight-input-wrap">
                      <el-input v-model="formData.grossWeight" placeholder="毛重" />
                      <span class="unit-text">g</span>
                    </div>
                  </el-form-item>
                  <el-form-item label="净重" label-width="40px" class="inline-form-item">
                    <div class="weight-input-wrap">
                      <el-input v-model="formData.netWeight" placeholder="净重" />
                      <span class="unit-text">g</span>
                    </div>
                  </el-form-item>
                </div>
                <el-form-item label-width="0">
                  <div class="calc-info-row">
                    <span class="auto-calc-text">体积：{{ formData.volume ? formData.volume + ' m³' : '-' }}</span>
                    <span class="auto-calc-text">
                      小柜：{{ formData.smallCabinet ? formData.smallCabinet + ' 支' : '-' }}
                    </span>
                    <span class="auto-calc-text">
                      高柜：{{ formData.largeCabinet ? formData.largeCabinet + ' 支' : '-' }}
                    </span>
                  </div>
                </el-form-item>
              </div>
              <div class="basic-cost-col">
                <el-form-item label="损耗/杂费" label-width="80px" class="form-item--mb18">
                  <el-input v-model="formData.lossFee" class="loss-input" placeholder="损耗/杂费" />
                </el-form-item>
                <div class="cost-row">
                  <el-form-item label="总成本" class="inline-form-item" label-width="80px">
                    <span class="auto-calc-text total-cost-text">¥ {{ totalCostComputed }}</span>
                  </el-form-item>
                  <el-form-item label="售价" class="inline-form-item" label-width="40px">
                    <el-input v-model="formData.sellingPrice" placeholder="请输入售价" />
                  </el-form-item>
                </div>
              </div>
            </div>
            <el-form-item label="产品图片">
              <div class="image-list">
                <div v-for="(img, idx) in visibleImageList" :key="idx" class="image-item">
                  <img :src="img.url" class="image-preview" />
                  <div class="image-overlay">
                    <el-icon class="overlay-icon" @click.stop="handleImagePreview(img.url)">
                      <ZoomIn />
                    </el-icon>
                    <el-icon class="overlay-icon" @click.stop="handleRemoveImage(idx)">
                      <Delete />
                    </el-icon>
                  </div>
                </div>
                <bz-upload
                  v-if="visibleImageList.length < 10"
                  class="img-uploader"
                  module-path="zs/product"
                  :accept="uploadData.accept"
                  :before-upload="beforeUpload"
                  :on-success="uploadSuccess"
                  :on-error="uploadError"
                >
                  <el-icon class="img-uploader-icon"><Plus /></el-icon>
                </bz-upload>
              </div>
            </el-form-item>
          </div>
          <div class="basic-desc-area">
            <div class="desc-header">
              <span class="desc-header-title">货品描述</span>
              <div class="desc-header-actions">
                <el-button :icon="Refresh" text title="刷新" :loading="descRefreshing" @click="refreshDescription" />
                <el-switch
                  v-model="descLang"
                  active-value="en"
                  inactive-value="zh"
                  inactive-text="中文"
                  active-text="英文"
                />
              </div>
            </div>
            <el-input
              v-model="currentDescription"
              type="textarea"
              :rows="6"
              :placeholder="descLang === 'zh' ? '请输入中文货品描述' : 'Enter English description'"
            />
          </div>
        </div>

        <div class="section-title section-title--compact">材料 / 工价</div>
        <div class="two-col-layout bottom-layout">
          <!-- 材料选择 -->
          <div class="bottom-col left-col">
            <div class="section-label section-label--gap">
              伞架选择
              <el-tag
                v-if="selectedUmbrellaFrame"
                type="success"
                class="ellipsis-tag"
                :title="`单价：¥${umbrellaUnitPrice} / 支`"
              >
                ¥{{ umbrellaUnitPrice }} / 支
              </el-tag>
            </div>
            <div class="umbrella-select-row">
              <el-cascader
                :key="cascaderKey"
                ref="umbrellaCascaderRef"
                v-model="umbrellaCascaderValue"
                :options="umbrellaCascaderOptions"
                :props="{ expandTrigger: 'hover', emitPath: true }"
                placeholder="类型 / 尺寸 / 功能 / 材料 / 特定属性"
                clearable
                filterable
                class="flex-1"
                @change="onUmbrellaCascaderChange"
                @visible-change="onCascaderVisibleChange"
              />
              <el-button
                :icon="Refresh"
                link
                :loading="umbrellaLoading"
                @click="reloadUmbrellaOptions"
                title="刷新伞架数据"
              />
            </div>
            <div class="section-label section-label--between">
              <span>其他材料</span>
              <el-button
                :icon="Refresh"
                text
                :loading="materialLoading"
                @click="reloadMaterialOptions"
                title="刷新材料数据"
              />
            </div>
            <div class="table-wrap">
              <el-table
                :data="visibleMaterialRows"
                border
                show-summary
                :summary-method="getMaterialSummary"
                row-class-name="tall-row"
              >
                <el-table-column label="材料分类" min-width="130" align="center">
                  <template #default="{ row }">
                    <template v-if="row._bound">
                      <el-tag type="warning">{{ row.materialType || '-' }}</el-tag>
                    </template>
                    <el-select
                      v-else
                      v-model="row.materialType"
                      placeholder="请选择"
                      filterable
                      @change="onMaterialTypeChange(row)"
                    >
                      <el-option v-for="opt in getMaterialTypeOptions(row)" :key="opt" :label="opt" :value="opt" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="材料名称" min-width="130" align="center">
                  <template #default="{ row }">
                    <template v-if="row._bound">
                      <el-tag type="warning">{{ row.materialName || '-' }}</el-tag>
                    </template>
                    <el-select
                      v-else
                      v-model="row.materialName"
                      placeholder="请选择"
                      filterable
                      :disabled="!row.materialType"
                      @change="onMaterialNameChange(row)"
                    >
                      <el-option
                        v-for="opt in getMaterialNameOptions(row.materialType)"
                        :key="opt.id"
                        :label="opt.name"
                        :value="opt.name"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="尺寸" min-width="100" align="center">
                  <template #default="{ row }">
                    <span>{{ row.materialSize || '-' }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量" min-width="90" align="center">
                  <template #default="{ row }">
                    <span v-if="row._bound">{{ row.materialQty || '-' }}</span>
                    <el-input
                      v-else
                      v-model="row.materialQty"
                      placeholder="请输入"
                      @input="row._priceRefresh = Date.now()"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="价格(元)" min-width="100" align="center">
                  <template #default="{ row }">
                    <span>{{ calcMaterialPrice(row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="70" fixed="right" align="center">
                  <template #default="{ row, $index }">
                    <el-button v-if="!row._bound" type="danger" link @click="handleDeleteMaterial(row, $index)">
                      删除
                    </el-button>
                    <el-tag v-else type="warning" class="bound-tag">绑定</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-button type="primary" class="add-row-btn" @click="addMaterialRow">添加材料</el-button>
          </div>

          <!-- 面料 + 包材 -->
          <div class="bottom-col middle-col">
            <div class="section-label section-label--between section-label--fixed-height">
              <span>面料选择</span>
              <el-button
                :icon="Refresh"
                link
                :loading="fabricLoading"
                @click="reloadFabricOptions"
                title="刷新面料数据"
              />
            </div>
            <div class="table-wrap table-wrap--flex2">
              <el-table
                :data="visibleFabricRows"
                border
                show-summary
                :summary-method="getFabricSummary"
                row-class-name="tall-row"
              >
                <el-table-column label="种类" min-width="110" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.fabricName"
                      placeholder="请选择"
                      filterable
                      @change="onFabricNameChange(row)"
                    >
                      <el-option v-for="opt in fabricNameOptions" :key="opt" :label="opt" :value="opt" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="型号" min-width="110" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.fabricCode"
                      placeholder="请选择"
                      filterable
                      @change="onFabricCodeChange(row)"
                    >
                      <el-option
                        v-for="opt in getFabricCodeOptions(row.fabricName)"
                        :key="opt.id"
                        :label="opt.modelName"
                        :value="opt.modelName"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="门幅(cm)" min-width="90" align="center">
                  <template #default="{ row }">
                    <span>{{ row.fabricWidth || '-' }}</span>
                  </template>
                </el-table-column>
                <el-table-column min-width="90" align="center">
                  <template #header>
                    <span>用量(米)</span>
                    <span v-if="hasShumaYin" class="shrink-badge" title="乘以收缩系数">×收缩系数</span>
                  </template>
                  <template #default="{ row }">
                    <el-icon v-if="fabricUsageLoading" class="is-loading loading-icon"><Loading /></el-icon>
                    <template v-else-if="row.params">
                      <span>{{ computedFabricUsage(row) }}</span>
                    </template>
                    <el-tooltip v-else content="用量数据缺失，请在面料用量表中补充对应尺寸的数据" placement="top">
                      <span class="missing-text">-</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="价格(元)" min-width="90" align="center">
                  <template #default="{ row }">
                    <el-icon v-if="fabricUsageLoading" class="is-loading loading-icon"><Loading /></el-icon>
                    <template v-else-if="calcFabricPrice(row) !== '-'">
                      <span>{{ calcFabricPrice(row) }}</span>
                    </template>
                    <el-tooltip v-else content="单价或用量缺失，价格无法计算" placement="top">
                      <span class="missing-text">-</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="70" fixed="right" align="center">
                  <template #default="{ row, $index }">
                    <el-button type="danger" link @click="handleDeleteFabric(row, $index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-button type="primary" class="add-row-btn" @click="addFabricRow">添加面料</el-button>
            <div class="section-label section-label--between section-label--mt">
              <span>默认包材</span>
              <el-button
                :icon="Refresh"
                text
                :loading="packagingLoading"
                @click="reloadPackagingOptions"
                title="刷新包材数据"
              />
            </div>
            <div class="table-wrap table-wrap--flex3">
              <el-table
                :data="visiblePackagingRows"
                border
                show-summary
                :summary-method="getPackagingSummary"
                row-class-name="tall-row"
              >
                <el-table-column label="包材类型" min-width="120" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.packagingType"
                      placeholder="请选择"
                      filterable
                      @change="onPackagingTypeChange(row)"
                    >
                      <el-option v-for="opt in packagingTypeOptions" :key="opt" :label="opt" :value="opt" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="包材名称" min-width="130" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.packagingName"
                      placeholder="请选择"
                      filterable
                      :disabled="!row.packagingType"
                      @change="onPackagingNameChange(row)"
                    >
                      <el-option
                        v-for="opt in getPackagingNameOptions(row.packagingType)"
                        :key="opt.id"
                        :label="opt.name"
                        :value="opt.name"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="尺寸(cm)" min-width="180" align="center">
                  <template #default="{ row }">
                    <template v-if="row.packagingType === '纸箱'">
                      <div class="box-size-row">
                        <el-input
                          v-model="row.boxLength"
                          placeholder="长"
                          class="box-size-input"
                          @input="row.packagingCost = calcBoxPrice(row)"
                        />
                        <span class="box-size-separator">×</span>
                        <el-input
                          v-model="row.boxWidth"
                          placeholder="宽"
                          class="box-size-input"
                          @input="row.packagingCost = calcBoxPrice(row)"
                        />
                        <span class="box-size-separator">×</span>
                        <el-input
                          v-model="row.boxHeight"
                          placeholder="高"
                          class="box-size-input"
                          @input="row.packagingCost = calcBoxPrice(row)"
                        />
                      </div>
                    </template>
                    <template v-else>
                      <el-select
                        v-model="row.packagingSpec"
                        placeholder="请选择"
                        filterable
                        :disabled="!row.packagingName"
                      >
                        <el-option
                          v-for="opt in getPackagingSpecOptions(row.packagingType, row.packagingName)"
                          :key="opt"
                          :label="opt"
                          :value="opt"
                        />
                      </el-select>
                    </template>
                  </template>
                </el-table-column>
                <el-table-column label="装箱数" min-width="100" align="center">
                  <template #default="{ row }">
                    <el-input
                      v-model="row.packagingQty"
                      placeholder="请输入"
                      @input="row.packagingType === '纸箱' && (row.packagingCost = calcBoxPrice(row))"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="单价(元)" min-width="110" align="center">
                  <template #default="{ row }">
                    <span>{{ getDisplayPackagingCost(row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="70" fixed="right" align="center">
                  <template #default="{ row, $index }">
                    <el-button type="danger" link @click="handleDeletePackaging(row, $index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-button type="primary" class="add-row-btn" @click="addPackagingRow">添加包材</el-button>
          </div>

          <!-- 印刷 + 工价 -->
          <div class="bottom-col right-col">
            <div class="section-label">印刷方式</div>
            <div class="table-wrap table-wrap--print">
              <el-table
                :data="visiblePrintRows"
                border
                row-class-name="tall-row"
                show-summary
                :summary-method="getPrintSummary"
              >
                <el-table-column label="面料类型" min-width="130" align="center">
                  <template #default="{ row }">
                    <span>{{ row.fabricLabel || '-' }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="印刷方式" min-width="120" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.printType"
                      placeholder="请选择"
                      filterable
                      @change="val => handlePrintTypeChange(row, val)"
                    >
                      <el-option v-for="opt in printTypeOptions" :key="opt.id" :label="opt.label" :value="opt.label" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="对齐方式" min-width="120" align="center">
                  <template #default="{ row }">
                    <el-select
                      v-model="row.alignment"
                      placeholder="请选择"
                      filterable
                      @change="val => handleAlignmentChange(row, val)"
                    >
                      <el-option
                        v-for="opt in printAlignmentOptions"
                        :key="opt.id"
                        :label="opt.label"
                        :value="opt.label"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="价格(元)" min-width="110" align="center">
                  <template #default="{ row }">
                    <el-input v-model="row.price" placeholder="请输入" />
                  </template>
                </el-table-column>
                <el-table-column label="版费(元)" min-width="110" align="center">
                  <template #default="{ row }">
                    <el-input v-model="row.plateFee" placeholder="请输入" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="section-label section-label--mt">工价</div>
            <div class="table-wrap table-wrap--flex2">
              <el-table
                :data="wagePairedRows"
                border
                show-summary
                :summary-method="getWageSummary"
                row-class-name="tall-row"
              >
                <el-table-column label="工序" min-width="90" prop="label1" align="center" />
                <el-table-column label="金额(元)" min-width="100">
                  <template #default="{ row }">
                    <el-input
                      v-if="row.field1"
                      v-model="wageAmounts[row.field1]"
                      placeholder="请输入"
                      @input="wageAmounts[row.field1] = validateDecimal(wageAmounts[row.field1])"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="工序" min-width="90" prop="label2" align="center" />
                <el-table-column label="金额(元)" min-width="100" align="center">
                  <template #default="{ row }">
                    <el-input
                      v-if="row.field2"
                      v-model="wageAmounts[row.field2]"
                      placeholder="请输入"
                      @input="wageAmounts[row.field2] = validateDecimal(wageAmounts[row.field2])"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </el-form>
    </div>
    <footer-actions>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
    </footer-actions>
    <el-image-viewer
      v-if="previewVisible"
      :url-list="[previewImageUrl]"
      :initial-index="0"
      hide-on-click-modal
      @close="previewVisible = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, onActivated, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsStore } from '@/views/zs/store/modules/tags'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete, Loading, Refresh } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import FooterActions from '@/components/footer-actions/index.vue'
import ZsLabelSelect from '@/components/zs-label-select/index.vue'
import { saveOrUpdateProduct, getProductDetail } from '@/api/zs/product'
import { translateText } from '@/api/zs/common'
import { listByNodeKey } from '@/api/zs/base-info/base-data'
import { getUmbrellaFrameList } from '@/api/zs/material/umbrella-frame'
import { getFabricPage } from '@/api/zs/material/fabric'
import { getMaterialCategoryList, getMaterialPage } from '@/api/zs/material/material'
import { getPackagingPage } from '@/api/zs/material/packaging'
import { getProcessPage } from '@/api/zs/base-info/process'
import { validateDecimal } from '@/utils/validate'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()

const id = computed(() => route.query.id as string)
const isEdit = computed(() => !!id.value)

const loading = ref(false)
const saveLoading = ref(false)
const uploadData = reactive({
  accept: imageFileType.join()
})
const descLang = ref('zh')
const descRefreshing = ref(false)
const formRef = ref()
const imageList = ref<any[]>([])
const visibleImageList = computed(() => imageList.value.filter((img: any) => !img.isDeleted))
const previewVisible = ref(false)
const previewImageUrl = ref('')

const formData = reactive<any>({
  id: null,
  productCode: '',
  productTypeIdList: [],
  grossWeight: null,
  netWeight: null,
  volume: null,
  sellingPrice: '',
  smallCabinet: null,
  largeCabinet: null,
  descriptionZh: '',
  descriptionEn: '',
  totalCost: null,
  lossFee: 0.45,
  umbrellaFrameId: null
})

const rules = {
  productCode: [{ required: true, message: '请输入产品编号', trigger: 'blur' }],
  productTypeIdList: [{ required: true, message: '请选择产品类型', trigger: 'change' }]
}

const currentDescription = computed({
  get: () => (descLang.value === 'zh' ? formData.descriptionZh : formData.descriptionEn || ''),
  set: (val: string) => {
    if (descLang.value === 'zh') formData.descriptionZh = val
    else formData.descriptionEn = val
  }
})

const umbrellaAllRecords = ref<any[]>([])
const umbrellaLoading = ref(false)
const umbrellaCascaderValue = ref<string[]>([])
const umbrellaCascaderRef = ref()
const selectedUmbrellaFrame = ref<any>(null)
const umbrellaDetailId = ref<any>(null)

const formatUmbrellaSize = (r: any) => {
  const parts: string[] = []
  if (r.lengthName) parts.push(r.lengthName)
  if (r.diameterName) parts.push(r.diameterName)
  if (r.ribCountName) parts.push(r.ribCountName)
  return parts.join(' × ') || '-'
}

const umbrellaUnitPrice = computed(() => {
  const frame = selectedUmbrellaFrame.value
  if (!frame?.price) return '-'
  const cost = parseFloat(frame.price)
  if (isNaN(cost)) return '-'
  const unit = frame.unit || '支'
  const price = unit === '打' ? cost / 12 : cost
  return price.toFixed(4).replace(/\.?0+$/, '')
})

const umbrellaCascaderOptions = computed(() => {
  const tree: any = {}
  for (const r of umbrellaAllRecords.value) {
    const type = r.typeName || '未知'
    const size = formatUmbrellaSize(r)
    const fn = r.functionName || '未知'
    const mat = r.materialName || '未知'
    const attr = r.specificAttribute || '-'
    if (!tree[type]) tree[type] = {}
    if (!tree[type][size]) tree[type][size] = {}
    if (!tree[type][size][fn]) tree[type][size][fn] = {}
    if (!tree[type][size][fn][mat]) tree[type][size][fn][mat] = new Set()
    tree[type][size][fn][mat].add(attr)
  }
  return Object.entries(tree).map(([type, sizes]: any) => ({
    value: type,
    label: type,
    children: Object.entries(sizes).map(([size, fns]: any) => ({
      value: size,
      label: size,
      children: Object.entries(fns).map(([fn, mats]: any) => ({
        value: fn,
        label: fn,
        children: Object.entries(mats).map(([mat, attrs]: any) => ({
          value: mat,
          label: mat,
          children: [...attrs].map((attr: any) => ({ value: attr, label: attr }))
        }))
      }))
    }))
  }))
})

const cascaderKey = ref(0)
const onCascaderVisibleChange = (visible: boolean) => {
  if (!visible && umbrellaCascaderValue.value.length) {
    cascaderKey.value++
  }
}

const onUmbrellaCascaderChange = (val: any) => {
  materialRows.value = materialRows.value.filter((r: any) => !r._bound)
  if (!val || val.length < 5) {
    selectedUmbrellaFrame.value = null
    formData.umbrellaFrameId = null
    return
  }
  const [type, size, fn, mat, attr] = val
  const record = umbrellaAllRecords.value.find(
    (r: any) =>
      (r.typeName || '未知') === type &&
      formatUmbrellaSize(r) === size &&
      (r.functionName || '未知') === fn &&
      (r.materialName || '未知') === mat &&
      (r.specificAttribute || '-') === attr
  )
  if (record) {
    selectedUmbrellaFrame.value = record
    formData.umbrellaFrameId = record.id
    fabricRows.value.forEach((row: any) => {
      row.params = lookupFabricUsage(record.lengthName, record.ribCountName)
    })
    if (record.materials?.length) {
      const bound = record.materials.map((m: any) => ({
        materialType: m.materialCategoryName || '',
        materialName: m.materialName || '',
        materialSize: m.size || '',
        materialQty: String(m.quantity || ''),
        _materialUnitCost: m.price != null ? String(m.price) : '',
        _bound: true
      }))
      materialRows.value = [...bound, ...materialRows.value]
    }
  }
}

const reloadUmbrellaOptions = async () => {
  umbrellaLoading.value = true
  try {
    const { code, data } = await getUmbrellaFrameList()
    if (code === 200) umbrellaAllRecords.value = data || []
    ElMessage.success('伞架数据已刷新')
  } finally {
    umbrellaLoading.value = false
  }
}

const fabricRows = ref<any[]>([])
const fabricAllRecords = ref<any[]>([])
const fabricUsageAllRecords = ref<any[]>([])
const fabricUsageLoading = ref(false)
const fabricLoading = ref(false)

const fabricNameOptions = computed(() => {
  const names = fabricAllRecords.value.map((r: any) => r.typeName).filter(Boolean)
  return [...new Set(names)]
})

const getFabricCodeOptions = (name: string) => {
  return fabricAllRecords.value.filter((r: any) => r.typeName === name)
}

const onFabricNameChange = (row: any) => {
  row.fabricCode = ''
  row.fabricWidth = ''
  row.fabricCost = null
  row.fabricUnit = ''
  row.params = ''
}

const onFabricCodeChange = (row: any) => {
  const record = fabricAllRecords.value.find(
    (r: any) => r.typeName === row.fabricName && r.modelName === row.fabricCode
  )
  row.fabricId = record?.id || null
  row.fabricWidth = record ? record.widthName || '' : ''
  row.fabricCost = record ? record.price ?? null : null
  row.fabricUnit = record ? record.unit || '' : ''
  row.params = lookupFabricUsage(selectedUmbrellaFrame.value?.lengthName, selectedUmbrellaFrame.value?.ribCountName)
}

const lookupFabricUsage = (sizeCm: any, sizeK: any) => {
  const match = fabricUsageAllRecords.value.find((u: any) => u.sizeCm === sizeCm && u.sizeK === sizeK)
  return match ? match.usage : ''
}

const lookupFabricShrink = (sizeCm: any, sizeK: any) => {
  const match = fabricUsageAllRecords.value.find((u: any) => u.sizeCm === sizeCm && u.sizeK === sizeK)
  return match ? parseFloat(match.shrink) : null
}

const hasShumaYin = computed(() => printRows.value.some((r: any) => !r.isDeleted && r.printType === '数码印刷'))

const computedFabricUsage = (row: any) => {
  const base = parseFloat(row.params)
  if (isNaN(base) || !row.params) return row.params
  const idx = fabricRows.value.indexOf(row)
  const printRow = idx >= 0 ? printRows.value[idx] : null
  if (printRow && printRow.printType === '数码印刷') {
    const shrink = lookupFabricShrink(
      selectedUmbrellaFrame.value?.lengthName,
      selectedUmbrellaFrame.value?.ribCountName
    )
    if (shrink != null && !isNaN(shrink)) {
      return (base * shrink).toFixed(3)
    }
  }
  return row.params
}
const calcFabricPrice = (row: any) => {
  const effectiveUsage = computedFabricUsage(row)
  const usage = parseFloat(effectiveUsage)
  const cost = parseFloat(row.fabricCost)
  if (!effectiveUsage || isNaN(usage) || isNaN(cost)) return '-'
  const qty = row.fabricUnit === '码' ? usage * 0.9144 : usage
  return (qty * cost).toFixed(2)
}

const visibleFabricRows = computed(() => fabricRows.value.filter((r: any) => !r.isDeleted))

const handleDeleteFabric = (row: any, idx: number) => {
  const actual = fabricRows.value.indexOf(visibleFabricRows.value[idx])
  if (fabricRows.value[actual]?._detailId) {
    fabricRows.value[actual].isDeleted = 1
    if (printRows.value[actual]) printRows.value[actual].isDeleted = 1
  } else {
    if (printRows.value[actual]?._detailId) {
      printRows.value[actual].isDeleted = 1
    } else {
      printRows.value.splice(actual, 1)
    }
    fabricRows.value.splice(actual, 1)
  }
}

const visiblePrintRows = computed(() => printRows.value.filter((r: any) => !r.isDeleted))

const handlePrintTypeChange = (row: any, val: string) => {
  row.printTypeId = printTypeOptions.value.find((o: any) => o.label === val)?.id || null
}

const handleAlignmentChange = (row: any, val: string) => {
  row.alignmentTypeId = printAlignmentOptions.value.find((o: any) => o.label === val)?.id || null
}

const addFabricRow = () => {
  fabricRows.value.push({
    fabricName: '',
    fabricCode: '',
    fabricWidth: '',
    params: lookupFabricUsage(selectedUmbrellaFrame.value?.lengthName, selectedUmbrellaFrame.value?.ribCountName)
  })
}

const reloadFabricOptions = async () => {
  fabricLoading.value = true
  try {
    const { code, data } = await getFabricPage({ pageNum: 1, pageSize: 1000 })
    if (code === 200) fabricAllRecords.value = data?.list || []
    ElMessage.success('面料数据已刷新')
  } finally {
    fabricLoading.value = false
  }
}

const materialRows = ref<any[]>([])
const materialCategoryOptions = ref<string[]>([])
const materialCategoryMap = ref<Record<string, number>>({})
const materialCache = ref<Record<number, any[]>>({})
const materialLoading = ref(false)

const fetchMaterialsByCategory = async (categoryName: string) => {
  const categoryId = materialCategoryMap.value[categoryName]
  if (!categoryId) return
  if (materialCache.value[categoryId]) return
  const { code, data } = await getMaterialPage({ categoryId, pageNum: 1, pageSize: 1000 })
  if (code === 200) {
    materialCache.value[categoryId] = data?.list || []
  }
}

const getMaterialTypeOptions = (row: any) => {
  const usedByOthers = new Set(
    materialRows.value.filter((r: any) => r !== row && !r.isDeleted && r.materialType).map((r: any) => r.materialType)
  )
  return materialCategoryOptions.value.filter((t: string) => !usedByOthers.has(t) || t === row.materialType)
}

const getMaterialNameOptions = (category: string) => {
  if (!category) return []
  const categoryId = materialCategoryMap.value[category]
  if (!categoryId) return []
  return materialCache.value[categoryId] || []
}

const onMaterialTypeChange = async (row: any) => {
  row.materialName = ''
  row.materialId = null
  row.materialSize = ''
  row._materialUnitCost = ''
  if (row.materialType) {
    await fetchMaterialsByCategory(row.materialType)
  }
}

const onMaterialNameChange = (row: any) => {
  const categoryId = materialCategoryMap.value[row.materialType]
  const materials = categoryId ? materialCache.value[categoryId] || [] : []
  const record = materials.find((r: any) => r.name === row.materialName)
  if (record) {
    row.materialId = record.id
    row.materialSize = record.size || ''
    row._materialUnitCost = record.price ?? ''
  }
}

const calcMaterialPrice = (row: any) => {
  const cost = parseFloat(row._materialUnitCost)
  const qty = parseFloat(row.materialQty)
  if (isNaN(cost) || isNaN(qty)) return '-'
  return (cost * qty).toFixed(2)
}

const visibleMaterialRows = computed(() => materialRows.value.filter((r: any) => !r.isDeleted))

const handleDeleteMaterial = (row: any, idx: number) => {
  const actual = materialRows.value.indexOf(visibleMaterialRows.value[idx])
  if (materialRows.value[actual]?._detailId) {
    materialRows.value[actual].isDeleted = 1
  } else {
    materialRows.value.splice(actual, 1)
  }
}

const addMaterialRow = () => {
  materialRows.value.push({
    materialType: '',
    materialName: '',
    materialSize: '',
    materialQty: '1',
    _materialUnitCost: ''
  })
}

const reloadMaterialOptions = async () => {
  materialLoading.value = true
  try {
    const { code, data } = await getMaterialCategoryList()
    if (code === 200) {
      const cats = data || []
      materialCategoryOptions.value = cats.map((c: any) => c.name)
      materialCategoryMap.value = Object.fromEntries(cats.map((c: any) => [c.name, c.id]))
      materialCache.value = {}
    }
    ElMessage.success('材料数据已刷新')
  } finally {
    materialLoading.value = false
  }
}

const packagingRows = ref<any[]>([])
const packagingOptions = ref<any[]>([])
const packagingLoading = ref(false)

const packagingTypeOptions = computed(() => {
  const types = packagingOptions.value.map((r: any) => r.typeName).filter(Boolean)
  return [...new Set(types)]
})

const getPackagingNameOptions = (type: string) => {
  if (!type) return []
  return packagingOptions.value.filter((r: any) => r.typeName === type)
}

const getPackagingSpecOptions = (type: string, name: string) => {
  if (!type || !name || type === '纸箱') return []
  const specs = packagingOptions.value
    .filter((r: any) => r.typeName === type && r.name === name && r.size)
    .map((r: any) => r.size)
  return [...new Set(specs)]
}

const onPackagingTypeChange = (row: any) => {
  row.packagingName = ''
  row.packagingSpec = ''
  row.packagingCost = ''
  row.boxLength = ''
  row.boxWidth = ''
  row.boxHeight = ''
}

const onPackagingNameChange = (row: any) => {
  const record = packagingOptions.value.find(
    (r: any) => r.typeName === row.packagingType && r.name === row.packagingName
  )
  row.packagingId = record?.id || null
  if (row.packagingType === '纸箱') {
    row._boxUnitCost = record ? record.price ?? '' : ''
    row.packagingCost = ''
  } else {
    row.packagingSpec = record ? record.size || '' : ''
    row._basePackagingCost = record ? record.price ?? '' : ''
  }
}

const calcBoxPrice = (row: any) => {
  const l = parseFloat(row.boxLength)
  const w = parseFloat(row.boxWidth)
  const h = parseFloat(row.boxHeight)
  const c = parseFloat(row._boxUnitCost)
  const qty = parseFloat(row.packagingQty) || 1
  if (isNaN(l) || isNaN(w) || isNaN(h) || isNaN(c)) return ''
  return (((l + w + 8) * (w + h + 4) * c) / 10000 / qty).toFixed(2)
}

const getDisplayPackagingCost = (row: any) => {
  if (row.packagingType === '纸箱') return row.packagingCost || '-'
  const base = parseFloat(row._basePackagingCost)
  const qty = parseFloat(row.packagingQty) || 1
  if (isNaN(base)) return '-'
  return (base / qty).toFixed(2)
}

const visiblePackagingRows = computed(() => packagingRows.value.filter((r: any) => !r.isDeleted))

const handleDeletePackaging = (row: any, idx: number) => {
  const actual = packagingRows.value.indexOf(visiblePackagingRows.value[idx])
  if (packagingRows.value[actual]?._detailId) {
    packagingRows.value[actual].isDeleted = 1
  } else {
    packagingRows.value.splice(actual, 1)
  }
}

const addPackagingRow = () => {
  packagingRows.value.push({
    packagingName: '',
    packagingType: '',
    packagingSpec: '',
    packagingQty: '1',
    packagingCost: '',
    boxLength: '',
    boxWidth: '',
    boxHeight: '',
    _boxUnitCost: ''
  })
}

const reloadPackagingOptions = async () => {
  packagingLoading.value = true
  try {
    const { code, data } = await getPackagingPage({ pageNum: 1, pageSize: 1000 })
    if (code === 200) packagingOptions.value = data?.list || []
    ElMessage.success('包材数据已刷新')
  } finally {
    packagingLoading.value = false
  }
}

const printRows = ref<any[]>([])
const printTypeOptions = ref<any[]>([])
const printAlignmentOptions = ref<any[]>([])
const wageProcesses = ref<any[]>([])
const wageAmounts = reactive<any>({})
const wageDetailIds = reactive<Record<string, any>>({})

const wageRows = computed(() => wageProcesses.value.map((p: any) => ({ label: p.name, field: String(p.id) })))

const wagePairedRows = computed(() => {
  const rows: any[] = []
  const list = wageRows.value
  for (let i = 0; i < list.length; i += 2) {
    rows.push({
      label1: list[i]?.label || '',
      field1: list[i]?.field || '',
      label2: list[i + 1]?.label || '',
      field2: list[i + 1]?.field || ''
    })
  }
  return rows
})

const totalCostComputed = computed(() => {
  const umbrellaCost = parseFloat(umbrellaUnitPrice.value) || 0
  const fabricTotal = fabricRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(calcFabricPrice(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  const materialTotal = materialRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(calcMaterialPrice(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  const packagingTotal = packagingRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(getDisplayPackagingCost(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  const wageTotal = wageRows.value.reduce((sum: number, row: any) => {
    const v = parseFloat(wageAmounts[row.field])
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  const printTotal = printRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const price = parseFloat(row.price) || 0
    const plateFee = parseFloat(row.plateFee) || 0
    return sum + price + plateFee
  }, 0)
  const misc = parseFloat(formData.lossFee) || 0
  return (umbrellaCost + fabricTotal + materialTotal + packagingTotal + wageTotal + printTotal + misc).toFixed(2)
})

const getMaterialSummary = ({ columns }: any) => {
  const total = materialRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(calcMaterialPrice(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  return columns.map((_: any, i: number) => {
    if (i === 0) return '总计'
    if (i === columns.length - 2) return total.toFixed(2)
    return ''
  })
}

const getFabricSummary = ({ columns }: any) => {
  const total = fabricRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(calcFabricPrice(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  return columns.map((_: any, i: number) => {
    if (i === 0) return '总计'
    if (i === columns.length - 2) return total.toFixed(2)
    return ''
  })
}

const getPackagingSummary = ({ columns }: any) => {
  const total = packagingRows.value.reduce((sum: number, row: any) => {
    if (row.isDeleted) return sum
    const v = parseFloat(getDisplayPackagingCost(row))
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  return columns.map((_: any, i: number) => {
    if (i === 0) return '总计'
    if (i === columns.length - 2) return total.toFixed(2)
    return ''
  })
}

const getPrintSummary = ({ columns }: any) => {
  const priceTotal = visiblePrintRows.value.reduce((sum: number, row: any) => {
    const v = parseFloat(row.price)
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  const plateFeeTotal = visiblePrintRows.value.reduce((sum: number, row: any) => {
    const v = parseFloat(row.plateFee)
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  return columns.map((_: any, i: number) => {
    if (i === 0) return '总计'
    if (i === columns.length - 2) return priceTotal.toFixed(2)
    if (i === columns.length - 1) return plateFeeTotal.toFixed(2)
    return ''
  })
}

const getWageSummary = ({ columns }: any) => {
  const total = wageRows.value.reduce((sum: number, row: any) => {
    const v = parseFloat(wageAmounts[row.field])
    return sum + (isNaN(v) ? 0 : v)
  }, 0)
  return columns.map((_: any, i: number) => {
    if (i === 0) return '总计'
    if (i === columns.length - 1) return total.toFixed(2)
    return ''
  })
}

watch(
  packagingRows,
  rows => {
    let maxVol = 0
    let maxRow: any = null
    rows.forEach((row: any) => {
      if (row.packagingType === '纸箱') {
        const l = parseFloat(row.boxLength)
        const w = parseFloat(row.boxWidth)
        const h = parseFloat(row.boxHeight)
        if (!isNaN(l) && !isNaN(w) && !isNaN(h)) {
          const vol = l * w * h
          if (vol > maxVol) {
            maxVol = vol
            maxRow = row
          }
        }
      }
    })
    if (maxVol > 0 && maxRow) {
      const boxVolM3 = maxVol / 1000000
      const qty = parseFloat(maxRow.packagingQty) || 1
      formData.volume = boxVolM3.toFixed(6)
      formData.smallCabinet = Math.floor((28 / boxVolM3) * qty)
      formData.largeCabinet = Math.floor((68 / boxVolM3) * qty)
    } else {
      formData.volume = ''
      formData.smallCabinet = ''
      formData.largeCabinet = ''
    }
  },
  { deep: true }
)

watch(fabricUsageAllRecords, () => {
  if (!selectedUmbrellaFrame.value) return
  fabricRows.value.forEach((row: any) => {
    if (row.fabricName && row.fabricCode) {
      row.params = lookupFabricUsage(selectedUmbrellaFrame.value.lengthName, selectedUmbrellaFrame.value.ribCountName)
    }
  })
})

watch(selectedUmbrellaFrame, frame => {
  if (!frame) return
  fabricRows.value.forEach((row: any) => {
    if (row.fabricName && row.fabricCode) {
      const usage = lookupFabricUsage(frame.lengthName, frame.ribCountName)
      if (usage) row.params = usage
    }
  })
})

watch(
  fabricRows,
  newRows => {
    const len = newRows.length
    while (printRows.value.length < len) {
      const i = printRows.value.length
      const f = newRows[i]
      printRows.value.push({
        fabricLabel: f ? [f.fabricName, f.fabricCode].filter(Boolean).join('-') : '',
        printType: '',
        alignment: '',
        price: '0',
        plateFee: '0'
      })
    }
    if (printRows.value.length > len) {
      printRows.value.splice(len)
    }
    newRows.forEach((f: any, i: number) => {
      if (printRows.value[i]) {
        printRows.value[i].fabricLabel = [f.fabricName, f.fabricCode].filter(Boolean).join('-') || ''
      }
      if (f.fabricName && f.fabricCode && selectedUmbrellaFrame.value) {
        f.params = lookupFabricUsage(selectedUmbrellaFrame.value.lengthName, selectedUmbrellaFrame.value.ribCountName)
      }
    })
  },
  { deep: true }
)

const beforeUpload = (file: any) => {
  const size = file.size / 1024 / 1024
  if (!imageFileType.includes(file.fileType.toLowerCase())) {
    ElMessage.warning('请上传' + imageFileType.join() + '格式的图片')
    return false
  } else if (size > 10) {
    ElMessage.warning('请上传10M以内的文件')
    return false
  }
  return true
}

const uploadSuccess = (res: any) => {
  imageList.value.push({ storageId: res.data.id, url: res.data.url })
}

const uploadError = () => {
  ElMessage.error('上传失败')
}

const handleImagePreview = (url: string) => {
  previewImageUrl.value = url
  previewVisible.value = true
}

const handleRemoveImage = (idx: number) => {
  const actual = imageList.value.indexOf(visibleImageList.value[idx])
  if (imageList.value[actual]?.id) {
    imageList.value[actual].isDeleted = 1
  } else {
    imageList.value.splice(actual, 1)
  }
}

const refreshDescription = async () => {
  const frame = selectedUmbrellaFrame.value
  const size = frame ? formatUmbrellaSize(frame) : ''
  const material = frame ? frame.materialName || '' : ''
  const frameFunction = frame ? frame.functionName || '' : ''
  const frameType = frame ? frame.typeName || '' : ''
  const productName = [frameFunction, frameType].filter(Boolean).join('')
  const fabric =
    fabricRows.value.length > 0
      ? fabricRows.value
          .map((r: any) => [r.fabricName, r.fabricCode].filter(Boolean).join('-'))
          .filter(Boolean)
          .join('、')
      : ''
  const matLines = materialRows.value.filter((r: any) => r.materialType || r.materialName)
  const zhLines = [
    `品名：${productName}`,
    `尺寸：${size}`,
    `伞架：${material}`,
    `面料：${fabric}`,
    ...matLines.map((r: any) => `${r.materialType || ''}：${r.materialName || ''}`)
  ]
  formData.descriptionZh = zhLines.join('\n')
  descRefreshing.value = true
  try {
    const { code, data } = await translateText({
      sourceText: formData.descriptionZh
    })
    if (code === 200 && data) formData.descriptionEn = data.translatedText
  } finally {
    descRefreshing.value = false
  }
}

const loadAllOptions = async () => {
  const [mcRes, fabricRes, pkRes, usageRes, processRes, ptRes, alRes] = await Promise.allSettled([
    getMaterialCategoryList(),
    getFabricPage({ pageNum: 1, pageSize: 1000 }),
    getPackagingPage({ pageNum: 1, pageSize: 1000 }),
    listByNodeKey({ nodeKey: 'FIELD_MGMT_FABRIC_USAGE' }),
    getProcessPage({ pageNum: 1, pageSize: 1000 }),
    listByNodeKey({ nodeKey: 'FIELD_MGMT_PRINTING_METHOD' }),
    listByNodeKey({ nodeKey: 'FIELD_MGMT_ALIGNMENT_METHOD' })
  ])
  if (mcRes.status === 'fulfilled' && mcRes.value.code === 200) {
    const cats = mcRes.value.data || []
    materialCategoryOptions.value = cats.map((c: any) => c.name)
    materialCategoryMap.value = Object.fromEntries(cats.map((c: any) => [c.name, c.id]))
  }
  if (fabricRes.status === 'fulfilled' && fabricRes.value.code === 200)
    fabricAllRecords.value = fabricRes.value.data?.list || []
  if (pkRes.status === 'fulfilled' && pkRes.value.code === 200) packagingOptions.value = pkRes.value.data?.list || []
  fabricUsageLoading.value = true
  if (usageRes.status === 'fulfilled' && usageRes.value.code === 200)
    fabricUsageAllRecords.value = (usageRes.value.data || []).map((d: any) => ({
      sizeCm: d.value1 || '',
      sizeK: d.value2 || '',
      usage: d.value3 || '',
      shrink: d.value4 || ''
    }))
  fabricUsageLoading.value = false
  if (processRes.status === 'fulfilled' && processRes.value.code === 200)
    wageProcesses.value = processRes.value.data?.list || []
  if (ptRes.status === 'fulfilled' && ptRes.value.code === 200)
    printTypeOptions.value = (ptRes.value.data || []).map((d: any) => ({ id: d.id, label: d.value1 }))
  if (alRes.status === 'fulfilled' && alRes.value.code === 200)
    printAlignmentOptions.value = (alRes.value.data || []).map((d: any) => ({ id: d.id, label: d.value1 }))
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    productCode: '',
    productTypeIdList: [],
    grossWeight: null,
    netWeight: null,
    volume: null,
    sellingPrice: '',
    smallCabinet: null,
    largeCabinet: null,
    descriptionZh: '',
    descriptionEn: '',
    totalCost: null,
    lossFee: 0.45,
    umbrellaFrameId: null
  })
  imageList.value = []
  materialRows.value = []
  fabricRows.value = []
  printRows.value = []
  packagingRows.value = []
  umbrellaCascaderValue.value = []
  selectedUmbrellaFrame.value = null
  umbrellaDetailId.value = null
  Object.keys(wageAmounts).forEach(k => delete wageAmounts[k])
  Object.keys(wageDetailIds).forEach(k => delete wageDetailIds[k])
}

const loadProductData = async () => {
  if (!isEdit.value) return
  resetForm()
  try {
    const { code, data } = await getProductDetail({ id: id.value })
    if (code !== 200 || !data) return
    const p = data.product || {}
    formData.id = p.id
    formData.productCode = p.productCode || ''
    formData.productTypeIdList = (p.productTypes || []).map((t: any) => t.typeId)
    formData.grossWeight = p.grossWeight
    formData.netWeight = p.netWeight
    formData.lossFee = p.lossFee ?? 0
    formData.descriptionZh = p.descriptionZh || ''
    formData.descriptionEn = p.descriptionEn || ''
    formData.volume = p.volume
    formData.smallCabinet = p.smallCabinet ?? ''
    formData.largeCabinet = p.largeCabinet ?? ''
    formData.totalCost = p.totalCost ?? 0
    formData.sellingPrice = p.sellingPrice != null ? String(p.sellingPrice) : ''

    // 图片
    if (Array.isArray(p.images)) {
      imageList.value = p.images
        .filter((img: any) => img.isDeleted !== 1)
        .map((img: any) => ({ id: img.id, url: img.url }))
    }

    // 伞架
    const uf = data.umbrellaFrame
    if (uf?.umbrellaFrameId) {
      umbrellaDetailId.value = uf.id || null
      formData.umbrellaFrameId = uf.umbrellaFrameId
      const frame = umbrellaAllRecords.value.find((r: any) => r.id === uf.umbrellaFrameId) || null
      if (frame) {
        selectedUmbrellaFrame.value = frame
        umbrellaCascaderValue.value = [
          frame.typeName || '未知',
          formatUmbrellaSize(frame),
          frame.functionName || '未知',
          frame.materialName || '未知',
          frame.specificAttribute || '-'
        ]
        if (frame.materials?.length) {
          const bound = frame.materials.map((m: any) => ({
            materialType: m.materialCategoryName || '',
            materialName: m.materialName || '',
            materialSize: m.size || '',
            materialQty: String(m.quantity || ''),
            _materialUnitCost: m.price != null ? String(m.price) : '',
            _bound: true
          }))
          materialRows.value = [...bound]
        }
      } else {
        selectedUmbrellaFrame.value = uf
        umbrellaAllRecords.value.push({
          id: uf.umbrellaFrameId,
          typeName: uf.typeName,
          lengthName: uf.lengthName,
          diameterName: uf.diameterName,
          ribCountName: uf.ribCountName,
          functionName: uf.functionName,
          materialName: uf.materialName,
          specificAttribute: uf.specificAttribute,
          price: uf.price,
          unit: uf.unit,
          quantity: uf.quantity
        })
        umbrellaCascaderValue.value = [
          uf.typeName || '',
          formatUmbrellaSize(uf),
          uf.functionName || '',
          uf.materialName || '',
          uf.specificAttribute || '-'
        ]
      }
    }

    // 其他材料
    if (data.materials?.length) {
      const nonBound = data.materials
        .filter((m: any) => !m.isBound)
        .map((m: any) => ({
          _detailId: m.id,
          materialId: m.materialId,
          materialType: m.categoryName || '',
          materialName: m.name || '',
          materialSize: m.size || '',
          materialQty: String(m.quantity || ''),
          _materialUnitCost: m.price != null ? String(m.price) : ''
        }))
      materialRows.value = [...materialRows.value, ...nonBound]
      const categoryNames = [...new Set<string>(nonBound.map((m: any) => m.materialType).filter(Boolean))]
      await Promise.all(categoryNames.map((name: string) => fetchMaterialsByCategory(name)))
    }

    // 面料
    if (data.fabrics?.length) {
      fabricRows.value = data.fabrics.map((f: any) => {
        return {
          _detailId: f.id,
          fabricId: f.fabricId,
          fabricName: f.typeName || '',
          fabricCode: f.modelName || '',
          fabricWidth: f.widthName || '',
          fabricCost: f.price != null ? String(f.price) : null,
          fabricUnit: f.unit || '',
          params: f.usage || ''
        }
      })
    }

    // 包材
    if (data.packagingList?.length) {
      packagingRows.value = data.packagingList.map((pk: any) => {
        const unitPrice = pk.price ?? ''
        return {
          _detailId: pk.id,
          packagingId: pk.packagingId,
          packagingType: pk.typeName || '',
          packagingName: pk.name || '',
          packagingSpec: pk.size || '',
          packagingQty: String(pk.boxCount || 1),
          packagingCost: unitPrice != null ? String(unitPrice) : '',
          boxLength: pk.typeName === '纸箱' && pk.size ? pk.size.split('*')[0] || '' : '',
          boxWidth: pk.typeName === '纸箱' && pk.size ? pk.size.split('*')[1] || '' : '',
          boxHeight: pk.typeName === '纸箱' && pk.size ? pk.size.split('*')[2] || '' : '',
          _boxUnitCost: '',
          _basePackagingCost: unitPrice != null ? String(unitPrice) : ''
        }
      })
    }

    // 印刷
    if (data.printingList?.length) {
      printRows.value = data.printingList.map((pr: any) => ({
        _detailId: pr.id,
        fabricLabel: pr.fabricTypeName || '',
        printType: pr.printTypeName || '',
        alignment: pr.alignmentTypeName || '',
        price: pr.price != null ? String(pr.price) : '',
        plateFee: pr.plateFee != null ? String(pr.plateFee) : '',
        printTypeId: pr.printTypeId,
        alignmentTypeId: pr.alignmentTypeId
      }))
    }

    // 工价
    if (data.processPriceList?.length) {
      const orderedProcesses: any[] = []
      const orderedIds = new Set<string>()
      data.processPriceList.forEach((pp: any) => {
        if (pp.processId) {
          const val = pp.price ?? ''
          wageAmounts[String(pp.processId)] = val != null ? String(val) : ''
          if (pp.id) wageDetailIds[String(pp.processId)] = pp.id
          const existing = wageProcesses.value.find((p: any) => String(p.id) === String(pp.processId))
          orderedProcesses.push(existing || { id: pp.processId, name: pp.name || '' })
          orderedIds.add(String(pp.processId))
        }
      })
      wageProcesses.value.forEach((p: any) => {
        if (!orderedIds.has(String(p.id))) orderedProcesses.push(p)
      })
      wageProcesses.value = orderedProcesses
    }
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  await formRef.value.validate()
  if (!formData.umbrellaFrameId) {
    ElMessage.warning('请选择伞架')
    return
  }
  if (fabricRows.value.length > 0) {
    const incomplete = fabricRows.value.some((row: any) => !row.params || calcFabricPrice(row) === '-')
    if (incomplete) {
      ElMessage.warning('面料数据不全（用量或单价缺失），请补充后再保存')
      return
    }
  }
  if (visiblePrintRows.value.length > 0) {
    if (visiblePrintRows.value.some((row: any) => !row.printType)) {
      ElMessage.warning('请选择印刷方式')
      return
    }
    if (visiblePrintRows.value.some((row: any) => !row.alignment)) {
      ElMessage.warning('请选择对齐方式')
      return
    }
    if (visiblePrintRows.value.some((row: any) => row.price === '' || row.price == null)) {
      ElMessage.warning('请填写印刷价格')
      return
    }
    if (visiblePrintRows.value.some((row: any) => row.plateFee === '' || row.plateFee == null)) {
      ElMessage.warning('请填写版费')
      return
    }
  }
  saveLoading.value = true
  try {
    const grossWeight =
      formData.grossWeight !== '' && formData.grossWeight != null ? parseFloat(formData.grossWeight) : null
    const netWeight = formData.netWeight !== '' && formData.netWeight != null ? parseFloat(formData.netWeight) : null
    const sellingPrice =
      formData.sellingPrice !== '' && formData.sellingPrice != null ? parseFloat(formData.sellingPrice) : null
    const lossFee = formData.lossFee !== '' && formData.lossFee != null ? parseFloat(formData.lossFee) : null

    const dto: any = {
      id: formData.id || undefined,
      productCode: formData.productCode,
      productTypeIdList: formData.productTypeIdList?.length ? formData.productTypeIdList : [],
      grossWeight,
      netWeight,
      lossFee,
      descriptionZh: formData.descriptionZh,
      descriptionEn: formData.descriptionEn,
      volume: formData.volume ? parseFloat(formData.volume) : null,
      smallCabinet: formData.smallCabinet ? parseInt(formData.smallCabinet) : null,
      largeCabinet: formData.largeCabinet ? parseInt(formData.largeCabinet) : null,
      totalCost: parseFloat(totalCostComputed.value) || null,
      sellingPrice
    }

    // 伞架
    if (formData.umbrellaFrameId && selectedUmbrellaFrame.value) {
      const frame = selectedUmbrellaFrame.value
      dto.umbrellaFrame = {
        id: umbrellaDetailId.value || undefined,
        umbrellaFrameId: formData.umbrellaFrameId,
        functionId: frame.functionId || null,
        functionName: frame.functionName || '',
        typeId: frame.typeId || null,
        typeName: frame.typeName || '',
        lengthId: frame.lengthId || null,
        lengthName: frame.lengthName || '',
        diameterId: frame.diameterId || null,
        diameterName: frame.diameterName || '',
        ribCountId: frame.ribCountId || null,
        ribCountName: frame.ribCountName || '',
        materialId: frame.materialId || null,
        materialName: frame.materialName || '',
        specificAttribute: frame.specificAttribute || '',
        price: parseFloat(frame.price) || 0,
        unit: frame.unit || '',
        quantity: 1
      }
    }

    // 其他材料
    dto.materials = [
      ...materialRows.value
        .filter((r: any) => !r._bound && !r.isDeleted && r.materialName)
        .map((r: any) => {
          const qty = parseInt(r.materialQty) || 1
          const price = parseFloat(r._materialUnitCost) || 0
          return {
            id: r._detailId || undefined,
            materialId: r.materialId || null,
            categoryId: materialCategoryMap.value[r.materialType] || null,
            categoryName: r.materialType || '',
            name: r.materialName || '',
            size: r.materialSize || '',
            quantity: qty,
            price: price,
            isBound: 0
          }
        }),
      ...materialRows.value
        .filter((r: any) => r._detailId && r.isDeleted === 1)
        .map((r: any) => ({ id: r._detailId, isDeleted: 1 }))
    ]

    // 面料
    dto.fabrics = [
      ...fabricRows.value
        .filter((r: any) => !r.isDeleted && r.fabricName && r.fabricCode)
        .map((r: any) => {
          const rec = fabricAllRecords.value.find((f: any) => f.id === r.fabricId)
          const usage = parseFloat(computedFabricUsage(r)) || 0
          const price = parseFloat(r.fabricCost) || 0
          return {
            id: r._detailId || undefined,
            fabricId: r.fabricId || null,
            typeId: rec?.typeId || null,
            typeName: r.fabricName || '',
            modelId: rec?.modelId || null,
            modelName: r.fabricCode || '',
            widthId: rec?.widthId || null,
            widthName: r.fabricWidth ? String(r.fabricWidth) : '',
            price: price,
            unit: rec?.unit || '',
            usage: usage
          }
        }),
      ...fabricRows.value
        .filter((r: any) => r._detailId && r.isDeleted === 1)
        .map((r: any) => ({ id: r._detailId, isDeleted: 1 }))
    ]

    // 包材
    dto.packagingList = [
      ...packagingRows.value
        .filter((r: any) => !r.isDeleted && r.packagingName)
        .map((r: any) => {
          const rec = packagingOptions.value.find((p: any) => p.id === r.packagingId)
          const unitPrice = parseFloat(r.packagingType === '纸箱' ? r.packagingCost : getDisplayPackagingCost(r)) || 0
          const qty = parseInt(r.packagingQty) || 1
          return {
            id: r._detailId || undefined,
            packagingId: r.packagingId || null,
            typeId: rec?.typeId || null,
            typeName: r.packagingType || '',
            name: r.packagingName || '',
            size:
              r.packagingType === '纸箱' && r.boxLength && r.boxWidth && r.boxHeight
                ? `${r.boxLength}*${r.boxWidth}*${r.boxHeight}`
                : r.packagingSpec || '',
            boxCount: qty,
            price: unitPrice
          }
        }),
      ...packagingRows.value
        .filter((r: any) => r._detailId && r.isDeleted === 1)
        .map((r: any) => ({ id: r._detailId, isDeleted: 1 }))
    ]

    // 印刷
    dto.printingList = [
      ...printRows.value
        .filter((r: any) => !r.isDeleted && (r.printType || r.price))
        .map((r: any) => {
          const price = parseFloat(r.price) || 0
          const plateFee = parseFloat(r.plateFee) || 0
          return {
            id: r._detailId || undefined,
            fabricTypeName: r.fabricLabel || '',
            printTypeId: r.printTypeId || printTypeOptions.value.find((o: any) => o.label === r.printType)?.id || null,
            printTypeName: r.printType || '',
            alignmentTypeId:
              r.alignmentTypeId || printAlignmentOptions.value.find((o: any) => o.label === r.alignment)?.id || null,
            alignmentTypeName: r.alignment || '',
            price: price,
            plateFee: plateFee
          }
        }),
      ...printRows.value
        .filter((r: any) => r._detailId && r.isDeleted === 1)
        .map((r: any) => ({ id: r._detailId, isDeleted: 1 }))
    ]

    // 工价
    dto.processPriceList = wageRows.value
      .filter((r: any) => wageAmounts[r.field] !== undefined && wageAmounts[r.field] !== '')
      .map((r: any) => ({
        id: wageDetailIds[r.field] || undefined,
        processId: r.field,
        name: r.label,
        price: parseFloat(wageAmounts[r.field]) || 0
      }))

    // 图片
    dto.images = imageList.value
      .filter((img: any) => img.isDeleted === 1 || img.storageId)
      .map((img: any) => ({
        ...(img.id && { id: img.id }),
        storageId: img.storageId,
        url: img.url,
        ...(img.isDeleted === 1 && { isDeleted: 1 })
      }))

    // return console.log('dto', dto)
    // eslint-disable-next-line no-unreachable
    const { code, message } = await saveOrUpdateProduct(dto)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('保存成功')
    tagsStore.delVisitedView(route)
    tagsStore.delCachedView(route)
    router.push('/base-info/product/index')
  } finally {
    saveLoading.value = false
  }
}

const handleCancel = () => {
  tagsStore.delVisitedView(route)
  tagsStore.delCachedView(route)
  router.push('/base-info/product/index')
}

const hasInitialized = ref(false)

const initPage = async () => {
  if (isEdit.value) {
    loading.value = true
  }
  await loadAllOptions()
  const { code, data } = await getUmbrellaFrameList()
  if (code === 200) umbrellaAllRecords.value = data || []
  if (isEdit.value) {
    await loadProductData()
  }
}

onMounted(() => {
  initPage()
})

onActivated(() => {
  if (hasInitialized.value) {
    // initPage()
  }
  hasInitialized.value = true
})
</script>

<style lang="scss" scoped>
.form-page {
  background: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 80px;

  .page-header {
    background: #fff;
    padding: 15px 15px 0;
    border-radius: 4px;

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }
  .form-content {
    background: #fff;
    padding: 15px;
    border-radius: 4px;
  }
  .section-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
    padding-left: 10px;
    border-left: 3px solid #409eff;
    margin: 30px 0 20px 0;
    display: flex;
    align-items: center;
  }
  .bottom-layout {
    align-items: flex-start;
  }
  .bottom-col {
    display: flex;
    flex-direction: column;
  }
  .basic-outer-layout {
    display: flex;
    gap: 24px;
    align-items: flex-start;
  }
  .basic-main-area {
    flex: 3;
    min-width: 0;
  }
  .basic-desc-area {
    flex: 1;
    min-width: 200px;
  }
  .two-col-layout {
    display: flex;
    gap: 40px;
    align-items: flex-start;
  }
  .basic-left-col,
  .basic-middle-col,
  .basic-cost-col,
  .basic-right-col {
    flex: 1;
    min-width: 0;
  }
  .left-col {
    flex: 1;
    min-width: 0;
  }
  .middle-col {
    flex: 1;
    min-width: 0;
  }
  .right-col {
    flex: 1;
    min-width: 0;
  }
  .image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    .image-item {
      position: relative;
      width: 80px;
      height: 80px;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      overflow: hidden;
      background: #ccc;

      .image-preview {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .image-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 15px;
        opacity: 0;
        transition: opacity 0.3s;
        cursor: pointer;

        &:hover {
          opacity: 1;
        }

        .overlay-icon {
          font-size: 20px;
          color: #fff;
          transition: transform 0.2s;

          &:hover {
            transform: scale(1.2);
          }
        }
      }
    }

    .img-uploader {
      :deep(.bz-upload) {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        width: 80px;
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover {
          border-color: #409eff;
        }
      }

      .img-uploader-icon {
        font-size: 20px;
        color: #8c939d;
      }
    }
  }
  .section-label {
    font-size: 14px;
    font-weight: 600;
    color: #606266;
    margin-bottom: 10px;
    display: flex;
    align-items: center;

    &--gap {
      gap: 8px;
    }

    &--between {
      justify-content: space-between;
    }

    &--mt {
      margin-top: 16px;
    }

    &--fixed-height {
      height: 17px;
    }
  }
  :deep(.tall-row td) {
    height: 52px;
  }
  .auto-calc-text {
    line-height: 32px;
    color: #303133;
    font-size: 14px;
  }
  :deep(.el-table__header th.el-table__cell) {
    height: 42px;
  }

  :deep(.el-select),
  :deep(.el-cascader) {
    width: 100%;
  }

  .weight-row {
    display: flex;
    gap: 4px;
    width: 100%;
    margin-bottom: 18px;
  }

  .weight-input-wrap {
    display: flex;
    align-items: center;
    gap: 4px;
    width: 60%;
  }

  .unit-text {
    flex-shrink: 0;
    color: #606266;
    font-size: 14px;
  }

  .inline-form-item {
    flex: 1;
    margin-bottom: 0;
  }

  .calc-info-row {
    display: flex;
    justify-content: space-between;
    width: 100%;
  }

  .cost-row {
    display: flex;
    gap: 4px;
    width: 100%;
  }

  .total-cost-text {
    font-weight: 600;
    color: #e6a23c;
  }

  .loss-input {
    width: 30%;
  }

  .form-item--mb18 {
    margin-bottom: 18px;
  }

  .upload-icon {
    font-size: 24px;
    color: #c0c4cc;

    &--loading {
      color: #409eff;
    }
  }

  .desc-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6px;
  }

  .desc-header-title {
    font-size: 14px;
    color: #606266;
  }

  .desc-header-actions {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .section-title--compact {
    margin-top: 10px;
  }

  .ellipsis-tag {
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .umbrella-select-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
  }

  .bound-tag {
    font-size: 11px;
  }

  .add-row-btn {
    width: 100%;
    margin-top: 8px;
  }

  .table-wrap--flex2 {
    flex: 2;
  }

  .table-wrap--flex3 {
    flex: 3;
  }

  .table-wrap--print {
    flex: 1;
    margin-bottom: 16px;
  }

  .shrink-badge {
    color: #e6a23c;
  }

  .loading-icon {
    color: #409eff;
  }

  .missing-text {
    color: #f56c6c;
    cursor: default;
  }

  .box-size-row {
    display: flex;
    align-items: center;
    gap: 2px;
  }

  .box-size-input {
    width: 52px;
  }

  .box-size-separator {
    color: #909399;
    font-size: 12px;
  }
}
</style>
