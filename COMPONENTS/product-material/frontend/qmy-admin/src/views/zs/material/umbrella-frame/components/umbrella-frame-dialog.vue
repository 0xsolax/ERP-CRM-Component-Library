<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑伞架' : '新增伞架'" width="700px" @close="onDestroy">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px" v-loading="detailLoading">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="功能" prop="functionId">
            <el-select v-model="formData.functionId" placeholder="请选择" filterable clearable style="width: 100%">
              <el-option v-for="o in functionOptions" :key="o.id" :label="o.value1" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="类型" prop="typeId">
            <el-select v-model="formData.typeId" placeholder="请选择" filterable clearable style="width: 100%">
              <el-option v-for="o in typeOptions" :key="o.id" :label="o.value1" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="特定属性">
            <el-input v-model="formData.specificAttribute" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="尺寸" prop="lengthId">
            <div style="display: flex; gap: 4px; width: 100%">
              <el-select v-model="formData.lengthId" placeholder="伞架长度" filterable clearable style="flex: 1">
                <el-option v-for="o in sizeCmOptions" :key="o.id" :label="o.value1" :value="o.id" />
              </el-select>
              <el-select v-model="formData.diameterId" placeholder="中棒直径" filterable clearable style="flex: 1">
                <el-option v-for="o in sizeMmOptions" :key="o.id" :label="o.value1" :value="o.id" />
              </el-select>
              <el-select v-model="formData.ribCountId" placeholder="伞骨数量" filterable clearable style="flex: 1">
                <el-option v-for="o in sizeKOptions" :key="o.id" :label="o.value1" :value="o.id" />
              </el-select>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="材料" prop="materialId">
            <el-select v-model="formData.materialId" placeholder="请选择" clearable style="width: 100%">
              <el-option v-for="o in materialOptions" :key="o.id" :label="o.value1" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="单价" prop="price">
            <el-input
              v-model="formData.price"
              placeholder="请输入"
              @input="(val: string) => (formData.price = validateDecimal(val))"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="单位" prop="unit">
            <el-radio-group v-model="formData.unit">
              <el-radio value="支">支</el-radio>
              <el-radio value="打">打</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title">伞架图片</div>
      <el-form-item label="图片" label-width="80px">
        <div class="image-list">
          <div v-for="(item, idx) in visibleImageList" :key="idx" class="image-item">
            <img :src="item.url" class="image-preview" />
            <div class="image-overlay">
              <el-icon class="overlay-icon" @click.stop="handleImagePreview(item.url)">
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
            module-path="zs/umbrella-frame"
            :accept="uploadData.accept"
            :before-upload="beforeUpload"
            :on-success="uploadSuccess"
            :on-error="uploadError"
          >
            <el-icon class="img-uploader-icon"><Plus /></el-icon>
          </bz-upload>
        </div>
      </el-form-item>

      <div class="section-title">绑定材料</div>
      <el-table :data="visibleBoundRows" style="width: 100%">
        <el-table-column label="材料分类" min-width="120">
          <template #default="{ row }">
            <el-select
              v-model="row.materialType"
              placeholder="请选择"
              style="width: 100%"
              filterable
              @change="onBoundTypeChange(row)"
            >
              <el-option v-for="opt in getBoundTypeOptions()" :key="opt" :label="opt" :value="opt" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="材料名称" min-width="130">
          <template #default="{ row }">
            <el-select
              v-model="row.materialName"
              placeholder="请选择"
              style="width: 100%"
              filterable
              :disabled="!row.materialType"
              @change="onBoundNameChange(row)"
            >
              <el-option
                v-for="opt in getBoundNameOptions(row.materialType)"
                :key="opt.id"
                :label="opt.name"
                :value="opt.name"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="尺寸" min-width="100">
          <template #default="{ row }">{{ row.materialSize || '-' }}</template>
        </el-table-column>
        <el-table-column label="数量" min-width="90">
          <template #default="{ row }">
            <el-input v-model="row.qty" placeholder="请输入" />
          </template>
        </el-table-column>
        <el-table-column label="价格(元)" min-width="100">
          <template #default="{ row }">{{ calcPrice(row) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="70" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDeleteBoundRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" class="add-row-btn" @click="addBoundRow">添加材料</el-button>
    </el-form>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
    </template>
    <el-image-viewer
      v-if="previewVisible"
      :url-list="[previewImageUrl]"
      :initial-index="0"
      hide-on-click-modal
      @close="previewVisible = false"
    />
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, useAttrs, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { imageFileType } from '@/constant/file-type'
import { validateDecimal } from '@/utils/validate'
import { saveUmbrellaFrame, getUmbrellaFrameDetail } from '@/api/zs/material/umbrella-frame'
import { getMaterialCategoryList, getMaterialPage } from '@/api/zs/material/material'
import { listByNodeKey } from '@/api/zs/base-info/base-data'

