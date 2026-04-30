<template>
  <div class="role-add-container">
    <h2 class="page-title">{{ isEdit ? '权限详情' : '添加角色' }}</h2>

    <div class="permission-section">
      <div class="section-title">角色详情</div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" inline class="role-form">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入" style="width: 300px" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
        </el-form-item>
      </el-form>

      <div class="permission-table-section">
        <el-table
          :data="permissionData"
          border
          style="width: 100%"
          :span-method="objectSpanMethod"
          :show-header="false"
        >
          <el-table-column width="150" align="center">
            <template #default="{ row }">
              <div v-if="row.isModule" style="font-weight: bold">
                <el-checkbox
                  v-model="row.module.checked"
                  :disabled="row.module.disabled"
                  @change="handleModuleChange(row.module)"
                >
                  {{ row.module.name }}
                </el-checkbox>
              </div>
            </template>
          </el-table-column>

          <el-table-column width="200" align="center">
            <template #default="{ row }">
              <div v-if="row.feature" style="text-align: left; padding-left: 20px">
                <el-checkbox
                  v-model="row.feature.checked"
                  :disabled="row.feature.disabled"
                  @change="handleFeatureChange(row.feature, row.module)"
                >
                  {{ row.feature.name }}
                </el-checkbox>
              </div>
            </template>
          </el-table-column>

          <el-table-column align="center">
            <template #default="{ row }">
              <div v-if="row.feature" style="text-align: left; padding-left: 20px">
                <el-checkbox
                  v-for="(permission, index) in row.feature.permissions"
                  :key="index"
                  v-model="permission.checked"
                  :disabled="permission.disabled"
                  @change="handlePermissionChange(row.feature, row.module)"
                  style="margin-right: 15px"
                >
                  {{ permission.name }}
                </el-checkbox>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <footer-actions>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </footer-actions>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import FooterActions from '@/components/footer-actions/index.vue'
import { roleSaveOrUpdate, roleDetail, getRolePermissions } from '@/api/admin/auth/role'

const router = useRouter()
const route = useRoute()

// 判断是否为编辑模式
const isEdit = computed(() => !!route.query.id)
const roleId = computed(() => route.query.id as string)

interface Permission {
  id: string
  name: string
  checked: boolean
  disabled?: boolean
  api?: string
}

interface Feature {
  id: string
  name: string
  checked: boolean
  disabled?: boolean
  permissions: Permission[]
}

interface Module {
  id: string
  name: string
  checked: boolean
  disabled?: boolean
  features: Feature[]
}

const formRef = ref()

