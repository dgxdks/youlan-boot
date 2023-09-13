import { StrUtil } from '@/framework/tools/index'

export default {
  isDevEnv() {
    return process.env.NODE_ENV === 'development'
  },
  isProdEnv() {
    return process.env.NODE_ENV === 'production'
  },
  getBaseApi() {
    return StrUtil.removeSlashRight(process.env.VUE_APP_BASE_API)
  }
}
