import { ObjectUtil } from '@/utils/tools/index'

export default {
  isBlank(str) {
    return !str || str.trim() === ''
  },
  isNotBlank(str) {
    return !this.isBlank(str)
  },
  toBoolean(str) {
    return Boolean(str)
  },
  //首字母大写
  upperFirst(str) {
    if (this.isBlank(str)) {
      return str
    }
    return str.slice(0, 1).toUpperCase() + str.slice(1)
  },
  //路径左侧添加/
  paddingSlashLeft(url) {
    if (!ObjectUtil.isString(url)) {
      return url
    }
    if (url.startsWith('/')) {
      return url
    }
    return '/' + url
  },
  //路径左侧删除/
  removeSlashLeft(url) {
    if (!ObjectUtil.isString(url)) {
      return url
    }
    if (url.startsWith('/')) {
      return url.replace('/', '')
    }
    return url
  }
}
