<template>
  <div class="login-container">
    <div class="logo-wrapper">
      <img v-if="tenantLogo" :src="tenantLogo" alt="logo" class="logo" />
    </div>

    <div class="login-background">
      <el-carousel v-if="showCarousel" :interval="8000" arrow="never" :autoplay="true" height="100vh">
        <el-carousel-item v-for="(image, index) in backgroundImages" :key="index">
          <img :src="image" alt="" class="carousel-image" draggable="false" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="login-box">
      <div v-if="tenantLoading" class="tenant-loading">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        <div style="margin-top: 20px">加载中...</div>
      </div>

      <div v-else class="login-form-wrapper">
        <div class="login-header">
          <div class="login-title">欢迎登录，{{ tenantInfo.name || '一唐数字智能系统' }}</div>
          <div class="login-subtitle">{{ tenantInfo.shortLogo || '执巧墨之技，筑云端智厂' }}</div>
        </div>

        <div v-if="loginType === 'account'">
          <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username" :class="{ 'is-error': loginError }">
              <div class="form-label">登录账号</div>
              <el-input
                v-model="form.username"
                placeholder="请输入账号"
                clearable
                size="large"
                @input="loginError = false"
              />
            </el-form-item>

            <el-form-item prop="password" :class="{ 'is-error': loginError }">
              <div class="form-label">密码</div>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                clearable
                @keyup.enter="handleLogin"
                @input="loginError = false"
              />
            </el-form-item>

            <el-button :loading="loading" type="primary" size="large" class="login-btn" @click="handleLogin">
              登录
            </el-button>
          </el-form>
        </div>

        <div v-else class="feishu-login">
          <div class="feishu-title">飞书扫码登录</div>
          <div class="qrcode-box">
            <div v-if="qrcodeLoading" class="qrcode-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <div>加载中...</div>
            </div>
            <div v-else-if="qrcodeError" class="qrcode-error">
              <el-icon><WarningFilled /></el-icon>
              <div>{{ qrcodeError }}</div>
              <el-button size="small" type="primary" @click="initQrLogin">重新加载</el-button>
            </div>
            <div v-else id="feishu_qrcode_container" />
          </div>
        </div>

        <div class="other-login">
          <div class="other-login-text">
            <img src="/images/login/login-type-desc.png" />
          </div>
          <div class="other-login-icons">
            <div class="login-icon" :class="{ active: loginType === 'feishu' }" @click="switchLoginType('feishu')">
              <el-tooltip content="飞书登录" placement="bottom">
                <img src="/images/login/feishu-icon.png" />
              </el-tooltip>
            </div>
            <div class="login-icon" :class="{ active: loginType === 'account' }" @click="switchLoginType('account')">
              <el-tooltip content="账号密码登录" placement="bottom">
                <img src="/images/login/account-icon.png" />
              </el-tooltip>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <div class="footer-text">巧墨云技术支持 © 2025-2035</div>
      <a class="icp-link" href="https://beian.miit.gov.cn/" target="_blank">浙ICP备2026007094号-1</a>
      <a
        class="icp-link"
        href="https://www.beian.gov.cn/portal/registerSystemInfo?recordcode=33010602014345"
        target="_blank"
      >
        <img class="guohui-icon" src="/images/login/guohui-icon.png" />
        浙公网安备 33010602014345号
      </a>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, toRefs, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/views/admin/store/modules/user'
import { usePermissionStore } from '@/views/admin/store/modules/permission'
import { router as adminRouter } from '@/views/admin/router'
import { Loading, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getTenantDetail } from '@/api/admin/auth/user'
import { setYitangAdminTenantInfo } from '@/utils/auth'

const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) {
    return callback(new Error('请输入账号'))
  }
  // if (!mobileReg.test(value)) {
  //   return callback(new Error('请输入正确的手机号'))
  // }
  callback()
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    return callback(new Error('请输入密码'))
  }
  if (value.length < 6) {
    return callback(new Error('密码最少6位'))
  }
  callback()
}

