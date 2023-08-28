import { StrUtil } from './index'
import { loadConfigByConfigKey } from '@/api/system/config'

export default {
  getConfig(configKey) {
    if (StrUtil.isBlank(configKey)) {
      return {}
    }
    return loadConfigByConfigKey({ configKey })
  },
  getCaptchaImageEnabled() {
    return this.getConfig('sys.captcha.image.enabled')
  },
  async getInitPassword() {
    return this.getConfig('sys.user.initPassword')
  }
}
