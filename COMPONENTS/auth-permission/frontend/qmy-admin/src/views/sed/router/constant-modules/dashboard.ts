import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/sed/index.vue'

const DashboardRouter: Array<RouteRecordRaw> = [
  {
    path: '/dashboard',
    redirect: '/dashboard/index',
    component: Layout,
    meta: {
      title: '首页',
      icon: 'Menu',
      hidden: true
    },
    children: [
      {
        path: '/dashboard/index',
        component: () => import('@/views/sed/dashboard/index.vue'),
        name: 'dashboard-index',
        meta: {
          title: '首页',
          icon: 'Menu',
          hidden: true
        }
      }
    ]
  }
]

export default DashboardRouter