const attrs = useAttrs()
const { isEdit, rowData, onDestroy, callback } = attrs as any

const dialogVisible = ref(true)
const formRef = ref()
const loading = ref(false)

const formData = reactive<any>({
  id: null,
  functionId: null,
  typeId: null,
  lengthId: null,
  diameterId: null,
  ribCountId: null,
  materialId: null,
  price: null,
  unit: '支',
  specificAttribute: ''
})

const detailLoading = ref(false)
const imageList = ref<any[]>([])
const visibleImageList = computed(() => imageList.value.filter((img: any) => !img.isDeleted))
const previewVisible = ref(false)
const previewImageUrl = ref('')
const uploadData = reactive({
  accept: imageFileType.join()
})
const boundMaterialRows = ref<any[]>([])
const boundCategoryOptions = ref<string[]>([])
const boundCategoryMap = ref<Record<string, number>>({})
const boundMaterialsCache = ref<Record<number, any[]>>({})

const functionOptions = ref<any[]>([])
const typeOptions = ref<any[]>([])
const sizeCmOptions = ref<any[]>([])
const sizeMmOptions = ref<any[]>([])
const sizeKOptions = ref<any[]>([])
const materialOptions = ref<any[]>([])

const rules = {
  functionId: [{ required: true, message: '请选择功能', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择类型', trigger: 'change' }],
  lengthId: [{ required: true, message: '请选择伞架长度', trigger: 'change' }],
  materialId: [{ required: true, message: '请选择材料', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }]
}

const loadDictOptions = async () => {
  const keys = [
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_FUNCTION', target: functionOptions },
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_TYPE', target: typeOptions },
    { key: 'FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH', target: sizeCmOptions },
    { key: 'FIELD_MGMT_SIZE_MIDDLE_POLE_DIAMETER', target: sizeMmOptions },
    { key: 'FIELD_MGMT_SIZE_RIB_COUNT', target: sizeKOptions },
    { key: 'FIELD_MGMT_UMBRELLA_FRAME_MATERIAL', target: materialOptions }
  ]
  await Promise.allSettled(
    keys.map(async ({ key, target }) => {
      try {
        const { code, data } = await listByNodeKey({ nodeKey: key })
        if (code === 200) target.value = data || []
      } catch {
        target.value = []
      }
    })
  )
}

const loadBoundOptions = async () => {
  const { code, data } = await getMaterialCategoryList()
  if (code === 200) {
    const cats = data || []
    boundCategoryOptions.value = cats.map((c: any) => c.name)
    boundCategoryMap.value = Object.fromEntries(cats.map((c: any) => [c.name, c.id]))
  }
}

const fetchMaterialsByCategory = async (categoryName: string) => {
  const categoryId = boundCategoryMap.value[categoryName]
  if (!categoryId) return
  const { code, data } = await getMaterialPage({ categoryId, pageNum: 1, pageSize: 1000 })
  if (code === 200) {
    boundMaterialsCache.value[categoryId] = data?.list || []
  }
}

const getBoundTypeOptions = () => {
  return boundCategoryOptions.value
}

const getBoundNameOptions = (category: string) => {
  if (!category) return []
  const categoryId = boundCategoryMap.value[category]
  if (!categoryId) return []
  return boundMaterialsCache.value[categoryId] || []
}

const onBoundTypeChange = async (row: any) => {
  row.materialName = ''
  row.materialId = null
  row.materialSize = ''
  row._unitPrice = ''
  if (row.materialType) {
    await fetchMaterialsByCategory(row.materialType)
  }
}

const onBoundNameChange = (row: any) => {
  const categoryId = boundCategoryMap.value[row.materialType]
  const materials = categoryId ? boundMaterialsCache.value[categoryId] || [] : []
  const record = materials.find((r: any) => r.name === row.materialName)
  if (record) {
    row.materialId = record.id
    row.materialSize = record.size || ''
    row._unitPrice = record.price ?? ''
  }
}

const calcPrice = (row: any) => {
  const cost = parseFloat(row._unitPrice)
  const qty = parseFloat(row.qty)
  if (isNaN(cost) || isNaN(qty)) return '-'
  return (cost * qty).toFixed(2)
}

const visibleBoundRows = computed(() => boundMaterialRows.value.filter((r: any) => !r.isDeleted))

const handleDeleteBoundRow = (row: any) => {
  row.isDeleted = 1
}

const addBoundRow = () => {
  boundMaterialRows.value.push({
    materialType: '',
    materialName: '',
    materialId: null,
    materialSize: '',
    qty: 1,
    _unitPrice: ''
  })
}

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
  ElMessage.success('上传成功')
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

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    ElMessage.warning('请填写所有必填项')
    return
  }
  loading.value = true
  try {
    const payload = {
      ...formData,
      images: imageList.value
        .filter((img: any) => img.isDeleted === 1 || img.storageId)
        .map((img: any) => ({
          ...(img.id && { id: img.id }),
          storageId: img.storageId,
          url: img.url,
          ...(img.isDeleted === 1 && { isDeleted: 1 })
        })),

      materials: [
        ...boundMaterialRows.value
          .filter((r: any) => r.materialId && !r.isDeleted)
          .map(({ id, materialId, qty }: any) => ({ id: id || undefined, materialId, quantity: Number(qty) })),
        ...boundMaterialRows.value
          .filter((r: any) => r.id && r.isDeleted)
          .map(({ id, materialId, qty }: any) => ({ id, materialId, quantity: Number(qty), isDeleted: 1 }))
      ]
    }
    const { code, message } = await saveUmbrellaFrame(payload)
    if (code !== 200) return ElMessage.warning(message)
    ElMessage.success('保存成功')
    callback?.()
    onDestroy()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (isEdit) {
    detailLoading.value = true
  }
  await Promise.all([loadDictOptions(), loadBoundOptions()])
  if (isEdit && rowData) {
    const { code, data } = await getUmbrellaFrameDetail({ id: rowData.id })
    if (code === 200 && data) {
      Object.assign(formData, {
        id: data.id,
        functionId: data.functionId,
        typeId: data.typeId,
        lengthId: data.lengthId,
        diameterId: data.diameterId,
        ribCountId: data.ribCountId,
        materialId: data.materialId,
        price: data.price,
        unit: data.unit || '支',
        specificAttribute: data.specificAttribute || ''
      })
      imageList.value = Array.isArray(data.images)
        ? data.images
            .filter((img: any) => img.isDeleted !== 1)
            .map((img: any) => ({ id: img.id, storageId: img.storageId, url: img.url }))
        : []
      if (data.materials && data.materials.length) {
        boundMaterialRows.value = data.materials.map((m: any) => ({
          id: m.id,
          materialType: m.materialCategoryName || '',
          materialName: m.materialName || '',
          materialId: m.materialId,
          materialSize: m.size || '',
          qty: m.quantity || 1,
          _unitPrice: m.price ?? ''
        }))
        const categoryNames = [
          ...new Set<string>(data.materials.map((m: any) => m.materialCategoryName).filter(Boolean))
        ]
        await Promise.all(categoryNames.map((name: string) => fetchMaterialsByCategory(name)))
      }
    }
    detailLoading.value = false
  }
  nextTick(() => formRef.value?.clearValidate())
})
</script>

<style lang="scss" scoped>
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  padding-left: 8px;
  border-left: 3px solid #409eff;
  margin: 16px 0 12px;
  line-height: 1.4;
}
.add-row-btn {
  width: 100%;
  margin-top: 8px;
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
</style>
