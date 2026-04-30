import { RouteRecordRaw } from 'vue-router'
const LayoutRouter: Array<RouteRecordRaw> = [
  {
    path: '/layout',
    component: () => import('@/layout/zs/index.vue'),
    name: 'layout',
    meta: {
      hidden: true
    }
  }
]
export default LayoutRouter
