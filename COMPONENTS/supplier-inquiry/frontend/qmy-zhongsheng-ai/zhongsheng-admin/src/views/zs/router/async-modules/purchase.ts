import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/zs/index.vue'
import { ZS_PERMISSIONS } from '@/constant/permissions'

const purchaseRoutes: RouteRecordRaw = {
  path: '/purchase',
  component: Layout,
  redirect: 'noredirect',
  meta: {
    title: '采购管理',
    icon: 'ShoppingCart',
    sort: 40
  },
  children: [
    {
      path: '/purchase/index',
      component: () => import('@/views/zs/purchase/index.vue'),
      name: 'purchase-index',
      meta: {
        title: '采购单列表',
        icon: 'ShoppingCart',
        sort: 10,
        permission: ZS_PERMISSIONS.purchase.page
      }
    },
    {
      path: '/supplier/inquiry',
      component: () => import('@/views/zs/supplier-inquiry/index.vue'),
      name: 'supplier-inquiry',
      meta: {
        title: '供应商询价',
        icon: 'Tickets',
        sort: 20,
        permission: ZS_PERMISSIONS.supplierInquiry.page
      }
    },
    {
      path: '/supplier/index',
      component: () => import('@/views/zs/supplier/index.vue'),
      name: 'supplier-index',
      meta: {
        title: '供应商列表',
        icon: 'OfficeBuilding',
        sort: 30,
        permission: ZS_PERMISSIONS.supplier.page
      }
    }
  ]
}

export default purchaseRoutes
