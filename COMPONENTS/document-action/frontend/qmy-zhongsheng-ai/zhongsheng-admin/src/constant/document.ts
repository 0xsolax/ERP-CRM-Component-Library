export interface StatusMeta {
  label: string
  type: string
}

export type StatusMetaMap = Record<string, StatusMeta>

export const DOCUMENT_LIST_LOCK_STATE_META: StatusMetaMap = {
  open: { label: '未锁定', type: 'info' },
  locked: { label: '已锁定', type: 'danger' },
  pending_unlock: { label: '待审批', type: 'warning' },
  temporary_unlocked: { label: '已解锁', type: 'success' }
}

export const DOCUMENT_FORM_LOCK_STATE_META: StatusMetaMap = {
  open: { label: '未锁定', type: 'info' },
  locked: { label: '已锁定', type: 'warning' },
  pending_unlock: { label: '待解锁审批', type: 'warning' },
  temporary_unlocked: { label: '已解锁', type: 'success' }
}

export const DOCUMENT_UNLOCK_APPROVAL_LOCK_STATE_META: StatusMetaMap = {
  open: { label: '未锁定', type: 'info' },
  locked: { label: '已锁定', type: 'danger' },
  pending_unlock: { label: '待解锁审批', type: 'warning' },
  temporary_unlocked: { label: '已解锁', type: 'success' }
}

export const DOCUMENT_UNLOCKABLE_LOCK_STATES = ['locked', 'pending_unlock'] as const

export const resolveStatusMeta = (map: StatusMetaMap, value: any): StatusMeta => {
  const key = String(value || '')
  return map[key] || { label: key || '-', type: 'info' }
}

export const isDocumentUnlockableLockState = (value: any) =>
  DOCUMENT_UNLOCKABLE_LOCK_STATES.includes(String(value || '') as any)
