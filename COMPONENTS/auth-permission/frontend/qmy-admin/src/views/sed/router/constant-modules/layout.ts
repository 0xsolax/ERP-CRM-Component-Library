import { RouteRecordRaw } from 'vue-router'
const LayoutRouter: Array<RouteRecordRaw> = [
  {
    path: '/layout',
    component: () => import('@/layout/sed/index.vue'),
    name: 'layout',
    meta: {
      hidden: true
    }
  }
]
export default LayoutRouter
