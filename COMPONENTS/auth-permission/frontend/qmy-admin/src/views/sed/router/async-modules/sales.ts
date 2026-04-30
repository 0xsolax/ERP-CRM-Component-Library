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
        path: '/sales/customer',
        component: () => import('@/views/sed/sales/customer/index.vue'),
        name: 'sales-customer',
        meta: {
          title: '客户列表',
          icon: 'User',
          permission: 'sal:yt:customer:list'
        },
        children: [
          {
            path: '/sales/customer/add',
            component: () => import('@/views/sed/sales/customer/add.vue'),
            name: 'sales-customer-add',
            meta: {
              title: '新增客户',
              permission: 'sal:yt:customer:save',
              hidden: true
            }
          },
          {
            path: '/sales/customer/detail',
            component: () => import('@/views/sed/sales/customer/detail.vue'),
            name: 'sales-customer-detail',
            meta: {
              title: '客户详情',
              permission: 'sal:yt:customer:detail',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/sales/quotation',
        component: () => import('@/views/sed/sales/quotation/index.vue'),
        name: 'sales-quotation',
        meta: {
          title: '报价单列表',
          icon: 'Document',
          permission: 'sal:sed:quotation:quotation:list'
        }
      },
      {
        path: '/sales/order',
        component: () => import('@/views/sed/sales/order/index.vue'),
        name: 'sales-order',
        meta: {
          title: '订单列表',
          icon: 'List',
          permission: 'sal:sed:order:list'
        },
        children: [
          {
            path: '/sales/order/detail',
            component: () => import('@/views/sed/sales/order/detail.vue'),
            name: 'sales-order-detail',
            meta: {
              title: '订单详情',
              hidden: true,
              permission: 'sal:sed:order:detail'
            }
          }
        ]
      }
    ]
  }
]

export default permissionRouter
