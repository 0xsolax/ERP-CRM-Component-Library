import { YitangAdminKeys, SedAdminKeys, ZsAdminKeys, QmyAdminKeys } from '@/config/settings'

// 一唐租户
export const getYitangAdminSidebarStatus = () => window.localStorage.getItem(YitangAdminKeys.sidebarStatusKey)
export const setYitangAdminSidebarStatus = (sidebarStatus: string) =>
  window.localStorage.setItem(YitangAdminKeys.sidebarStatusKey, sidebarStatus)
export const getYitangAdminToken = () => window.localStorage.getItem(YitangAdminKeys.tokenKey)
export const setYitangAdminToken = (token: string) => window.localStorage.setItem(YitangAdminKeys.tokenKey, token)
export const removeYitangAdminToken = () => window.localStorage.removeItem(YitangAdminKeys.tokenKey)
export const getYitangAdminUserId = () => window.localStorage.getItem(YitangAdminKeys.userIdKey)
export const setYitangAdminUserId = (userId: string) => window.localStorage.setItem(YitangAdminKeys.userIdKey, userId)
export const removeYitangAdminUserId = () => window.localStorage.removeItem(YitangAdminKeys.userIdKey)
export const getYitangAdminUserName = () => window.localStorage.getItem(YitangAdminKeys.userNameKey)
export const setYitangAdminUserName = (val: string) => window.localStorage.setItem(YitangAdminKeys.userNameKey, val)
export const removeYitangAdminUserName = () => window.localStorage.removeItem(YitangAdminKeys.userNameKey)
export const getYitangAdminNickName = () => window.localStorage.getItem(YitangAdminKeys.nickNameKey)
export const setYitangAdminNickName = (val: string) => window.localStorage.setItem(YitangAdminKeys.nickNameKey, val)
export const removeYitangAdminNickName = () => window.localStorage.removeItem(YitangAdminKeys.nickNameKey)
export const getYitangAdminTenantInfo = () => {
  const info = window.localStorage.getItem(YitangAdminKeys.tenantInfoKey)
  return info ? JSON.parse(info) : {}
}
export const setYitangAdminTenantInfo = (info: any) =>
  window.localStorage.setItem(YitangAdminKeys.tenantInfoKey, JSON.stringify(info))
export const removeYitangAdminTenantInfo = () => window.localStorage.removeItem(YitangAdminKeys.tenantInfoKey)

// 盛尔达租户
export const getSedAdminSidebarStatus = () => window.localStorage.getItem(SedAdminKeys.sidebarStatusKey)
export const setSedAdminSidebarStatus = (sidebarStatus: string) =>
  window.localStorage.setItem(SedAdminKeys.sidebarStatusKey, sidebarStatus)
export const getSedAdminToken = () => window.localStorage.getItem(SedAdminKeys.tokenKey)
export const setSedAdminToken = (token: string) => window.localStorage.setItem(SedAdminKeys.tokenKey, token)
export const removeSedAdminToken = () => window.localStorage.removeItem(SedAdminKeys.tokenKey)
export const getSedAdminUserId = () => window.localStorage.getItem(SedAdminKeys.userIdKey)
export const setSedAdminUserId = (userId: string) => window.localStorage.setItem(SedAdminKeys.userIdKey, userId)
export const removeSedAdminUserId = () => window.localStorage.removeItem(SedAdminKeys.userIdKey)
export const getSedAdminUserName = () => window.localStorage.getItem(SedAdminKeys.userNameKey)
export const setSedAdminUserName = (val: string) => window.localStorage.setItem(SedAdminKeys.userNameKey, val)
export const removeSedAdminUserName = () => window.localStorage.removeItem(SedAdminKeys.userNameKey)
export const getSedAdminNickName = () => window.localStorage.getItem(SedAdminKeys.nickNameKey)
export const setSedAdminNickName = (val: string) => window.localStorage.setItem(SedAdminKeys.nickNameKey, val)
export const removeSedAdminNickName = () => window.localStorage.removeItem(SedAdminKeys.nickNameKey)
export const getSedAdminTenantInfo = () => {
  const info = window.localStorage.getItem(SedAdminKeys.tenantInfoKey)
  return info ? JSON.parse(info) : {}
}
export const setSedAdminTenantInfo = (info: any) =>
  window.localStorage.setItem(SedAdminKeys.tenantInfoKey, JSON.stringify(info))
