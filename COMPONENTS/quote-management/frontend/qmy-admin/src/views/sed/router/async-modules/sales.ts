import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/sed/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/sales',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '销售管理',
      icon: 'ShoppingCart'
    },
    children: [
      {
        path: '/sales/quotation',
        component: () => import('@/views/sed/sales/quotation/index.vue'),
        name: 'sales-quotation',
        meta: {
          title: '报价单列表',
          icon: 'Document',
          permission: 'sal:sed:quotation:quotation:list'
        }
      }
    ]
  }
]

export default permissionRouter
