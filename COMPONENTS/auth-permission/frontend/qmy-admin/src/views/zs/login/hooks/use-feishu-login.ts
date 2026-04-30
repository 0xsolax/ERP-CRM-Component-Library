import { ref } from 'vue'
import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useFeishuLogin(options: {
  tenantInfo: Ref<any>
  redirectUri: Ref<string>
  qrcodeLoading: Ref<boolean>
  qrcodeError: Ref<string>
  onSuccess: (code: string) => void
}) {
  const { tenantInfo, redirectUri, qrcodeLoading } = options
  const QRLoginObj = ref<any>(null)

  const handleMessage = (event: MessageEvent) => {
    if (QRLoginObj.value && QRLoginObj.value.matchOrigin?.(event.origin) && QRLoginObj.value.matchData?.(event.data)) {
      const loginTmpCode = event.data.tmp_code
      const goto = `https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=${
        tenantInfo.value.feiShuAppId
      }&redirect_uri=${encodeURIComponent(redirectUri.value)}&response_type=code&state=custom_state`
      window.location.href = `${goto}&tmp_code=${loginTmpCode}`
    }
  }

  const init = (containerId: string) => {
    if (!tenantInfo.value.feiShuAppId) {
      qrcodeLoading.value = false
      ElMessage.error('未配置飞书 AppId')
      return
    }

    const goto = `https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=${
      tenantInfo.value.feiShuAppId
    }&redirect_uri=${encodeURIComponent(redirectUri.value)}&response_type=code&state=custom_state`

    QRLoginObj.value = (window as any).QRLogin({
      id: containerId,
      goto,
      width: '280',
      height: '280',
      style: 'margin: 0 auto;'
    })

    window.addEventListener('message', handleMessage, false)
    qrcodeLoading.value = false
  }

  const cleanup = () => {
    if (QRLoginObj.value) {
      window.removeEventListener('message', handleMessage, false)
      QRLoginObj.value = null
    }
  }

  return { init, cleanup }
}
