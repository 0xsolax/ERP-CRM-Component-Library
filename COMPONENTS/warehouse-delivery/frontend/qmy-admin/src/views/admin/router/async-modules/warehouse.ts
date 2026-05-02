import { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/admin/index.vue'

const permissionRouter: Array<RouteRecordRaw> = [
  {
    path: '/warehouse',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '仓储管理',
      permission: 'sto:yt:store:list',
      icon: 'House',
      sort: 40
    },
    children: [
      {
        path: '/warehouse/inventory',
        component: () => import('@/views/admin/warehouse/inventory/index.vue'),
        name: 'warehouse-inventory',
        meta: {
          title: '实时库存',
          permission: 'sto:yt:store:list',
          icon: 'Box'
        },
        children: [
          {
            path: '/warehouse/inventory/history',
            component: () => import('@/views/admin/warehouse/inventory/history.vue'),
            name: 'warehouse-inventory-history',
            meta: {
              title: '库存历史',
              permission: 'sto:yt:store:list',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/warehouse/inbound',
        component: () => import('@/views/admin/warehouse/inbound/index.vue'),
        name: 'warehouse-inbound',
        meta: {
          title: '入库列表',
          permission: 'sto:yt:order:list',
          icon: 'Download'
        },
        children: [
          {
            path: '/warehouse/inbound/record',
            component: () => import('@/views/admin/warehouse/inbound/record.vue'),
            name: 'warehouse-inbound-record',
            meta: {
              title: '出入库记录',
              permission: 'sto:yt:order:list',
              hidden: true
            }
          },
          {
            path: '/warehouse/inbound/add',
            component: () => import('@/views/admin/warehouse/inbound/add.vue'),
            name: 'warehouse-inbound-add',
            meta: {
              title: '创建独立出入库单',
              permission: 'sto:yt:order:addStore',
              hidden: true
            }
          }
        ]
      },
      {
        path: '/warehouse/shipping',
        component: () => import('@/views/admin/warehouse/shipping/index.vue'),
        name: 'warehouse-shipping',
        meta: {
          title: '发货列表',
          permission: 'sto:yt:delivery:list',
          icon: 'Van'
        }
      },
      {
        path: '/warehouse/logistics',
        component: () => import('@/views/admin/warehouse/logistics/index.vue'),
        name: 'warehouse-logistics',
        meta: {
          title: '物流公司',
          permission: 'sto:yt:transportCompany:list',
          icon: 'HelpFilled'
        }
      },
      {
        path: '/warehouse/packing',
        component: () => import('@/views/admin/warehouse/packing/index.vue'),
        name: 'warehouse-packing',
        meta: {
          title: '打包箱列表',
          permission: 'sto:yt:box:list',
          icon: 'Present'
        }
      }
    ]
  }
]

export default permissionRouter
