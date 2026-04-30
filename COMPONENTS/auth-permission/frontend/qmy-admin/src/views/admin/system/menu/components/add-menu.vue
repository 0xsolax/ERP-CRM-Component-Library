<template>
  <el-dialog custom-class="add-menu" v-model="dialogVisible" width="769px" @close="onDestroy">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-row :gutter="35">
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="菜单类型" prop="menuType">
            <el-radio-group v-model="form.menuType">
              <el-radio-button :label="1">目录</el-radio-button>
              <el-radio-button :label="2">菜单</el-radio-button>
              <el-radio-button :label="3">按钮</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="35">
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="form.menuName" placeholder="请输入" clearable />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="上级菜单">
            <tree-select
              v-model="form.parentId"
              :list="treeSelectList"
              @tree-select="handleTreeSelect"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="菜单排序" prop="menuSort">
            <el-input v-model="form.menuSort" placeholder="菜单排序" clearable />
          </el-form-item>
        </el-col>
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 1 || form.menuType === 2"
        >
          <el-form-item label="菜单图标">
            <icon-selector placeholder="请输入菜单图标" v-model="form.menuIcon" />
          </el-form-item>
        </el-col>

        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 1 || form.menuType === 2"
        >
          <el-form-item label="组件名称" prop="menuRoute">
            <el-input v-model="form.menuRoute" placeholder="请输入" clearable />
          </el-form-item>
        </el-col>
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 1 || form.menuType === 2"
        >
          <el-form-item label="组件" prop="menuComponents">
            <el-input v-model="form.menuComponents" placeholder="请输入" clearable />
          </el-form-item>
        </el-col>
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 1 || form.menuType === 2"
        >
          <el-form-item label="组件路径" prop="menuUrl">
            <el-input v-model="form.menuUrl" placeholder="请输入" clearable />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="是否显示" prop="hiddenFlag">
            <el-select v-model="form.hiddenFlag" placeholder="请选择" style="width: 100%">
              <el-option label="显示" :value="1" />
              <el-option label="隐藏" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12" class="mb10">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
              <el-option label="启用" :value="0" />
              <el-option label="禁用" :value="1" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 2 || form.menuType === 3"
        >
          <el-form-item label="权限标识">
            <el-input v-model="form.menuCode" placeholder="请输入" clearable />
          </el-form-item>
        </el-col>
        <el-col
          :xs="24"
          :sm="12"
          :md="12"
          :lg="12"
          :xl="12"
          class="mb10"
          v-if="form.menuType === 2 || form.menuType === 3"
        >
          <el-form-item label="缓存">
            <el-select v-model="form.cache" placeholder="请选择" style="width: 100%">
              <el-option label="禁用" :value="0" />
              <el-option label="启用" :value="1" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onDestroy">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref, useAttrs } from 'vue'
import iconSelector from '@/components/icon-selector/index.vue'
import treeSelect from '@/components/tree-select/index.vue'
import { addMenu, updateMenu } from '@/api/admin/auth/menu'
import { ElMessage } from 'element-plus'
import { filter, forEachTree } from '@/utils'

interface DialogProps {
  title?: string
  isAdd?: boolean
  rowData?: any
  tableData?: any
  callback?: () => Promise<any>
}

defineProps(['onDestroy'])

const { isAdd, rowData, tableData, callback } = useAttrs() as DialogProps
const dialogVisible = ref(true)
const formRef = ref<any>(null)
const treeSelectList = ref([])

const form = reactive({
  menuType: 1,
  menuName: null,
  parentId: 0,
  menuCode: null,
  menuSort: 0,
  menuIcon: 'Menu',
  menuRoute: null,
  menuUrl: null,
  hiddenFlag: 1, // 默认显示
  status: 0,
  menuComponents: null,
  remarks: null,
  cache: 0
})

const rules = reactive({
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'blur' }],
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuSort: [{ required: true, message: '请输入菜单排序', trigger: 'blur' }],
  menuRoute: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
  menuUrl: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  menuComponents: [{ required: true, message: '请输入组件地址', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  cache: [{ required: true, message: '请选择缓存', trigger: 'blur' }]
})

const setMenuTreeList = data => {
  const filterMenu = filter(data, item => {
    return item.menuType !== 3
  })
  let childrenName = 'childTreeList'
  forEachTree(filterMenu, item => {
    if (item[childrenName]) {
      item.label = item.menuName
      item.value = item.id
      item.children = item[childrenName]
    }
  })
  console.log('filterMenu', filterMenu)
  ;(treeSelectList.value as any) = filterMenu
}

const onMounted = async () => {
  setMenuTreeList(tableData)
  if (!isAdd) {
    form.menuType = rowData.menuType
    form.menuName = rowData.menuName
    form.parentId = rowData.parentId
    form.menuCode = rowData.menuCode
    form.menuSort = rowData.menuSort
    form.menuIcon = rowData.menuIcon
    form.menuRoute = rowData.menuRoute
    form.menuUrl = rowData.menuUrl
    form.hiddenFlag = rowData.hiddenFlag
    form.status = rowData.status
    form.cache = rowData.cache
    form.menuComponents = rowData.menuComponents
  }
}

onMounted()

const handleTreeSelect = data => {
  form.parentId = data.id
}

const handleSubmit = async () => {
  await formRef.value.validate()
  const reqBody = {
    id: isAdd ? undefined : rowData.id,
    createDefaultButton: false,
    menuType: form.menuType,
    menuName: form.menuName,
    parentId: form.parentId,
    menuCode: form.menuCode,
    menuSort: form.menuSort,
    menuIcon: form.menuIcon,
    menuRoute: form.menuRoute,
    menuUrl: form.menuUrl,
    hiddenFlag: form.hiddenFlag,
    status: form.status,
    menuComponents: form.menuComponents,
    remarks: form.remarks,
    cache: form.cache
  }

  const { code, message } = isAdd ? await addMenu(reqBody) : await updateMenu(reqBody)
  if (code !== 200) return ElMessage.warning(message)
  dialogVisible.value = false
  callback!()
}
</script>

<style lang="scss" scoped>
.add-menu {
  :deep(.el-input-group__prepend) {
    padding: 0 10px;
  }
}
</style>
