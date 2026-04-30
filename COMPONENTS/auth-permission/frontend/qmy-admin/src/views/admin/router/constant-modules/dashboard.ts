import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const DashboardRouter: Array<RouteRecordRaw> = [
  {
    path: '/dashboard',
    redirect: '/dashboard/index',
    component: Layout,
    meta: {
      title: '仪表盘',
      icon: 'Menu'
    },
    children: [
      {
        path: '/dashboard/index',
        component: () => import('@/views/admin/dashboard/index.vue'),
        name: 'dashboard-index',
        meta: {
          title: '业务看板',
          icon: 'Menu'
        }
      },
      {
        path: '/dashboard/purchase',
        component: () => import('@/views/admin/dashboard/purchase/index.vue'),
        name: 'dashboard-purchase',
        meta: {
          title: '采购看板',
          icon: 'ShoppingBag'
        }
      }
    ]
  }
]

export default DashboardRouter
