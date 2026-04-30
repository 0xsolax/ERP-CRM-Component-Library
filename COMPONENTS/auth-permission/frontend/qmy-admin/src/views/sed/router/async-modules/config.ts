import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/sed/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/config',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '基础配置',
      icon: 'Setting'
    },
    children: [
      {
        path: '/config/mold',
        component: () => import('@/views/sed/config/mold/index.vue'),
        name: 'config-mold',
        meta: {
          title: '模具管理',
          icon: 'Grid',
          permission: 'pro:sed:mold:page'
        }
      },
      {
        path: '/config/color',
        component: () => import('@/views/sed/config/color/index.vue'),
        name: 'config-color',
        meta: {
          title: '颜色管理',
          icon: 'Brush',
          permission: 'pro:sed:color:list'
        }
      },
      {
        path: '/config/package',
        component: () => import('@/views/sed/config/package/index.vue'),
        name: 'config-package',
        meta: {
          title: '包材管理',
          icon: 'Box',
          permission: 'pro:sed:packing:list'
        }
      },
      {
        path: '/config/part',
        component: () => import('@/views/sed/config/part/index.vue'),
        name: 'config-part',
        meta: {
          title: '零件管理',
          icon: 'Connection',
          permission: 'pro:sed:part:list'
        }
      },
      {
        path: '/config/accessory',
        component: () => import('@/views/sed/config/accessory/index.vue'),
        name: 'config-accessory',
        meta: {
          title: '配件管理',
          icon: 'Tools',
          permission: 'pro:sed:fitting:list'
        }
      },
      {
        path: '/config/process',
        component: () => import('@/views/sed/config/process/index.vue'),
        name: 'config-process',
        meta: {
          title: '工艺管理',
          icon: 'Operation',
          permission: 'pro:sed:craft:list'
        }
      },
      {
        path: '/config/effect',
        component: () => import('@/views/sed/config/effect/index.vue'),
        name: 'config-effect',
        meta: {
          title: '效果管理',
          icon: 'MagicStick',
          permission: 'pro:sed:effect:list'
        }
      }
    ]
  }
]

export default permissionRouter
