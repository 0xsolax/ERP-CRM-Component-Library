import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from './router'
import { RouteLocationNormalized } from 'vue-router'
import { whiteList, whiteNameList } from '@/config/whitelist'
import { useUserStore } from './store/modules/user'
import { usePermissionStore } from './store/modules/permission'
import { routeListener } from '@/utils/permission'
import { setRouteChange } from '@/hooks/listener/use-route-listener'

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to: RouteLocationNormalized, _: RouteLocationNormalized, next: any) => {
  NProgress.start()
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (whiteList.indexOf(to.path) !== -1 || whiteNameList.indexOf(to.name as string) !== -1) {
    return next()
  }
  routeListener()
  if (userStore.token) {
    if (!userStore.loadUserInfo) {
      await userStore.getUserInfo()
      await userStore.getMenu()
      permissionStore.dynamicRoutes.forEach((route: any) => {
        router.addRoute('layout', route)
      })
      next({ ...to, replace: true })
    } else {
      next()
    }
  } else {
    next(`/login`)
    NProgress.done()
  }
})

router.afterEach((to: RouteLocationNormalized) => {
  setRouteChange(to)
  NProgress.done()
  document.title = to.meta.title as string
})
