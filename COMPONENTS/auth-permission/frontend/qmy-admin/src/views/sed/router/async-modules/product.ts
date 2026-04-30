import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/sed/index.vue'

const productRoutes: RouteRecordRaw = {
  path: '/product',
  component: Layout,
  redirect: 'noredirect',
  meta: {
    title: '产品管理',
    icon: 'Box'
  },
  children: [
    {
      path: '/product/list',
      component: () => import('@/views/sed/product/list/index.vue'),
      name: 'product-list',
      meta: {
        title: '产品列表',
        icon: 'Tickets',
        permission: 'pro:sed:product:list'
      },
      children: [
        {
          path: '/product/list/detail',
          name: 'product-list-detail',
          component: () => import('@/views/sed/product/list/detail.vue'),
          meta: {
            title: '产品详情',
            hidden: true,
            permission: 'pro:sed:product:detail'
          }
        }
      ]
    }
  ]
}

export default productRoutes
