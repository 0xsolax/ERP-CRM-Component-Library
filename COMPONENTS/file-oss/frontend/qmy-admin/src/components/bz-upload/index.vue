<script lang="ts">
import { defineComponent, h } from 'vue'
import BzUpload from '@bzlab/bz-upload'
import '@bzlab/bz-upload/lib/bz-upload.css'
import { ElMessage } from 'element-plus'
import { getYitangAdminToken, getSedAdminToken, getZsAdminToken, getQmyAdminToken } from '@/utils/auth'
import { YitangAdminKeys, SedAdminKeys, ZsAdminKeys, QmyAdminKeys } from '@/config/settings'
import { generateUUID } from '@/utils'
import { saveSysStorage } from '@/api/admin/system/storage'
import dayjs from 'dayjs'
const BzUploadComponent = window['bz-upload'] ? window['bz-upload'].default : BzUpload

function NOOP() {}
export default defineComponent({
  name: 'BzUpload',
  components: {
    BzUpload: BzUploadComponent
  },
  props: {
    aliyun: {
      type: Boolean,
      default: true
    },
    keepOrigin: {
      type: Boolean,
      default: true
    },
    ossPrefix: {
      type: String,
      default: ''
    },
    ftpPrefix: {
      type: String,
      default: ''
    },
    ossDataEnum: {
      type: Object,
      default: () => ({
        md5: 'fileOssKey',
        size: 'fileSize'
      })
    },
    downloadDataEnum: {
      type: Object,
      default: () => ({
        name: 'fileName',
        key: 'ossKey'
      })
    },
    ossRequestConfig: {
      type: Function,
      default: NOOP
    },
    directory: {
      type: Boolean,
      default: false
    },
    action: {
      type: String,
      default: ''
    },
    download: {
      type: String,
      default: ''
    },
    headers: {
      type: Object,
      default: () => ({
        token: ''
      })
    },
    data: {
      type: Object,
      default: () => ({})
    },
    filterDataFields: {
      type: Array,
      default: () => ['path', 'fileName']
    },
    multiple: {
      type: Boolean,
      default: false
    },
    name: {
      type: String,
      default: 'file'
    },
    drag: {
      type: Boolean,
      default: false
    },
    showFileList: {
      type: Boolean,
      default: false
    },
    accept: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'select'
    },
    beforeUpload: {
      type: Function,
      default: NOOP
    },
    beforeRemove: {
      type: Function,
      default: NOOP
    },
    onRemove: {
      type: Function,
      default: NOOP
    },
    onSuccess: {
      type: Function,
      default: NOOP
    },
    onProgress: {
      type: Function,
      default: NOOP
    },
    onError: {
      type: Function,
      default: null
    },
    onExceed: {
      type: Function,
      default: () => NOOP
    },
    onStart: {
      type: Function,
      default: () => NOOP
    },
    fileList: {
      type: Array,
      default: () => {
        return []
      }
    },
    autoUpload: {
      type: Boolean,
      default: true
    },
    disabled: Boolean,
    limit: {
      type: Number,
      default: null
    },
    showTotal: {
      type: Boolean,
      default: true
    },
    showMd5: {
      type: Boolean,
      default: false
    },
    fileWords: {
      type: Number,
      default: 200
    },
    directoryWords: {
      type: Number,
      default: 200
    },
    modulePath: {
      type: String,
      default: 'yt/product'
    }
  },
  render() {
    const userStore = bz.store
    const pageType = userStore.app.pageType
    const year = dayjs().format('YYYY')
    const month = dayjs().format('MM')
    const day = dayjs().format('DD')
    const tenantId = userStore.user.tenantInfo?.id
    const defaultOssPrefix = `tenant_${tenantId}/${this.modulePath}/${year}/${month}/${day}`
    const fileUuidMap = new Map()
    const onError = res => {
      if (res.status === 500) {
        ElMessage.error('登录已失效，请重新登录')
        userStore.user.resetToken().then(() => {
          location.reload()
        })
      }
      if (res.status === 502) {
        ElMessage.error('服务器响应失败，请重试')
      }
    }
    const onSuccess = async (res, file, files, trees) => {
      const fileKey = file.uid
      const uuid = fileUuidMap.get(fileKey)
      fileUuidMap.delete(fileKey)

      const url = `https://${res.response.bucketName}.${res.response.endpoint}/${defaultOssPrefix}/${uuid}_${file.name}`
      const mimeType = file.raw?.type || ''
      const reqBody = { name: file.name, url, type: mimeType, size: file.size }
      const { code, data } = await saveSysStorage(reqBody)
      if (code !== 200) return ElMessage.warning(data)
      let result = {
        url: data.url,
        id: data.id
      }
      res.data = result
      this.onSuccess(res, file, files, trees)
    }
    const onStart = file => {
      const fileKey = file.uid
      fileUuidMap.set(fileKey, generateUUID())
      this.onStart?.(file)
    }
    const uploadData = {
      aliyun: this.aliyun,
      keepOrigin: this.keepOrigin,
      ossPrefix: this.ossPrefix,
      ftpPrefix: this.ftpPrefix,
      ossDataEnum: this.ossDataEnum,
      downloadDataEnum: this.downloadDataEnum,
      ossRequestConfig: this.ossRequestConfig,
      type: this.type,
      directory: this.directory,
      drag: this.drag,
      action: this.action,
      download: this.download,
      multiple: this.multiple,
      'before-upload': this.beforeUpload,
      showFileList: this.showFileList,
      headers: this.headers,
      name: this.name,
      data: this.data,
      filterDataFields: this.filterDataFields,
      accept: this.accept,
      fileList: this.fileList,
      autoUpload: this.autoUpload,
      disabled: this.disabled,
      fileWords: this.fileWords,
      directoryWords: this.directoryWords,
      limit: this.limit,
      showTotal: this.showTotal,
      showMd5: this.showMd5,
      'on-exceed': this.onExceed,
      'on-start': onStart,
      'on-progress': this.onProgress,
      'on-success': onSuccess,
      'on-error': this.onError ? this.onError : onError,
      'on-remove': this.onRemove,
      ref: 'bzUploadRef'
    }

    uploadData.headers = {}

    if (pageType === 'yitang') {
      uploadData.headers[YitangAdminKeys.requestHeaderTokenKey] = getYitangAdminToken()
      uploadData.headers[YitangAdminKeys.requestHeaderTenantKey] = userStore.user.tenantInfo?.id ?? ''
      uploadData.action = uploadData.action || import.meta.env.VITE_APP_YITANG_BASE_API + `/oss/getOssToken`
    } else if (pageType === 'sed') {
      uploadData.headers[SedAdminKeys.requestHeaderTokenKey] = getSedAdminToken()
      uploadData.headers[SedAdminKeys.requestHeaderTenantKey] = userStore.user.tenantInfo?.id ?? ''
      uploadData.action = uploadData.action || import.meta.env.VITE_APP_SED_BASE_API + `/oss/getOssToken`
    } else if (pageType === 'zs') {
      uploadData.headers[ZsAdminKeys.requestHeaderTokenKey] = getZsAdminToken()
      uploadData.headers[ZsAdminKeys.requestHeaderTenantKey] = userStore.user.tenantInfo?.id ?? ''
      uploadData.action = uploadData.action || import.meta.env.VITE_APP_ZS_BASE_API + `/oss/getOssToken`
    } else if (pageType === 'qmy') {
      uploadData.headers[QmyAdminKeys.requestHeaderTokenKey] = getQmyAdminToken()
      uploadData.headers[QmyAdminKeys.requestHeaderTenantKey] = userStore.user.tenantInfo?.id ?? ''
      uploadData.action = uploadData.action || import.meta.env.VITE_APP_QMY_BASE_API + `/oss/getOssToken`
    }

    if (!uploadData.aliyun) {
      uploadData.download = uploadData.download || ''
      uploadData.ftpPrefix = uploadData.ftpPrefix || ''
    } else {
      uploadData.ossPrefix = uploadData.ossPrefix || defaultOssPrefix
      uploadData.ossRequestConfig = (option, response) => {
        const uuid = fileUuidMap.get(option.file.uid)
        return {
          endpoint: response['endpoint'],
          accessKeyId: response['accessKeyId'],
          accessKeySecret: response['accessKeySecret'],
          bucket: response['bucketName'],
          stsToken: response['securityToken'],
          name: `tenant_${tenantId}/${this.modulePath}/${year}/${month}/${day}/${uuid}_${option.file.name}`
        }
      }
    }

    const trigger = this.$slots.trigger || this.$slots.default
    const uploadComponent = h(BzUploadComponent, uploadData, {
      default: () => trigger?.()
    })
    return h('div', [
      this.$slots.trigger ? [uploadComponent, (this.$slots as any).default()] : uploadComponent,
      this.$slots.tip?.()
    ])
  }
})
</script>
