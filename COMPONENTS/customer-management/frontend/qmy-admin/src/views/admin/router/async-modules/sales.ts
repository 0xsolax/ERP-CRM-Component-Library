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
      },
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
