<template>
  <el-select
    :model-value="modelValue"
    :multiple="multiple"
    filterable
    :placeholder="placeholder"
    class="zs-dict-select"
    popper-class="zs-dict-select-popper"
    clearable
    @visible-change="onVisibleChange"
    @update:model-value="onUpdate"
  >
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="getOptionValue(item)">
      <template v-if="editingValue === item.value">
        <div class="dict-select-editing-row" @click.stop>
          <el-input v-model="editingNewName" class="dict-select-editing-input" />
          <div class="dict-select-editing-action">
            <el-icon class="dict-select-confirm" @click.stop="handleConfirmEdit"><Check /></el-icon>
            <el-icon class="dict-select-cancel" @click.stop="handleCancelEdit"><Close /></el-icon>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="dict-select-item-row">
          <span>{{ item.label }}</span>
          <span v-if="editable" class="dict-select-actions" @mousedown.stop>
            <el-icon class="dict-select-edit" @click.stop="handleEdit(item)" title="重命名"><Edit /></el-icon>
            <el-icon class="dict-select-delete" @click.stop="handleDelete(item)" title="删除"><Delete /></el-icon>
          </span>
        </div>
      </template>
    </el-option>
    <template #footer>
      <template v-if="addable">
        <el-button v-if="!isAdding" text bg size="small" @click="isAdding = true">添加选项</el-button>
        <template v-else>
          <el-input v-model="newName" placeholder="请输入" @keyup.enter="handleConfirmAdd" />
          <div class="footer-actions">
            <el-button type="primary" size="small" @click="handleConfirmAdd">确认</el-button>
            <el-button size="small" @click="handleCancelAdd">取消</el-button>
          </div>
        </template>
      </template>
    </template>
  </el-select>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import { getHookFactory } from './hooks'

const props = withDefaults(
  defineProps<{
    modelValue: string[] | string | number | null
    nodeKey?: string
    nodeId?: string
    multiple?: boolean
    placeholder?: string
    hookType?: string
    editable?: boolean
    addable?: boolean
    useIdValue?: boolean
  }>(),
  {
    multiple: false,
    placeholder: '请选择',
    hookType: 'base-data',
    editable: false,
    addable: true,
    useIdValue: false
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'change', value: any, label: string): void
}>()

const getOptionValue = (item: any) => (props.useIdValue ? item.value : item.label)

const onUpdate = (val: any) => {
  emit('update:modelValue', val)
  const opt = props.useIdValue ? options.value.find(o => o.value === val) : options.value.find(o => o.label === val)
  emit('change', val, opt?.label || '')
}

const hookFactory = getHookFactory(props.hookType)
const { options, loadOptions, addOption, editOption, deleteOption } = hookFactory({
  nodeKey: props.nodeKey,
  nodeId: props.nodeId
})

const editingValue = ref<any>(null)
const editingNewName = ref('')

const isAdding = ref(false)
const newName = ref('')

const handleConfirmAdd = async () => {
  const label = newName.value.trim()
  if (!label) return
  const ok = await addOption(label)
  if (ok) {
    ElMessage.success('添加成功')
    await loadOptions()
    const newOpt = options.value.find(o => o.label === label)
    if (newOpt) {
      const selectedVal = getOptionValue(newOpt)
      const newVal = props.multiple
        ? [...(Array.isArray(props.modelValue) ? props.modelValue : []), selectedVal]
        : selectedVal
      emit('update:modelValue', newVal)
      emit('change', selectedVal, newOpt.label)
    }
    handleCancelAdd()
  }
}

const handleCancelAdd = () => {
  newName.value = ''
  isAdding.value = false
}

const handleEdit = (item: { label: string; value: any }) => {
  editingValue.value = item.value
  editingNewName.value = item.label
}

const handleConfirmEdit = async () => {
  const newVal = editingNewName.value.trim()
  if (!newVal) return
  const target = options.value.find(o => o.value === editingValue.value)
  if (!target) return
  const ok = await editOption(target.id, newVal)
  if (ok) {
    ElMessage.success('修改成功')
    await loadOptions()
    handleCancelEdit()
  }
}

const handleCancelEdit = () => {
  editingValue.value = null
  editingNewName.value = ''
}

const handleDelete = async (item: { label: string; value: any }) => {
  await ElMessageBox.confirm(`确定删除 "${item.label}" ？`, '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  const target = options.value.find(o => o.value === item.value)
  if (!target) return
  await deleteOption(target.id)
  ElMessage.success('已删除')
  await loadOptions()
}

const onVisibleChange = (visible: boolean) => {
  if (!visible) {
    editingValue.value = null
    editingNewName.value = ''
    isAdding.value = false
    newName.value = ''
  }
}

const reset = () => {
  isAdding.value = false
  newName.value = ''
}

onMounted(loadOptions)
defineExpose({ options, reset, reload: loadOptions })
</script>

<style lang="scss" scoped>
.zs-dict-select {
  width: 100%;
}

.footer-actions {
  margin-top: 8px;
}
</style>

<style lang="scss">
.zs-dict-select-popper {
  .el-select-dropdown__item {
    padding: 0 12px;
  }
}
.dict-select-editing-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  height: 34px;

  .dict-select-editing-input {
    width: 170px;
  }

  .dict-select-editing-action {
    display: flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      font-size: 16px;
      cursor: pointer;

      &.dict-select-confirm {
        color: #67c23a;
        &:hover {
          color: #85ce61;
        }
      }

      &.dict-select-cancel {
        color: #f56c6c;
        &:hover {
          color: #f78989;
        }
      }
    }
  }
}

.dict-select-item-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  .dict-select-actions {
    display: flex;
    align-items: center;
    gap: 6px;
    opacity: 0;
    transition: opacity 0.15s;
  }

  &:hover .dict-select-actions {
    opacity: 1;
  }

  .dict-select-edit {
    font-size: 14px;
    color: #409eff;
    cursor: pointer;

    &:hover {
      color: #66b1ff;
    }
  }

  .dict-select-delete {
    font-size: 14px;
    color: #f56c6c;
    cursor: pointer;

    &:hover {
      color: #f78989;
    }
  }
}
</style>
