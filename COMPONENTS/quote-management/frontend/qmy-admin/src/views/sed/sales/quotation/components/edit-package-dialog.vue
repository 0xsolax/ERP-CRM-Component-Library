<template>
  <el-dialog v-model="dialogVisible" title="修改包装" width="1100px" @close="onDestroy">
    <el-table :data="visiblePackageList" border style="width: 100%">
      <el-table-column label="包材类型" align="center" width="130">
        <template #default="{ row }">
          <el-select v-model="row.type" placeholder="请选择" style="width: 100%" @change="handlePackageTypeChange(row)">
            <el-option v-for="item in packingTypeList" :key="item" :label="item" :value="item" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="包材名称" align="center" width="220">
        <template #default="{ row }">
          <div style="display: flex; gap: 4px; align-items: center">
            <RemoteAutocomplete
              v-model="row.name"
              placeholder="请选择"
              style="flex: 1"
              :fetch-fn="(params: any) => fetchPackingList({ ...params, type: row.type })"
              @select="(item: any) => handlePackingSelect(row, item)"
              @clear="() => handlePackingClear(row)"
            />
            <el-button type="primary" link @click="handleAddPackingName(row)">新增</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="包材尺寸" align="center" width="130">
        <template #default="{ row }">
          <el-input v-model="row.size" placeholder="请输入" style="width: 100px" />
        </template>
      </el-table-column>
      <el-table-column align="center" width="120">
        <template #header>
          <span>装箱数</span>
          <el-tooltip content="一箱可以装几个型号" placement="top">
            <el-icon style="margin-left: 4px; vertical-align: middle; margin-bottom: 3px">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </template>
        <template #default="{ row }">
          <el-input
            v-model="row.packingNum"
            placeholder="请输入"
            style="width: 80px"
            @input="(val: string) => (row.packingNum = validateInteger(val))"
          />
        </template>
      </el-table-column>
      <el-table-column label="成本价" align="center" width="130">
        <template #default="{ row }">
          <el-input
            v-model="row.packingCost"
            placeholder="请输入"
            style="width: 100px"
            @input="(val: string) => (row.packingCost = validateDecimal(val, 2))"
          />
        </template>
      </el-table-column>
      <el-table-column label="附件" align="center" min-width="200">
        <template #default="{ row }">
          <div class="upload-cell">
            <div v-if="getVisibleAttachments(row).length" class="attachment-list">
              <el-tooltip
                v-for="(att, index) in getVisibleAttachments(row)"
                :key="index"
                :content="att.fileName"
                placement="top"
              >
                <el-tag closable class="attachment-tag" @close="handleRemoveAttachment(row, att)">
                  <span class="tag-text">{{ att.fileName }}</span>
                </el-tag>
              </el-tooltip>
            </div>
            <bz-upload
              module-path="sed/sales"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="(res: any, result) => handleUploadSuccess(res, result, row)"
            >
              <el-button size="small">选择文件</el-button>
            </bz-upload>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="80">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleRemove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-button type="primary" style="width: 100%; margin-top: 10px" @click="handleAdd">添加包材</el-button>

    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, useAttrs, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { dynamic } from '@bzlab/bz-core'
import { QuestionFilled } from '@element-plus/icons-vue'
import RemoteAutocomplete from '@/components/remote-autocomplete/index.vue'
import { getSedPackingList, getSedPackingTypeList } from '@/api/sed/product/packing'
import { validateInteger, validateDecimal } from '@/utils/validate'
import AddPackingNameDialog from './add-packing-name-dialog.vue'

const attrs = useAttrs()
const { rowData, onDestroy, callback, isExisting } = attrs as any
const dialogVisible = ref(true)

const packingTypeList = ref<string[]>([])

const packageList = ref<any[]>([])

// 过滤掉已删除的项
const visiblePackageList = computed(() => packageList.value.filter(item => item.isDeleted !== 1))

const loadPackingTypeList = async () => {
  const { code, data } = await getSedPackingTypeList({})
  if (code === 200) packingTypeList.value = data || []
}

const initPackageList = () => {
  if (rowData?.packages?.length) {
    packageList.value = rowData.packages.map((pkg: any) => ({
      id: pkg.id,
      packageId: pkg.packageId,
      type: pkg.type || '',
      name: pkg.name || '',
      size: pkg.size || '',
      packingNum: pkg.packingNum || pkg.qty || '',
      packingCost: pkg.packingCost || '',
      isDeleted: pkg.isDeleted,
      attachmentList: (pkg.attachmentList || []).map((att: any) => ({
        ...att,
        isExisting: true
      })),
      isExisting: isExisting
    }))
  } else {
    packageList.value = [{ type: '', name: '', size: '', packingNum: '', packingCost: '', attachmentList: [] }]
  }
  console.log('packageList.value', packageList.value)
}