const APP_ENV = import.meta.env.VITE_APP_ENV
const formRef = ref<any>(null)
const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const tenantLoading = ref(true)
const loginType = ref('') // feishu|account
const qrcodeLoading = ref(false)
const qrcodeError = ref('')
const QRLoginObj = ref<any>(null)
// const redirectUri = ref('http://test.yitang.hzq.plus/login')
const redirectUri = ref(window.location.origin + '/login')
const backgroundImages = ref<string[]>([])
const showCarousel = ref(false)
const tenantLogo = ref('')
const tenantInfo = ref<any>({
  // name: '一唐数字智能系统',
  // shortLogo: '执巧墨之技，筑云端智厂',
  name: '',
  shortLogo: '',
  feiShuAppId: '',
  accountSystemKey: ''
})

const switchLoginType = (type: string) => {
  state.loading = false
  if (type === 'feishu') {
    if (!tenantInfo.value.feiShuAppId) {
      return ElMessage.warning('当前租户未配置飞书登录，请使用账号密码登录')
    }
  }
  loginType.value = type
  if (type === 'feishu') {
    setTimeout(() => {
      initQrLogin()
    }, 100)
  }
}

const state = reactive({
  form: {
    username: '',
    password: ''
  },
  rules: {
    username: [
      { validator: validateUsername, trigger: 'change' }
      // { required: true, len: 11, message: '手机号不能为空且必须为11位', trigger: 'blur' },
      // { pattern: /^(?:(?:\+|00)86)?1[3-9]\d{9}$/, message: '手机号格式不对', trigger: 'change' }
    ],
    password: [
      { required: true, message: '密码不能为空', trigger: 'blur' },
      { validator: validatePassword, trigger: 'blur' },
      { min: 6, max: 12, message: '密码必须是6到12位', trigger: 'blur' }
    ]
  },
  loading: false,
  loginError: false
})

const loadTenantDetail = async () => {
  tenantLoading.value = true
  try {
    let domainName = window.location.hostname
    if (APP_ENV === 'development') {
      const proxyUrl = __APP_INFO__.proxy?.yitangProxy || 'test.yitang.hzq.plus'
      domainName = new URL(proxyUrl).hostname
      redirectUri.value = new URL(proxyUrl).origin + '/login'
    }
    const { data, code, message } = await getTenantDetail({ domainName })
    if (code !== 200) {
      ElMessage.warning(message)
      loginType.value = 'account'
      return
    }
    // data.feiShuAppId = 'cli_a87b40d0b8aed00e'
    tenantInfo.value = data
    if (data?.id) {
      userStore.setTenantInfo(data)
      setYitangAdminTenantInfo(data)
    }

    if (data?.loginLogoFileList?.length) {
      tenantLogo.value = data?.loginLogoFileList[0].url || ''
    }

    // 加载背景图
    if (data?.backgroundFileList?.length) {
      backgroundImages.value = data.backgroundFileList.map((item: any) => item.url)
      await nextTick()
      showCarousel.value = true
    } else {
      showCarousel.value = false
    }

    if (data?.accountSystemKey === 'FeiShu') {
      loginType.value = 'feishu'
      setTimeout(() => {
        initQrLogin()
      }, 100)
    } else {
      loginType.value = 'account'
    }
  } finally {
    tenantLoading.value = false
  }
}

const handleLogin = async () => {
  await formRef.value.validate()
  state.loading = true
  state.loginError = false
  try {
    let reqBody = {
      userName: state.form.username,
      password: state.form.password
    }
    await userStore.login(reqBody)
    const hasPermission = await userStore.getUserInfo()
    if (hasPermission) {
      permissionStore.dynamicRoutes.forEach((route: any) => {
        adminRouter.addRoute('layout', route)
      })
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      state.loading = false
    }
  } catch (error) {
    state.loading = false
    state.loginError = true
  }
}

