import { reactive, toRefs } from 'vue'
import { defineStore } from 'pinia'
import { RouteLocationNormalized } from 'vue-router'
import { store } from '@/views/sed/store'
import { SedAdminKeys } from '@/config/settings'
import { router } from '@/views/sed/router'

export interface TagView extends Partial<RouteLocationNormalized> {
  title?: string
}

export interface TagsState {
  visitedViews: TagView[]
  cachedViews: string[]
}

const isValidRoute = (path: string): boolean => {
  const resolved = router.resolve(path)
  return (
    resolved.matched.length > 0 &&
    !resolved.matched.some(
      route => route.path === '/:pathMatch(.*)' || route.path === '/404' || route.name === 'NotFound'
    )
  )
}

const filterValidViews = (views: TagView[]): TagView[] => {
  return views.filter(view => {
    if (!view.path) return false
    if (view.meta?.affix) return true
    return isValidRoute(view.path)
  })
}

const getStoredVisitedViews = (): TagView[] => {
  const stored = sessionStorage.getItem(SedAdminKeys.visitedViewsKey)
  if (!stored) return []
  const parsedViews: TagView[] = JSON.parse(stored)
  const validViews = filterValidViews(parsedViews)
  if (validViews.length !== parsedViews.length) {
    saveVisitedViews(validViews)
  }
  return validViews
}

const getStoredCachedViews = (): string[] => {
  const stored = sessionStorage.getItem(SedAdminKeys.cachedViewsKey)
  if (!stored) return []
  const parsed = JSON.parse(stored)
  return Array.isArray(parsed) ? parsed : []
}

const saveVisitedViews = (views: TagView[]) => {
  const serializedViews = views.map(view => ({
    name: view.name,
    path: view.path,
    fullPath: view.fullPath,
    meta: view.meta,
    params: view.params,
    query: view.query,
    title: view.title
  }))
  sessionStorage.setItem(SedAdminKeys.visitedViewsKey, JSON.stringify(serializedViews))
}

const saveCachedViews = (views: string[]) => {
  sessionStorage.setItem(SedAdminKeys.cachedViewsKey, JSON.stringify(views))
}

export const useTagsStore = defineStore('tags', () => {
  const state = reactive<TagsState>({
    visitedViews: getStoredVisitedViews(),
    cachedViews: getStoredCachedViews()
  })

  const addVisitedView = (view: TagView) => {
    const index = state.visitedViews.findIndex(v => v.path === view.path)
    if (index !== -1) {
      if (state.visitedViews[index].fullPath !== view.fullPath) {
        state.visitedViews[index] = { ...view }
        saveVisitedViews(state.visitedViews)
      }
    } else {
      state.visitedViews.push({ ...view })
      saveVisitedViews(state.visitedViews)
    }
  }

  const delVisitedView = (view: TagView) => {
    const index = state.visitedViews.findIndex((v: TagView) => v.path === view.path)
    if (index !== -1) {
      state.visitedViews.splice(index, 1)
      saveVisitedViews(state.visitedViews)
    }
  }

  const delOthersVisitedViews = (view: TagView) => {
    state.visitedViews = state.visitedViews.filter((v: TagView) => {
      return v.meta?.affix || v.path === view.path
    })
    saveVisitedViews(state.visitedViews)
  }

  const delAllVisitedViews = () => {
    state.visitedViews = state.visitedViews.filter((tag: TagView) => tag.meta?.affix)
    saveVisitedViews(state.visitedViews)
  }

  const addCacheView = (views: any) => {
    if (Array.isArray(views)) {
      state.cachedViews = views
    } else if (views && typeof views === 'object') {
      const viewName = views.name?.toString()
      if (viewName && !state.cachedViews.includes(viewName)) {
        state.cachedViews.push(viewName)
      }
    }
    saveCachedViews(state.cachedViews)
  }

  const delCachedView = (view: any) => {
    if (!view) return

    if (!Array.isArray(state.cachedViews)) {
      state.cachedViews = []
      saveCachedViews(state.cachedViews)
      return
    }

    const viewName = view?.name?.toString() || view?.toString()
    const index = state.cachedViews.indexOf(viewName)
    if (index > -1) {
      state.cachedViews.splice(index, 1)
      saveCachedViews(state.cachedViews)
    }
  }

  const delOthersCachedViews = (view: TagView) => {
    if (typeof view.name !== 'string') return

    if (!Array.isArray(state.cachedViews)) {
      state.cachedViews = []
      saveCachedViews(state.cachedViews)
      return
    }

    const index = state.cachedViews.indexOf(view.name)
    if (index !== -1) {
      state.cachedViews = state.cachedViews.slice(index, index + 1)
    } else {
      state.cachedViews = []
    }
    saveCachedViews(state.cachedViews)
  }

  const delAllCachedViews = () => {
    state.cachedViews = []
    saveCachedViews(state.cachedViews)
  }

  const cleanInvalidViews = () => {
    const validViews = filterValidViews(state.visitedViews)
    const removedCount = state.visitedViews.length - validViews.length

    if (removedCount > 0) {
      state.visitedViews = validViews
      saveVisitedViews(state.visitedViews)
      return removedCount
    }

    return 0
  }

  return {
    ...toRefs(state),
    addVisitedView,
    delVisitedView,
    delOthersVisitedViews,
    delAllVisitedViews,
    addCacheView,
    delCachedView,
    delOthersCachedViews,
    delAllCachedViews,
    cleanInvalidViews
  }
})

export function useTagsStoreHook() {
  return useTagsStore(store)
}