const fetchPackingList = async ({ pageNum, pageSize, keyword, type }: any) => {
  const { code, data } = await getSedPackingList({
    pageNum,
    pageSize,
    name: keyword || undefined,
    type: type || undefined
  })

  if (code !== 200) return []
  const list = data?.list || []
  const addedPackageIds = new Set(
    packageList.value.filter(pkg => pkg.isDeleted !== 1 && pkg.packageId).map(pkg => pkg.packageId)
  )
  // 过滤掉已添加的包材
  return list.filter((item: any) => !addedPackageIds.has(item.id))
}

const handlePackingSelect = (row: any, item: any) => {
  console.log('handlePackingSelect', item)
  row.packageId = item.id
  row.name = item.name
  row.size = item.size || ''
}

const handlePackingClear = (row: any) => {
  row.packageId = undefined
  row.name = ''
  row.size = ''
}

const handleAddPackingName = (row: any) => {
  dynamic.show({
    id: 'addPackingNameDialog',
    el: '#app',
    data: {
      type: row.type,
      callback: () => {}
    },
    render: AddPackingNameDialog
  })
}

const handlePackageTypeChange = (row: any) => {
  row.packageId = undefined
  row.name = ''
  row.size = ''
  row.packingNum = ''
  row.packingCost = ''
}

const handleAdd = () => {
  packageList.value.push({
    type: '',
    name: '',
    size: '',
    packageId: '',
    packingNum: '',
    packingCost: '',
    attachmentList: []
  })
}

const handleRemove = (row: any) => {
  console.log('row', row)
  if (row.isExisting) {
    row.isDeleted = 1
    if (row.attachmentList && row.attachmentList.length > 0) {
      row.attachmentList.forEach((att: any) => {
        if (att.isExisting) {
          att.isDeleted = 1
        }
      })
    }
  } else {
    const index = packageList.value.indexOf(row)
    if (index > -1) {
      packageList.value.splice(index, 1)
    }
  }
}

const beforeUpload = (file: any) => {
  const size = file.size / 1024 / 1024
  const allowedTypes = ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.png', '.jpg', '.jpeg']
  if (!allowedTypes.includes(file.fileType.toLowerCase())) {
    ElMessage.warning('请上传pdf、doc、docx、xls、xlsx、png、jpg、jpeg格式的文件')
    return false
  } else if (size > 10) {
    ElMessage.warning('请上传50M以内的文件')
    return false
  }
  return true
}

const handleUploadSuccess = (res, result, row) => {
  console.log('handleUploadSuccess', res, result, row)
  if (!row.attachmentList) row.attachmentList = []
  row.attachmentList.push({
    storageId: res.data?.id,
    url: res.data?.url,
    fileName: result?.name || ''
  })
  ElMessage.success('上传成功')
}

const handleRemoveAttachment = (row: any, att: any) => {
  if (att.isExisting) {
    att.isDeleted = 1
  } else {
    const index = row.attachmentList.indexOf(att)
    if (index > -1) {
      row.attachmentList.splice(index, 1)
    }
  }
}

// 获取可见的附件列表
const getVisibleAttachments = (row: any) => {
  if (!row.attachmentList) return []
  return row.attachmentList.filter((att: any) => att.isDeleted !== 1)
}

const handleConfirm = () => {
  console.log('packageList', packageList.value)

  const visibleItems = packageList.value.filter(item => item.isDeleted !== 1)
  for (let i = 0; i < visibleItems.length; i++) {
    const pkg = visibleItems[i]
    if (!pkg.type) {
      return ElMessage.warning(`第${i + 1}行包材类型不能为空`)
    }
    if (!pkg.packageId) {
      return ElMessage.warning(`第${i + 1}行包材名称不能为空`)
    }
    if (!pkg.packingNum) {
      return ElMessage.warning(`第${i + 1}行装箱数不能为空`)
    }
    if (!pkg.packingCost) {
      return ElMessage.warning(`第${i + 1}行成本价不能为空`)
    }
  }
  if (callback) {
    ElMessage.success('操作成功')
    console.log('packageList', packageList.value)

    callback(packageList.value)
  }
  dialogVisible.value = false
}

onMounted(() => {
  loadPackingTypeList()
  initPackageList()
})
</script>

<style scoped lang="scss">
.upload-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
  max-width: 180px;
}

.attachment-tag {
  :deep(.el-tag__content) {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.tag-text {
  display: block;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 2;
}
</style>
