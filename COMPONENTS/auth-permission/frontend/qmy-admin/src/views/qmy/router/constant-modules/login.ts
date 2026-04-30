import { RouteRecordRaw } from 'vue-router'
const LoginRouter: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/qmy/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  }
]
export default LoginRouter
