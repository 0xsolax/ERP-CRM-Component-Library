<template>
  <div class="dict-col">
    <div class="col-header">
      <div class="col-title">{{ title }}</div>
      <el-button type="primary" @click="addRow">添加一行</el-button>
    </div>
    <div class="table-area">
      <el-table :data="rows" style="width: 100%">
        <el-table-column :label="title" min-width="140" align="left">
          <template #default="{ row }">
            <el-input v-if="row._editing" v-model="row.label" placeholder="请输入" @keyup.enter="handleConfirm(row)" />
            <span v-else>{{ row.label }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="{ row, $index }">
            <template v-if="row._editing">
              <el-button type="primary" link @click="handleConfirm(row)">确定</el-button>
              <el-button type="primary" link @click="handleCancel(row, $index)">取消</el-button>
            </template>
            <template v-else-if="row.value2 === '1'" />
            <template v-else>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="deleteRow(row, $index)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="col-total">共 {{ rows.length }} 条</div>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps({
  title: { type: String, default: '' },
  rows: { type: Array as () => any[], default: () => [] }
})

const emit = defineEmits(['save', 'delete'])

const addRow = () => {
  props.rows.push({ label: '', _editing: true, _isNew: true })
}

const deleteRow = (row: any, index: number) => {
  emit('delete', row, index)
}

const handleEdit = (row: any) => {
  row._originLabel = row.label
  row._editing = true
}

const handleConfirm = (row: any) => {
  if (!row.label?.trim()) return
  emit('save', row)
}

const handleCancel = (row: any, index: number) => {
  if (row._isNew) {
    props.rows.splice(index, 1)
  } else {
    row.label = row._originLabel
    row._editing = false
    delete row._originLabel
  }
}
</script>

<style lang="scss" scoped>
.dict-col {
  display: flex;
  flex-direction: column;
  min-height: 0;

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

  .table-area {
    flex: 1;
    min-height: 0;
    overflow: hidden;
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
