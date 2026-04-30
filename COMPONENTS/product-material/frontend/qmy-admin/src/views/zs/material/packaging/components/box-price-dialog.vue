<template>
  <el-dialog v-model="dialogVisible" title="纸箱单价" width="500px" @close="onDestroy">
    <el-table :data="visibleRows">
      <el-table-column label="名称" min-width="180">
        <template #default="{ row }">
          <el-input v-if="row._editing" v-model="row.name" placeholder="请输入名称" />
          <span v-else>{{ row.name || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单价(元)" min-width="130">
        <template #default="{ row }">
          <el-input
            v-if="row._editing"
            v-model="row.price"
            placeholder="请输入单价"
            @input="row.price = validateDecimal(row.price)"
          />
          <span v-else>{{ row.price != null ? '¥' + row.price : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row, $index }">
          <template v-if="row._editing">
            <!-- <el-button type="primary" link @click="confirmEdit(row)">确定</el-button> -->
            <el-button type="primary" link @click="cancelEdit(row, $index)">取消</el-button>
          </template>
          <template v-else>
            <el-button type="primary" link @click="startEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteRow($index)">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <el-button style="width: 100%; margin-top: 8px" type="primary" @click="addRow">增加一行</el-button>
    <template #footer>
      <el-button @click="onDestroy">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, useAttrs } from 'vue'
import { ElMessage } from 'element-plus'
import { getPackagingPage, saveOrUpdateDefaultPaperBox } from '@/api/zs/material/packaging'
import { validateDecimal } from '@/utils/validate'

const attrs = useAttrs()
const { onDestroy } = attrs as any

const dialogVisible = ref(true)
const loading = ref(false)
const rows = ref<any[]>([])

const addRow = () => {
  rows.value.push({
    id: null,
    typeId: null,
    typeName: '纸箱',
    name: '',
    price: null,
    _editing: true,
    _isNew: true
  })
}

const visibleRows = computed(() => rows.value.filter((r: any) => !r._deleted))

const deleteRow = (index: number) => {
  const row = visibleRows.value[index]
  if (row.id) {
    row._deleted = true
  } else {
    const realIndex = rows.value.indexOf(row)
    if (realIndex !== -1) rows.value.splice(realIndex, 1)
  }
}

const startEdit = (row: any) => {
  row._backup = { name: row.name, price: row.price }
  row._editing = true
}

const confirmEdit = (row: any) => {
  delete row._backup
  row._editing = false
}
confirmEdit

const cancelEdit = (row: any, index: number) => {
  if (row._isNew && !row._backup) {
    const realIndex = rows.value.indexOf(visibleRows.value[index])
    if (realIndex !== -1) rows.value.splice(realIndex, 1)
    return
  }
  if (row._backup) {
    row.name = row._backup.name
    row.price = row._backup.price
    delete row._backup
  }
  row._editing = false
}

const loadData = async () => {
  const { code, data } = await getPackagingPage({ pageNum: 1, pageSize: 9999, defaultTypeFlag: 1 })
  if (code === 200) {
    rows.value = (data?.list || []).map((r: any) => ({ ...r, _editing: false }))
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    const payload = rows.value
      .filter((row: any) => !(row._isNew && row._deleted))
      .map((row: any) => {
        const item: any = {
          name: row.name,
          price: row.price != null ? parseFloat(row.price) : null,
          typeId: row.typeId || undefined,
          isDeleted: row._deleted ? 1 : undefined
        }
        if (row.id) item.id = row.id
        if (row.size) item.size = row.size
        return item
      })
    const { code, message } = await saveOrUpdateDefaultPaperBox(payload)
    if (code !== 200) {
      ElMessage.warning(message)
      return
    }
    ElMessage.success('保存成功')
    onDestroy()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
