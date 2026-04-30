import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/qmy/index.vue'

const saasRouter: Array<RouteRecordRaw> = [
  {
    path: '/saas',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: 'SAAS管理',
      icon: 'Menu'
    },
    children: [
      {
        path: '/saas/tenant',
        component: () => import('@/views/qmy/saas/tenant/index.vue'),
        name: 'saas-tenant',
        meta: {
          title: '租户管理',
          icon: 'OfficeBuilding'
        },
        children: [
          {
            path: '/saas/tenant/add',
            component: () => import('@/views/qmy/saas/tenant/add.vue'),
            name: 'saas-tenant-add',
            meta: {
              title: '新增租户',
              hidden: true
            }
          },
          {
            path: '/saas/tenant/detail',
            component: () => import('@/views/qmy/saas/tenant/add.vue'),
            name: 'saas-tenant-detail',
            meta: {
              title: '租户详情',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/saas/account',
        component: () => import('@/views/qmy/saas/account/index.vue'),
        name: 'saas-account',
        meta: {
          title: '账号管理',
          icon: 'User'
        },
        children: [
          {
            path: '/saas/account/add',
            component: () => import('@/views/qmy/saas/account/add.vue'),
            name: 'saas-account-add',
            meta: {
              title: '新增账号',
              hidden: true
            }
          },
          {
            path: '/saas/account/detail',
            component: () => import('@/views/qmy/saas/account/add.vue'),
            name: 'saas-account-detail',
            meta: {
              title: '账号详情',
              hidden: true
            }
          }
        ]
      }
    ]
  }
]

export default saasRouter
