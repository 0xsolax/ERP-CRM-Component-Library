import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/qmy/index.vue'

const OtherRouter: Array<RouteRecordRaw> = [
  {
    path: '/redirect',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/qmy/redirect/index.vue')
      }
    ]
  }
]

export default OtherRouter
