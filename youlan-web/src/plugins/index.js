import tab from './tab'
import modal from '../utils/modal'
import download from './download'
import Tools, { ArrayUtil, AuthUtil, CookieUtil, CryptoUtil, DictUtil, StrUtil, UrlUtil } from '@/utils/tools'
import { StorageUtil } from '@/utils/tools/storage'
import Config from '@/plugins/config'

export default {
  install(Vue) {
    // 页签操作
    Vue.prototype.$tab = tab
    // 模态框对象
    Vue.prototype.$modal = modal
    // 下载文件
    Vue.prototype.$download = download
    // 系统参数
    Vue.prototype.$config = Config
    // 字符串工具
    Vue.prototype.$str = StrUtil
    // 浏览器Storage工具
    Vue.prototype.$storage = StorageUtil
    // 加密工具
    Vue.prototype.$crypto = CryptoUtil
    // url工具
    Vue.prototype.$url = UrlUtil
    // cookie工具
    Vue.prototype.$cookie = CookieUtil
    // 权限工具
    Vue.prototype.$auth = AuthUtil
    // 数据工具
    Vue.prototype.$array = ArrayUtil
    // 字典工具
    Vue.prototype.$dict = DictUtil
    // 全量工具包
    Vue.prototype.$tools = Tools
  }
}
