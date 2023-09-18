import { StrUtil } from './index'
import { loadConfigByConfigKey } from '@/api/system/config'

export default {
  getConfig(configKey) {
    if (StrUtil.isBlank(configKey)) {
      return Promise.reject('系统配置键值不能为空')
    }
    return loadConfigByConfigKey({ configKey })
  },
  async getInitPassword() {
    return this.getConfig('sys.user.initPassword')
  }
}
