import { Directive, DirectiveBinding } from 'vue'
import { usePermissionStore } from '@/views/admin/store/modules/permission'

/**
 * 权限指令
 * 使用方法：
 * 1. 单个权限：v-permission="'admin:role:create'"
 * 2. 多个权限（满足任一）：v-permission="['admin:role:create', 'admin:role:update']"
 * 3. 多个权限（需全部满足）：v-permission.all="['admin:role:create', 'admin:role:update']"
 */

function checkPermission(el: HTMLElement, binding: DirectiveBinding) {
  const { value, modifiers } = binding
  const permissionStore = usePermissionStore()
  const userPermissions = permissionStore.permissions

  if (!value) return
  let hasPermission = false

  if (Array.isArray(value)) {
    if (modifiers.all) {
      // 需要全部满足
      hasPermission = value.every(permission => userPermissions.includes(permission))
    } else {
      // 满足任一（默认）
      hasPermission = value.some(permission => userPermissions.includes(permission))
    }
  } else if (typeof value === 'string') {
    hasPermission = userPermissions.includes(value)
  }

  if (!hasPermission && el.parentNode) {
    el.parentNode.removeChild(el)
  }
}

export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  }
}
