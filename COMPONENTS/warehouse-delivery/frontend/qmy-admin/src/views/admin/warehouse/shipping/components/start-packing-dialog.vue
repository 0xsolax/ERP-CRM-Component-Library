<template>
  <el-dialog v-model="dialogVisible" title="开始打包" width="1000" @close="onDestroy">
    <div class="customer-info">客户名称：{{ customerName }}</div>

    <div class="packing-content">
      <div ref="packageSidebarRef" class="package-sidebar">
        <div
          v-for="(pkg, index) in visiblePackageList"
          :key="pkg.id"
          :class="['package-card', { active: currentPackageIndex === index }]"
          @click="currentPackageIndex = index"
        >
          <el-icon class="close-icon" @click.stop="removePackage(index)"><Close /></el-icon>
          <div class="package-label">包裹{{ index + 1 }}</div>
          <!-- <div v-if="!pkg.isNew" class="package-size">{{ pkg.boxCode || '-' }}</div> -->
          <el-input
            v-model="pkg.boxCode"
            placeholder="请输入箱号"
            size="small"
            style="margin-top: 3px"
            clearable
            @click.stop
          />
        </div>
        <el-button type="primary" link @click="addNewPackage">新增包裹</el-button>
      </div>

      <div class="package-main">
        <div class="scan-area">
          <div class="scan-input">
            <el-input v-model="scanCode" placeholder="请扫码" @keyup.enter="handleScan">
              <template #suffix>
                <el-icon :size="24" style="cursor: pointer" @click="handleScan"><View /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="weight-input">
            <el-input
              v-model="currentPackage.boxWeight"
              placeholder="预估10kg，请输入"
              style="width: 200px"
              @input="handleWeightInput"
            >
              <template #append>kg</template>
            </el-input>
          </div>
          <div class="dimension-input">
            <el-input
              v-model="currentPackage.boxSize"
              placeholder="请输入尺寸"
              style="width: 200px"
              @input="handleDimensionInput"
            />
          </div>
          <el-button type="danger" @click="clearAllProducts">一键全清</el-button>
        </div>

        <el-table :data="currentPackage.products" border style="width: 100%">
          <el-table-column label="产品ID" prop="productCode" width="100" align="center" />
          <el-table-column label="规格名称" align="center">
            <template #default="{ row }">
              <el-tooltip v-if="row.description" :content="row.description" placement="top">
                <div>{{ row.specName }}</div>
              </el-tooltip>
              <div v-else>{{ row.specName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="图片" align="center">
            <template #default="{ row }">
              <el-image v-if="row.image" :src="row.image" fit="cover" style="width: 40px; height: 40px" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="库位" prop="locationName" align="center" />
          <el-table-column label="实际打包数量" align="center">
            <template #default="{ row }">
              <el-input
                v-model="row.actualCount"
                placeholder="请输入"
                style="width: 80px"
                @input="(val: string) => (row.actualCount = validateInteger(val))"
              />
            </template>
          </el-table-column>
          <el-table-column label="本次打包数量" align="center">
            <template #default="{ row }">
              <div>要求: {{ row.packCount }}</div>
              <div>仓库: {{ row.enterNumber }}</div>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" prop="categorySpecificationItemName" align="center">
            <template #default="{ row }">{{ row.categorySpecificationItemName || '/' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeProduct($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleSave">暂存</el-button>
      <el-button type="primary" @click="handleComplete">打包完成</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted, nextTick } from 'vue'
import { Close, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import {
  getPackageList,
  getPackageItemList,
  deliveryScan,
  getDeliveryDetail,
  savePackage,
  completePackage
} from '@/api/admin/warehouse'
import { validateInteger, validateDecimal } from '@/utils/validate'
import OrderConfirmDialog from './order-confirm-dialog.vue'

const attrs = useAttrs()
const { rowData, onDestroy, callback } = attrs as any
const dialogVisible = ref(true)
const currentPackageIndex = ref(0)

// 将 null、undefined、''、'-1' 都统一转换为空字符串
const normalizeCategoryId = (id: any): string => {
  if (id === null || id === undefined || id === '' || id === '-1') {
    return ''
  }
  return String(id)
}
const scanCode = ref('')
const packageSidebarRef = ref<HTMLElement>()

const customerName = ref(rowData?.customerName)
const packageList = ref<any[]>([])
const visiblePackageList = computed(() => {
  return packageList.value.filter((pkg: any) => !pkg.isDeleted)
})

const currentPackage = computed(() => {
  if (currentPackageIndex.value >= 0 && currentPackageIndex.value < visiblePackageList.value.length) {
    return visiblePackageList.value[currentPackageIndex.value]
  }
  return { products: [], boxWeight: '' }
})

const addNewPackage = async () => {
  // 获取所有未打包的产品和数量
  const unpackedProducts = await fetchUnpackedProducts()

  packageList.value.push({
    id: String(Date.now()),
    boxCode: '',
    boxName: '',
    boxSize: '',
    boxWeight: '',
    products: unpackedProducts,
    isNew: true
  })

  currentPackageIndex.value = visiblePackageList.value.length - 1

  nextTick(() => {
    if (packageSidebarRef.value) {
      packageSidebarRef.value.scrollTop = packageSidebarRef.value.scrollHeight
    }
  })
}

const handleWeightInput = (val: string) => {
  if (!visiblePackageList.value.length) {
    ElMessage.warning('请先添加包裹')
    const currentPkg = currentPackage.value
    if (currentPkg) {
      currentPkg.boxWeight = ''
    }
    return
  }

  const currentPkg = currentPackage.value
  if (currentPkg) {
    currentPkg.boxWeight = validateDecimal(val)
  }
}

const handleDimensionInput = (val: string) => {
  if (!visiblePackageList.value.length) {
    ElMessage.warning('请先添加包裹')
    const currentPkg = currentPackage.value
    if (currentPkg) {
      currentPkg.boxSize = ''
    }
    return
  }

  const currentPkg = currentPackage.value
  if (currentPkg) {
    currentPkg.boxSize = val
  }
}

const clearAllProducts = () => {
  const currentPkg = currentPackage.value
  scanCode.value = ''
  currentPackage.value.boxWeight = ''
  currentPackage.value.boxSize = ''
  if (currentPkg && currentPkg.products) {
    currentPkg.products = []
    ElMessage.success('已清空本包裹产品')
  }
}

// 获取所有未打包的产品和数量
const fetchUnpackedProducts = async () => {
  const { code, data, message } = await getDeliveryDetail({ id: rowData.id })
  if (code !== 200) {
    ElMessage.warning(message)
    return []
  }

  if (!data || !data.itemList || data.itemList.length === 0) {
    return []
  }

  const normalizeCategoryId = (id: any) => {
    if (id === null || id === undefined || id === '' || id === '-1') {
      return ''
    }
    return String(id)
  }

  // 计算当前所有包裹中已打包的数量
  const packedQuantities = new Map<string, number>()

  packageList.value.forEach((pkg: any) => {
    if (!pkg.isDeleted && pkg.products) {
      pkg.products.forEach((product: any) => {
        const normalizedCategoryId = normalizeCategoryId(product.categorySpecificationItemId)
        const key = `${product.specificationId}_${normalizedCategoryId}`
        const currentPacked = packedQuantities.get(key) || 0
        const actualCount = Number(product.actualCount || 0)
        packedQuantities.set(key, currentPacked + actualCount)
      })
    }
  })

  const unpackedProducts: any[] = []
  for (const item of data.itemList.filter((deliveryItem: any) => deliveryItem.specificationId)) {
    const specificationId = item.specificationId
    const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)
    const requiredNumber = item.number || 0

    // 计算已打包数量
    const key = `${specificationId}_${normalizedCategoryId}`
    const packed = packedQuantities.get(key) || 0
    const remaining = requiredNumber - packed

    if (remaining > 0) {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
        .filter((v: string) => v)
        .join('-')
      const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''

      unpackedProducts.push({
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: specName || '-',
        image: imageUrl,
        locationName: item.locationName || '-',
        actualCount: remaining,
        packCount: requiredNumber,
        enterNumber: item.enterNumber || 0,
        description: item.specificationDesc || ''
      })
    }
  }

  return unpackedProducts
}

// 获取所有未打包的产品和数量
const getUnpackedProducts = () => {
  // 收集所有已打包的产品数量
  const packedQuantities = new Map<string, number>()

  packageList.value.forEach((pkg: any) => {
    if (!pkg.isDeleted && pkg.products) {
      pkg.products.forEach((product: any) => {
        const key = `${product.specificationId}_${product.categorySpecificationItemId || ''}`
        const currentPacked = packedQuantities.get(key) || 0
        packedQuantities.set(key, currentPacked + Number(product.actualCount || 0))
      })
    }
  })

  // 获取所有产品的总需求量
  const allProducts = new Map<string, any>()
  packageList.value.forEach((pkg: any) => {
    if (pkg.products) {
      pkg.products.forEach((product: any) => {
        const key = `${product.specificationId}_${product.categorySpecificationItemId || ''}`
        if (!allProducts.has(key)) {
          allProducts.set(key, { ...product })
        }
      })
    }
  })

  // 计算未打包的产品
  const unpackedProducts: any[] = []
  allProducts.forEach((product, key) => {
    const packed = packedQuantities.get(key) || 0
    const required = Number(product.packCount || 0)
    const remaining = required - packed

    if (remaining > 0) {
      unpackedProducts.push({
        ...product,
        actualCount: remaining,
        packCount: required
      })
    }
  })

  return unpackedProducts
}
getUnpackedProducts

const removePackage = (index: number) => {
  const pkg = visiblePackageList.value[index]
  if (!pkg) return

  if (pkg.isNew) {
    const realIndex = packageList.value.findIndex((p: any) => p.id === pkg.id)
    if (realIndex !== -1) {
      packageList.value.splice(realIndex, 1)
    }
  } else {
    const realIndex = packageList.value.findIndex((p: any) => p.id === pkg.id)
    if (realIndex !== -1) {
      packageList.value[realIndex].isDeleted = 1
    }
  }

  if (currentPackageIndex.value >= visiblePackageList.value.length) {
    currentPackageIndex.value = Math.max(0, visiblePackageList.value.length - 1)
  }
}

const removeProduct = (index: number) => {
  const currentPkg = currentPackage.value
  if (currentPkg && currentPkg.products && currentPkg.products.length > index) {
    currentPkg.products.splice(index, 1)
  }
}

const fetchPackageList = async () => {
  const { code, data, message } = await getPackageList({ id: rowData.id })
  if (code !== 200) return ElMessage.warning(message)
  if (data && Array.isArray(data) && data.length > 0) {
    packageList.value = await Promise.all(
      data.map(async (pkg: any) => {
        const products = await fetchPackageItems(pkg.id)
        return {
          ...pkg,
          id: pkg.id,
          boxCode: pkg.boxCode ?? '',
          boxName: `${pkg.length || 0}*${pkg.width || 0}*${pkg.height || 0}`,
          boxSize: pkg.boxSize ?? '',
          boxWeight: pkg.boxWeight ?? '',
          products: products,
          isNew: false,
          isDeleted: 0
        }
      })
    )
  } else {
    // 如果没有现有包裹，创建第一个包裹并填充所有产品
    const allProducts = await fetchUnpackedProducts()
    packageList.value = [
      {
        id: String(Date.now()),
        boxCode: '',
        boxName: '',
        boxSize: '',
        boxWeight: '',
        products: allProducts,
        isNew: true
      }
    ]
  }
}

// 获取所有需要打包的产品
const fetchAllProducts = async () => {
  try {
    const { code, data, message } = await getPackageItemList({ id: rowData.id, deliveryBoxId: '' })
    if (code !== 200) {
      ElMessage.warning(message)
      return []
    }
    if (data && Array.isArray(data)) {
      return data.map((item: any) => {
        const specItems = item.itemList || []
        const specName = specItems
          .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
          .filter((v: string) => v)
          .join('-')
        const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''

        return {
          productCode: item.productCode || '',
          productId: item.productId,
          specificationId: item.specificationId,
          locationId: item.locationId,
          categorySpecificationItemId: item.categorySpecificationItemId,
          categorySpecificationItemName: item.categorySpecificationItemName,
          specName: specName || '-',
          image: imageUrl,
          locationName: item.locationName || '-',
          actualCount: item.number || 0,
          packCount: item.number || 0,
          enterNumber: item.enterNumber || 0,
          description: item.description || ''
        }
      })
    }
    return []
  } catch (error: any) {
    return []
  }
}
fetchAllProducts

const fetchPackageItems = async (deliveryBoxId: string) => {
  try {
    const { code, data, message } = await getPackageItemList({ id: rowData.id, deliveryBoxId })
    if (code !== 200) return ElMessage.warning(message)
    if (!data || !Array.isArray(data) || data.length === 0) {
      return []
    }

    const detailResult = await getDeliveryDetail({ id: rowData.id })
    if (detailResult.code !== 200) {
      ElMessage.warning(detailResult.message)
      return []
    }

    const deliveryItemMap = new Map<string, any>()
    if (detailResult.data && detailResult.data.itemList) {
      detailResult.data.itemList
        .filter((deliveryItem: any) => deliveryItem.specificationId)
        .forEach((item: any) => {
          const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)
          const key = `${item.specificationId}_${normalizedCategoryId}`
          deliveryItemMap.set(key, {
            packCount: item.number || 0,
            enterNumber: item.enterNumber || 0
          })
        })
    }

    // 使用 packageItemList 的 number 作为 actualCount，使用 delivery/detail 的数据作为 packCount 和 enterNumber
    return data.map((item: any) => {
      const specItems = item.itemList || []
      const specName = specItems
        .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
        .filter((v: string) => v)
        .join('-')
      const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''

      // 从 deliveryItemMap 中获取正确的要求数量和仓库数量
      const normalizedCategoryId = normalizeCategoryId(item.categorySpecificationItemId)
      const key = `${item.specificationId}_${normalizedCategoryId}`
      const deliveryItem = deliveryItemMap.get(key) || { packCount: 0, enterNumber: 0 }

      return {
        id: item.id || null,
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: specName || '-',
        image: imageUrl,
        locationName: item.locationName || '-',
        actualCount: item.number || 0, // 使用 packageItemList 的 number 作为已打包数量
        packCount: deliveryItem.packCount, // 使用 delivery/detail 的 number 作为要求数量
        enterNumber: deliveryItem.enterNumber, // 使用 delivery/detail 的 enterNumber 作为仓库数量
        description: item.description || ''
      }
    })
  } catch (error: any) {
    return []
  }
}

