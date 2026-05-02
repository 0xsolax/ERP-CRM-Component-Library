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
        path: '/sales/order',
        component: () => import('@/views/admin/sales/order/index.vue'),
        name: 'sales-order',
        meta: {
          title: '订单列表',
          permission: 'sal:yt:order:list',
          icon: 'List'
        },
        children: [
          {
            path: '/sales/order/add',
            component: () => import('@/views/admin/sales/order/add.vue'),
            name: 'sales-order-add',
            meta: {
              title: '新增订单',
              permission: 'sal:yt:order:save',
              hidden: true
            }
          },
          {
            path: '/sales/order/edit',
            component: () => import('@/views/admin/sales/order/edit.vue'),
            name: 'sales-order-edit',
            meta: {
              title: '编辑订单',
              permission: 'sal:yt:order:save',
              hidden: true
            }
          },
          {
            path: '/sales/order/detail',
            component: () => import('@/views/admin/sales/order/detail.vue'),
            name: 'sales-order-detail',
            meta: {
              title: '订单详情',
              permission: 'sal:yt:order:detail',
              hidden: true
            }
          },
          {
            path: '/sales/order/purchase',
            component: () => import('@/views/admin/sales/order/purchase.vue'),
            name: 'sales-order-purchase',
            meta: {
              title: '申请采购',
              hidden: true
            }
          }
        ]
      }
    ]
  }
]

export default permissionRouter
