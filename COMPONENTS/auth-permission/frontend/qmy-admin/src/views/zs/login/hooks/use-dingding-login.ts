import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useDingdingLogin(options: {
  tenantInfo: Ref<any>
  redirectUri: Ref<string>
  qrcodeLoading: Ref<boolean>
  qrcodeError: Ref<string>
  onSuccess: (code: string) => void
}) {
  const { tenantInfo, redirectUri, qrcodeLoading, qrcodeError, onSuccess } = options

  const init = (containerId: string) => {
    if (!tenantInfo.value.dingTalkAppKey) {
      qrcodeLoading.value = false
      ElMessage.error('未配置钉钉 AppId')
      return
    }

    const dtLogin = (window as any).DTFrameLogin
    if (typeof dtLogin !== 'function') {
      qrcodeError.value = '钉钉登录组件未加载，请刷新页面重试'
      qrcodeLoading.value = false
      return
    }

    const state = `zs_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
    dtLogin(
      { id: containerId, width: 280, height: 280 },
      {
        redirect_uri: encodeURIComponent(redirectUri.value),
        client_id: tenantInfo.value.dingTalkAppKey,
        scope: 'openid',
        response_type: 'code',
        state,
        prompt: 'consent'
      },
      (loginResult: { redirectUrl?: string; authCode?: string; state?: string }) => {
        const { authCode, redirectUrl } = loginResult
        if (authCode) {
          onSuccess(authCode)
        } else if (redirectUrl) {
          window.location.href = redirectUrl
        }
      },
      (errorMsg: string) => {
        qrcodeError.value = errorMsg || '钉钉扫码失败'
        ElMessage.error(errorMsg || '钉钉扫码失败')
      }
    )

    const container = document.getElementById(containerId)
    if (container) {
      const observer = new MutationObserver(() => {
        if (container.children.length > 0) {
          qrcodeLoading.value = false
          observer.disconnect()
        }
      })
      observer.observe(container, { childList: true, subtree: true })
      setTimeout(() => {
        qrcodeLoading.value = false
        observer.disconnect()
      }, 3000)
    } else {
      qrcodeLoading.value = false
    }
  }

  return { init }
}