const handleScan = async () => {
  if (!scanCode.value) {
    ElMessage.warning('请输入扫码内容')
    return
  }

  const currentPkg = currentPackage.value
  if (!currentPkg || !currentPkg.id) {
    ElMessage.warning('请先选择包裹')
    return
  }

  let specificationId = ''
  let categorySpecificationItemId = ''
  if (scanCode.value.includes('?')) {
    // 解析格式：?specificationId=56&categorySpecificationItemId=166
    const queryString = scanCode.value.split('?')[1]
    const params = new URLSearchParams(queryString)
    specificationId = params.get('specificationId') || ''
    categorySpecificationItemId = params.get('categorySpecificationItemId') || ''
  } else {
    specificationId = scanCode.value
  }

  const { data } = await deliveryScan({
    id: rowData.id,
    specificationId: specificationId,
    categorySpecificationItemId: categorySpecificationItemId
  })

  if (data && Array.isArray(data) && data.length > 0) {
    const item = data[0]
    const specItems = item.itemList || []
    const specName = specItems
      .map((spec: any) => `${spec.categorySpecificationItemValue || ''}`)
      .filter((v: string) => v)
      .join('-')
    const imageUrl = item.imageList && item.imageList.length > 0 ? item.imageList[0].url : ''

    const realPkgIndex = packageList.value.findIndex((p: any) => p.id === currentPkg.id)
    if (realPkgIndex === -1) return

    const realPkg = packageList.value[realPkgIndex]
    if (!realPkg.products) {
      realPkg.products = []
    }

    const existingProduct = realPkg.products.find((p: any) => p.specificationId === item.specificationId)

    if (existingProduct) {
      const newCount = Number(existingProduct.actualCount) + 1
      if (newCount > Number(existingProduct.packCount)) {
        // ElMessage.warning('实际打包数量不能超过本次打包数量')
        scanCode.value = ''
        return
      }
      existingProduct.actualCount = newCount
    } else {
      const packCount = item.number || 0
      realPkg.products.push({
        productCode: item.productCode || '',
        productId: item.productId,
        specificationId: item.specificationId,
        locationId: item.locationId,
        categorySpecificationItemId: item.categorySpecificationItemId,
        categorySpecificationItemName: item.categorySpecificationItemName,
        specName: specName || '-',
        image: imageUrl,
        locationName: item.locationName || '-',
        actualCount: packCount,
        packCount: packCount,
        enterNumber: item.enterNumber || 0,
        description: item.description || ''
      })
    }

    ElMessage.success('扫码成功')
    scanCode.value = ''
  } else {
    ElMessage.warning('未找到对应产品')
  }
}

