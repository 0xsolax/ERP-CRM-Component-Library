import { ref } from 'vue'
import type { Ref } from 'vue'
import { getBaseDataList, saveOrUpdateBaseData, listByNodeKey } from '@/api/zs/base-info/base-data'

export interface ZsLabelSelectOption {
  id: any
  label: string
  value: any
}

export interface ZsLabelSelectHook {
  options: Ref<ZsLabelSelectOption[]>
  loadOptions: () => Promise<void>
  addOption: (label: string) => Promise<boolean>
  editOption: (id: any, label: string) => Promise<boolean>
  deleteOption: (id: any) => Promise<boolean>
}

export interface ZsLabelSelectConfig {
  nodeKey?: string
  nodeId?: string
}

export function useBaseDataSelect(config: ZsLabelSelectConfig): ZsLabelSelectHook {
  const options = ref<ZsLabelSelectOption[]>([])
  const currentNodeId = ref<any>(null)

  const loadOptions = async () => {
    if (config.nodeKey) {
      const { code, data } = await listByNodeKey({ nodeKey: config.nodeKey })
      if (code === 200) {
        const list = data || []
        if (list.length > 0) currentNodeId.value = list[0].nodeId
        options.value = list.map((d: any) => ({
          id: d.id,
          label: d.value1 || '',
          value: d.id
        }))
      }
    } else if (config.nodeId) {
      const { code, data } = await getBaseDataList({ nodeIds: [config.nodeId] })
      if (code === 200) {
        const list = (data || []).filter((d: any) => d.nodeId === config.nodeId)
        options.value = list.map((d: any) => ({
          id: d.id,
          label: d.value1 || '',
          value: d.id
        }))
      }
    }
  }

  const getNodeId = () => config.nodeId || currentNodeId.value

  const addOption = async (label: string): Promise<boolean> => {
    const { code } = await saveOrUpdateBaseData({ nodeId: getNodeId(), value1: label })
    return code === 200
  }

  const editOption = async (id: any, label: string): Promise<boolean> => {
    const { code } = await saveOrUpdateBaseData({ id, nodeId: getNodeId(), value1: label })
    return code === 200
  }

  const deleteOption = async (id: any): Promise<boolean> => {
    const { code } = await saveOrUpdateBaseData({ id, nodeId: getNodeId(), isDeleted: 1 })
    return code === 200
  }

  return { options, loadOptions, addOption, editOption, deleteOption }
}
