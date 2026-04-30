import { useBaseDataSelect } from './use-base-data-select'
import type { ZsLabelSelectHook, ZsLabelSelectConfig } from './use-base-data-select'

export type HookFactory = (config: ZsLabelSelectConfig) => ZsLabelSelectHook

const hookMap: Record<string, HookFactory> = {
  'base-data': useBaseDataSelect
}

export function registerHook(name: string, factory: HookFactory) {
  hookMap[name] = factory
}

export function getHookFactory(name: string): HookFactory {
  return hookMap[name] || useBaseDataSelect
}

export type { ZsLabelSelectHook, ZsLabelSelectConfig }