const handleSave = async () => {
  const packageData = packageList.value.map((pkg: any) => {
    const boxItemList = (pkg.products || []).map((product: any) => ({
      id: product.id || null,
      specificationId: product.specificationId,
      locationId: product.locationId || null,
      number: product.actualCount || 0,
      productId: product.productId,
      categorySpecificationItemId: product.categorySpecificationItemId || '',
      categorySpecificationItemName: product.categorySpecificationItemName || ''
    }))

    const data: any = {
      deliveryId: rowData.id,
      boxId: pkg.boxId || null,
      boxCode: pkg.boxCode,
      boxWeight: pkg.boxWeight ?? '',
      boxItemList,
      boxSize: pkg.boxSize ?? ''
    }

    if (!pkg.isNew && pkg.id) {
      data.id = pkg.id
    }

    if (!pkg.isNew && pkg.isDeleted) {
      data.isDeleted = 1
    }

    return data
  })

  const { code, message } = await savePackage(packageData)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success('暂存成功')
  dialogVisible.value = false
  if (callback) callback()
}

const handleComplete = async () => {
  console.log('rowData', rowData)

  const activePackages = visiblePackageList.value

  if (activePackages.length === 0) {
    ElMessage.warning('请至少添加一个包裹')
    return
  }

  for (let i = 0; i < activePackages.length; i++) {
    const pkg = activePackages[i]
    if (!pkg.boxCode) {
      ElMessage.warning(`请为包裹${i + 1}输入箱号`)
      return
    }
  }

  for (let i = 0; i < activePackages.length; i++) {
    const pkg = activePackages[i]
    if (!pkg.boxWeight || pkg.boxWeight === '') {
      ElMessage.warning(`请填写包裹${i + 1}的重量`)
      return
    }
  }

  for (let i = 0; i < activePackages.length; i++) {
    const pkg = activePackages[i]
    if (!pkg.boxSize || pkg.boxSize === '') {
      ElMessage.warning(`请为包裹${i + 1}输入尺寸`)
      return
    }
  }

  for (let i = 0; i < activePackages.length; i++) {
    const pkg = activePackages[i]
    if (!pkg.products || pkg.products.length === 0) {
      ElMessage.warning(`请为包裹${i + 1}添加产品`)
      return
    }
  }

  // 同一个产品规格在不同包裹中的数量加起来比较
  const productSummary = new Map<
    string,
    { productCode: string; specName: string; totalActual: number; totalRequired: number }
  >()

  for (let i = 0; i < activePackages.length; i++) {
    const pkg = activePackages[i]
    if (pkg.products && pkg.products.length > 0) {
      pkg.products.forEach((product: any) => {
        const normalizedCategoryId = normalizeCategoryId(product.categorySpecificationItemId)
        const key = `${product.specificationId}_${normalizedCategoryId}`
        const actualCount = Number(product.actualCount) || 0
        const packCount = Number(product.packCount) || 0

        if (productSummary.has(key)) {
          const summary = productSummary.get(key)!
          summary.totalActual += actualCount
        } else {
          productSummary.set(key, {
            productCode: product.productCode,
            specName: product.specName || '-',
            totalActual: actualCount,
            totalRequired: packCount
          })
        }
      })
    }
  }

  // 检查每个产品规格的总数量是否符合要求
  const missingProducts: string[] = []
  const extraProducts: string[] = []

  productSummary.forEach(summary => {
    const diff = summary.totalActual - summary.totalRequired

    if (diff < 0) {
      // 实际打包数量少于本次打包数量
      missingProducts.push(`产品ID: ${summary.productCode}，规格: ${summary.specName}，缺${Math.abs(diff)}个`)
    } else if (diff > 0) {
      // 实际打包数量多于本次打包数量
      extraProducts.push(`产品ID: ${summary.productCode}，规格: ${summary.specName}，多${diff}个`)
    }
  })

  if (missingProducts.length > 0 || extraProducts.length > 0) {
    const allMessages = [...missingProducts, ...extraProducts]
    const message = allMessages.join('<br/>')

    await ElMessageBox.alert(message, '提示', {
      confirmButtonText: '确定',
      type: 'warning',
      dangerouslyUseHTMLString: true
    })
    return
  }

  const params = {
    id: 'orderConfirmDialog',
    el: '#app',
    data: {
      deliveryId: rowData.id,
      rowData: rowData,
      onConfirm: async (selectedRows: any[]) => {
        await submitPackage(selectedRows)
      }
    },
    render: OrderConfirmDialog
  }
  dynamic.show(params)
}

