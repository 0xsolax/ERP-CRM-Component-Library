import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/finance',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '财务管理',
      icon: 'Wallet',
      sort: 50
    },
    children: [
      {
        path: '/finance/payable',
        component: () => import('@/views/admin/finance/payable/index.vue'),
        name: 'finance-payable',
        meta: {
          title: '应付款',
          icon: 'CreditCard',
          permission: ['fin:yt:payment:deliveryPaymentList', 'fin:yt:payment:purchasePaymentList']
        }
      },
      {
        path: '/finance/receivable',
        component: () => import('@/views/admin/finance/receivable/index.vue'),
        name: 'finance-receivable',
        meta: {
          title: '应收款',
          icon: 'Money',
          permission: ['fin:yt:payment:orderReceiveList', 'fin:yt:payment:deliveryReceiveList']
        }
      },
      {
        path: '/finance/profit',
        component: () => import('@/views/admin/finance/profit/index.vue'),
        name: 'finance-profit',
        meta: {
          title: '利润列表',
          icon: 'TrendCharts',
          permission: ['fin:yt:payment:orderProfitList', 'fin:yt:payment:customerProfitList']
        },
        children: [
          {
            path: '/finance/profit/detail',
            component: () => import('@/views/admin/finance/profit/detail.vue'),
            name: 'finance-profit-detail',
            meta: {
              title: '利润详情',
              permission: 'fin:yt:payment:orderProfitList',
              hidden: true
            }
          },
          {
            path: '/finance/profit/customer-detail',
            component: () => import('@/views/admin/finance/profit/customer-detail.vue'),
            name: 'finance-profit-customer-detail',
            meta: {
              title: '客户利润详情',
              permission: 'fin:yt:payment:customerProfitDetail',
              hidden: true,
              keepQuery: true
            },
            children: [
              {
                path: '/finance/profit/customer-detail/profit-detail',
                component: () => import('@/views/admin/finance/profit/detail.vue'),
                name: 'finance-profit-customer-detail-profit-detail',
                meta: {
                  title: '利润详情',
                  permission: 'fin:yt:payment:orderProfitList',
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
