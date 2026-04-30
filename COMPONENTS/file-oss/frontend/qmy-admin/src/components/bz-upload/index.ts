import { App } from 'vue'
import BzUpload from './index.vue'

BzUpload.install = (app: App): void => {
  app.component(BzUpload.name, BzUpload)
}

export default BzUpload
