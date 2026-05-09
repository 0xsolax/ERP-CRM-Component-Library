import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/zs/index.vue'
import { ZS_PERMISSIONS } from '@/constant/permissions'

const productionRoutes: RouteRecordRaw = {
  path: '/production',
  component: Layout,
  redirect: 'noredirect',
  meta: {
    title: '生产管理',
    icon: 'Operation',
    sort: 30
  },
  children: [
    {
      path: '/production/order',
      component: () => import('@/views/zs/production/order.vue'),
      name: 'production-order',
      meta: {
        title: '生产总单',
        icon: 'Operation',
        sort: 10,
        permission: ZS_PERMISSIONS.production.orderPage
      }
    },
    {
      path: '/production/group',
      component: () => import('@/views/zs/production/group.vue'),
      name: 'production-group',
      meta: {
        title: '生产组',
        icon: 'Collection',
        sort: 20,
        permission: ZS_PERMISSIONS.production.groupPage
      }
    }
  ]
}

export default productionRoutes