// 初始化飞书扫码登录
const initQrLogin = async () => {
  qrcodeLoading.value = true
  qrcodeError.value = ''
  try {
    if (QRLoginObj.value) {
      window.removeEventListener('message', handleFeishuMessage, false)
      QRLoginObj.value = null
    }
    const container = document.getElementById('feishu_qrcode_container')
    if (container) {
      container.innerHTML = ''
    }

    if (!tenantInfo.value.feiShuAppId) {
      return ElMessage.error('未配置飞书 AppId')
    }

    const goto = `https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=${
      tenantInfo.value.feiShuAppId
    }&redirect_uri=${encodeURIComponent(redirectUri.value)}&response_type=code&state=custom_state`

    QRLoginObj.value = window.QRLogin({
      id: 'feishu_qrcode_container',
      goto: goto,
      width: '280',
      height: '280',
      style: 'margin: 0 auto;'
    })

    window.addEventListener('message', handleFeishuMessage, false)
    qrcodeLoading.value = false
  } catch (error) {
    console.error('初始化飞书二维码失败:', error)
    qrcodeError.value = '二维码加载失败，请刷新页面重试'
    ElMessage.error('加载二维码失败')
    qrcodeLoading.value = false
  }
}

const handleFeishuMessage = (event: MessageEvent) => {
  if (
    QRLoginObj.value &&
    QRLoginObj.value.matchOrigin &&
    QRLoginObj.value.matchOrigin(event.origin) &&
    QRLoginObj.value.matchData &&
    QRLoginObj.value.matchData(event.data)
  ) {
    const loginTmpCode = event.data.tmp_code
    const goto = `https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=${
      tenantInfo.value.feiShuAppId
    }&redirect_uri=${encodeURIComponent(redirectUri.value)}&response_type=code&state=custom_state`
    window.location.href = `${goto}&tmp_code=${loginTmpCode}`
  }
}

// 处理飞书回调登录
const handleFeishuCallback = async (code: string) => {
  try {
    state.loading = true
    console.log('Feishu callback code:', code)
    let reqBody = {
      code: code,
      type: 'feishu'
    }
    await userStore.loginByScan(reqBody)
    window.history.replaceState({}, '', window.location.pathname)

    const hasPermission = await userStore.getUserInfo()
    if (hasPermission) {
      permissionStore.dynamicRoutes.forEach((route: any) => {
        adminRouter.addRoute('layout', route)
      })
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      state.loading = false
    }
  } catch (error) {
    console.error('飞书登录失败:', error)
    ElMessage.error('登录失败，请重试')
    state.loading = false
  }
}

const { form, rules, loading, loginError } = toRefs(state)

onMounted(() => {
  loadTenantDetail()
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  if (code) {
    loginType.value = 'feishu'
    handleFeishuCallback(code)
  }
})