const submitPackage = async (selectedRows: any[] = []) => {
  const packageData = packageList.value.map((pkg: any) => {
    const boxItemList = (pkg.products || []).map((product: any) => ({
      id: product.id || null,
      specificationId: product.specificationId,
      locationId: product.locationId || null,
      number: product.actualCount || 0,
      productId: product.productId,
      categorySpecificationItemId: product.categorySpecificationItemId || '',
      categorySpecificationItemName: product.categorySpecificationItemName || ''
    }))

    const data: any = {
      deliveryId: rowData.id,
      boxId: pkg.boxId || null,
      boxCode: pkg.boxCode,
      boxWeight: pkg.boxWeight ?? '',
      boxItemList,
      boxSize: pkg.boxSize ?? ''
    }

    if (!pkg.isNew && pkg.id) {
      data.id = pkg.id
    }

    if (!pkg.isNew && pkg.isDeleted) {
      data.isDeleted = 1
    }

    return data
  })

  // return console.log('packageData', packageData)
  // eslint-disable-next-line no-unreachable
  const { code, message } = await savePackage(packageData)
  if (code !== 200) return ElMessage.warning(message)

  const completeData = {
    deliveryId: rowData.id,
    orderIdList: selectedRows.map((row: any) => row.orderId)
  }

  const completeResult = await completePackage(completeData)
  if (completeResult.code !== 200) return ElMessage.warning(completeResult.message)

  ElMessage.success('打包完成')
  dialogVisible.value = false
  if (callback) callback()
}

