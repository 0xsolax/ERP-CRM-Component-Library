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
      <div class="login-form-wrapper">
        <div class="login-header">
          <div class="login-title">欢迎登录，{{ tenantName || '巧墨云' }}</div>
          <div class="login-subtitle">{{ tenantSlogan || '执巧墨之技，筑云端智厂' }}</div>
        </div>

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

        <div class="other-login">
          <div class="other-login-text">
            <img src="/images/login/login-type-desc.png" />
          </div>
          <div class="other-login-icons">
            <div class="login-icon">
              <el-tooltip content="飞书登录" placement="bottom">
                <img src="/images/login/feishu-icon.png" />
              </el-tooltip>
            </div>
            <div class="login-icon">
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
import { reactive, ref, toRefs, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/views/qmy/store/modules/user'
import { getTenantDetail } from '@/api/qmy/auth/tenant'
import { setQmyAdminTenantInfo } from '@/utils/auth'

const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) {
    return callback(new Error('请输入登录账号'))
  }
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

const formRef = ref<any>(null)
const router = useRouter()
const userStore = useUserStore()

const APP_ENV = import.meta.env.VITE_APP_ENV
const backgroundImages = ref<string[]>([])
const showCarousel = ref(false)
const tenantLogo = ref('')
const tenantName = ref('')
const tenantSlogan = ref('')

// 加载租户详情
const loadTenantDetail = async () => {
  let domainName = window.location.hostname
  if (APP_ENV === 'development') {
    domainName = 'test.qmy.hzq.plus'
  }
  const { data, code, message } = await getTenantDetail({ domainName })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  if (data?.id) {
    userStore.setTenantInfo(data)
    setQmyAdminTenantInfo(data)
  }

  if (data?.loginLogoFileList?.length) {
    tenantLogo.value = data?.loginLogoFileList[0].url || ''
  }
  tenantName.value = data?.name || ''
  tenantSlogan.value = data?.shortLogo || ''

  // 加载背景图
  if (data?.backgroundFileList?.length) {
    backgroundImages.value = data.backgroundFileList.map((item: any) => item.url)
    await nextTick()
    showCarousel.value = true
  } else {
    showCarousel.value = false
  }
}

const state = reactive({
  form: {
    username: '',
    password: ''
  },
  rules: {
    username: [{ validator: validateUsername, trigger: 'change' }],
    password: [
      { required: true, message: '密码不能为空', trigger: 'blur' },
      { validator: validatePassword, trigger: 'blur' },
      { min: 6, max: 12, message: '密码必须是6到12位', trigger: 'blur' }
    ]
  },
  loading: false,
  loginError: false
})

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
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    state.loading = false
    state.loginError = true
  }
}

const { form, rules, loading, loginError } = toRefs(state)

onMounted(() => {
  loadTenantDetail()
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
          margin-bottom: 28px;
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
        margin-top: 10px;
        border-radius: 8px;
      }
    }

    .other-login {
      margin-top: 55px;
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
