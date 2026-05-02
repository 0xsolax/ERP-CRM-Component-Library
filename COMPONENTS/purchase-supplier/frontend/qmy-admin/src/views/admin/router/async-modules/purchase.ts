import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/purchase',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '采购管理',
      permission: 'pur:yt:applyPurchase:list',
      icon: 'ShoppingBag',
      sort: 30
    },
    children: [
      {
        path: '/purchase/stock-warning',
        component: () => import('@/views/admin/purchase/stock-warning/index.vue'),
        name: 'purchase-stock-warning',
        meta: {
          title: '库存预警',
          permission: 'pur:yt:storeWarning:list',
          icon: 'Warning'
        },
        children: [
          {
            path: '/purchase/stock-warning/batch-apply',
            component: () => import('@/views/admin/purchase/stock-warning/batch-apply.vue'),
            name: 'purchase-stock-warning-batch-apply',
            meta: {
              title: '批量申购',
              permission: 'pur:yt:storeWarning:submitApplyPurchase',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/purchase/pending',
        component: () => import('@/views/admin/purchase/pending/index.vue'),
        name: 'purchase-pending',
        meta: {
          title: '待采购列表',
          permission: 'pur:yt:applyPurchase:list',
          icon: 'Clock'
        },
        children: [
          {
            path: '/purchase/pending/generate-order',
            component: () => import('@/views/admin/purchase/pending/generate-order.vue'),
            name: 'purchase-pending-generate-order',
            meta: {
              title: '生成采购单',
              permission: 'pur:yt:applyPurchase:saveOrUpdate',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/purchase/purchased',
        component: () => import('@/views/admin/purchase/purchased/index.vue'),
        name: 'purchase-purchased',
        meta: {
          title: '已采购列表',
          permission: 'pur:yt:purchase:list',
          icon: 'CircleCheck'
        },
        children: [
          {
            path: '/purchase/purchased/add',
            component: () => import('@/views/admin/purchase/purchased/add.vue'),
            name: 'purchase-purchased-add',
            meta: {
              title: '新增',
              permission: ['pur:yt:purchase:createOrUpdate'],
              hidden: true
            }
          },
          {
            path: '/purchase/purchased/detail',
            component: () => import('@/views/admin/purchase/purchased/detail.vue'),
            name: 'purchase-purchased-detail',
            meta: {
              title: '采购单详情',
              permission: 'pur:yt:purchase:detail',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/purchase/supplier',
        component: () => import('@/views/admin/purchase/supplier/index.vue'),
        name: 'purchase-supplier',
        meta: {
          title: '供应商列表',
          permission: 'pur:yt:purchaseSupplier:list',
          icon: 'User'
        },
        children: [
          {
            path: '/purchase/supplier/add',
            component: () => import('@/views/admin/purchase/supplier/add.vue'),
            name: 'purchase-supplier-add',
            meta: {
              title: '新增供应商',
              permission: 'pur:yt:purchaseSupplier:add',
              hidden: true
            }
          },
          {
            path: '/purchase/supplier/detail',
            component: () => import('@/views/admin/purchase/supplier/detail.vue'),
            name: 'purchase-supplier-detail',
            meta: {
              title: '供应商详情',
              permission: 'pur:yt:purchaseSupplier:detail',
              hidden: true
            }
          }
        ]
      }
    ]
  }
]

export default permissionRouter
