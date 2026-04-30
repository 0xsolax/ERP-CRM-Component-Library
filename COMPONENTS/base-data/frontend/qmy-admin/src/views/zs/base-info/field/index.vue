<template>
  <div class="field-manage-page">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane v-for="tab in tabs" :key="tab.id" :label="tab.name" :name="tab.nodeKey">
        <template v-if="tab.nodeKey === FABRIC_USAGE_KEY">
          <div style="display: flex; justify-content: flex-end; margin-bottom: 8px">
            <el-button type="primary" @click="addUsageRow">增加一行</el-button>
          </div>
          <div class="table-area">
            <el-table :data="usageRows" height="100%" style="width: 100%">
              <el-table-column label="尺寸" align="center">
                <template #default="{ row }">
                  <div v-if="row._editing" style="display: flex; align-items: center; gap: 4px">
                    <DictSelect v-model="row.sizeCm" :nodeId="sizeLengthNodeId" style="flex: 1" />
                    <DictSelect v-model="row.sizeK" :nodeId="sizeRibNodeId" style="flex: 1" />
                  </div>
                  <span v-else>{{ [row.sizeCm, row.sizeK].filter(Boolean).join(' ') || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="用量(米)" align="center">
                <template #default="{ row }">
                  <el-input v-if="row._editing" v-model="row.usage" placeholder="请输入用量" />
                  <span v-else>{{ row.usage || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="收缩系数" align="center">
                <template #default="{ row }">
                  <el-input v-if="row._editing" v-model="row.shrink" placeholder="请输入收缩系数" />
                  <span v-else>{{ row.shrink || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="110" align="center" fixed="right">
                <template #default="{ row, $index }">
                  <template v-if="row._editing">
                    <el-button type="primary" link @click="handleUsageConfirm(row)">确定</el-button>
                    <el-button type="primary" link @click="handleUsageCancel(row, $index)">取消</el-button>
                  </template>
                  <template v-else>
                    <el-button type="primary" link @click="handleUsageEdit(row)">编辑</el-button>
                    <el-button type="danger" link @click="deleteUsageRow(row, $index)">删除</el-button>
                  </template>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="col-total">共 {{ usageRows.length }} 条</div>
        </template>

        <template v-else>
          <div :class="getChildren(tab.id).length <= 2 ? 'two-col' : 'three-col'">
            <DictCol
              v-for="child in getChildren(tab.id)"
              :key="child.id"
              :title="child.name"
              :rows="getRows(child.id)"
              @save="row => handleColSave(child, row)"
              @delete="(row, index) => handleColDelete(child, row, index)"
            />
          </div>
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup name="base-info-field">
import { ref, reactive, computed, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DictSelect from './components/dict-select.vue'
import DictCol from './components/dict-col.vue'
import { getBaseDataTreeNodeList, getBaseDataList, saveOrUpdateBaseData } from '@/api/zs/base-info/base-data'

const hasInitialized = ref(true)
const FABRIC_USAGE_KEY = 'FIELD_MGMT_FABRIC_USAGE'

const treeNodes = ref<any[]>([])
const activeTab = ref('')
const dataMap = reactive<Record<string, any[]>>({})
const usageRows = ref<any[]>([])

const tabs = computed(() =>
  treeNodes.value.filter((n: any) => n.level === 2).sort((a: any, b: any) => a.sortNum - b.sortNum)
)

const getChildren = (parentId: string) =>
  treeNodes.value.filter((n: any) => n.parentId === parentId).sort((a: any, b: any) => a.sortNum - b.sortNum)

const getRows = (nodeId: string) => {
  if (!dataMap[nodeId]) dataMap[nodeId] = []
  return dataMap[nodeId]
}

const fabricUsageNode = computed(() => treeNodes.value.find((n: any) => n.nodeKey === FABRIC_USAGE_KEY))

const SIZE_KEY = 'FIELD_MGMT_SIZE'
const sizeLengthNodeId = computed(() => {
  const sizeTab = tabs.value.find((t: any) => t.nodeKey === SIZE_KEY)
  if (!sizeTab) return ''
  return getChildren(sizeTab.id).find((c: any) => c.nodeKey === 'FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH')?.id || ''
})
const sizeRibNodeId = computed(() => {
  const sizeTab = tabs.value.find((t: any) => t.nodeKey === SIZE_KEY)
  if (!sizeTab) return ''
  return getChildren(sizeTab.id).find((c: any) => c.nodeKey === 'FIELD_MGMT_SIZE_RIB_COUNT')?.id || ''
})

const loadTree = async () => {
  const { code, data } = await getBaseDataTreeNodeList({ bizType: 'FIELD_MGMT' })
  if (code === 200) {
    treeNodes.value = data || []
    if (tabs.value.length) {
      activeTab.value = tabs.value[0].nodeKey
      await loadTabData(tabs.value[0])
    }
  }
}

const loadTabData = async (tab: any) => {
  if (tab.nodeKey === FABRIC_USAGE_KEY) {
    const { code, data } = await getBaseDataList({ nodeIds: [tab.id] })
    if (code === 200) {
      usageRows.value = (data || []).map((d: any) => {
        return {
          id: d.id,
          sizeCm: d.value1 || '',
          sizeK: d.value2 || '',
          usage: d.value3 || '',
          shrink: d.value4 || '',
          _editing: false
        }
      })
    }
    return
  }
  const children = getChildren(tab.id)
  if (children.length > 0) {
    const nodeIds = children.map((c: any) => c.id)
    const { code, data } = await getBaseDataList({ nodeIds })
    if (code === 200) {
      for (const child of children) {
        const items = (data || [])
          .filter((d: any) => d.nodeId === child.id)
          .map((d: any) => ({ id: d.id, label: d.value1 || '', value2: d.value2 || null, _editing: false }))
        const rows = getRows(child.id)
        rows.splice(0, rows.length, ...items)
      }
    }
  }
}

const onTabChange = (key: string) => {
  const tab = tabs.value.find((t: any) => t.nodeKey === key)
  if (tab) loadTabData(tab)
}

const reloadCurrentTab = async () => {
  const tab = tabs.value.find((t: any) => t.nodeKey === activeTab.value)
  if (tab) await loadTabData(tab)
}

const handleColSave = async (child: any, row: any) => {
  const payload: any = { nodeId: child.id, value1: row.label.trim() }
  if (row.id) payload.id = row.id
  const { code } = await saveOrUpdateBaseData(payload)
  if (code === 200) {
    ElMessage.success('已保存')
    await reloadCurrentTab()
  }
}

const handleColDelete = async (child: any, row: any, _index: number) => {
  await ElMessageBox.confirm('确定删除该条数据吗？', '提示', { type: 'warning' })
  if (row.id) {
    await saveOrUpdateBaseData({ id: row.id, nodeId: child.id, isDeleted: 1 })
  }
  await reloadCurrentTab()
}

const addUsageRow = () => {
  usageRows.value.push({ sizeCm: '', sizeK: '', usage: '', shrink: '', _editing: true, _isNew: true })
}

const deleteUsageRow = async (row: any, _index: number) => {
  await ElMessageBox.confirm('确定删除该条数据吗？', '提示', { type: 'warning' })
  if (row.id && fabricUsageNode.value) {
    await saveOrUpdateBaseData({ id: row.id, nodeId: fabricUsageNode.value.id, isDeleted: 1 })
  }
  await reloadCurrentTab()
}

const handleUsageEdit = (row: any) => {
  row._origin = { sizeCm: row.sizeCm, sizeK: row.sizeK, usage: row.usage, shrink: row.shrink }
  row._editing = true
}

const handleUsageConfirm = (row: any) => {
  if (!row.sizeCm) {
    ElMessage.warning('请选择尺寸 cm')
    return
  }
  if (!row.sizeK) {
    ElMessage.warning('请选择尺寸 k')
    return
  }
  if (!row.usage) {
    ElMessage.warning('请输入用量')
    return
  }
  if (!row.shrink) {
    ElMessage.warning('请输入收缩系数')
    return
  }
  saveUsageRow(row)
}

const handleUsageCancel = (row: any, index: number) => {
  if (row._isNew) {
    usageRows.value.splice(index, 1)
  } else {
    Object.assign(row, row._origin)
    row._editing = false
    delete row._origin
  }
}

const saveUsageRow = async (row: any) => {
  const nodeId = fabricUsageNode.value?.id
  if (!nodeId) return
  const payload: any = {
    nodeId,
    value1: row.sizeCm,
    value2: row.sizeK,
    value3: row.usage,
    value4: row.shrink
  }
  if (row.id) payload.id = row.id
  const { code } = await saveOrUpdateBaseData(payload)
  if (code === 200) {
    ElMessage.success('已保存')
    await reloadCurrentTab()
  }
}

onMounted(() => {
  loadTree()
})

onActivated(() => {
  if (!hasInitialized.value) {
    loadTree()
  }
  hasInitialized.value = false
})
</script>

<style lang="scss" scoped>
.field-manage-page {
  padding: 5px 15px 15px 15px;
  display: flex;
  flex-direction: column;
  background: #fff;
  min-height: 100%;
  border-radius: 4px;

  :deep(.el-card) {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }
  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
    overflow: hidden;
  }
  :deep(.el-tabs) {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }
  :deep(.el-tabs__content) {
    flex: 1;
    overflow: hidden;
    padding-top: 12px;
  }
  :deep(.el-tab-pane) {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .three-col {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    flex: 1;
    min-height: 0;
  }
  .two-col {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    flex: 1;
    min-height: 0;
  }
  .dict-col {
    display: flex;
    flex-direction: column;
    min-height: 0;
  }
  .table-area {
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }
  .col-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
    flex-shrink: 0;
  }
  .col-title {
    font-weight: 600;
    color: #303133;
  }
  .col-total {
    flex-shrink: 0;
    text-align: right;
    padding-top: 8px;
    font-size: 13px;
    color: #909399;
  }
}
</style>