const form = ref({
  roleName: '',
  status: true
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const modules = ref<Module[]>([])

const permissionData = computed(() => {
  const data: any[] = []
  modules.value.forEach(module => {
    module.features.forEach((feature, featureIndex) => {
      data.push({
        module: module,
        feature: feature,
        isModule: featureIndex === 0,
        featureCount: module.features.length
      })
    })
  })
  return data
})

const objectSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0) {
    if (row.isModule) {
      return {
        rowspan: row.featureCount,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
  return {
    rowspan: 1,
    colspan: 1
  }
}

const handleModuleChange = (module: Module) => {
  module.features.forEach(feature => {
    feature.checked = module.checked
    feature.permissions.forEach(permission => {
      permission.checked = module.checked
    })
  })
}

const handleFeatureChange = (feature: Feature, module: Module) => {
  feature.permissions.forEach(permission => {
    permission.checked = feature.checked
  })

  updateModuleState(module)
}

const handlePermissionChange = (feature: Feature, module: Module) => {
  const anyPermissionChecked = feature.permissions.length > 0 && feature.permissions.some(p => p.checked)
  feature.checked = anyPermissionChecked
  updateModuleState(module)
}

const updateModuleState = (module: Module) => {
  const anyFeatureChecked = module.features.some(f => f.checked)
  module.checked = anyFeatureChecked
}

const loadRoleData = async () => {
  const { data, code, message } = await roleDetail({ id: roleId.value })
  if (code !== 200) return ElMessage.warning(message)
  form.value.roleName = data?.name || ''
  form.value.status = data?.enabled === '1' || data?.enabled === 1
  return data?.permissions || []
}

const loadPermissionsData = async (assignedPermissions: string[] = []) => {
  const params = isEdit.value ? { roleId: roleId.value } : {}
  const { data, code, message } = await getRolePermissions(params)
  if (code !== 200) return ElMessage.warning(message)
  if (!data || !data.systemPermissions) return

  // 检查是否为全部权限
  const isAllPermissions = assignedPermissions.includes('*')
  const assignedPermissionIds = new Set(assignedPermissions)
  const convertedModules: Module[] = []

  data.systemPermissions.forEach((systemModule: any) => {
    const module: Module = {
      id: systemModule.id,
      name: systemModule.label,
      checked: false,
      disabled: isAllPermissions,
      features: []
    }

    if (systemModule.children && Array.isArray(systemModule.children)) {
      systemModule.children.forEach((featureNode: any) => {
        const feature: Feature = {
          id: featureNode.id,
          name: featureNode.label,
          checked: false,
          disabled: isAllPermissions,
          permissions: []
        }

        if (featureNode.children && Array.isArray(featureNode.children)) {
          featureNode.children.forEach((permissionNode: any) => {
            const permission: Permission = {
              id: permissionNode.id,
              name: permissionNode.label,
              api: permissionNode.api,
              checked: isAllPermissions || assignedPermissionIds.has(permissionNode.id),
              disabled: isAllPermissions
            }
            feature.permissions.push(permission)
          })
        }

        if (feature.permissions.length > 0) {
          feature.checked = feature.permissions.some(p => p.checked)
        }

        module.features.push(feature)
      })
    }

    if (module.features.length > 0) {
      module.checked = module.features.some(f => f.checked)
    }

    convertedModules.push(module)
  })

  modules.value = convertedModules
}

const handleCancel = () => {
  router.back()
}

const handleConfirm = async () => {
  await formRef.value.validate()
  // 收集选中的权限id
  const selectedPermissions: string[] = []
  modules.value.forEach(module => {
    module.features.forEach(feature => {
      feature.permissions.forEach(permission => {
        if (permission.checked) {
          selectedPermissions.push(permission.id)
        }
      })
    })
  })

  const payload: any = {
    name: form.value.roleName,
    enabled: form.value.status ? '1' : '0',
    permissions: selectedPermissions
  }

  if (isEdit.value) {
    payload.id = roleId.value
  }

  const { code, message } = await roleSaveOrUpdate(payload)
  if (code !== 200) return ElMessage.warning(message)
  ElMessage.success(isEdit.value ? '修改成功' : '保存成功')
  router.back()
}

onMounted(async () => {
  let rolePermissions: string[] = []
  if (isEdit.value) {
    rolePermissions = (await loadRoleData()) || []
  }
  await loadPermissionsData(rolePermissions)
})
</script>

<style scoped lang="scss">
.role-add-container {
  padding: 20px;
  padding-bottom: 80px;
  background: #fff;
  min-height: calc(100vh - 60px);

  .page-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin: 0 0 20px 0;
    padding-bottom: 15px;
    border-bottom: 1px solid #e5e5e5;
  }

  .permission-section {
    .section-title {
      font-size: 16px;
      font-weight: bold;
      padding-left: 12px;
      margin-bottom: 20px;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: #409eff;
        border-radius: 2px;
      }
    }

    .role-form {
      margin-bottom: 20px;
      max-width: 1400px;
    }

    .permission-table-section {
      margin-top: 20px;
    }

    :deep(.el-table) {
      .el-checkbox {
        display: inline-flex;
        align-items: center;
      }
      .el-checkbox__input.is-disabled + span.el-checkbox__label {
        color: #606266;
      }
    }
  }
}
</style>