export const removeSedAdminTenantInfo = () => window.localStorage.removeItem(SedAdminKeys.tenantInfoKey)

// 中圣租户
export const getZsAdminSidebarStatus = () => window.localStorage.getItem(ZsAdminKeys.sidebarStatusKey)
export const setZsAdminSidebarStatus = (sidebarStatus: string) =>
  window.localStorage.setItem(ZsAdminKeys.sidebarStatusKey, sidebarStatus)
export const getZsAdminToken = () => window.localStorage.getItem(ZsAdminKeys.tokenKey)
export const setZsAdminToken = (token: string) => window.localStorage.setItem(ZsAdminKeys.tokenKey, token)
export const removeZsAdminToken = () => window.localStorage.removeItem(ZsAdminKeys.tokenKey)
export const getZsAdminUserId = () => window.localStorage.getItem(ZsAdminKeys.userIdKey)
export const setZsAdminUserId = (userId: string) => window.localStorage.setItem(ZsAdminKeys.userIdKey, userId)
export const removeZsAdminUserId = () => window.localStorage.removeItem(ZsAdminKeys.userIdKey)
export const getZsAdminUserName = () => window.localStorage.getItem(ZsAdminKeys.userNameKey)
export const setZsAdminUserName = (val: string) => window.localStorage.setItem(ZsAdminKeys.userNameKey, val)
export const removeZsAdminUserName = () => window.localStorage.removeItem(ZsAdminKeys.userNameKey)
export const getZsAdminNickName = () => window.localStorage.getItem(ZsAdminKeys.nickNameKey)
export const setZsAdminNickName = (val: string) => window.localStorage.setItem(ZsAdminKeys.nickNameKey, val)
export const removeZsAdminNickName = () => window.localStorage.removeItem(ZsAdminKeys.nickNameKey)
export const getZsAdminTenantInfo = () => {
  const info = window.localStorage.getItem(ZsAdminKeys.tenantInfoKey)
  return info ? JSON.parse(info) : {}
}
export const setZsAdminTenantInfo = (info: any) =>
  window.localStorage.setItem(ZsAdminKeys.tenantInfoKey, JSON.stringify(info))
export const removeZsAdminTenantInfo = () => window.localStorage.removeItem(ZsAdminKeys.tenantInfoKey)

// 总租户
export const getQmyAdminSidebarStatus = () => window.localStorage.getItem(QmyAdminKeys.sidebarStatusKey)
export const setQmyAdminSidebarStatus = (sidebarStatus: string) =>
  window.localStorage.setItem(QmyAdminKeys.sidebarStatusKey, sidebarStatus)
export const getQmyAdminToken = () => window.localStorage.getItem(QmyAdminKeys.tokenKey)
export const setQmyAdminToken = (token: string) => window.localStorage.setItem(QmyAdminKeys.tokenKey, token)
export const removeQmyAdminToken = () => window.localStorage.removeItem(QmyAdminKeys.tokenKey)
export const getQmyAdminUserId = () => window.localStorage.getItem(QmyAdminKeys.userIdKey)
export const setQmyAdminUserId = (userId: string) => window.localStorage.setItem(QmyAdminKeys.userIdKey, userId)
export const removeQmyAdminUserId = () => window.localStorage.removeItem(QmyAdminKeys.userIdKey)
export const getQmyAdminUserName = () => window.localStorage.getItem(QmyAdminKeys.userNameKey)
export const setQmyAdminUserName = (val: string) => window.localStorage.setItem(QmyAdminKeys.userNameKey, val)
export const removeQmyAdminUserName = () => window.localStorage.removeItem(QmyAdminKeys.userNameKey)
export const getQmyAdminNickName = () => window.localStorage.getItem(QmyAdminKeys.nickNameKey)
export const setQmyAdminNickName = (val: string) => window.localStorage.setItem(QmyAdminKeys.nickNameKey, val)
export const removeQmyAdminNickName = () => window.localStorage.removeItem(QmyAdminKeys.nickNameKey)
export const getQmyAdminTenantInfo = () => {
  const info = window.localStorage.getItem(QmyAdminKeys.tenantInfoKey)
  return info ? JSON.parse(info) : {}
}
export const setQmyAdminTenantInfo = (info: any) =>
  window.localStorage.setItem(QmyAdminKeys.tenantInfoKey, JSON.stringify(info))
export const removeQmyAdminTenantInfo = () => window.localStorage.removeItem(QmyAdminKeys.tenantInfoKey)
