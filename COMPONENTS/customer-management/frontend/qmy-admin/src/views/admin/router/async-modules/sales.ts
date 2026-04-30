import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/sales',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '销售管理',
      icon: 'ShoppingCart',
      sort: 20
    },
    children: [
      {
        path: '/sales/customer',
        component: () => import('@/views/admin/sales/customer/index.vue'),
        name: 'sales-customer',
        meta: {
          title: '客户列表',
          icon: 'User',
          permission: 'sal:yt:customer:list'
        },
        children: [
          {
            path: '/sales/customer/add',
            component: () => import('@/views/admin/sales/customer/add.vue'),
            name: 'sales-customer-add',
            meta: {
              title: '新增客户',
              permission: 'sal:yt:customer:save',
              hidden: true
            }
          },
          {
            path: '/sales/customer/detail',
            component: () => import('@/views/admin/sales/customer/detail.vue'),
            name: 'sales-customer-detail',
            meta: {
              title: '客户详情',
              permission: 'sal:yt:customer:detail',
              hidden: true,
              keepQuery: true
            },
            children: [
              {
                path: '/sales/customer/detail/warehouse-history',
                component: () => import('@/views/admin/sales/warehouse-history/index.vue'),
                name: 'sales-customer-detail-warehouse-history',
                meta: {
                  title: '历史流向',
                  hidden: true
                }
              }
            ]
          }
        ]
      }
    ]
  }
]

export default permissionRouter
