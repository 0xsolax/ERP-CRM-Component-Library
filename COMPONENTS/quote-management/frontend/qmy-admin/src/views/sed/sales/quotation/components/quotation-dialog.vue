<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit && !isRecreate ? '编辑报价单' : '新增报价单'"
    width="1400px"
    @close="onDestroy"
  >
    <div class="quotation-form">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px" label-position="top">
        <el-row :gutter="20" align="middle">
          <el-col :span="5">
            <el-form-item label="客户名称" prop="customerId">
              <el-select
                v-model="form.customerId"
                placeholder="请选择"
                style="width: 100%"
                clearable
                filterable
                @change="handleCustomerChange"
              >
                <el-option v-for="item in customerList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="收货地址" prop="receiveAddressId">
              <el-select v-model="form.receiveAddressId" placeholder="请选择" style="width: 100%" clearable>
                <el-option v-for="item in addressList" :key="item.id" :label="item.address" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="特殊要求">
              <el-input v-model="form.specialRequirements" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="3" style="margin-top: 10px">
            <el-button
              type="primary"
              @click="handleImportHistory"
              v-permission="'sal:sed:quotation:getHistoryQuotation'"
            >
              从历史报价导入
            </el-button>
          </el-col>
        </el-row>
      </el-form>

      <div class="tab-section">
        <el-button-group>
          <el-button :type="activeTab === 'sku' ? 'primary' : ''" @click="activeTab = 'sku'">SKU信息</el-button>
          <el-button :type="activeTab === 'history' ? 'primary' : ''" @click="activeTab = 'history'">
            历史记录
          </el-button>
        </el-button-group>
      </div>

      <div v-show="activeTab === 'sku'" class="sku-section">
        <div class="section-header">
          <div class="section-title">SKU信息</div>
          <el-button type="primary" size="small" @click="handleAddProduct">新增</el-button>
        </div>

        <div class="sku-table">
          <el-table :data="tableData" border :span-method="spanMethod">
            <el-table-column label="型号名称" prop="productName" align="center" width="100" />
            <el-table-column label="搭配名称" prop="combinationName" align="center" width="100" />
            <el-table-column label="SKU名称" prop="skuName" align="center" width="100" />
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
            <el-table-column label="报价" align="center" width="100">
              <template #default="{ row }">
                <el-input
                  v-model="row.skuRef.price"
                  placeholder="请输入"
                  @input="(val: string) => (row.skuRef.price = validateDecimal(val))"
                />
              </template>
            </el-table-column>
            <el-table-column label="数量" align="center" width="100">
              <template #default="{ row }">
                <el-input
                  v-model="row.skuRef.quantity"
                  placeholder="请输入"
                  @input="(val: string) => (row.skuRef.quantity = validateInteger(val))"
                />
              </template>
            </el-table-column>
            <el-table-column label="体积 (m³)" align="center" width="100">
              <template #default="{ row }">
                <span>{{ calculateVolume(row.skuRef) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="包材名称" prop="packageName" align="center" />
            <el-table-column label="包材尺寸" prop="packageSize" align="center" />
            <el-table-column label="装箱数" prop="packingNum" align="center" width="80" />
            <el-table-column label="包材成本价" align="center" width="100">
              <template #default="{ row }">
                <span>{{ row.packingCost && row.packingCost !== '-' ? `${row.packingCost}` : '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" align="center" width="150">
              <template #default="{ row }">
                <el-input v-model="row.skuRef.remark" placeholder="请输入备注" />
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEditPackage(row.skuRef)">修改包装</el-button>
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="handleHistoryPrice(row.skuRef)"
                  v-permission="'sal:sed:quotation:getHistoryQuotationInfo'"
                >
                  历史报价
                </el-button>
                <el-button type="danger" link size="small" @click="handleRemoveSku(row.skuIndex)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div v-show="activeTab === 'history'" class="history-section">
        <div class="section-header">
          <div class="section-title">历史记录</div>
        </div>
        <el-table :data="historyList" border>
          <el-table-column label="操作时间" prop="time" align="center" />
          <el-table-column label="操作人" prop="operator" align="center" />
          <el-table-column label="操作内容" prop="content" align="center" />
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <span class="footer-label">币种</span>
          <el-select v-model="form.currency" placeholder="请选择" style="width: 100px">
            <el-option v-for="item in currencyList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <template v-if="form.currency === '2'">
            <span class="footer-label">汇率</span>
            <el-input
              v-model="form.exchangeRate"
              placeholder="请输入"
              maxlength="10"
              style="width: 100px"
              @input="(val: string) => (form.exchangeRate = validateDecimal(val, 5))"
            />
          </template>
          <template v-else>
            <span class="footer-label">是否含税</span>
            <el-select v-model="form.tax" placeholder="请选择" style="width: 100px">
              <el-option v-for="item in taxList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </template>

          <span class="footer-label">FOB</span>
          <el-select v-model="form.fob" placeholder="请选择" style="width: 100px">
            <el-option v-for="item in fobList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <span class="footer-label">EXW</span>
          <el-select v-model="form.exw" placeholder="请选择" style="width: 100px">
            <el-option v-for="item in exwList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <span class="footer-label" style="margin-left: 10px">优惠金额</span>
          <el-input
            v-model="form.discountAmount"
            placeholder="请输入"
            style="width: 100px"
            @input="(val: string) => (form.discountAmount = validateDecimal(val, 2))"
          />
          <span class="footer-label">订单金额</span>
          <span class="order-amount">{{ form.currency === '1' ? '¥' : '$' }}{{ totalAmount }}</span>
        </div>
        <div class="footer-right">
          <!-- 新增或暂存 -->
          <template v-if="!currentStatus || currentStatus === '0'">
            <el-button
              :loading="submitLoading"
              @click="handleSaveDraft"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
            >
              暂存
            </el-button>
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="handleSubmit"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
            >
              确定提交，进入成本核算环节
            </el-button>
          </template>
          <!-- 计算成本中 -->
          <template v-else-if="currentStatus === '1'">
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="handleReSubmit"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
            >
              确定修改，重新进入成本核算环节
            </el-button>
          </template>
          <!-- 计算成本完毕 -->
          <template v-else-if="currentStatus === '2'">
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="handleReSubmit"
              v-permission="'sal:sed:quotation:saveOrUpdate'"
            >
              确定修改，重新进入成本核算环节
            </el-button>
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="handleSubmitAudit"
              v-permission="'sal:sed:quotation:audit'"
            >
              提交审核
            </el-button>
          </template>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import ImportHistoryDialog from './import-history-dialog.vue'
import EditPackageDialog from './edit-package-dialog.vue'
import HistoryPriceDrawer from './history-price-drawer.vue'
import SedProductSelector from '@/components/sed-product-selector/index.vue'
import {
  saveOrUpdateQuotation,
  getCustomerAddress,
  getQuotationDetail,
  submitQuotationAudit
} from '@/api/sed/sales/quotation'
import { getCustomerSelectList } from '@/api/admin/sales/customer'
import { validateDecimal, validateInteger } from '@/utils/validate'
import { currencyList, taxList, fobList, exwList } from '@/constant/sed/sales'
import { getQuotationOperationLabel } from '@/constant/sed/quotation'
import { useUserStore } from '@/views/sed/store/modules/user'

const userStore = useUserStore()
const submitLoading = ref(false)
const attrs = useAttrs()
const { isEdit, isRecreate, rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const activeTab = ref('sku')

const currentStatus = computed(() => {
  if (!isEdit || isRecreate) return ''
  return rowData?.status || ''
})

const form = ref({
  customerId: '' as string | number,
  receiveAddressId: '' as string | number,
  specialRequirements: '',
  discountAmount: '',
  currency: '1',
  exchangeRate: '',
  tax: '1',
  fob: '',
  exw: ''
})

const formRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  receiveAddressId: [{ required: true, message: '请选择收货地址', trigger: 'change' }]
}

const formRef = ref()
const customerList = ref<any[]>([])
const addressList = ref<any[]>([])

const loadCustomerList = async () => {
  const { code, data, message } = await getCustomerSelectList({ pageNum: 1, pageSize: 1000 })
  if (code !== 200) return ElMessage.warning(message)
  customerList.value = data || []
}

const loadAddressList = async () => {
  if (!form.value.customerId) {
    addressList.value = []
    return
  }
  const { code, data, message } = await getCustomerAddress({ id: form.value.customerId })
  if (code !== 200) return ElMessage.warning(message)
  addressList.value = (data || []).map((item: any) => ({
    ...item,
    address: [item.province, item.city, item.county, item.detail].filter(val => val && val !== '0').join('')
  }))
}

const handleCustomerChange = () => {
  form.value.receiveAddressId = ''
  loadAddressList()
}

const loadQuotationDetail = async () => {
  if (!rowData?.id) return
  const { code, data, message } = await getQuotationDetail({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)

  form.value.customerId = data.customerId || ''
  form.value.receiveAddressId = data.receiveAddressId || ''
  form.value.specialRequirements = data.specialRequirements || ''
  form.value.discountAmount = data.discountAmount || ''
  form.value.currency = data.currency || '1'
  form.value.exchangeRate = data.exchangeRate || ''
  form.value.tax = data.tax || '1'
  form.value.fob = data.fob || ''
  form.value.exw = data.exw || ''

  // 加载地址列表
  await loadAddressList()

  const skuMap = new Map<string, any>()
  ;(data.skuList || []).forEach((item: any) => {
    const key = item.quotationSkuId
    let packageSize = item.packageSize ?? ''
    let packingSize = item.packingSize ?? ''
    if ((packageSize != '' && packingSize != '') || (packageSize == '' && packingSize != '')) {
      packageSize = packingSize
    }
    if (skuMap.has(key)) {
      const existSku = skuMap.get(key)
      if (item.packageName) {
        existSku.packages.push({
          id: item.quotationSkuPackingId, // 包材表skuId
          packageId: item.packageId || '', // 包材id
          name: item.packageName || '',
          size: packageSize || '',
          packingNum: item.packingNumber || '',
          packingCost: item.packingCost || '',
          type: item.packageType || '',
          attachmentList: item.attachmentList || []
        })
      }
    } else {
      skuMap.set(key, {
        id: item.quotationSkuId,
        productId: item.productId,
        matchId: item.combinationId,
        skuId: item.skuId,
        modelName: item.modelName || '',
        productName: item.productName || '',
        combinationName: item.combinationName || '',
        skuName: item.skuName || '',
        image: item.pic?.[0] || '',
        price: item.price || '',
        basicPrice: item.basicPrice || '',
        quantity: item.number || '',
        volume: item.volume || '',
        remark: item.remark || '',
        packages: item.packageName
          ? [
              {
                id: item.quotationSkuPackingId,
                packageId: item.packageId || '',
                name: item.packageName || '',
                size: packageSize || '',
                packingNum: item.packingNumber || '',
                packingCost: item.packingCost || '',
                type: item.packageType || '',
                attachmentList: item.attachmentList || []
              }
            ]
          : []
      })
    }
  })

  skuList.value = Array.from(skuMap.values())

  // 加载历史记录
  historyList.value = (data.historyList || []).map((item: any) => ({
    time: item.operateTime || '',
    operator: item.operatePerson || '',
    content: getQuotationOperationLabel(item.operation)
  }))
}

onMounted(() => {
  loadCustomerList()
  if ((isEdit || isRecreate) && rowData) {
    loadQuotationDetail()
  }
})

const skuList = ref<any[]>([])
const historyList = ref<any[]>([])

const tableData = computed(() => {
  const result: any[] = []
  skuList.value.forEach((sku, skuIndex) => {
    if (sku.isDeleted === 1) return
    const packages = (sku.packages || []).filter((pkg: any) => pkg.isDeleted !== 1)
    if (packages.length === 0) {
      result.push({
        ...sku,
        skuRef: sku,
        skuIndex,
        skuId: sku.skuId,
        packageName: '-',
        packageSize: '-',
        packingNum: '-',
        packingCost: '-',
        packageIndex: 0,
        packageCount: 1
      })
    } else {
      packages.forEach((pkg: any, pkgIndex: number) => {
        result.push({
          ...sku,
          skuRef: sku,
          skuIndex,
          skuId: sku.skuId,
          packageName: pkg.name || '-',
          packageSize: pkg.size || '-',
          packingNum: pkg.packingNum || '-',
          packingCost: pkg.packingCost || '-',
          packageIndex: pkgIndex,
          packageCount: packages.length
        })
      })
    }
  })
  return result
})

const formatBasicPrice = (sku: any): string => {
  if (!sku.basicPrice) return '-'
  return sku.currency == '1' ? `¥${sku.basicPrice}` : `$${sku.basicPrice}`
}

// 计算SKU体积
const calculateVolume = (row: any): string => {
  if (!row.packages || !row.packages.length) return '0'
  const quantity = Number(row.quantity) || 0
  let totalVolumeCm3 = 0

  row.packages.forEach((pkg: any) => {
    if (pkg.isDeleted === 1) return

    if (pkg.size) {
      const sizeStr = pkg.size.replace(/[a-zA-Z]/g, '').trim()
      const dimensions = sizeStr.split('*').map((d: string) => parseFloat(d.trim()) || 0)

      if (dimensions.length >= 3 && dimensions[0] > 0 && dimensions[1] > 0 && dimensions[2] > 0) {
        const [length, width, height] = dimensions
        // 单个箱子体积
        const singleBoxVolume = length * width * height
        if (quantity > 0) {
          totalVolumeCm3 += singleBoxVolume * quantity
        } else {
          totalVolumeCm3 += singleBoxVolume
        }
      }
    }
  })
  if (totalVolumeCm3 === 0) return '0'
  // 立方厘米转立方米
  const volumeM3 = totalVolumeCm3 / 1000000
  return volumeM3.toFixed(4)
}

// 计算合并单元格
const spanMethod = ({ row, rowIndex, columnIndex }: { row: any; rowIndex: number; columnIndex: number }) => {
  const data = tableData.value
  if (columnIndex >= 7 && columnIndex <= 10) {
    return { rowspan: 1, colspan: 1 }
  }

  if (columnIndex === 0) {
    const currentModel = row.modelName
    if (rowIndex === 0 || data[rowIndex - 1]?.modelName !== currentModel) {
      let rowspan = 1
      for (let i = rowIndex + 1; i < data.length; i++) {
        if (data[i].modelName === currentModel) {
          rowspan++
        } else {
          break
        }
      }
      return { rowspan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }

  if (columnIndex === 1) {
    const currentModel = row.modelName
    const currentComb = row.combinationName
    if (
      rowIndex === 0 ||
      data[rowIndex - 1]?.modelName !== currentModel ||
      data[rowIndex - 1]?.combinationName !== currentComb
    ) {
      let rowspan = 1
      for (let i = rowIndex + 1; i < data.length; i++) {
        if (data[i].modelName === currentModel && data[i].combinationName === currentComb) {
          rowspan++
        } else {
          break
        }
      }
      return { rowspan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }

  if (row.packageIndex === 0) {
    return { rowspan: row.packageCount, colspan: 1 }
  }
  return { rowspan: 0, colspan: 0 }
}

const totalAmount = computed(() => {
  const total = skuList.value
    .filter(item => item.isDeleted !== 1)
    .reduce((sum, item) => {
      const price = Number(item.price) || 0
      const packingNum = Number(item.quantity) || 0
      return sum + price * packingNum
    }, 0)
  const discount = Number(form.value.discountAmount) || 0
  return (total - discount).toFixed(2)
})

const handleImportHistory = () => {
  const params = {
    id: 'importHistoryDialog',
    el: '#app',
    data: {
      callback: (data: any[]) => {
        console.log('导入的历史数据:', data)

        // 按skuId分组
        const skuMap = new Map<string, any>()

        data.forEach((item: any) => {
          const key = item.skuId
          let packageSize = item.packageSize ?? ''
          let packingSize = item.packingSize ?? ''
          if ((packageSize != '' && packingSize != '') || (packageSize == '' && packingSize != '')) {
            packageSize = packingSize
          }
          if (skuMap.has(key)) {
            const existSku = skuMap.get(key)
            if (item.packageName) {
              const existingPackage = existSku.packages.find((pkg: any) => pkg.packageId === item.packageId)
              if (!existingPackage) {
                existSku.packages.push({
                  id: item.quotationSkuPackingId || '',
                  packageId: item.packageId || '',
                  name: item.packageName || '',
                  size: packageSize,
                  packingNum: item.packingNumber || '',
                  packingCost: item.packingCost || '',
                  type: item.packageType || '',
                  attachmentList: item.attachmentList || []
                })
              }
            }
          } else {
            skuMap.set(key, {
              productId: item.productId,
              matchId: item.combinationId,
              quotationSkuId: item.quotationSkuId,
              skuId: item.skuId,
              modelName: item.modelName || '',
              productName: item.productName || '',
              combinationName: item.combinationName || '',
              skuName: item.skuName || '',
              image: item.pic?.[0] || '',
              price: item.price ?? '',
              basicPrice: item.basicPrice ?? '',
              quantity: item.number || '',
              remark: item.remark || '',
              packages: item.packageName
                ? [
                    {
                      id: item.packageId || '',
                      packageId: item.packageId || '',
                      name: item.packageName || '',
                      size: packageSize,
                      packingNum: item.packingNumber || '',
                      packingCost: item.packingCost || '',
                      type: item.packageType || '',
                      attachmentList: item.attachmentList || []
                    }
                  ]
                : []
            })
          }
        })

        // 将分组后的SKU添加到列表
        const newSkus = Array.from(skuMap.values())
        newSkus.forEach((newSku: any) => {
          const existingIndex = skuList.value.findIndex((sku: any) => sku.skuId === newSku.skuId)
          if (existingIndex === -1) {
            skuList.value.push(newSku)
          } else {
            const existingSku = skuList.value[existingIndex]
            newSku.packages.forEach((newPkg: any) => {
              const existingPkg = existingSku.packages.find((pkg: any) => pkg.packageId === newPkg.packageId)
              if (!existingPkg) {
                existingSku.packages.push(newPkg)
              }
            })
          }
        })

        console.log('当前skuList:', skuList.value)
      }
    },
    render: ImportHistoryDialog
  }
  dynamic.show(params)
}

const handleAddProduct = () => {
  const params = {
    id: 'sedProductSelector',
    el: '#app',
    data: {
      callback: (selectedSkus: any[]) => {
        console.log('selectedSkus', selectedSkus)

        selectedSkus.forEach((sku: any) => {
          skuList.value.push({
            productId: sku.productId,
            productName: sku.productName,
            matchId: sku.matchId,
            skuId: sku.id,
            modelName: sku.modelName || '',
            combinationName: sku.combinationName || '',
            skuName: sku.name || '',
            image: sku.imageList?.[0]?.url || '',
            price: '',
            basicPrice: sku.basicPrice ?? '',
            currency: sku.currency ?? '',
            quantity: '',
            volume: '',
            remark: '',
            packages: (sku.packageList || []).map((pkg: any) => ({
              packageId: pkg.packageId,
              name: pkg.name || '',
              size: pkg.size || '',
              packingNum: pkg.number || '',
              packingCost: pkg.cost || '',
              type: pkg.type || '',
              attachmentList: []
            }))
          })
        })
      }
    },
    render: SedProductSelector
  }
  dynamic.show(params)
}

const handleHistoryPrice = (row: any) => {
  console.log('handleHistoryPrice', row)
  if (!form.value.customerId) return ElMessage.warning('请先选择客户')

  const params = {
    id: 'historyPriceDrawer',
    el: '#app',
    data: {
      rowData: {
        skuId: row.skuId,
        productId: row.productId,
        quotationSkuId: row.quotationSkuId,
        matchId: row.matchId,
        customerId: form.value.customerId,
        image: row.image
      }
    },
    render: HistoryPriceDrawer
  }
  dynamic.show(params)
}

const handleEditPackage = (row: any) => {
  console.log('handleEditPackage', row)

  const params = {
    id: 'editPackageDialog',
    el: '#app',
    data: {
      rowData: row,
      isExisting: !!row.id, // 有id代表从数据库加载的，否则是新增
      callback: (packages: any[]) => {
        const uniquePackages: any[] = []
        const packageKeys = new Set<string>()

        packages.forEach(pkg => {
          if (pkg.isDeleted === 1) {
            uniquePackages.push(pkg)
            return
          }
          const key = `${pkg.packageId || ''}_${pkg.name || ''}_${pkg.size || ''}`
          if (!packageKeys.has(key)) {
            packageKeys.add(key)
            uniquePackages.push(pkg)
          }
        })

        row.packages = uniquePackages.map(pkg => ({
          id: pkg.id,
          packageId: pkg.packageId,
          type: pkg.type,
          name: pkg.name,
          size: pkg.size,
          packingNum: pkg.packingNum,
          packingCost: pkg.packingCost,
          attachmentList: pkg.attachmentList || [],
          isDeleted: pkg.isDeleted
        }))
      }
    },
    render: EditPackageDialog
  }
  dynamic.show(params)
}

const handleRemoveSku = (index: number) => {
  const sku = skuList.value[index]
  if (!sku.id) {
    skuList.value.splice(index, 1)
  } else {
    sku.isDeleted = 1
    if (sku.packages && sku.packages.length > 0) {
      sku.packages.forEach((pkg: any) => {
        pkg.isDeleted = 1
      })
    }
  }
}

const validateFields = (): boolean => {
  if (form.value.currency === '1') {
    if (!form.value.tax) {
      ElMessage.warning('请选择是否含税')
      return false
    }
    if (!form.value.fob) {
      ElMessage.warning('请选择FOB')
      return false
    }
    if (!form.value.exw) {
      ElMessage.warning('请选择EXW')
      return false
    }
  } else if (form.value.currency === '2') {
    if (!form.value.exchangeRate) {
      ElMessage.warning('请输入汇率')
      return false
    }
    if (!form.value.fob) {
      ElMessage.warning('请选择FOB')
      return false
    }
    if (!form.value.exw) {
      ElMessage.warning('请选择EXW')
      return false
    }
  }
  return true
}

const buildSubmitData = (status: string, operation: string) => {
  const submitData: any = {
    status,
    operation,
    customerId: form.value.customerId,
    receiveAddressId: form.value.receiveAddressId,
    specialRequirements: form.value.specialRequirements,
    salesmanId: userStore.userId,
    discountAmount: form.value.discountAmount || 0,
    currency: form.value.currency,
    fob: form.value.fob || undefined,
    exw: form.value.exw || undefined,
    skuList: skuList.value
      .filter((item: any) => {
        // 新增模式：过滤掉标记为删除的项
        if (!item.id && item.isDeleted === 1) return false
        return true
      })
      .map((item: any) => {
        const skuData: any = {
          productId: item.productId,
          matchId: Number(item.matchId),
          skuId: item.skuId,
          quotationPrice: item.price,
          quantity: item.quantity,
          volume: calculateVolume(item),
          remark: item.remark || '',
          isDeleted: item.isDeleted || undefined,
          packing: (item.packages || []).map((pkg: any) => {
            const packData: any = {
              packingId: pkg.packageId,
              packingNum: pkg.packingNum,
              packingSize: pkg.size,
              cost: pkg.packingCost,
              attachmentList: (pkg.attachmentList || []).map((att: any) => {
                const attData: any = { storageId: att.storageId }
                if (att.id) attData.id = att.id
                if (att.isDeleted) attData.isDeleted = att.isDeleted
                return attData
              })
            }
            if (pkg.id) packData.id = pkg.id
            if (pkg.isDeleted) packData.isDeleted = pkg.isDeleted
            return packData
          })
        }
        if (item.id) skuData.id = item.id
        return skuData
      })
  }

  if (form.value.currency === '1') {
    submitData.tax = form.value.tax
  } else if (form.value.currency === '2') {
    submitData.exchangeRate = form.value.exchangeRate
  }

  if (isEdit && rowData?.id) {
    submitData.id = rowData.id
  }
  return submitData
}

const handleSaveDraft = async () => {
  if (submitLoading.value) return
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const activeSkus = skuList.value.filter(sku => sku.isDeleted !== 1)
  if (activeSkus.length === 0) {
    return ElMessage.warning('请添加SKU')
  }

  for (let i = 0; i < activeSkus.length; i++) {
    const sku = activeSkus[i]
    if (!sku.quantity) {
      return ElMessage.warning(`第${i + 1}个SKU的数量不能为空`)
    }
  }

  if (!validateFields()) return
  const submitData = buildSubmitData('0', '7')
  submitLoading.value = true
  try {
    const { code, message } = await saveOrUpdateQuotation(submitData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('暂存成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    submitLoading.value = false
  }
}

const handleSubmit = async () => {
  if (submitLoading.value) return
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const activeSkus = skuList.value.filter(sku => sku.isDeleted !== 1)
  if (activeSkus.length === 0) {
    return ElMessage.warning('请添加SKU')
  }

  for (let i = 0; i < activeSkus.length; i++) {
    const sku = activeSkus[i]
    if (!sku.quantity) {
      return ElMessage.warning(`第${i + 1}个SKU的数量不能为空`)
    }
  }

  if (!validateFields()) return
  const submitData = buildSubmitData('1', '6')
  submitLoading.value = true
  try {
    const { code, message } = await saveOrUpdateQuotation(submitData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success(isEdit ? '编辑成功' : '提交成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    submitLoading.value = false
  }
}

const handleReSubmit = async () => {
  if (submitLoading.value) return
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const activeSkus = skuList.value.filter(sku => sku.isDeleted !== 1)
  if (activeSkus.length === 0) {
    return ElMessage.warning('请添加SKU')
  }

  for (let i = 0; i < activeSkus.length; i++) {
    const sku = activeSkus[i]
    if (!sku.quantity) {
      return ElMessage.warning(`第${i + 1}个SKU的数量不能为空`)
    }
  }

  if (!validateFields()) return
  submitLoading.value = true
  try {
    const submitData = buildSubmitData('1', '5')
    const { code, message } = await saveOrUpdateQuotation(submitData)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('修改成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    submitLoading.value = false
  }
}

const handleSubmitAudit = async () => {
  if (submitLoading.value) return
  await ElMessageBox.confirm('确认提交审核？', '提交审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const activeSkus = skuList.value.filter(sku => sku.isDeleted !== 1)
  if (activeSkus.length === 0) {
    return ElMessage.warning('请添加SKU')
  }

  for (let i = 0; i < activeSkus.length; i++) {
    const sku = activeSkus[i]
    if (!sku.quantity) {
      return ElMessage.warning(`第${i + 1}个SKU的数量不能为空`)
    }
  }

  if (!validateFields()) return
  submitLoading.value = true
  try {
    let status = '1' // 计算成本中
    let operation = '2' // 提交审核
    if (isEdit) {
      status = '5' // 总裁未审核，财务未审核
    }
    const submitData = buildSubmitData(status, operation)
    const { code: saveCode, message: saveMessage } = await saveOrUpdateQuotation(submitData)
    if (saveCode !== 200) return ElMessage.warning(saveMessage)
    // 保存成功后，提交审核
    const { code, message } = await submitQuotationAudit({
      id: rowData.id,
      salesmanId: userStore.userId
    })
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('提交成功')
    dialogVisible.value = false
    if (callback) callback()
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.quotation-form {
  .tab-section {
    margin-bottom: 15px;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    padding-left: 10px;
    border-left: 3px solid #409eff;
  }

  .label-tag {
    margin-left: 5px;
    border-radius: 50%;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
  }

  .header-tag {
    margin-left: 3px;
    border-radius: 50%;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
  }

  .sku-section,
  .history-section {
    margin-bottom: 20px;
  }

  .sku-table {
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
    gap: 10px;

    .footer-label {
      color: #606266;
      font-size: 14px;
    }

    .footer-tag {
      border-radius: 50%;
      min-width: 20px;
      height: 20px;
      padding: 0 5px;
    }

    .order-amount {
      font-size: 16px;
      font-weight: 600;
      color: #f56c6c;
    }
  }

  .footer-right {
    display: flex;
  }
}
</style>
