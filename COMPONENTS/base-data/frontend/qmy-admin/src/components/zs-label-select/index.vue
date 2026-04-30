<template>
  <el-select
    :model-value="modelValue"
    :multiple="multiple"
    filterable
    :placeholder="placeholder"
    class="zs-label-select"
    popper-class="zs-label-select-popper"
    clearable
    @visible-change="onVisibleChange"
    @update:model-value="onUpdate"
  >
    <el-option
      v-if="showFallback"
      :key="'__fallback__'"
      :label="fallbackLabel"
      :value="modelValue"
      style="display: none"
    />
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
      <template v-if="editingValue === item.value">
        <div class="label-select-editing-row" @click.stop>
          <el-input v-model="editingNewName" class="label-select-editing-input" />
          <div class="label-select-editing-action">
            <el-icon class="label-select-confirm" @click.stop="handleConfirmEdit"><Check /></el-icon>
            <el-icon class="label-select-cancel" @click.stop="handleCancelEdit"><Close /></el-icon>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="label-select-item-row">
          <span>{{ item.label }}</span>
          <span v-if="showEdit || showDelete" class="label-select-actions" @mousedown.stop>
            <el-icon v-if="showEdit" class="label-select-edit" @click.stop="handleEdit(item)" title="重命名">
              <Edit />
            </el-icon>
            <el-icon v-if="showDelete" class="label-select-delete" @click.stop="handleDelete(item)" title="删除">
              <Delete />
            </el-icon>
          </span>
        </div>
      </template>
    </el-option>
    <template #footer>
      <el-button v-if="!isAdding" text bg size="small" @click="isAdding = true">添加选项</el-button>
      <template v-else>
        <el-input v-model="newName" placeholder="请输入" @keyup.enter="handleConfirmAdd" />
        <div class="footer-actions">
          <el-button type="primary" size="small" @click="handleConfirmAdd">确认</el-button>
          <el-button size="small" @click="handleCancelAdd">取消</el-button>
        </div>
      </template>
    </template>
  </el-select>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
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
    localOnly?: boolean
    fallbackLabel?: string
    resultFilterFn?: (item: any) => boolean
    showEdit?: boolean
    showDelete?: boolean
    addExtraData?: Record<string, any>
  }>(),
  {
    multiple: false,
    placeholder: '请选择',
    hookType: 'base-data',
    localOnly: false,
    fallbackLabel: '',
    showEdit: false,
    showDelete: false
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'change', value: any, label: string): void
}>()

const showFallback = computed(() => {
  if (!props.fallbackLabel || !props.modelValue) return false
  return !options.value.some(o => o.value === props.modelValue)
})

const onUpdate = (val: any) => {
  emit('update:modelValue', val)
  const opt = options.value.find(o => o.value === val)
  emit('change', val, opt?.label || '')
}

const hookFactory = getHookFactory(props.hookType)
const { options, loadOptions, addOption, editOption, deleteOption } = hookFactory({
  nodeKey: props.nodeKey,
  nodeId: props.nodeId,
  resultFilterFn: props.resultFilterFn,
  addExtraData: props.addExtraData
})

const editingValue = ref<any>(null)
const editingNewName = ref('')

const isAdding = ref(false)
const newName = ref('')

const handleConfirmAdd = async () => {
  const label = newName.value.trim()
  if (!label) return
  if (props.localOnly) {
    const localId = `local_${Date.now()}`
    options.value.push({ id: localId, label, value: localId })
    const newVal = props.multiple ? [...(Array.isArray(props.modelValue) ? props.modelValue : []), localId] : localId
    emit('update:modelValue', newVal)
    emit('change', localId, label)
    handleCancelAdd()
    return
  }
  const ok = await addOption(label)
  if (ok) {
    ElMessage.success('添加成功')
    await loadOptions()
    const newOpt = options.value.find(o => o.label === label)
    if (newOpt) {
      const newVal = props.multiple
        ? [...(Array.isArray(props.modelValue) ? props.modelValue : []), newOpt.value]
        : newOpt.value
      emit('update:modelValue', newVal)
      emit('change', newOpt.value, newOpt.label)
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

const isLocalItem = (id: any) => typeof id === 'string' && id.startsWith('local_')

const handleConfirmEdit = async () => {
  const newVal = editingNewName.value.trim()
  if (!newVal) return
  const target = options.value.find(o => o.value === editingValue.value)
  if (!target) return
  if (isLocalItem(target.id)) {
    target.label = newVal
    handleCancelEdit()
    return
  }
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
  if (isLocalItem(target.id)) {
    options.value = options.value.filter(o => o.id !== target.id)
    if (props.multiple) {
      const arr = Array.isArray(props.modelValue) ? props.modelValue : []
      const filtered = arr.filter(v => v !== target.value)
      emit('update:modelValue', filtered)
    } else if (props.modelValue === target.value) {
      emit('update:modelValue', '')
    }
    return
  }
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

onMounted(loadOptions)
defineExpose({ options, reload: loadOptions })
</script>

<style lang="scss" scoped>
.zs-label-select {
  width: 100%;
}

.footer-actions {
  margin-top: 8px;
}
</style>

<style lang="scss">
.zs-label-select-popper {
  .el-select-dropdown__item {
    padding: 0 12px 0 12px;

    &.is-selected::after {
      transition: opacity 0.15s;
    }

    &:hover.is-selected::after {
      opacity: 0;
    }
  }
}
.label-select-editing-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  height: 34px;

  .label-select-editing-input {
    width: 170px;
  }

  .label-select-editing-action {
    display: flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      font-size: 16px;
      cursor: pointer;

      &.label-select-confirm {
        color: #67c23a;
        &:hover {
          color: #85ce61;
        }
      }

      &.label-select-cancel {
        color: #f56c6c;

        &:hover {
          color: #f78989;
        }
      }
    }
  }
}

.label-select-item-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  .label-select-actions {
    display: flex;
    align-items: center;
    gap: 6px;
    opacity: 0;
    transition: opacity 0.15s;
  }

  &:hover .label-select-actions {
    opacity: 1;
  }

  .label-select-edit {
    font-size: 14px;
    color: #409eff;
    cursor: pointer;

    &:hover {
      color: #66b1ff;
    }
  }

  .label-select-delete {
    font-size: 14px;
    color: #f56c6c;
    cursor: pointer;

    &:hover {
      color: #f78989;
    }
  }
}
</style>
