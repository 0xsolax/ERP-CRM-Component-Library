import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/sed/index.vue'

const OtherRouter: Array<RouteRecordRaw> = [
  {
    path: '/redirect',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/sed/redirect/index.vue')
      }
    ]
  }
]

export default OtherRouter
