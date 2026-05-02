<template>
  <el-dialog v-model="visible" title="产品确认" width="600px" @close="handleClose">
    <div class="confirm-dialog-container">
      <div class="product-info">
        <div class="info-item">
          <span class="label">产品ID：</span>
          <span class="value">{{ productInfo.productId }}</span>
        </div>
        <div class="info-item">
          <span class="label">产品图片：</span>
          <el-image
            :src="productInfo.productImage"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px"
            :preview-src-list="[productInfo.productImage]"
            hide-on-click-modal
          />
        </div>
        <div class="info-item">
          <span class="label">数量：</span>
          <span class="value">{{ productInfo.number }}</span>
        </div>
      </div>

      <el-table :data="specList" border style="margin-top: 20px">
        <el-table-column label="规格名称" prop="specName" align="center" width="200">
          <template #default="{ $index }">
            <el-select
              v-model="specList[$index].specificationId"
              placeholder="请选择规格"
              @change="handleSpecChange($index, specList[$index].specificationId)"
              style="width: 100%"
              filterable
            >
              <el-option v-for="spec in availableSpecs" :key="spec.id" :label="spec.name" :value="spec.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="规格图片" align="center" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.specImage"
              :src="row.specImage"
              fit="cover"
              style="width: 40px; height: 40px"
              :preview-src-list="[row.specImage]"
              hide-on-click-modal
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="number" align="center">
          <template #default="{ $index }">
            <el-input v-model="specList[$index].number" placeholder="请输入" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="handleDeleteSpec($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="text-align: center; margin-top: 20px">
        <el-button type="primary" @click="handleAddSpec" style="width: 100%">新增</el-button>
      </div>

      <!-- <div class="tips">
        没有想要的规格，
        <el-button type="primary" link @click="handleCreateNew">立即新增</el-button>
      </div> -->
    </div>

    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/admin/product'
import { confirmInComplete } from '@/api/admin/sales/order'

interface Props {
  productData?: any
  onSuccess?: () => void
}

const props = defineProps<Props>()
const visible = ref(true)

const productInfo = ref({
  productId: '',
  productImage: '',
  number: 0
})

const availableSpecs = ref<any[]>([])

const specList = ref([
  {
    specificationId: '',
    specName: '',
    specImage: '',
    number: ''
  }
])

const handleAddSpec = () => {
  specList.value.push({
    specificationId: '',
    specName: '',
    specImage: '',
    number: ''
  })
}

const handleDeleteSpec = (index: number) => {
  if (specList.value.length <= 1) {
    ElMessage.warning('至少保留一条规格')
    return
  }
  specList.value.splice(index, 1)
}

const handleSpecChange = (index: number, specificationId: string) => {
  const selectedSpec = availableSpecs.value.find(spec => spec.id === specificationId)
  if (selectedSpec) {
    specList.value[index].specificationId = specificationId
    specList.value[index].specName = selectedSpec.name
    specList.value[index].specImage = selectedSpec.specificationImages?.[0]?.url || ''
  }
}

// 加载产品规格列表
const loadProductSpecs = async (productId: string) => {
  const { code, data, message } = await getProductDetail({ id: productId })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  availableSpecs.value = data?.specifications || []
}

// const handleCreateNew = () => {
//   ElMessage.info('立即新增')
// }

const handleConfirm = async () => {
  const validSpecs = specList.value.filter(spec => spec.specificationId && spec.number)
  if (validSpecs.length === 0) {
    ElMessage.warning('请至少填写一条完整的规格信息')
    return
  }

  const { code, message } = await confirmInComplete({
    itemIdList: props.productData?.salYtOrderSubItemIds ?? [],
    specificationList: validSpecs.map(spec => ({
      specificationId: spec.specificationId,
      number: spec.number
    }))
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  ElMessage.success('确认成功')
  props.onSuccess?.()
  visible.value = false
}

const handleClose = () => {
  visible.value = false
}

onMounted(async () => {
  console.log('props.productData', props.productData)

  if (props.productData) {
    const productId = props.productData.productId
    productInfo.value = {
      productId: props.productData.productCode || '',
      productImage: props.productData.productImage || '',
      number: props.productData.number || 0
    }

    if (productId) {
      await loadProductSpecs(productId)
    }
  }
})
</script>

<style lang="scss" scoped>
.confirm-dialog-container {
  .product-info {
    display: flex;
    gap: 30px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 4px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 10px;

      .label {
        font-size: 14px;
        color: #909399;
      }

      .value {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }
    }
  }

  .tips {
    margin-top: 15px;
    font-size: 14px;
    color: #909399;
    text-align: center;
  }
}
</style>
