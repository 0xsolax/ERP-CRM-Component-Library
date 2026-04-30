import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/zs/index.vue'

const materialRoutes: RouteRecordRaw = {
  path: '/material',
  component: Layout,
  redirect: 'noredirect',
  meta: {
    title: '材料管理',
    icon: 'Box'
  },
  children: [
    {
      path: '/material/umbrella-frame',
      component: () => import('@/views/zs/material/umbrella-frame/index.vue'),
      name: 'material-umbrella-frame',
      meta: {
        title: '伞架列表',
        icon: 'Umbrella',
        permission: 'material:umbrella:page'
      }
    },
    {
      path: '/material/fabric',
      component: () => import('@/views/zs/material/fabric/index.vue'),
      name: 'material-fabric',
      meta: {
        title: '面料列表',
        icon: 'Tickets',
        permission: 'material:fabric:page'
      }
    },
    {
      path: '/material/material',
      component: () => import('@/views/zs/material/material/index.vue'),
      name: 'material-other',
      meta: {
        title: '其他材料',
        icon: 'Goods',
        permission: 'material:material:page'
      }
    },
    {
      path: '/material/packaging',
      component: () => import('@/views/zs/material/packaging/index.vue'),
      name: 'material-packaging',
      meta: {
        title: '包材列表',
        icon: 'TakeawayBox',
        permission: 'material:packaging:page'
      }
    }
  ]
}

export default materialRoutes
