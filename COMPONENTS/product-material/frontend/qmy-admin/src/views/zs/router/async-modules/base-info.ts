import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/zs/index.vue'

const baseInfoRoutes: RouteRecordRaw = {
  path: '/base-info',
  component: Layout,
  redirect: 'noredirect',
  meta: {
    title: '基础信息',
    icon: 'Setting'
  },
  children: [
    {
      path: '/base-info/product/index',
      component: () => import('@/views/zs/base-info/product/index.vue'),
      name: 'base-info-product-index',
      meta: {
        title: '产品列表',
        icon: 'Tickets',
        permission: 'product:product:page'
      },
      children: [
        {
          path: '/base-info/product/add',
          name: 'base-info-product-add',
          component: () => import('@/views/zs/base-info/product/add.vue'),
          meta: {
            title: '新增产品',
            permission: 'product:product:save',
            hidden: true
          }
        },
        {
          path: '/base-info/product/edit',
          name: 'base-info-product-edit',
          component: () => import('@/views/zs/base-info/product/edit.vue'),
          meta: {
            title: '编辑产品',
            permission: 'product:product:save',
            hidden: true
          }
        }
      ]
    },
    {
      path: '/base-info/process',
      component: () => import('@/views/zs/base-info/process/index.vue'),
      name: 'base-info-process',
      meta: {
        title: '工价管理',
        permission: 'process:process:page',
        icon: 'Money'
      }
    },
    {
      path: '/base-info/field',
      component: () => import('@/views/zs/base-info/field/index.vue'),
      name: 'base-info-field',
      meta: {
        title: '字段管理',
        permission: 'base:data:list',
        icon: 'List'
      }
    }
  ]
}

export default baseInfoRoutes