onMounted(() => {
  fetchPackageList()
})
</script>

<style lang="scss" scoped>
.customer-info {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.packing-content {
  display: flex;
  gap: 10px;

  .package-sidebar {
    width: 120px;
    flex-shrink: 0;
    overflow-y: auto;
    max-height: 600px;
    padding-right: 5px;

    .package-card {
      position: relative;
      padding: 10px;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      margin-bottom: 10px;
      cursor: pointer;

      &.active {
        border-color: #409eff;
        background-color: #ecf5ff;
      }

      .close-icon {
        position: absolute;
        top: 4px;
        right: 4px;
        font-size: 12px;
        color: #909399;
        cursor: pointer;

        &:hover {
          color: #f56c6c;
        }
      }

      .package-label {
        font-size: 14px;
        font-weight: 500;
        color: #409eff;
      }

      .package-size {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .package-main {
    flex: 1;

    .scan-area {
      display: flex;
      align-items: center;
      gap: 20px;
      padding: 20px;
      border: 2px dashed #409eff;
      border-radius: 8px;
      margin-bottom: 20px;
      background-color: #f5f7fa;

      .scan-input {
        flex: 1;
      }

      .weight-input {
        width: 200px;
      }

      .dimension-input {
        width: 200px;
      }
    }
  }
}
</style>
