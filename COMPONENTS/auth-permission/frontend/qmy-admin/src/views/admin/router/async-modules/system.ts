import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/system',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '系统管理',
      icon: 'Menu',
      sort: 60
    },
    children: [
      {
        path: '/system/role',
        component: () => import('@/views/admin/system/role/index.vue'),
        name: 'system-role',
        meta: {
          title: '角色管理',
          icon: 'Female',
          permission: 'sys:role:list'
        },
        children: [
          {
            path: '/system/role/add',
            component: () => import('@/views/admin/system/role/add.vue'),
            name: 'system-role-add',
            meta: {
              title: '新增角色',
              permission: 'sys:role:create',
              hidden: true
            }
          },
          {
            path: '/system/role/auth',
            component: () => import('@/views/admin/system/role/add.vue'),
            name: 'system-role-auth',
            meta: {
              title: '权限详情',
              permission: 'sys:role:read',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/system/org',
        component: () => import('@/views/admin/system/org/index.vue'),
        name: 'system-org',
        meta: {
          title: '组织架构',
          icon: 'User',
          permission: 'sys:organization:list'
        },
        children: [
          {
            path: '/system/org/detail',
            component: () => import('@/views/admin/system/org/add.vue'),
            name: 'system-org-detail',
            meta: {
              title: '账号详情',
              permission: 'sys:organization:accountDetail',
              hidden: true
            }
          }
        ]
      }
      // {
      //   path: '/system/menu',
      //   component: () => import('@/views/admin/system/menu/index.vue'),
      //   name: 'system-menu',
      //   meta: {
      //     title: '菜单管理',
      //     icon: 'Menu'
      //   }
      // },
    ]
  }
]

export default permissionRouter
