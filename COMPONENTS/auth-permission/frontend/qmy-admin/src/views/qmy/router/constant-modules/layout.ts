import { RouteRecordRaw } from 'vue-router'
const LayoutRouter: Array<RouteRecordRaw> = [
  {
    path: '/layout',
    component: () => import('@/layout/qmy/index.vue'),
    name: 'layout',
    meta: {
      hidden: true
    }
  }
]
export default LayoutRouter
