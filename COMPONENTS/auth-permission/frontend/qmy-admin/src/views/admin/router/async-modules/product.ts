import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/product',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '产品管理',
      icon: 'Box',
      sort: 10
    },
    children: [
      {
        path: '/product/list',
        component: () => import('@/views/admin/product/list/index.vue'),
        name: 'product-list',
        meta: {
          title: '产品列表',
          icon: 'Goods',
          permission: 'pro:yt:product:list'
        },
        children: [
          {
            path: '/product/list/add',
            component: () => import('@/views/admin/product/list/add.vue'),
            name: 'product-list-add',
            meta: {
              title: '新增产品',
              permission: 'pro:yt:product:save',
              hidden: true
            }
          },
          {
            path: '/product/list/edit',
            component: () => import('@/views/admin/product/list/edit.vue'),
            name: 'product-list-edit',
            meta: {
              title: '编辑产品',
              permission: 'pro:yt:product:save',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/product/combination',
        component: () => import('@/views/admin/product/combination/index.vue'),
        name: 'product-combination',
        meta: {
          title: '产品组合',
          icon: 'Grid',
          permission: 'pro:yt:combination:list'
        },
        children: [
          {
            path: '/product/combination/add',
            component: () => import('@/views/admin/product/combination/add.vue'),
            name: 'product-combination-add',
            meta: {
              title: '新增组合',
              permission: 'pro:yt:combination:update',
              hidden: true
            }
          },
          {
            path: '/product/combination/edit',
            component: () => import('@/views/admin/product/combination/edit.vue'),
            name: 'product-combination-edit',
            meta: {
              title: '编辑组合',
              permission: 'pro:yt:combination:update',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/product/diamond',
        component: () => import('@/views/admin/product/diamond/index.vue'),
        name: 'product-diamond',
        meta: {
          title: '配钻列表',
          icon: 'Discount',
          permission: 'pro:yt:diamond:list'
        }
      }
    ]
  }
]

export default permissionRouter
