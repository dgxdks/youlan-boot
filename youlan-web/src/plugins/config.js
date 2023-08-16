import { ObjectUtil, StrUtil } from '@/utils/tools'
import { loadConfigByConfigKey } from '@/api/system/config'

export default {
  async getConfig(configKey) {
    if (StrUtil.isBlank(configKey)) {
      return {}
    }
    const data = await loadConfigByConfigKey({
      configKey
    })
    if (ObjectUtil.isEmpty(data)) {
      return {}
    }
    return data
  },
  async getConfigValue(configKey) {
    const config = await this.getConfig(configKey)
    return config.configValue
  },
  async getCaptchaImageEnabled() {
    return await this.getConfigValue('sys.captcha.image.enabled')
  }
}
