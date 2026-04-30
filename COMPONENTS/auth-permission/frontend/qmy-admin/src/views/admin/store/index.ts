import { createPinia } from 'pinia'
import { useAppStore } from './modules/app'
import { usePermissionStore } from './modules/permission'
import { useSettingsStore } from './modules/settings'
import { useTagsStore } from './modules/tags'
import { useUserStore } from './modules/user'

export const store = createPinia()

export function useStore() {
  return {
    app: useAppStore(),
    permission: usePermissionStore(),
    settings: useSettingsStore(),
    tags: useTagsStore(),
    user: useUserStore()
  }
}