onBeforeUnmount(() => {
  if (QRLoginObj.value) {
    window.removeEventListener('message', handleFeishuMessage, false)
    QRLoginObj.value = null
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100vh;
  background: rgba(240, 242, 245, 0.3);
  position: relative;
  overflow: hidden;
  padding-right: 11%;

  .logo-wrapper {
    position: absolute;
    top: 40px;
    left: 40px;
    z-index: 10;

    .logo {
      height: 40px;
      object-fit: contain;
    }
  }

  .login-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    user-select: none;
    -webkit-user-drag: none;

    .carousel-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      pointer-events: none;
      user-select: none;
      -webkit-user-drag: none;
    }

    .default-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      pointer-events: none;
      user-select: none;
      -webkit-user-drag: none;
    }

    :deep(.el-carousel) {
      width: 100%;
      height: 100%;

      .el-carousel__container {
        height: 100%;
      }
    }
  }

  .login-box {
    position: relative;
    z-index: 5;
    width: 450px;
    background: rgba(255, 255, 255, 1);
    border-radius: 20px;
    box-shadow: 0px 9px 40px 0px rgba(207, 199, 240, 0.16);
    padding: 55px 50px;
    backdrop-filter: blur(10px);

    .tenant-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 0;
      color: #666;
      font-size: 14px;

      .el-icon {
        color: var(--el-color-primary);
      }
    }

    .login-form-wrapper {
      .login-header {
        margin-bottom: 45px;
        text-align: center;

        .login-title {
          font-size: 22px;
          font-weight: 600;
          color: #333;
          margin-bottom: 20px;
        }

        .login-subtitle {
          font-size: 13px;
          color: #999;
        }
      }

      .login-form {
        :deep(.el-form-item) {
          margin-bottom: 38px;
        }
        :deep(.el-form-item__content) {
          position: relative;
        }

        .form-label {
          font-size: 13px;
          color: #a9adb4;
          position: absolute;
          top: -11px;
          left: 14px;
          background: #fff;
          z-index: 100;
          height: 20px;
          line-height: 20px;
          padding: 0 5px;
        }

        :deep(.is-error .el-input__wrapper) {
          border-color: #f56c6c !important;
        }

        :deep(.is-error .el-input__inner) {
          color: #f56c6c !important;
          &:-webkit-autofill {
            -webkit-text-fill-color: #f56c6c !important;
          }
        }

        :deep(.el-input) {
          .el-input__wrapper {
            padding: 8px 10px;
            box-shadow: none;
            background: transparent;
            border-radius: 10px;
            border: 1px solid #e0e0e0;

            &:hover {
              border-color: #c2bfbf;
            }
          }

          &.is-focus {
            border-color: var(--el-color-primary);
            border-width: 1px;
          }
        }

        .el-input__inner {
          background: transparent;
          font-size: 14px;
          color: #333;
          height: 35px;
          line-height: 35px;
          &::placeholder {
            color: #c0c4cc;
          }
          &:-webkit-autofill {
            box-shadow: 0 0 0 1000px transparent inset !important;
            -webkit-text-fill-color: #333;
            transition: background-color 5000s ease-in-out 0s;
          }
        }
      }

      .login-btn {
        width: 100%;
        height: 46px;
        font-size: 15px;
        margin-top: 16px;
        margin-bottom: 50px;
        border-radius: 8px;
      }
    }

    .feishu-login {
      text-align: center;

      .feishu-title {
        font-size: 13px;
        font-weight: bold;
        color: #606266;
        background: #f5f6f8;
        border-radius: 6px;
        text-align: center;
        width: 100px;
        margin: 0 auto -23px auto;
        height: 33px;
        line-height: 33px;
      }

      .qrcode-box {
        margin: 0 auto;
        display: inline-block;

        :deep(#feishu_qrcode_container) {
          overflow: hidden;
          transform: scale(0.85);
          transform-origin: center;
          iframe {
            border: 0;
            display: block;
          }
        }

        .qrcode-loading,
        .qrcode-error {
          width: 280px;
          height: 280px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #999;
          border: 1px solid #e8e8e8;
          border-radius: 12px;
          background: #fafafa;

          .el-icon {
            font-size: 32px;
            margin-bottom: 10px;
          }
        }

        .qrcode-error {
          .el-icon {
            color: #f56c6c;
          }

          div {
            margin-bottom: 10px;
          }
        }
      }
    }

    .other-login {
      text-align: center;

      .other-login-text {
        font-size: 13px;
        color: #999;
        margin-bottom: 18px;
        img {
          width: 288px;
        }
      }

      .other-login-icons {
        display: flex;
        justify-content: center;
        gap: 16px;

        .login-icon {
          width: 38px;
          height: 38px;
          border-radius: 50%;
          background: #f5f5f5;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            transform: scale(1.1);
          }

          &.active {
            background: #e8f4ff;
          }

          img {
            width: 30px;
            height: 30px;
          }
        }
      }
    }
  }
}

.footer {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  z-index: 10;
  display: flex;
  align-items: center;

  .footer-text {
    font-size: 12px;
    color: #666;
  }

  .icp-link {
    font-size: 12px;
    color: #666;
    text-decoration: none;
    margin-left: 25px;
    display: flex;
    align-items: center;

    &:hover {
      color: var(--el-color-primary);
    }
  }
  .guohui-icon {
    width: 16px;
    margin-right: 3px;
    margin-top: -1px;
    vertical-align: middle;
  }
}
</style>
