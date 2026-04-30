import { RouteRecordRaw } from 'vue-router'

const ErrorRouter: Array<RouteRecordRaw> = [
  {
    path: '/401',
    component: () => import('@/views/qmy/error-page/401.vue'),
    meta: {
      hidden: true
    }
  },
  {
    path: '/404',
    component: () => import('@/views/qmy/error-page/404.vue'),
    meta: {
      hidden: true
    }
  }
]

export default ErrorRouter
